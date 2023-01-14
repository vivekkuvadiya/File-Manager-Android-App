package com.vivek.filemanager.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.vivek.filemanager.databinding.FragmentStorageSpaceBinding;
import com.vivek.filemanager.utils.Utils;


public class StorageSpaceFragment extends Fragment {

    private FragmentStorageSpaceBinding binding;
    private int width;
    private int from;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            from = getArguments().getInt("FROM");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentStorageSpaceBinding.inflate(inflater);

        binding.ivBack.setOnClickListener(v -> getActivity().onBackPressed());

        ViewTreeObserver treeObserver = binding.linearLayout.getViewTreeObserver();
        treeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.linearLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int measuredHeight = binding.linearLayout.getMeasuredHeight();
                int measuredWidth = binding.linearLayout.getMeasuredWidth();

                width = measuredWidth;

                Log.d("MAINADAPER", "onBind: " + measuredHeight + "----------" + measuredWidth);

                if (from == 2)
                    StorageSpaceFragment.this.setSeekBar();
                else
                    StorageSpaceFragment.this.setSdCardSeekBar();

            }
        });

        binding.mainImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                binding.mainImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                int measuredWidth = binding.mainImage.getMeasuredWidth();
                Log.d("FILEMANHBDJBHDJ", "onBind: " + measuredWidth);
                double imgPre;
                if (from == 2) {
                    imgPre = ((double) Utils.EXT_IMAGE_SIZE * 100 / (double) Utils.TOTAL_EXTERNAL_SIZE);
                    binding.textView7.setText(Formatter.formatFileSize(getContext(), Utils.EXT_IMAGE_SIZE));
                } else {
                    imgPre = ((double) Utils.SD_IMAGE_SIZE * 100 / (double) Utils.TOTAL_SD_CARD_SIZE);
                    binding.textView7.setText(Formatter.formatFileSize(getContext(), Utils.SD_IMAGE_SIZE));
                }
                ViewGroup.LayoutParams imageLayoutParams = binding.seekImage.getLayoutParams();
                imageLayoutParams.width = (int) (measuredWidth * imgPre) / 100;
                binding.seekImage.setLayoutParams(imageLayoutParams);

            }
        });

        binding.mainAudio.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.mainAudio.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                int measuredWidth = binding.mainAudio.getMeasuredWidth();
                Log.d("FILEMANHBDJBHDJ", "onBind: " + measuredWidth);
                double pre;
                if (from == 2) {
                    pre = ((double) Utils.EXT_AUDIO_SIZE * 100 / (double) Utils.TOTAL_EXTERNAL_SIZE);
                    binding.textView9.setText(Formatter.formatFileSize(getContext(), Utils.EXT_AUDIO_SIZE));
                } else {
                    pre = ((double) Utils.SD_AUDIO_SIZE * 100 / (double) Utils.TOTAL_SD_CARD_SIZE);
                    binding.textView9.setText(Formatter.formatFileSize(getContext(), Utils.SD_AUDIO_SIZE));
                }
                ViewGroup.LayoutParams layoutParams = binding.seekAudio.getLayoutParams();
                layoutParams.width = (int) (measuredWidth * pre) / 100;
                binding.seekAudio.setLayoutParams(layoutParams);

            }
        });

        binding.mainVideo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                binding.mainVideo.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                int measuredWidth = binding.mainVideo.getMeasuredWidth();
                Log.d("FILEMANHBDJBHDJ", "onBind: " + measuredWidth);
                double pre;
                if (from == 2) {
                    pre = ((double) Utils.EXT_VIDEO_SIZE * 100 / (double) Utils.TOTAL_EXTERNAL_SIZE);
                    binding.textView12.setText(Formatter.formatFileSize(getContext(), Utils.EXT_VIDEO_SIZE));
                } else {
                    pre = ((double) Utils.SD_VIDEO_SIZE * 100 / (double) Utils.TOTAL_SD_CARD_SIZE);
                    binding.textView12.setText(Formatter.formatFileSize(getContext(), Utils.SD_VIDEO_SIZE));
                }
                ViewGroup.LayoutParams layoutParams = binding.seekVideo.getLayoutParams();
                layoutParams.width = (int) (measuredWidth * pre) / 100;
                binding.seekVideo.setLayoutParams(layoutParams);

            }
        });

        binding.mainZips.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                binding.mainZips.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                int measuredWidth = binding.mainZips.getMeasuredWidth();
                Log.d("FILEMANHBDJBHDJ", "onBind: " + measuredWidth);
                double pre;
                if (from == 2) {
                    pre = ((double) Utils.EXT_ZIPS_SIZE * 100 / (double) Utils.TOTAL_EXTERNAL_SIZE);
                    binding.textView14.setText(Formatter.formatFileSize(getContext(), Utils.EXT_ZIPS_SIZE));
                } else {
                    pre = ((double) Utils.SD_ZIPS_SIZE * 100 / (double) Utils.TOTAL_SD_CARD_SIZE);
                    binding.textView14.setText(Formatter.formatFileSize(getContext(), Utils.SD_ZIPS_SIZE));
                }
                ViewGroup.LayoutParams layoutParams = binding.seekZips.getLayoutParams();
                layoutParams.width = (int) (measuredWidth * pre) / 100;
                binding.seekZips.setLayoutParams(layoutParams);

            }
        });

        binding.mainApps.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                binding.mainApps.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                int measuredWidth = binding.mainApps.getMeasuredWidth();
                Log.d("FILEMANHBDJBHDJ", "onBind: " + measuredWidth);
                double pre;
                if (from == 2) {
                    pre = ((double) Utils.EXT_APPS_SIZE * 100 / (double) Utils.TOTAL_EXTERNAL_SIZE);
                    binding.textView16.setText(Formatter.formatFileSize(getContext(), Utils.EXT_APPS_SIZE));
                } else {
                    pre = ((double) Utils.SD_APPS_SIZE * 100 / (double) Utils.TOTAL_SD_CARD_SIZE);
                    binding.textView16.setText(Formatter.formatFileSize(getContext(), Utils.SD_APPS_SIZE));
                }
                ViewGroup.LayoutParams layoutParams = binding.seekApps.getLayoutParams();
                layoutParams.width = (int) (measuredWidth * pre) / 100;
                binding.seekApps.setLayoutParams(layoutParams);

            }
        });

        binding.mainDoc.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                binding.mainDoc.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                int measuredWidth = binding.mainDoc.getMeasuredWidth();
                Log.d("FILEMANHBDJBHDJ", "onBind: " + measuredWidth);
                double pre;
                if (from == 2) {
                    pre = ((double) Utils.EXT_DOCUMENT_SIZE * 100 / (double) Utils.TOTAL_EXTERNAL_SIZE);
                    binding.textView18.setText(Formatter.formatFileSize(getContext(), Utils.EXT_DOCUMENT_SIZE));
                } else {
                    pre = ((double) Utils.SD_DOCUMENT_SIZE * 100 / (double) Utils.TOTAL_SD_CARD_SIZE);
                    binding.textView18.setText(Formatter.formatFileSize(getContext(), Utils.SD_DOCUMENT_SIZE));
                }

                ViewGroup.LayoutParams layoutParams = binding.seekDoc.getLayoutParams();
                layoutParams.width = (int) (measuredWidth * pre) / 100;
                binding.seekDoc.setLayoutParams(layoutParams);

            }
        });

        binding.mainOther.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                binding.mainOther.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                int measuredWidth = binding.mainOther.getMeasuredWidth();

                Log.d("FILEMANHBDJBHDJ", "onBind: " + measuredWidth);
                double pre;
                if (from == 2) {
                    long others = Utils.TOTAL_EXTERNAL_SIZE - (Utils.EXT_IMAGE_SIZE + Utils.EXT_AUDIO_SIZE + Utils.EXT_VIDEO_SIZE + Utils.EXT_ZIPS_SIZE + Utils.EXT_APPS_SIZE + Utils.EXT_DOCUMENT_SIZE + Utils.TOTAL_AVAILABLE_EXTERNAL_SIZE);
                    pre = ((double) others * 100 / (double) Utils.TOTAL_EXTERNAL_SIZE);
                    binding.textView20.setText(Formatter.formatFileSize(getContext(), others));

                } else {
                    long others = Utils.TOTAL_SD_CARD_SIZE - (Utils.SD_IMAGE_SIZE + Utils.SD_AUDIO_SIZE + Utils.SD_VIDEO_SIZE + Utils.SD_ZIPS_SIZE + Utils.SD_APPS_SIZE + Utils.SD_DOCUMENT_SIZE + Utils.TOTAL_AVAILABLE_SD_CARD_SIZE);
                    pre = ((double) others * 100 / (double) Utils.TOTAL_SD_CARD_SIZE);
                    binding.textView20.setText(Formatter.formatFileSize(getContext(), others));
                }
                ViewGroup.LayoutParams layoutParams = binding.seekOther.getLayoutParams();
                layoutParams.width = (int) (measuredWidth * pre) / 100;
                binding.seekOther.setLayoutParams(layoutParams);

            }
        });

        return binding.getRoot();
    }

    void setSeekBar() {
        long others = Utils.TOTAL_EXTERNAL_SIZE - (Utils.EXT_IMAGE_SIZE + Utils.EXT_AUDIO_SIZE + Utils.EXT_VIDEO_SIZE + Utils.EXT_ZIPS_SIZE + Utils.EXT_APPS_SIZE + Utils.EXT_DOCUMENT_SIZE + Utils.TOTAL_AVAILABLE_EXTERNAL_SIZE);

        double freePre = ((double) Utils.TOTAL_AVAILABLE_EXTERNAL_SIZE * 100 / (double) Utils.TOTAL_EXTERNAL_SIZE);
        double imgPre = ((double) Utils.EXT_IMAGE_SIZE * 100 / (double) Utils.TOTAL_EXTERNAL_SIZE);
        double audPre = ((double) Utils.EXT_AUDIO_SIZE * 100 / (double) Utils.TOTAL_EXTERNAL_SIZE);
        double vidPre = ((double) Utils.EXT_VIDEO_SIZE * 100 / (double) Utils.TOTAL_EXTERNAL_SIZE);
        double zipPre = ((double) Utils.EXT_ZIPS_SIZE * 100 / (double) Utils.TOTAL_EXTERNAL_SIZE);
        double appPre = ((double) Utils.EXT_APPS_SIZE * 100 / (double) Utils.TOTAL_EXTERNAL_SIZE);
        double docPre = ((double) Utils.EXT_DOCUMENT_SIZE * 100 / (double) Utils.TOTAL_EXTERNAL_SIZE);
        double otherPre = ((double) others * 100 / (double) Utils.TOTAL_EXTERNAL_SIZE);

        binding.textView4.setText(String.format("Used %s / %s", Formatter.formatFileSize(getActivity(), Utils.TOTAL_AVAILABLE_EXTERNAL_SIZE), Formatter.formatFileSize(getActivity(), Utils.TOTAL_EXTERNAL_SIZE)));
        Log.d("STORAGEFRAG", "" + width);


        ViewGroup.LayoutParams params = binding.prImage.getLayoutParams();
        params.width = (int) (width * imgPre) / 100;
        binding.prImage.setLayoutParams(params);

        ViewGroup.LayoutParams paramsAudio = binding.prAudio.getLayoutParams();
        paramsAudio.width = (int) (width * audPre) / 100;
        binding.prAudio.setLayoutParams(paramsAudio);

        ViewGroup.LayoutParams paramsVideo = binding.prVideo.getLayoutParams();
        paramsVideo.width = (int) (width * vidPre) / 100;
        binding.prVideo.setLayoutParams(paramsVideo);

        ViewGroup.LayoutParams paramsZips = binding.prZips.getLayoutParams();
        paramsZips.width = (int) (width * zipPre) / 100;
        binding.prZips.setLayoutParams(paramsZips);

        ViewGroup.LayoutParams paramsApps = binding.prApps.getLayoutParams();
        paramsApps.width = (int) (width * appPre) / 100;
        binding.prApps.setLayoutParams(paramsApps);

        ViewGroup.LayoutParams paramsDoc = binding.prDocument.getLayoutParams();
        paramsDoc.width = (int) (width * docPre) / 100;
        binding.prDocument.setLayoutParams(paramsDoc);

        ViewGroup.LayoutParams paramsOthers = binding.prOthers.getLayoutParams();
        paramsOthers.width = (int) (width * otherPre) / 100;
        binding.prOthers.setLayoutParams(paramsOthers);

        ViewGroup.LayoutParams paramsFree = binding.prBlank.getLayoutParams();
        paramsFree.width = (int) (width * freePre) / 100;
        binding.prBlank.setLayoutParams(paramsFree);

        binding.textView7.setText(Formatter.formatFileSize(getContext(), Utils.EXT_IMAGE_SIZE));
        binding.textView9.setText(Formatter.formatFileSize(getContext(), Utils.EXT_AUDIO_SIZE));
        binding.textView12.setText(Formatter.formatFileSize(getContext(), Utils.EXT_VIDEO_SIZE));
        binding.textView14.setText(Formatter.formatFileSize(getContext(), Utils.EXT_ZIPS_SIZE));
        binding.textView16.setText(Formatter.formatFileSize(getContext(), Utils.EXT_APPS_SIZE));
        binding.textView18.setText(Formatter.formatFileSize(getContext(), Utils.EXT_DOCUMENT_SIZE));
        binding.textView20.setText(Formatter.formatFileSize(getContext(), others));

    }

    void setSdCardSeekBar() {

        long sdOthers = Utils.TOTAL_SD_CARD_SIZE - (Utils.SD_IMAGE_SIZE + Utils.SD_AUDIO_SIZE + Utils.SD_VIDEO_SIZE + Utils.SD_ZIPS_SIZE + Utils.SD_APPS_SIZE + Utils.SD_DOCUMENT_SIZE + Utils.TOTAL_AVAILABLE_SD_CARD_SIZE);

        double sdFreePre = ((double) Utils.TOTAL_AVAILABLE_SD_CARD_SIZE * 100 / (double) Utils.TOTAL_SD_CARD_SIZE);
        double sdImgPre = ((double) Utils.SD_IMAGE_SIZE * 100 / (double) Utils.TOTAL_SD_CARD_SIZE);
        double sdAudPre = ((double) Utils.SD_AUDIO_SIZE * 100 / (double) Utils.TOTAL_SD_CARD_SIZE);
        double sdVidPre = ((double) Utils.SD_VIDEO_SIZE * 100 / (double) Utils.TOTAL_SD_CARD_SIZE);
        double sdZipPre = ((double) Utils.SD_ZIPS_SIZE * 100 / (double) Utils.TOTAL_SD_CARD_SIZE);
        double sdAppPre = ((double) Utils.SD_APPS_SIZE * 100 / (double) Utils.TOTAL_SD_CARD_SIZE);
        double sdDocPre = ((double) Utils.SD_DOCUMENT_SIZE * 100 / (double) Utils.TOTAL_SD_CARD_SIZE);
        double sdOtherPre = ((double) sdOthers * 100 / (double) Utils.TOTAL_SD_CARD_SIZE);

        binding.textView4.setText(String.format("Used %s / %s", Formatter.formatFileSize(getActivity(), Utils.TOTAL_AVAILABLE_SD_CARD_SIZE), Formatter.formatFileSize(getActivity(), Utils.TOTAL_SD_CARD_SIZE)));

        ViewGroup.LayoutParams params = binding.prImage.getLayoutParams();
        params.width = (int) (width * sdImgPre) / 100;
        binding.prImage.setLayoutParams(params);

        ViewGroup.LayoutParams paramsAudio = binding.prAudio.getLayoutParams();
        paramsAudio.width = (int) (width * sdAudPre) / 100;
        binding.prAudio.setLayoutParams(paramsAudio);

        ViewGroup.LayoutParams paramsVideo = binding.prVideo.getLayoutParams();
        paramsVideo.width = (int) (width * sdVidPre) / 100;
        binding.prVideo.setLayoutParams(paramsVideo);

        ViewGroup.LayoutParams paramsZips = binding.prZips.getLayoutParams();
        paramsZips.width = (int) (width * sdZipPre) / 100;
        binding.prZips.setLayoutParams(paramsZips);

        ViewGroup.LayoutParams paramsApps = binding.prApps.getLayoutParams();
        paramsApps.width = (int) (width * sdAppPre) / 100;
        binding.prApps.setLayoutParams(paramsApps);

        ViewGroup.LayoutParams paramsDoc = binding.prDocument.getLayoutParams();
        paramsDoc.width = (int) (width * sdDocPre) / 100;
        binding.prDocument.setLayoutParams(paramsDoc);

        ViewGroup.LayoutParams paramsOthers = binding.prOthers.getLayoutParams();
        paramsOthers.width = (int) (width * sdOtherPre) / 100;
        binding.prOthers.setLayoutParams(paramsOthers);

        ViewGroup.LayoutParams paramsFree = binding.prBlank.getLayoutParams();
        paramsFree.width = (int) (width * sdFreePre) / 100;
        binding.prBlank.setLayoutParams(paramsFree);

        binding.textView7.setText(Formatter.formatFileSize(getContext(), Utils.SD_IMAGE_SIZE));
        binding.textView9.setText(Formatter.formatFileSize(getContext(), Utils.SD_AUDIO_SIZE));
        binding.textView12.setText(Formatter.formatFileSize(getContext(), Utils.SD_VIDEO_SIZE));
        binding.textView14.setText(Formatter.formatFileSize(getContext(), Utils.SD_ZIPS_SIZE));
        binding.textView16.setText(Formatter.formatFileSize(getContext(), Utils.SD_APPS_SIZE));
        binding.textView18.setText(Formatter.formatFileSize(getContext(), Utils.SD_DOCUMENT_SIZE));
        binding.textView20.setText(Formatter.formatFileSize(getContext(), sdOthers));


    }
}