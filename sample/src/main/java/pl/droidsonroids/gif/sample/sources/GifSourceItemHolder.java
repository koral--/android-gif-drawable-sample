package pl.droidsonroids.gif.sample.sources;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import pl.droidsonroids.gif.GifImageView;
import pl.droidsonroids.gif.sample.R;

class GifSourceItemHolder extends RecyclerView.ViewHolder {
    final GifImageView gifImageView;
    final TextView descriptionTextView;

    public GifSourceItemHolder(View itemView) {
        super(itemView);
        descriptionTextView = (TextView) itemView.findViewById(R.id.desc_tv);
        gifImageView = (GifImageView) itemView.findViewById(R.id.giv);
    }
}
