package com.juego.objects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

/**
 * Created by Fernando on 18/10/2015.
 */
public class ChainWall extends DrawableBody {

    public static final int WALL_COLOR = Color.BLUE;

    private Body body;

    public ChainWall(World world, Vec2[] points, boolean loop){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.STATIC;
        ChainShape chainShape = new ChainShape();
        if(loop){
            chainShape.createLoop(points, points.length);
        }else{
            chainShape.createChain(points, points.length);
        }
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = chainShape;
        fixtureDef.friction = 0.05f;
        body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);

        body.setUserData(this);
    }

    @Override
    public void drawBody(Canvas c, Paint p, float scale) {
        Vec2[] vertices = ((ChainShape)body.getFixtureList().getShape()).m_vertices;
        for (int i = 0, len = vertices.length; i < len - 1; i++) {
            c.drawLine(vertices[i].x*scale, vertices[i].y*scale, vertices[i+1].x*scale, vertices[i+1].y*scale, p);
        }
    }
}
