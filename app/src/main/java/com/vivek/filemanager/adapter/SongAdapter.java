package com.vivek.filemanager.adapter;

import android.app.Activity;
import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.vivek.filemanager.R;
import com.vivek.filemanager.databinding.MusicItemBinding;
import com.vivek.filemanager.fragment.SongSelectFragment;
import com.vivek.filemanager.interfaces.FileSelectedListener;
import com.vivek.filemanager.utils.OpenFile;
import com.tuyenmonkey.mkloader.MKLoader;

import java.io.File;
import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MusicViewHolder> implements Filterable {

    ArrayList<File> song = new ArrayList<>();
    Activity activity;
    FileSelectedListener fileSelectedListener;
    ArrayList<File> filterList;

    public SongAdapter(Activity activity, FileSelectedListener fileSelectedListener) {
        this.activity = activity;
        this.fileSelectedListener = fileSelectedListener;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MusicViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.music_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SongAdapter.MusicViewHolder holder, int position) {
        holder.onBind(song.get(position));
    }

    @Override
    public int getItemCount() {
        return song.size();
    }

    public void addAll(ArrayList<File> song, Fragment fragment, MKLoader mkLoader, LinearLayout linearLayout) {
        this.song.clear();
        this.song.addAll(song);
        this.filterList = new ArrayList<>(this.song);
        if (fragment != null) {
            activity.runOnUiThread(() -> notifyDataSetChanged());
            if (mkLoader != null && linearLayout != null) {
                activity.runOnUiThread(() -> {
                    mkLoader.setVisibility(View.GONE);
                    if (song.size()>0){
                        linearLayout.setVisibility(View.GONE);
                    }else {
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                });

            }
        }
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public class MusicViewHolder extends RecyclerView.ViewHolder {
        MusicItemBinding binding;

        public MusicViewHolder(@NonNull MusicItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void onBind(File file) {

            if (SongSelectFragment.isActionEnable) {
                fileSelectedListener.onBind(binding.ivSelect, file);
            }

            binding.tvFileName.setText(file.getName());
            binding.tvFileSize.setText(Formatter.formatFileSize(activity, file.length()));
            binding.tvFileDate.setText(DateFormat.format("dd/MM/yyyy", file.lastModified()));
            binding.tvFileTime.setText(DateFormat.format("hh:mm a", file.lastModified()));

            binding.ivSelect.setOnClickListener(v -> {
                fileSelectedListener.onFileSelect(binding.ivSelect, file);
//                Toast.makeText(activity, file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            });

            binding.getRoot().setOnClickListener(v -> OpenFile.open(file));
        }
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<File> filList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filList.addAll(filterList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (File file : filterList) {
                    if (file.getName().toLowerCase().contains(filterPattern)) {
                        filList.add(file);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            song.clear();
            song.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };
}
