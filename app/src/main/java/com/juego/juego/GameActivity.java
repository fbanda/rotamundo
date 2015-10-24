package com.juego.juego;

import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.juego.objects.Ball;
import com.juego.objects.ChainWall;
import com.juego.sensors.GyroscopeManager;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import java.util.ArrayList;

public class GameActivity extends BaseActivity {

    private World world;
    private GyroscopeManager gyroManager;
    private ArrayList<ChainWall> walls;
    private Ball ball;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        gyroManager = new GyroscopeManager((SensorManager)getSystemService(SENSOR_SERVICE), this);
        gyroManager.toggleListener();

        Vec2 gravity = new Vec2(0f, 0f);
        world = new World(gravity);

        walls = new ArrayList<>();

        Vec2[] vertices = new Vec2[10];
        vertices[0] = new Vec2(0, 0);
        vertices[1] = new Vec2(0, 20);
        vertices[2] = new Vec2(20, 20);
        vertices[3] = new Vec2(20, 40);
        vertices[4] = new Vec2(50, 40);
        vertices[5] = new Vec2(50, 60);
        vertices[6] = new Vec2(90, 60);
        vertices[7] = new Vec2(90, 72);
        vertices[8] = new Vec2(119.6f, 72);
        vertices[9] = new Vec2(119.6f, 0);
        walls.add(new ChainWall(world, vertices, true));

        ball = new Ball(world, 15, 15);
        //ball.setLinearVelocity(new Vec2(8f, 0));
    }

    @Override
    public void onRestart(){
        super.onRestart();
        ball.setPosition(new Vec2(15, 15));
        //ball.setLinearVelocity(new Vec2(8f, 0));
    }

    private int counter = 0;
    private float gravityAngle = 0f;
    private String getGravityText(){
        if(gravityAngle < Math.PI/2){
            return "Derecha";
        }else if(gravityAngle < Math.PI){
            return "Arriba";
        }else if(gravityAngle < 3*Math.PI/2){
            return "Izquierda";
        }else{
            return "Abajo";
        }
    }

    @Override
    public void update(){
        /*counter++;
        if(counter >= 2*ActivityThread.FPS){
            gravityAngle += Math.PI/2;
            gravityAngle %= 2*Math.PI;
            counter = 0;
        }*/

        //ball.applyRotatedGravity(gravityAngle);
        ball.applyRotatedGravity(gyroManager.getScreenAngle());
        world.step(1f/ActivityThread.FPS, 6, 2);
    }

    @Override
    public void draw(Canvas c){
        p.setColor(Color.WHITE);
        c.drawRect(0, 0, screenWidth, screenHeight, p);

        p.setColor(Color.BLUE);
        for(ChainWall wall : walls){
            wall.draw(c, p, scale);
        }

        p.setColor(Ball.BALL_COLOR);
        ball.drawBody(c, p, scale);

        /*p.setColor(Color.GRAY);
        c.drawText(getGravityText(), screenWidth/2, screenHeight/2, p);*/
    }

}
