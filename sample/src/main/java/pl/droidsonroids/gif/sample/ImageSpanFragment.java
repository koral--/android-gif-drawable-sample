package pl.droidsonroids.gif.sample;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;

public class ImageSpanFragment extends BaseFragment implements Drawable.Callback {

    private TextView tv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        tv = new TextView(getActivity());
        GifDrawable gifDrawable;
        try {
            gifDrawable = new GifDrawable(getResources(), R.drawable.anim_flag_england);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SpannableStringBuilder ssb = new SpannableStringBuilder("test");
        gifDrawable.setBounds(0, 0, gifDrawable.getIntrinsicWidth(), gifDrawable.getIntrinsicHeight());
        gifDrawable.setCallback(this);
        ssb.setSpan(new ImageSpan(gifDrawable), 1, 2, 0);
        tv.setText(ssb);
        return tv;
    }

    @Override
    public void invalidateDrawable(Drawable who) {
        tv.invalidate();
    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        tv.postDelayed(what, when);
    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {
        tv.removeCallbacks(what);
    }
}