package com.example.yinshuai.imageeditext.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yinshuai.imageeditext.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yinshuai on 2017/8/14.
 *
 * @author yinshuai
 */
public class EditTextPlus extends EditText {
    /**
     * 最大输入字符
     */
    public static final int MAXLENGTH = 2000;

    /**
     * 一张图片所占的字符长度
     */
    public static final int IMAGELENGTH = 2;
    /**
     * 占位符
     */
    private String placeholder = "&";

    /**
     * 最大添加图片数量
     */
    private int maxImage = 8;
    private Context mContext;

    private String submitCon = "";
    private boolean insertImage = false;

    private OnInsertionImageListener onInsertionImageListener;
    private OnDeleteConteneListener onDeleteConteneListener;

    private float startY;
    private float startX;
    private float selectionStart;

    private List<String> image = new ArrayList<>();

    public EditTextPlus(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public EditTextPlus(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public void init() {
        setGravity(Gravity.TOP);
        addTextChangedListener(watcher);
    }

    public interface OnInsertionImageListener {
        /**
         * 插入图片时的监听
         */
        void insertion();
    }

    public void setOnInsertionImageListener(OnInsertionImageListener onInsertionImageListener) {
        this.onInsertionImageListener = onInsertionImageListener;
    }

    public interface OnDeleteConteneListener {
        /**
         * 删除图片的监听
         */
        void delete();
    }

    public void setOnDeleteConteneListener(OnDeleteConteneListener onDeleteConteneListener) {
        this.onDeleteConteneListener = onDeleteConteneListener;
    }


    /**
     * 添加图片集合
     *
     * @param list
     */
    public void addImage(List<String> list) {
        if (getTextContent().length() + IMAGELENGTH > MAXLENGTH) {
            Toast.makeText(mContext, "输入的内容超过最大限制", Toast.LENGTH_SHORT).show();
            return;
        }
        Editable editable = getText();
        for (int i = 0; i < list.size(); i++) {
            if (getImage().size() >= maxImage) {
                Toast.makeText(mContext, "图片超过最大数量", Toast.LENGTH_SHORT).show();
                return;
            }
            if (list.get(i) != null && !TextUtils.isEmpty(list.get(i))) {
                if (!TextUtils.isEmpty(getText().toString()) && !insertImage) {
                    //如果第一张就是图片不用换行
                    editable.insert(getSelectionStart(), "\n");
                } else if (getSelectionStart() < getText().length()) {
                    //当从中间插入时
                    editable.insert(getSelectionStart(), "\n");
                }
                CharSequence sequence = getDrawableStr(list.get(i));
                if (sequence != null) {
                    image.add(list.get(i));
                    editable.insert(getSelectionStart(), sequence);
                    editable.insert(getSelectionStart(), "\n");
                    insertImage = true;
                }
            } else {
                Toast.makeText(mContext, "图片路径为空", Toast.LENGTH_SHORT).show();
            }
        }
        //让光标始终在最后
        this.setSelection(getText().toString().length());
        if (onInsertionImageListener != null) {
            onInsertionImageListener.insertion();
        }
    }


    /**
     * 获取插入的图片列表
     *
     * @return
     */
    public List<String> getImage() {
        List<String> picPaths = new ArrayList<>();
        String content = this.getText().toString();
        for (int i = 0; i < image.size(); i++) {
            if (content.indexOf(image.get(i)) != -1) {
                picPaths.add(image.get(i));
            }
        }
        return picPaths;
    }

    public boolean isImage(String content) {
        for (int i = 0; i < image.size(); i++) {
            if (content.indexOf(image.get(i)) != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取去除image后的文字内容
     *
     * @return
     */
    public String getTextContent() {
        return submitCon;
    }


    private String tempString;
    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            insertImage = false;
            //如果小于就是删除操作
            if (s.length() < tempString.length()) {
                String deletString = tempString.substring(start, start + before);
                if (image != null && image.size() > 0) {
                    for (int i = 0; i < image.size(); i++) {
                        //如果删除的内容中包含这张图片 那么就把图片集合中的对应的图片删除
                        if (deletString.toString().indexOf(image.get(i)) != -1) {
                            image.remove(i);
                            if (onDeleteConteneListener != null) {
                                onDeleteConteneListener.delete();
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            tempString = s.toString();
        }

        @Override
        public void afterTextChanged(Editable s) {
            invalidate();
            requestLayout();

            StringBuffer stringBuffer = new StringBuffer(getText().toString());
            for (int i = 0; i < image.size(); i++) {
                if (stringBuffer.indexOf(image.get(i)) != -1) {
                    int index = stringBuffer.indexOf(image.get(i));
                    stringBuffer.delete(index - 10, index + image.get(i).length() + 3);
                    stringBuffer.insert(index - 10, placeholder);
                }
            }

            if (stringBuffer.toString().indexOf(placeholder) == 0) {
                stringBuffer.insert(0, " ");
            }
            submitCon = stringBuffer.toString();
        }


    };

    /**
     * 编辑插入的内容
     *
     * @param picPath
     * @return
     */
    private CharSequence getDrawableStr(String picPath) {
        String str = "<img src=\"" + picPath + "\"/>";
        Bitmap bm = createImageThumbnail(picPath);
        final SpannableString ss = new SpannableString(str);
        // 定义插入图片
        Drawable drawable = new BitmapDrawable(bm);

        float scenewidth = Util.getScene(Util.SCENE_WIDTH) / 3;
        float width = drawable.getIntrinsicWidth();
        float height = drawable.getIntrinsicHeight();
        if (width > scenewidth) {
            width = width - 20;
            height = height - 20;
        } else {
            float scale = (scenewidth) / width;
            width *= scale;
            height *= scale;
        }

        //设置图片的宽高
        drawable.setBounds(2, 0, (int) width, (int) height);
        //ALIGN_BOTTOM 调整图片距离字有一定的间隙
        VerticalCenterImageSpan span = new VerticalCenterImageSpan(drawable, 1);
        //SPAN_INCLUSIVE_EXCLUSIVE 会导致删除后面的文字消失
        ss.setSpan(span, 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        /*
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE：前后都不包括，即在指定范围的前面和后面插入新字符都不会应用新样式
        Spannable.SPAN_EXCLUSIVE_INCLUSIVE：前面不包括，后面包括。即仅在范围字符的后面插入新字符时会应用新样式
        Spannable.SPAN_INCLUSIVE_EXCLUSIVE：前面包括，后面不包括。
        Spannable.SPAN_INCLUSIVE_INCLUSIVE：前后都包括。
         */
        return ss;
    }

    public class VerticalCenterImageSpan extends ImageSpan {

        public VerticalCenterImageSpan(Drawable d, int verticalAlignment) {
            super(d, verticalAlignment);
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            Drawable b = getDrawable();
            canvas.save();

            int transY = bottom - b.getBounds().bottom;
            if (mVerticalAlignment == ALIGN_BASELINE) {
                transY -= paint.getFontMetricsInt().descent;
            } else if (mVerticalAlignment == ALIGN_BOTTOM) {

            } else {
                transY += paint.getFontMetricsInt().descent * 2;
            }

            canvas.translate(x, transY);
            b.draw(canvas);
            canvas.restore();
        }
    }

    public static Bitmap createImageThumbnail(String filePath) {
        Bitmap bitmap = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inTempStorage = new byte[100 * 1024];
        // 默认是Bitmap.Config.ARGB_8888
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        opts.inSampleSize = 2;
        try {
            bitmap = BitmapFactory.decodeFile(filePath, opts);
        } catch (Exception e) {
        }
        return bitmap;
    }


    /**
     * 重写dispatchTouchEvent是为了解决上下滑动时光标跳跃的问题
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = event.getRawY();
                startX = event.getRawX();
                selectionStart = getSelectionStart();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                float endY = event.getRawY();
                float endX = event.getRawX();

                if (Math.abs(endY - startY) > 10 || Math.abs(endX - startX) > 10) {
                    return true;
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }
}
