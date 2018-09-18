package br.ufal.ic.cg.teatro;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;

import br.ufal.ic.cg.teatro.Camera.Direction;

public class Canvas extends GLCanvas implements GLEventListener, KeyListener {

	private static final long serialVersionUID = 1L;
	
	Camera camera;
	double xAngle = 0.0;
	double yAngle = 0.0;
	double zAngle = 0.0;
	double doorAngle = 0.0;
	
	public Canvas(int width, int height, GLCapabilities capabilities) {
		super(capabilities);
		setSize(width, height);
		addGLEventListener(this);
		addKeyListener(this);
		camera = new Camera(150.0, 10.0, -100.0);
		//camera = new Camera(0,10,-100);
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
		
		//draw Ground
		//gl.glColor(51, 217, 178, 1.0);
		gl.glColor(25, 42, 86, 1.0);
		gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3d(-1000.0, 0.0, -1000.0);
			gl.glVertex3d(1000.0, 0.0, -1000.0);
			gl.glVertex3d(1000.0, 0.0, 1000.0);
			gl.glVertex3d(-1000.0, 0.0, 1000.0);
		gl.glEnd();
		gl.drawTheater(100.0, 0.0, 100.0, 200.0, 100.0, 240.0, doorAngle);
		//gl.drawTheater(-5.0, -0.7, -7.0, 5.0, 9.3, 7.0);
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
			camera.move(fraction, Direction.UP);
		} else if(e.getKeyCode() == KeyEvent.VK_S) {
			System.out.println("S");
			camera.move(fraction, Direction.DOWN);
		} else if(e.getKeyCode() == KeyEvent.VK_A) {
			System.out.println("A");
			camera.move(fraction, Direction.LEFT);
		} else if(e.getKeyCode() == KeyEvent.VK_D) {
			System.out.println("D");
			camera.move(fraction, Direction.RIGHT);
		} 
		
		else if(e.getKeyCode() == KeyEvent.VK_UP) {
			System.out.println("^");
            camera.turn(0.05, Direction.UP);
		} else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			System.out.println("v");
			camera.turn(0.05, Direction.DOWN);
		} else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			System.out.println("<");
            camera.turn(0.05, Direction.LEFT);
		} else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			System.out.println(">");
			camera.turn(0.05, Direction.RIGHT);
		}
		
		else if(e.getKeyCode() == KeyEvent.VK_R) {
			System.out.println("R");
			System.out.println(Math.toDegrees(doorAngle));
			if(Math.toDegrees(doorAngle+1.7) < 5000)
				doorAngle+=1.7;
			
		} else if(e.getKeyCode() == KeyEvent.VK_E) {
			System.out.println("E");
			if(Math.toDegrees(doorAngle-1.7) > 390)
				doorAngle-=1.7;
		}
		
		else if(e.getKeyCode() == KeyEvent.VK_X) {
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
