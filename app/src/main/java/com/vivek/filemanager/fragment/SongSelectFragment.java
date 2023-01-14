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

import com.vivek.filemanager.R;
import com.vivek.filemanager.SelectFileActivity;
import com.vivek.filemanager.adapter.SongAdapter;
import com.vivek.filemanager.databinding.DialogDeleteBinding;
import com.vivek.filemanager.databinding.DialogFileInfoBinding;
import com.vivek.filemanager.databinding.FragmentSongSelectBinding;
import com.vivek.filemanager.db.DbHelper;
import com.vivek.filemanager.interfaces.ActionModeListener;
import com.vivek.filemanager.interfaces.FileSelectedListener;
import com.vivek.filemanager.model.MediaModel;
import com.vivek.filemanager.utils.Utils;

import java.io.File;
import java.util.ArrayList;

public class SongSelectFragment extends Fragment implements FileSelectedListener, View.OnClickListener, ActionModeListener {

    FragmentSongSelectBinding binding;
    //    ArrayList<File> allAudio = new ArrayList<>();
    ArrayList<File> selectedList = new ArrayList<>();
    public static boolean isActionEnable = true;
    public static ArrayList<File> audioList = new ArrayList<>();
    int position;
    public static ActionModeListener actionModeListener;
    private boolean isMoCo = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt("position");
            audioList = (ArrayList<File>) getArguments().getSerializable("val");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isMoCo) {
            SongFragment.actionModeListener.onEventListener(Utils.EVENT_COPY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSongSelectBinding.inflate(inflater);

        actionModeListener = this;

        binding.rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        SongAdapter songAdapter = new SongAdapter(getActivity(), this);
        binding.rv.setAdapter(songAdapter);

       /* if (Utils.EXTERNAL_FILE != null) {
            audioList.addAll(audio(Utils.EXTERNAL_FILE));
        }
        if (Utils.SD_CARD_FILE != null) {
            audioList.addAll(audio(Utils.SD_CARD_FILE));
        }
*/

        selectedList.add(audioList.get(position));

        if (audioList.size() > 0) {
            songAdapter.addAll(audioList,this,null,null);
        }

        binding.lyDetails.setOnClickListener(this);
        binding.lyDelete.setOnClickListener(this);
        binding.lyFavourite.setOnClickListener(this);
        binding.lyMove.setOnClickListener(this);
        binding.lyCopy.setOnClickListener(this);

        binding.ivExit.setOnClickListener(v -> getActivity().onBackPressed());

        return binding.getRoot();
    }

    ArrayList<File> audio(File file) {
        ArrayList<File> fileArrayList = new ArrayList<>();
        File[] files = file.listFiles();
        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    fileArrayList.addAll(audio(singleFile));
                } else {
                    if (!singleFile.isHidden() && singleFile.getName().endsWith(".mp3") || singleFile.getName().toLowerCase().endsWith(".wav")) {
                        fileArrayList.add(singleFile);
                    }
                }
            }
        }

        return fileArrayList;
    }


    @Override
    public void onFileSelect(ImageView imageView, File file) {

        if (imageView.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.ic_selected).getConstantState())) {
            selectedList.remove(file);
            imageView.setImageResource(R.drawable.no_select);
        } else {
            selectedList.add(file);
            imageView.setImageResource(R.drawable.ic_selected);
        }
        binding.tvCounter.setText(String.format("%d Selected", selectedList.size()));
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
    public void onIvSelectClick(ArrayList<File> file, int position, ImageView imageView) {


    }

    @Override
    public void onIvMediaSelectClick(ArrayList<MediaModel> files, int position, ImageView imageView) {

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
            intent.putExtra("from", "allSong");
            startActivity(intent);
        }else if (v.equals(binding.lyCopy)) {
            Intent intent = new Intent(getActivity(), SelectFileActivity.class);
            intent.putExtra("action", "copy");
            intent.putExtra("from", "allSong");
            startActivity(intent);
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
                file.delete();
            }
            dialog.dismiss();

            SongFragment.actionModeListener.onEventListener(Utils.EVENT_DELETE);

        });

        binding.btnCancel.setOnClickListener(v -> dialog.dismiss());

    }

    private void addToFavourite() {
        for (File file : selectedList) {
            DbHelper.addToFavourite(file.getAbsolutePath());
        }
        SongFragment.actionModeListener.onEventListener(Utils.EVENT_ADD_TO_FAV);
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
        isMoCo = false;
    }
}