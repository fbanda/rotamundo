package com.juego.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class GyroscopeManager implements SensorEventListener {
    private SensorManager sManager;
    private Sensor accelerometer;
    private double angle;
    private static final double threshold = 8;
    private boolean pushedButton;
    private boolean sensorActive;

    public GyroscopeManager(SensorManager sManager){
        this.sManager = sManager;
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

    public boolean pushedButton() {return pushedButton;}
    public void resetButtonState() {pushedButton = false;}

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Nada
    }

    float[] mGravity;
    float[] mGeomagnetic;

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            if(z>threshold) pushedButton = true;

            angle = Math.atan2(y, x);
            angle -= Math.PI / 2;
            angle += 2 * Math.PI;
            angle %= 2 * Math.PI;
        }
    }

}
