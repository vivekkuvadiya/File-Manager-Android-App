package com.vivek.filemanager.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vivek.filemanager.R;
import com.vivek.filemanager.SelectFileActivity;
import com.vivek.filemanager.adapter.NestVideoAdapter;
import com.vivek.filemanager.databinding.DialogDeleteBinding;
import com.vivek.filemanager.databinding.DialogFileInfoBinding;
import com.vivek.filemanager.databinding.FragmentImageSelectBinding;
import com.vivek.filemanager.db.DbHelper;
import com.vivek.filemanager.interfaces.ActionModeListener;
import com.vivek.filemanager.model.ImgShortModel;
import com.vivek.filemanager.utils.Utils;

import java.io.File;
import java.util.ArrayList;


public class ImageSelectFragment extends Fragment implements View.OnClickListener, ActionModeListener {

    private FragmentImageSelectBinding binding;
    private ArrayList<ImgShortModel> imgShortModels;
    private File selectedFile;
    public static boolean isActionModeOn = true;
    public static ArrayList<File> selectedList = new ArrayList<>();
    NestVideoAdapter adapter;
    public static TextView tvCounter;
    public int position;
    public int p;
    public static ActionModeListener actionModeListener;
    private boolean isMoCo = false;

    @Override
    public void onResume() {
        super.onResume();
        if (isMoCo){
            AllImageFragment.actionModeListener.onEventListener(Utils.EVENT_COPY);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imgShortModels = (ArrayList<ImgShortModel>) getArguments().getSerializable("data");
            selectedFile = new File(getArguments().getString("file"));
            position = getArguments().getInt("position");
            p = getArguments().getInt("p");
        }
        isActionModeOn = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentImageSelectBinding.inflate(inflater);
        actionModeListener = this;
        tvCounter = binding.tvCounter;
//        selectedList.add(new ImgModel(selectedFile,date,null));

        binding.rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new NestVideoAdapter(getActivity(), 0,9);
        adapter.addToSelected(selectedFile);
        binding.rv.setAdapter(adapter);
        adapter.addAll(imgShortModels,this,null,null);
        binding.rv.scrollToPosition(position);
        adapter.scroll(p);

        binding.lyDetails.setOnClickListener(this);
        binding.lyDelete.setOnClickListener(this);
        binding.lyFavourite.setOnClickListener(this);
        binding.lyCopy.setOnClickListener(this);
        binding.lyMove.setOnClickListener(this);

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isActionModeOn = false;
        isMoCo = false;
        selectedList.clear();
    }

    private void showInfoDialog() {
        Dialog dialog = new Dialog(getContext());
        DialogFileInfoBinding dialogFileInfoBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_file_info, null, false);
        dialog.setContentView(dialogFileInfoBinding.getRoot());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        if (selectedList.size()>1){
            dialogFileInfoBinding.location.setVisibility(View.GONE);
            dialogFileInfoBinding.tvLocation.setVisibility(View.GONE);
            dialogFileInfoBinding.date.setText("Contains :");
            dialogFileInfoBinding.tvDate.setText(String.format("%d Files",selectedList.size()));
            dialogFileInfoBinding.size.setVisibility(View.GONE);
            dialogFileInfoBinding.tvSize.setVisibility(View.GONE);
        }else {
            dialogFileInfoBinding.location.setVisibility(View.VISIBLE);
            dialogFileInfoBinding.tvLocation.setVisibility(View.VISIBLE);
            dialogFileInfoBinding.date.setText("Date :");
            dialogFileInfoBinding.tvDate.setText(DateFormat.format("dd - MM- yyyy",selectedList.get(0).lastModified()));
            dialogFileInfoBinding.size.setVisibility(View.VISIBLE);
            dialogFileInfoBinding.tvSize.setVisibility(View.VISIBLE);
            dialogFileInfoBinding.tvLocation.setText(selectedList.get(0).getPath());
            dialogFileInfoBinding.tvSize.setText(Formatter.formatFileSize(getContext(),selectedList.get(0).length()));
        }

        dialogFileInfoBinding.btnOk.setOnClickListener(v -> dialog.dismiss());
    }

    @Override
    public void onClick(View v) {
        if (binding.lyDetails.equals(v)){
            showInfoDialog();
        }else if (binding.lyDelete.equals(v)){
            showDeleteDialog();
            getActivity().onBackPressed();
        }else if (binding.lyFavourite.equals(v)){
            addToFavourite();
        }else if (binding.lyCopy.equals(v)){
            Intent intent = new Intent(getActivity(), SelectFileActivity.class);
            intent.putExtra("action", "copy");
            intent.putExtra("from", "allImage");
            startActivity(intent);
        }else if (binding.lyMove.equals(v)){
            Intent intent = new Intent(getActivity(), SelectFileActivity.class);
            intent.putExtra("action", "move");
            intent.putExtra("from", "allImage");
            startActivity(intent);
        }
    }

    private void showDeleteDialog(){
        Dialog dialog = new Dialog(getActivity());
        DialogDeleteBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),R.layout.dialog_delete,null,false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        binding.btnDelete.setOnClickListener(v -> {
            for (File file : selectedList){
                Log.e("TAG", "showDeleteDialog: "+file );
                file.delete();
            }
            dialog.dismiss();

            AllImageFragment.actionModeListener.onEventListener(Utils.EVENT_DELETE);
        });

        binding.btnCancel.setOnClickListener(v -> dialog.dismiss());
    }

    private void addToFavourite(){
        if (selectedList.size()>0) {
            for (File file : selectedList) {
                DbHelper.addToFavourite(file.getAbsolutePath());
            }
        }
        AllImageFragment.actionModeListener.onEventListener(Utils.EVENT_ADD_TO_FAV);
    }

    @Override
    public void onEventListener(int event) {
        if (event == Utils.EVENT_MOVE){
            isMoCo = true;
        }else if (event == Utils.EVENT_COPY){
            isMoCo = true;
        }
    }
}