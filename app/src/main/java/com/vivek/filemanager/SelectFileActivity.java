package com.vivek.filemanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.vivek.filemanager.databinding.ActivitySelectFileBinding;
import com.vivek.filemanager.fragment.SelectStorageFileFragment;
import com.vivek.filemanager.utils.Utils;

public class SelectFileActivity extends AppCompatActivity {

    private ActivitySelectFileBinding binding;
    public static int SELECT_FILE_CON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_select_file);

        SELECT_FILE_CON = binding.container.getId();

        if (Utils.IS_SD_CARD_EXIST){
            binding.constraintLayout4.setVisibility(View.VISIBLE);
        }else {
            binding.constraintLayout4.setVisibility(View.GONE);
        }

        binding.constraintLayout2.setOnClickListener(v -> {

            Bundle bundle = new Bundle();
            bundle.putString("path",Utils.EXTERNAL_FILE.getAbsolutePath());
            bundle.putString("action",getIntent().getStringExtra("action"));
            bundle.putString("from",getIntent().getStringExtra("from"));
            SelectStorageFileFragment selectStorageFileFragment = new SelectStorageFileFragment();
            selectStorageFileFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(binding.container.getId(),selectStorageFileFragment).addToBackStack(null).commit();

        });

        binding.constraintLayout4.setOnClickListener(v -> {

            Bundle bundle = new Bundle();
            bundle.putString("path",Utils.SD_CARD_FILE.getAbsolutePath());
            bundle.putString("action",getIntent().getStringExtra("action"));
            bundle.putString("from",getIntent().getStringExtra("from"));
            SelectStorageFileFragment selectStorageFileFragment = new SelectStorageFileFragment();
            selectStorageFileFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(binding.container.getId(),selectStorageFileFragment).addToBackStack(null).commit();

        });

        binding.ivClose.setOnClickListener(v -> onBackPressed());

    }
}