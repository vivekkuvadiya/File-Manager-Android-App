package com.vivek.filemanager.adapter;

import android.app.Activity;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Filter;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.vivek.filemanager.R;
import com.vivek.filemanager.databinding.FileItemBinding;
import com.vivek.filemanager.fragment.SelectAppsFragment;
import com.vivek.filemanager.interfaces.FileSelectedListener;
import com.tuyenmonkey.mkloader.MKLoader;

import java.io.File;
import java.util.ArrayList;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.AppsViewHolder> implements Filterable {

    ArrayList<File> appsList  = new ArrayList<>();
    Activity activity;
    FileSelectedListener fileSelectedListener;
    ArrayList<File> filterList = new ArrayList<>();

    public AppsAdapter(Activity activity, FileSelectedListener fileSelectedListener) {
        this.activity = activity;
        this.fileSelectedListener = fileSelectedListener;
    }

    @NonNull
    @Override
    public AppsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AppsViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.file_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AppsAdapter.AppsViewHolder holder, int position) {
        holder.onBind(appsList.get(position));
    }

    public void addAll(ArrayList<File> appsList, Fragment fragment, MKLoader mkLoader, LinearLayout linearLayout){
        this.appsList.clear();
        this.appsList.addAll(appsList);
        this.filterList = new ArrayList<>(this.appsList);
        if (fragment!=null) {
            activity.runOnUiThread(() -> notifyDataSetChanged());
            if (mkLoader != null && linearLayout != null){
                activity.runOnUiThread(()-> {
                    mkLoader.setVisibility(View.GONE);
                    if (appsList.size()>0){
                        linearLayout.setVisibility(View.GONE);
                    }else {
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return appsList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public class AppsViewHolder extends RecyclerView.ViewHolder {
        FileItemBinding binding;
        public AppsViewHolder(@NonNull FileItemBinding itemView) {
            super(itemView.getRoot());
            binding= itemView;
        }

        public void onBind(File file) {

            if (SelectAppsFragment.isActionModeOn){
                fileSelectedListener.onBind(binding.ivSelect,file);
            }

            binding.imageView4.setImageDrawable(activity.getPackageManager().getPackageArchiveInfo(file.getAbsolutePath(), 0).applicationInfo.loadIcon(activity.getPackageManager()));
            binding.tvFileName.setText(file.getName());
            binding.tvSize.setText(Formatter.formatFileSize(activity,file.length()));

            binding.getRoot().setOnClickListener(v -> fileSelectedListener.onFileSelect(binding.ivSelect,file));

            binding.ivSelect.setOnClickListener(v -> fileSelectedListener.onIvSelectClick(appsList,getAdapterPosition(),binding.ivSelect));
        }
    }

    private  Filter filter = new Filter() {
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
            appsList.clear();;
            appsList.addAll((ArrayList)results.values);
            notifyDataSetChanged();
        }
    };

}
