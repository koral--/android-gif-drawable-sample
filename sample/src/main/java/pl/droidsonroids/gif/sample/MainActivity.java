package pl.droidsonroids.gif.sample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Main activity, hosts the pager
 *
 * @author koral--
 */
public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((ViewPager) findViewById(R.id.main_pager)).setAdapter(new MainPagerAdapter(this));
    }

    static float x = 0.5f;

    public void test(View view) {
        GifImageView tv = (GifImageView) view;
        GifDrawable gifDrawable = (GifDrawable) tv.getDrawable();
        Bitmap currentFrame = gifDrawable.getCurrentFrame();
        Bitmap scaledFrame = Bitmap.createScaledBitmap(currentFrame, 800, 600, true);
        tv.setImageDrawable(new BitmapDrawable(getResources(), currentFrame));
    }

    static class MainPagerAdapter extends FragmentStatePagerAdapter {
        private final String[] mPageTitles;

        public MainPagerAdapter(FragmentActivity act) {
            super(act.getSupportFragmentManager());
            mPageTitles = act.getResources().getStringArray(R.array.pages);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new GifSourcesFragment();
                case 1:
                    return new GifTextViewFragment();
                case 2:
                    return new GifTextureFragment();
                case 3:
                    return new AboutFragment();
                default:
                    throw new IndexOutOfBoundsException("Invalid page index");
            }
        }

        @Override
        public int getCount() {
            return mPageTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mPageTitles[position];
        }
    }

}
