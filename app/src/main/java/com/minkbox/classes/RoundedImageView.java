package com.minkbox.classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class RoundedImageView extends ImageView {


public RoundedImageView(Context ctx, AttributeSet attrs) {
    super(ctx, attrs);
}


@Override
protected void onDraw(Canvas canvas) {


    try {
        Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();

        Bitmap bitmap = b.copy(Config.ARGB_8888, true);



        int w = getWidth(), h = getHeight();


        bitmap= getRoundedCroppedBitmap(bitmap, w);
        canvas.drawBitmap(bitmap, 0, 0, null);
    }    catch (Exception e) {
            Log.e("UnRegister Receiver Err", "> " + e.getMessage());
        }
}

public static Bitmap getRoundedCroppedBitmap(Bitmap bitmap, int radius) {
        Bitmap output=null;
        Bitmap finalBitmap;

    try {
        if (bitmap.getWidth() != radius || bitmap.getHeight() != radius)
            finalBitmap = Bitmap.createScaledBitmap(bitmap, radius, radius, false);
        else
            finalBitmap = bitmap;
        output = Bitmap.createBitmap(finalBitmap.getWidth(),
                finalBitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, finalBitmap.getWidth(), finalBitmap.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(finalBitmap.getWidth() / 2 + 0.7f, finalBitmap.getHeight() / 2 + 0.7f,
                finalBitmap.getWidth() / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(finalBitmap, rect, rect, paint);

        bitmap.recycle();



    }catch (Exception e) {
        Log.e("UnRegister Receiver Err", "> " + e.getMessage());
    }
    return output;
    }


}