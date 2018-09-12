package br.ufal.ic.cg.teatro;

import com.jogamp.opengl.DebugGL2;
import com.jogamp.opengl.GL2;

public class MyGL extends DebugGL2{

	public MyGL(GL2 downstream) {
		super(downstream);
	}
	
	public void drawCircularWall(double x, double y, double radius) {
		int aux = 0;
		int lineAmount = 100;
		
		glBegin(GL_QUAD_STRIP);
			for(int i = 0; i <= lineAmount;i++) {
				glVertex3d(x + (radius * Math.cos(i *  Math.PI / lineAmount)), y + (radius* Math.sin(i * Math.PI / lineAmount)), aux);
				if(aux == 0) aux += 10;
				else aux -= 10;
				glVertex3d(x + (radius * Math.cos(i *  Math.PI / lineAmount)), y + (radius* Math.sin(i * Math.PI / lineAmount)), aux);
			}
		glEnd();
	}

	public void drawWall(double xMin, double zMin, double xMax, double zMax) {
		glBegin(GL_QUADS);
			glVertex3d(xMin, 0, zMin);
			glVertex3d(xMin, 0, zMax);
			glVertex3d(xMax, 0, zMax);
			glVertex3d(xMax, 0, zMin);
		glEnd();			
	}
	
}
