package com.vivek.filemanager.adapter;

import android.app.Activity;
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
import com.vivek.filemanager.databinding.FileItemBinding;
import com.vivek.filemanager.interfaces.FileSelectedListener;
import com.tuyenmonkey.mkloader.MKLoader;

import java.io.File;
import java.util.ArrayList;

public class ZipAdapter extends RecyclerView.Adapter<ZipAdapter.ZipViewHolder> implements Filterable {

    private Activity activity;
    private ArrayList<File> files = new ArrayList<>();
    private FileSelectedListener fileSelectedListener;
    private ArrayList<File> filterList;

    public ZipAdapter(Activity activity, FileSelectedListener fileSelectedListener) {
        this.activity = activity;
        this.fileSelectedListener = fileSelectedListener;
    }

    @NonNull
    @Override
    public ZipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ZipViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.file_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ZipAdapter.ZipViewHolder holder, int position) {
        holder.onBind(files.get(position));
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public void addAll(ArrayList<File> files, Fragment fragment, MKLoader mkLoader, LinearLayout linearLayout){
        this.files.clear();
        this.files.addAll(files);
        this.filterList = new ArrayList<>(files);
        if (fragment!=null) {
            activity.runOnUiThread(() -> notifyDataSetChanged());
            if (mkLoader != null && linearLayout != null){
                activity.runOnUiThread(()-> {
                    mkLoader.setVisibility(View.GONE);
                    if (files.size()>0){
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

    public class ZipViewHolder extends RecyclerView.ViewHolder {
        FileItemBinding binding;
        public ZipViewHolder(@NonNull FileItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void onBind(File file) {
            fileSelectedListener.onBind(binding.ivSelect,file);
            binding.imageView4.setImageResource(R.drawable.ic_zip);
            binding.tvFileName.setText(file.getName());
            binding.tvSize.setText(Formatter.formatFileSize(activity, file.length()));

            binding.getRoot().setOnClickListener(v -> fileSelectedListener.onFileSelect(binding.ivSelect,file));

            binding.ivSelect.setOnClickListener(v -> fileSelectedListener.onIvSelectClick(files,getAdapterPosition(),binding.ivSelect));

        }
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<File> filList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0){
                filList.addAll(filterList);
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (File file : filterList){
                    if (file.getName().contains(filterPattern)){
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
            files.clear();
            files.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };

}
