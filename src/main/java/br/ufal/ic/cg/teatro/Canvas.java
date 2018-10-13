package br.ufal.ic.cg.teatro;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.nio.FloatBuffer;

import javax.swing.JSlider;

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
	JSlider doorSlider;
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
		gl.glShadeModel(GL2.GL_SMOOTH);		
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		
		float ambientLight[] = { 0.6f, 0.6f, 0.6f, 0.5f };// 0.5
		float diffuseLight[] = { 0.15f, 0.15f, 0.15f, 0.5f };// 0.8
		float specularLight[] = { 0.8f, 0.8f, 0.8f, 1.0f };// 0.3
		float lightPos[] = {150.0f, 500.0f, -70.0f, 1.0f};
		
	    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, FloatBuffer.wrap(ambientLight));
	    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, FloatBuffer.wrap(diffuseLight));
	    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, FloatBuffer.wrap(specularLight));
	    gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, FloatBuffer.wrap(lightPos));
		
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_LIGHT0);
		gl.glEnable(GL2.GL_LIGHT1);
		gl.glEnable(GL2.GL_LIGHT2);
		gl.glEnable(GL2.GL_TEXTURE_2D);

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
		//System.out.println(camera);
		
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
		gl.glFlush();
		gl.glDisable(GL2.GL_TEXTURE_2D);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();
		drawable.setGL(new MyGL(gl));
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glViewport(0, 0, drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
		GLU glu = GLU.createGLU(gl);
		glu.gluPerspective(45.0, (double) drawable.getSurfaceWidth() / drawable.getSurfaceHeight(), 0.1, 10000.0);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		double fraction = 5;
		if(e.getKeyCode() == KeyEvent.VK_W) {
			camera.move(fraction, Direction.UP);
		} else if(e.getKeyCode() == KeyEvent.VK_S) {
			camera.move(fraction, Direction.DOWN);
		} else if(e.getKeyCode() == KeyEvent.VK_A) {
			camera.move(fraction, Direction.LEFT);
		} else if(e.getKeyCode() == KeyEvent.VK_D) {
			camera.move(fraction, Direction.RIGHT);
		} 
		
		else if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			camera.fly(fraction, Direction.UP);
		} else if(e.getKeyCode() == KeyEvent.VK_V) {
			camera.fly(fraction, Direction.DOWN);
		} 
		
		else if(e.getKeyCode() == KeyEvent.VK_UP) {
            camera.turn(0.05, Direction.UP);
		} else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			camera.turn(0.05, Direction.DOWN);
		} else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
            camera.turn(0.05, Direction.LEFT);
		} else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			camera.turn(0.05, Direction.RIGHT);
		}
		
		else if(e.getKeyCode() == KeyEvent.VK_R) {
			if(doorAngle+1.5 < 90.0) {
				doorAngle+=1.5;
				doorSlider.setValue((int) doorAngle*100);
			}
		} else if(e.getKeyCode() == KeyEvent.VK_E) {
			if(doorAngle-1.5 > 0.0) {
				doorAngle-=1.5;
				doorSlider.setValue((int) doorAngle*100);
			}
		}
		
		display();		
	}

	public void setDoorAngle(double newAngle) {
		doorAngle = newAngle;
		if(doorAngle > 90.0) doorAngle = 90.0;
		else if(doorAngle < 0) doorAngle = 0.0;
	}
	
	public void setDoorSlider(JSlider doorSlider) {
		this.doorSlider = doorSlider;
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
