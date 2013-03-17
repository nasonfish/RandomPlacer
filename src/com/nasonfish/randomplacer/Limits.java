package com.nasonfish.randomplacer;

public class Limits {

	private int highX;
	private int highZ;
	private int lowX;
	private int lowZ;
	
	public Limits(int highX, int highZ, int lowX, int lowZ){
		if(highZ < lowZ || highX < lowX){
			RandomPlacer.log.info("The high/low numbers in the config conflict with each other." +
					" Please check the config for this error and then reload the config with /tpr reload.");
			RandomPlacer.log.info("Setting default world numbers to 1000, -1000, 1000, -1000");
			highX = 1000;
			lowX = -1000;
			highZ = 1000;
			lowZ = -1000;
		}
		this.setHighX(highX);
		this.setHighZ(highZ);
		this.setLowX(lowX);
		this.setLowZ(lowZ);
	}

	public int getHighX() {
		return highX;
	}

	public void setHighX(int highX) {
		this.highX = highX;
	}

	public int getHighZ() {
		return highZ;
	}

	public void setHighZ(int highZ) {
		this.highZ = highZ;
	}

	public int getLowX() {
		return lowX;
	}

	public void setLowX(int lowX) {
		this.lowX = lowX;
	}

	public int getLowZ() {
		return lowZ;
	}

	public void setLowZ(int lowZ) {
		this.lowZ = lowZ;
	}
	
}
