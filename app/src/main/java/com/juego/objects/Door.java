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
        this.orientation = orientation;

        BodyDef bodyDef1 = new BodyDef();
        bodyDef1.type = BodyType.STATIC;
        BodyDef bodyDef2 = new BodyDef();
        bodyDef2.type = BodyType.STATIC;
        BodyDef bodyDef3 = new BodyDef();
        bodyDef3.type = BodyType.STATIC;

        Vec2[] points1;
        Vec2[] points2;
        Vec2[] points3;
        switch(orientation) {
            case 0:
            case 2:
            default:
                points1 = new Vec2[]{new Vec2(x, y), new Vec2(x + 3.4f, y),
                        new Vec2(x + 3.4f, y - 18.4f), new Vec2(x, y - 18.4f)};
                points2 = new Vec2[]{new Vec2(x + 15f, y), new Vec2(x + 18.4f, y),
                        new Vec2(x + 18.4f, y - 18.4f), new Vec2(x + 15f, y - 18.4f)};
                points3 = new Vec2[]{new Vec2(x + 3.4f, y), new Vec2(x + 15f, y),
                        new Vec2(x + 15f, y - 13.2f), new Vec2(x + 3.4f, y - 13.2f)};
                drawX = x;
                drawY = y - 18.4f;
                break;
            case 1:
                points1 = new Vec2[]{new Vec2(x, y), new Vec2(x + 9.2f, y),
                        new Vec2(x + 9.2f, y + 1.7f), new Vec2(x, y + 1.7f)};
                points2 = new Vec2[]{new Vec2(x, y + 7.5f), new Vec2(x + 9.2f, y + 7.5f),
                        new Vec2(x + 9.2f, y + 9.2f), new Vec2(x, y + 9.2f)};
                points3 = new Vec2[]{new Vec2(x, y + 1.7f), new Vec2(x + 9.2f, y + 1.7f),
                        new Vec2(x + 9.2f, y + 7.5f), new Vec2(x, y + 7.5f)};
                drawX = x;
                drawY = y;
                break;
        }

        ChainShape chain1Shape = new ChainShape();
        chain1Shape.createLoop(points1, points1.length);
        FixtureDef fixtureDef1 = new FixtureDef();
        fixtureDef1.shape = chain1Shape;
        fixtureDef1.friction = 0.05f;
        body1 = world.createBody(bodyDef1);
        body1.createFixture(fixtureDef1);

        ChainShape chain2Shape = new ChainShape();
        chain2Shape.createLoop(points2, points2.length);
        FixtureDef fixtureDef2 = new FixtureDef();
        fixtureDef2.shape = chain2Shape;
        fixtureDef2.friction = 0.05f;
        body2 = world.createBody(bodyDef2);
        body2.createFixture(fixtureDef2);

        ChainShape chain3Shape = new ChainShape();
        chain3Shape.createLoop(points3, points3.length);
        FixtureDef fixtureDef3 = new FixtureDef();
        fixtureDef3.shape = chain3Shape;
        fixtureDef3.friction = 0.05f;
        electricBody = world.createBody(bodyDef3);
        electricBody.createFixture(fixtureDef3);
    }

    public DoorColor getColor(){
        return color;
    }

    @Override
    protected void drawBody(Res res, Canvas c, Paint p, float scale, float xOff, float yOff) {
    }

    public void drawDoor(Canvas c, Paint p, float scale, float xOff, float yOff, boolean pressed) {
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
                break;
            case GREEN:
                if(pressed){
                    id = R.drawable.door_g_open;
                }else if(frame % (2*FRAMES_TO_ANIMATE) < FRAMES_TO_ANIMATE){
                    id = R.drawable.door_g_closed_1;
                }else{
                    id = R.drawable.door_g_closed_2;
                }
                break;
            case PURPLE:
                if(pressed){
                    id = R.drawable.door_p_open;
                }else if(frame % (2*FRAMES_TO_ANIMATE) < FRAMES_TO_ANIMATE){
                    id = R.drawable.door_p_closed_1;
                }else{
                    id = R.drawable.door_p_closed_2;
                }
                break;
        }
        return GameActivity.rotatedDoors.get(id)[orientation];
    }
}
