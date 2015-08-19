package pl.droidsonroids.gif.sample;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class AnimationControlFragment extends Fragment implements View.OnClickListener {

	private GifDrawable gifDrawable;
	private ToggleButton toggleButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.animation_control, container, false);
		final GifImageView gifImageView = (GifImageView) view.findViewById(R.id.gifImageView);
		view.findViewById(R.id.btn_reset).setOnClickListener(this);
		toggleButton = (ToggleButton) view.findViewById(R.id.btn_toggle);
		toggleButton.setOnClickListener(this);
		gifDrawable = (GifDrawable) gifImageView.getDrawable();

		gifDrawable.addAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationCompleted() {
				Snackbar.make(view, R.string.animation_loop_completed, Snackbar.LENGTH_SHORT).show();
			}
		});
		resetAnimation();
		return view;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_toggle) {
			toggleAnimation();
		} else {
			resetAnimation();
		}
	}

	private void resetAnimation() {
		gifDrawable.stop();
		gifDrawable.seekToFrameAndGet(5);
		toggleButton.setChecked(false);
	}

	private void toggleAnimation() {
		if (gifDrawable.isPlaying()) {
			gifDrawable.stop();
		} else {
			gifDrawable.start();
		}
	}
}
