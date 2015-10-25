package com.juego.juego;

import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.juego.Lector;
import com.juego.objects.Ball;
import com.juego.objects.ChainWall;
import com.juego.sensors.GyroscopeManager;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import java.util.ArrayList;

public class GameActivity extends BaseActivity {

    private World world;
    private GyroscopeManager sensorProvider;
    private ArrayList<ChainWall> walls;
    private Ball ball;

    private float cameraXOffset;
    private float cameraYOffset;
    public static final float ANGLE_OFFSET_RADIUS = 5f;
    private float angleXOffset;
    private float angleYOffset;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        sensorProvider = new GyroscopeManager((SensorManager)getSystemService(SENSOR_SERVICE),this);
        sensorProvider.toggleListener();

        Vec2 gravity = new Vec2(0f, 0f);
        world = new World(gravity);

        walls = new ArrayList<>();

        /*Vec2[] vertices = new Vec2[10];
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
        walls.add(new ChainWall(world, vertices, true));*/
        ArrayList<Vec2[]> wallArray = Lector.Leer(getAssets(), "testVec2.txt");
        for(Vec2[] vertices : wallArray){
            walls.add(new ChainWall(world, vertices, true));
        }

        ball = new Ball(world, 15, 15);
        //ball.setLinearVelocity(new Vec2(8f, 0));
    }

    @Override
    public void onRestart(){
        super.onRestart();
        ball.setPosition(new Vec2(15, 15));
        sensorProvider.toggleListener();
        //ball.setLinearVelocity(new Vec2(8f, 0));
    }

    @Override
    public void onPause(){
        super.onPause();
        sensorProvider.toggleListener();
    }

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
        //ball.applyRotatedGravity(gravityAngle);
        double screenAngle = sensorProvider.getScreenAngle();

        ball.applyRotatedGravity(screenAngle);
        world.step(1f / ActivityThread.FPS, 6, 2);

        angleXOffset = 0;//ANGLE_OFFSET_RADIUS*(float)Math.cos(screenAngle);
        angleYOffset = 0;//-ANGLE_OFFSET_RADIUS*(float)Math.sin(screenAngle);
        Vec2 position = ball.getPosition();
        cameraXOffset = -position.x;
        cameraYOffset = -position.y;
    }

    @Override
    public void draw(Canvas c) {
        p.setColor(Color.WHITE);
        c.drawRect(0, 0, screenWidth, screenHeight, p);

        p.setColor(Color.BLUE);
        for(ChainWall wall : walls){
            wall.draw(c, p, scale, cameraXOffset + angleXOffset, cameraYOffset + angleYOffset);
        }

        p.setColor(Ball.BALL_COLOR);
        ball.drawBodyAt(c, p, scale, screenWidth/2 + angleXOffset, screenHeight/2 + angleYOffset);

        /*p.setColor(Color.GRAY);
        c.drawText(getGravityText(), screenWidth/2, screenHeight/2, p);*/
    }

}
