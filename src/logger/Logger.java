package logger;

public class Logger {

	enum LogLevel {
		INFO, WARNING, ERROR, DEBUG
	}

	public static LogLevel logLevel = LogLevel.DEBUG;

	public static void info(String msg) {
		System.out.println("INFO: " + msg);
	}

	public static void warning(String msg) {
		System.out.println("WARNING: " + msg);
	}

	public static void error(String msg) {
		System.out.println("ERROR: " + msg);
	}

	public static void debug(String msg) {
		System.out.println("DEBUG: " + msg);
	}
}
