package com.juego.juego;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;

public abstract class BaseActivity extends Activity {

    private GameCanvas canvas;
    protected Paint p;
    protected float scale;

    protected int screenWidth;
    protected int screenHeight;

    private ActivityThread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        setContentView(R.layout.activity_main);
        canvas = (GameCanvas)findViewById(R.id.main_canvas);

        Display display = getWindowManager().getDefaultDisplay();
        try{
            Point point = new Point();
            display.getSize(point);
            screenWidth = point.x;
            screenHeight = point.y;
        }catch(NoSuchMethodError e){
            screenWidth = display.getWidth();
            screenHeight = display.getHeight();
        }
        float density = getResources().getDisplayMetrics().density;
        p = new Paint();
        p.setStrokeWidth(2*density);
        p.setTextSize(16*density);
        p.setTextAlign(Paint.Align.CENTER);

        scale = 10f * screenHeight / 720;
    }

    @Override
    public void onResume(){
        super.onResume();
        thread = new ActivityThread(this);
        thread.start();
    }

    @Override
    public void onPause(){
        super.onPause();
        thread.stopThread();
    }

    public final void draw(){
        canvas.draw(this);
    }

    /* Game loop */

    public abstract void update();

    public abstract void draw(Canvas c);


}
