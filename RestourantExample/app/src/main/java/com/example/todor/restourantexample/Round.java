package com.example.todor.restourantexample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created by todor on 07.08.14.
 */
public class Round {

    //static int borderWidth = 5;
    //static float roundPx = 20;
    public static Bitmap getRoundedCornerImage(Bitmap bitmap, int width, int height, int borderWidth, int roundPx) {
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        //final int color = 0xff424242;
        final int color = 0xff000000;
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Rect rect2;


        if (bitmap.getWidth()*height > bitmap.getHeight()*width) {
            int realHeight = bitmap.getHeight()*width/bitmap.getWidth();
            rect2 = new Rect(0, (height - realHeight)/2, width, (realHeight + height)/2);
        }
        else {
            int realWidth = bitmap.getWidth()*height/bitmap.getHeight();
            rect2 = new Rect ((width - realWidth)/2, 0, (realWidth + width)/2, height);
        }

        RectF rectF = new RectF(rect2.left + 1, rect2.top + 1, rect2.right - 1, rect2.bottom - 1);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect2, paint);

        Paint paintFrame = new Paint();
        paintFrame.setAntiAlias(true);
        paintFrame.setColor(0xffffffff);
        paintFrame.setStrokeWidth(borderWidth);
        paintFrame.setStyle(Paint.Style.STROKE);
        rect = new Rect(borderWidth/2 + rect2.left, borderWidth/2 + rect2.top,
                rect2.right - borderWidth/2, rect2.bottom - borderWidth/2);
        rectF = new RectF(rect);

        canvas.drawRoundRect(rectF, roundPx, roundPx, paintFrame);

        return output;
    }
}
