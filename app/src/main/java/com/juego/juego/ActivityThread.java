package com.juego.juego;

import android.os.SystemClock;

/**
 * Created by Fernando on 15/10/2015.
 */
public class ActivityThread extends Thread {

    public static final int FPS = 30;
    public static final int SKIP_TICKS = 1000/FPS;

    private BaseActivity activity;
    private boolean isRunning = true;
    private long nextGameTick;

    public ActivityThread(BaseActivity activity){
        this.activity = activity;
    }

    @Override
    public void run(){
        long sleepTime;
        nextGameTick = SystemClock.uptimeMillis();
        while(isRunning){
            activity.draw();
            activity.update();
            nextGameTick += SKIP_TICKS;
            sleepTime = nextGameTick - SystemClock.uptimeMillis();
            if(sleepTime > 0){
                try{ Thread.sleep(sleepTime); }catch(InterruptedException e){}
            }else{
                //Te estás retrasando = Lag
                nextGameTick = SystemClock.uptimeMillis();
            }
        }
    }

    public void stopThread(){
        isRunning = false;
    }

}
