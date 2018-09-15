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
	ArrayList<Point> roofPoints = new ArrayList<>();
	
	public void drawCircularPart(double radius, double yMin, double yMax, double zMin, double zMax) {
		double xCenter=0, zCenter=0;
		Point prevLower = new Point(0, 0, 0);
		Point prevHigher = new Point(0, 0, 0);
		double yDiff = yMax - yMin;
		double zDiff = zMax - zMin;
		double yPlateau = yDiff*3/4 + yMin;
		roofPoints.add(new Point(xCenter, yMax, zCenter));
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
				roofPoints.add(new Point(x, yMax, z));
				
				if(i >= -10 && i <= 20) {
					//this is for the door space
					if(i == -10) {
						glVertex3d(prevLower.addToNewPoint(0.0, yDiff*1.0/4.0-0.5, 0.0));
						glVertex3d(prevHigher);
					}
					glVertex3d(x, yDiff*1/4 + yMin-0.5, z);
					glVertex3d(x, yMax, z);
					if(i == 20) {
						glVertex3d(x, yMin, z);
						glVertex3d(x, yMax, z);
					}
				} else {
					//points on the circular wall
					prevLower = new Point(x, yMin, z);
					prevHigher = new Point(x, yMax, z);
					glVertex3d(x, yMin, z);
					glVertex3d(x, yMax, z);
				}
			}
		glEnd();
		drawCircularRoof();
		drawPlateaus(yDiff, zDiff);
	}
	
	private void drawCircularRoof() {
		glBegin(GL_TRIANGLE_FAN);
		for(Point p : roofPoints) {
			glVertex3d(p);
		}
		glEnd();
	}
	
	public void glColor(double r, double g, double b, double a) {
		glColor4d(r/255, g/255, b/255, a);
	}
	
	private void drawPlateaus(double yDiff, double zDiff) {
		//glColor3d(0, 1, 0);
		//glColor(204, 142, 53, 1.0);
		glColor(141, 110, 99, 1.0);
		for(int i = 0; i < 2; ++i) {
			glPushMatrix();
			if(i == 1) glTranslated(0.0, 5.0, 0.0);
			for(int j = 0; j < 3; ++j) {
				glBegin(GL_QUAD_STRIP);
					glVertex3d(plateauPoints.get(0).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
					glVertex3d(plateauPoints.get(1).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
					for(Point p : plateauPoints)
						glVertex3d(p);
					glVertex3d(plateauPoints.get(plateauPoints.size()-2).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
					glVertex3d(plateauPoints.get(plateauPoints.size()-1).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
				glEnd();
				glTranslated(0, -1.0/4.0*yDiff, 0);
			}
			glPopMatrix();
		}
		glPushMatrix();
		for(int j = 0; j < 3; ++j) {
			glBegin(GL_QUAD_STRIP);
				glVertex3d(innerPlateauPoints.get(0).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
				glVertex3d(innerPlateauPoints.get(1).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
				for(Point p : innerPlateauPoints)
					glVertex3d(p);
				glVertex3d(innerPlateauPoints.get(innerPlateauPoints.size()-2).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
				glVertex3d(innerPlateauPoints.get(innerPlateauPoints.size()-1).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
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
		//glColor(234, 181, 67,1.0);
		glColor(255, 236, 179,1.0);
		//regular walls
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
		
		//regular roof
		glBegin(GL_QUADS);
			glVertex3d(xMin, yMax, zMax);
			glVertex3d(xMax, yMax, zMax);
			glVertex3d(xMax, yMax, zMin);
			glVertex3d(xMin, yMax, zMin);
		glEnd();
		
		//stage
		GLUT glut = new GLUT();
		glColor(121, 85, 72, 1.0);
		glPushMatrix();
			glTranslated((xMax+xMin)/2, yMin+(yMax-yMin)/20, zMax-(zMax-zMin)/5);
			glScaled((xMax-xMin)-0.1, (yMax-yMin)/10, (zMax-zMin)/5-0.1);
			glut.glutSolidCube(1.0f);
		glPopMatrix();
		
		glColor(215, 204, 200,1.0);
		//left column
		glPushMatrix();
			glTranslated(xMax-(xMax-xMin)/10, yMin+yMax/2, zMax-2*(zMax-zMin)/10);
			glScaled((xMax-xMin)/5-0.1, yMax-0.1, 2*(zMax-zMin)/5-0.1);
			glut.glutSolidCube(1.0f);
		glPopMatrix();
		//right column
		glPushMatrix();
			glTranslated(xMin+(xMax-xMin)/10, yMin+yMax/2, zMax-2*(zMax-zMin)/10);
			glScaled((xMax-xMin)/5-0.1, yMax-0.1, 2*(zMax-zMin)/5-0.1);
			glut.glutSolidCube(1.0f);
		glPopMatrix();
		
		//glColor3d(0, 0, 1);
		//glColor(234, 181, 67,1.0);
		glColor(255, 236, 179,1.0);
		glRotated(90, 0, 1, 0);
		//circular part (including stands and circular part of the roof)
		glPushMatrix();
			glTranslated(-zMin, 0.0, (xMax-xMin)/2 + xMin);
			drawCircularPart((xMax - xMin)/2, yMin, yMax, zMin, zMax);
		glPopMatrix();
	}
	
}
