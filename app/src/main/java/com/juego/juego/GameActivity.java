package com.juego.juego;

import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import com.juego.Lector;
import com.juego.objects.Ball;
import com.juego.objects.ChainWall;
import com.juego.objects.Mine;
import com.juego.objects.SpikeRow;
import com.juego.sensors.GyroscopeManager;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import java.util.ArrayList;

public class GameActivity extends BaseActivity {

    private World world;
    private GyroscopeManager sensorProvider;
    private ArrayList<ChainWall> walls;
    private Ball ball;
    private Mine mine;
    private SpikeRow spikeRow;

    public static final boolean CAMERA = false;

    public static final float ANGLE_OFFSET = 5f;
    private float cameraXOffset;
    private float cameraYOffset;
    private float angleXOffset;
    private float angleYOffset;

    @Override
    protected int[] getNeededBitmaps(){
        return new int[]{R.drawable.ball_kirby, R.drawable.mine_gordo, R.drawable.spike};
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        sensorProvider = new GyroscopeManager((SensorManager)getSystemService(SENSOR_SERVICE));

        Vec2 gravity = new Vec2(0f, 0f);
        world = new World(gravity);

        walls = new ArrayList<>();
        ArrayList<Vec2[]> wallArray = Lector.Leer(getAssets(), "testVec2.txt");
        for(Vec2[] vertices : wallArray){
            walls.add(new ChainWall(world, vertices, true));
        }

        mine = new Mine(world, 15, 30);

        ball = new Ball(world, 15, 15);

        spikeRow = new SpikeRow(res, world,
                new Vec2[]{new Vec2(0, 0), new Vec2(60, 0), new Vec2(60, 8.2f), new Vec2(0, 8.2f)},
                2, BaseActivity.drawScale, p);
    }

    @Override
    public void onRestart(){
        super.onRestart();
        ball.setPosition(new Vec2(15, 15));
    }

    @Override
    public void onResume(){
        super.onResume();
        sensorProvider.toggleListener();
    }

    @Override
    public void onPause(){
        super.onPause();
        sensorProvider.toggleListener();
    }

    @Override
    public void update(){
        double screenAngle = sensorProvider.getScreenAngle();

        if(sensorProvider.pushedButton()){
            sensorProvider.resetButtonState();
        }


        ball.applyRotatedGravity(screenAngle);
        world.step(1f / ActivityThread.FPS, 6, 2);

        Vec2 position = ball.getPosition();
        cameraXOffset = -position.x;
        cameraYOffset = -position.y;
        if(CAMERA) {
            angleXOffset = ANGLE_OFFSET * (float) Math.cos(screenAngle);
            angleYOffset = -ANGLE_OFFSET * (float) Math.sin(screenAngle);
        }else{
            angleXOffset = 0;
            angleYOffset = 0;
        }
    }

    @Override
    public void draw(Canvas c) {
        p.setColor(Color.WHITE);
        c.drawRect(0, 0, screenWidth, screenHeight, p);

        p.setColor(ChainWall.WALL_COLOR);
        for(ChainWall wall : walls){
            wall.draw(res, c, p, BaseActivity.getCombinedScale(), cameraXOffset + angleXOffset, cameraYOffset + angleYOffset);
        }

        /*p.setColor(Color.BLACK);
        c.drawCircle(screenWidth/2, screenHeight/2, 3*drawScale, p);*/

        p.setColor(Ball.BALL_COLOR);
        ball.drawBodyAt(res, c, p, BaseActivity.getCombinedScale(),
                screenWidth / 2 + angleXOffset * BaseActivity.getCombinedScale(),
                screenHeight / 2 + angleYOffset * BaseActivity.getCombinedScale());

        mine.draw(res, c, p, BaseActivity.getCombinedScale(), cameraXOffset + angleXOffset, cameraYOffset + angleYOffset);

        spikeRow.draw(res, c, p, BaseActivity.getCombinedScale(), cameraXOffset + angleXOffset, cameraYOffset + angleYOffset);
    }

}
