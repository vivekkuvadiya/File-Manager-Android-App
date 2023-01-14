package com.vivek.filemanager.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

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
import com.vivek.filemanager.databinding.VideoItemBinding;
import com.vivek.filemanager.fragment.ImageSelectFragment;
import com.vivek.filemanager.model.ImgModel;
import com.vivek.filemanager.utils.OpenFile;

import java.io.File;
import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Activity activity;
    ArrayList<ImgModel> files;
    int from;
    ImageSelect imageSelect;
    String date;
    int position;
    RequestOptions requestOptions;

    public VideoAdapter(Activity activity, ArrayList<ImgModel> files, String date, int position, int from, ImageSelect imageSelect, RequestOptions requestOptions) {
        this.activity = activity;
        this.files = files;
        this.date = date;
        this.from = from;
        this.position = position;
        this.imageSelect = imageSelect;
        this.requestOptions = requestOptions;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0)
            return new ImageViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.image_item, parent, false));
        else
            return new VideoViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.video_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        File file = files.get(position).file;
        if (holder.getItemViewType() == 0) {
            ((ImageViewHolder) holder).onBind(file);
        } else {
            ((VideoViewHolder) holder).onBind(file);
        }


//        retriever.setDataSource(file.getPath());
//        holder.binding.tvLength.setText(Utils.getDurationBreakdown(Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))));
    }

    @Override
    public int getItemViewType(int position) {
        if (from == 0) {
            return 0;
        } else {
            return 1;
        }
    }


    interface ImageSelect {
        void imgSelect(ImgModel imgModel, ImageView imageView, int i, int j);

        void onImageBind(ImageView imageView, File file);
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        VideoItemBinding binding;

        public VideoViewHolder(@NonNull VideoItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void onBind(File file) {
            Glide.with(itemView.getContext())
                    .load(file.getPath())
                    .apply(requestOptions)
                    .into(binding.iv);
            binding.iv.setOnClickListener(v -> OpenFile.open(file));

            binding.tvLength.setText(files.get(getAdapterPosition()).duration);
        }
    }
    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageItemBinding binding;

        public ImageViewHolder(@NonNull ImageItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void onBind(File file) {

            if (ImageSelectFragment.isActionModeOn) {
                imageSelect.onImageBind(binding.icSelect, file);
            }

            Glide.with(itemView.getContext())
                    .load(file.getPath())
                    .apply(new RequestOptions()
                            .transform(new CenterCrop(), new RoundedCorners(18))
                            .skipMemoryCache(true)
                            .priority(Priority.LOW)
                            .format(DecodeFormat.PREFER_ARGB_8888))
                    .into(binding.iv);

//            Picasso.get().load(file).into(binding.iv);

            binding.iv.setOnClickListener(v -> OpenFile.open(file));

            binding.icSelect.setOnClickListener(v -> imageSelect.imgSelect(new ImgModel(file, null, null), binding.icSelect, position, getAdapterPosition()));
        }
    }
}
