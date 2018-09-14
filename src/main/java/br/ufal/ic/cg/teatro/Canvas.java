package br.ufal.ic.cg.teatro;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;

public class Canvas extends GLCanvas implements GLEventListener, KeyListener {

	private static final long serialVersionUID = 1L;
	
	Camera camera;
	double xAngle = 0.0;
	double yAngle = 0.0;
	double zAngle = 0.0;
	
	public Canvas(int width, int height, GLCapabilities capabilities) {
		super(capabilities);
		setSize(width, height);
		addGLEventListener(this);
		camera = new Camera(0.0, -5.0);
	}
	
	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		drawable.setGL(new MyGL(gl));
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glClearColor(0.0f, 0.7f, 1.0f, 1.0f);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		MyGL gl = new MyGL(drawable.getGL().getGL2());
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		
		gl.glLoadIdentity();
		
		camera.setLookAt(GLU.createGLU(gl));
		System.out.println(camera);
		gl.glRotated(xAngle, 1, 0, 0);
		gl.glRotated(yAngle, 0, 1, 0);
		gl.glRotated(zAngle, 0, 1, camera.getCenterZ());
		gl.drawTheater(-5.0, -0.7, -7.0, 5.0, 9.3, 7.0);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();
		drawable.setGL(new MyGL(gl));
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glViewport(0, 0, drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
		GLU glu = GLU.createGLU(gl);
		glu.gluPerspective(45.0, drawable.getSurfaceWidth() / drawable.getSurfaceHeight(), 0.1, 10000.0);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		double fraction = 5;
		if(e.getKeyCode() == KeyEvent.VK_W) {
			System.out.println("W");
			camera.moveForward(fraction);
		} else if(e.getKeyCode() == KeyEvent.VK_S) {
			System.out.println("S");
			camera.moveBackward(fraction);
		} else if(e.getKeyCode() == KeyEvent.VK_A) {
			System.out.println("A");
            camera.turnLeft(0.05);
		} else if(e.getKeyCode() == KeyEvent.VK_D) {
			System.out.println("D");
			camera.turnRight(0.05);
		} else if(e.getKeyCode() == KeyEvent.VK_X) {
			System.out.println("D");
			xAngle-=0.7;
		} else if(e.getKeyCode() == KeyEvent.VK_Y) {
			System.out.println("D");
			yAngle-=0.7;
		} else if(e.getKeyCode() == KeyEvent.VK_Z) {
			System.out.println("D");
			zAngle-=0.7;
		}
		
		display();		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
