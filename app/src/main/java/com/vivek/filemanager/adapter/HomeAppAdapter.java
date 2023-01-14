package com.vivek.filemanager.adapter;

import android.app.Activity;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.vivek.filemanager.R;
import com.vivek.filemanager.databinding.HomeMenuBinding;
import com.vivek.filemanager.interfaces.HomeAppClickListener;
import com.vivek.filemanager.utils.Utils;

public class HomeAppAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String[] apps;
    private Activity mainActivity;
    HomeAppClickListener homeAppClickListener;

    public HomeAppAdapter(Activity mainActivity, String[] apps, HomeAppClickListener homeAppClickListener) {
        this.apps = apps;
        this.mainActivity = mainActivity;
        this.homeAppClickListener = homeAppClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MenuAppViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.home_menu, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MenuAppViewHolder viewHolder = (MenuAppViewHolder) holder;
        if (apps[position].equals("Images")) {
            viewHolder.binding.img.setImageResource(R.drawable.ic_image);
            viewHolder.binding.size.setText(Formatter.formatFileSize(holder.itemView.getContext(), (Utils.EXT_IMAGE_SIZE + Utils.SD_IMAGE_SIZE)));
        } else if (apps[position].equals("Audio")) {
            viewHolder.binding.img.setImageResource(R.drawable.ic_music);
            viewHolder.binding.size.setText(Formatter.formatFileSize(holder.itemView.getContext(), (Utils.EXT_AUDIO_SIZE + Utils.SD_AUDIO_SIZE)));
        } else if (apps[position].equals("Videos")) {
            viewHolder.binding.img.setImageResource(R.drawable.ic_video);
            viewHolder.binding.size.setText(Formatter.formatFileSize(holder.itemView.getContext(), (Utils.EXT_VIDEO_SIZE + Utils.SD_VIDEO_SIZE)));
        } else if (apps[position].equals("Zips")) {
            viewHolder.binding.img.setImageResource(R.drawable.ic_zips);
            viewHolder.binding.size.setText(Formatter.formatFileSize(holder.itemView.getContext(), (Utils.EXT_ZIPS_SIZE + Utils.SD_ZIPS_SIZE)));
        } else if (apps[position].equals("Apps")) {
            viewHolder.binding.img.setImageResource(R.drawable.ic_apps);
            viewHolder.binding.size.setText(Formatter.formatFileSize(holder.itemView.getContext(), (Utils.EXT_APPS_SIZE + Utils.SD_APPS_SIZE)));
        } else if (apps[position].equals("Document")) {
            viewHolder.binding.img.setImageResource(R.drawable.ic_document);
            viewHolder.binding.size.setText(Formatter.formatFileSize(holder.itemView.getContext(), (Utils.EXT_DOCUMENT_SIZE + Utils.SD_DOCUMENT_SIZE)));
        } else if (apps[position].equals("Download")) {
            viewHolder.binding.img.setImageResource(R.drawable.ic_download);
            viewHolder.binding.size.setText(Formatter.formatFileSize(holder.itemView.getContext(), (Utils.EXT_DOWNLOAD_SIZE)));
        } else {
            viewHolder.binding.img.setImageResource(R.drawable.ic_more);
            viewHolder.binding.size.setText("");
        }
        viewHolder.binding.txt.setText(apps[position]);
        viewHolder.itemView.setOnClickListener(v -> homeAppClickListener.onHomeAppClick(apps[position]));
    }

    @Override
    public int getItemCount() {
        return apps.length;
    }

    public void notifyApps() {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    class MenuAppViewHolder extends RecyclerView.ViewHolder {

        HomeMenuBinding binding;

        public MenuAppViewHolder(@NonNull HomeMenuBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

}
