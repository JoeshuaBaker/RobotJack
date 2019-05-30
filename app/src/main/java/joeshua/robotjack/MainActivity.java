package joeshua.robotjack;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity{

    private ImageButton buttonPlay;
    private ImageButton buttonTutorial;
    private MediaPlayer mediaPlayer;
    public static boolean tutorialMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        buttonPlay = (ImageButton) findViewById(R.id.buttonPlay);
        buttonPlay.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v)
                {
                    //start game activity
                    startGame();
                    buttonPlay.setEnabled(false);
                    tutorialMode = false;
                }
            });

        buttonTutorial = (ImageButton) findViewById(R.id.buttonTutorial);
        buttonTutorial.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                //start game activity
                startGame();
                buttonTutorial.setEnabled(false);
                tutorialMode = true;
            }
        });
    }

    private void startGame()
    {
        startActivity(new Intent(this, GameActivity.class));
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.menutheme);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
        buttonPlay.setEnabled(true);
        buttonTutorial.setEnabled(true);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }
}
