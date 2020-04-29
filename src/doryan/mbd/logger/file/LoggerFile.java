package doryan.mbd.logger.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import doryan.mbd.logger.listeners.LoggerListener;

public class LoggerFile implements LoggerListener {

	protected File file;
	protected FileWriter writer;
	
	public LoggerFile(File folder) {
		folder.mkdir();
		this.file = new File(folder, time()+"-0.log");
		int i = 1;
		while(this.file.exists()) {
			this.file = new File(folder, time()+"-"+i+".log");
			i++;
		}
		try {
			this.file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			this.writer = new FileWriter(this.file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void log(String message) {
		try {
			this.writer.write(message);
			this.writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			this.writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String time() {
		return new SimpleDateFormat("dd-MM-yyyy").format(System.currentTimeMillis());
	}
}
