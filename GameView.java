package joeshua.robotjack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;

import joeshua.robotjack.GameObjects.*;
import joeshua.robotjack.GameObjects.Enemies.Bubble;
import joeshua.robotjack.GameObjects.Enemies.Enemy;
import joeshua.robotjack.GameObjects.Enemies.FaceGun;
import joeshua.robotjack.GameObjects.Enemies.MissileMan;
import joeshua.robotjack.GameObjects.Enemies.Worm;
import joeshua.robotjack.Managers.Abilities.Ability;
import joeshua.robotjack.Managers.*;
import joeshua.robotjack.Managers.Abilities.AbilityIcon;
import joeshua.robotjack.Managers.Abilities.Firebomb;
import joeshua.robotjack.Managers.Abilities.Gatling;
import joeshua.robotjack.Managers.Abilities.Leech;
import joeshua.robotjack.Managers.Abilities.Lightning;
import joeshua.robotjack.Managers.Abilities.Spikes;
import joeshua.robotjack.Rendering.AnimationLibrary;
import joeshua.robotjack.Rendering.AnimationPlayer;

/**
 * Created by joeshua on 4/11/2017.
 */

public class GameView extends SurfaceView implements Runnable {
    volatile boolean playing;

    //objects to have a running thread and manage delta T to run at 60 fps
    private Thread gameThread = null;
    private long lastTime;
    private final double ticks = 60D;
    private double ns = 1000000000 / ticks;
    private double delta = 0;
    private long now;
    private int second = 0;

    //objects to manage the different gameModes.
    private enum GameMode {
        starting,
        playing,
        ending,
        selecting;
    }

    private GameMode gameMode;
    private int level = 0;
    private boolean gameOver = false;
    private int gameOverTimer = 300;
    private ArrayList<Ability> fullDeck = new ArrayList<>();

    //drawing objects
    private ArrayList<GameObject> displayList;
    private LinkedList<Ability> abilityList;
    private Paint paint;
    private Paint strokePaint;
    private Matrix reverse = new Matrix();
    int[] offset = {Math.round(Globals.screenWidth*(-0.025f)),
            Math.round(Globals.screenWidth*.13125f),
            Math.round(Globals.screenWidth*.375f),
            Math.round(Globals.screenWidth*.61875f),
            Math.round(Globals.screenWidth*.76875f)};
    Float[] cdX = {.18f, .3f, .5f, .6875f, .80f};
    Float[] cdY = {.07f, .14f, 0.23f, 0.14f, .07f};
    Float[] tooltipPos = {.125f, .1635f, .202f, .24f};
    final int healthTextSize = Globals.screenWidth/40;
    final int uiTextSize = Globals.screenWidth/55;

    //gameobjects and containers
    private Jack player;
    private Grid grid;
    private Deck deck;
    private ArrayList<Enemy> enemies;
    private ArrayList<Enemy> graveyard;

    //tutorial objects
    private boolean tutorialNextStep = false;
    private int tutorialNextTimer = 0;
    private String tutorialText = new String("");
    private int tutorialCounter = 0;
    private final int tutorialTextMaxChars = 80;
    private boolean tutorialMode;
    private Enemy tutorialGuy;

    //managers
    Input input;

    public GameView(Context context, int _screenWidth, int _screenHeight, boolean _tutorial) {
        super(context);
        lastTime = System.nanoTime();
        //setup dependent globals
        Globals.initDependentGlobals(_screenWidth, _screenHeight);
        Globals.initActivityGlobals(new AnimationLibrary());

        tutorialMode = _tutorial;

        //setup drawing variables, enable hardware accelleration
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        setWillNotDraw(false);
        displayList = new ArrayList<GameObject>();
        paint = new Paint();
        paint.setAntiAlias(false);
        paint.setFilterBitmap(false);
        paint.setTextSize(Globals.screenWidth/40);
        paint.setColor(Color.WHITE);
        paint.setTypeface(Typeface.DEFAULT_BOLD);

        strokePaint = new Paint();
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setTextSize(paint.getTextSize());
        strokePaint.setStrokeWidth(paint.getTextSize()/3);
        strokePaint.setColor(Color.BLACK);
        strokePaint.setTypeface(Typeface.DEFAULT_BOLD);

        reverse.postScale(-1, 1);

        //initialize game objects
        grid = new Grid();
        enemies = new ArrayList<>();
        graveyard = new ArrayList<>();
        player = new Jack(grid.getTile(1,1));
        displayList.add(player);

        Globals.initGlobals(player, grid, input, null);

        //initialize managers
        input = new Input();
        fullDeck = new ArrayList<>();
        if(!tutorialMode)
        {
            for(int i = 0; i < 4; ++i)
            {
                fullDeck.add(new Firebomb());
                fullDeck.add(new Spikes());
                fullDeck.add(new Gatling());
            }
        }
        deck = new Deck();
        deck.refreshDeck(fullDeck);

        abilityList = deck.getActiveAbilities();
        //setup global references
        Globals.initGlobals(player, grid, input, deck);

        if(!tutorialMode)
        {
            switchGameMode(GameMode.starting);
        }
        else
        {
            switchGameMode(GameMode.playing);
        }

    }

