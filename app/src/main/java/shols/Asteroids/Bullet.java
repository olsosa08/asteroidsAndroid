package shols.Asteroids;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by shols on 1/22/2018.
 */

//Timer goes off calls move on each object. Request render at end. Check for intersection in this.

public class Bullet implements GraphicsObject {
    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private final FloatBuffer vertexBuffer;
    private final ShortBuffer drawListBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private double mX = 0;
    private double mY = 0;
    private double dX = 0;
    private double dY = 0;
    private double shipX = 0;
    private double shipY = 0;
    private double mAngle = 0;
    static final int COORDS_PER_VERTEX = 3;
    static float bulletCoords[] = {
            -0.0125f, 0.0125f, 0.0f,   // top left
            -0.0125f, -0.0125f, 0.0f,   // bottom left
            0.0125f, -0.0125f, 0.0f,   // bottom right
            0.0125f, 0.0125f, 0.0f};  // top right


    private final short drawOrder[] = {0, 1, 2, 0, 2, 3};

    private final int vertexStride = COORDS_PER_VERTEX * 4;

    float color[] = {1.0f, 1.0f, 1.0f, 0.0f};

    public Bullet() {
        ByteBuffer bb = ByteBuffer.allocateDirect(bulletCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(bulletCoords);
        vertexBuffer.position(0);
        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);
        int vertexShader = MyGLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    public void fireBullet(Ship ship, double angle){
        mAngle = angle;
        shipX = ship.getX();
        shipY = ship.getY();
        dX = -Math.cos(mAngle) * 0.025;
        dY = Math.sin(mAngle) * 0.025;
        mX = shipX + .00000000001;
        mY = shipY + .00000000001;
    }

    public void outOfBoundsBullet(Bullet b){
        if (b.getX() > 1.25 || b.getX() < -1.25 || b.getY() > 1.25 || b.getY() < -1.25){
            MyGLRenderer.mFiredBulletArray.remove(b);
        }
    }


    public void draw(float[] mMvpMatrix) {
        move();
        float[] mvpMatrix = new float[16];
        Matrix.setIdentityM(mvpMatrix, 0);
        Matrix.translateM(mvpMatrix, 0, (float) mX, (float) mY, 0);
        Matrix.multiplyMM(mvpMatrix,0,mvpMatrix,0,mMvpMatrix,0);
        GLES20.glUseProgram(mProgram);
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");
        GLES20.glDrawElements(
                GLES20.GL_LINE_LOOP, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    @Override
    public void move() {
        mX += dX;
        mY += dY;
    }

    @Override
    public double getRadius() {
        return 0.01875;
    }

    @Override
    public double getX() {
        return mX;
    }

    @Override
    public double getY() {
        return mY;
    }
}
