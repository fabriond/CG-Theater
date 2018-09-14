package br.ufal.ic.cg.teatro;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;

public class App {
    
	public static void main(String[] args) {
		int width = 800;
		int height = 800;
		
		GLCapabilities capabilities = new GLCapabilities(GLProfile.getDefault());
		Canvas canvas = new Canvas(width, height, capabilities);
		JFrame jframe = new JFrame("Teatro Deodoro");
		jframe.pack();
		jframe.setLocation(0, 0);
		//jframe.setExtendedState(JFrame.MAXIMIZED_BOTH);
		jframe.addKeyListener(canvas);
		jframe.getContentPane().add(canvas);
        jframe.setSize(width, height);//GLUT_DEPTH | GLUT_DOUBLE | GLUT_RGBA
        jframe.setVisible(true);
        jframe.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowevent) {
                jframe.dispose();
                System.exit(0);
            }
        });
    }
	
}
