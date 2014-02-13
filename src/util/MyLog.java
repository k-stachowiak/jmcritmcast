package util;

import java.util.EnumSet;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public enum MyLog {

	CURRENT_ALGORITHM_CONSOLE("algConsoleDebug", Level.INFO, EnumSet
			.of(MyApp.STDERR)),

	GENERAL_ALGORITHM_FILE("algFileDebug", Level.DEBUG, EnumSet
			.of(MyApp.DEBUG_FILE));

	private final Logger logger;

	MyLog(String name, Level level, Set<MyApp> appenders) {
		logger = Logger.getLogger(name);
		logger.setLevel(level);
		for (MyApp appender : appenders) {
			logger.addAppender(appender.getAppender());
		}
	}

	public void trace(Object message) {
		logger.info(message);
	}
}
