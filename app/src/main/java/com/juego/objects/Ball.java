package com.juego.objects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.animation.AccelerateInterpolator;

import com.juego.juego.ActivityThread;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

public class Ball extends DrawableBody {

    public static final int BALL_COLOR = Color.RED;

    public static final float ROTATED_GRAVITY_IMPULSE = 9.8f / ActivityThread.FPS;

    private Body body;

    public Ball(World world, float x, float y){
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.fixedRotation = true;
        CircleShape cShape = new CircleShape();
        cShape.m_radius = 3;
        FixtureDef fix2 = new FixtureDef();
        fix2.shape = cShape;
        fix2.friction = 0.05f;
        fix2.restitution = 0.5f;
        body = world.createBody(bodyDef);
        body.createFixture(fix2);

        body.setUserData(this);
    }

    public void setPosition(Vec2 position){
        body.setTransform(position, 0f);
    }

    /*public void setLinearVelocity(Vec2 velocity){
        body.setLinearVelocity(velocity);
    }*/

    public void applyRotatedGravity(double angle){
        Vec2 force = new Vec2((float)(ROTATED_GRAVITY_IMPULSE*Math.cos(angle)), -(float)(ROTATED_GRAVITY_IMPULSE*Math.sin(angle)));
        body.applyLinearImpulse(force, body.getWorldCenter());
    }

    @Override
    public void drawBody(Canvas c, Paint p, float scale) {
        float radius = body.getFixtureList().getShape().m_radius;
        Vec2 pos = body.getPosition();
        float x = pos.x;
        float y = pos.y;
        c.drawCircle(x*scale, y*scale, radius*scale, p);
    }
}
