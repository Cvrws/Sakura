package cc.unknown.event.impl;

import cc.unknown.event.Event;

public class VelocityEvent implements Event {
    private double x, y, z;
    private final VelocityType velocityType;

    public VelocityEvent(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.velocityType = VelocityType.PRE;
    }

    public VelocityEvent() {
        this.velocityType = VelocityType.POST;
    }

    public boolean isPre() {
        return velocityType == VelocityType.PRE;
    }

    public boolean isPost() {
        return velocityType == VelocityType.POST;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        if (isPre()) this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        if (isPre()) this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        if (isPre()) this.z = z;
    }

    public VelocityType getVelocityType() {
        return velocityType;
    }

    public enum VelocityType {
        PRE, POST;
    }
}

