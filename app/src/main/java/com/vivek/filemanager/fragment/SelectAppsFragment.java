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
import com.vivek.filemanager.adapter.AppsAdapter;
import com.vivek.filemanager.adapter.DocumentAdapter;
import com.vivek.filemanager.adapter.DownloadAdapter;
import com.vivek.filemanager.adapter.ZipAdapter;
import com.vivek.filemanager.databinding.DialogDeleteBinding;
import com.vivek.filemanager.databinding.DialogFileInfoBinding;
import com.vivek.filemanager.databinding.FragmentSelectAppsBinding;
import com.vivek.filemanager.db.DbHelper;
import com.vivek.filemanager.interfaces.ActionModeListener;
import com.vivek.filemanager.interfaces.FileSelectedListener;
import com.vivek.filemanager.model.MediaModel;
import com.vivek.filemanager.utils.Utils;

import java.io.File;
import java.util.ArrayList;

import static java.lang.String.format;


public class SelectAppsFragment extends Fragment implements FileSelectedListener, View.OnClickListener, ActionModeListener {

    private FragmentSelectAppsBinding binding;
    private ArrayList<File> files = new ArrayList<>();
    private int position;
    public static ArrayList<File> selectedList = new ArrayList<>();
    public static boolean isActionModeOn = true;
    private String from;
    ActionModeListener listener;
    public static ActionModeListener actionModeListener;
    private boolean isMoCo = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            files = (ArrayList<File>) getArguments().getSerializable("List");
            position = getArguments().getInt("Position");
            from = getArguments().getString("From");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isMoCo){
            listener.onEventListener(Utils.EVENT_COPY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSelectAppsBinding.inflate(inflater);

        actionModeListener = this;

        binding.rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        selectedList.add(files.get(position));

        if (from.equals("Apps")) {
            listener = AppsFragment.actionModeListener;
            AppsAdapter appsAdapter = new AppsAdapter(getActivity(), this);
            binding.rv.setAdapter(appsAdapter);
            appsAdapter.addAll(files,this,null,null);
        } else if (from.equals("Doc")) {
            listener = DocumentFragment.actionModeListener;
            DocumentAdapter docAdapter = new DocumentAdapter(getActivity(), this);
            binding.rv.setAdapter(docAdapter);
            docAdapter.addAll(files,this,null,null);
        } else if (from.equals("Dow")) {
            listener = DownloadFragment.actionModeListener;
            DownloadAdapter downloadAdapter = new DownloadAdapter(getActivity(), this);
            binding.rv.setAdapter(downloadAdapter);
            downloadAdapter.addAll(files,this,null,null);
        } else if (from.equals("Zip")) {
            listener = ZipFragment.actionModeListener;
            ZipAdapter zipAdapter = new ZipAdapter(getActivity(), this);
            binding.rv.setAdapter(zipAdapter);
            zipAdapter.addAll(files,this,null,null);
        } else if (from.equals("Large")){
            listener = LargeFileFragment.actionModeListener;
            DownloadAdapter downloadAdapter = new DownloadAdapter(getActivity(), this);
            binding.rv.setAdapter(downloadAdapter);
            downloadAdapter.addAll(files,this,null,null);
        }
        binding.rv.scrollToPosition(position);

        binding.lyFavourite.setOnClickListener(this);
        binding.ivClose.setOnClickListener(this);
        binding.lyDetails.setOnClickListener(this);
        binding.lyDelete.setOnClickListener(this);
        binding.lyCopy.setOnClickListener(this);
        binding.lyMove.setOnClickListener(this);

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
        if (imageView.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.ic_selected).getConstantState())) {
            selectedList.remove(files.get(position));
            imageView.setImageResource(R.drawable.no_select);
        } else {
            selectedList.add(files.get(position));
            imageView.setImageResource(R.drawable.ic_selected);
        }
        binding.tvCounter.setText(format("%d Selected", selectedList.size()));
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
        if (v.equals(binding.lyFavourite)) {
            if (selectedList.size() > 0) {
                for (File file : selectedList) {
                    DbHelper.addToFavourite(file.getAbsolutePath());
                }
            }
            getActivity().onBackPressed();
//            listener.onEventListener(Utils.EVENT_ADD_TO_FAV);
        } else if (v.equals(binding.ivClose)) {
            listener.onEventListener(Utils.EVENT_CLOSE);
        } else if (v.equals(binding.lyDetails)) {
            /*listener.onEventListener(Utils.EVENT_DETAILS);*/
            showInfoDialog();
        } else if (v.equals(binding.lyDelete)) {
            if (selectedList.size() > 0) {
                showDeleteDialog();
            }
        } else if (v.equals(binding.lyCopy)) {
            Intent intent = new Intent(getActivity(), SelectFileActivity.class);
            intent.putExtra("action", "copy");
            intent.putExtra("from", from);
            startActivity(intent);

        } else if (v.equals(binding.lyMove)) {
            Intent intent = new Intent(getActivity(), SelectFileActivity.class);
            intent.putExtra("action", "move");
            intent.putExtra("from", from);
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
            dialogFileInfoBinding.tvDate.setText(format("%d Files", selectedList.size()));
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

            listener.onEventListener(Utils.EVENT_DELETE);
        });

        binding.btnCancel.setOnClickListener(v -> dialog.dismiss());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        selectedList.clear();
        isMoCo = false;
    }

    @Override
    public void onEventListener(int event) {
        if (event == Utils.EVENT_COPY) {
            isMoCo = true;
//            listener.onEventListener(Utils.EVENT_COPY);

        } else if (event == Utils.EVENT_MOVE) {
            isMoCo = true;
//            listener.onEventListener(Utils.EVENT_MOVE);
        }
    }


}