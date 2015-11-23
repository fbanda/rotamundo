package com.juego;
import org.jbox2d.common.Vec2;
import android.content.res.AssetManager;

import com.juego.objects.scanner.Button;
import com.juego.objects.scanner.ForceField;
import com.juego.objects.scanner.Spike;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by KEVIN BROWN on 25/10/2015.
 */

public class Lector {
    ArrayList<Vec2[]> wallVertices;
    ArrayList<Vec2> mines;
    ArrayList<Button> buttons;
    ArrayList<Spike> spikes;
    ArrayList<ForceField> fields;
    float playerX;
    float playerY;

    public Lector(){
        wallVertices = new ArrayList<>();
        mines = new ArrayList<>();
        buttons = new ArrayList<>();
        spikes = new ArrayList<>();
        fields = new ArrayList<>();
    }

    public float getPlayerX(){
        return playerX;
    }

    public float getPlayerY(){
        return playerY;
    }

    public ArrayList<Vec2[]> getWalls(){
        return wallVertices;
    }

    public ArrayList<Vec2> getMines(){
        return mines;
    }

    public ArrayList<Button> getButtons(){
        return buttons;
    }

    public ArrayList<Spike> getSpikes(){
        return spikes;
    }

    public ArrayList<ForceField> getForceFields(){
        return fields;
    }

    public void Leer(AssetManager assetManager, String path){
        try {
            InputStreamReader in = new InputStreamReader(assetManager.open(path));
            BufferedReader br = new BufferedReader(in);
            String strLine;

            while ((strLine = br.readLine())!= null){
                String[] strAux =  strLine.split(" "); // ex: 0,0 100,0 100,100 0,100
                if(strLine.startsWith("wall")) readWall(strAux);
                else if(strLine.startsWith("mine")) readMine(strAux[1]);
                else if(strLine.startsWith("btn")) readButton(strAux);
                else if(strLine.startsWith("spike")) readSpike(strAux);
                else if(strLine.startsWith("field")) readField(strAux);
                else if(strLine.startsWith("player")) readPlayer(strAux[1]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readWall(String[] verts){
        Vec2[] vertices = new Vec2[verts.length-1];

        for (int i = 1; i< verts.length ; i++){
            String[] strNumbersAux = verts[i].split(",");
            vertices[i-1] = new Vec2(Float.parseFloat(strNumbersAux[0]),Float.parseFloat(strNumbersAux[1]));
        }

        wallVertices.add(vertices);
    }

    private void readMine(String vert){
        String[] strNumbersAux = vert.split(",");
        mines.add(new Vec2(Float.parseFloat(strNumbersAux[0]),Float.parseFloat(strNumbersAux[1])));
    }

    private void readButton(String[] verts){
        String[] strNumbersAux = verts[1].split(",");

        Button btn = new Button(Float.parseFloat(strNumbersAux[0]), Float.parseFloat(strNumbersAux[1]), verts[2].charAt(0));

        buttons.add(btn);
    }

    private void readSpike(String[] verts){
        String[] strNumbersAux1 = verts[1].split(",");
        String[] strNumbersAux2 = verts[2].split(",");

        Spike spike = new Spike(Float.parseFloat(strNumbersAux1[0]), Float.parseFloat(strNumbersAux1[1]),
                Float.parseFloat(strNumbersAux2[0]), Float.parseFloat(strNumbersAux2[1]),
                Integer.parseInt(verts[3]));

        spikes.add(spike);
    }

    private void readField(String[] verts){
        String[] strNumbersAux = verts[1].split(",");

        ForceField field = new ForceField(Float.parseFloat(strNumbersAux[0]), Float.parseFloat(strNumbersAux[1]), Integer.parseInt(verts[2]));

        fields.add(field);
    }

    private void readPlayer(String vert){
        String[] strNumbersAux = vert.split(",");
        playerX = Float.parseFloat(strNumbersAux[0]);
        playerY = Float.parseFloat(strNumbersAux[1]);
    }

}
