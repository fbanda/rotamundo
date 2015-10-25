package com.juego.sensors;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import org.jbox2d.common.Vec2;


public class GyroscopeManager implements SensorEventListener {
    private SensorManager sManager;
    private Sensor magneto;
    private Sensor accelerometer;
    private Sensor orientationSensor;
    private double angle;
    private boolean sensorActive;
    private Activity gameActivity;

    public GyroscopeManager(SensorManager sManager, Activity gameActivity){
        this.sManager = sManager;
        this.gameActivity = gameActivity;
        sensorActive = false;
        accelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void toggleListener(){
        if(sensorActive){
            sManager.unregisterListener(this);
            sensorActive = false;
        }else{
            sManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
            sensorActive = true;
        }
    }

    public double getScreenAngle(){
        return angle;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Nada
    }

    float[] mGravity;
    float[] mGeomagnetic;

    @Override
    public void onSensorChanged(SensorEvent event) {
        /*if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values.clone();
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values.clone();
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimut = orientation[0]; // orientation contains: azimut, pitch and roll
                Log.d("a", "" + Math.toDegrees(azimut));
            }
        }*/
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            float x =  event.values[0];
            float y =  event.values[1];

            angle = Math.atan2(y,x);
            angle -= Math.PI/2;
            angle += 2*Math.PI;
            angle %= 2*Math.PI;


        }
    }

}
