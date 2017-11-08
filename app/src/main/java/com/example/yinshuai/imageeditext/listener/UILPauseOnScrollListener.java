package com.example.yinshuai.imageeditext.listener;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Desction:
 * Author:pengjianbo
 * Date:2016/1/9 0009 18:47
 */
public class UILPauseOnScrollListener extends PauseOnScrollListener {

    public UILPauseOnScrollListener(boolean pauseOnScroll, boolean pauseOnFling) {
        super(pauseOnScroll, pauseOnFling);
    }

    @Override
    public void resume() {
        ImageLoader.getInstance().resume();
    }

    @Override
    public void pause() {
        ImageLoader.getInstance().pause();
    }
}
