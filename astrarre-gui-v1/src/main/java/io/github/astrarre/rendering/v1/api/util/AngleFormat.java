package io.github.astrarre.rendering.v1.api.util;

public enum AngleFormat {
	ROTATIONS(1),
	PI(2),
	RADIAN(Math.PI * 2),
	BINARY(256),
	DEGREES(360),
	GRAD(400),
	MINUTE(21600),
	SECONDS(1296000);

	final double rotationRatio;

	AngleFormat(double ratio) {
		this.rotationRatio = ratio;
	}

	public double convert(AngleFormat dest, double angle) {
		if(dest == this) return angle; // todo add cos squared support
		return (angle / this.rotationRatio) * dest.rotationRatio;
	}

	public float convert(AngleFormat dest, float angle) {
		if(dest == this) return angle;
		return (float) ((angle / this.rotationRatio) * dest.rotationRatio);
	}

}
