package pl.droidsonroids.gif.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Fragment with various GIF sources examples
 */
public class GifSourcesFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ListView lv = new ListView(inflater.getContext());
        lv.setAdapter(new GifSourcesAdapter(inflater));
        return lv;
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
            return mDescriptions.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = mInflater.inflate(R.layout.source_item, parent, false);
            ((TextView) convertView.findViewById(R.id.desc_tv)).setText(mDescriptions[position]);
            GifImageView gifImageView = (GifImageView) convertView.findViewById(R.id.giv);
            GifDrawable gd = null;
            try {
                switch (position) {
                    case 0: //asset
                        gd = new GifDrawable(getContext().getAssets(), "Animated-Flag-Finland.gif");
                        break;
                    case 1: //resource
                        gd = new GifDrawable(getContext().getResources(), R.drawable.anim_flag_england);
                        break;
                    case 2: //byte[]
                        break;
                    case 3: //FileDescriptor
                        break;
                    case 4: //file path
                        break;
                    case 5: //File
                        break;
                    case 6: //AssetFileDescriptor
                        break;
                    case 7: //ByteBuffer
                        break;
                    case 8: //Uri
                        //gd=new GifDrawable(getContext().getContentResolver(),Uri.parse("file:///android_asset/Animated-Flag-Delaware.gif"));
                        break;
                    default:
                        throw new IndexOutOfBoundsException("Invalid source index");
                }
                gifImageView.setImageDrawable(gd);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return convertView;
        }
    }
}