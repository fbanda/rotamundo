package com.juego.objects.scanner;

/**
 * Created by Luis on 11/14/2015.
 */
public class Spike {
    public float x1;
    public float y1;
    public float x2;
    public float y2;
    public int orientation;

    public Spike(float x1, float y1, float x2, float y2, int orientation){
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.orientation = orientation;
    }
}
