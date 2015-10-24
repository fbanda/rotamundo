package com.juego.sensors;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;


public class GyroscopeManager implements SensorEventListener {
    private SensorManager sManager;
    private Sensor magneto;
    private Sensor accelerometer;
    private double azimut;
    private boolean sensorActive;
    private Activity gameActivity;

    public GyroscopeManager(SensorManager sManager, Activity gameActivity){
        this.sManager = sManager;
        this.gameActivity = gameActivity;
        sensorActive = false;
        magneto = sManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void toggleListener(){
        if(sensorActive){
            sManager.unregisterListener(this);
        }else{
            sManager.registerListener(this, magneto, SensorManager.SENSOR_DELAY_FASTEST);
            sManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    public double getScreenAngle(){
        return azimut - Math.toRadians(45);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Nada
    }

    float[] mGravity;
    float[] mGeomagnetic;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
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
        }
    }

}
