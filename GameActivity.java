package joeshua.robotjack;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class GameActivity extends AppCompatActivity{

    private GameView gameView;
    private static GameActivity instance;
    private ImageButton menuButton;
    private ImageButton exitButton;
    private ImageButton nextButton;
    private ImageButton prevButton;
    private ImageButton resumeButton;
    private ImageButton skillsButton;
    private ImageButton hpButton;
    private ImageView gameOver;
    private ImageView gameWon;
    private LinearLayout gameWidgets;
    private RelativeLayout tutorialWidgets;
    private RelativeLayout selectionWidgets;
    private RelativeLayout gameStates;
    private final int pauseColor = Color.argb(128, 0, 0, 0);
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        instance = this;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                             WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        gameView = new GameView(this, size.x, size.y, MainActivity.tutorialMode);
        gameView.setBackgroundResource(R.drawable.background);


        FrameLayout game = new FrameLayout(this);
        tutorialWidgets = new RelativeLayout(this);
        selectionWidgets = new RelativeLayout(this);
        gameStates = new RelativeLayout(this);
        gameWidgets = new LinearLayout (this);

        //define buttons
        menuButton = new ImageButton(this);
        menuButton.setImageResource(R.drawable.menubutton);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size.y/9, size.y/9);
        layoutParams.topMargin = size.y/10;
        layoutParams.leftMargin = size.y/40;
        menuButton.setLayoutParams(layoutParams);
        menuButton.setScaleType(ImageView.ScaleType.FIT_XY);
        menuButton.setBackgroundColor(Color.TRANSPARENT);
        menuButton.setPadding(0, 0, 0, 0);
        menuButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                exitButton.setVisibility(View.VISIBLE);
                resumeButton.setVisibility(View.VISIBLE);
                gameWidgets.setBackgroundColor(pauseColor);
                gameView.pause();
                // Do something in response to button click
            }
        });
        //yourImageButton.setVisiblity(View.INVISIBLE);

        exitButton = new ImageButton(this);
        exitButton.setImageResource(R.drawable.exitbutton);
        layoutParams = new LinearLayout.LayoutParams(Math.round(size.x*.3125f), Math.round(size.y*0.277777f));
        layoutParams.topMargin = size.y/100;
        layoutParams.leftMargin = size.x / 15;
        exitButton.setLayoutParams(layoutParams);
        exitButton.setScaleType(ImageView.ScaleType.FIT_XY);
        exitButton.setBackgroundColor(Color.TRANSPARENT);
        exitButton.setPadding(0, 0, 0, 0);
        exitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
                // Do something in response to button click
            }
        });
        exitButton.setVisibility(View.INVISIBLE);

        resumeButton = new ImageButton(this);
        resumeButton.setImageResource(R.drawable.resumebutton);
        layoutParams = new LinearLayout.LayoutParams(Math.round(size.x*.3125f), Math.round(size.y*0.277777f));
        layoutParams.topMargin = size.y/100;
        layoutParams.leftMargin = size.x / 15;
        resumeButton.setLayoutParams(layoutParams);
        resumeButton.setScaleType(ImageView.ScaleType.FIT_XY);
        resumeButton.setBackgroundColor(Color.TRANSPARENT);
        resumeButton.setPadding(0, 0, 0, 0);
        resumeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                exitButton.setVisibility(View.INVISIBLE);
                resumeButton.setVisibility(View.INVISIBLE);
                gameWidgets.setBackgroundColor(Color.TRANSPARENT);
                gameView.resume();
            }
        });
        resumeButton.setVisibility(View.INVISIBLE);

        gameWidgets.addView(menuButton);
        gameWidgets.addView(exitButton);
        gameWidgets.addView(resumeButton);

        RelativeLayout.LayoutParams relativeParams;

        nextButton = new ImageButton(this);
        nextButton.setImageResource(R.drawable.nextbutton);
        relativeParams = new RelativeLayout.LayoutParams(Math.round(size.x*(.3125f/2.0f)), Math.round(size.y*(0.277777f/2.0f)));
        relativeParams.bottomMargin = size.y/100;
        relativeParams.rightMargin = size.x / 15;
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        nextButton.setLayoutParams(relativeParams);
        nextButton.setScaleType(ImageView.ScaleType.FIT_XY);
        nextButton.setBackgroundColor(Color.TRANSPARENT);
        nextButton.setPadding(0, 0, 0, 0);
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                gameView.tutorialButton(true);
            }
        });
        if(MainActivity.tutorialMode)
        {
            nextButton.setVisibility(View.VISIBLE);
        }
        else
        {
            nextButton.setVisibility(View.INVISIBLE);
        }


        prevButton = new ImageButton(this);
        prevButton.setImageResource(R.drawable.prevbutton);
        relativeParams = new RelativeLayout.LayoutParams(Math.round(size.x*(.3125f/2.0f)), Math.round(size.y*(0.277777f/2.0f)));
        relativeParams.bottomMargin = size.y/100;
        relativeParams.leftMargin = size.x / 15;
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        prevButton.setLayoutParams(relativeParams);
        prevButton.setScaleType(ImageView.ScaleType.FIT_XY);
        prevButton.setBackgroundColor(Color.TRANSPARENT);
        prevButton.setPadding(0, 0, 0, 0);
        prevButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                gameView.tutorialButton(false);
            }
        });

        if(MainActivity.tutorialMode)
        {
            prevButton.setVisibility(View.VISIBLE);
        }
        else
        {
            prevButton.setVisibility(View.INVISIBLE);
        }

        tutorialWidgets.addView(nextButton);
        tutorialWidgets.addView(prevButton);

        hpButton = new ImageButton(this);
        hpButton.setImageResource(R.drawable.hpbutton);
        relativeParams = new RelativeLayout.LayoutParams(Math.round(size.x*0.4f), Math.round(size.y*0.34f));
        relativeParams.topMargin = size.y/10;
        relativeParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        hpButton.setLayoutParams(relativeParams);
        hpButton.setScaleType(ImageView.ScaleType.FIT_XY);
        hpButton.setBackgroundColor(Color.TRANSPARENT);
        hpButton.setPadding(0, 0, 0, 0);
        hpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                gameView.addSelection(true);
                toggleSelectOff();
            }
        });

        skillsButton = new ImageButton(this);
        skillsButton.setImageResource(R.drawable.skillsbutton);
        relativeParams = new RelativeLayout.LayoutParams(Math.round(size.x*0.4f), Math.round(size.y*0.34f));
        relativeParams.bottomMargin = size.y/10;
        relativeParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        skillsButton.setLayoutParams(relativeParams);
        skillsButton.setScaleType(ImageView.ScaleType.FIT_XY);
        skillsButton.setBackgroundColor(Color.TRANSPARENT);
        skillsButton.setPadding(0, 0, 0, 0);
        skillsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                gameView.addSelection(false);
                toggleSelectOff();
            }
        });

        selectionWidgets.addView(skillsButton);
        selectionWidgets.addView(hpButton);
        toggleSelectOff();

        gameOver = new ImageView(this);
        gameOver.setImageResource(R.drawable.gameover);
        relativeParams = new RelativeLayout.LayoutParams(Math.round(size.x*0.5f), Math.round(size.y*0.4444444f));
        relativeParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        relativeParams.addRule(RelativeLayout.CENTER_VERTICAL);
        gameOver.setLayoutParams(relativeParams);
        gameOver.setScaleType(ImageView.ScaleType.FIT_XY);
        gameOver.setBackgroundColor(Color.TRANSPARENT);
        gameOver.setPadding(0, 0, 0, 0);
        gameOver.setVisibility(View.INVISIBLE);

        gameWon = new ImageView(this);
        gameWon.setImageResource(R.drawable.gamewon);
        relativeParams = new RelativeLayout.LayoutParams(Math.round(size.x*0.5f), Math.round(size.y*0.4444444f));
        relativeParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        relativeParams.addRule(RelativeLayout.CENTER_VERTICAL);
        gameWon.setLayoutParams(relativeParams);
        gameWon.setScaleType(ImageView.ScaleType.FIT_XY);
        gameWon.setBackgroundColor(Color.TRANSPARENT);
        gameWon.setPadding(0, 0, 0, 0);
        gameWon.setVisibility(View.INVISIBLE);

        gameStates.addView(gameOver);
        gameStates.addView(gameWon);

        game.addView(gameView);
        game.addView(tutorialWidgets);
        game.addView(selectionWidgets);
        game.addView(gameStates);
        game.addView(gameWidgets);

        setContentView(game);
    }

    public void toggleSelectOn()
    {
        hpButton.setVisibility(View.VISIBLE);
        skillsButton.setVisibility(View.VISIBLE);
    }

    public void toggleSelectOff()
    {
        hpButton.setVisibility(View.INVISIBLE);
        skillsButton.setVisibility(View.INVISIBLE);
    }

    public void gameOver()
    {
        gameOver.setVisibility(View.VISIBLE);
    }

    public void gameWon()
    {
        gameWon.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause(){
        super.onPause();
        gameView.pause();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.battletheme);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
        gameView.resume();
    }

    public static GameActivity getContext()
    {
        return instance;
    }

    @Override
    public void onBackPressed()
    {

    }
}
