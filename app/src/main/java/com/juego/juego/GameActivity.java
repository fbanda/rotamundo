package com.juego.juego;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import com.juego.Lector;
import com.juego.SoundManager;
import com.juego.objects.Ball;
import com.juego.objects.ChainWall;
import com.juego.objects.Door;
import com.juego.objects.DoorColor;
import com.juego.objects.DoorSwitch;
import com.juego.objects.Mine;
import com.juego.objects.SpikeRow;
import com.juego.objects.scanner.Button;
import com.juego.objects.scanner.ForceField;
import com.juego.objects.scanner.Spike;
import com.juego.sensors.GyroscopeManager;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import java.util.ArrayList;
import java.util.HashMap;

public class GameActivity extends BaseActivity {

    public static long frame = 0;

    public static final int GAME_STATE_RUNNING = 0;
    public static final int GAME_STATE_WON = 1;
    public static final int GAME_STATE_LOST = 2;
    private int gameState;
    private boolean restart;

    private World world;
    private GyroscopeManager sensorProvider;
    private double lastAngle = -1;
    private Lector lector;

    private ArrayList<ChainWall> walls;
    private ArrayList<Mine> mines;
    private ArrayList<SpikeRow> spikeRows;

    public static final int FRAMES_FOR_PUSH_LAG = 3*ActivityThread.FPS;
    private int pushLag = 0;
    private boolean[] colorSwitches = new boolean[DoorColor.values().length];
    private ArrayList<DoorSwitch> doorSwitches;
    private ArrayList<Door> doors;

    private Ball ball;

    public static final boolean CAMERA = true;
    public static final double ANGLE_STEP = Math.toRadians(8.0);

    public static final float ANGLE_OFFSET = 8f;
    private float cameraXOffset;
    private float cameraYOffset;
    private float angleXOffset;
    private float angleYOffset;

    public static final int MAX_LIVES = 3;
    private int lives;

