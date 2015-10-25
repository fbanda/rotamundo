package com.juego;
import org.jbox2d.common.Vec2;
import android.content.res.AssetManager;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by KEVIN BROWN on 25/10/2015.
 */
public class Lector {

    public static ArrayList<Vec2[]> Leer(AssetManager assetManager, String path){
        ArrayList vecs = new ArrayList<Vec2[]>();
        try {
            InputStreamReader in = new InputStreamReader(assetManager.open(path));
            BufferedReader br = new BufferedReader(in);
            String strLine;

            while ((strLine = br.readLine())!= null){
                String[] strAux =  strLine.split(" "); // ex: 0,0 100,0 100,100 0,100

                Vec2[] vertices = new Vec2[strAux.length];

                for (int i = 0; i< strAux.length ; i++){
                    String[] strNumbersAux = strAux[i].split(",");
                    vertices[i] = new Vec2(Integer.parseInt(strNumbersAux[0]),Integer.parseInt(strNumbersAux[1]));
                }
            vecs.add(vertices);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return vecs;
    }

}
