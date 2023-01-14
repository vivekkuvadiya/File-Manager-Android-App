package com.vivek.filemanager.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.vivek.filemanager.R;
import com.vivek.filemanager.databinding.ImageItemBinding;
import com.vivek.filemanager.utils.OpenFile;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

public class RecentImageAdapter extends RecyclerView.Adapter<RecentImageAdapter.RecentItemViewHolder> {

    Activity activity;
    ArrayList<File> files = new ArrayList<>();

    public RecentImageAdapter(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @NotNull
    @Override
    public RecentItemViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new RecentItemViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.image_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecentItemViewHolder holder, int position) {
        holder.binding.icSelect.setVisibility(View.GONE);

        Glide.with(holder.itemView.getContext())
                .load(files.get(position).getPath())
                .apply(new RequestOptions()
                        .transform(new CenterCrop(), new RoundedCorners(18))
                        .skipMemoryCache(true)
                        .priority(Priority.LOW)
                        .format(DecodeFormat.PREFER_ARGB_8888))
                .into(holder.binding.iv);

        holder.binding.iv.setOnClickListener(v -> OpenFile.open(files.get(position)));
    }

    @Override
    public int getItemCount() {
        return Math.min(files.size(), 8);
    }

    public void addAll(ArrayList<File> files) {
        this.files.clear();
        this.files.addAll(files);
        activity.runOnUiThread(() -> notifyDataSetChanged());
    }

    public class RecentItemViewHolder extends RecyclerView.ViewHolder {
        ImageItemBinding binding;

        public RecentItemViewHolder(@NonNull @NotNull ImageItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
