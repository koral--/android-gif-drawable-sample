package pl.droidsonroids.gif.sample;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import pl.droidsonroids.gif.GifTextView;
import pl.droidsonroids.gif.GifTextureView;
import pl.droidsonroids.gif.InputSource;

/**
 * Fragment with various GIF sources examples
 */
public class GifSourcesFragment extends ListFragment {

    private File mFileForUri, mFile;
    private String mFilePath;
    private byte[] mByteArray;
    private ByteBuffer mByteBuffer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //prepare input sources
        mFileForUri = getFileFromAssets("Animated-Flag-Uruguay.gif");
        mFile = getFileFromAssets("Animated-Flag-Virgin_Islands.gif");
        mFilePath = getFileFromAssets("Animated-Flag-Estonia.gif").getPath();
        mByteArray = getBytesFromAssets("Animated-Flag-France.gif");
        byte[] gifBytes = getBytesFromAssets("Animated-Flag-Georgia.gif");
        mByteBuffer = ByteBuffer.allocateDirect(gifBytes.length);
        mByteBuffer.put(gifBytes);
    }

    private byte[] getBytesFromAssets(String filename) {
        try {
            final AssetFileDescriptor assetFileDescriptor = getResources().getAssets().openFd(filename);
            FileInputStream input = assetFileDescriptor.createInputStream();
            byte[] buf = new byte[(int) assetFileDescriptor.getDeclaredLength()];
            input.read(buf);
            input.close();
            return buf;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private File getFileFromAssets(String filename) {
        try {
            File file = new File(getActivity().getCacheDir(), filename);
            final AssetFileDescriptor assetFileDescriptor = getResources().getAssets().openFd(filename);
            FileInputStream input = assetFileDescriptor.createInputStream();
            FileOutputStream output = new FileOutputStream(file);
            byte[] buf = new byte[(int) assetFileDescriptor.getDeclaredLength()];
            int bytesRead = input.read(buf);
            input.close();
            if (bytesRead != buf.length) {
                throw new RuntimeException("Asset read failed");
            }
            output.write(buf, 0, bytesRead);
            output.close();
            return file;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setListAdapter(new GifSourcesAdapter(inflater));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    class GifSourcesAdapter extends ArrayAdapter<String> {
        private final LayoutInflater mInflater;
        private final String[] mDescriptions;

        public GifSourcesAdapter(LayoutInflater inflater) {
            super(inflater.getContext(), View.NO_ID);
            mInflater = inflater;
            mDescriptions = getResources().getStringArray(R.array.sources);
        }

        @Override
        public int getCount() {
            return mDescriptions.length*1000;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = new GifTextureView(parent.getContext());
                convertView.setLayoutParams(new ViewGroup.LayoutParams(600, 600));
            }
            ((GifTextureView)convertView).setInputSource(new InputSource.ResourcesSource(getResources(), R.drawable.ef));
//            ((TextView) convertView.findViewById(R.id.desc_tv)).setText(mDescriptions[position]);
//            GifImageView gifImageView = (GifImageView) convertView.findViewById(R.id.giv);
//            GifDrawable gd;
//            try {
//                switch (position) {
//                    case 0: //asset
//                        gd = new GifDrawable(getContext().getAssets(), "Animated-Flag-Finland.gif");
//                        break;
//                    case 1: //resource
//                        gd = new GifDrawable(getContext().getResources(), R.drawable.anim_flag_england);
//                        break;
//                    case 2: //byte[]
//                        gd = new GifDrawable(mByteArray);
//                        break;
//                    case 3: //FileDescriptor
//                        gd = new GifDrawable(getContext().getAssets().openFd("Animated-Flag-Greece.gif"));
//                        break;
//                    case 4: //file path
//                        gd = new GifDrawable(mFilePath);
//                        break;
//                    case 5: //File
//                        gd = new GifDrawable(mFile);
//                        break;
//                    case 6: //AssetFileDescriptor
//                        gd = new GifDrawable(getContext().getResources().openRawResourceFd(R.raw.anim_flag_hungary));
//                        break;
//                    case 7: //ByteBuffer
//                        gd = new GifDrawable(mByteBuffer);
//                        break;
//                    case 8: //Uri
//                        gd = new GifDrawable(getContext().getContentResolver(), Uri.parse("file:///" + mFileForUri.getAbsolutePath()));
//                        break;
//                    case 9: //InputStream
//                        gd = new GifDrawable(getContext().getResources().getAssets().open("Animated-Flag-Delaware.gif", AssetManager.ACCESS_RANDOM));
//                        break;
//                    default:
//                        throw new IndexOutOfBoundsException("Invalid source index");
//                }
//                gifImageView.setImageDrawable(gd);
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            }
            return convertView;
        }
    }
}