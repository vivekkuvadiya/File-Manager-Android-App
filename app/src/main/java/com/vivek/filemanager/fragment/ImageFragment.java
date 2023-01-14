package com.vivek.filemanager.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vivek.filemanager.R;
import com.vivek.filemanager.databinding.FragmentImageBinding;
import com.vivek.filemanager.utils.Utils;

import java.util.ArrayList;

public class ImageFragment extends Fragment {

    private FragmentImageBinding binding;
    private String argPath;
    private String[] apps = {"Images", "Audio", "Videos", "Zips", "Apps", "Document", "Download", "More"};
    public static int CON;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            argPath = getArguments().getString("FILE");
        }
        Utils.setStatusBarColor(R.color.white,getActivity(),true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentImageBinding.inflate(inflater);
        CON = binding.container.getId();
        ImagePagerAdapter pagerAdapter = new ImagePagerAdapter(getChildFragmentManager());

        if (argPath.equals(apps[0])){
            binding.tvTitle.setText(apps[0]);
            pagerAdapter.addFragment(new AllImageFragment(),"Image");
            pagerAdapter.addFragment(new ImageFolderFragment(),"Album");
        }else if (argPath.equals(apps[1])){
            binding.tvTitle.setText(apps[1]);
            pagerAdapter.addFragment(new SongFragment(),"Song");
            pagerAdapter.addFragment(new SongFolderFragment(),"Albums");
        }else if (argPath.equals(apps[2])){
            binding.tvTitle.setText(apps[2]);
            pagerAdapter.addFragment(new VideoFragment(),"Video");
            pagerAdapter.addFragment(new VideoFolderFragment(),"Folders");
        }else if (argPath.equals(apps[3])){
            binding.tvTitle.setText(apps[3]);

        }else if (argPath.equals(apps[4])){
            binding.tvTitle.setText(apps[4]);

        }else if (argPath.equals(apps[5])){
            binding.tvTitle.setText(apps[5]);

        }else if (argPath.equals(apps[6])){
            binding.tvTitle.setText(apps[6]);

        }else if (argPath.equals(apps[7])){
            binding.tvTitle.setText(apps[7]);
        }

        binding.viewPager.setAdapter(pagerAdapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        binding.ivBack.setOnClickListener(v -> getActivity().onBackPressed());

        return binding.getRoot();
    }

    private class ImagePagerAdapter extends FragmentPagerAdapter{

        ArrayList<Fragment> fragmentList = new ArrayList<>();
        ArrayList<String> stringList = new ArrayList<>();

        public ImagePagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title){
            fragmentList.add(fragment);
            stringList.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return stringList.get(position);
        }
    }
}