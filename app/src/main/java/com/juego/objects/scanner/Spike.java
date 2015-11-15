package com.juego.objects.scanner;

import org.jbox2d.common.Vec2;

/**
 * Created by Luis on 11/14/2015.
 */
public class Spike {
    private float x1;
    private float y1;
    private float x2;
    private float y2;
    public int orientation;

    public Spike(float x1, float y1, float x2, float y2, int orientation){
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.orientation = orientation;
    }

    public Vec2[] getRect(){
        return new Vec2[]{
                new Vec2(x1, y1),
                new Vec2(x2, y1),
                new Vec2(x2, y2),
                new Vec2(x1, y2)
        };
    }
}
