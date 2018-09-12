package br.ufal.ic.cg.teatro;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;

public class Canvas extends GLCanvas implements GLEventListener {

	private static final long serialVersionUID = 1L;

	public Canvas(int width, int height, GLCapabilities capabilities) {
		super(capabilities);
		setSize(width, height);
		addGLEventListener(this);
	}
	
	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		drawable.setGL(new MyGL(gl));
		
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glViewport(0, 0, drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
		GLU glu = new GLU();
		glu.gluPerspective(45.0f, drawable.getSurfaceWidth() / drawable.getSurfaceHeight(), 0.1f, 100.0f);
		//gl.glOrtho(0, drawable.getSurfaceWidth(), 0, drawable.getSurfaceHeight(), -1, 1);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		MyGL gl = new MyGL(drawable.getGL().getGL2());
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
	    
	    //gl.glTranslatef( 0f, 0f, -2.5f );
        
        gl.glBegin(GL2.GL_QUADS);  // Wall
		    gl.glVertex3f(0.3f,0,1);
		    gl.glVertex3f(2,0,1);
		    gl.glVertex3f(2,-1.5f,1);
		    gl.glVertex3f(0.3f,-1.5f,1);
        gl.glEnd();
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();
		drawable.setGL(new MyGL(gl));
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glViewport(0, 0, drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
		GLU glu = new GLU();
		glu.gluPerspective(45.0f, drawable.getSurfaceWidth() / drawable.getSurfaceHeight(), 0.1f, 100.0f);
		//gl.glOrtho(0, drawable.getSurfaceWidth(), 0, drawable.getSurfaceHeight(), -1, 1);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
	}

}
