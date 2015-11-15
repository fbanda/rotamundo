package com.juego.objects;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.juego.juego.Res;

public abstract class DrawableBody {

    public boolean invisible = false;

    public final void draw(Res res, Canvas c, Paint p, float scale, float xOff, float yOff){
        if(!invisible){
            drawBody(res, c, p, scale, xOff, yOff);
        }
    }

    protected abstract void drawBody(Res res, Canvas c, Paint p, float scale, float xOff, float yOff);

}
