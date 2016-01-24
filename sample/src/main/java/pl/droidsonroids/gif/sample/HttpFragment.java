package pl.droidsonroids.gif.sample;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import pl.droidsonroids.gif.GifTextureView;
import pl.droidsonroids.gif.InputSource;

public class HttpFragment extends BaseFragment implements View.OnClickListener {

    private static final String URL = "https://raw.githubusercontent.com/koral--/android-gif-drawable-sample/cb2d1f42b3045b2790a886d1574d3e74281de743/sample/src/main/assets/Animated-Flag-Hungary.gif";
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private GifTextureView mGifTextureView;
    private Snackbar mSnackbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (container != null) {
                Snackbar.make(container, R.string.gif_texture_view_stub_api_level, Snackbar.LENGTH_INDEFINITE).show();
            }
            return null;
        } else {
            mGifTextureView = (GifTextureView) inflater.inflate(R.layout.http, container, false);
            loadGif();
            return mGifTextureView;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSnackbar != null) {
            mSnackbar.dismiss();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && !mGifTextureView.isHardwareAccelerated()) {
            Snackbar.make(mGifTextureView, R.string.gif_texture_view_stub_acceleration, Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void loadGif() {
        final Call call = mOkHttpClient.newCall(new Request.Builder().url(URL).build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                if (isDetached()) {
                    return;
                }
                mGifTextureView.setOnClickListener(HttpFragment.this);
                final String message = getString(R.string.gif_texture_view_loading_failed, e.getMessage());
                mSnackbar = Snackbar.make(mGifTextureView, message, Snackbar.LENGTH_INDEFINITE);
                mSnackbar.show();
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                if (isDetached()) {
                    return;
                }
                if (!response.isSuccessful()) {
                    onFailure(call, new IOException(response.message()));
                    return;
                }
                final ResponseBody body = response.body();
                mGifTextureView.setInputSource(new InputSource.ByteArraySource(body.bytes()));
                mGifTextureView.setOnClickListener(null);
            }
        });
    }

    @Override
    public void onClick(View v) {
        loadGif();
        mSnackbar.dismiss();
    }
}
