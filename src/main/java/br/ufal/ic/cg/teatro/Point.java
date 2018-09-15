package br.ufal.ic.cg.teatro;

import com.jogamp.opengl.glu.GLU;

public class Point {
	double x, y, z;
	public Point(double x, double y, double z) {
		this.x = x; this.y = y; this.z = z;
	}
	
	public Point addToNewPoint(double dx, double dy, double dz) {
		return new Point(x+dx, y+dy, z+dz);
	}
	
	public void move(double dx, double dy, double dz) {
		x+=dx;
		y+=dy;
		z+=dz;
	}
	
	public void divide(double dx, double dy, double dz) {
		x/=dx;
		y/=dy;
		z/=dz;
	}
	
	public double getModule() {
		return Math.sqrt(x*x+y*y+z*z);
	}
	
	public static void lookAt(GLU glu, Point eye, Point center, Point up) {
		glu.gluLookAt(eye.x, eye.y, eye.z, center.x, center.y, center.z, up.x, up.y, up.z);
	}
	
	//Cross product code adapted from: https://gatechgrad.wordpress.com/2011/10/08/cross-product/
	public static Point crossProduct(Point p1, Point p2) {
		return new Point((p1.y * p2.z) - (p1.z * p2.y), (p1.z * p2.x) - (p1.x * p2.z), (p1.x * p2.y) - (p1.y * p2.x));
	}
}
