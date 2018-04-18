package shols.Asteroids;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "MyGLRenderer";
    public static Ship mShip = null;
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    public static ArrayList<LargeAsteroid> mLAsterArray = new ArrayList<>();
    public static ArrayList<MediumAsteroid> mMAsterArray = new ArrayList<>();
    public static ArrayList<SmallAsteroid> mSAsterArray = new ArrayList<>();
    public static ArrayList<Bullet> mBulletArray = new ArrayList<>();
    public static ArrayList<Bullet> mFiredBulletArray = new ArrayList<>();
    private Random r = new Random();

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        mShip = new Ship();
        for (int i = 0; i < 10; i++) {
            mBulletArray.add(new Bullet());
        }
        for (int i = 0; i < 7; i++) {
            int temp = r.nextInt(3);
            if (temp == 0) {
                mLAsterArray.add(new LargeAsteroid());
            } else if (temp == 1) {
                MediumAsteroid tempMAster = new MediumAsteroid();
                tempMAster.setXPos(tempMAster.randomX());
                tempMAster.setYPos(tempMAster.randomY());
                mMAsterArray.add(tempMAster);
            } else if (temp == 2) {
                SmallAsteroid tempSAster = new SmallAsteroid();
                tempSAster.setXPos(tempSAster.randomX());
                tempSAster.setYPos(tempSAster.randomY());
                mSAsterArray.add(tempSAster);
            }
        }
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        if (mLAsterArray.size() < 3) {
            mLAsterArray.add(new LargeAsteroid());
        }
        if (mBulletArray.size() < 10) {
            mBulletArray.add(new Bullet());
        }
        // Iterates through each item in the arraylist containing the larger asteroids and draws on the screen.
        for (LargeAsteroid item : mLAsterArray) {
            item.draw(mMVPMatrix);
        }
        for (MediumAsteroid item : mMAsterArray) {
            item.draw(mMVPMatrix);
        }
        for (SmallAsteroid item : mSAsterArray) {
            item.draw(mMVPMatrix);
        }
        mShip.draw(mMVPMatrix);
        try {
            for (Bullet item : mFiredBulletArray) {
                    if(item.getX() != 0 && item.getY() != 0) {
                        item.draw(mMVPMatrix);
                    }
                    item.outOfBoundsBullet(item);
                    for (SmallAsteroid sAster : mSAsterArray){
                        sAster.destroy(sAster, mSAsterArray, item);
                    }
                    for (LargeAsteroid lAster : mLAsterArray){
                        lAster.destroy(lAster, mMAsterArray, item);
                    }
                    for (MediumAsteroid mAster : mMAsterArray){
                        mAster.destroy(mAster, mSAsterArray, item);
                    }
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

    }

    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }
}