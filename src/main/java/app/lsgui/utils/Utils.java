package app.lsgui.utils;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class Utils {

	private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);
	private static final JsonParser PARSER = new JsonParser();

	public static void startLivestreamer(final String URL, final String quality) {
		LOGGER.info("Starting Stream {} with Quality {}", URL, quality);
		new Thread(() -> {
			try {
				ProcessBuilder pb = new ProcessBuilder(Arrays.asList("livestreamer", URL, quality));
				pb.redirectOutput(Redirect.INHERIT);
				pb.redirectError(Redirect.INHERIT);
				Process prc = pb.start();
				prc.waitFor();
			} catch (IOException | InterruptedException e) {
				LOGGER.error("ERROR while running livestreamer", e);
			}
		}).start();
	}

	public static void recordLivestreamer(final String URL, final String quality, final File filePath) {
		LOGGER.info("Record Stream {} with Quality {} to file {}", URL, quality, filePath);
		new Thread(() -> {
			try {
				String path = "\"" + filePath.getAbsolutePath() + "\"";
				path = path.replace("\\", "/");
				LOGGER.debug(path);
				ProcessBuilder pb = new ProcessBuilder(Arrays.asList("livestreamer", "-o", path, URL, quality));
				pb.redirectOutput(Redirect.INHERIT);
				pb.redirectError(Redirect.INHERIT);
				Process prc = pb.start();
				prc.waitFor();
			} catch (IOException | InterruptedException e) {
				LOGGER.error("ERROR while recording", e);
			}
		}).start();
	}

	public static void openURLInBrowser(final String URL) {
		LOGGER.info("Open Browser URL {}", URL);
		try {
			URI uri = new URI(URL);
			Desktop.getDesktop().browse(uri);
		} catch (IOException | URISyntaxException e) {
			LOGGER.error("ERROR while opening URL in Browser", e);
		}
	}

	public static String getStringIfNotNull(final String name, final JsonObject obj) {
		if (obj.has(name) && !obj.get(name).isJsonNull()) {
			return obj.get(name).getAsString();
		}
		return "";
	}

	public static Boolean getBooleanIfNotNull(final String name, final JsonObject obj) {
		if (obj.has(name) && !obj.get(name).isJsonNull()) {
			return obj.get(name).getAsBoolean();
		}
		return false;
	}

	public static Integer getIntegerIfNotNull(final String name, final JsonObject obj) {
		if (obj.has(name) && !obj.get(name).isJsonNull()) {
			return obj.get(name).getAsInt();
		}
		return 0;
	}

	public static List<String> getAvailableQuality(final String URL) {
		final List<String> qualities = new ArrayList<String>();
		final JsonObject qualitiesJson = getQualityJsonFromLivestreamer(URL);
		if (!qualitiesJson.toString().contains("error")) {
			final JsonObject jsonQualitiyList = qualitiesJson.get("streams").getAsJsonObject();
			jsonQualitiyList.entrySet().forEach(entry -> qualities.add(entry.getKey()));
			return sortQualities(qualities);
		}
		return qualities;
	}

	private static JsonObject getQualityJsonFromLivestreamer(final String URL) {
		try {
			final String livestreamerExec = "livestreamer";
			JsonObject jsonQualities = new JsonObject();
			final Process process = new ProcessBuilder(livestreamerExec, "-j", URL).redirectErrorStream(true).start();
			jsonQualities = PARSER
					.parse(new JsonReader(new BufferedReader(new InputStreamReader(process.getInputStream()))))
					.getAsJsonObject();
			process.waitFor();
			return jsonQualities;
		} catch (final IOException | InterruptedException e) {
			LOGGER.error("failed to retrieve stream qualites for " + URL + "," + " reason: " + e.getMessage());
		}
		return new JsonObject();
	}

	private static List<String> sortQualities(final List<String> qualities) {
		List<String> sortedQualities = new ArrayList<String>();
		qualities.forEach((s) -> s = s.toLowerCase());
		if (qualities.contains("audio")) {
			sortedQualities.add("Audio");
		}
		if (qualities.contains("mobile")) {
			sortedQualities.add("Mobile");
		}
		if (qualities.contains("low")) {
			sortedQualities.add("Low");
		}
		if (qualities.contains("medium")) {
			sortedQualities.add("Medium");
		}
		if (qualities.contains("high")) {
			sortedQualities.add("High");
		}
		if (qualities.contains("source")) {
			sortedQualities.add("Source");
		}
		if (sortedQualities.size() == 0) {
			sortedQualities.add("Worst");
			sortedQualities.add("Best");
		}
		return sortedQualities;
	}
}
