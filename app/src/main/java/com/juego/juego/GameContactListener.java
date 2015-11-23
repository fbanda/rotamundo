package com.juego.juego;

import com.juego.objects.Ball;
import com.juego.objects.DrawableBody;
import com.juego.objects.Mine;
import com.juego.objects.SpikeRow;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

/**
 * Created by Fernando on 15/11/2015.
 */
public class GameContactListener implements ContactListener {

    private GameActivity activity;

    public GameContactListener(GameActivity activity) {
        this.activity = activity;
    }

    @Override
    public void beginContact(Contact contact) {
        DrawableBody object1 = (DrawableBody)contact.getFixtureA().getBody().getUserData();
        DrawableBody object2 = (DrawableBody)contact.getFixtureB().getBody().getUserData();
        Ball ball = null;
        DrawableBody other = null;

        if(object1 instanceof Ball){
            ball = (Ball)object1;
            other = object2;
        }else if(object2 instanceof Ball){
            ball = (Ball)object2;
            other = object1;
        }

        if(ball != null){
            handleCollision(ball, other);
        }
    }

    public void handleCollision(Ball ball, DrawableBody other){
        if(other instanceof Mine || other instanceof SpikeRow){
            if(ball.canGetHit()) {
                activity.reduceLives();
                ball.hit();
            }

            if(other instanceof Mine){
                activity.removeMine((Mine)other);
            }
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }
}
