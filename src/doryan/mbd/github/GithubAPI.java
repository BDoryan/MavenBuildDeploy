package doryan.mbd.github;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import doryan.mbd.download.DownloadInfo;

public class GithubAPI {

	private String username;
	private String token;

	public GithubAPI(String username, String token) {
		this.username = username;
		this.token = token;
	}

	public boolean download(File destination, String project, DownloadInfo downloadInfo) throws IOException {
		String url_string = "https://github.com/" + username + "/" + project + "/archive/master.zip";

		File file = new File(destination, "master.zip");

		if (file.exists()) {
			file.delete();
		}
		file.getParentFile().mkdirs();
		file.createNewFile();
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url_string);
		request.setHeader("Authorization", "token " + this.token);
		HttpResponse response = client.execute(request);
		HttpEntity entity = response.getEntity();

		if (downloadInfo != null) {
			downloadInfo.file = file;
		}

		InputStream is = entity.getContent();

		FileOutputStream fos = new FileOutputStream(file);
		if (downloadInfo != null)
			downloadInfo.start();

		int inByte;
		while ((inByte = is.read()) != -1) {
			fos.write(inByte);
			if (downloadInfo != null)
				downloadInfo.download();
		}

		is.close();
		fos.close();
		if (downloadInfo != null)
			downloadInfo.finish();
		return true;
	}

	public File unzip(File file) throws FileNotFoundException, IOException {
		byte[] buffer = new byte[1024];

		ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
		ZipEntry ze = zis.getNextEntry();

		File zip_directory = null;

		while (ze != null) {
			String fileName = ze.getName();
			File newFile = new File(getCacheDirectory(), fileName);

			if (ze.isDirectory()) {
				newFile.mkdirs();
				if (zip_directory == null)
					zip_directory = newFile;
			} else {
				FileOutputStream fos = new FileOutputStream(newFile);

				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

				fos.close();
			}
			ze = zis.getNextEntry();
		}

		zis.closeEntry();
		zis.close();
		
		return zip_directory;
	}

	public String getUsername() {
		return username;
	}

	public String getToken() {
		return token;
	}

	public static File getCacheDirectory() {
		File cache_directory = new File(localDirectory(), "cache");
		if (!cache_directory.exists())
			cache_directory.mkdirs();
		return cache_directory;
	}

	public static File localDirectory() {
		try {
			File file = new File(GithubAPI.class.getProtectionDomain().getCodeSource().getLocation().toURI())
					.getParentFile();
			return file;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
}
