package br.ufal.ic.cg.teatro;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JSlider;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import br.ufal.ic.cg.teatro.Camera.Direction;

public class Canvas extends GLCanvas implements GLEventListener, KeyListener {

	private static final long serialVersionUID = 1L;
	
	Camera camera;
	JSlider doorSlider;
	double doorAngle = 0.0;
	boolean lightsOn = true;
	Map<String, Texture> textures = new HashMap<>();
	
	public Canvas(int width, int height, GLCapabilities capabilities) {
		super(capabilities);
		setSize(width, height);
		addGLEventListener(this);
		addKeyListener(this);
		camera = new Camera(150.0, 10.0, -100.0);
	}
	
	public void loadTexture(String file){
		try {	
			File img = new File("textures/"+file);
			textures.put(file.split("\\.")[0], TextureIO.newTexture(img, true));
		} catch (Exception e) {
			System.err.println("Texture file not found: \""+file+"\", please make sure all texture files loaded in the init function"
							 + " are in the textures folder inside the project's folder");
			System.exit(0);
		}
	}
	
	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		
		loadTexture("curtain.png");
		loadTexture("door.png");
		loadTexture("inside-roof.png");
		loadTexture("inside-wall.png");
		loadTexture("outside-wall.png");
		loadTexture("chair.png");
		loadTexture("p-chair.png");
		loadTexture("p-chair-wood.png");
		loadTexture("floor.png");
		loadTexture("roof.png");
		loadTexture("soundbox.png");
		loadTexture("stage.png");
		loadTexture("stage-sidewall.png");		
		
		drawable.setGL(new MyGL(gl, textures));
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_LIGHT0); // sunlight
		gl.glEnable(GL2.GL_TEXTURE_2D);

		gl.glClearColor(0.0f, 0.7f, 1.0f, 1.0f);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
	}
	
	private void updateLights(MyGL gl) {
		if(lightsOn) {
			gl.glEnable(GL2.GL_LIGHT1); // chandelier light
			gl.glEnable(GL2.GL_LIGHT2); // plateau lights start here
			gl.glEnable(GL2.GL_LIGHT3);
			gl.glEnable(GL2.GL_LIGHT4);
			gl.glEnable(GL2.GL_LIGHT5); // plateau lights end here
		} else {
			gl.glDisable(GL2.GL_LIGHT1); // chandelier light
			gl.glDisable(GL2.GL_LIGHT2); // plateau lights start here
			gl.glDisable(GL2.GL_LIGHT3);
			gl.glDisable(GL2.GL_LIGHT4);
			gl.glDisable(GL2.GL_LIGHT5); // plateau lights end here
		}
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		MyGL gl = new MyGL(drawable.getGL().getGL2(), textures);
		updateLights(gl);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		
		camera.setLookAt(GLU.createGLU(gl));
		
		//draw Ground
		gl.glColor(25, 42, 86, 1.0);
		gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3d(-1000.0, 0.0, -1000.0);
			gl.glVertex3d(1000.0, 0.0, -1000.0);
			gl.glVertex3d(1000.0, 0.0, 1000.0);
			gl.glVertex3d(-1000.0, 0.0, 1000.0);
		gl.glEnd();
		
		gl.drawTheater(doorAngle);
		gl.glFlush();
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();
		drawable.setGL(new MyGL(gl, textures));
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
		
		else if(e.getKeyCode() == KeyEvent.VK_T) {
			lightsOn = !lightsOn;
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
