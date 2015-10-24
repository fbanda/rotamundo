package com.juego.objects;

import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class DrawableBody {

    public boolean invisible = false;

    public final void draw(Canvas c, Paint p, float scale, float xOff, float yOff){
        if(!invisible){
            drawBody(c, p, scale, xOff, yOff);
        }
    }

    public abstract void drawBody(Canvas c, Paint p, float scale, float xOff, float yOff);

}
