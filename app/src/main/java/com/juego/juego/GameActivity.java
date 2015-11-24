package com.juego.juego;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.SensorManager;
import android.os.Bundle;
import com.juego.Lector;
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

    private int lives;

    /* Rotar */
    private static final int NUM_ROTATED_IMAGES = 36;
    private ArrayList<Bitmap> rotatedKirbys;
    public static HashMap<Integer, Bitmap[]> rotatedDoors;

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

    public HashMap<Integer, Bitmap[]> generateDoorBitmaps(int number){
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
        return new int[]{R.drawable.ball_kirby, R.drawable.mine_gordo, R.drawable.spike};
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        sensorProvider = new GyroscopeManager((SensorManager)getSystemService(SENSOR_SERVICE));
        lives = 3;

        //Crear bitmaps rotados
        rotatedKirbys = generateKirbyBitmaps(NUM_ROTATED_IMAGES);
        rotatedDoors = generateDoorBitmaps(4);

        //Inicializar el nivel

        lector = new Lector();
        lector.Leer(getAssets(), "testVec2.txt");

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
    }

    @Override
    public void onPause(){
        super.onPause();
        sensorProvider.toggleListener();
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
            door.setActive(!colorSwitches[i]);
        }
    }

    @Override
    public void update(){
        Door.frame++;

        if(pushLag <= 0) {
            if (sensorProvider.pushedButton()) {
                DoorSwitch pressedSwitch = ball.isOnTopOfSwitch(doorSwitches);
                if (pressedSwitch != null) {
                    pushSwitch(pressedSwitch.getColor());
                    pushLag = FRAMES_FOR_PUSH_LAG;
                }
            }
        }else{
            pushLag--;
        }
        sensorProvider.resetButtonState();

        double screenAngle = sensorProvider.getScreenAngle();
        updateAngle(screenAngle);
        ball.update(lastAngle);
        if(lives==0){
            gameOver();
            return;
        }

        world.step(1f / ActivityThread.FPS, 6, 2);
        if(checkVictoryCondition()){
            restartGame();
        }

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
    }

    @Override
    public void draw(Canvas c) {
        p.setColor(Color.WHITE);
        c.drawRect(0, 0, screenWidth, screenHeight, p);
        double screenAngle = Math.toDegrees(lastAngle) + 90;
        int index = (int)(Math.round(screenAngle / (360/NUM_ROTATED_IMAGES)));
        index = index % NUM_ROTATED_IMAGES;

        for(DoorSwitch doorSwitch : doorSwitches){
            doorSwitch.draw(res, c, p, BaseActivity.getCombinedScale(), cameraXOffset + angleXOffset, cameraYOffset + angleYOffset,
                    colorSwitches[doorSwitch.getColor().ordinal()]);
        }

        p.setColor(ChainWall.WALL_COLOR);
        for(ChainWall wall : walls){
            wall.draw(res, c, p, BaseActivity.getCombinedScale(), cameraXOffset + angleXOffset, cameraYOffset + angleYOffset);
        }

        /*p.setColor(Color.BLACK);
        c.drawCircle(screenWidth/2, screenHeight/2, 3*drawScale, p);*/

        Bitmap rotatedKirby = rotatedKirbys.get(index);
        ball.drawBodyAt(rotatedKirby, c, p, BaseActivity.getCombinedScale(), screenWidth / 2 + angleXOffset * BaseActivity.getCombinedScale(), screenHeight / 2 + angleYOffset * BaseActivity.getCombinedScale());

        for(Mine mine : mines){
            mine.draw(res, c, p, BaseActivity.getCombinedScale(), cameraXOffset + angleXOffset, cameraYOffset + angleYOffset);
        }

        for(SpikeRow spikeRow : spikeRows) {
            spikeRow.draw(res, c, p, BaseActivity.getCombinedScale(), cameraXOffset + angleXOffset, cameraYOffset + angleYOffset);
        }

        for(Door door : doors){
            door.drawDoor(c, p, BaseActivity.getCombinedScale(), cameraXOffset + angleXOffset, cameraYOffset + angleYOffset,
                    colorSwitches[door.getColor().ordinal()]);
        }

        c.drawText("Lives: " + lives, screenWidth-100, 40, p);
    }

    public void reduceLives(){
        lives--;
    }

    private void gameOver(){
        restartGame();
    }

    public void restartGame(){
        lives = 3;
        activateMines();
        resetDoorSwitches();
        ball.setPosition( new Vec2(lector.getPlayerX(), lector.getPlayerY()));
    }

    private boolean checkVictoryCondition(){
        Vec2 topLeftCorner = lector.victoryRect.get(0);
        Vec2 botRightCorner = lector.victoryRect.get(1);
        Vec2 ballPos = ball.getPosition();
        return ballPos.x >= topLeftCorner.x && ballPos.x <= botRightCorner.x
                && ballPos.y>=topLeftCorner.y && ballPos.y<= botRightCorner.y;

    }

}
