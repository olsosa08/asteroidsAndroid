package shols.Asteroids;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by shols on 1/17/2018.
 */

public class MediumAsteroid implements GraphicsObject{
    private Random r = new Random();
    private double mX = 0;
    private double mY = 0;
    private double startY = 0;
    private double startX = 0;
    private double dTheta = (2 + 1) * r.nextDouble() + 1;
    private double dX = (0.0015 + 0.001) * r.nextDouble() + 0.001;
    private double dY = (0.0015 + 0.001) * r.nextDouble() + 0.001;
    private double angle = 0;
    private static final float VERTICES[] = {
            -0.1f, -0.1f, -0.1f,
            0.1f, -0.1f, -0.1f,
            0.1f, 0.1f, -0.1f,
            -0.1f, 0.1f, -0.1f,
            -0.1f, -0.1f, 0.1f,
            0.1f, -0.1f, 0.1f,
            0.1f, 0.1f, 0.1f,
            -0.1f, 0.1f, 0.1f
    };
    private static final float COLORS[] = {
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
    };
    private static final byte INDICES[] = {
            0, 1, 3, 3, 1, 2, // Front face.
            0, 1, 4, 4, 5, 1, // Bottom face.
            1, 2, 5, 5, 6, 2, // Right face.
            2, 3, 6, 6, 7, 3, // Top face.
            3, 7, 4, 4, 3, 0, // Left face.
            4, 5, 7, 7, 6, 5, // Rear face.
    };
    private static final int COORDS_PER_VERTEX = 3;
    private static final int VALUES_PER_COLOR = 4;
    private final int VERTEX_STRIDE = COORDS_PER_VERTEX * 4;
    private final int COLOR_STRIDE = VALUES_PER_COLOR * 4;
    private static final String VERTEX_SHADER_CODE =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec4 vColor;" +
                    "varying vec4 _vColor;" +
                    "void main() {" +
                    "  _vColor = vColor;" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private static final String FRAGMENT_SHADER_CODE =
            "precision mediump float;" +
                    "varying vec4 _vColor;" +
                    "void main() {" +
                    "  gl_FragColor = _vColor;" +
                    "}";


    private final FloatBuffer mVertexBuffer;
    private final FloatBuffer mColorBuffer;
    private final ByteBuffer mIndexBuffer;
    private final int mProgram;
    private final int mPositionHandle;
    private final int mColorHandle;
    private final int mMVPMatrixHandle;

    public MediumAsteroid() {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(VERTICES.length * 4);

        byteBuffer.order(ByteOrder.nativeOrder());
        mVertexBuffer = byteBuffer.asFloatBuffer();
        mVertexBuffer.put(VERTICES);
        mVertexBuffer.position(0);

        byteBuffer = ByteBuffer.allocateDirect(COLORS.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        mColorBuffer = byteBuffer.asFloatBuffer();
        mColorBuffer.put(COLORS);
        mColorBuffer.position(0);

        mIndexBuffer = ByteBuffer.allocateDirect(INDICES.length);
        mIndexBuffer.put(INDICES);
        mIndexBuffer.position(0);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_CODE));
        GLES20.glAttachShader(
                mProgram, loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE));
        GLES20.glLinkProgram(mProgram);

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "vColor");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
    }


    public double randomX(){
        double lower = -0.25;
        double xPos = 0;
        double temp = r.nextDouble() * .5 + lower;
        if (temp < 0) {
            xPos = (temp/-temp) - .25;
        }
        else if (temp > 0){
            xPos = (temp/ temp) + .25;
        }

        return xPos;
    }

    public double randomY(){
        double yPos = -1.25 + (1.25 - -1.25) * r.nextDouble();
        return yPos;
    }

    public void setXPos(double xPos){
        mX = xPos;
        startX = mX;
    }

    public void setYPos(double yPos){
        mY = yPos;
        startY = mY;
    }


    public void setRateX() {
        if (startX > 0) {
            mX -= dX;
        } else if (startX < 0) {
            mX += dX;
        }
    }

    public void setRateY() {
        if (startY > 0) {
            mY -= dY;
        } else if (startY < 0) {
            mY += dY;
        }
    }

    public void resetXY() {
        if(startX < 0){
            mX = -1.25 - .3;
        }
        else{
            mX = 1.25 + .3;
        }
        mY = startY;
    }

    public void destroy(MediumAsteroid mMAster, ArrayList<SmallAsteroid> mSAsterArray, Bullet mBullet) {
        boolean temp = collision(mMAster, mBullet);
        if (temp) {
            double xPos = mMAster.getX();
            double yPos = mMAster.getY();
            SmallAsteroid mSAster1 = new SmallAsteroid();
            SmallAsteroid mSAster2 = new SmallAsteroid();
            mSAster1.setXPos(xPos);
            mSAster1.setYPos(yPos);
            mSAster2.setXPos(xPos);
            mSAster2.setYPos(yPos);
            mSAsterArray.add(mSAster1);
            mSAsterArray.add(mSAster2);
            MyGLRenderer.mMAsterArray.remove(mMAster);
        }
    }

    public boolean collision(MediumAsteroid mMAster, Bullet mBullet) {
        boolean ans = false;
        double distance = Math.sqrt(Math.pow(mMAster.getX() - mBullet.getX(), 2) + Math.pow(mMAster.getY() - mBullet.getY(), 2));
        if (distance < mMAster.getRadius()) {
            ans = true;
            MyGLRenderer.mFiredBulletArray.remove(mBullet);
        }
        return ans;
    }


    public void draw(float[] mMvpMatrix) {
        float[] scratch = new float[16];
        float[] mvpMatrix = new float[16];
        float[] mRotationMatrix = new float[16];
        move();
        Matrix.setRotateM(mRotationMatrix, 0, (float) angle, -1.0f, -1.0f, -1.0f);
        Matrix.multiplyMM(scratch, 0, mMvpMatrix, 0, mRotationMatrix, 0);
        Matrix.setIdentityM(mvpMatrix, 0);
        Matrix.translateM(mvpMatrix, 0, (float) mX, (float) mY, 0);
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, scratch, 0);
        GLES20.glUseProgram(mProgram);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(
                mPositionHandle, 3, GLES20.GL_FLOAT, false, VERTEX_STRIDE, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(
                mColorHandle, 4, GLES20.GL_FLOAT, false, COLOR_STRIDE, mColorBuffer);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glDrawElements(
                GLES20.GL_LINE_LOOP, INDICES.length, GLES20.GL_UNSIGNED_BYTE, mIndexBuffer);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mColorHandle);
    }

    @Override
    public void move() {
        setRateX();
        setRateY();
        if (mX == -startX || mY > 1.25 || mY < -1.25) {
            resetXY();
        }
        if(angle >= 360){
            angle = 0;
        }
        angle += dTheta;
    }

    @Override
    public double getRadius() {
        return 0.15;
    }

    @Override
    public double getX() {
        return mX;
    }

    @Override
    public double getY() {
        return mY;
    }

    private static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}
