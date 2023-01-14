package com.vivek.filemanager.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.vivek.filemanager.R;
import com.vivek.filemanager.SelectFileActivity;
import com.vivek.filemanager.adapter.AudioFolderAdapter;
import com.vivek.filemanager.databinding.DialogDeleteBinding;
import com.vivek.filemanager.databinding.DialogFileInfoBinding;
import com.vivek.filemanager.databinding.FragmentSelectVideoFolderBinding;
import com.vivek.filemanager.db.DbHelper;
import com.vivek.filemanager.interfaces.ActionModeListener;
import com.vivek.filemanager.interfaces.FileSelectedListener;
import com.vivek.filemanager.model.MediaModel;
import com.vivek.filemanager.utils.Utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class SelectVideoFolderFragment extends Fragment implements FileSelectedListener, ActionModeListener, View.OnClickListener {

    private FragmentSelectVideoFolderBinding binding;
    public static ArrayList<File> selectedList = new ArrayList<>();
    public static boolean isActionModeOn = true;
    private String from;

    private ArrayList<MediaModel> list = new ArrayList<>();
    private int position;
    public static ActionModeListener actionModeListener;
    private boolean isMoCo = false;
    private ActionModeListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            list = (ArrayList<MediaModel>) getArguments().getSerializable("val");
            position = getArguments().getInt("position");
            from = getArguments().getString("from");
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isMoCo) {
            listener.onEventListener(Utils.EVENT_MOVE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSelectVideoFolderBinding.inflate(inflater);

        actionModeListener = this;

        if (from.equals("vid")) {
            listener = VideoFolderFragment.actionModeListener;
        } else if (from.equals("aud")) {
            listener = SongFolderFragment.actionModeListener;
        }

        binding.rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        AudioFolderAdapter audioFolderAdapter = new AudioFolderAdapter(getActivity(), "vid", this);
        binding.rv.setAdapter(audioFolderAdapter);

        selectedList.add(list.get(position).file);

        if (selectedList.size() > 0) {
            audioFolderAdapter.addAll(list,this,null,null);
        }

        binding.rv.scrollToPosition(position);

        binding.lyDetails.setOnClickListener(this);
        binding.lyDelete.setOnClickListener(this);
        binding.lyFavourite.setOnClickListener(this);
        binding.lyMove.setOnClickListener(this);
        binding.lyCopy.setOnClickListener(this);

        return binding.getRoot();
    }

    @Override
    public void onFileSelect(ImageView imageView, File file) {

    }

    @Override
    public void onBind(ImageView imageView, File file) {
        if (isAvailable(file)) {
            imageView.setImageResource(R.drawable.ic_selected);
        } else {
            imageView.setImageResource(R.drawable.no_select);
        }
    }

    @Override
    public void onIvSelectClick(ArrayList<File> files, int position, ImageView imageView) {

    }

    @Override
    public void onIvMediaSelectClick(ArrayList<MediaModel> mediaModels, int position, ImageView imageView) {
        if (imageView.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.ic_selected).getConstantState())) {
            selectedList.remove(mediaModels.get(position).file);
            imageView.setImageResource(R.drawable.no_select);
        } else {
            selectedList.add(mediaModels.get(position).file);
            imageView.setImageResource(R.drawable.ic_selected);
        }
        binding.tvCounter.setText(String.format("%d Selected", selectedList.size()));
    }

    private boolean isAvailable(File file) {
        for (int i = 0; i < selectedList.size(); i++) {
            if (file.equals(selectedList.get(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onEventListener(int event) {

        switch (event) {
            case Utils.EVENT_COPY:
            case Utils.EVENT_MOVE:
                isMoCo = true;
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        selectedList.clear();
        isMoCo = false;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(binding.lyDetails)) {

            showInfoDialog();

        } else if (v.equals(binding.lyDelete)) {

            showDeleteDialog();

        } else if (v.equals(binding.lyFavourite)) {

            addToFavourite();

        } else if (v.equals(binding.lyMove)) {

            Intent intent = new Intent(getActivity(), SelectFileActivity.class);
            intent.putExtra("action", "move");
            intent.putExtra("from", "vidFolder");
            startActivity(intent);

        } else if (v.equals(binding.lyCopy)) {
            Intent intent = new Intent(getActivity(), SelectFileActivity.class);
            intent.putExtra("action", "copy");
            intent.putExtra("from", "vidFolder");
            startActivity(intent);

        } else if (v.equals(binding.ivClose)) {
            listener.onEventListener(Utils.EVENT_CLOSE);
        }
    }

    private void showInfoDialog() {
        Dialog dialog = new Dialog(getContext());
        DialogFileInfoBinding dialogFileInfoBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_file_info, null, false);
        dialog.setContentView(dialogFileInfoBinding.getRoot());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        if (selectedList.size() > 1) {
            dialogFileInfoBinding.location.setVisibility(View.GONE);
            dialogFileInfoBinding.tvLocation.setVisibility(View.GONE);
            dialogFileInfoBinding.date.setText("Contains :");
            dialogFileInfoBinding.tvDate.setText(String.format("%d Files", selectedList.size()));
            dialogFileInfoBinding.size.setVisibility(View.GONE);
            dialogFileInfoBinding.tvSize.setVisibility(View.GONE);
        } else {
            dialogFileInfoBinding.location.setVisibility(View.VISIBLE);
            dialogFileInfoBinding.tvLocation.setVisibility(View.VISIBLE);
            dialogFileInfoBinding.date.setText("Date :");
            dialogFileInfoBinding.tvDate.setText(DateFormat.format("dd - MM- yyyy", selectedList.get(0).lastModified()));
            dialogFileInfoBinding.size.setVisibility(View.VISIBLE);
            dialogFileInfoBinding.tvSize.setVisibility(View.VISIBLE);
            dialogFileInfoBinding.tvLocation.setText(selectedList.get(0).getPath());
            dialogFileInfoBinding.tvSize.setText(Formatter.formatFileSize(getContext(), selectedList.get(0).length()));
        }

        dialogFileInfoBinding.btnOk.setOnClickListener(v -> dialog.dismiss());
    }

    private void showDeleteDialog() {
        Dialog dialog = new Dialog(getActivity());
        DialogDeleteBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_delete, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        binding.btnDelete.setOnClickListener(v -> {
            for (File file : selectedList) {
                if (file.isDirectory()) {
                    try {
                        FileUtils.deleteDirectory(file);
                    } catch (IOException e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    file.delete();
                }
            }
            dialog.dismiss();

            listener.onEventListener(Utils.EVENT_DELETE);

        });

        binding.btnCancel.setOnClickListener(v -> dialog.dismiss());

    }

    private void addToFavourite() {
        for (File file : selectedList) {
            DbHelper.addToFavourite(file.getAbsolutePath());
        }
        listener.onEventListener(Utils.EVENT_ADD_TO_FAV);
    }
}