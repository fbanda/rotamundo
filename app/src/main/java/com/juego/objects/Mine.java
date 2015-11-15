package com.juego.objects;

import android.graphics.Canvas;
import android.graphics.Paint;

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

/**
 * Created by Fernando on 14/11/2015.
 */
public class Mine extends DrawableBody {

    public static final float MINE_DRAW_RADIUS = 5.3f;
    public static final float MINE_COLLISION_RADIUS = 5f;

    private Body body;

    public Mine(World world, float x, float y){
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);
        bodyDef.type = BodyType.STATIC;
        CircleShape cShape = new CircleShape();
        cShape.m_radius = MINE_COLLISION_RADIUS;
        FixtureDef fix2 = new FixtureDef();
        fix2.shape = cShape;
        fix2.friction = 0.05f;
        body = world.createBody(bodyDef);
        body.createFixture(fix2);

        body.setUserData(this);
    }

    @Override
    protected void drawBody(Res res, Canvas c, Paint p, float scale, float xOff, float yOff) {
        Vec2 pos = body.getPosition();
        float x = pos.x;
        float y = pos.y;
        /*c.drawCircle(BaseActivity.screenWidth/2 + (x + xOff)*drawScale,
                BaseActivity.screenHeight/2 + (y + yOff)*drawScale, MINE_DRAW_RADIUS*drawScale, p);*/
        c.drawBitmap(res.bitmap(R.drawable.mine_gordo), BaseActivity.screenWidth/2 + (x + xOff - MINE_DRAW_RADIUS)*scale,
                BaseActivity.screenHeight/2 + (y + yOff - MINE_DRAW_RADIUS)*scale, p);
    }

}
