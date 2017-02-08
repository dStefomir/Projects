package com.securegroup.githubapp.basecomponents;

import android.app.Application;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Application class used to initialize the DbFlow database and the Universal Image Loader instance
 * <p>
 *
 * @author Stefomir Dimitrov </p>
 */
public class GitHubApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initDbFlow();
        initImageLoader();
    }


    private void initDbFlow() {
        FlowManager.init(
                new FlowConfig.Builder(this).build()
        );
    }

    private void initImageLoader() {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(this);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); //50 Mbs
        config.tasksProcessingOrder(QueueProcessingType.LIFO);

        ImageLoader.getInstance().init(config.build());
    }
}
