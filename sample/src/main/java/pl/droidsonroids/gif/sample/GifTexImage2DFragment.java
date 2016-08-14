package pl.droidsonroids.gif.sample;

import android.content.pm.FeatureInfo;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import pl.droidsonroids.gif.GifOptions;
import pl.droidsonroids.gif.GifTexImage2D;
import pl.droidsonroids.gif.InputSource;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;

public class GifTexImage2DFragment extends BaseFragment {

	private static final String VERTEX_SHADER_CODE =
			"attribute vec4 position;" +
					"uniform mediump mat4 projection;" +
					"uniform mediump mat4 model;" +
					"attribute vec4 coordinate;" +
					"varying vec2 textureCoordinate;" +
					"void main()" +
					"{" +
					"    gl_Position = position;" +
					"    mediump vec4 outCoordinate = projection * model * coordinate;" +
					"    textureCoordinate = vec2(outCoordinate.s, 1.0 - outCoordinate.t);" +
					"}";

	private static final String FRAGMENT_SHADER_CODE =
			"varying mediump vec2 textureCoordinate;" +
					"uniform sampler2D texture;" +
					"void main() { " +
					"    gl_FragColor = texture2D(texture, textureCoordinate);" +
					"}";

	private GifTexImage2D mGifTexImage2D;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (!isOpenGLES2Supported()) {
			Snackbar.make(container, R.string.gles2_not_supported, Snackbar.LENGTH_LONG).show();
			return null;
		}
		try {
			GifOptions options = new GifOptions();
			options.setInIsOpaque(true);
			mGifTexImage2D = new GifTexImage2D(new InputSource.ResourcesSource(getResources(), R.drawable.anim_flag_chile), options);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		final GLSurfaceView view = (GLSurfaceView) inflater.inflate(R.layout.opengl, container, false);
		view.setEGLContextClientVersion(2);
		view.setRenderer(new Renderer());
		final int size = Math.max(mGifTexImage2D.getWidth(), mGifTexImage2D.getHeight());
		view.getHolder().setFixedSize(size, size);
		mGifTexImage2D.startDecoderThread();
		return view;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		if (mGifTexImage2D != null) {
			mGifTexImage2D.recycle();
		}
	}

	private class Renderer implements GLSurfaceView.Renderer {

		private int mProjectionLocation;
		private int mModelLocation;
		final float[] mModel = new float[16];
		final float[] mProjection = new float[16];

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//			final int[] texNames = {0};
//			glGenTextures(1, texNames, 0);
//			glBindTexture(GL_TEXTURE_2D, texNames[0]);
//			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
//			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
//			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

			final int vertexShader = loadShader(GL_VERTEX_SHADER, VERTEX_SHADER_CODE);
			final int pixelShader = loadShader(GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE);
			final int program = glCreateProgram();
			glAttachShader(program, vertexShader);
			glAttachShader(program, pixelShader);
			glLinkProgram(program);
			glDeleteShader(pixelShader);
			glDeleteShader(vertexShader);
			final int positionLocation = glGetAttribLocation(program, "position");
			final int textureLocation = glGetUniformLocation(program, "texture");
			mModelLocation = glGetUniformLocation(program, "model");
			mProjectionLocation = glGetUniformLocation(program, "projection");
			final int coordinateLocation = glGetAttribLocation(program, "coordinate");
			glUseProgram(program);

			Buffer textureBuffer = createFloatBuffer(new float[]{0, 0, 1, 0, 0, 1, 1, 1});
			Buffer verticesBuffer = createFloatBuffer(new float[]{-1, -1, 1, -1, -1, 1, 1, 1});
			glVertexAttribPointer(coordinateLocation, 2, GL_FLOAT, false, 0, textureBuffer);
			glEnableVertexAttribArray(coordinateLocation);
			glUniform1i(textureLocation, 0);
			glVertexAttribPointer(positionLocation, 2, GL_FLOAT, false, 0, verticesBuffer);
			glEnableVertexAttribArray(positionLocation);
			//glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, mGifTexImage2D.getWidth(), mGifTexImage2D.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			Matrix.setIdentityM(mProjection, 0);
			Matrix.orthoM(mProjection, 0, 0, 1, 0, 1, -1, 1);

			Matrix.setIdentityM(mModel, 0);
			Matrix.translateM(mModel, 0, 0.5f, 0.5f, 0);
			Matrix.scaleM(mModel, 0, 0.5f, 0.5f, 1);

			glUniformMatrix4fv(mProjectionLocation, 1, false, mProjection, 0);
			glUniformMatrix4fv(mModelLocation, 1, false, mModel, 0);
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			mGifTexImage2D.renderFrame(GL_TEXTURE_2D, 0);
			glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
			SystemClock.sleep(300);
		}
	}

	private static int loadShader(int shaderType, String source) {
		final int shader = glCreateShader(shaderType);
		glShaderSource(shader, source);
		glCompileShader(shader);
		return shader;
	}

	private static Buffer createFloatBuffer(float[] floats) {
		return ByteBuffer
				.allocateDirect(floats.length * 4)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer()
				.put(floats)
				.rewind();
	}

	private boolean isOpenGLES2Supported() {
		FeatureInfo[] featureInfos = getContext().getPackageManager().getSystemAvailableFeatures();
		if (featureInfos != null) {
			for (FeatureInfo featureInfo : featureInfos) {
				if (featureInfo.name == null) {
					return ((featureInfo.reqGlEsVersion & 0xffff0000) >> 16) >= 2;
				}
			}
		}
		return false;
	}

}
