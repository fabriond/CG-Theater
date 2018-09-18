package br.ufal.ic.cg.teatro;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;

public class App {
    
	public static void main(String[] args) {
		int width = 1200;
		int height = 1000;
		
		GLCapabilities capabilities = new GLCapabilities(GLProfile.getDefault());
		Canvas canvas = new Canvas(width, height, capabilities);
		JFrame jframe = new JFrame("Teatro Deodoro");
		JPanel jpanel = new JPanel();
		JSlider doorSlider = new JSlider(JSlider.HORIZONTAL, 0, 90, 0);
		doorSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				canvas.setDoorAngle(doorSlider.getValue()/100.0);
				canvas.display();
			}
		});
		doorSlider.setMinimum(0);
		doorSlider.setMajorTickSpacing(1500);
		doorSlider.setMaximum(9000);
		doorSlider.setPaintTicks(true);
		doorSlider.setFocusable(false);
		canvas.setDoorSlider(doorSlider);
		jpanel.add(doorSlider);
		jframe.add(jpanel);
		jframe.pack();
		jframe.setLocation(0, 0);
		jframe.addKeyListener(canvas);
		jframe.getContentPane().add(canvas);
        jframe.setSize(width, height);
        jframe.setVisible(true);
        jframe.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowevent) {
                jframe.dispose();
                System.exit(0);
            }
        });
    }
	
}
