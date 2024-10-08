package com.example.beatflow;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beatflow.Data.Song;

import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private List<Song> songs;
    private OnSongLongClickListener longClickListener;

    public SongAdapter(List<Song> songs) {
        this.songs = songs != null ? songs : new ArrayList<>();
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    public interface OnSongLongClickListener {
        void onSongLongClick(Song song);
    }

    public void setOnSongLongClickListener(OnSongLongClickListener listener) {
        this.longClickListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        if (position < songs.size()) {
            Song song = songs.get(position);
            holder.bind(song, position + 1);
            Log.d("SongAdapter", "Binding song: " + song.getName() + " by " + song.getArtist() + " at position " + position);

            holder.itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.onSongLongClick(song);
                }
                return true;
            });
        } else {
            Log.d("SongAdapter", "No song to bind at position " + position);
        }
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs != null ? songs : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        private TextView songNumber;
        private TextView songName;
        private TextView artistName;

        SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songNumber = itemView.findViewById(R.id.song_number);
            songName = itemView.findViewById(R.id.song_name);
            artistName = itemView.findViewById(R.id.artist_name);
        }

        void bind(Song song, int number) {
            songNumber.setText(String.format("%d.", number));
            songName.setText(song.getName());
            artistName.setText(song.getArtist());
        }
    }
}