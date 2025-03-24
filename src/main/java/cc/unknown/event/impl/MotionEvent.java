package cc.unknown.event.impl;

import cc.unknown.event.CancellableEvent;

public class MotionEvent extends CancellableEvent {
    private double posX;
    private double posY;
    private double posZ;
    private float yaw;
    private float pitch;
    private boolean onGround;
    private boolean isSprinting;
    private boolean isSneaking;
    
    public MotionEvent(double posX, double posY, double posZ, float yaw, float pitch, boolean onGround, boolean isSprinting, boolean isSneaking) {
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.yaw = yaw;
		this.pitch = pitch;
		this.onGround = onGround;
		this.isSprinting = isSprinting;
		this.isSneaking = isSneaking;
	}
    
    public static class Pre extends MotionEvent {
        public Pre(double posX, double posY, double posZ, float yaw, float pitch, boolean onGround, boolean isSprinting, boolean isSneaking) {
            super(posX, posY, posZ, yaw, pitch, onGround, isSprinting, isSneaking);
        }
    }

    public static class Post extends MotionEvent {
        public Post(double posX, double posY, double posZ, float yaw, float pitch, boolean onGround, boolean isSprinting, boolean isSneaking) {
            super(posX, posY, posZ, yaw, pitch, onGround, isSprinting, isSneaking);
        }
    }

	public double getPosX() {
		return posX;
	}

	public void setPosX(double posX) {
		this.posX = posX;
	}

	public double getPosY() {
		return posY;
	}

	public void setPosY(double posY) {
		this.posY = posY;
	}

	public double getPosZ() {
		return posZ;
	}

	public void setPosZ(double posZ) {
		this.posZ = posZ;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public boolean isOnGround() {
		return onGround;
	}

	public void setOnGround(boolean onGround) {
		this.onGround = onGround;
	}

	public boolean isSprinting() {
		return isSprinting;
	}

	public void setSprinting(boolean isSprinting) {
		this.isSprinting = isSprinting;
	}

	public boolean isSneaking() {
		return isSneaking;
	}

	public void setSneaking(boolean isSneaking) {
		this.isSneaking = isSneaking;
	}
}
