package br.ufal.ic.cg.teatro;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.glu.gl2.GLUgl2;
import com.jogamp.opengl.util.gl2.GLUT;

public class MyGLUT extends GLUT {
	private static float[][] boxVertices;
	private static final float[][] boxNormals = {
		{-1.0f, 0.0f, 0.0f},
		{0.0f, 1.0f, 0.0f},
		{1.0f, 0.0f, 0.0f},
		{0.0f, -1.0f, 0.0f},
		{0.0f, 0.0f, 1.0f},
		{0.0f, 0.0f, -1.0f}
	};
	private static final int[][] boxFaces = {
		{0, 1, 2, 3},
		{3, 2, 6, 7},
		{7, 6, 5, 4},
		{4, 5, 1, 0},
		{5, 6, 2, 1},
		{7, 4, 0, 3}
	};
	private void drawBox(final GL2 gl, final float size, final int type, boolean singleSideTexture) {
		if (boxVertices == null) {
			final float[][] v = new float[8][];
			for (int i = 0; i < 8; i++) {
				v[i] = new float[3];
			}
			v[0][0] = v[1][0] = v[2][0] = v[3][0] = -0.5f;
			v[4][0] = v[5][0] = v[6][0] = v[7][0] =  0.5f;
			v[0][1] = v[1][1] = v[4][1] = v[5][1] = -0.5f;
			v[2][1] = v[3][1] = v[6][1] = v[7][1] =  0.5f;
			v[0][2] = v[3][2] = v[4][2] = v[7][2] = -0.5f;
			v[1][2] = v[2][2] = v[5][2] = v[6][2] =  0.5f;
			boxVertices = v;
		}
		final float[][] v = boxVertices;
		final float[][] n = boxNormals;
		final int[][] faces = boxFaces;
		for (int i = 5; i >= 0; i--) {
			gl.glBegin(type);
				gl.glNormal3fv(n[i], 0);
				
				float[] vt = v[faces[i][0]];
				if(i >= 4 || !singleSideTexture) gl.glTexCoord2f(0.0f, 0.0f);
				gl.glVertex3f(vt[0] * size, vt[1] * size, vt[2] * size);
				
				vt = v[faces[i][1]];
				if(!singleSideTexture) gl.glTexCoord2f(0.0f, 3.0f);
				else if(i >= 4) gl.glTexCoord2f(0.0f, 1.0f);
				gl.glVertex3f(vt[0] * size, vt[1] * size, vt[2] * size);
				
				vt = v[faces[i][2]];
				if(!singleSideTexture) gl.glTexCoord2f(3.0f, 3.0f);
				else if(i >= 4) gl.glTexCoord2f(1.0f, 1.0f);
				gl.glVertex3f(vt[0] * size, vt[1] * size, vt[2] * size);
				
				vt = v[faces[i][3]];
				if(!singleSideTexture) gl.glTexCoord2f(3.0f, 0.0f);
				if(i >= 4) gl.glTexCoord2f(1.0f, 0.0f);
				gl.glVertex3f(vt[0] * size, vt[1] * size, vt[2] * size);
			gl.glEnd();
		}
	}

	/*
	 * Overloaded because glutSolidCube doesn't add texture coords by default
	 */
	public void glutSolidCube(final float size) {
		drawBox(GLUgl2.getCurrentGL2(), size, GL2GL3.GL_QUADS, false);
	}
	
	/**
	 * Draws the same as glutSolidCube, but adds texture only to the front and back of the cube
	 */
	public void glutCubeFrontAndBack(final float size){
		drawBox(GLUgl2.getCurrentGL2(), size, GL2GL3.GL_QUADS, true);
	}
}
