package com.juego.objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.juego.juego.BaseActivity;
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
public class SpikeRow extends DrawableBody {

    private Vec2 leftTop;
    private Bitmap bitmap;
    private Body body;

    public SpikeRow(Res res, World world, Vec2[] rect, int orientation, float drawScale, Paint p){
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

        int width, height;
        if(orientation == 0 || orientation == 2){
            width = Math.round(BaseActivity.BOX_2D_SCALE*(rect[2].x - rect[0].x));
            height = 82;
        }else{
            width = 82;
            height = Math.round(BaseActivity.BOX_2D_SCALE * (rect[2].y - rect[0].y));
        }
        bitmap = Bitmap.createBitmap(Math.round(width*drawScale), Math.round(height*drawScale), Bitmap.Config.ARGB_8888);
        leftTop = rect[0];
        SpikeRow.drawSpikes(res, bitmap, orientation, drawScale, p);
    }

    public static void drawSpikes(Res res, Bitmap bitmap, int orientation, float scale, Paint p){
        Bitmap originalSpike = res.bitmap(R.drawable.spike);
        Bitmap rotatedSpike;
        if(orientation != 0) {
            Matrix matrix = new Matrix();
            float angle;
            switch (orientation) {
                case 1:
                    angle = 90;
                    break;
                case 2:
                    angle = 180;
                    break;
                default:
                    angle = 270;
                    break;
            }
            matrix.postRotate(angle);
            rotatedSpike = Bitmap.createBitmap(originalSpike, 0, 0, originalSpike.getWidth(), originalSpike.getHeight(),
                    matrix, true);
        }else{
            rotatedSpike = originalSpike;
        }

        int xoff, yoff, numberOfSpikes;
        if(orientation % 2 == 0){
            xoff = 60;
            yoff = 0;
            numberOfSpikes = (int)Math.ceil(bitmap.getWidth() / 60 / scale);
        }else{
            xoff = 0;
            yoff = 60;
            numberOfSpikes = (int)Math.ceil(bitmap.getHeight() / 60 / scale);
        }
        Canvas c = new Canvas(bitmap);
        for(int i=0; i<numberOfSpikes; i++){
            c.drawBitmap(rotatedSpike, (xoff*i)*scale, (yoff*i)*scale, p);
        }
        if(orientation != 0){
            rotatedSpike.recycle();
        }

    }

    @Override
    public void drawBody(Res res, Canvas c, Paint p, float scale, float xOffset, float yOffset) {
        c.drawBitmap(bitmap, BaseActivity.screenWidth/2 + (leftTop.x + xOffset)*scale,
                BaseActivity.screenHeight/2 + (leftTop.y + yOffset)*scale, p);
    }

}
