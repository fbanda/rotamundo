package com.juego.juego;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class MainActivity extends AppCompatActivity {

    private GameCanvas canvas;
    private Paint p;

    private int screenWidth;
    private int screenHeight;

    private Bitmap marioBitmap;
    private World world;

    private Body floor;
    private Body ball;

    private ActivityThread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        setContentView(R.layout.activity_main);
        canvas = (GameCanvas)findViewById(R.id.main_canvas);

        Display display = getWindowManager().getDefaultDisplay();
        try{
            Point point = new Point();
            display.getSize(point);
            screenWidth = point.x;
            screenHeight = point.y;
        }catch(NoSuchMethodError e){
            screenWidth = display.getWidth();
            screenHeight = display.getHeight();
        }
        p = new Paint();
        marioBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mario);

        Vec2 gravity = new Vec2(0, 9.8f);
        world = new World(gravity, true);

        //Floor
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(0, 30);
        bodyDef.type = BodyType.STATIC;
        PolygonShape shape = new PolygonShape();
        shape.set(new Vec2[]{new Vec2(0, 30), new Vec2(30, 30), new Vec2(30, 33), new Vec2(0, 33)}, 4);
        FixtureDef fix = new FixtureDef();
        fix.shape = shape;
        floor = world.createBody(bodyDef);
        floor.createFixture(fix);

        //Ball
        BodyDef bodyDef2 = new BodyDef();
        bodyDef2.position.set(15, 15);
        bodyDef2.type = BodyType.DYNAMIC;
        CircleShape cShape = new CircleShape();
        cShape.m_radius = 3;
        FixtureDef fix2 = new FixtureDef();
        fix2.shape = cShape;
        fix2.restitution = 0.5f;
        ball = world.createBody(bodyDef2);
        ball.createFixture(fix2);
    }

    @Override
    public void onResume(){
        super.onResume();
        thread = new ActivityThread(this);
        thread.start();
    }

    @Override
    public void onPause(){
        super.onPause();
        thread.stopThread();
    }

    public final void draw(){
        canvas.draw(this);
    }

    /* Game loop */

    public void update(){
        world.step(1f/ActivityThread.FPS, 6, 2);
    }

    public static final int DRAW_RESOLUTION = 4;

    public void draw(Canvas c){
        p.setColor(Color.BLACK);
        c.drawRect(0, 0, screenWidth, screenHeight, p);
        p.setColor(Color.BLUE);
        PolygonShape floorShape = ((PolygonShape) floor.getFixtureList().getShape());
        Vec2 floorPos = floor.getPosition();
        c.drawRect((floorPos.x + floorShape.getVertex(0).x)*DRAW_RESOLUTION, (floorPos.y + floorShape.getVertex(0).y)*DRAW_RESOLUTION,
                (floorPos.x + floorShape.getVertex(2).x)*DRAW_RESOLUTION, (floorPos.y + floorShape.getVertex(2).y)*DRAW_RESOLUTION, p);
        p.setColor(Color.YELLOW);
        CircleShape ballShape = ((CircleShape)ball.getFixtureList().getShape());
        Vec2 ballPos = ball.getPosition();
        c.drawCircle(ballPos.x*DRAW_RESOLUTION, ballPos.y*DRAW_RESOLUTION, ballShape.m_radius*DRAW_RESOLUTION, p);
    }


}
