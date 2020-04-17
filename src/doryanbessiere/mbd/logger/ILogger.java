package doryanbessiere.mbd.logger;

public interface ILogger {

	public abstract void info(String message);
	public abstract void info_(String message);
	public abstract void error(String message);
	public abstract void error_(String message);
	public abstract void debug(String message);
	public abstract void debug_(String message);
	
	public abstract void exception(Exception exception);
	public abstract void throwable(String localizedMessage, Throwable throwable);

}
