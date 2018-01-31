package com.softwareoverflow.colorfall.characters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;


public abstract class GameObject extends android.support.v7.widget.AppCompatImageView{

    final int screenX, screenY;
    protected int x, y, speed;


    private Bitmap bitmap;

    public GameObject(Context context,  int bitmapRes, int screenX, int screenY){
        super(context);
        this.screenX = screenX;
        this.screenY = screenY;

        bitmap = BitmapFactory.decodeResource(context.getResources(), bitmapRes);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public abstract void update();

    public final void draw(Canvas canvas, Paint paint){
        canvas.drawBitmap(bitmap, x, y, paint);
    }
}
