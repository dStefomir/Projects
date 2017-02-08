package com.securegroup.githubapp.utils;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.securegroup.githubapp.R;

/**
 * Utility class that sets the options of the Universal Image Loader
 * <p>
 *
 * @author Stefomir Dimitrov </p>
 */

public class ImageUtils {

    public static DisplayImageOptions getImageLoaderOptions() {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_person_black_36dp)
                .showImageForEmptyUri(R.drawable.ic_person_black_36dp)
                .showImageOnFail(android.R.drawable.stat_notify_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(20))
                .build();
    }
}
