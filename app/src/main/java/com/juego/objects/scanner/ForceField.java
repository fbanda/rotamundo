package com.juego.objects.scanner;

/**
 * Created by Luis on 11/14/2015.
 */
public class ForceField {
    public float x;
    public float y;
    public char color;
    public int orientation;

    public ForceField(float x, float y, int orientation, char color){
        this.x = x;
        this.y = y;
        this.orientation = orientation;
        this.color = color;
    }
}
