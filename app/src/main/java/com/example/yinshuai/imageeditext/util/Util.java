package com.example.yinshuai.imageeditext.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by yinshuai on 2017/10/27.
 */
public class Util {
    public static final int SCENE_WIDTH = 1;
    public static final int SCENE_HEIGHT = 2;
    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    public static int getScene(int paramete) {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (paramete == SCENE_WIDTH) {
            return wm.getDefaultDisplay().getWidth();
        } else if (paramete == SCENE_HEIGHT) {
            return wm.getDefaultDisplay().getHeight();
        }
        return 0;
    }
}
