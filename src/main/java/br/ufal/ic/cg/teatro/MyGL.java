package br.ufal.ic.cg.teatro;

import java.io.File;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Map;

import com.jogamp.opengl.DebugGL2;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class MyGL extends DebugGL2{

	public MyGL(GL2 downstream, Map<String, Texture> textures) {
		super(downstream);
		GLU glu = GLU.createGLU(this);
		IDquadric = glu.gluNewQuadric();
		glu.gluQuadricNormals(IDquadric, GLU.GLU_SMOOTH);
		glu.gluQuadricTexture(IDquadric, true);
		this.textures = textures;
	}
	
	GLUquadric IDquadric;	
	ArrayList<Point> plateauPoints = new ArrayList<>();
	ArrayList<Point> innerPlateauPoints = new ArrayList<>();
	ArrayList<Point> outerPlateauPoints = new ArrayList<>();
	ArrayList<Point> roofPoints = new ArrayList<>();
	ArrayList<Point> plateauCutPoints = new ArrayList<>();
	ArrayList<Point> innerPlateauCutPoints = new ArrayList<>();
	ArrayList<Point> outerPlateauCutPoints = new ArrayList<>();
	Texture currentTexture;
	Map<String, Texture> textures;
	
	private void drawCircularPart(double radius, double xMin, double xMax, double yMin, double yMax, double zMin, double zMax) {
		Point prevLower = new Point(0, 0, 0);
		Point prevHigher = new Point(0, 0, 0);
		double yDiff = yMax - yMin;
		double zDiff = zMax - zMin;
		double yPlateau = yDiff*2/3 + yMin;
		roofPoints.add(new Point(0.0, yMax, 0.0));
		loadTexture("inside-wall-3", true);
		boolean textureFlag;
		glBegin(GL_QUAD_STRIP);
			for(int i = -90; i <= 90; i+=10){
				double ang = (i * Math.PI/180);
				double x = Math.cos(ang)*radius;
				double z = Math.sin(ang)*radius;
				double xAux = x*2/3;
				double zAux = z*2/3;
				
				textureFlag = (Math.abs(i/10)%2 == 1); 

				//used to draw stands
				plateauPoints.add(new Point(x, yPlateau, z, ang));
				plateauPoints.add(new Point(xAux, yPlateau, zAux, ang));
				outerPlateauPoints.add(new Point(x*2.99/3, yPlateau, z*2.99/3, ang));
				outerPlateauPoints.add(new Point(x*2.99/3, yPlateau+5.0, z*2.99/3, ang));
				innerPlateauPoints.add(new Point(xAux, yPlateau, zAux, ang));
				innerPlateauPoints.add(new Point(xAux, yPlateau+5.0, zAux, ang));
				roofPoints.add(new Point(x, yMax, z, ang));
				
				if(i >= -20 && i <= 20) {
					plateauCutPoints.add(new Point(x, yPlateau, z, ang));
					plateauCutPoints.add(new Point(xAux, yPlateau, zAux, ang));
					innerPlateauCutPoints.add(new Point(xAux, yPlateau, zAux, ang));
					innerPlateauCutPoints.add(new Point(xAux, yPlateau+5.0, zAux, ang));
					outerPlateauCutPoints.add(new Point(x*2.99/3, yPlateau, z*2.99/3, ang));
					outerPlateauCutPoints.add(new Point(x*2.99/3, yPlateau+5.0, z*2.99/3, ang));
				}
				
				if(i >= -10 && i <= 20) {
					//this is for the door space
					if(i == -10) {
						setCircularTexture(textureFlag);
						setCircularNormal(ang);
						glVertex3d(prevLower.addToNewPoint(0.0, yDiff/4.0, 0.0));
						setCircularTexture(textureFlag);
						setCircularNormal(ang);
						glVertex3d(prevHigher);
					}
					
					setCircularTexture(textureFlag);
					setCircularNormal(ang);
					glVertex3d(x, yDiff/4.0 + yMin, z);
					setCircularTexture(textureFlag);
					setCircularNormal(ang);
					glVertex3d(x, yMax, z);
					
					if(i == 20) {
						setCircularTexture(textureFlag);
						setCircularNormal(ang);
						glVertex3d(x, yMin, z);
						setCircularTexture(textureFlag);
						setCircularNormal(ang);
						glVertex3d(x, yMax, z);
					}
				} else {
					//points on the circular wall
					prevLower = new Point(x, yMin, z);
					prevHigher = new Point(x, yMax, z);
					
					setCircularTexture(textureFlag);
					setCircularNormal(ang);
					glVertex3d(x, yMin, z);
					setCircularTexture(textureFlag);
					setCircularNormal(ang);
					glVertex3d(x, yMax, z);					
				}
			}
		glEnd();
		drawCircularRoof();
		loadTexture("plateau");
		drawPlateaus(yDiff, zDiff);
		unloadTexture();
		drawColumns(xMin, yMin, zMin, xMax, yMax, zMax);
	}
	
	private void drawCircularRoof() {
		glBegin(GL_TRIANGLE_FAN);
		for(Point p : roofPoints) {
			glNormal3d(0, -1, 0);
			glVertex3d(p);
		}
		glEnd();
	}
	boolean helper = true;
	private void setCircularTexture(boolean textureFlag) {
		if(textureFlag && helper) glTexCoord2d(0.0, 0.0);
		else if(textureFlag && !helper) glTexCoord2d(0.0, 50.0);
		else if(!textureFlag && helper) glTexCoord2d(5.0, 0.0);
		else if(!textureFlag && !helper) glTexCoord2d(5.0, 50.0);
		helper = !helper;
	}
	
	private void setCircularNormal(double angle) {
		glNormal3d(-Math.cos(angle), 0, -Math.sin(angle));
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
				Point normal;
				if(i == 1) {
					glTranslated(0.0, 5.0, 0.0);
					normal = new Point(0, 1, 0);
				} else normal = new Point(0, -1, 0);
				
				glPushMatrix();
					for(int j = 0; j < 3; ++j) {
						glBegin(GL_QUAD_STRIP);
							glNormal3d(normal);
							glTexCoord2d(20.0, 0.0);
							glVertex3d(plateauPoints.get(0).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
							glNormal3d(normal);
							glTexCoord2d(20.0, 4.0);
							glVertex3d(plateauPoints.get(1).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
							if(j < 2) {
								for(int k = 0; k < plateauPoints.size(); ++k/*Point p : plateauPoints*/) {
									Point p = plateauPoints.get(k);
								
									if(k%4 == 0) glTexCoord2d(0.0, 0.0);     // k == 0
									if((k+3)%4 == 0) glTexCoord2d(0.0, 4.0); // k == 1
									if((k+2)%4 == 0) glTexCoord2d(1.0, 0.0); // k == 2
									if((k+1)%4 == 0) glTexCoord2d(1.0, 4.0); // k == 3

									glNormal3d(normal);
									glVertex3d(p);
									
								}
							}
							else {
								boolean stopped = false;
								for(int k = 0; k < plateauPoints.size(); ++k/*Point p : plateauPoints*/) {
									Point p = plateauPoints.get(k);
									
									if(plateauCutPoints.contains(p) && !stopped) {
										glEnd();
										stopped = true;
									}
									else if(!plateauCutPoints.contains(p) && stopped) {
										glBegin(GL_QUAD_STRIP);
										stopped = false;
									}
									if(!stopped) {
										if(k%4 == 0) glTexCoord2d(0.0, 0.0);     // k == 0
										if((k+3)%4 == 0) glTexCoord2d(0.0, 4.0); // k == 1
										if((k+2)%4 == 0) glTexCoord2d(1.0, 0.0); // k == 2
										if((k+1)%4 == 0) glTexCoord2d(1.0, 4.0); // k == 3
										
										glNormal3d(normal);
										glVertex3d(p);					
									}
								}
							}
							glNormal3d(normal);
							glTexCoord2d(20.0, 0.0);
							glVertex3d(plateauPoints.get(plateauPoints.size()-2).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
							glNormal3d(normal);
							glTexCoord2d(20.0, 4.0);
							glVertex3d(plateauPoints.get(plateauPoints.size()-1).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
						glEnd();
						glTranslated(0, -yDiff/3.0, 0);
					}
				glPopMatrix();
			}
		glPopMatrix();
		//inner plateau filling
		glPushMatrix();
			for(int j = 0; j < 3; ++j) {
				glBegin(GL_QUAD_STRIP);
					setCircularNormal(innerPlateauPoints.get(0).angle);
					glTexCoord2d(20.0, 0.0);
					glVertex3d(innerPlateauPoints.get(0).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
					setCircularNormal(innerPlateauPoints.get(1).angle);
					glTexCoord2d(20.0, 2.0);
					glVertex3d(innerPlateauPoints.get(1).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
					if(j < 2) {
						for(int k = 0; k < innerPlateauPoints.size(); ++k/*Point p : innerPlateauPoints*/) {
							Point p = innerPlateauPoints.get(k);
							if(k%4 == 0) glTexCoord2d(0.0, 0.0);     // k == 0
							if((k+3)%4 == 0) glTexCoord2d(0.0, 2.0); // k == 1
							if((k+2)%4 == 0) glTexCoord2d(1.0, 0.0); // k == 2
							if((k+1)%4 == 0) glTexCoord2d(1.0, 2.0); // k == 3
							
							setCircularNormal(p.angle);	
							glVertex3d(p);
						}
					}
					else {
						Point lastPoint = null;
						boolean stopped = false;
						for(int k = 0; k < innerPlateauPoints.size(); ++k) {
							Point p = innerPlateauPoints.get(k);
							
							if(innerPlateauCutPoints.contains(p) && !stopped) {
								setCircularNormal(lastPoint.angle);
								glTexCoord2d(5.0, 0.0);
								glVertex3d(lastPoint.addToNewPoint(lastPoint.x/2.0, -5, lastPoint.z/2.0));
								setCircularNormal(lastPoint.angle);
								glTexCoord2d(5.0, 2.0);
								glVertex3d(lastPoint.addToNewPoint(lastPoint.x/2.0, 0, lastPoint.z/2.0));
								glEnd();
								stopped = true;
							}
							else if(!innerPlateauCutPoints.contains(p) && stopped) {
								glBegin(GL_QUAD_STRIP);
								setCircularNormal(p.angle);
								glTexCoord2d(5.0, 0.0);
								glVertex3d(p.addToNewPoint(p.x/2.0, 0, p.z/2.0));
								glTexCoord2d(5.0, 2.0);
								setCircularNormal(p.angle);
								glVertex3d(p.addToNewPoint(p.x/2.0, 5, p.z/2.0));
								stopped = false;
							}
							if(!stopped) {
								if(k%4 == 0) glTexCoord2d(0.0, 0.0);     // k == 0
								if((k+3)%4 == 0) glTexCoord2d(0.0, 2.0); // k == 1
								if((k+2)%4 == 0) glTexCoord2d(1.0, 0.0); // k == 2
								if((k+1)%4 == 0) glTexCoord2d(1.0, 2.0); // k == 3
								
								setCircularNormal(p.angle);
								glVertex3d(p);
							}
							lastPoint = p;
						}
					}
					setCircularNormal(innerPlateauPoints.get(innerPlateauPoints.size()-2).angle);
					glTexCoord2d(20.0, 0.0);
					glVertex3d(innerPlateauPoints.get(innerPlateauPoints.size()-2).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
					setCircularNormal(innerPlateauPoints.get(innerPlateauPoints.size()-1).angle);
					glTexCoord2d(20.0, 2.0);
					glVertex3d(innerPlateauPoints.get(innerPlateauPoints.size()-1).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
				glEnd();
				
				//outer plateau filling (doesn't need texturing, it just helps lighting)
				glBegin(GL_QUAD_STRIP);
					glVertex3d(outerPlateauPoints.get(0).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
					glVertex3d(outerPlateauPoints.get(1).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
					if(j < 2) {
						for(Point p : outerPlateauPoints) {
								glVertex3d(p);
						}
					}
					else {
						boolean stopped = false;
						for(Point p : outerPlateauPoints) {
							if(outerPlateauCutPoints.contains(p) && !stopped) {
								glEnd();
								stopped = true;
							}
							else if(!outerPlateauCutPoints.contains(p) && stopped) {
								glBegin(GL_QUAD_STRIP);
								stopped = false;
							}
							if(!stopped) glVertex3d(p);
						}
					}
					glVertex3d(outerPlateauPoints.get(outerPlateauPoints.size()-2).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
					glVertex3d(outerPlateauPoints.get(outerPlateauPoints.size()-1).addToNewPoint(-zDiff/1.5, 0.0, 0.0));
				glEnd();
				
				glTranslated(0, -yDiff/3.0, 0);
			}
		glPopMatrix();		
	}
	
	public void glVertex3d(Point p) {
		glVertex3d(p.x, p.y, p.z);
	}
	
	public void glNormal3d(Point p) {
		glNormal3d(p.x, p.y, p.z);
	}
	
	private void drawOutside(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax, double doorAngle, MyGLUT glut) {
		double wallWidth = 5.0;
		double doorSize = (xMax-xMin+(wallWidth+0.2)*2)/6;
		glColor(255, 205, 210, 0.5);
		loadTexture("outside-wall", true);
		//side walls
		glPushMatrix();
			glTranslated(xMin-(wallWidth+0.2)/2.0, (yMax-yMin)/2, zMin+(zMax-zMin)/2-(xMax - xMin)/4);
			glPushMatrix();
				glScaled(wallWidth, yMax-yMin, zMax-zMin+(xMax - xMin)/2+wallWidth*3+0.03);
				glut.glutSolidCube(1.0f);
			glPopMatrix();
		    
			glPushMatrix();
				glTranslated(xMax-xMin+(wallWidth+0.2), 0, 0);
				glScaled(wallWidth, yMax-yMin, zMax-zMin+(xMax - xMin)/2+wallWidth*3+0.03/*1.15*/);
				glut.glutSolidCube(1.0f);
			glPopMatrix();
		glPopMatrix();
		
		//glColor(255, 225, 230, 0.5);
		//front walls
		glPushMatrix();
			glTranslated((xMax-xMin+(wallWidth+0.2)*2)/2+xMin-wallWidth, (yMax-yMin)/2, zMin-(xMax - xMin)/2 - wallWidth);
			glPushMatrix();
				glTranslated((xMax-xMin+(wallWidth+0.2)*2)/(2*wallWidth/3-0.1), 0, wallWidth*1.15/2.0-0.4);
				glScaled((xMax-xMin+(wallWidth+0.2)*2)/(2*wallWidth/3-0.1), yMax-yMin, wallWidth*2/*1.15*/);
				glut.glutSolidCube(1.0f);
			glPopMatrix();
			
			glPushMatrix();
				glTranslated(-(xMax-xMin+(wallWidth+0.2)*2)/(2*wallWidth/3-0.1), 0, wallWidth*1.15/2.0-0.4);
				glScaled((xMax-xMin+(wallWidth+0.2)*2)/(2*wallWidth/3-0.1), yMax-yMin, wallWidth*2/*1.15*/);
				glut.glutSolidCube(1.0f);
			glPopMatrix();
			
			//glColor(255, 205, 210, 0.5);
			//glColor(225, 175, 185, 1.0);
			//center front wall
			glPushMatrix();
				glTranslated(0, (yMax-yMin)/8, wallWidth*0.5-2.5);
				glPushMatrix();
					//glScaled((xMax-xMin+(wallWidth+0.2)*2)/3, 2.5*(yMax-yMin)/4, wallWidth/* *0.5*/);
					glScaled((xMax-xMin+(wallWidth+0.2)*2)/3, 3*(yMax-yMin)/4, wallWidth/* *0.5*/);
					glut.glutSolidCube(1.0f);
				glPopMatrix();
				//roof front
				glTranslated(0, (yMax+yMin)/2.9, -0.02/*wallWidth*0.022*/);
				glRotated(45, 0, 0, 1);
				glScaled((xMax-xMin)/1.3, (yMax-yMin)/1.3, wallWidth*0.99);
				glut.glutSolidCube(1.0f);
			glPopMatrix();
			
			glColor(255, 205, 210, 0.5);
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
				//orange roof piece
				loadTexture("roof", false);
				glColor(225, 112, 85,1.0);
				glTranslated(0,0,-wallWidth*9.5);
				glRotated(45, 0, 0, 1);
				glScaled((xMax-xMin)/1.17, (yMax-yMin)/1.17, wallWidth*18);
				glut.glutSolidCube(1.0f);
				//roof pieces end here
			glPopMatrix();
			
			//doors
			glColor(215, 204, 200, 1.0);
			unloadTexture();
			glPushMatrix();
				glTranslated(-doorSize, (yMax-yMin)/8.0-(yMax-yMin)/1.97, wallWidth/2.5);
				glRotated(doorAngle, 0, 1, 0);
				glTranslated(doorSize/2, 0, 2);
				glScaled(doorSize, (yMax-yMin)*3.0/11, 2);
				glut.glutSolidCube(1.0f);
			glPopMatrix();

			glPushMatrix();
				glTranslated(doorSize, (yMax-yMin)/8.0-(yMax-yMin)/1.97, wallWidth/2.5);
				glRotated(-doorAngle, 0, 1, 0);
				glTranslated(-doorSize/2, 0, 2);
				glScaled(doorSize, (yMax-yMin)*3.0/11, 2);
				glut.glutSolidCube(1.0f);
			glPopMatrix();
		glPopMatrix();
	}
	
	private void setBulbLight(int light, float[] ambientLight, float[] diffuseLight, float[] specularLight, float[] lightPos, float[] spotDirection) {
		glLightfv(light, GL_AMBIENT, FloatBuffer.wrap(ambientLight));
		glLightfv(light, GL_DIFFUSE, FloatBuffer.wrap(diffuseLight));
		glLightfv(light, GL_SPECULAR, FloatBuffer.wrap(specularLight));
		glLightfv(light, GL_POSITION, FloatBuffer.wrap(lightPos));
		glLightf(light, GL_SPOT_CUTOFF, 180.0f);
		//if(light == GL_LIGHT3 || light == GL_LIGHT4) glLightf(light, GL_CONSTANT_ATTENUATION, 0f);
		glLightf(light, GL_CONSTANT_ATTENUATION, 0f);
		glLightf(light, GL_LINEAR_ATTENUATION, .05f);
		glLightfv(light, GL_SPOT_DIRECTION, FloatBuffer.wrap(spotDirection));
	}
	
	private void drawColumns(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
		MyGLUT glut = new MyGLUT();
		Point p, aux;
		
		float ambientLight[] = {0.1f, 0.1f, 0.1f, 0.3f};
		float diffuseLight[] = {0.05f, 0.05f, 0.05f, 0.5f};
		float specularLight[] = {0.15f, 0.15f, 0.15f, 0.0f};
		float spotDirection[] = {0.0f, -1.0f, 0.0f};
		float lightPos[] = {0.0f, 0.0f, 0.0f, 1.0f};
		
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
							glut.glutSolidSphere(1.5, 10, 10);
							glTranslated(0, -2, 0);
							if(i == 3) setBulbLight(GL_LIGHT2+j, ambientLight, diffuseLight, specularLight, lightPos, spotDirection);
							glTranslated(0, 7, 0);
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
								glut.glutSolidSphere(1.5, 10, 10);
								glTranslated(0, -2, 0);
								//if(i <= 13) setBulbLight(GL_LIGHT3, ambientLight, diffuseLight, specularLight, lightPos, spotDirection);
								//if(i >= 26) setBulbLight(GL_LIGHT4, ambientLight, diffuseLight, specularLight, lightPos, spotDirection);
								glTranslated(0, 6.5, 0);
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
							glut.glutSolidSphere(1.5, 10, 10);
							glTranslated(0, -2, 0);
							if(i == 3) setBulbLight(GL_LIGHT5+j, ambientLight, diffuseLight, specularLight, lightPos, spotDirection);
							glTranslated(0, 7, 0);
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
	
	private void drawInnerChair(MyGLUT glut) {
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
	
	public void drawPlateauChair(MyGLUT glut) {
		//loadTexture("chandelier-3");
		loadTexture("p-chair-wood");
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
		loadTexture("p-chair-2");
		//chair seat
		glColor(232, 189, 136, 0.5);
		glPushMatrix();
			glTranslated(0, 4, 1.75);
			glRotated(90, 1, 0, 0);
			glScaled(5, 4.5, 1.5);
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
	
	public void setChandelierLighting(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
		float ambientLight[] = {0.30f, 0.30f, 0.30f, 0.2f};
		float diffuseLight[] = {0.6f, 0.6f, 0.30f, 0.5f};// 0.8
		float specularLight[] = {0.15f, 0.15f, 0.15f, 1.0f};// 0.3
		float lightPos[] = {(float) (xMax-xMin)*1.5f, (float) (yMax-Math.sqrt(3.0*3.0))-10.5f, (float) (zMax-zMin), 1.0f};
		glLightfv(GL_LIGHT1, GL_AMBIENT, FloatBuffer.wrap(ambientLight));
		glLightfv(GL_LIGHT1, GL_DIFFUSE, FloatBuffer.wrap(diffuseLight));
		glLightfv(GL_LIGHT1, GL_SPECULAR, FloatBuffer.wrap(specularLight));
		glLightf(GL_LIGHT1, GL_CONSTANT_ATTENUATION, 0.1f);
		glLightf(GL_LIGHT1, GL_LINEAR_ATTENUATION, .01f);
		glLightfv(GL_LIGHT1, GL_POSITION, FloatBuffer.wrap(lightPos));
		
	}
	
	public void loadTexture(String path, Boolean direction){ // true == vertical, false == horizontal, null == both
		try {	
			unloadTexture();
			File img = new File(path);
			currentTexture = TextureIO.newTexture(img, true);
			currentTexture.bind(this);
			currentTexture.enable(this);
			glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
			glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadTexture(String name, boolean vertical) {
		if(currentTexture != null) currentTexture.disable(this);
		currentTexture = textures.get(name);
		currentTexture.bind(this);
		currentTexture.enable(this);
		glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
		if(!vertical) glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
	}
	
	public void loadTexture(String name) {
		loadTexture(name, false);
	}
	
	private void unloadTexture() {
		if(currentTexture != null) {
			currentTexture.disable(this);
			//currentTexture.destroy(this);
		}
	}
	
	public void drawTheater(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax, double doorAngle) {
		
		MyGLUT glut = new MyGLUT();
		GLU glu = GLU.createGLU(this);
		
		//global lighting
		float ambientLight[] = {0.3f, 0.3f, 0.3f, 0.5f};// 0.5
		float diffuseLight[] = {0.055f, 0.055f, 0.055f, 0.15f};// 0.8
		float specularLight[] = {0.0055f, 0.0055f, 0.0055f, 0.1f};// 0.3
		float lightPos[] = {150.0f, 500.0f, -500.0f, 1.0f};
		
		glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, FloatBuffer.wrap(ambientLight));
		glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, FloatBuffer.wrap(diffuseLight));
		glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, FloatBuffer.wrap(specularLight));
		glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, FloatBuffer.wrap(lightPos));
		
		drawOutside(xMin, yMin, zMin, xMax, yMax, zMax, doorAngle, glut);
		//glColor3d(1, 0, 0);
		glColor(255, 236, 179,1.0);
		//regular walls
		loadTexture("inside-wall-3", true);
		glBegin(GL_QUAD_STRIP);
			glTexCoord2d(0.0, 0.0);
			glNormal3d(1, 0, 0);
			glVertex3d(xMin, yMax, zMin);
			
			glTexCoord2d(0.0, 50.0);
			glNormal3d(1, 0, 0);
			glVertex3d(xMin, yMin, zMin);
			
			glTexCoord2d(50.0, 0.0);
			glNormal3d(1, 0, 0);
			glVertex3d(xMin, yMax, zMax);
			
			glTexCoord2d(50.0, 50.0);
			glNormal3d(1, 0, 0);
			glVertex3d(xMin, yMin, zMax);

			
			glTexCoord2d(0.0, 0.0);
			glNormal3d(-1, 0, 0);
			glVertex3d(xMax, yMax, zMax);
			
			glTexCoord2d(0.0, 50.0);
			glNormal3d(-1, 0, 0);
			glVertex3d(xMax, yMin, zMax);
			
			glTexCoord2d(50.0, 0.0);
			glNormal3d(-1, 0, 0);
			glVertex3d(xMax, yMax, zMin);
			
			glTexCoord2d(50.0, 50.0);
			glNormal3d(-1, 0, 0);
			glVertex3d(xMax, yMin, zMin);
		glEnd();		
		
		//inner roof
		glBegin(GL_QUADS);
			glNormal3d(0, -1, 0);
			glVertex3d(xMin, yMax, zMax);
			glNormal3d(0, -1, 0);
			glVertex3d(xMax, yMax, zMax);
			glNormal3d(0, -1, 0);
			glVertex3d(xMax, yMax, zMin);
			glNormal3d(0, -1, 0);
			glVertex3d(xMin, yMax, zMin);
		glEnd();
		
		loadTexture("roof-2", true); //couldn't find roof pictures for texture
		//outer roof
		glColor(225, 112, 85,1.0);
		glBegin(GL_QUAD_STRIP);
			glTexCoord2d(1.0, 1.0);
			glNormal3d(1.0/Math.sqrt(2.0), 1.0/Math.sqrt(2.0), 0.0); 
			glVertex3d(xMin-0.5, yMax-0.01, zMin-(xMax-xMin)/2-3);
			
			glTexCoord2d(1.0, 0.0);
			glNormal3d(1.0/Math.sqrt(2.0), 1.0/Math.sqrt(2.0), 0.0);
			glVertex3d(xMin-0.5, yMax-0.01, zMax+zMin/2);
			
			glTexCoord2d(0.0, 1.0);
			glNormal3d(0.0, 1.0, 0.0);
			glVertex3d((xMax+xMin)/2, yMax*1.5, zMin-(xMax-xMin)/2-3);
			
			glTexCoord2d(0.0, 0.0);
			glNormal3d(0.0, 1.0, 0.0);
			glVertex3d((xMax+xMin)/2, yMax*1.5, zMax+zMin/2);
			
			glTexCoord2d(1.0, 1.0);
			glNormal3d(-1.0/Math.sqrt(2.0), 1.0/Math.sqrt(2.0), 0.0);
			glVertex3d(xMax+0.5, yMax-0.01, zMin-(xMax-xMin)/2-3);
			
			glTexCoord2d(1.0, 0.0);
			glNormal3d(-1.0/Math.sqrt(2.0), 1.0/Math.sqrt(2.0), 0.0);
			glVertex3d(xMax+0.5, yMax-0.01, zMax+zMin/2);
		glEnd();		
		unloadTexture();
		
		//stage curtain
		glColor(198, 40, 40, 1.0);
		loadTexture("curtain", true);
		glBegin(GL_QUADS);
			glNormal3d(0, 0, -1);
			glTexCoord2d(0.0, 6.0);
			glVertex3d(xMin, yMax, zMax+(zMin-zMax)/4);
			glNormal3d(0, 0, -1);
			glTexCoord2d(0.0, 0.0);
			glVertex3d(xMin, yMin+(yMax-yMin)/10+0.02, zMax+(zMin-zMax)/4);
			glNormal3d(0, 0, -1);
			glTexCoord2d(3.0, 0.0);
			glVertex3d(xMax, yMin+(yMax-yMin)/10+0.02, zMax+(zMin-zMax)/4);
			glNormal3d(0, 0, -1);
			glTexCoord2d(3.0, 6.0);
			glVertex3d(xMax, yMax, zMax+(zMin-zMax)/4);
		glEnd();
		unloadTexture();
		//chandelier
		glPushMatrix();
			glTranslated((xMax-xMin)*1.5, yMax-Math.sqrt(3.0*3.0), (zMax-zMin));
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
			glColor(253, 236, 206, 0.7);
			glPushMatrix();
				//loadTexture("chandelier-3");
				glRotated(90, 1, 0, 0);
				//glut.glutSolidCone(7, 14, 4, 2);
				glu.gluCylinder(IDquadric,7.0f,0.0f,14.0f,8,1);//(IDq, base, top, height, slices, stacks);
				glRotated(180, 0, 1, 0);
				//glut.glutSolidCone(7, 14, 4, 2);
				glu.gluCylinder(IDquadric,7.0f,0.0f,14.0f,8,1);
			glPopMatrix();
		glPopMatrix();

		setChandelierLighting(xMin, yMin, zMin, xMax, yMax, zMax);
		
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
		
		//stage
		loadTexture("stage");
		glColor(121, 85, 72, 1.0);
		glPushMatrix();
			glTranslated((xMax+xMin)/2, yMin+(yMax-yMin)/20, zMax-(zMax-zMin)/6);
			glPushMatrix();
				glScaled((xMax-xMin)-1, (yMax-yMin)/10, (zMax-zMin)/3-0.1);
				glut.glutSolidCube(1.0f);
			glPopMatrix();
		glPopMatrix();
		unloadTexture();
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
		glColor(255, 236, 179,0.1);
		glRotated(90, 0, 1, 0);
		//circular part (including stands and circular part of the roof)
		glPushMatrix();
			glTranslated(-zMin, 0.0, (xMax-xMin)/2 + xMin);
			drawCircularPart((xMax - xMin)/2, xMin, xMax, yMin, yMax, zMin, zMax);
		glPopMatrix();
	}
	
}
