package impossible.util;

import java.io.OutputStreamWriter;
import java.io.PrintStream;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;

public enum Appenders {
	STDOUT(System.out),
	STDERR(System.err);
	
	private final ConsoleAppender appender;
	
	Appenders(PrintStream printStream) {
		appender = new ConsoleAppender();
		appender.setWriter(new OutputStreamWriter(printStream));
		appender.setLayout(new PatternLayout());
	}
	
	public Appender getAppender() {
		return appender;
	}
}
