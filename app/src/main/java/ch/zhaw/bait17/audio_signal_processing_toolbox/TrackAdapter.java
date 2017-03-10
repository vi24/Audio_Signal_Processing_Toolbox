package ch.zhaw.bait17.audio_signal_processing_toolbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ch.zhaw.bait17.audio_signal_processing_toolbox.model.Track;

public class TrackAdapter extends BaseAdapter {

    private ArrayList<Track> tracks;
    private LayoutInflater inflater;

    public TrackAdapter(Context context, ArrayList<Track> tracks) {
        this.tracks = tracks;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return tracks.size();
    }

    @Override
    public Object getItem(int position) {
        return tracks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.media_list_item, parent, false);
        TextView title = (TextView) layout.findViewById(R.id.song_title);
        TextView artist = (TextView) layout.findViewById(R.id.song_artist);
        Track track = tracks.get(position);
        title.setText(track.getTitle());
        artist.setText(track.getArtist());
        return layout;
    }

}