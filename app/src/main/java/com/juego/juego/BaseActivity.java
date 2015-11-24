package com.juego.juego;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public abstract class BaseActivity extends Activity {

    private GameCanvas canvas;
    protected Paint p;

    public static float drawScale;
    public static final float BOX_2D_SCALE = 10f;
    public static float getCombinedScale(){
        return drawScale*BOX_2D_SCALE;
    }

    public static int screenWidth;
    public static int screenHeight;

    private ActivityThread thread;
    protected Res res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        setContentView(R.layout.activity_main);
        canvas = (GameCanvas)findViewById(R.id.main_canvas);

        Display display = getWindowManager().getDefaultDisplay();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){
            Point point = new Point();
            display.getSize(point);
            screenWidth = point.x;
            screenHeight = point.y;
        }else{
            screenWidth = display.getWidth();
            screenHeight = display.getHeight();
        }
        float density = getResources().getDisplayMetrics().density;
        p = new Paint();
        p.setStrokeWidth(2 * density);
        p.setTextSize(16 * density);
        p.setTextAlign(Paint.Align.CENTER);

        drawScale = (float)screenHeight / 720;

        res = new Res(getResources());
    }

    public void setOnTouchListener(View.OnTouchListener listener){
        canvas.setOnTouchListener(listener);
    }

    @Override
    public void onResume(){
        super.onResume();
        thread = new ActivityThread(this);
        thread.start();

        /*for(int bitmap : getNeededBitmaps()){
            res.bitmap(bitmap);
        }*/
    }

    @Override
    public void onPause(){
        super.onPause();
        thread.stopThread();

        //res.freeAllResources();
    }

    public final void draw(){
        canvas.draw(this);
    }

    /* Game loop */

    public abstract void update();

    public abstract void draw(Canvas c);

    protected int[] getNeededBitmaps(){
        return new int[0];
    }


}
