package impossible.util;

import java.io.OutputStreamWriter;
import java.io.PrintStream;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.PatternLayout;

enum MyApp {
	
	STDOUT(System.out),
	STDERR(System.err),
	DEBUG_FILE("debug.log");
	
	private final Appender appender;
	
	MyApp(PrintStream printStream) {		
		ConsoleAppender app = new ConsoleAppender();
		app.setWriter(new OutputStreamWriter(printStream));
		app.setLayout(new PatternLayout());
		appender = app;
	}
	
	MyApp(String filename) {
		FileAppender app = new FileAppender();
		app.setFile(filename);
		app.setLayout(new PatternLayout());
		appender = app;
	}
	
	public Appender getAppender() {
		return appender;
	}
}
