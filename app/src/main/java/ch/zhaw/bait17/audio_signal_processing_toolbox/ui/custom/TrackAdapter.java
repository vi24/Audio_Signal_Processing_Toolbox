package ch.zhaw.bait17.audio_signal_processing_toolbox.ui.custom;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ch.zhaw.bait17.audio_signal_processing_toolbox.R;
import ch.zhaw.bait17.audio_signal_processing_toolbox.model.Track;


/**
 * @author georgrem, stockan1
 */

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> {


    private final List<Track> tracks;
    private final ItemSelectedListener listener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView title;
        public final TextView artists;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.track_title);
            artists = (TextView) itemView.findViewById(R.id.track_artist);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemSelected(v, tracks.get(getAdapterPosition()));
        }
    }

    public interface ItemSelectedListener {
        void onItemSelected(View itemView, Track item);
    }

    public TrackAdapter(List<Track> tracks, ItemSelectedListener listener) {
        this.tracks = tracks;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Track track = tracks.get(position);
        holder.title.setText(track.getTitle());
        holder.artists.setText(track.getArtist());
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }
}