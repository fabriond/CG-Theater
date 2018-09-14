package br.ufal.ic.cg.teatro;

import com.jogamp.opengl.glu.GLU;

public class Camera {
	private double x, z;
	private double dx, dz;
	private double angle;
	
	public Camera(double x, double z) {
		this.x = x;
		this.z = z;
		this.dx = 0.0;
		this.dz = 1.0;
		this.angle = 0.0;
	}
	
	public double getCenterX() {
		return x+dx;
	}
	
	public double getCenterZ() {
		return z+dz;
	}
	
	public void setLookAt(GLU glu) {
		glu.gluLookAt(x, 1.0, z, x+dx, 1.0, z+dz, 0.0, 1.0, 0.0);
	}
	
	public void moveForward(double stepSize) {
		x += dx * stepSize;
		z += dz * stepSize;
	}
	
	public void moveBackward(double stepSize) {
		x -= dx * stepSize;
		z -= dz * stepSize;
	}
	
	public void turnLeft(double turnAngle) {
		angle += turnAngle;
		turn();
	}
	
	public void turnRight(double turnAngle) {
		angle -= turnAngle;
		turn();
	}
	
	private void turn() {
		dx = Math.sin(angle);
		dz = Math.cos(angle);
	}
	
	public String toString() {
		return "Camera Info\n  (x, z): ("+(x) + ", " + (z)+")\n  (dx, dz): ("+(dx) + ", " + (dz)+")\n  current angle: "+angle;
	}
}
