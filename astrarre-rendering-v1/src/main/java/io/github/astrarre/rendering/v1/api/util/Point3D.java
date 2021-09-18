package io.github.astrarre.rendering.v1.api.util;

import java.awt.geom.Point2D;

import it.unimi.dsi.fastutil.HashCommon;

public abstract class Point3D extends Point2D implements Cloneable {
	public static class Float extends Point3D {
		float x, y, z;

		public Float() {
		}

		public Float(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		@Override
		public double getZ() {
			return this.z;
		}

		@Override
		public void setLocation(double x, double y, double z) {
			this.x = (float) x;
			this.y = (float) y;
			this.z = (float) z;
		}

		@Override
		public double getX() {
			return this.x;
		}

		@Override
		public double getY() {
			return this.y;
		}
	}


	public abstract double getZ();

	public abstract void setLocation(double x, double y, double z);

	@Override
	public void setLocation(double x, double y) {
		this.setLocation(x, y, this.getZ());
	}

	public void setLocation(Point3D point) {
		this.setLocation(point.getX(), point.getY(), point.getZ());
	}

	public double distanceSq(double x, double y, double z) {
		double px = x - this.getX();
		double py = y - this.getY();
		double pz = z - this.getZ();
		return (px * px + py * py + pz * pz);
	}

	public double distanceSq(Point3D point) {
		return this.distanceSq(point.getX(), point.getY(), point.getZ());
	}

	public double distance(double x, double y, double z) {
		return Math.sqrt(this.distanceSq(x, y, z));
	}

	public double distance(Point3D point) {
		return this.distance(point.getX(), point.getY(), point.getZ());
	}

	@Override
	public Point3D clone() {
		return (Point3D) super.clone();
	}

	@Override
	public int hashCode() {
		long bits = java.lang.Double.doubleToLongBits(this.getX());
		bits ^= java.lang.Double.doubleToLongBits(this.getY()) * 31;
		bits ^= java.lang.Double.doubleToLongBits(this.getZ()) * 31;
		return HashCommon.long2int(bits);
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && obj instanceof Point3D p && p.getZ() == this.getZ();
	}

	@Override
	public String toString() {
		return "[" + this.getX() + ", " + this.getY() + ", " + this.getZ() + "]";
	}
}
