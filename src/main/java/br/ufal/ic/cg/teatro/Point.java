package br.ufal.ic.cg.teatro;

import com.jogamp.opengl.glu.GLU;

public class Point {
	double x, y, z;
	double angle; //used for circular lighting (to have unitary normal vectors)
	public Point(double x, double y, double z) {
		this.x = x; this.y = y; this.z = z;
	}
	
	public Point(double x, double y, double z, double angle) {
		this.x = x; this.y = y; this.z = z; this.angle = angle;
	}
	
	/**
	 * Creates a new point adding dx, dy and dz to their respective values in the current point
	 * */
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
	
	@Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!Point.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final Point other = (Point) obj;

        return this.x == other.x && this.y == other.y && this.z == other.z;
    }
	
	public String toString() {
		return "("+x+", "+y+", "+z+")";
		
	}
}
