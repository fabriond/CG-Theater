package br.ufal.ic.cg.teatro;

import com.jogamp.opengl.glu.GLU;

public class Camera {
	public static enum Direction {UP, DOWN, LEFT, RIGHT;}
	private double x, y, z;
	private double dx, dy, dz;
	private double hAngle, vAngle;
	
	public Camera(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.dx = 0.0;
		this.dy = 0.0;
		this.dz = 1.0;
		this.hAngle = 0.0;
		this.vAngle = 0.0;
	}
	
	public double getCenterX() {
		return x+dx;
	}
	
	public double getCenterZ() {
		return z+dz;
	}
	
	public void setLookAt(GLU glu) {
		glu.gluLookAt(x, y, z, x+dx, y+dy, z+dz, 0.0, 1.0, 0.0);
	}
	
	public void move(double stepSize, Direction direction) {
		switch(direction) {
			case UP:
				x += dx * stepSize;
				z += dz * stepSize;
			break;
			
			case DOWN:
				x -= dx * stepSize;
				z -= dz * stepSize;
			break;
			
			case LEFT:
				x += dz * stepSize;
				z += dx * stepSize;
			break;
			
			case RIGHT:
				x -= dz * stepSize;
				z -= dx * stepSize;
			break;
		}
	}
	
	public void moveForward(double stepSize) {
		x += dx * stepSize;
		z += dz * stepSize;
	}
	
	public void moveBackward(double stepSize) {
		x -= dx * stepSize;
		z -= dz * stepSize;
	}
	
	public void turn(double turnAngle, Direction direction) {
		switch(direction) {
			case UP:
				if(vAngle + turnAngle < Math.toRadians(92))
					vAngle += turnAngle;
			break;
			
			case DOWN:
				if(vAngle - turnAngle > Math.toRadians(-92))
					vAngle -= turnAngle;
			break;
			
			case LEFT:
				hAngle += turnAngle;
			break;
			
			case RIGHT:
				hAngle -= turnAngle;
			break;
		}
		turnCam();
	}
	
	private void turnCam() {
		dx = Math.sin(hAngle);
		dy = Math.sin(vAngle);
		dz = Math.cos(hAngle);
	}
	
	public String toString() {
		return "Camera Info\n"
			 + "  (x, z): ("+(x) + ", " + (z)+")\n"
			 + "  (dx, dz): ("+(dx) + ", " + (dz)+")\n"
			 + "  horizontal angle: "+Math.toDegrees(hAngle)+"\n"
			 + "  vertical angle: "+Math.toDegrees(vAngle);
	}
}
