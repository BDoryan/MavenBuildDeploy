package doryanbessiere.mbd.builder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang3.SystemUtils;

public class MavenBuilderAPI {

	private File file;

	public MavenBuilderAPI(File file) {
		this.file = file;
	}

	/**
	 * 
	 * @param arguments
	 * @return true=success, false=failed
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public boolean build(String... arguments) throws FileNotFoundException, IOException, InterruptedException {
		System.out.println("Building project... "+file.getPath());
		if(arguments == null || arguments.length==0) {
			System.err.println("arguments not defined!");
			return false;
		}
		StringBuilder arguments_string_ = new StringBuilder();
		for(String argument : arguments) {
			arguments_string_.append(argument+" ");
		}
		System.out.println("Build arguments: "+arguments_string_.toString()+"\n");
		if (this.file.exists()) {
			String[] args = new String[arguments.length + 3];
			if(SystemUtils.IS_OS_UNIX) {
				args[0] = "bash";
				args[1] = "-c";	
			} else if (SystemUtils.IS_OS_WINDOWS){
				args[0] = "cmd.exe";
				args[1] = "/c";
			} else {
				System.err.println("Unsupported your oparating system! (only Windows and Unix)");
				return false;
			}
			args[2] = "mvn";	
			/*
			for(int i = 0; i < arguments.length; i++) {
				String arg = arguments[i];
				args[i+3] = arg;
			}*/

			StringBuilder arguments_string = new StringBuilder();
			for(String argument : args) {
				arguments_string.append(argument+" ");
			}
			System.out.println("Maven build arguments: "+arguments_string.toString()+"\n");
			
			ProcessBuilder pb = new ProcessBuilder(args[0], 
					args[1], args[2] +" "+arguments_string_);
			pb.directory(file);

			Process proc = pb.start();

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

			String s = null;
			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
			}

			while ((s = stdError.readLine()) != null) {
				System.err.println(s);
			}

			if (proc.waitFor() == 0) {
				return true;
			} else {
				return false;
			}
		} else {
			throw new FileNotFoundException(file.getPath() + " not found");
		}
	}

	public File getFile() {
		return file;
	}
}
