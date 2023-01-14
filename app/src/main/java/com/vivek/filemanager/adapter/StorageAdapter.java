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

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.vivek.filemanager.R;
import com.vivek.filemanager.databinding.FileItem2Binding;
import com.vivek.filemanager.databinding.FileItemBinding;
import com.vivek.filemanager.databinding.FolderItemBinding;
import com.vivek.filemanager.databinding.LargeFileItemBinding;
import com.vivek.filemanager.fragment.SelectStorageFragment;
import com.vivek.filemanager.interfaces.FileSelectedListener;
import com.tuyenmonkey.mkloader.MKLoader;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;


public class StorageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    ArrayList<File> files = new ArrayList<>();
    Activity activity;
    FileSelectedListener fileSelectedListener;
    private ArrayList<File> filterList;
    private String from;

    public StorageAdapter(Activity activity, FileSelectedListener fileSelectedListener, String from) {
        this.activity = activity;
        this.from = from;
        this.fileSelectedListener = fileSelectedListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0)
            return new FolderViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.folder_item, parent, false));
        else if (viewType == 1)
            return new FileViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.file_item2, parent, false));
        else
            return new LargeFileViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.large_file_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 0) {
            FolderViewHolder viewHolder = (FolderViewHolder) holder;
            viewHolder.onBind(files.get(position));
        } else if (holder.getItemViewType() == 1) {
            FileViewHolder viewHolder = (FileViewHolder) holder;
            viewHolder.onBind(files.get(position));
        } else {
            LargeFileViewHolder viewHolder = (LargeFileViewHolder) holder;
            viewHolder.onBind(files.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (from.equals("storage")) {
            File file = files.get(position);
            if (file.isDirectory()) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return 2;
        }
    }

    public void addAll(ArrayList<File> files, Fragment fragment, MKLoader mkLoader, LinearLayout linearLayout) {
        this.files.clear();
        this.files.addAll(files);
        this.filterList = new ArrayList<>(files);
        if (fragment != null) {
            activity.runOnUiThread(() -> notifyDataSetChanged());
            if (mkLoader != null) {
                activity.runOnUiThread(() -> mkLoader.setVisibility(View.GONE));
            }
            if (linearLayout != null){
                activity.runOnUiThread(() -> {
                    if (files.size()>0){
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

    class FolderViewHolder extends RecyclerView.ViewHolder {

        FolderItemBinding binding;

        public FolderViewHolder(@NonNull FolderItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void onBind(File file) {
            if (SelectStorageFragment.isActionModeOn) {
                fileSelectedListener.onBind(binding.ivSelect, file);
            }
            binding.tvFileName.setText(file.getName());
            binding.tvDate.setText(DateFormat.format("dd/MM/yy", file.lastModified()));
            binding.tvSongCount.setText(String.format("%d Item", getCount(file)));

            binding.getRoot().setOnClickListener(v -> fileSelectedListener.onFileSelect(binding.imageView4, file));
            binding.ivSelect.setOnClickListener(v -> fileSelectedListener.onIvSelectClick(files, getAdapterPosition(), binding.ivSelect));
        }
    }

    class FileViewHolder extends RecyclerView.ViewHolder {
        FileItem2Binding binding;

        public FileViewHolder(@NonNull FileItem2Binding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void onBind(File file) {

            if (SelectStorageFragment.isActionModeOn) {
                fileSelectedListener.onBind(binding.ivSelect, file);
            }
            if (file.getName().toLowerCase().endsWith(".doc")) {
                binding.imageView4.setImageResource(R.drawable.ic_doc);
            } else if (file.getName().toLowerCase().endsWith(".docx")) {
                binding.imageView4.setImageResource(R.drawable.ic_docx);
            } else if (file.getName().toLowerCase().endsWith(".xls")) {
                binding.imageView4.setImageResource(R.drawable.ic_xls);
            } else if (file.getName().toLowerCase().endsWith(".xlsx")) {
                binding.imageView4.setImageResource(R.drawable.ic_xls);
            } else if (file.getName().toLowerCase().endsWith(".txt")) {
                binding.imageView4.setImageResource(R.drawable.ic_txt);
            } else if (file.getName().toLowerCase().endsWith("ppt") || file.getName().toLowerCase().endsWith(".pptx")) {
                binding.imageView4.setImageResource(R.drawable.ic_ppt);
            } else if (file.getName().toLowerCase().endsWith(".pdf")) {
                binding.imageView4.setImageResource(R.drawable.ic_pdf);
            } else if (file.getName().toLowerCase().endsWith(".mp4")) {
                Glide.with(itemView.getContext())
                        .load(file.getPath())
                        .apply(new RequestOptions()
                                .transform(new CenterCrop(), new RoundedCorners(18))
                                .skipMemoryCache(true)
                                .priority(Priority.LOW)
                                .format(DecodeFormat.PREFER_ARGB_8888))
                        .into(binding.imageView4);
            } else if (file.getName().toLowerCase().endsWith(".mp3") || file.getName().toLowerCase().endsWith(".wav")) {
                binding.imageView4.setImageResource(R.drawable.ic_audio);
            } else if (file.getName().toLowerCase().endsWith(".jpeg") || file.getName().toLowerCase().endsWith(".jpg") || file.getName().toLowerCase().endsWith(".png")) {
                Glide.with(itemView.getContext())
                        .load(file.getPath())
                        .apply(new RequestOptions()
                                .transform(new CenterCrop(), new RoundedCorners(18))
                                .skipMemoryCache(true)
                                .priority(Priority.LOW)
                                .format(DecodeFormat.PREFER_ARGB_8888))
                        .into(binding.imageView4);
            } else if (file.getName().toLowerCase().endsWith(".apk")) {
                binding.imageView4.setImageDrawable(activity.getPackageManager().getPackageArchiveInfo(file.getAbsolutePath(), 0).applicationInfo.loadIcon(activity.getPackageManager()));
            } else {
                binding.imageView4.setImageResource(R.drawable.ic_folder);
            }

            binding.tvFileName.setText(file.getName());
            binding.tvSize.setText(Formatter.formatFileSize(activity, file.length()));
            binding.getRoot().setOnClickListener(v -> fileSelectedListener.onFileSelect(binding.imageView4, file));

            binding.ivSelect.setOnClickListener(v -> fileSelectedListener.onIvSelectClick(files, getAdapterPosition(), binding.ivSelect));
        }
    }

    class LargeFileViewHolder extends RecyclerView.ViewHolder {
        LargeFileItemBinding binding;

        public LargeFileViewHolder(@NonNull @NotNull LargeFileItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void onBind(File file) {

            if (SelectStorageFragment.isActionModeOn) {
                fileSelectedListener.onBind(binding.ivSelect, file);
            }

            if (file.getName().toLowerCase().endsWith(".doc")) {
                binding.imageView4.setImageResource(R.drawable.ic_doc);
            } else if (file.getName().toLowerCase().endsWith(".docx")) {
                binding.imageView4.setImageResource(R.drawable.ic_docx);
            } else if (file.getName().toLowerCase().endsWith(".xls")) {
                binding.imageView4.setImageResource(R.drawable.ic_xls);
            } else if (file.getName().toLowerCase().endsWith(".xlsx")) {
                binding.imageView4.setImageResource(R.drawable.ic_xls);
            } else if (file.getName().toLowerCase().endsWith(".txt")) {
                binding.imageView4.setImageResource(R.drawable.ic_txt);
            } else if (file.getName().toLowerCase().endsWith("ppt") || file.getName().toLowerCase().endsWith(".pptx")) {
                binding.imageView4.setImageResource(R.drawable.ic_ppt);
            } else if (file.getName().toLowerCase().endsWith(".pdf")) {
                binding.imageView4.setImageResource(R.drawable.ic_pdf);
            } else if (file.getName().toLowerCase().endsWith(".mp4")) {
                Glide.with(itemView.getContext())
                        .load(file.getPath())
                        .apply(new RequestOptions()
                                .transform(new CenterCrop(), new RoundedCorners(18))
                                .skipMemoryCache(true)
                                .priority(Priority.LOW)
                                .format(DecodeFormat.PREFER_ARGB_8888))
                        .into(binding.imageView4);
            } else if (file.getName().toLowerCase().endsWith(".mp3") || file.getName().toLowerCase().endsWith(".wav")) {
                binding.imageView4.setImageResource(R.drawable.ic_audio);
            } else if (file.getName().toLowerCase().endsWith(".jpeg") || file.getName().toLowerCase().endsWith(".jpg") || file.getName().toLowerCase().endsWith(".png")) {
                Glide.with(itemView.getContext())
                        .load(file.getPath())
                        .apply(new RequestOptions()
                                .transform(new CenterCrop(), new RoundedCorners(18))
                                .skipMemoryCache(true)
                                .priority(Priority.LOW)
                                .format(DecodeFormat.PREFER_ARGB_8888))
                        .into(binding.imageView4);
            } else if (file.getName().toLowerCase().endsWith(".apk")) {
                binding.imageView4.setImageDrawable(activity.getPackageManager().getPackageArchiveInfo(file.getAbsolutePath(), 0).applicationInfo.loadIcon(activity.getPackageManager()));
            } else {
                binding.imageView4.setImageResource(R.drawable.ic_folder);
            }

            binding.tvFileName.setText(file.getName());


            binding.tvSize.setText(Formatter.formatFileSize(activity, file.length()));
            binding.tvPath.setText(file.getAbsolutePath());
            binding.getRoot().setOnClickListener(v -> fileSelectedListener.onFileSelect(binding.imageView4, file));

            binding.ivSelect.setOnClickListener(v -> fileSelectedListener.onIvSelectClick(files, getAdapterPosition(), binding.ivSelect));
        }
    }

    int getCount(File file) {
        int count = 0;
        File[] files = file.listFiles();
        if (files != null) {
            for (File singleFile : files) {
                if (!singleFile.isHidden()) {
                    count += 1;
                }
            }
        }
        return count;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<File> files = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                files.addAll(filterList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (File file : filterList) {
                    if (file.getName().toLowerCase().contains(filterPattern)) {
                        files.add(file);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = files;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            files.clear();
            files.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };

}
