package pl.droidsonroids.gif.sample;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.droidsonroids.gif.GifTextureView;

public class GifTextureFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.texture, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            final GifTextureView gifTextureView = (GifTextureView) view.findViewById(R.id.gifTextureView);
            if (!gifTextureView.isHardwareAccelerated()) {
                view.findViewById(R.id.text_textureview_stub).setVisibility(View.VISIBLE);
            }
        }
        return view;
    }
}
