package pl.droidsonroids.gif.sample;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

import pl.droidsonroids.gif.GifTextureView;
import pl.droidsonroids.gif.InputSource;

public class HttpFragment extends Fragment implements Callback, View.OnClickListener {

    private static final String URL = "https://raw.githubusercontent.com/koral--/android-gif-drawable-sample/cb2d1f42b3045b2790a886d1574d3e74281de743/sample/src/main/assets/Animated-Flag-Hungary.gif";
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private GifTextureView mGifTextureView;
    private Snackbar mSnackbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            Snackbar.make(container, R.string.gif_texture_view_stub_api_level, Snackbar.LENGTH_INDEFINITE).show();
            return null;
        } else {
            mGifTextureView = (GifTextureView) inflater.inflate(R.layout.http, container, false);
            loadGif();
            return mGifTextureView;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && !mGifTextureView.isHardwareAccelerated()) {
            Snackbar.make(mGifTextureView, R.string.gif_texture_view_stub_acceleration, Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    private void loadGif() {
        final Call call = mOkHttpClient.newCall(new Request.Builder().url(URL).build());
        call.enqueue(this);
    }

    @Override
    public void onFailure(Request request, IOException e) {
        mGifTextureView.setOnClickListener(this);
        mSnackbar = Snackbar.make(mGifTextureView, getString(R.string.gif_texture_view_loading_failed, e.getMessage()), Snackbar.LENGTH_INDEFINITE);
        mSnackbar.show();
    }

    @Override
    public void onResponse(Response response) throws IOException {
        if (!response.isSuccessful()) {
            onFailure(response.request(), new IOException(response.message()));
            return;
        }
        final ResponseBody body = response.body();
        mGifTextureView.setInputSource(new InputSource.ByteArraySource(body.bytes()));
        mGifTextureView.setOnClickListener(null);
    }

    @Override
    public void onClick(View v) {
        loadGif();
        mSnackbar.dismiss();
    }
}
