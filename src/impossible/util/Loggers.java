package impossible.util;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public enum Loggers {
	ALGORITHM_DEBUG("algorithmDebug", Level.TRACE, Appenders.STDERR);

	private final Logger logger;

	Loggers(String name, Level level, Appenders appender) {
		logger = Logger.getLogger(name);
		logger.setLevel(level);
		logger.addAppender(appender.getAppender());
	}	
	
}
