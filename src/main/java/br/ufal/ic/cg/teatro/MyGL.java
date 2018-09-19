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
	ArrayList<Point> plateauCutPoints = new ArrayList<>();
	ArrayList<Point> innerPlateauCutPoints = new ArrayList<>();
	Point doorStart, doorEnd;
	
	private void drawCircularPart(double radius, double xMin, double xMax, double yMin, double yMax, double zMin, double zMax) {
		Point prevLower = new Point(0, 0, 0);
		Point prevHigher = new Point(0, 0, 0);
		double yDiff = yMax - yMin;
		double zDiff = zMax - zMin;
		double yPlateau = yDiff*2/3 + yMin;
		roofPoints.add(new Point(0.0, yMax, 0.0));
		glBegin(GL_QUAD_STRIP);
			for(int i = -90; i <= 90; i+=10){
				double ang = (i * Math.PI/180);
				double x = Math.cos(ang)*radius;
				double z = Math.sin(ang)*radius;
				double xAux = x*2/3;
				double zAux = z*2/3;

				//used to draw stands
				plateauPoints.add(new Point(x, yPlateau, z));
				plateauPoints.add(new Point(xAux, yPlateau, zAux));
				innerPlateauPoints.add(new Point(xAux, yPlateau, zAux));
				innerPlateauPoints.add(new Point(xAux, yPlateau+5.0, zAux));
				roofPoints.add(new Point(x, yMax, z));
				
				if(i >= -20 && i <= 20) {
					plateauCutPoints.add(new Point(x, yPlateau, z));
					plateauCutPoints.add(new Point(xAux, yPlateau, zAux));
					innerPlateauCutPoints.add(new Point(xAux, yPlateau, zAux));
					innerPlateauCutPoints.add(new Point(xAux, yPlateau+5.0, zAux));
				}
				
				if(i >= -10 && i <= 20) {
					//this is for the door space
					if(i == -10) {
						glVertex3d(prevLower.addToNewPoint(0.0, yDiff/3.0, 0.0));
						glVertex3d(prevHigher);
						doorStart = new Point(x, yMin, z);
					}
					glVertex3d(x, yDiff/3.0 + yMin, z);
					glVertex3d(x, yMax, z);	
					if(i == 10) doorEnd = new Point(x, yDiff/3.0 + yMin, z);
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
		drawColumns(xMin, yMin, zMin, xMax, yMax, zMax);
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
		glPushMatrix();
			for(int i = 0; i < 2; ++i) {
				if(i == 1) glTranslated(0.0, 5.0, 0.0);
				glPushMatrix();
					for(int j = 0; j < 2; ++j) {
						glBegin(GL_QUAD_STRIP);
							glVertex3d(plateauPoints.get(0).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
							glVertex3d(plateauPoints.get(1).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
							for(Point p : plateauPoints)
								glVertex3d(p);					
							glVertex3d(plateauPoints.get(plateauPoints.size()-2).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
							glVertex3d(plateauPoints.get(plateauPoints.size()-1).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
						glEnd();
						glTranslated(0, -yDiff/3.0, 0);
					}
				glPopMatrix();
			}
			glTranslated(0.0, -2*yDiff/3.0, 0.0);
			boolean stopped = false;
			glBegin(GL_QUAD_STRIP);
				glVertex3d(plateauPoints.get(0).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
				glVertex3d(plateauPoints.get(1).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
				for(Point p : plateauPoints) {
					if(plateauCutPoints.contains(p) && !stopped) {
						glEnd();
						stopped = true;
					}
					else if(!plateauCutPoints.contains(p) && stopped) {
						glBegin(GL_QUAD_STRIP);
						stopped = false;
					}
					if(!stopped) glVertex3d(p);					
				}
				glVertex3d(plateauPoints.get(plateauPoints.size()-2).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
				glVertex3d(plateauPoints.get(plateauPoints.size()-1).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
			glEnd();
		glPopMatrix();
		
		glPushMatrix();
			for(int j = 0; j < 2; ++j) {
				glBegin(GL_QUAD_STRIP);
					glVertex3d(innerPlateauPoints.get(0).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
					glVertex3d(innerPlateauPoints.get(1).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
					for(Point p : innerPlateauPoints)
							glVertex3d(p);
					glVertex3d(innerPlateauPoints.get(innerPlateauPoints.size()-2).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
					glVertex3d(innerPlateauPoints.get(innerPlateauPoints.size()-1).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
				glEnd();
				glTranslated(0, -yDiff/3.0, 0);
			}
			stopped = false;
			glBegin(GL_QUAD_STRIP);
				glVertex3d(innerPlateauPoints.get(0).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
				glVertex3d(innerPlateauPoints.get(1).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
				Point lastPoint = null;
				for(Point p : innerPlateauPoints) {
					if(innerPlateauCutPoints.contains(p) && !stopped) {
						glVertex3d(lastPoint.addToNewPoint(lastPoint.x/2.0, -5, lastPoint.z/2.0));
						glVertex3d(lastPoint.addToNewPoint(lastPoint.x/2.0, 0, lastPoint.z/2.0));
						glEnd();
						stopped = true;
					}
					else if(!innerPlateauCutPoints.contains(p) && stopped) {
						glBegin(GL_QUAD_STRIP);
						glVertex3d(p.addToNewPoint(p.x/2.0, 0, p.z/2.0));
						glVertex3d(p.addToNewPoint(p.x/2.0, 5, p.z/2.0));
						stopped = false;
					}
					if(!stopped) glVertex3d(p);
					lastPoint = p;
				}
				glVertex3d(innerPlateauPoints.get(innerPlateauPoints.size()-2).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
				glVertex3d(innerPlateauPoints.get(innerPlateauPoints.size()-1).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
			glEnd();
		glPopMatrix();		
	}
	
	public void glVertex3d(Point p) {
		glVertex3d(p.x, p.y, p.z);
	}
	
	private void drawOutside(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax, double doorAngle, GLUT glut) {
		double wallWidth = 5.0;
		double doorSize = (xMax-xMin+(wallWidth+0.2)*2)/6;
		glColor(255, 205, 210, 0.5);
		//side walls
		glPushMatrix();
			glTranslated(xMin-(wallWidth+0.2)/2, (yMax-yMin)/2, zMin+(zMax-zMin)/2-(xMax - xMin)/4);
			glPushMatrix();
				glScaled(wallWidth, yMax-yMin, zMax-zMin+(xMax - xMin)/2+wallWidth*1.15);
				glut.glutSolidCube(1.0f);
			glPopMatrix();

			glPushMatrix();
				glTranslated(xMax-xMin+(wallWidth+0.2), 0, 0);
				glScaled(wallWidth, yMax-yMin, zMax-zMin+(xMax - xMin)/2+wallWidth*1.15);
				glut.glutSolidCube(1.0f);
			glPopMatrix();
		glPopMatrix();
		
		//front and back walls
		glPushMatrix();
			glTranslated((xMax-xMin+(wallWidth+0.2)*2)/2+xMin-wallWidth, (yMax-yMin)/2, zMin-(xMax - xMin)/2 - wallWidth/2);
			glPushMatrix();
				glTranslated((xMax-xMin+(wallWidth+0.2)*2)/(2*wallWidth/3-0.1), 0, wallWidth*1.15/2.0-0.4);
				glScaled((xMax-xMin+(wallWidth+0.2)*2)/(2*wallWidth/3-0.1), yMax-yMin, wallWidth*1.15);
				glut.glutSolidCube(1.0f);
			glPopMatrix();
			
			glPushMatrix();
				glTranslated(-(xMax-xMin+(wallWidth+0.2)*2)/(2*wallWidth/3-0.1), 0, wallWidth*1.15/2.0-0.4);
				glScaled((xMax-xMin+(wallWidth+0.2)*2)/(2*wallWidth/3-0.1), yMax-yMin, wallWidth*1.15);
				glut.glutSolidCube(1.0f);
			glPopMatrix();
			//center front wall
			glPushMatrix();
				glTranslated(0, (yMax-yMin)/8.08, wallWidth*0.5/2.0-0.4);
				glPushMatrix();
					glScaled((xMax-xMin+(wallWidth+0.2)*2)/3, (yMax-yMin)-(yMax-yMin)/4, wallWidth*0.6);
					glut.glutSolidCube(1.0f);
				glPopMatrix();
				glTranslated(0, (yMax+yMin)/3, wallWidth*0.022);
				glRotated(45, 0, 0, 1);
				glScaled((xMax-xMin)/1.3, (yMax-yMin)/1.3, wallWidth*0.61);
				glut.glutSolidCube(1.0f);
			glPopMatrix();
			
			//back wall
			glPushMatrix();
				glTranslated(-0.23, (yMax-yMin)*0.05, zMax-zMin+(wallWidth+0.1) + (xMax-xMin)/2 + wallWidth*10);
				glPushMatrix();
					glScaled((xMax-xMin+(wallWidth+0.1)*2)*1.2, (yMax-yMin)*1.1, wallWidth*20);
					glut.glutSolidCube(1.0f);
				glPopMatrix();
				//roof pieces
				glTranslated(0, (yMax+yMin)*1.1/2, wallWidth*9.5);
				glPushMatrix();
					glRotated(45, 0, 0, 1);
					glScaled((xMax-xMin)/1.15, (yMax-yMin)/1.15, wallWidth);
					glut.glutSolidCube(1.0f);
				glPopMatrix();
				glPushMatrix();
					glTranslated(0, 0, -18.5*wallWidth);
					glRotated(45, 0, 0, 1);
					glScaled((xMax-xMin)/1.2, (yMax-yMin)/1.2, wallWidth);
					glut.glutSolidCube(1.0f);
				glPopMatrix();
				glColor(225, 112, 85,1.0);
				glTranslated(0,0,-wallWidth*9.5);
				glRotated(45, 0, 0, 1);
				glScaled((xMax-xMin)/1.17, (yMax-yMin)/1.17, wallWidth*18);
				glut.glutSolidCube(1.0f);
				//roof pieces end here
			glPopMatrix();
			
			//doors
			glPushMatrix();
				glTranslated(-doorSize, -(yMax-yMin)/2.0+(yMax-yMin)/8, wallWidth/2.5);
				glRotated(doorAngle, 0, 1, 0);
				glTranslated(doorSize/2, 0, 2);
				glColor(215, 204, 200, 1.0);
				glScaled(doorSize, (yMax-yMin)*3.0/11, 2);
				glut.glutSolidCube(1.0f);
			glPopMatrix();

			glPushMatrix();
				glTranslated(doorSize, -(yMax-yMin)/2.0+(yMax-yMin)/8, wallWidth/2.5);
				glRotated(-doorAngle, 0, 1, 0);
				glTranslated(-doorSize/2, 0, 2);
				glColor(215, 204, 200, 1.0);
				glScaled(doorSize, (yMax-yMin)*3.0/11, 2);
				glut.glutSolidCube(1.0f);
			glPopMatrix();
		glPopMatrix();
	}
	
	private void drawColumns(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
		GLUT glut = new GLUT();
		Point p, aux;
		glPushMatrix();
			glColor(44, 62, 80, 0.1);
			//glColor(44, 44, 84, 0.5);
			p = plateauPoints.get(1);
			for(int i = 2; i < 5; ++i) {
				if(i != 3) {
					glPushMatrix();
						glTranslated(p.x-((zMax-zMin)/1.5)+18*i, yMin, p.z);
						glRotated(-90, 1, 0, 0);
						glut.glutSolidCylinder(1.0, (yMax-yMin), 10, 1);
					glPopMatrix();
				}
				glPushMatrix();
					glTranslated((p.x-((zMax-zMin)/1.5)+18*(i-1)) + 9*(i-1), yMin, p.z*(3.0/2.5));
					for(int j = 0; j < 2; ++j) {
						glColor(255, 255, 255,1.0);
						glTranslated(0, (yMax-yMin)/3.0, 0);
						glPushMatrix();
							glut.glutSolidSphere(2.5, 10, 10);
							glTranslated(0, 4, 0);
							drawPlateauChair(glut);
						glPopMatrix();
					}
					glTranslated(0, -2*(yMax-yMin)/3, 0);
					glTranslated(0, 4, 0);
					drawPlateauChair(glut);
					glColor(44, 62, 80, 0.1);
				glColor(44, 62, 80, 0.1);
				glPopMatrix();
				
			}
			for(int i = 1; i < plateauPoints.size(); i+=6) {
				if(i > 13 && i < 25) continue;
				glPushMatrix();
					p = plateauPoints.get(i);
					glTranslated(p.x, yMin, p.z);
					glRotated(-90, 1, 0, 0);
					glut.glutSolidCylinder(1.0, (yMax-yMin), 10, 1);
				glPopMatrix();
				glPushMatrix();
					if(i > 3 && i != 25) {
						aux = plateauPoints.get(i-3);
						for(int j = 0; j < 2; ++j) {
							glColor(255, 255, 255,1.0);
							glPushMatrix();
								glTranslated(aux.x*1.7/2, yMin+(yMax-yMin)/3.0+0.5, aux.z*1.7/2);
								glut.glutSolidSphere(2.5, 10, 10);
								glTranslated(0, 4, 0);
								glRotated(-50, 0, 1, 0);
								if(i > 25) glRotated(255, 0, 1, 0);
								drawPlateauChair(glut);
							glPopMatrix();
							glTranslated(0, (yMax-yMin)/3.0, 0);
						}
						glTranslated(aux.x*1.7/2, -2*(yMax-yMin)/3, aux.z*1.7/2);
						glTranslated(0, 4, 0);
						glRotated(-50, 0, 1, 0);
						if(i > 25) glRotated(255, 0, 1, 0);
						drawPlateauChair(glut);
					}
				glColor(44, 62, 80, 0.1);
				glPopMatrix();
			}
			for(int i = 2; i < 5; ++i) {
				if(i != 3) {
					glPushMatrix();
						glTranslated(p.x-((zMax-zMin)/1.5)+18*i, yMin, p.z);
						glRotated(-90, 1, 0, 0);
						glut.glutSolidCylinder(1.0, (yMax-yMin), 10, 1);
					glPopMatrix();
				}
				glPushMatrix();
					glTranslated((p.x-((zMax-zMin)/1.5)+18*(i-1)) + 9*(i-1), yMin, p.z*(3.0/2.5));
					for(int j = 0; j < 2; ++j) {
						glColor(255, 255, 255,1.0);
						glTranslated(0, (yMax-yMin)/3.0, 0);
						glPushMatrix();
							glut.glutSolidSphere(2.5, 10, 10);
							glTranslated(0, 4, 0);
							glRotated(180, 0, 1, 0);
							drawPlateauChair(glut);
						glPopMatrix();
					}
					glTranslated(0, -2*(yMax-yMin)/3, 0);
					glTranslated(0, 4, 0);
					glRotated(180, 0, 1, 0);
					drawPlateauChair(glut);
					glColor(44, 62, 80, 0.1);
				glPopMatrix();
			}
		glPopMatrix();
	}
	
	private void drawInnerChair(GLUT glut) {
		//chair back
		glColor(255, 177, 66, 0.5);
		glPushMatrix();
			glTranslated(0, 5, 0);
			glRotated(-10, 1, 0, 0);
			glScaled(5, 10, 1.5);
			glut.glutSolidCube(1.0f);
		glPopMatrix();
		//chair seat
		glPushMatrix();
			glTranslated(0, 3, 2);
			glRotated(90, 1, 0, 0);
			glScaled(5, 8, 1.5);
			glut.glutSolidCube(1.0f);
		glPopMatrix();
		//chair sides
		glColor(204, 142, 53,1.0);
		glPushMatrix();
			glTranslated(3, 3, 1.5);
			glRotated(90, 0, 1, 0);
			glScaled(5.75, 6, 1);
			glut.glutSolidCube(1.0f);
		glPopMatrix();
		glPushMatrix();
			glTranslated(-3, 3, 1.5);
			glRotated(90, 0, 1, 0);
			glScaled(5.75, 6, 1);
			glut.glutSolidCube(1.0f);
		glPopMatrix();
	}
	
	public void drawPlateauChair(GLUT glut) {
		glColor(255, 177, 66, 0.5);
		//chair seat
		glPushMatrix();
			glTranslated(0, 4, 1.75);
			glRotated(90, 1, 0, 0);
			glScaled(5, 4.5, 1.5);
			glut.glutSolidCube(1.0f);
		glPopMatrix();
		//draw chair back sides
		glColor(204, 142, 53,1.0);
		glPushMatrix();
			glTranslated(-2, 5, -1);
			glScaled(1, 10, 1);
			glut.glutSolidCube(1.0f);
		glPopMatrix();
		glPushMatrix();
			glTranslated(2, 5, -1);
			glScaled(1, 10, 1);
			glut.glutSolidCube(1.0f);
		glPopMatrix();
		//draw chair back top
		glPushMatrix();
			glTranslated(0, 9.5, -1);
			glScaled(5, 1, 1);
			glut.glutSolidCube(1.0f);
		glPopMatrix();
		//chair legs front
		glPushMatrix();
			glTranslated(-2, 1.75, 3.5);
			glScaled(1, 3, 1);
			glut.glutSolidCube(1.0f);
		glPopMatrix();
		glPushMatrix();
			glTranslated(2, 1.75, 3.5);
			glScaled(1, 3, 1);
			glut.glutSolidCube(1.0f);
		glPopMatrix();
		//chair back
		glColor(230, 238, 156, 1.0);
		glPushMatrix();
			glTranslated(0, 7.375, 0);
			glScaled(5, 5.25, 1);
			glut.glutSolidCube(1.0f);
		glPopMatrix();
	}
	
	public void drawTheater(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax, double doorAngle) {
		
		GLUT glut = new GLUT();		
				
		
		
		drawOutside(xMin, yMin, zMin, xMax, yMax, zMax, doorAngle, glut);

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
		
		//inner roof
		glBegin(GL_QUADS);
			glVertex3d(xMin, yMax, zMax);
			glVertex3d(xMax, yMax, zMax);
			glVertex3d(xMax, yMax, zMin);
			glVertex3d(xMin, yMax, zMin);
		glEnd();
		
		
		//outer roof
		glColor(225, 112, 85,1.0);
		glBegin(GL_QUAD_STRIP);
			glVertex3d(xMin, yMax, zMin-(xMax-xMin)/2);
			glVertex3d(xMin, yMax, zMax+zMin/2);
			
			glVertex3d((xMax+xMin)/2, yMax*1.5, zMin-(xMax-xMin)/2);
			glVertex3d((xMax+xMin)/2, yMax*1.5, zMax+zMin/2);
			
			glVertex3d(xMax, yMax, zMin-(xMax-xMin)/2);
			glVertex3d(xMax, yMax, zMax+zMin/2);
		glEnd();		
		
		//stage curtain
		glColor(198, 40, 40, 1.0);
		glBegin(GL_QUADS);
			glVertex3d(xMin, yMax, zMax+(zMin-zMax)/4);
			glVertex3d(xMin, yMin, zMax+(zMin-zMax)/4);

			glVertex3d(xMax, yMin, zMax+(zMin-zMax)/4);
			glVertex3d(xMax, yMax, zMax+(zMin-zMax)/4);
		glEnd();
		
		//chandelier
		glPushMatrix();
			glTranslated((xMax-xMin)*1.5, yMax-Math.sqrt(3.0*3.0*3), (zMax-zMin));
			glRotated(180, 1, 0, 1);
			glColor(255, 177, 66, 1.0);
			glPushMatrix();
				glRotated(45, 1, 0, 1);
				glut.glutSolidCube(3.0f);
			glPopMatrix();
			glTranslated(0, Math.sqrt(3.0*3.0*3)-1.5, 0);
			glPushMatrix();
				glRotated(45, 1, 0, 0);
				glRotated(-45, 0, 0, 1);
				glut.glutSolidCube(3.0f);
			glPopMatrix();
			glTranslated(0, 14, 0);
			glColor(255, 255, 255, 1.0);
			glPushMatrix();
				glRotated(90, 1, 0, 0);
				glut.glutSolidCone(7, 14, 4, 2);
				glRotated(180, 0, 1, 0);
				glut.glutSolidCone(7, 14, 4, 2);
			glPopMatrix();
		glPopMatrix();
		
		//draw chairs in inner audience
		glPushMatrix();
			glTranslated((xMax+xMin)/2-16, yMin, (zMax+zMin)/2);
			for(int i = 0; i < 4; ++i) {
				glPushMatrix();				
				for(int j = 0; j < 5; ++j) {
					drawInnerChair(glut);
					glTranslated(8, 0, 0);
				}
				glPopMatrix();
				glTranslated(0, 0, -12);
			}
		glPopMatrix();

		//draw chairs in outer audience
		glPushMatrix();
			
		glPopMatrix();
		
		//stage
		glColor(121, 85, 72, 1.0);
		glPushMatrix();
			glTranslated((xMax+xMin)/2, yMin+(yMax-yMin)/20, zMax-(zMax-zMin)/6);
			glPushMatrix();
				glScaled((xMax-xMin)-1, (yMax-yMin)/10, (zMax-zMin)/3-0.1);
				glut.glutSolidCube(1.0f);
			glPopMatrix();
		glPopMatrix();
		
		//left column
		glPushMatrix();
			glTranslated(xMax-(xMax-xMin)/10, (yMin+yMax)/2, zMax-(zMax-zMin)/5);
			//left sound box
			glPushMatrix();
				glColor(0, 0, 0,1.0);
				glTranslated(-(xMax-xMin)/1.4, (yMax-yMin)/7.5+5.0-(yMin+yMax)/2, -(zMax-zMin)/5-0.3);
				glScaled((xMax-xMin)/9-0.3, (yMax-yMin)/6, (zMax-zMin)/20-0.3);
				glRotated(45, 0, 1, 0);
				glut.glutSolidCube(1.0f);
				glColor(215, 204, 200,1.0);
			glPopMatrix();
			glScaled((xMax-xMin)/5-0.3, yMax-0.3, 2*(zMax-zMin)/5-0.3);
			glut.glutSolidCube(1.0f);
		glPopMatrix();
		
		//right column
		glPushMatrix();
			glTranslated(xMin+(xMax-xMin)/10, yMin+yMax/2, zMax-2*(zMax-zMin)/10);
			//right sound box
			glPushMatrix();
				glColor(0, 0, 0,1.0);
				glTranslated((xMax-xMin)/1.4, (yMax-yMin)/7.5+5.0-(yMin+yMax)/2, -(zMax-zMin)/5-0.3);
				glScaled((xMax-xMin)/9-0.3, (yMax-yMin)/6, (zMax-zMin)/20-0.3);
				glRotated(-45, 0, 1, 0);
				glut.glutSolidCube(1.0f);
				glColor(215, 204, 200,1.0);
			glPopMatrix();
			glScaled((xMax-xMin)/5-0.3, yMax-0.3, 2*(zMax-zMin)/5-0.3);
			glut.glutSolidCube(1.0f);
		glPopMatrix();
		
		//glColor3d(0, 0, 1);
		//glColor(234, 181, 67,1.0);
		glColor(255, 236, 179,1.0);
		glRotated(90, 0, 1, 0);
		//circular part (including stands and circular part of the roof)
		glPushMatrix();
			glTranslated(-zMin, 0.0, (xMax-xMin)/2 + xMin);
			drawCircularPart((xMax - xMin)/2, xMin, xMax, yMin, yMax, zMin, zMax);
		glPopMatrix();
	}
	
}
