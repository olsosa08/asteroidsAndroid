package shols.Asteroids;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by shols on 1/8/2018.
 */

public class Ship implements GraphicsObject{
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
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = {
            // in counterclockwise order:
            0.0f, 0.07775211f, 0.0f,   // top
            -0.07f, -0.07775211f, 0.0f,   // bottom left
            0.07f, -0.07775211f, 0.0f    // bottom right
    };
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private final float[] mRotationMatrix = new float[16];
    private double mAngle = 0;
    private double dX = 0;
    private double dY = 0;
    private double mX = 0;
    private double mY = 0;

    float color[] = {1.0f, 1.0f, 1.0f, 0.0f};

    public Ship() {
        ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(triangleCoords);
        vertexBuffer.position(0);
        int vertexShader = MyGLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);

    }

    public void setmAngle(double angle) {
        mAngle = angle;
    }

    public void fireThruster(){
        dX = -Math.cos(mAngle) * 0.001 + dX;
        dY = Math.sin(mAngle) * 0.001 + dY;
    }

    public void reappear(){
        if(mX > 1.25 || mX < -1.25){
            mX = -mX;
            mY = -mY;
        }
        else if (mY > 1.25 || mY < -1.25){
            mX = -mX;
            mY = -mY;
        }
    }

    public void draw(float[] mMVPMatrix) {
        move();
        float[] scratch = new float[16];
        float[] mvpMatrix = new float[16];
        Matrix.setRotateM(mRotationMatrix, 0, (float) Math.toDegrees(mAngle) - 90, 0, 0, 1.0f);
        Matrix.multiplyMM(mvpMatrix, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        Matrix.setIdentityM(scratch, 0);
        Matrix.translateM(scratch, 0, (float) mX, (float) mY, 0);
        Matrix.multiplyMM(mvpMatrix, 0, scratch, 0, mvpMatrix, 0);
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
        GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, vertexCount);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    @Override
    public void move() {
        mX += dX;
        mY += dY;
        reappear();
    }

    @Override
    public double getRadius() {
        return 0;
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
