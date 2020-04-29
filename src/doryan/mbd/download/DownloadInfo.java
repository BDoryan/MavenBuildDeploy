package doryan.mbd.download;

import java.io.File;

public abstract class DownloadInfo {

	public String url;
	public File file;
	
	public abstract void start();
	public abstract void download();
	public abstract void finish();
	
	public File getFile() {
		return file;
	}
	
	public String getUrl() {
		return url;
	}
}
