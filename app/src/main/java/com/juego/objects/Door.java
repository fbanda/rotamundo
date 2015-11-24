package com.juego.objects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.juego.juego.BaseActivity;
import com.juego.juego.GameActivity;
import com.juego.juego.R;
import com.juego.juego.Res;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

/**
 * Created by Fernando on 14/11/2015.
 */
public class Door extends DrawableBody {

    public static long frame;
    public static final int FRAMES_TO_ANIMATE = 6;

    private DoorColor color;
    private int orientation;
    private Body body1;
    private Body body2;
    private Body electricBody;
    private float drawX;
    private float drawY;

    public Door(World world, float x, float y, int orientation, DoorColor color) {
        this.color = color;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.STATIC;

        Vec2[] points1;
        Vec2[] points2;
        Vec2[] points3;
        switch(orientation) {
            case 0:
            case 1:
            case 2:
            default:
                points1 = new Vec2[]{new Vec2(x, y), new Vec2(x + 1.7f, y),
                        new Vec2(x + 1.7f, y - 9.2f), new Vec2(x, y - 9.2f)};
                points2 = new Vec2[]{new Vec2(x + 7.5f, y), new Vec2(x + 9.2f, y),
                        new Vec2(x + 9.2f, y - 9.2f), new Vec2(x + 7.5f, y - 9.2f)};
                points3 = new Vec2[]{new Vec2(x + 1.7f, y), new Vec2(x + 7.5f, y),
                        new Vec2(x + 7.5f, y + 6.6f), new Vec2(x + 1.7f, y + y + 66.f)};
                drawX = x;
                drawY = y - 9.2f;
                break;
        }

        ChainShape chain1Shape = new ChainShape();
        chain1Shape.createLoop(points1, points1.length);
        FixtureDef fixtureDef1 = new FixtureDef();
        fixtureDef1.shape = chain1Shape;
        fixtureDef1.friction = 0.05f;
        body1 = world.createBody(bodyDef);
        body1.createFixture(fixtureDef1);

        ChainShape chain2Shape = new ChainShape();
        chain2Shape.createLoop(points2, points2.length);
        FixtureDef fixtureDef2 = new FixtureDef();
        fixtureDef2.shape = chain2Shape;
        fixtureDef2.friction = 0.05f;
        body2 = world.createBody(bodyDef);
        body2.createFixture(fixtureDef1);

        ChainShape chain3Shape = new ChainShape();
        chain3Shape.createLoop(points3, points3.length);
        FixtureDef fixtureDef3 = new FixtureDef();
        fixtureDef3.shape = chain3Shape;
        fixtureDef3.friction = 0.05f;
        electricBody = world.createBody(bodyDef);
        electricBody.createFixture(fixtureDef1);
    }

    @Override
    protected void drawBody(Res res, Canvas c, Paint p, float scale, float xOff, float yOff) {
    }

    protected void drawBody(Res res, Canvas c, Paint p, float scale, float xOff, float yOff, boolean pressed) {
        c.drawBitmap(getBitmap(pressed), BaseActivity.screenWidth/2 + (drawX + xOff)*scale,
                BaseActivity.screenHeight/2 + (drawY + yOff)*scale, p);
    }

    public void setActive(boolean active){
        electricBody.setActive(active);
    }

    public Bitmap getBitmap(boolean pressed){
        int id = 0;
        switch(color){
            case RED:
                if(pressed){
                    id = R.drawable.door_r_open;
                }else if(frame % (2*FRAMES_TO_ANIMATE) < FRAMES_TO_ANIMATE){
                    id = R.drawable.door_r_closed_1;
                }else{
                    id = R.drawable.door_r_closed_2;
                }
            case GREEN:
                if(pressed){
                    id = R.drawable.door_g_open;
                }else if(frame % (2*FRAMES_TO_ANIMATE) < FRAMES_TO_ANIMATE){
                    id = R.drawable.door_g_closed_1;
                }else{
                    id = R.drawable.door_g_closed_2;
                }
            case PURPLE:
                if(pressed){
                    id = R.drawable.door_p_open;
                }else if(frame % (2*FRAMES_TO_ANIMATE) < FRAMES_TO_ANIMATE){
                    id = R.drawable.door_p_closed_1;
                }else{
                    id = R.drawable.door_p_closed_2;
                }
        }
        return GameActivity.rotatedDoors.get(id)[orientation];
    }
}
