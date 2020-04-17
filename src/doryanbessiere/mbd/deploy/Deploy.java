package doryanbessiere.mbd.deploy;

import java.io.File;

public abstract class Deploy {

	private File destination;

	public Deploy(File destination) {
		this.destination = destination;
	}
	
	public abstract void deploy();
	
	public File getDestination() {
		return destination;
	}
}
