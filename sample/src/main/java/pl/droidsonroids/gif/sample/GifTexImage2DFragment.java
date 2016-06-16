package pl.droidsonroids.gif.sample;

import android.content.pm.FeatureInfo;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import pl.droidsonroids.gif.GifOptions;
import pl.droidsonroids.gif.GifTexImage2D;
import pl.droidsonroids.gif.InputSource;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;

public class GifTexImage2DFragment extends BaseFragment {

	private static final String VERTEX_SHADER_CODE =
			"attribute vec4 position;" +
					"uniform mediump mat4 texMatrix;" +
					"attribute vec4 coordinate;" +
					"varying vec2 textureCoordinate;" +
					"void main()" +
					"{" +
					"    gl_Position = position;" +
					"    mediump vec4 outCoordinate = texMatrix * coordinate;" +
					"    textureCoordinate = vec2(1.0 - outCoordinate.s, 1.0 - outCoordinate.t);" +
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
		try {
			GifOptions options = new GifOptions();
			options.setInIsOpaque(true);
			mGifTexImage2D = new GifTexImage2D(new InputSource.ResourcesSource(getResources(), R.drawable.rot2), options);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		if (!isOpenGLES2Supported()) {
			Snackbar.make(container, R.string.gles2_not_supported, Snackbar.LENGTH_LONG).show();
			return null;
		}
		final GLSurfaceView view = (GLSurfaceView) inflater.inflate(R.layout.opengl, container, false);
		view.setEGLContextClientVersion(2);
		view.setRenderer(new Renderer());
		mGifTexImage2D.startDecoderThread();
/*		ImageView imageView = new ImageView(getContext());
		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		imageView.setImageResource(R.drawable.rot2);
		return imageView;*/
		return view;
	}

	@Override
	public void onDestroyView() {
		mGifTexImage2D.recycle();
		super.onDestroyView();
	}

	private class Renderer implements GLSurfaceView.Renderer {

		private int mTexMatrixLocation;
		private int coordinateLocation;
		final float[] transformMatrix = new float[16];

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			final int[] texNames = {0};
			glGenTextures(1, texNames, 0);
			glBindTexture(GL_TEXTURE_2D, texNames[0]);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

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
			mTexMatrixLocation = glGetUniformLocation(program, "texMatrix");
			coordinateLocation = glGetAttribLocation(program, "coordinate");
			glUseProgram(program);

			Buffer verticesBuffer = createFloatBuffer(-1, -1, 1, -1, -1, 1, 1, 1);
			glEnableVertexAttribArray(coordinateLocation);
			glUniform1i(textureLocation, 0);
			glVertexAttribPointer(positionLocation, 2, GL_FLOAT, false, 0, verticesBuffer);
			glEnableVertexAttribArray(positionLocation);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, mGifTexImage2D.getWidth(), mGifTexImage2D.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			float imageWidth = mGifTexImage2D.getWidth();
			float imageHeight = mGifTexImage2D.getHeight();
			final float factor = (imageWidth - imageHeight) / imageWidth;
			final float v;
			if (height > width) {
				v = 1f - factor * 0.5f;
			} else {
				v = 0.5f + factor * 0.5f;
			}
			Buffer textureBuffer = createFloatBuffer(
					0, 0,
					1, 0,
					0, 1,
					1, 1

			);
			glVertexAttribPointer(coordinateLocation, 2, GL_FLOAT, false, 0, textureBuffer);

			final PointF scale = new PointF(1, 1);
			final PointF translation = new PointF();
			if (imageWidth > imageHeight) {
				scale.x = imageHeight / imageWidth;
				translation.x = (1 / scale.x - 1) / 2;
			} else {
				scale.y = imageWidth / imageHeight;
				translation.y = (1 / scale.y - 1) / 2;
			}
			Matrix.setIdentityM(transformMatrix, 0);
//			Matrix.scaleM(transformMatrix, 0, scale.x, scale.y, 1);
//			Matrix.translateM(transformMatrix, 0, translation.x, translation.y, 0);
			glUniformMatrix4fv(mTexMatrixLocation, 1, false, transformMatrix, 0);
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			mGifTexImage2D.glTexSubImage2D(GL_TEXTURE_2D, 0);
			glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
		}
	}

	private static int loadShader(int shaderType, String source) {
		final int shader = glCreateShader(shaderType);
		glShaderSource(shader, source);
		glCompileShader(shader);
		return shader;
	}

	private static Buffer createFloatBuffer(float... floats) {
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
