package de.me.gradeplanner.business;


public class Grade {
	private int value;
	private int weight;
	
	
	public Grade(int value, int weight) {
		this.value = value;
		this.weight = weight;
	}

	public int getValue() {
		return this.value;
	}
	
	public int getWeight() {
		return this.weight;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int describeContents() {
		return 0;
	}
}
