/**
 * MIT License
 *
 * Copyright (c) 2016 Jan-Niklas Keck
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package app.lsgui.remote.twitch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import app.lsgui.model.twitch.TwitchService;
import app.lsgui.utils.TwitchUtils;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

/**
 *
 * @author Niklas 11.06.2016
 *
 */
public final class TwitchServiceUpdateService extends ScheduledService<TwitchService> {

	private static final Logger LOGGER = LoggerFactory.getLogger(TwitchServiceUpdateService.class);

	private static final double UPDATE_PERIOD = 60;
	private TwitchService service;

	public TwitchServiceUpdateService(final TwitchService service) {
		LOGGER.debug("Create UpdateService for Twitch Service");
		this.service = service;
		this.setUpConstant();
	}

	private void setUpConstant() {
		setPeriod(Duration.seconds(UPDATE_PERIOD));
		setRestartOnFailure(true);
		setOnFailed(event -> LOGGER.warn("Channel Update Service FAILED. Event: {}", event.getEventType()));
	}

	@Override
	protected Task<TwitchService> createTask() {
		return new Task<TwitchService>() {
			@Override
			protected TwitchService call() throws Exception {
				TwitchUtils.fetchChannelIds(service);
				JsonObject streamData = TwitchAPIClient.getInstance()
						.getStreamData(TwitchServiceUpdateService.this.service);
				Platform.runLater(() -> {
					TwitchUtils.updateTwitchService(service, streamData, new JsonObject());
					JsonObject gameData = TwitchAPIClient.getInstance()
							.getGameInfo(TwitchServiceUpdateService.this.service);
					TwitchUtils.updateTwitchService(service, streamData, gameData);
				});
				return TwitchServiceUpdateService.this.service;
			}
		};
	}
}