    @Override
    public void run(){
        while(playing)
        {
            //calculate delta T. if it's over our threshold, run a tick
            now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if(delta >= 1)
            {
                tick();
                delta--;
                second++;
                //code for executing things once every second
                if(second == 60)
                {
                    second = 0;
                    //do stuff
                }
            }
        }
    }

    public void tick()
    {
        update();
        draw();
        control();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent)
    {
        if(!gameOver) input.handleInput(motionEvent);
        return true;
    }

    private void update() {
        player.update();

        //handle inputs
        if(input.isLeftDown())
        {
            Tile newTile = grid.getNearestTile(input.getLeftX(), input.getLeftY(), true);
            if(newTile != player.getTile())
            {
                Globals.input.setMoveTrigger(true);
            }

            if(newTile != null)
                grid.placeObject(player, newTile);

        }

        deck.update();

        for(Enemy e : enemies)
        {
            e.update();
            if(e.isDead())
            {
                graveyard.add(e);
            }
        }

        for(Enemy e : graveyard)
        {
            removeEnemy(e);
        }

        if(tutorialMode)
        {
            if(tutorialNextStep)
            {
                if(tutorialNextTimer != -99) tutorialNextTimer = 600;
                tutorial();
                tutorialNextStep = false;
            }
            else if(tutorialNextTimer != -99)
            {
                tutorialNextTimer--;
                if(tutorialNextTimer <= 0 )
                {
                    tutorialNextStep = true;
                }
            }

        }

        if(enemies.isEmpty() && !tutorialMode)
        {
            switchGameMode(GameMode.selecting);
        }
        else if(player.getHealth() <= 0)
        {
            switchGameMode(GameMode.ending);
        }
    }

    private void tutorial()
    {
        tutorialCounter++;
        switch(tutorialCounter)
        {
            case 1:
                tutorialText = "Welcome to Robot Jack! This tutorial will teach you the basic controls and UI. You play as Jack, the robot on the left.";

                break;

            case 2:
                tutorialText = "All actions in RobotJack are executed by moving or swiping your fingers on the different parts of the screen."
                + " Inputs are interpreted differently whether you touch the left or right half of the screen.";
                break;

            case 3:
                tutorialText = "To move, simply move your thumb around the left half of the screen. You can only move inside your 3x3 grid.";
                break;

            case 4:
                tutorialText = "To execute a basic attack, swipe down on the right half of the screen. Swipe down and hold (keep your finger pressed to the screen)" +
                        " to continuously attack.";
                break;

            case 5:
                tutorialText = "I have added an ability to your hand at the top of the screen. Swipe left on the right half of the screen twice to move it to your center slot (the biggest one)."
                + " Abilities in your center slot display their name, damage, and tooltip information around your UI.";
                if(deck.getHand()[2] == null)
                {
                    deck.addHand(new Firebomb(), 4);
                    Ability leech = new Leech();
                    leech.setCooldown(20);
                    deck.addDeck(leech);
                }
                break;

            case 6:
                tutorialText = "When an ability is in your center slot (focus), you swipe up on the right half of the screen to use it. Using an ability incurs a cooldown on the ability that fills the slot.";
                break;

            case 7:
                if(tutorialGuy == null)
                {
                    tutorialGuy = new MissileMan(Globals.grid.getTile(4, 1));
                    tutorialGuy.disableAI();
                    tutorialGuy.setHealth(800);
                    addEnemy(tutorialGuy);
                }
                tutorialText = "This is an enemy. Continuously hitting an enemy with your basic attack makes the cooldown of your focus slot tick down twice as fast.";
                break;

            case 8:
                if(tutorialGuy != null)
                {
                    tutorialGuy.enableAI();
                }
                tutorialText = "Enemies will move and attack you in a pattern. Enemies use abilities just like you do. The yellow squares serve to warn you" +
                        " ahead of time which tiles are dangerous and about to take damage. Avoid moving into these marked tiles.";
                break;

            case 9:
                tutorialText = "The menu button is the button with lines in the upper left hand corner. Tap this button to exit the tutorial when you are finished.";
                tutorialCounter--;
                break;
        }
    }

