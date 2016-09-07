package pl.droidsonroids.gif.sample;

import android.content.pm.FeatureInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import pl.droidsonroids.gif.GifOptions;
import pl.droidsonroids.gif.GifTexImage2D;
import pl.droidsonroids.gif.InputSource;

import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glDrawArrays;

public class GifTexImage2DFragment extends BaseFragment {

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

		private final Image mImage = new Image();

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			mImage.prepare(mGifTexImage2D);
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			mImage.setSurfaceSize(width, height);
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			mGifTexImage2D.glTexSubImage2D(GL_TEXTURE_2D, 0);
			glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
		}
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
