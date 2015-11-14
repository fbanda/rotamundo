package com.juego.objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

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
public class SpikeRow extends DrawableBody {

    private Bitmap bitmap;
    private Body body;

    public SpikeRow(World world, Vec2[] rect, int orientation){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.STATIC;
        ChainShape chainShape = new ChainShape();
        chainShape.createLoop(rect, rect.length);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = chainShape;
        fixtureDef.friction = 0.05f;
        body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);

        body.setUserData(this);

        bitmap = Bitmap.createBitmap(0, 0, Bitmap.Config.ARGB_8888);
    }

    @Override
    public void drawBody(Res res, Canvas c, Paint p, float scale, float xOffset, float yOffset) {

    }

}
