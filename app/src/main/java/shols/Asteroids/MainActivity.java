package shols.Asteroids;

import android.app.Activity;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class MainActivity extends Activity {

    private GLSurfaceView mGLView;
    private MediaPlayer mediaPlayerMusic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Resumes from bundle if it exists
        super.onCreate(savedInstanceState);
        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);
        mediaPlayerMusic = MediaPlayer.create(this, R.raw.music);
        mediaPlayerMusic.setLooping(true);
        mediaPlayerMusic.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
        mediaPlayerMusic.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
        mediaPlayerMusic.start();
    }
}