    private void draw()
    {
        AnimationPlayer anim;
        for(GameObject object : displayList) {
            for(AnimationPlayer objectAnim : object.getAnimations())
            {
                objectAnim.update();
            }
        }
        for(Ability ability : abilityList)
        {
            anim = ability.getAnimations();
            anim.update();
        }

        postInvalidate();

    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        Bitmap currentBmp;
        Tile currentTile;
        float textX;
        float textY;

        //draw UI elements
        Ability[] hand = deck.getHand();

        int i;
        int onCd = 0;

        for(i = 2; i < hand.length; ++i)
        {
            if(hand[i] != null)
            {
                onCd = (hand[i].getCooldown() > 0) ? 5 : 0;
                currentBmp = hand[i].getIcon().getIcon().getFrames()[i + onCd];
                canvas.drawBitmap(currentBmp, offset[i], 0, paint);

                if(hand[i].getCooldown() != 0)
                {
                    textX = Globals.screenWidth*cdX[i] - paint.measureText(Integer.toString((hand[i].getCooldown()/60)));
                    textY = Globals.screenHeight*cdY[i];
                    canvas.drawText(Integer.toString((hand[i].getCooldown()/60) + 1), textX, textY, strokePaint);
                    canvas.drawText(Integer.toString((hand[i].getCooldown()/60) + 1), textX, textY, paint);
                }
            }

        }

        for(i = 1; i >= 0; --i)
        {
            if(hand[i] != null)
            {
                onCd = (hand[i].getCooldown() > 0) ? 5 : 0;
                currentBmp = hand[i].getIcon().getIcon().getFrames()[i + onCd];
                canvas.drawBitmap(currentBmp, offset[i], 0, paint);
                if(hand[i].getCooldown() != 0)
                {
                    //paint.measureText(Integer.toString(object.getHealth()))/2
                    textX = Globals.screenWidth*cdX[i] - paint.measureText(Integer.toString((hand[i].getCooldown()/60)));
                    textY = Globals.screenHeight*cdY[i];
                    canvas.drawText(Integer.toString((hand[i].getCooldown()/60) + 1), textX, textY, strokePaint);
                    canvas.drawText(Integer.toString((hand[i].getCooldown()/60) + 1), textX, textY, paint);
                }
            }
        }

        //draw ui text
        paint.setTextSize(uiTextSize);
        strokePaint.setTextSize(uiTextSize);
        String[] tooltip;
        AbilityIcon icon;
        String current;
        if(hand[2] != null)
        {
            icon = hand[2].getIcon();
            tooltip = icon.getTooltipFormatted(paint);
            //draw tooltip
            for(i = 0; i < 4; ++i)
            {
                if(!tooltip[i].equalsIgnoreCase(""))
                {
                    textY = Globals.screenHeight*tooltipPos[i];
                    textX = Globals.screenWidth*.78f;
                    canvas.drawText(tooltip[i], textX, textY, paint);
                }
            }

            //draw damage text and name
            textX = Globals.screenWidth*.36875f - paint.measureText(icon.getName());
            textY = Globals.screenHeight*.22222f;
            canvas.drawText(icon.getName(), textX, textY, paint);
            textX = Globals.screenWidth*.63125f;
            textY = Globals.screenHeight*.22222f;
            canvas.drawText(icon.getDamage() + " damage", textX, textY, paint);
        }


        //draw warning tiles
        for(Tile tile : grid.getLinearGrid()) {
            if (tile.getWarn()) {
                currentBmp = tile.updateWarn();
                canvas.drawBitmap(currentBmp,
                        tile.getX() - currentBmp.getWidth(),
                        tile.getY() - currentBmp.getHeight(),
                        paint);
            }
        }


        paint.setTextSize(healthTextSize);
        strokePaint.setTextSize(healthTextSize);
        //draw ui health
        textX = Globals.screenWidth*.0125f;
        textY = Globals.screenHeight*.06666f;
        if(player != null) canvas.drawText(Integer.toString(player.getHealth()) + " HP", textX, textY, paint);

        //draw gameObjects and health text
        ArrayList<GameObject> objects = (ArrayList) displayList.clone();
        for(GameObject object : objects)
        {
            if(object == null) continue;

            //bitmap is drawn with its bottom right edge aligned with the tile's bottom right.
            currentTile = object.getTile();
            for(AnimationPlayer anim : object.getAnimations())
            {
                currentBmp = anim.getCurrentFrame();
                if(currentBmp != null && currentTile != null) canvas.drawBitmap(currentBmp,
                        currentTile.getX() - currentBmp.getWidth(),
                        currentTile.getY() - currentBmp.getHeight(),
                        paint);
            }

            textX = currentTile.getX() - (object.getDefaultAnimation().getCurrentFrame().getWidth()/2) - (paint.measureText(Integer.toString(object.getHealth()))/2);
            textY = currentTile.getY();

            canvas.drawText(Integer.toString(object.getHealth()), textX, textY, strokePaint);
            canvas.drawText(Integer.toString(object.getHealth()), textX, textY, paint);
        }

        //draw ability animations
        for(Tile tile : grid.getLinearGrid())
        {
            if(tile.getAnimation() != null) {
                currentBmp = tile.getAnimation().getCurrentFrame();
                if(tile.isFriendly())
                {
                    if(currentBmp != null){

                        //Bitmap reverseBmp = Bitmap.createBitmap(currentBmp, 0, 0, currentBmp.getWidth(), currentBmp.getHeight(), reverse, true);
                        reverse.reset();
                        reverse.postScale(-1, 1);
                        reverse.postTranslate(tile.getX() - (Globals.screenWidth*.1667f) + currentBmp.getWidth(), tile.getY() - currentBmp.getHeight());
                        canvas.drawBitmap(currentBmp, reverse, paint);

                    }
                }
                else
                {
                    if (currentBmp != null) canvas.drawBitmap(currentBmp,
                            tile.getX() - currentBmp.getWidth(),
                            tile.getY() - currentBmp.getHeight(),
                            paint);
                }
            }
        }

        if(tutorialMode)
        {
            boolean finishFlag = false;
            String currentText;
            int numChars = 0;
            int loops = 0;
            while(!finishFlag)
            {
                numChars += tutorialTextMaxChars;
                loops++;
                if(numChars > tutorialText.length())
                {
                    finishFlag = true;
                    if(!tutorialText.isEmpty())
                    {
                        currentText = tutorialText.substring(numChars - tutorialTextMaxChars, tutorialText.length() - 1);
                        if(!currentText.isEmpty())
                            canvas.drawText(currentText, Globals.screenWidth*.02f, (Globals.screenHeight*0.24f + Globals.screenHeight*0.05f*loops), paint);
                    }
                }
                else
                {
                    currentText = tutorialText.substring(numChars - tutorialTextMaxChars, numChars);
                    //find a space
                    char space = 'a';
                    int index = tutorialTextMaxChars;
                    while(space != ' ')
                    {
                        --index;
                        if(index < 0)
                        {
                            index = 0;
                            break;
                        }
                        space = currentText.charAt(index);
                    }
                    index++;
                    currentText = currentText.substring(0, index);
                    numChars -= (tutorialTextMaxChars - index);
                    canvas.drawText(currentText, Globals.screenWidth*.02f, (Globals.screenHeight*0.24f + Globals.screenHeight*0.05f*loops), paint);
                }
            }
        }

    }

