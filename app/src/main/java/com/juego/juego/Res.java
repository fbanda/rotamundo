package com.juego.juego;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.Hashtable;

public class Res {

    private Resources resources;
    private Hashtable<Integer, Bitmap> bitmaps;

    public Res(Resources resources){
        this.resources = resources;
        this.bitmaps = new Hashtable<>();
    }

    public Bitmap bitmap(int resourceId){
        if(!bitmaps.containsKey(resourceId)){
            Bitmap bitmap = BitmapFactory.decodeResource(resources, resourceId);
            Bitmap newBitmap;
            if(BaseActivity.drawScale == 1f){
                newBitmap = bitmap;
            }else {
                newBitmap = Bitmap.createScaledBitmap(bitmap,
                        Math.round(bitmap.getWidth() * BaseActivity.drawScale),
                        Math.round(bitmap.getHeight() * BaseActivity.drawScale), true);
                Log.d("Res", "Bitmap : " + bitmap.getWidth() + " " + bitmap.getHeight() + " -> " + newBitmap.getWidth() + " " + newBitmap.getHeight());
                bitmap.recycle();
            }
            bitmaps.put(resourceId, newBitmap);
            return newBitmap;
        }else{
            return bitmaps.get(resourceId);
        }
    }

    public void freeBitmap(int resourceId){
        Bitmap bitmap = bitmaps.remove(resourceId);
        if(bitmap != null){
            bitmap.recycle();
        }
    }

    public void freeAllResources(){
        for(Bitmap bitmap : bitmaps.values()){
            bitmap.recycle();
        }
        bitmaps.clear();
    }

}
