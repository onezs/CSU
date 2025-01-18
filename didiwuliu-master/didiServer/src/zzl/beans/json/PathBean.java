package zzl.beans.json;

public class PathBean {
	private String location;

	private String destination;
	private float carriage;

	public PathBean() {
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public float getCarriage() {
		return carriage;
	}

	public void setCarriage(float carriage) {
		this.carriage = carriage >= 0 ? carriage : 0.0f;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}
}
