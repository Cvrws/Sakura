package cc.unknown.event.impl;

import cc.unknown.event.Event;

public class VelocityEvent implements Event {
	private double x, y, z;

	public VelocityEvent(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public static class Post extends VelocityEvent {
		public Post(double x, double y, double z) {
			super(x, y, z);
		}
	}
}
