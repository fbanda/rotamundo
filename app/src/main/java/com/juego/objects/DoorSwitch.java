package com.juego.objects;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.juego.juego.BaseActivity;
import com.juego.juego.R;
import com.juego.juego.Res;

import org.jbox2d.common.Vec2;

/**
 * Created by Fernando on 23/11/2015.
 */
public class DoorSwitch {

    public static final float SWITCH_PRESS_RADIUS = 8.9f;
    public static final float SWITCH_DRAW_RADIUS = 8.9f;

    private float x;
    private float y;
    private DoorColor color;
    public boolean pressed;

    public DoorSwitch(float x, float y, DoorColor color) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.pressed = false;
    }

    public DoorColor getColor(){
        return color;
    }

    public void draw(Res res, Canvas c, Paint p, float scale, float xOff, float yOff) {
        c.drawBitmap(res.bitmap(getBitmap()), BaseActivity.screenWidth/2 + (x + xOff - SWITCH_DRAW_RADIUS)*scale,
                BaseActivity.screenHeight/2 + (y + yOff - SWITCH_DRAW_RADIUS)*scale, p);
    }

    private int getBitmap(){
        switch(color){
            case RED:
                return pressed ? R.drawable.switch_r_pressed : R.drawable.switch_r;
            case GREEN:
                return pressed ? R.drawable.switch_g_pressed : R.drawable.switch_g;
            case PURPLE:
                return pressed ? R.drawable.switch_p_pressed : R.drawable.switch_p;
        }
        return 0;
    }

    public boolean isOnTop(Vec2 position){
        float dx = position.x - x;
        float dy = position.y - y;
        return Math.sqrt(dx*dx + dy*dy) <= SWITCH_PRESS_RADIUS + Ball.BALL_COLLISION_RADIUS;
    }
}
