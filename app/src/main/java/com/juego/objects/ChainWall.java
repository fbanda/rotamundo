package com.juego.objects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.juego.juego.BaseActivity;
import com.juego.juego.Res;

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
    public void drawBody(Res res, Canvas c, Paint p, float scale, float xOffset, float yOffset) {
        Vec2[] vertices = ((ChainShape)body.getFixtureList().getShape()).m_vertices;
        for (int i = 0, len = vertices.length; i < len - 1; i++) {
            c.drawLine(BaseActivity.screenWidth/2 + (vertices[i].x + xOffset)*scale, BaseActivity.screenHeight/2 + (vertices[i].y+yOffset)*scale,
                    BaseActivity.screenWidth/2 + (vertices[i+1].x+xOffset)*scale, BaseActivity.screenHeight/2 + (vertices[i+1].y+yOffset)*scale, p);
        }
    }
}
