package doryanbessiere.mbd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import doryanbessiere.mbd.builder.MavenBuilderAPI;
import doryanbessiere.mbd.download.DownloadInfo;
import doryanbessiere.mbd.github.GithubAPI;

public class Exemple {

	public static void main(String[] args) {
		// clear the cache directory
		if (GithubAPI.getCacheDirectory().exists()) {
			try {
				FileUtils.deleteDirectory(GithubAPI.getCacheDirectory());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		GithubAPI.getCacheDirectory().mkdirs();

		// initialize GitHub access (with a token API)
		GithubAPI githubAPI = new GithubAPI("DoryanBessiere", "MyTokenAPI");
		try {
			githubAPI.download(GithubAPI.getCacheDirectory(), "MyProjectGithub", new DownloadInfo() {
				@Override
				public void start() {
					// Downloading the source code (master)
					System.out.println("Downloading...");
				}

				@Override
				public void finish() {
					System.out.println("Downloading finish");

					// Starting the decompression for use master directory
					System.out.println("Decompressing...");
					try {
						File unzip_directory = githubAPI.unzip(getFile());
						System.out.println("Decompressing finish");

						System.out.println("Building projects...");
						MavenBuilderAPI build = new MavenBuilderAPI(unzip_directory);
						try {
							// starting the build (if return true=success, false=failed)
							if (build.build(null, "package", "compile", "-U")) {
								System.out.println("Build success :D");
								// Upload the production here
								File latest_directory = new File("/var/www/html/production/");

								if (latest_directory.exists()) {
									FileUtils.deleteDirectory(latest_directory);
								}
								latest_directory.mkdirs();

								FileUtils.copyFileToDirectory(new File(unzip_directory, "target/application.jar"),
										latest_directory);
								System.exit(0);
							} else {
								System.out.println("Build failed!");
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void download() {
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
