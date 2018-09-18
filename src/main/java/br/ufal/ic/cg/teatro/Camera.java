package br.ufal.ic.cg.teatro;

import com.jogamp.opengl.glu.GLU;

public class Camera {
	public static enum Direction {UP, DOWN, LEFT, RIGHT;}
	private final Point upDirection = new Point(0.0, 1.0, 0.0);
	private Point eyePos;
	private double dx, dy, dz;
	private double hAngle, vAngle;
	
	public Camera(double x, double y, double z) {
		eyePos = new Point(x, y, z);
		this.dx = 0.0;
		this.dy = 0.0;
		this.dz = 1.0;
		this.hAngle = 0.0;
		this.vAngle = 0.0;
	}
	
	public double getCenterX() {
		return eyePos.x+dx;
	}
	
	public double getCenterZ() {
		return eyePos.z+dz;
	}
	
	public void setLookAt(GLU glu) {
		Point.lookAt(glu, eyePos, eyePos.addToNewPoint(dx, dy, dz), upDirection);
	}
	
	public void move(double stepSize, Direction direction) {
		double module;
		//Idea for the left and right strafe codes gotten from: https://gamedev.stackexchange.com/questions/63819/first-person-camera-strafing-at-angle
		switch(direction) {
			case UP:
				eyePos.move(dx * stepSize, 0.0, dz * stepSize);
			break;
			
			case DOWN:
				eyePos.move(-dx * stepSize, 0.0, -dz * stepSize);
			break;
			
			case LEFT:
				Point left = Point.crossProduct(new Point(dx, dy, dz), upDirection);
				module = left.getModule();
				System.out.println("("+left.x+", "+left.y+", "+left.z+")");
				left.divide(-module, -module, -module);
				eyePos.move(stepSize*left.x, stepSize*left.y, stepSize*left.z);
			break;
			
			case RIGHT:
				Point right = Point.crossProduct(new Point(dx, dy, dz), upDirection);
				module = right.getModule();
				right.divide(module, module, module);
				eyePos.move(stepSize*right.x, stepSize*right.y, stepSize*right.z);
			break;
		}
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
	
	public void fly(double stepSize, Direction direction) {
		switch(direction) {
			case UP:
				eyePos.move(0, stepSize, 0);
			break;
			
			case DOWN:
				if(eyePos.y >= 0)
				eyePos.move(0, -stepSize, 0);
			break;
			
			default:
			break;			
		}
	}
	
	private void turnCam() {
		dx = Math.sin(hAngle);
		dy = Math.sin(vAngle);
		dz = Math.cos(hAngle);
	}
	
	public String toString() {
		return "Camera Info\n"
			 + "  (x, z): ("+(eyePos.x) + ", " + (eyePos.z)+")\n"
			 + "  (dx, dz): ("+(dx) + ", " + (dz)+")\n"
			 + "  horizontal angle: "+Math.toDegrees(hAngle)+"\n"
			 + "  vertical angle: "+Math.toDegrees(vAngle);
	}
}
