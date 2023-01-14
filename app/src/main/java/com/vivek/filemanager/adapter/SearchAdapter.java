package com.vivek.filemanager.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

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
import com.vivek.filemanager.databinding.SearchItemBinding;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> implements Filterable {

    private Activity activity;
    private ArrayList<File> filesList = new ArrayList<>();
    private ArrayList<File> filterList = new ArrayList<>();
    private onFileClick onFileClick;

    public SearchAdapter(Activity activity,onFileClick onFileClick) {
        this.activity = activity;
        this.onFileClick = onFileClick;
    }

    @NonNull
    @NotNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new SearchViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.search_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SearchViewHolder holder, int position) {
        holder.onBind(filesList.get(position));
    }

    @Override
    public int getItemCount() {
        return filesList.size();
    }

    public void addAll(ArrayList<File> filesList) {
        this.filesList.addAll(filesList);
        this.filterList.addAll(filesList);
        activity.runOnUiThread(() -> notifyDataSetChanged());
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {
        SearchItemBinding binding;
        public SearchViewHolder(@NonNull @NotNull SearchItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void onBind(File file) {

            if (file.getName().toLowerCase().endsWith(".doc")) {
                binding.ivIcon.setImageResource(R.drawable.ic_doc);
            } else if (file.getName().toLowerCase().endsWith(".docx")) {
                binding.ivIcon.setImageResource(R.drawable.ic_docx);
            } else if (file.getName().toLowerCase().endsWith(".xls")) {
                binding.ivIcon.setImageResource(R.drawable.ic_xls);
            } else if (file.getName().toLowerCase().endsWith(".xlsx")) {
                binding.ivIcon.setImageResource(R.drawable.ic_xls);
            } else if (file.getName().toLowerCase().endsWith(".txt")) {
                binding.ivIcon.setImageResource(R.drawable.ic_txt);
            } else if (file.getName().toLowerCase().endsWith("ppt") || file.getName().toLowerCase().endsWith(".pptx")) {
                binding.ivIcon.setImageResource(R.drawable.ic_ppt);
            } else if (file.getName().toLowerCase().endsWith(".pdf")) {
                binding.ivIcon.setImageResource(R.drawable.ic_pdf);
            }else if (file.getName().toLowerCase().endsWith(".mp4")){
                Glide.with(itemView.getContext())
                        .load(file.getPath())
                        .apply(new RequestOptions()
                                .transform(new CenterCrop(), new RoundedCorners(18))
                                .skipMemoryCache(true)
                                .priority(Priority.LOW)
                                .format(DecodeFormat.PREFER_ARGB_8888))
                        .into(binding.ivIcon);
            } else if (file.getName().toLowerCase().endsWith(".mp3") || file.getName().toLowerCase().endsWith(".wav")) {
                binding.ivIcon.setImageResource(R.drawable.ic_audio);
            } else if (file.getName().toLowerCase().endsWith(".jpeg") || file.getName().toLowerCase().endsWith(".jpg") || file.getName().toLowerCase().endsWith(".png")) {
                Glide.with(itemView.getContext())
                        .load(file.getPath())
                        .apply(new RequestOptions()
                                .transform(new CenterCrop(), new RoundedCorners(18))
                                .skipMemoryCache(true)
                                .priority(Priority.LOW)
                                .format(DecodeFormat.PREFER_ARGB_8888))
                        .into(binding.ivIcon);
            }else if (file.getName().toLowerCase().endsWith(".apk")){
                binding.ivIcon.setImageDrawable(activity.getPackageManager().getPackageArchiveInfo(file.getAbsolutePath(), 0).applicationInfo.loadIcon(activity.getPackageManager()));
            }else {
                binding.ivIcon.setImageResource(R.drawable.ic_folder);
            }

            binding.tvFileName.setText(file.getName());

            binding.getRoot().setOnClickListener(v -> onFileClick.onClick(file));

        }
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<File> files = new ArrayList<>();
            if (constraint == null || constraint.length() == 0){
                files.addAll(filterList);
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (File file:filterList){
                    if (file.getName().toLowerCase().contains(filterPattern)){
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
            filesList.clear();
            filesList.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };

    public interface onFileClick{
       void onClick(File file);
    }
}
