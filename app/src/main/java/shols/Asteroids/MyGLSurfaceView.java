package shols.Asteroids;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {

    private MyGLRenderer mRenderer;
    public static double mAngle = 0;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private MediaPlayer mediaPlayerFire;
    private MediaPlayer mediaPlayerBoost;


    private class MySensorEventListener implements SensorEventListener {


        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_GRAVITY) {
                double x = sensorEvent.values[0];
                double y = sensorEvent.values[1];
                mAngle = Math.acos(x / (Math.sqrt((Math.pow(x, 2) + Math.pow(y, 2)))));
                if (y >= 0) {
                    mAngle = -mAngle;
                }
            }
            if (mRenderer.mShip != null) {
                MyGLRenderer.mShip.setmAngle(mAngle);
            }
            requestRender();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        System.out.println(y);
        switch (e.getAction()) {
            case MotionEvent.ACTION_UP:
                if (y > 900) {
                    Bullet temp = MyGLRenderer.mBulletArray.remove(0);
                    temp.fireBullet(MyGLRenderer.mShip, mAngle);
                    MyGLRenderer.mFiredBulletArray.add(temp);
                    mediaPlayerFire.start();
                }
                else if(y < 900){
                    MyGLRenderer.mShip.fireThruster();
                    mediaPlayerBoost.start();
                }

        }
        return true;
    }

    public MyGLSurfaceView(Context context) {
        super(context);
        mediaPlayerFire = MediaPlayer.create(context, R.raw.bullet);
        mediaPlayerBoost = MediaPlayer.create(context, R.raw.thrust);
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        setEGLContextClientVersion(2);
        mRenderer = new MyGLRenderer();
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mSensorManager.registerListener(new MySensorEventListener(), mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
}
