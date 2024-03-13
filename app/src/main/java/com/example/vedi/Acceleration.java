package com.example.vedi;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class Acceleration {
    //double
    private static double prevXAcc, prevYAcc, prevZAcc;
    private static double xAcc, yAcc, zAcc;
    //booleans
    private static boolean firstTime = true;

    public static double getAverageAcc() {
        return ShakeListener.getAverageAcc();
    }

    public static void setPrevXAcc(double prevXAcc) {
        Acceleration.prevXAcc = prevXAcc;
    }

    public static void setPrevYAcc(double prevYAcc) {
        Acceleration.prevYAcc = prevYAcc;
    }

    public static void setPrevZAcc(double prevZAcc) {
        Acceleration.prevZAcc = prevZAcc;
    }

    public static double getxAcc() {
        return xAcc;
    }

    public static double getyAcc() {
        return yAcc;
    }

    public static double getzAcc() {
        return zAcc;
    }

    public static class ShakeListener implements SensorEventListener {

        private static double averageAcc;

        public static double getAverageAcc() {
            return averageAcc;
        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            if (firstTime) {
                prevXAcc = sensorEvent.values[0];
                prevYAcc = sensorEvent.values[1];
                prevZAcc = sensorEvent.values[2];
                firstTime = false;
            }
            xAcc = sensorEvent.values[0];
            yAcc = sensorEvent.values[1];
            zAcc = sensorEvent.values[2];

            double deltaXAcc = Math.abs(xAcc - prevXAcc);
            double deltaYAcc = Math.abs(yAcc - prevYAcc);
            double deltaZAcc = Math.abs(zAcc - prevZAcc);

            /*double deltaXAcc = (xAcc - prevXAcc);
            double deltaYAcc = (yAcc - prevYAcc);
            double deltaZAcc = (zAcc - prevZAcc);*/

            prevXAcc = xAcc;
            prevYAcc = yAcc;
            prevZAcc = zAcc;

            /*averageAcc = (double) Math.cbrt(Math.pow(deltaXAcc, 3) + Math.pow(deltaYAcc, 3) + Math.pow(deltaZAcc, 3));*/
            averageAcc = (double) Math.cbrt(Math.pow(deltaXAcc, 2) + Math.pow(deltaYAcc, 2) + Math.pow(deltaZAcc, 2));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }
}
