package com.example.yinshuai.imageeditext.app;

import android.app.Application;
import android.graphics.Bitmap;

import com.example.yinshuai.imageeditext.listener.UILImageLoader;
import com.example.yinshuai.imageeditext.listener.UILPauseOnScrollListener;
import com.example.yinshuai.imageeditext.util.Util;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ThemeConfig;

/**
 * Created by yinshuai on 2017/10/27.
 */
public class APP extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Util.init(this);

        ImageLoader.getInstance().init(getConfig());
        GalleryFinal.init(coreConfig());
    }

    public ThemeConfig themeConfig() {
        ThemeConfig theme = new ThemeConfig.Builder().build();
        return theme;
    }

    public FunctionConfig functionConfig() {
        FunctionConfig functionConfig = new FunctionConfig.Builder()
                .setEnableCamera(true)
                .setEnableEdit(true)
                .setEnableCrop(true)
                .setEnableRotate(true)
                .setCropSquare(true)
                .setEnablePreview(true)
                .build();

        return functionConfig;
    }

    public CoreConfig coreConfig() {
        CoreConfig coreConfig = new CoreConfig.Builder(this, new UILImageLoader(), themeConfig())
                .setFunctionConfig(functionConfig())
//                .setPauseOnScrollListener(new UILPauseOnScrollListener(false, true))
                .build();
        return coreConfig;
    }

    public ImageLoaderConfiguration getConfig() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .memoryCacheExtraOptions(480, 800)
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 1)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .imageDownloader(new BaseImageDownloader(this))
                .imageDecoder(new BaseImageDecoder(true))
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .writeDebugLogs()
                .build();

        return config;
    }
}
