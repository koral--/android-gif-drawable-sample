package pl.droidsonroids.gif.sample;

import android.opengl.Matrix;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import pl.droidsonroids.gif.GifTexImage2D;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;

public class Image {

	private static final String FRAGMENT = "precision mediump float;"
			+ "varying highp vec2 textureCoordinate;"
			+ "uniform sampler2D texture;"
			+ "void main()"
			+ "{"
			+ "    gl_FragColor = texture2D(texture, textureCoordinate);"
			+ "}";
	private static final String VERTEX = "attribute vec2 position;"
			+ "attribute vec2 texCoords;"
			+ "varying vec2 textureCoordinate;"
			+ "uniform mat4 model;"
			+ "uniform mat4 projection;"
			+ "void main()"
			+ "{"
			+ "    gl_Position = projection * model * vec4(position, 0.0, 1.0);"
			+ "    textureCoordinate = texCoords;"
			+ "}";
	private static final Buffer TEXTURE_BUFFER = createFloatBuffer(
			0, 0,
			1, 0,
			0, 1,
			1, 1);
	private static final Buffer VERTICES_BUFFER = createFloatBuffer(
			-1, -1,
			1, -1,
			-1, 1,
			1, 1);
	private int mProjectionLocation;
	private int mModelLocation;
	private int mSourceHeight;
	private int mSourceWidth;

	private static int createProgram(final String vertexSource, final String fragmentSource) {
		final int vertexShader = loadShader(GL_VERTEX_SHADER, vertexSource);
		final int pixelShader = loadShader(GL_FRAGMENT_SHADER, fragmentSource);
		final int program = glCreateProgram();
		glAttachShader(program, vertexShader);
		glAttachShader(program, pixelShader);
		glLinkProgram(program);
		return program;
	}

	private static int loadShader(final int shaderType, final String source) {
		final int shader = glCreateShader(shaderType);
		glShaderSource(shader, source);
		glCompileShader(shader);
		return shader;
	}

	private static Buffer createFloatBuffer(final float... coords) {
		return ByteBuffer.allocateDirect(coords.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer()
				.put(coords)
				.rewind()
				.position(0);
	}

	public void prepare(GifTexImage2D source) {
		mSourceWidth = source.getWidth();
		mSourceHeight = source.getHeight();
		final int[] textures = new int[1];
		glGenTextures(1, textures, 0);

		glBindTexture(GL_TEXTURE_2D, textures[0]);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, mSourceWidth, mSourceHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
		final int program = createProgram(VERTEX, FRAGMENT);
		glUseProgram(program);

		final int positionLocation = glGetAttribLocation(program, "position");
		glVertexAttribPointer(positionLocation, 2, GL_FLOAT, false, 0, VERTICES_BUFFER);
		glEnableVertexAttribArray(positionLocation);

		final int texCoordsLocation = glGetAttribLocation(program, "texCoords");
		glVertexAttribPointer(texCoordsLocation, 2, GL_FLOAT, false, 0, TEXTURE_BUFFER);
		glEnableVertexAttribArray(texCoordsLocation);

		mModelLocation = glGetUniformLocation(program, "model");
		mProjectionLocation = glGetUniformLocation(program, "projection");
	}

	public void setSurfaceSize(int width, int height) {
		float scale = (float) width / mSourceWidth;
		float translateX = width / 2f;
		float translateY = height / 2f;

		final float[] model = new float[16];
		Matrix.setIdentityM(model, 0);
		Matrix.translateM(model, 0, translateX, translateY, 0);
		Matrix.scaleM(model, 0, mSourceWidth, mSourceHeight, 0);
		Matrix.scaleM(model, 0, scale, scale, 0);
		glUniformMatrix4fv(mModelLocation, 1, false, model, 0);

		final float[] projection = new float[16];
		Matrix.orthoM(projection, 0, 0, width, height, 0, -1, 1);
		glUniformMatrix4fv(mProjectionLocation, 1, false, projection, 0);
	}

}