    private void control()
    {
        if(gameOver)
        {
            gameOverTimer--;
            if(gameOverTimer <= 0)
            {
                final GameActivity activity = (GameActivity) getContext();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //stuff that updates ui
                        activity.finish();
                    }
                });
            }
        }
    }

    public void tutorialButton(boolean next)
    {
        if(next)
        {
            tutorialNextTimer = -99;
            tutorialNextStep = true;
        }
        else
        {
            tutorialNextTimer = -99;
            tutorialCounter -= 2;
            if(tutorialCounter < 0) tutorialCounter = 0;
            tutorial();
        }
    }

    //This will halt our game loop and manage the thread
    public void pause()
    {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e)
        {

        }
    }

    //This will restart our game loop and restart the thread
    public void resume()
    {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void addEnemy(Enemy o)
    {
        displayList.add(o);
        enemies.add(o);
    }

    private void removeEnemy(Enemy o)
    {
        displayList.remove(o);
        enemies.remove(o);
    }

    private void advanceLevels()
    {
        level++;
        switch(level)
        {
            case 1:
                addEnemy(new MissileMan(grid.getTile(4, 1)));
                addEnemy(new Worm(grid.getTile(5, 1)));
                addEnemy(new Worm(grid.getTile(5, 0)));
                break;

            case 2:
                addEnemy(new FaceGun(grid.getTile(3, 1)));
                addEnemy(new MissileMan(grid.getTile(4, 2)));
                addEnemy(new MissileMan(grid.getTile(4, 0)));
                break;

            case 3:
                addEnemy(new FaceGun(grid.getTile(4, 1)));
                addEnemy(new FaceGun(grid.getTile(3, 1)));
                for(Enemy e : enemies)
                {
                    e.setHealth(160);
                }
                addEnemy(new Bubble(grid.getTile(5, 1)));
                break;

            case 4:
                addEnemy(new Bubble(grid.getTile(3, 1)));
                addEnemy(new Bubble(grid.getTile(5, 1)));
                addEnemy(new Worm(grid.getTile(4, 2)));
                break;

            case 5:
                addEnemy(new Bubble(grid.getTile(3, 1)));
                addEnemy(new MissileMan(grid.getTile(5, 1)));
                addEnemy(new Worm(grid.getTile(4, 2)));
                addEnemy(new FaceGun(grid.getTile(3, 2)));
                break;

            default:
                switchGameMode(GameMode.ending);
        }
    }

    public void addSelection(boolean hp)
    {
        if(hp)
        {
            player.upgradeHealth();
        }
        else
        {
            int abilityAdd = 4;
            int i;
            switch(level)
            {
                case 1:
                    for(i = 0; i < abilityAdd; ++i)
                    {
                        fullDeck.add(new Leech());
                    }
                    break;

                case 2:
                    for(i = 0; i < abilityAdd; ++i)
                    {
                        fullDeck.add(new Firebomb());
                    }
                    break;
                case 3:
                    for(i = 0; i < abilityAdd; ++i)
                    {
                        fullDeck.add(new Gatling());
                    }
                    break;

                case 4:
                    for(i = 0; i < abilityAdd; ++i)
                    {
                        fullDeck.add(new Lightning());
                    }
                    break;

                case 5:
                    break;

            }
        }

        player.setHealth(Math.min(player.getMaxHealth(), (player.getHealth() + player.getMaxHealth()/3)));
        switchGameMode(GameMode.starting);
    }


    public void switchGameMode(GameMode newMode)
    {
        final GameActivity activity = (GameActivity) getContext();
        switch(newMode)
        {
            case starting:
                advanceLevels();
                deck.refreshDeck(fullDeck);
            case playing:
                for(Enemy e : enemies)
                {
                    e.enableAI();
                }
                break;

            case ending:
                gameOver = true;
                for(Enemy e : enemies)
                {
                    e.disableAI();
                }

                if(player.getHealth() <= 0)
                {
                    //player died
                    if(gameMode != GameMode.ending)
                    {
                        displayList.remove(player);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                //stuff that updates ui
                                activity.gameOver();
                            }
                        });
                    }
                }
                else
                {
                    //player won the game
                    if(gameMode != GameMode.ending)
                    {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                //stuff that updates ui
                                activity.gameWon();
                            }
                        });
                    }
                }
                gameOver = true;
                break;

            case selecting:
                if(level == 5)
                {
                    switchGameMode(GameMode.ending);
                }
                else if(gameMode != GameMode.selecting && gameMode != GameMode.ending)
                {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //stuff that updates ui
                            activity.toggleSelectOn();
                        }
                    });
                }
                break;
        }
        gameMode = newMode;
    }
}
