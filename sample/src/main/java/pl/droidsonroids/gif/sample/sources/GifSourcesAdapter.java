package pl.droidsonroids.gif.sample.sources;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifDrawableBuilder;
import pl.droidsonroids.gif.sample.R;

class GifSourcesAdapter extends RecyclerView.Adapter<GifSourceItemHolder> {

    private final GifSourcesResolver mGifSourcesResolver;
    public GifSourcesAdapter(final Context context) {
        mGifSourcesResolver = new GifSourcesResolver(context);
    }

    @Override
    public GifSourceItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.source_item, parent, false);
        return new GifSourceItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final GifSourceItemHolder holder, int position) {
        final String[] descriptions = holder.itemView.getResources().getStringArray(R.array.sources);
        position %= descriptions.length;
        holder.descriptionTextView.setText(descriptions[position]);

        final GifDrawable existingDrawable = (GifDrawable) holder.gifImageView.getDrawable();
        final GifDrawableBuilder builder = new GifDrawableBuilder().with(existingDrawable);
        try {
            mGifSourcesResolver.bindSource(position, builder);
            holder.gifImageView.setImageDrawable(builder.build());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

}
