package com.juego.objects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.juego.juego.ActivityThread;
import com.juego.juego.BaseActivity;
import com.juego.juego.R;
import com.juego.juego.Res;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import java.util.List;

public class Ball extends DrawableBody {

    public enum State{
        NORMAL, HITSTUN
    }

    private State ballState;
    private int framesRemaining;
    public static final int FRAMES_FOR_BLINK_HITSTUN = 3;
    public static final int FRAMES_FOR_HITSTUN = FRAMES_FOR_BLINK_HITSTUN*15; //1.5 segundos
    public static final int FRAMES_TO_PUSH_BUTTON = 1;

    public static final float ROTATED_GRAVITY_IMPULSE = 22f / ActivityThread.FPS;
    public static final float MAX_SPEED = 26f;
    public static final float BALL_DRAW_RADIUS = 3;
    public static final float BALL_COLLISION_RADIUS = 3;

    private Body body;

    public Ball(World world, float x, float y){
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.fixedRotation = true;
        CircleShape cShape = new CircleShape();
        cShape.m_radius = BALL_COLLISION_RADIUS;
        FixtureDef fix2 = new FixtureDef();
        fix2.shape = cShape;
        fix2.friction = 0.05f;
        fix2.restitution = 0.3f;
        body = world.createBody(bodyDef);
        body.createFixture(fix2);

        body.setUserData(this);

        ballState = State.NORMAL;
    }

    public Vec2 getPosition(){
        return body.getPosition();
    }

    public void setPosition(Vec2 position){
        body.setTransform(position, 0f);
    }

    /*public void setLinearVelocity(Vec2 velocity){
        body.setLinearVelocity(velocity);
    }*/

    public void update(double gravityAngle){
        if(ballState == State.HITSTUN){
            framesRemaining--;
            if(framesRemaining < 0){
                ballState = State.NORMAL;
            }
        }
        if(ballState == State.NORMAL || ballState == State.HITSTUN) { //No cae mientras presiona un botón
            applyRotatedGravity(gravityAngle);
        }
    }

    private void applyRotatedGravity(double angle){
        Vec2 force = new Vec2((float)(ROTATED_GRAVITY_IMPULSE*Math.cos(angle)), -(float)(ROTATED_GRAVITY_IMPULSE*Math.sin(angle)));
        body.applyLinearImpulse(force, body.getWorldCenter());

        //Max speed
        Vec2 velocity = body.getLinearVelocity();
        float speed = velocity.length();
        if(speed > MAX_SPEED){
            velocity.mulLocal(MAX_SPEED / speed);
        }
    }

    public void drawBodyAt(Bitmap bm, Canvas c, Paint p, float scale, float x, float y){
        if(ballState == State.HITSTUN) {
            if ((framesRemaining - 1) % (2 * FRAMES_FOR_BLINK_HITSTUN) >= FRAMES_FOR_BLINK_HITSTUN) {
                p.setAlpha(170);
            } else {
                p.setAlpha(85);
            }
        }
        c.drawBitmap(bm, x - BALL_DRAW_RADIUS * scale, y - BALL_DRAW_RADIUS * scale, p);
        p.setAlpha(255);
    }

    @Override
    protected void drawBody(Res res, Canvas c, Paint p, float scale, float xOff, float yOff) {
        Vec2 pos = body.getPosition();
        float x = pos.x;
        float y = pos.y;
        c.drawBitmap(res.bitmap(R.drawable.ball_kirby), BaseActivity.screenWidth / 2 + (x + xOff - BALL_DRAW_RADIUS) * scale,
                BaseActivity.screenHeight / 2 + (y + yOff - BALL_DRAW_RADIUS) * scale, p);
    }

    public boolean canGetHit(){
        return ballState == State.NORMAL;
    }

    public void hit(){
        ballState = State.HITSTUN;
        framesRemaining = FRAMES_FOR_HITSTUN;
    }

    public DoorSwitch isOnTopOfSwitch(List<DoorSwitch> doorSwitches){
        for(DoorSwitch doorSwitch : doorSwitches){
            if(doorSwitch.isOnTop(body.getPosition())){
                return doorSwitch;
            }
        }
        return null;
    }
}
