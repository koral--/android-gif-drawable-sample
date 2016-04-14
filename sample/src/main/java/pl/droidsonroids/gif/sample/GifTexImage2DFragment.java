package pl.droidsonroids.gif.sample;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import pl.droidsonroids.gif.GifTexImage2D;
import pl.droidsonroids.gif.InputSource;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;

public class GifTexImage2DFragment extends BaseFragment {

	private static final String VERTEX_SHADER_CODE =
			"attribute vec4 position;" +
					"attribute vec4 coordinate;" +
					"varying vec2 textureCoordinate;" +
					"void main()" +
					"{" +
					"    gl_Position = position;" +
					"    textureCoordinate = vec2(coordinate.s, 1.0 - coordinate.t);" +
					"}";

	private static final String FRAGMENT_SHADER_CODE =
			"varying vec2 textureCoordinate;" +
					"uniform sampler2D texture;" +
					"void main() { " +
					"    gl_FragColor = texture2D(texture, textureCoordinate);" +
					"}";

	private GifTexImage2D mGifTexImage2D;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//TODO check if opengles 2 is supported
		final GLSurfaceView view = (GLSurfaceView) inflater.inflate(R.layout.opengl, container, false);
		view.setEGLContextClientVersion(2);
		view.setRenderer(new Renderer());
		try {
			mGifTexImage2D = new GifTexImage2D(new InputSource.ResourcesSource(getResources(), R.drawable.anim_flag_chile));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		mGifTexImage2D.startDecoderThread();
		return view;
	}

	@Override
	public void onDestroyView() {
		mGifTexImage2D.recycle();
		super.onDestroyView();
	}

	class Renderer implements GLSurfaceView.Renderer {

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			int[] texNames = {0};
			glGenTextures(1, texNames, 0);
			glBindTexture(GL_TEXTURE_2D, texNames[0]);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

			final int vertexShader = loadShader(GL_VERTEX_SHADER, VERTEX_SHADER_CODE);
			final int pixelShader = loadShader(GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE);
			final int program = glCreateProgram();
			glAttachShader(program, vertexShader);
			glAttachShader(program, pixelShader);
			glLinkProgram(program);
			int position = glGetAttribLocation(program, "position");
			int texture = glGetUniformLocation(program, "texture");
			int coordinate = glGetAttribLocation(program, "coordinate");
			glUseProgram(program);

			FloatBuffer textureBuffer = createFloatBuffer(new float[]{0, 0, 1, 0, 0, 1, 1, 1});
			FloatBuffer verticesBuffer = createFloatBuffer(new float[]{-1, -1, 1, -1, -1, 1, 1, 1});
			glVertexAttribPointer(coordinate, 2, GL_FLOAT, false, 0, textureBuffer);
			glEnableVertexAttribArray(coordinate);
			glUniform1i(texture, 0);
			glVertexAttribPointer(position, 2, GL_FLOAT, false, 0, verticesBuffer);
			glEnableVertexAttribArray(position);
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {

		}

		@Override
		public void onDrawFrame(GL10 gl) {
			mGifTexImage2D.glTexImage2D();
			glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
		}
	}

	static int loadShader(int shaderType, String source) {
		int shader = glCreateShader(shaderType);
		glShaderSource(shader, source);
		glCompileShader(shader);
		return shader;
	}

	static FloatBuffer createFloatBuffer(float[] floats) {
		FloatBuffer fb = ByteBuffer
				.allocateDirect(floats.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer();
		fb.put(floats);
		fb.rewind();
		return fb;
	}
}
