package doryan.mbd.logger;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import doryan.mbd.logger.file.LoggerFile;
import doryan.mbd.logger.listeners.LoggerListener;

public class Logger implements ILogger {

	private String name;
	
	private LoggerFile logger_file;

	private ArrayList<String> messages = new ArrayList<String>();
	private ArrayList<LoggerListener> listeners = new ArrayList<LoggerListener>();
	
	public Logger(String name, LoggerFile logger_file) {
		this.name = name;
		this.logger_file = logger_file;

		this.listeners.add(getLoggerFile());
		this.listeners.add(new LoggerListener() {
			@Override
			public void log(String message) {
				messages.add(message);
			}
		});

		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread t, Throwable e) {
				throwable("exception in thread '" + t.getName() + "' " + e.getClass().getName(), e);
			}
		});

		PrintStream out = new PrintStream(System.out) {
			@Override
			public void println(String msg) {
				super.println(msg);
			}

			@Override
			public void print(String msg) {
				if (msg.startsWith("logger:")) {
					msg = msg.substring(7);
					if(!listeners.isEmpty()) {
						for(LoggerListener listener : listeners) {
							listener.log(msg.contains("\n") ? msg : msg+"\n");
						}
					}
					super.print(msg);
				} else {
					info_(msg);
				}
			}
		};
		System.setOut(out);

		PrintStream error = new PrintStream(System.err) {
			@Override
			public void println(String msg) {
				super.println(msg);
			}

			@Override
			public void print(String msg) {
				if (msg.startsWith("logger:")) {
					msg = msg.substring(7);
					if(!listeners.isEmpty()) {
						for(LoggerListener listener : listeners) {
							listener.log(msg.contains("\n") ? msg : msg+"\n");
						}
					}
					super.print(msg);
				} else {
					error_(msg);
				}
			}
		};
		System.setErr(error);
	}
	
	@Override
	public String toString() {
		String text = "";
		for(String message : this.messages) {
			text += message;
		}
		if(text.endsWith("\n"))
			text = text.substring(0, text.length() - 1);
		return text;
	}
	
	public LoggerFile getLoggerFile() {
		return logger_file;
	}
	
	public ArrayList<LoggerListener> getListeners() {
		return listeners;
	}

	public ArrayList<String> getMessages() {
		return messages;
	}

	@Override
	public void exception(Exception exception) {
		for (Throwable throwable : exception.getSuppressed()) {
			throwable(exception.getLocalizedMessage(), throwable);
		}
	}

	@Override
	public void throwable(String localizedMessage, Throwable throwable) {
		error(localizedMessage);
		for (StackTraceElement element : throwable.getStackTrace()) {
			error("    at " + element.getClassName() + "." + element.getMethodName() + "(" + element.getFileName() + ":"
					+ element.getLineNumber() + ")");
		}

		Throwable ourCause = throwable.getCause();
		if (ourCause != null)
			throwable("Caused by: " + ourCause.getClass().getName(), ourCause);
	}

	public String time() {
		return "[" + new SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis()) + "] ";
	}

	public String prefix() {
		if(this.name== null)return "";
		return "[" + this.name + "] ";
	}

	@Override
	public void info(String message) {
		message = prefix() + time() + "[INFO] " + message;
		System.out.println("logger:" + message);
	}

	@Override
	public void info_(String message) {
		message = prefix() + time() + "[INFO] " + message;
		System.out.print("logger:" + message);
	}

	@Override
	public void error(String message) {
		message= prefix() + time() + "[ERROR] " + message;
		System.err.println("logger:" + message);
	}

	@Override
	public void error_(String message) {
		message= prefix() + time() + "[ERROR] " + message;
		System.err.print("logger:" + message);
	}

	@Override
	public void debug(String message) {
		message= prefix() + time() + "[DEBUG] " + message;
		System.out.println("logger:" + message);
	}

	@Override
	public void debug_(String message) {
		message= prefix() + time() + "[DEBUG] " + message;
		System.out.print("logger:" + message);
	}

	public String getName() {
		return name;
	}
}
