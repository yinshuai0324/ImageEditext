package com.example.yinshuai.imageeditext;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.yinshuai.imageeditext.view.EditTextPlus;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * @author yinshuai
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int REQUEST_CODE_CAMERA = 1000;
    private final int REQUEST_CODE_GALLERY = 1001;
    private final int REQUEST_CODE_CROP = 1002;
    private final int REQUEST_CODE_EDIT = 1003;

    public EditTextPlus editText;
    public Button getImage;
    public ImageView addImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void init() {
        getImage = (Button) findViewById(R.id.getimage);
        editText = (EditTextPlus) findViewById(R.id.edit_text);
        addImage = (ImageView) findViewById(R.id.addimage);

        addImage.setOnClickListener(this);
        getImage.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addimage:
                //带配置
                FunctionConfig config = new FunctionConfig.Builder()
                        .setMutiSelectMaxSize(8)
                        .build();
                GalleryFinal.openGalleryMuti(REQUEST_CODE_GALLERY, config, mOnHanlderResultCallback);
                break;
            case R.id.getimage:
                if (editText != null) {
                    Log.e("-----", "获取插入的图片集合:");
                    for (String path : editText.getImage()) {
                        Log.e("-----", "" + path + "\n");
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 选择照片的回调
     */
    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                List<String> data = new ArrayList<>();
                for (PhotoInfo p : resultList) {
                    data.add(p.getPhotoPath());
                    Log.e("----", "" + p.getPhotoPath());
                }
                editText.addImage(data);
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };
}
