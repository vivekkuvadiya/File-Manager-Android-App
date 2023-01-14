package com.vivek.filemanager.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.vivek.filemanager.MainActivity;
import com.vivek.filemanager.R;
import com.vivek.filemanager.adapter.AppsAdapter;
import com.vivek.filemanager.databinding.FragmentAppsBinding;
import com.vivek.filemanager.interfaces.ActionModeListener;
import com.vivek.filemanager.interfaces.FileSelectedListener;
import com.vivek.filemanager.model.MediaModel;
import com.vivek.filemanager.utils.OpenFile;
import com.vivek.filemanager.utils.Utils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class AppsFragment extends Fragment implements FileSelectedListener, ActionModeListener {

    private FragmentAppsBinding binding;
    private String arg;
    private String mParam2;
    private ArrayList<File> appList = new ArrayList<>();
    private String[] apps = {"Images", "Audio", "Videos", "Zips", "Apps", "Document", "Download", "More"};
    private ArrayList<File> selectedList = new ArrayList<>();
    public static ActionModeListener actionModeListener;
    private AppsAdapter appsAdapter;


   /* @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            arg = getArguments().getString("APPS");
        }
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAppsBinding.inflate(inflater);
        Utils.setStatusBarColor(R.color.white,getActivity(),true);

        binding.rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        appsAdapter = new AppsAdapter(getActivity(), this);
        binding.rv.setAdapter(appsAdapter);

        binding.loader.setVisibility(View.VISIBLE);
        setData();

        actionModeListener = this;

        binding.ivBack.setOnClickListener(v -> getActivity().onBackPressed());

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                appsAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.et.addTextChangedListener(null);
    }

    private void setData() {

        appList.clear();

        Executors.newSingleThreadExecutor().execute(() -> {


            if (Utils.EXTERNAL_FILE != null) {
                appList.addAll(getApps(Utils.EXTERNAL_FILE));
            }
            if (Utils.SD_CARD_FILE != null) {
                appList.addAll(getApps(Utils.SD_CARD_FILE));
            }

            appsAdapter.addAll(appList,this,binding.loader,binding.noData);


//            getActivity().runOnUiThread(() -> binding.loader.setVisibility(View.GONE));
        });

    }

    ArrayList<File> getApps(File file) {
        ArrayList<File> fileArrayList = new ArrayList<>();
        File[] files = file.listFiles();
        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    fileArrayList.addAll(getApps(singleFile));
                } else {
                    if (!singleFile.isHidden() && singleFile.getName().endsWith(".apk")) {
                        fileArrayList.add(singleFile);
                    }
                }
            }

        }

        return fileArrayList;
    }


    @Override
    public void onFileSelect(ImageView imageView, File file) {
        OpenFile.open(file);
    }

    @Override
    public void onBind(ImageView imageView, File file) {
        /*if (isAvailable(file)){
            imageView.setImageResource(R.drawable.ic_selected);
        } else {
            imageView.setImageResource(R.drawable.no_select);
        }*/

    }

    @Override
    public void onIvSelectClick(ArrayList<File> files, int position, ImageView imageView) {

        Bundle bundle = new Bundle();
        bundle.putSerializable("List", (Serializable) files);
        bundle.putInt("Position", position);
        bundle.putString("From", "Apps");
        SelectAppsFragment selectAppsFragment = new SelectAppsFragment();
        selectAppsFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().add(MainActivity.MAIN_CONTAINER, selectAppsFragment).addToBackStack(null).commit();

          /*  if (!isActionModeOn){
                isActionModeOn = true;
                binding.lyToolbar.setVisibility(View.GONE);
                binding.lyActionMode.setVisibility(View.VISIBLE);
            }
        if (imageView.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.ic_selected).getConstantState())) {
            selectedList.remove(file);
            imageView.setImageResource(R.drawable.no_select);
        } else {
            selectedList.add(file);
            imageView.setImageResource(R.drawable.ic_selected);
        }
        binding.tvSelect.setText(String.format("%d Selected", selectedList.size()));*/

    }

    @Override
    public void onIvMediaSelectClick(ArrayList<MediaModel> files, int position, ImageView imageView) {

    }

    @Override
    public void onEventListener(int event) {
        switch (event) {
            case Utils.EVENT_ADD_TO_FAV:

            case Utils.EVENT_CLOSE:
                getActivity().onBackPressed();
                break;

            case Utils.EVENT_DELETE:
                setData();
                getActivity().onBackPressed();
                break;

            case Utils.EVENT_COPY:
            case Utils.EVENT_MOVE:
                setData();
                new Handler(Looper.getMainLooper()).post(() -> {
                    getActivity().onBackPressed();
                });
                break;
        }
    }
}