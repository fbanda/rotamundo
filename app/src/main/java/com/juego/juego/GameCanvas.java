package com.juego.juego;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Fernando on 15/10/2015.
 */
public class GameCanvas extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder holder;

    public GameCanvas(Context context, AttributeSet attrs){
        super(context, attrs);
        holder = getHolder();
        holder.addCallback(this);
        setFocusable(false);
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
    }

    public void draw(BaseActivity activity){
        Canvas c = holder.lockCanvas(null);
        if(c != null){
            activity.draw(c);
            holder.unlockCanvasAndPost(c);
        }
    }
}
