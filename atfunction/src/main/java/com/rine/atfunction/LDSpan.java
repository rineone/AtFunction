package com.rine.atfunction;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.style.DynamicDrawableSpan;

/**
 * 整体化@信息
 * @author rine
 * @date:2020/9/30
 */
public class LDSpan extends DynamicDrawableSpan {
    private Context context;
    private Bitmap bitmap;
    //at颜色
    private int mAtColor = 0;

    public LDSpan(Context context , String name) {
        this.context = context;
        this.bitmap = getNameBitmap(name);
    }

    public LDSpan(Context context , String name, int color) {
        this.context = context;
        this.bitmap = getNameBitmap(name);
        this.mAtColor = color;
    }

    @Override
    public Drawable getDrawable() {
        BitmapDrawable drawable = new BitmapDrawable(
                context.getResources(), bitmap);
        drawable.setBounds(0, 0,
                bitmap.getWidth(),
                bitmap.getHeight());
        return drawable;
    }


    /**
     * 把返回的人名，转换成bitmap
     * <p>
     * 比如返回@李达达
     *
     * @param name
     * @return
     */
    private Bitmap getNameBitmap(String name) {
        /* 把@相关的字符串转换成bitmap 然后使用DynamicDrawableSpan加入输入框中 */
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        if (mAtColor!=0){
            //如果没有设置颜色，则使用默认颜色
            mAtColor = context.getResources().getColor(R.color.color_blue);
        }
        //设置字体画笔的颜色
        paint.setColor(mAtColor);
        //设置字体的大小
        paint.setTextSize(50);
        Rect rect = new Rect();
        paint.getTextBounds(name, 0, name.length(), rect);
        // 获取字符串在屏幕上的长度
        int width = (int) (paint.measureText(name));
        final Bitmap bmp = Bitmap.createBitmap(width, rect.height(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
//        canvas.drawColor(getResources().getColor(R.color.color_blue));
        canvas.drawText(name, rect.left, rect.height() - rect.bottom, paint);
        return bmp;
    }

}
