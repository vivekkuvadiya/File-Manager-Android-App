package com.vivek.filemanager.adapter;

import android.app.Activity;
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
import com.vivek.filemanager.databinding.DocumentItemBinding;
import com.vivek.filemanager.databinding.MediaFolderItemBinding;
import com.vivek.filemanager.fragment.SelectVideoFolderFragment;
import com.vivek.filemanager.interfaces.FileSelectedListener;
import com.vivek.filemanager.model.MediaModel;
import com.tuyenmonkey.mkloader.MKLoader;

import java.util.ArrayList;

public class AudioFolderAdapter extends RecyclerView.Adapter<AudioFolderAdapter.AudioFolderViewHolder> implements Filterable {

    Activity activity;
    ArrayList<MediaModel> fileList = new ArrayList<>();
    FileSelectedListener fileSelectedListener;
    String from;
    ArrayList<MediaModel> filterList;

    public AudioFolderAdapter(Activity activity, String from, FileSelectedListener fileSelectedListener) {
        this.activity = activity;
        this.from = from;
        this.fileSelectedListener = fileSelectedListener;
    }

    @NonNull
    @Override
    public AudioFolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AudioFolderViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.media_folder_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AudioFolderViewHolder holder, int position) {
        holder.onBind(fileList.get(position));
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public void addAll(ArrayList<MediaModel> fileList, Fragment fragment, MKLoader mkLoader, LinearLayout linearLayout) {
        this.fileList.clear();
        this.fileList.addAll(fileList);
        this.filterList = new ArrayList<>(this.fileList);
        if (fragment != null) {
            activity.runOnUiThread(() -> notifyDataSetChanged());
            if (mkLoader != null && linearLayout != null) {
                activity.runOnUiThread(() -> {
                    mkLoader.setVisibility(View.GONE);
                    if (fileList.size()>0){
                        linearLayout.setVisibility(View.GONE);
                    }else{
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

    public class AudioFolderViewHolder extends RecyclerView.ViewHolder {

        MediaFolderItemBinding binding;

        public AudioFolderViewHolder(@NonNull MediaFolderItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            if (from.equals("vid")) {
                binding.imageView4.setImageResource(R.drawable.ic_vid_folder);
            } else {
                binding.imageView4.setImageResource(R.drawable.ic_music_folder);
            }
        }

        public void onBind(MediaModel mediaModel) {

            if (SelectVideoFolderFragment.isActionModeOn) {
                fileSelectedListener.onBind(binding.imageView5, mediaModel.file);
            }

            binding.tvFileName.setText(mediaModel.file.getName());
            binding.tvSongCount.setText(mediaModel.count + " Songs");

            itemView.setOnClickListener(v -> fileSelectedListener.onFileSelect(binding.imageView5, mediaModel.file));

            binding.imageView5.setOnClickListener(v -> fileSelectedListener.onIvMediaSelectClick(fileList, getAdapterPosition(), binding.imageView5));
        }
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<MediaModel> filList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filList.addAll(filterList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (MediaModel mediaModel : filterList) {
                    if (mediaModel.file.getName().toLowerCase().trim().contains(filterPattern)) {
                        filList.add(mediaModel);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            fileList.clear();
            fileList.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };
}
