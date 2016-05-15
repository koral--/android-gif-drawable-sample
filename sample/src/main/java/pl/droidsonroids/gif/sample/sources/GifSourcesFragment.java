package pl.droidsonroids.gif.sample.sources;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifDrawableBuilder;
import pl.droidsonroids.gif.sample.BaseFragment;
import pl.droidsonroids.gif.sample.R;

/**
 * Fragment with various GIF sources examples
 */
public class GifSourcesFragment extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ListView listView = new ListView(getContext());
		listView.setAdapter(new GifListAdapter(getContext()));
		return listView;
	}

	static class GifListAdapter implements ListAdapter {

		private final GifSourcesResolver mGifSourcesResolver;

		public GifListAdapter(Context context) {
			mGifSourcesResolver = new GifSourcesResolver(context);
		}

		@Override
		public boolean areAllItemsEnabled() {
			return true;
		}

		@Override
		public boolean isEnabled(int position) {
			return true;
		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {

		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {

		}

		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.source_item, parent, false);
				convertView.setTag(new GifSourceItemHolder(convertView));
			}
			GifSourceItemHolder holder = (GifSourceItemHolder) convertView.getTag();
			final String[] descriptions = holder.itemView.getResources().getStringArray(R.array.sources);
			position %= descriptions.length;

			final GifDrawable existingOriginalDrawable = (GifDrawable) holder.gifImageViewOriginal.getDrawable();
			final GifDrawable existingSampledDrawable = (GifDrawable) holder.gifImageViewSampled.getDrawable();
			final GifDrawableBuilder builder = new GifDrawableBuilder().with(existingOriginalDrawable);
			try {
				mGifSourcesResolver.bindSource(position, builder);
				final GifDrawable fullSizeDrawable = builder.build();
				holder.gifImageViewOriginal.setImageDrawable(fullSizeDrawable);
				holder.gifImageViewOriginal.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (fullSizeDrawable.isPlaying())
							fullSizeDrawable.stop();
						else
							fullSizeDrawable.start();
					}
				});

				builder.with(existingSampledDrawable).sampleSize(3);
				mGifSourcesResolver.bindSource(position, builder);
				final GifDrawable subsampledDrawable = builder.build();
				final SpannableStringBuilder stringBuilder = new SpannableStringBuilder(descriptions[position] + '\ufffc');
				stringBuilder.setSpan(new ImageSpan(subsampledDrawable), stringBuilder.length() - 1, stringBuilder.length(), 0);
				holder.descriptionTextView.setText(stringBuilder);
				holder.gifImageViewSampled.setImageDrawable(subsampledDrawable);
				subsampledDrawable.setCallback(holder.multiCallback);
				holder.multiCallback.addView(holder.gifImageViewSampled);
				holder.multiCallback.addView(holder.descriptionTextView);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
			return convertView;
		}

		@Override
		public int getItemViewType(int position) {
			return 0;
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}
	}
}