    /* Rotar */
    private static final int NUM_ROTATED_IMAGES = 36;
    private ArrayList<Bitmap> rotatedKirbys;
    public static HashMap<Integer, Bitmap[]> rotatedDoors;

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                );
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
                screenWidth = metrics.widthPixels;
            }
        }
    }

    public ArrayList<Bitmap> generateKirbyBitmaps(int number) {
        ArrayList<Bitmap> kirbys = new ArrayList<>();
        float angle = 360/number;
        float angle2 = angle;
        Bitmap originalKirby = res.bitmap(R.drawable.ball_kirby);
        kirbys.add(originalKirby);

        while (angle2 < 360){
            Bitmap rotatedWithMargin = rotateBitmap(originalKirby, -angle2);
            Bitmap rotatedWithoutMargin = Bitmap.createBitmap(originalKirby.getWidth(), originalKirby.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(rotatedWithoutMargin);
            c.drawBitmap(rotatedWithMargin,
                    (rotatedWithoutMargin.getWidth() - rotatedWithMargin.getWidth())/2,
                    (rotatedWithoutMargin.getHeight() - rotatedWithMargin.getHeight())/2, p);
            rotatedWithMargin.recycle();

            kirbys.add(rotatedWithoutMargin);
            angle2 = angle2 + angle;
        }
        return kirbys;
    }

    public HashMap<Integer, Bitmap[]> generateDoorBitmaps(){
        HashMap<Integer, Bitmap[]> result = new HashMap<>();
        addBitmaps(result, R.drawable.door_r_open);
        addBitmaps(result, R.drawable.door_r_closed_1);
        addBitmaps(result, R.drawable.door_r_closed_2);
        addBitmaps(result, R.drawable.door_g_open);
        addBitmaps(result, R.drawable.door_g_closed_1);
        addBitmaps(result, R.drawable.door_g_closed_2);
        addBitmaps(result, R.drawable.door_p_open);
        addBitmaps(result, R.drawable.door_p_closed_1);
        addBitmaps(result, R.drawable.door_p_closed_2);
        return result;
    }

    public void addBitmaps(HashMap<Integer, Bitmap[]> result, int id){
        Bitmap[] bitmaps = new Bitmap[4];
        bitmaps[0] = res.bitmap(id);
        bitmaps[1] = rotateBitmap(res.bitmap(id), 90);
        bitmaps[2] = rotateBitmap(res.bitmap(id), 180);
        bitmaps[3] = rotateBitmap(res.bitmap(id), 270);
        result.put(id, bitmaps);
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
    /**/

    @Override
    protected int[] getNeededBitmaps(){
        return new int[]{R.drawable.ball_kirby, R.drawable.mine_gordo, R.drawable.spike,
                R.drawable.switch_r, R.drawable.switch_r_pressed, R.drawable.switch_g, R.drawable.switch_g_pressed,
                R.drawable.switch_p, R.drawable.switch_p_pressed, R.drawable.door_r_open, R.drawable.door_r_closed_1, R.drawable.door_r_closed_2,
                R.drawable.door_g_open, R.drawable.door_g_closed_1, R.drawable.door_g_closed_2,
                R.drawable.door_p_open, R.drawable.door_p_closed_1, R.drawable.door_p_closed_2};
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getActionMasked() == MotionEvent.ACTION_UP && gameState != GAME_STATE_RUNNING && pushLag <= 0){
                    restart = true;
                }
                return true;
            }
        });

        sensorProvider = new GyroscopeManager((SensorManager)getSystemService(SENSOR_SERVICE));
        lives = MAX_LIVES;

        //Crear bitmaps rotados
        rotatedKirbys = generateKirbyBitmaps(NUM_ROTATED_IMAGES);
        rotatedDoors = generateDoorBitmaps();

        //Inicializar el nivel

        lector = new Lector();
        lector.Leer(getAssets(), "nivel1.txt");

        Vec2 gravity = new Vec2(0f, 0f);
        world = new World(gravity);
        world.setContactListener(new GameContactListener(this));

        walls = new ArrayList<>();
        for(Vec2[] vertices : lector.getWalls()){
            walls.add(new ChainWall(world, vertices, true));
        }

        mines = new ArrayList<>();
        for(Vec2 vertex : lector.getMines()){
            mines.add(new Mine(world, vertex.x, vertex.y));
        }

        spikeRows = new ArrayList<>();
        for(Spike spike : lector.getSpikes()){
            spikeRows.add(new SpikeRow(res, world, spike.getRect(), spike.orientation, BaseActivity.drawScale, p));
        }
        doorSwitches = new ArrayList<>();
        for(Button button : lector.getButtons()){
            doorSwitches.add(new DoorSwitch(button.x, button.y, DoorColor.fromChar(button.color)));
        }
        doors = new ArrayList<>();
        for(ForceField forceField : lector.getForceFields()){
            doors.add(new Door(world, forceField.x, forceField.y, forceField.orientation, DoorColor.fromChar(forceField.color)));
        }

        for(int i=0; i<colorSwitches.length; i++){
            colorSwitches[i] = false;
        }
        for(Door door : doors){
            door.setActive(true);
        }

        ball = new Ball(world, lector.getPlayerX(), lector.getPlayerY());

    }

    public void removeMine(Mine mine){
        mine.invisible = true;
        mine.body.setActive(false);
    }

    public void activateMines(){
        for(Mine mine : mines){
            mine.invisible = false;
            mine.body.setActive(true);
        }
    }

    public void resetDoorSwitches(){
        for(int i=0; i<colorSwitches.length; i++) {
            colorSwitches[i] = false;
        }
        for(Door door : doors){
            door.setActive(true);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        sensorProvider.toggleListener();
        //rotatedDoors = generateDoorBitmaps();
        //rotatedKirbys = generateKirbyBitmaps(NUM_ROTATED_IMAGES);
    }

    @Override
    public void onPause(){
        super.onPause();
        sensorProvider.toggleListener();
        /*for(int i=1; i<rotatedKirbys.size(); i++){
            rotatedKirbys.get(i).recycle();
        }
        rotatedKirbys.clear();
        for(Bitmap[] bitmaps : rotatedDoors.values()){
            for(int i=1; i<bitmaps.length; i++){
                bitmaps[i].recycle();
            }
        }
        rotatedDoors.clear();*/
    }

    public void updateAngle(double screenAngle){
        if(lastAngle < 0){
            lastAngle = screenAngle;
        }else if(lastAngle != screenAngle){
            if((screenAngle > lastAngle && screenAngle - lastAngle < Math.PI)
                    || (screenAngle < lastAngle && lastAngle - screenAngle > Math.PI)){
                double newAngle = (lastAngle + ANGLE_STEP) % (2*Math.PI);
                if(newAngle < Math.PI/2 && lastAngle > Math.PI*3/2){
                    lastAngle = screenAngle; //Passed to over 360°
                }else {
                    lastAngle = Math.min(screenAngle, newAngle);
                }
            }else{
                double newAngle = (lastAngle - ANGLE_STEP + 2*Math.PI) % (2*Math.PI);
                if(newAngle > Math.PI*3/2 && lastAngle < Math.PI/2){
                    lastAngle = screenAngle; //Passed to below 0°
                }else {
                    lastAngle = Math.max(screenAngle, newAngle);
                }
            }
        }
    }

    public void pushSwitch(DoorColor color){
        int i = color.ordinal();
        colorSwitches[i] = !colorSwitches[i];
        for(Door door : doors){
            if(door.getColor() == color) {
                door.setActive(!colorSwitches[i]);
            }
        }
    }

    @Override
    public void update(){
        if(gameState == GAME_STATE_RUNNING) {
            if(frame == 0){
                SoundManager.playSound(this, R.raw.kirby_hi);
            }
            frame++;

            if (pushLag <= 0) {
                if (sensorProvider.pushedButton()) {
                    DoorSwitch pressedSwitch = ball.isOnTopOfSwitch(doorSwitches);
                    if (pressedSwitch != null) {
                        pushSwitch(pressedSwitch.getColor());
                        pushLag = FRAMES_FOR_PUSH_LAG;
                    }
                }
            } else {
                pushLag--;
            }
            sensorProvider.resetButtonState();

            double screenAngle = sensorProvider.getScreenAngle();
            updateAngle(screenAngle);
            ball.update(lastAngle);
            if (lives == 0) {
                gameOver();
                return;
            }

            world.step(1f / ActivityThread.FPS, 6, 2);

            Vec2 position = ball.getPosition();
            cameraXOffset = -position.x;
            cameraYOffset = -position.y;
            if (CAMERA) {
                angleXOffset = ANGLE_OFFSET * (float) Math.cos(lastAngle);
                angleYOffset = -ANGLE_OFFSET * (float) Math.sin(lastAngle);
            } else {
                angleXOffset = 0;
                angleYOffset = 0;
            }

            if (checkVictoryCondition()) {
                win();
            }
        }else{
            if(pushLag > 0){
                pushLag--;
            }
            if(restart){
                restart = false;
                restartGame();
            }
        }
    }

    @Override
    public void draw(Canvas c) {
        if(gameState == GAME_STATE_RUNNING) {
            p.setColor(Color.WHITE);
            c.drawRect(0, 0, screenWidth, screenHeight, p);
            double screenAngle = Math.toDegrees(lastAngle) + 90;
            int index = (int) (Math.round(screenAngle / (360 / NUM_ROTATED_IMAGES)));
            index = index % NUM_ROTATED_IMAGES;

            for (DoorSwitch doorSwitch : doorSwitches) {
                doorSwitch.draw(res, c, p, BaseActivity.getCombinedScale(), cameraXOffset + angleXOffset, cameraYOffset + angleYOffset,
                        colorSwitches[doorSwitch.getColor().ordinal()]);
            }

            p.setColor(ChainWall.WALL_COLOR);
            for (ChainWall wall : walls) {
                wall.draw(res, c, p, BaseActivity.getCombinedScale(), cameraXOffset + angleXOffset, cameraYOffset + angleYOffset);
            }

        /*p.setColor(Color.BLACK);
        c.drawCircle(screenWidth/2, screenHeight/2, 3*drawScale, p);*/

            Bitmap rotatedKirby = rotatedKirbys.get(index);
            ball.drawBodyAt(rotatedKirby, c, p, BaseActivity.getCombinedScale(), screenWidth / 2 + angleXOffset * BaseActivity.getCombinedScale(), screenHeight / 2 + angleYOffset * BaseActivity.getCombinedScale());

            for (Mine mine : mines) {
                mine.draw(res, c, p, BaseActivity.getCombinedScale(), cameraXOffset + angleXOffset, cameraYOffset + angleYOffset);
            }

            for (SpikeRow spikeRow : spikeRows) {
                spikeRow.draw(res, c, p, BaseActivity.getCombinedScale(), cameraXOffset + angleXOffset, cameraYOffset + angleYOffset);
            }

            for (Door door : doors) {
                door.drawDoor(c, p, BaseActivity.getCombinedScale(), cameraXOffset + angleXOffset, cameraYOffset + angleYOffset,
                        colorSwitches[door.getColor().ordinal()]);
            }

            c.drawText("Vida: " + lives, screenWidth - 100, 40, p);

            float density = getResources().getDisplayMetrics().density;
            p.setColor(Color.WHITE);
            c.drawRect(0, 0, Math.round(140*density), Math.round(30*density), p);
            p.setTextAlign(Paint.Align.LEFT);
            p.setColor(Color.BLACK);
            c.drawText("Tiempo: " + getTime(), 5*density, 20*density, p);
            p.setTextAlign(Paint.Align.CENTER);
        }else{
            p.setColor(Color.BLACK);
            c.drawRect(0, 0, screenWidth, screenHeight, p);
            p.setColor(Color.WHITE);
            /*c.drawText(gameState == GAME_STATE_WON ? getString(R.string.win_message, getTime()) : getString(R.string.lose_message),
                    screenWidth/2, screenHeight/2, p);*/
            float density = getResources().getDisplayMetrics().density;
            if(gameState == GAME_STATE_WON){
                c.drawText(getString(R.string.win_message1), screenWidth/2, screenHeight/2 - 30*density, p);
                c.drawText(getString(R.string.win_message2, getTime()), screenWidth/2, screenHeight/2 - 10*density, p);
            }else{
                c.drawText(getString(R.string.lose_message), screenWidth/2, screenHeight/2 - 20*density, p);
            }
            c.drawText(getString(R.string.retry_message), screenWidth/2, screenHeight/2 + 20*density, p);
        }
    }

    public String getTime(){
        int minutes = (int)(frame / ActivityThread.FPS / 60);
        int seconds = (int)(frame / ActivityThread.FPS) % 60;
        int centiseconds = (int)(frame * 100 / ActivityThread.FPS) % 100;
        return String.format("%02d:%02d:%02d", minutes, seconds, centiseconds);
    }

    public void reduceLives(){
        lives--;
        if(lives == 0){
            SoundManager.playSound(this, R.raw.kirby_iieeeeeeee);
        }else{
            SoundManager.playSound(this, R.raw.kirby_aaaaahhhh);
        }
    }

    private void win(){
        gameState = GAME_STATE_WON;
        pushLag = 2*ActivityThread.FPS;
        SoundManager.playSound(this, R.raw.tu_tu_turu_turu_tu);
    }

    private void gameOver(){
        gameState = GAME_STATE_LOST;
        pushLag = 2*ActivityThread.FPS;
    }

    public void restartGame(){
        frame = 0;

        lives = MAX_LIVES;
        activateMines();
        resetDoorSwitches();
        ball.setPosition(new Vec2(lector.getPlayerX(), lector.getPlayerY()));
        gameState = GAME_STATE_RUNNING;
    }

    private boolean checkVictoryCondition(){
        Vec2 topLeftCorner = lector.victoryRect.get(0);
        Vec2 botRightCorner = lector.victoryRect.get(1);
        Vec2 ballPos = ball.getPosition();
        return ballPos.x >= topLeftCorner.x && ballPos.x <= botRightCorner.x
                && ballPos.y>=topLeftCorner.y && ballPos.y<= botRightCorner.y;

    }

}
