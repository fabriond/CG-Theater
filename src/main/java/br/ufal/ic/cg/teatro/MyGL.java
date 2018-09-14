package br.ufal.ic.cg.teatro;

import java.util.ArrayList;

import com.jogamp.opengl.DebugGL2;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;

public class MyGL extends DebugGL2{

	public MyGL(GL2 downstream) {
		super(downstream);
	}
	
	ArrayList<Point> plateauPoints = new ArrayList<>();
	ArrayList<Point> innerPlateauPoints = new ArrayList<>();
	
	public void drawCircularWall(double radius, double yMin, double yMax) {
		double xCenter=0, zCenter=0;
		Point prevLower = new Point(0, 0, 0);
		Point prevHigher = new Point(0, 0, 0);
		double yDiff = yMax - yMin;
		double yPlateau = yDiff*3/4 + yMin;
		
		glBegin(GL_QUAD_STRIP);
			for(int i = -90; i <= 90; i+=10){
				double ang = (i * Math.PI/180);
				double x = Math.cos(ang)*radius;
				double z = Math.sin(ang)*radius;
				double xAux = xCenter + x*2/3;
				double zAux = zCenter + z*2/3;
				x += xCenter;
				z += zCenter;

				//used to draw stands
				plateauPoints.add(new Point(x, yPlateau, z));
				plateauPoints.add(new Point(xAux, yPlateau, zAux));
				innerPlateauPoints.add(new Point(xAux, yPlateau, zAux));
				innerPlateauPoints.add(new Point(xAux, yPlateau+5.0, zAux));				
				
				if(i >= -10 && i <= 10) {
					if(i == -10) {
						glVertex3d(prevLower.addY(yDiff*1/4-0.5));
						glVertex3d(prevHigher);
					}
					glVertex3d(x, yDiff*1/4 + yMin-0.5, z);
					glVertex3d(x, yMax, z);
					if(i == 10) {
						glVertex3d(x, yMin, z);
						glVertex3d(x, yMax, z);
					}
				} else {
					prevLower = new Point(x, yMin, z);
					prevHigher = new Point(x, yMax, z);
					glVertex3d(x, yMin, z);
					glVertex3d(x, yMax, z);
				}
			}
		glEnd();				
	}
	
	public void glColor(double r, double g, double b, double a) {
		glColor4d(r/255, g/255, b/255, a);
	}
	
	private void drawPlateaus(double zDiff, double yDiff) {
		for(int i = 0; i < 2; ++i) {
			glPushMatrix();
			if(i == 1) glTranslated(0.0, 5.0, 0.0);
			for(int j = 0; j < 3; ++j) {
				glBegin(GL_QUAD_STRIP);
					glVertex3d(plateauPoints.get(0).subX(zDiff));
					glVertex3d(plateauPoints.get(1).subX(zDiff));
					for(Point p : plateauPoints)
						glVertex3d(p);
					glVertex3d(plateauPoints.get(plateauPoints.size()-2).subX(zDiff));
					glVertex3d(plateauPoints.get(plateauPoints.size()-1).subX(zDiff));
				glEnd();
				glTranslated(0, -1.0/4.0*yDiff, 0);
			}
			glPopMatrix();
		}
		glPushMatrix();
		for(int j = 0; j < 3; ++j) {
			glBegin(GL_QUAD_STRIP);
				glVertex3d(innerPlateauPoints.get(0).subX(zDiff));
				glVertex3d(innerPlateauPoints.get(1).subX(zDiff));
				for(Point p : innerPlateauPoints)
					glVertex3d(p);
				glVertex3d(innerPlateauPoints.get(innerPlateauPoints.size()-2).subX(zDiff));
				glVertex3d(innerPlateauPoints.get(innerPlateauPoints.size()-1).subX(zDiff));
			glEnd();
			glTranslated(0, -1.0/4.0*yDiff, 0);
		}
		glPopMatrix();
		
		
	}
	
	void glVertex3d(Point p) {
		glVertex3d(p.x, p.y, p.z);
	}

	public void drawTheater(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
		
		//glColor3d(1, 0, 0);
		glColor(234, 181, 67,1.0);
		glBegin(GL_QUAD_STRIP);
			glVertex3d(xMin, yMax, zMin);
			glVertex3d(xMin, yMin, zMin);
			
			glVertex3d(xMin, yMax, zMax);
			glVertex3d(xMin, yMin, zMax);
			
			glVertex3d(xMax, yMax, zMax);
			glVertex3d(xMax, yMin, zMax);
			
			glVertex3d(xMax, yMax, zMin);
			glVertex3d(xMax, yMin, zMin);
		glEnd();
		//TODO: ADD THE PRESENTATION PLACE THINGY
		GLUT glut = new GLUT();
		glut.glutSolidCube(1.0f);
		
		//glColor3d(0, 0, 1);
		glColor(234, 181, 67,1.0);
		glRotated(90, 0, 1, 0);
		glPushMatrix();
			glTranslated(-zMin, 0.0, (xMax-xMin)/2 + xMin);
			drawCircularWall((xMax - xMin)/2, yMin, yMax);
			//glColor3d(0, 1, 0);
			glColor(204, 142, 53, 1.0);
			drawPlateaus((zMax - zMin), (yMax - yMin));
		glPopMatrix();
	}
	
	private class Point{
		double x, y, z;
		public Point(double x, double y, double z) {
			this.x = x; this.y = y; this.z = z;
		}
		
		public Point addY(double plus) {
			return new Point(x, y+plus, z);
		}
		
		public Point subX(double sub) {
			return new Point(x-sub, y, z);
		}
	}
	
}
