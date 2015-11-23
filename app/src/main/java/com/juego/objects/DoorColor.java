package com.juego.objects;

/**
 * Created by Fernando on 23/11/2015.
 */
public enum DoorColor {
    RED, GREEN, PURPLE;

    public static DoorColor fromChar(char c){
        switch(c){
            case 'R': return RED;
            case 'G': return GREEN;
            case 'P': return PURPLE;
        }
        return null;
    }
}
