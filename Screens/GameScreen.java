package com.dropkick.btd3.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.dropkick.btd3.Bloons.Bloon;
import com.dropkick.btd3.Hazards.Glue;
import com.dropkick.btd3.Hazards.Pineapple;
import com.dropkick.btd3.Hazards.Spikes;
import com.dropkick.btd3.Helpers.BodyCreator;
import com.dropkick.btd3.Helpers.ContactListener;
import com.dropkick.btd3.Helpers.Paths;
import com.dropkick.btd3.Helpers.Rounds;
import com.dropkick.btd3.Scenes.GameScreenUI;
import com.dropkick.btd3.Towers.Cannon;
import com.dropkick.btd3.Towers.IceTower;
import com.dropkick.btd3.Towers.MonkeyBeacon;
import com.dropkick.btd3.Towers.Monkeys.BoomerangMonkey;
import com.dropkick.btd3.Towers.Monkeys.DartMonkey;
import com.dropkick.btd3.Towers.Monkeys.SpikePult;
import com.dropkick.btd3.Towers.Monkeys.SuperMonkey;
import com.dropkick.btd3.Towers.TackShooter;
import com.dropkick.btd3.Towers.Tower;

import java.util.ArrayList;
import java.util.HashMap;

public class GameScreen implements Screen {

    //WORLD STUFF
    public static int PPM = 100;

    public static short NOTHING_BIT = 0;
    public static short TRACK_BIT = 0x1 << 1;
    public static short REACH_BIT = 0x1 << 2;
    public static short REGULAR_BLOON_BIT = 0x1 << 3;
    public static short FROZEN_BLOON_BIT = 0x1 << 4;
    public static short LEAD_BLOON_BIT = 0x1 << 5;
    public static short BLACKWHITE_BLOON_BIT = 0x1 << 6;
    public static short REGULAR_PROJECTILE_BIT = 0x1 << 7;
    public static short FROZEN_PROJECTILE_BIT = 0x1 << 8;
    public static short LEAD_PROJECTILE_BIT = 0x1 << 9;
    public static short BLACKWHITE_PROJECTILE_BIT = 0x1 << 10;

    private World world = new World(new Vector2(0, 0), true);
    private float timeSinceLastUpdate = 0f;
    private final ContactListener contactListener = new ContactListener(this);
    private final Box2DDebugRenderer b2dr = new Box2DDebugRenderer();
    private final OrthographicCamera camera = new OrthographicCamera(800f / PPM, 480f / PPM);
    private final FitViewport viewport = new FitViewport(800f / PPM, 480f / PPM, camera);

    private final BodyCreator bodyCreator = new BodyCreator(world);

    //UI
    private final GameScreenUI gameScreenUI;

    public static int LIVES = 75;
    public static int MONEY = 650;
    private final int initialLives;

    //Paths
    private final Paths paths = new Paths();
    private final CatmullRomSpline<Vector2> balloonPathA = new CatmullRomSpline<>(paths.getPathA(), false);

    //Bloons
    private final ArrayList<Bloon> activeBloon = new ArrayList<>();
    private final ArrayList<Bloon> inactiveBloon = new ArrayList<>();

    /*Towers*/

    public int amountOfTowers;

    //Towers
    private final ArrayList<Tower> towers = new ArrayList<>();

    /////Buying towers
    public boolean buyingTower;
    public int towerBeingBought;//0 - Dart Monkey, 1 - Tack Shooter, 2 - Boomerang Monkey, 3 - Spike-o-Pult, 4 - Cannon, 5 - Ice Ball, 6 - Monkey Beacon, 7 - Super Monkey
    public int towerBeingBoughtCost;

    private Vector2 touchPosition = new Vector2();
    private final ArrayList<Body> spawnTowerBody = new ArrayList<>();
    private int spawnTowerAmount;
    ////Handling Towers
    private final ArrayList<ArrayList<Integer>> bloonsInTowerRange = new ArrayList<>();
    private final ArrayList<Integer> priorityBloonForTower = new ArrayList<>();

    private float bloonBuffer;

    private int selectedTower = -1;
    private int selectedBeacon = -1;

    private final ArrayList<MonkeyBeacon> monkeyBeacon = new ArrayList<>();

    public boolean buyingHazard;
    public int hazardBeingBought;

    private final ArrayList<Spikes> spikeList = new ArrayList<>();
    private final ArrayList<Glue> glueList = new ArrayList<>();
    private final ArrayList<Pineapple> pineappleList = new ArrayList<>();
    public int amountOfSpikes;
    public int amountOfGlue;
    public int amountOfPineapple;

    private boolean superMonkeyStormActive;

    private final ArrayList<Bloon> moabList = new ArrayList<>();

    private final Rounds rounds = new Rounds();
    private String[] roundBloonList = new String[]{"Yellow"};
    private int[] roundBloonCount = new int[]{0};//bloonCount of each bloon is reduced by one to account for arrays starting at 0
    public static int ROUND = 1;

    private int current;
                    //Bloon#  //AmountOfDuplications
    private final HashMap<Integer, Integer> bloonsToDuplicate = new HashMap<>();//Bloons get duplicated when destroyed depending on type, this happens during collision detection ; during collision detection the world is stepping and new bodies cannot be created without causing an error so it must be done on the next frame

    public boolean roundBegan;

    private final Vector3 labelPosition = new Vector3();

    private boolean lostGame;

    public GameScreen(SpriteBatch batch, int difficulty) {
        world.setContactListener(contactListener);
        spawnTowerBody.add(bodyCreator.createCircle(13, true, BodyDef.BodyType.DynamicBody, "SpawnCircle", GameScreen.TRACK_BIT, TRACK_BIT));
        spawnTowerBody.get(0).setTransform(4.5f, 1, 0);
        //test
        bodyCreator.createChainShape(paths.getPathAEdgeA(), false, BodyDef.BodyType.StaticBody, "Track0", TRACK_BIT, TRACK_BIT);
        bodyCreator.createChainShape(paths.getPathAEdgeB(), false, BodyDef.BodyType.StaticBody, "Track1", TRACK_BIT, TRACK_BIT);
        bodyCreator.createChainShape(paths.getPathA(), false, BodyDef.BodyType.StaticBody, "Track2", TRACK_BIT, TRACK_BIT);
        for(int i = 0 ; i < 10 ; i++) {
            bloonsToDuplicate.put(i, 0);
        }
        float modifier = 1f;
        switch (difficulty) {
            case 0:
                LIVES = 100;
                modifier = 0.85f;
                break;
            case 2:
                LIVES = 50;
                modifier = 1.05f;
                break;
        }
        initialLives = LIVES;
        gameScreenUI = new GameScreenUI(this, batch, modifier);
        Gdx.input.setCatchKey(Input.Keys.BACK, true);
    }

    @Override
    public void show() {}

    private void stepWorld() {
        if(!world.isLocked()) {
            timeSinceLastUpdate += Gdx.graphics.getDeltaTime(); //Accumulate delta time
            while(timeSinceLastUpdate >= 1f / 120f){ //If the accumulated delta-time is greater than, do a physics update - this can happen multiple times in a single frame, depending on fps/lag/physicsUpdateSpeed
                world.step(1 / 120f, 8, 3);
                timeSinceLastUpdate -= 1 / 120f; //Subtract physicsUpdateSpeed from timeSinceLastUpdate  - if timeSinceLastUpdate is STILL >= physicsUpdateSpeed, repeat, if not, the loop breaks
            }
        }
    }

    private void update(float dt) {
        touchPosition.set(Gdx.input.getX(), Gdx.input.getY());touchPosition = viewport.unproject(touchPosition);
        if(roundBegan) {
            handleDuplications();
            beginRound();
            moveBloons();
            handleRoundOver();
        }
        handleTowers(dt);
        handleBuyingTower();
        handleBuyingHazard();
        handlePineapples();
        if(Gdx.input.justTouched()) handleSelectingTower();
    }

    private void handleRoundOver() {
        if(activeBloon.size() == 0 && current == roundBloonList.length) {
            MONEY += (99 + ROUND);
            ROUND++;
            roundBloonList = rounds.getBloonListForRound(ROUND - 2);
            roundBloonCount = rounds.getBloonCountForRound(ROUND - 2);
            for(int i = 0 ; i < spikeList.size() ; i++) spikeList.get(i).setInActive();
            for(int i = 0 ; i < glueList.size() ; i++) glueList.get(i).setInActive();
            roundBegan = false;
            gameScreenUI.setBeginRoundVisibility(true);
            current = 0;
        }
    }

    private void handleDuplications() {
        for(int i = 0 ; i < bloonsToDuplicate.size() ; i++) {
            for(int j = 0 ; j < bloonsToDuplicate.get(i) ; j++) {
                activeBloon.add(activeBloon.get(i).cpy());
            }//Special case for rainbow bloons where it splits into two black and two white
            if(bloonsToDuplicate.get(i) > 0) {
                if("Black".equals(activeBloon.get(i).type) && bloonsToDuplicate.get(i) == 3) {
                    activeBloon.get(activeBloon.size() - 1).create("White");
                    activeBloon.get(activeBloon.size() - 2).create("White");
                }
            }
            bloonsToDuplicate.put(i, 0);
        }
    }

    private void beginRound() {
        if(current < roundBloonList.length) {
            bloonBuffer += Gdx.graphics.getDeltaTime();
            if(bloonBuffer > 0.5f) {
                createBloon(roundBloonList[current]);
                roundBloonCount[current]--;
                if(roundBloonCount[current] < 0) current++;
                bloonBuffer = 0;
            }
        }
    }

    private void createBloon(String type) {
        if(inactiveBloon.size() > 0) {
            inactiveBloon.get(0).body.setTransform(0, 0, 0);
            inactiveBloon.get(0).create(type);
            inactiveBloon.get(0).distance = 0;
            activeBloon.add(inactiveBloon.get(0));
            inactiveBloon.remove(inactiveBloon.get(0));
        } else {
            activeBloon.add(new Bloon(bodyCreator, type));
        }
        if("M.O.A.B".equals(type)) moabList.add(activeBloon.get(activeBloon.size() - 1));
    }

    private void handleBuyingTower() {
        if(buyingTower) {
            spawnTowerBody.get(spawnTowerAmount).setAwake(true);
            if(!Gdx.input.isTouched() && Gdx.input.getX() > Gdx.graphics.getWidth() / 8.4f && Gdx.input.getX() < Gdx.graphics.getWidth() / 1.45f && !contactListener.towerTouchingTrack[0] && !contactListener.towerTouchingTrack[1] && !contactListener.towerTouchingTrack[2] && !contactListener.towerTouchingTower) {
                MONEY -= towerBeingBoughtCost;
                switch (towerBeingBought) {
                    case 0:
                        towers.add(new DartMonkey(bodyCreator, amountOfTowers, touchPosition));
                        break;
                    case 1:
                        towers.add(new TackShooter(bodyCreator, amountOfTowers, touchPosition));
                        break;
                    case 2:
                        towers.add(new BoomerangMonkey(bodyCreator, amountOfTowers, touchPosition));
                        break;
                    case 3:
                        towers.add(new SpikePult(bodyCreator, amountOfTowers, touchPosition));
                        break;
                    case 4:
                        towers.add(new Cannon(bodyCreator, amountOfTowers, touchPosition));
                        break;
                    case 5:
                        towers.add(new IceTower(bodyCreator, amountOfTowers, touchPosition));
                        break;
                    case 7:
                        towers.add(new SuperMonkey(bodyCreator, amountOfTowers, touchPosition));
                        break;
                }
                switch (towerBeingBought) {
                    case 6:
                        monkeyBeacon.add(new MonkeyBeacon(touchPosition.cpy()));
                        bodyCreator.createCircle(monkeyBeacon.get(monkeyBeacon.size() - 1).range, true, BodyDef.BodyType.StaticBody, "DebuggingPurposes", GameScreen.NOTHING_BIT, GameScreen.NOTHING_BIT).setTransform(touchPosition.cpy(), 0);
                        float distance;
                        for (int i = 0; i < amountOfTowers; i++) {
                            if (towers.get(i).monkeyTower) {
                                distance = (float) (Math.sqrt(Math.pow(towers.get(i).position.x - touchPosition.x, 2) + Math.pow(towers.get(i).position.y -touchPosition.y, 2)));
                                if (distance < monkeyBeacon.get(monkeyBeacon.size() - 1).range / PPM && !towers.get(i).upgradedByBeaconTower[0]) {
                                    towers.get(i).increaseRange(0.10f);
                                    towers.get(i).upgradedByBeaconTower[0] = true;
                                }
                            }
                        }
                        break;
                    case 0:
                    case 1:
                    case 3:
                    case 7:
                        if(amountOfTowers > -1) {
                            for(int i = 0 ; i < monkeyBeacon.size() ; i++) {
                                distance = (float) (Math.sqrt(Math.pow(towers.get(amountOfTowers).position.x - monkeyBeacon.get(i).position.x, 2) + Math.pow(towers.get(amountOfTowers - 1).position.y - monkeyBeacon.get(i).position.y, 2)));
                                if (distance < monkeyBeacon.get(i).range / PPM && !towers.get(amountOfTowers).upgradedByBeaconTower[0]) {
                                    towers.get(amountOfTowers).increaseRange(0.10f);
                                    towers.get(amountOfTowers).upgradedByBeaconTower[0] = true;
                                    break;
                                }
                            }
                        }
                        //NO BREAK IS INTENTIONAL, MONKEYS ARE TOWERS BUT SHOULD ALSO GET RANGE INCREASE IF IN RANGE OF A BEACON
                    default:
                        bloonsInTowerRange.add(new ArrayList<Integer>());
                        priorityBloonForTower.add(-1);
                        towers.get(amountOfTowers).buyTower();
                        towers.get(amountOfTowers).type = towerBeingBought;
                        amountOfTowers++;
                        break;
                }
                spawnTowerBody.get(spawnTowerAmount).setType(BodyDef.BodyType.StaticBody);
                spawnTowerAmount++;
                buyingTower = false;
                spawnTowerBody.add(bodyCreator.createCircle(13, true, BodyDef.BodyType.DynamicBody, "SpawnCircle", GameScreen.TRACK_BIT, GameScreen.TRACK_BIT));
                spawnTowerBody.get(spawnTowerAmount).setTransform(4.5f, 1, 0);
            } else if(Gdx.input.isTouched()){
                spawnTowerBody.get(spawnTowerAmount).setTransform(touchPosition, 0);
            }

            if(!Gdx.input.isTouched() && Gdx.input.getX() > Gdx.graphics.getWidth() / 1.45 && Gdx.input.getY() > Gdx.graphics.getHeight() / 3.25f) {
                buyingTower = false;
                spawnTowerBody.get(spawnTowerAmount).setTransform(4.5f, 1, 0);
            }
        }
    }

    private void handleBuyingHazard() {
        if(buyingHazard) {
            spawnTowerBody.get(spawnTowerAmount).setAwake(true);
            if(!Gdx.input.isTouched() && Gdx.input.getX() > Gdx.graphics.getWidth() / 8.4f && Gdx.input.getX() < Gdx.graphics.getWidth() / 1.45f && (contactListener.towerTouchingTrack[0] || contactListener.towerTouchingTrack[1] || contactListener.towerTouchingTrack[2])) {
                switch (hazardBeingBought) {
                    case 0:
                        for(int i  = 0 ; i < spikeList.size() ; i++) {
                            if(spikeList.get(i).hitsLeft == 0) {
                                spikeList.get(i).setActive(touchPosition);
                                return;
                            }
                        }
                        spikeList.add(new Spikes(bodyCreator, touchPosition.cpy(), amountOfSpikes));
                        amountOfSpikes++;
                        break;
                    case 1:
                        for(int i  = 0 ; i < glueList.size() ; i++) {
                            if(glueList.get(i).slowCount == 0) {
                                glueList.get(i).setActive(touchPosition.cpy());
                                return;
                            }
                        }
                        glueList.add(new Glue(bodyCreator, touchPosition.cpy(), amountOfGlue));
                        amountOfGlue++;
                        break;
                    case 2:
                        for(int i  = 0 ; i < pineappleList.size() ; i++) {
                            if(!pineappleList.get(i).active) {
                                pineappleList.get(i).setActive(touchPosition.cpy());
                                return;
                            }
                        }
                        pineappleList.add(new Pineapple(bodyCreator, touchPosition.cpy(), amountOfPineapple));
                        amountOfPineapple++;
                        break;
                }
                buyingHazard = false;
                MONEY -= 30;
                spawnTowerBody.get(spawnTowerAmount).setTransform(4.5f, 1, 0);
            } else if(Gdx.input.isTouched()){
                spawnTowerBody.get(spawnTowerAmount).setTransform(touchPosition, 0);
            }

            if(!Gdx.input.isTouched() && Gdx.input.getX() > Gdx.graphics.getWidth() / 1.45 && Gdx.input.getY() > Gdx.graphics.getHeight() / 3.25f) {
                buyingHazard = false;
                spawnTowerBody.get(spawnTowerAmount).setTransform(4.5f, 1, 0);
            }
        }
    }

    private void handleSelectingTower() {
        for(int i = 0 ; i < towers.size() ; i++) {
            selectedTower = touchingTower(selectedTower, i, towers.get(i).position, 7.5f / PPM, 7.5f / PPM);
            if(selectedTower > -1) break;
        }
        for(int i = 0 ; i < monkeyBeacon.size() ; i++) {
            selectedBeacon = touchingTower(selectedBeacon, i, monkeyBeacon.get(i).position, 7.5f / PPM, 7.5f / PPM);
            if(selectedBeacon > -1) break;
        }
        int type = selectedTower > -1 ? towers.get(selectedTower).type : 6;
        if(selectedTower > -1) {
            for(int i = 0 ; i < 2 ; i++) {
                if(towers.get(selectedTower).upgradeBought(i * 2) && !towers.get(selectedTower).upgradeBought(1 + (i * 2))) {
                    gameScreenUI.setUpgradeButtonVisibility(i * 2, false, type);
                    gameScreenUI.setUpgradeButtonVisibility(1 + (i * 2), true, type);
                }
                if(!towers.get(selectedTower).upgradeBought(i * 2)) {
                    gameScreenUI.setUpgradeButtonVisibility(i * 2, true, type);
                    gameScreenUI.setUpgradeButtonVisibility(1 + (i * 2), false, type);
                }
                if(towers.get(selectedTower).upgradeBought((i * 2)) && towers.get(selectedTower).upgradeBought(1 + (i * 2))) {
                    gameScreenUI.setUpgradeButtonVisibility(i * 2, false, type);
                    gameScreenUI.setUpgradeButtonVisibility(1 + (i * 2), false, type);
                }
            }
        } else if(selectedBeacon > -1) {
            for(int i = 0 ; i < 2 ; i++) {
                if(monkeyBeacon.get(selectedBeacon).upgraded[i * 2] && !monkeyBeacon.get(selectedBeacon).upgraded[1 + (i * 2)]) {
                    gameScreenUI.setUpgradeButtonVisibility(i * 2, false, type);
                    gameScreenUI.setUpgradeButtonVisibility(1 + (i * 2), true, type);
                }
                if(!monkeyBeacon.get(selectedBeacon).upgraded[i * 2]) {
                    gameScreenUI.setUpgradeButtonVisibility(i * 2, true, type);
                    gameScreenUI.setUpgradeButtonVisibility(1 + (i * 2), false, type);
                }
                if(monkeyBeacon.get(selectedBeacon).upgraded[(i * 2)] && monkeyBeacon.get(selectedTower).upgraded[1 + (i * 2)]) {
                    gameScreenUI.setUpgradeButtonVisibility(i * 2, false, type);
                    gameScreenUI.setUpgradeButtonVisibility(1 + (i * 2), false, type);
                }
            }
        } else {
            for(int i = 0 ; i < 4 ; i++) gameScreenUI.setUpgradeButtonVisibility(i, false, -1);
        }
    }

    private int touchingTower(int current, int i, Vector2 position, float width, float height) {
        if(Gdx.input.getX() < Gdx.graphics.getWidth() / 20) return current;
        return (touchPosition.x < position.x + width && touchPosition.x > position.x - width &&
                touchPosition.y < position.y + height && touchPosition.y > position.y - height) ? i : -1;
    }

    private void handlePineapples() {
        if(roundBegan) for(int i = 0 ; i < pineappleList.size() ; i++) pineappleList.get(i).countDown();
    }

    private void handleTowers(float dt) {
        if(amountOfTowers > 0) {
            for(int i = 0 ; i < amountOfTowers ; i++) {
                towers.get(i).handleProjectiles(dt);
                if(priorityBloonForTower.get(i) >= 0) towers.get(i).attackBloon(activeBloon.get(priorityBloonForTower.get(i)).body.getPosition(), dt);
            }
        }
    }

    public void upgradeSelectedTower(int upgrade) {
        if(selectedTower != -1) {
            if(!towers.get(selectedTower).upgradeBought(upgrade)) {
                switch (upgrade) {
                    case 0:
                        towers.get(selectedTower).firstUpgrade();
                        break;
                    case 1:
                        if(towers.get(selectedTower).upgradeBought(0)) towers.get(selectedTower).secondUpgrade();
                        break;
                    case 2:
                        towers.get(selectedTower).thirdUpgrade();
                        break;
                    case 3:
                        if(towers.get(selectedTower).upgradeBought(2)) towers.get(selectedTower).fourthUpgrade();
                        break;
                }
            }
        }
        if(selectedBeacon != -1) {
            if(!monkeyBeacon.get(selectedBeacon).upgraded[upgrade]) {
                float distance;
                switch (upgrade) {
                    case 0:
                        monkeyBeacon.get(selectedBeacon).increaseRange();
                        for(int i  = 0 ; i < towers.size() ; i++) {
                            if(towers.get(i).monkeyTower) {
                                distance = (float)(Math.sqrt(Math.pow(towers.get(i).position.x - monkeyBeacon.get(selectedBeacon).position.x, 2) + Math.pow(towers.get(i).position.y - monkeyBeacon.get(selectedBeacon).position.y, 2)));
                                if(distance < 100 && distance > 77.5f) towers.get(i).increaseRange(0.1f);//if towers previously not in range now are then increase range
                            }
                        }
                        break;
                    case 1:
                        for(int i  = 0 ; i < towers.size() ; i++) {
                            if(towers.get(i).monkeyTower) {
                                distance = (float)(Math.sqrt(Math.pow(towers.get(i).position.x - monkeyBeacon.get(selectedBeacon).position.x, 2) + Math.pow(towers.get(i).position.y - monkeyBeacon.get(selectedBeacon).position.y, 2)));
                                if(distance < monkeyBeacon.get(selectedBeacon).range && !towers.get(i).upgradedByBeaconTower[1]) {
                                    towers.get(i).decreaseReloadSpeed(18);
                                    towers.get(i).upgradedByBeaconTower[1] = true;
                                }
                            }
                        }
                        break;
                    case 2:
                        superMonkeyStormActive = true;
                        gameScreenUI.secretUnlocked();
                        break;
                }
                monkeyBeacon.get(selectedBeacon).upgraded[upgrade] = true;
            }
            if(upgrade == 3) sendSuperMonkeyStorm();
        }
    }

    public void handleBloonInTowerRange(int i, Body body, boolean entering) {
        if(entering) {
            for(int j = 0; j < activeBloon.size() ; j++) {
                if (activeBloon.get(j).body == body) {
                    bloonsInTowerRange.get(i).add(j);
                    break;
                }
            }
        } else {
            for(int j = 0 ; j < bloonsInTowerRange.get(i).size() ; j++) {
                if (activeBloon.get(bloonsInTowerRange.get(i).get(j)).body == body) {
                    bloonsInTowerRange.get(i).remove(j);
                    break;
                }
            }
        }
        resetBloonPriority(i);
    }

    private void resetBloonPriority(int i) {
        int targetBloon = bloonsInTowerRange.get(i).size() > 0 ? bloonsInTowerRange.get(i).get(0) : -1;
        for(int j = 0 ; j < bloonsInTowerRange.get(i).size() ; j++) {
            if(activeBloon.get(bloonsInTowerRange.get(i).get(j)).distance > activeBloon.get(targetBloon).distance) {
                targetBloon = bloonsInTowerRange.get(i).get(j);
            }
            //You're cute, hay
        }
        priorityBloonForTower.set(i, targetBloon);
    }

    public void handleBloonInIceTowerRange(boolean entering, Body bloonBody, int i) {
        String info = towers.get(i).info();

        for(int j = 0; j < activeBloon.size() ; j++) {
            if(activeBloon.get(j).body == bloonBody) {
                if(entering) {
                    if(info.contains("Pop") && Math.random() > 0.25f) {
                        if(!handleBloonDamagedAndDestroyed(j)) {
                            activeBloon.get(j).handleFrozen();
                        }
                    } else {
                        activeBloon.get(j).handleFrozen();
                    }
                } else {
                    activeBloon.get(j).handleUnFrozen(info.contains("Slow") ? 0.5f : 1f);
                }
            }
        }
    }

    public void handleBloonHit(int i, Body bloonBody, Body projectileBody) {
        towers.get(i).hitBloon(projectileBody);
        for(int j = 0; j < activeBloon.size() ; j++) {
            if(activeBloon.get(j).body == bloonBody) {
                if(activeBloon.get(j).previouslyHitByProjectile != projectileBody) {
                    activeBloon.get(j).previouslyHitByProjectile = projectileBody;
                    handleBloonDamagedAndDestroyed(j);
                }
                break;
            }
        }
    }

    private boolean handleBloonDamagedAndDestroyed(int j) {//This method fires during collision detection, as the world is stepping new bodies cannot be created so it must be done on the next frame
        if(activeBloon.get(j).hitAndDestroyed()) {
            MONEY += activeBloon.get(j).poppingReward;
            switch (activeBloon.get(j).type) {
                case "Red":
                    destroyBloon(j);
                    return true;
                case "Blue":
                    activeBloon.get(j).create("Red");
                    break;
                case "Green":
                    activeBloon.get(j).create("Blue");
                    break;
                case "Yellow":
                    activeBloon.get(j).create("Green");
                    break;
                case "Black":
                case "White":
                    activeBloon.get(j).create("Yellow");
                    bloonsToDuplicate.put(j, 1);
                    break;
                case "Lead":
                    activeBloon.get(j).create("Black");
                    bloonsToDuplicate.put(j, 1);
                    break;
                case "Rainbow":
                    activeBloon.get(j).create("Black");
                    bloonsToDuplicate.put(j, 3);
                    break;
                case "Ceramic":
                    activeBloon.get(j).create("Rainbow");
                    bloonsToDuplicate.put(j, 1);
                    break;
                case "M.O.A.B":
                    activeBloon.get(j).create("Ceramic");
                    bloonsToDuplicate.put(j, 3);
            }
            return true;
        }
        return false;
    }

    private void destroyBloon(int i) {
        handleBloonRemovedFromList(i, activeBloon.get(i).body);
        inactiveBloon.add(activeBloon.get(i));
        activeBloon.get(i).destroyed();
        activeBloon.remove(i);
    }

    public void handleBlooniFramesOver(Body bloonBody) {
        for(int i = 0 ; i < activeBloon.size() ; i++) {
            if(activeBloon.get(i).body == bloonBody) {
                activeBloon.get(i).previouslyHitByProjectile = null;
                break;
            }
        }
    }

    public void releaseFirstBloon() {
        createBloon(roundBloonList[0]);
        roundBloonCount[0]--;
        if(roundBloonCount[0] < 0) current++;
        bloonBuffer = 0;
    }

    public void handleBloonHitBySpikes(int i, Body bloonBody) {
        spikeList.get(i).hitsLeft--;
        for(int j = 0; j < activeBloon.size() ; j++) {
            if(activeBloon.get(j).body == bloonBody) {
                handleBloonDamagedAndDestroyed(j);
                break;
            }
        }
        if(spikeList.get(i).hitsLeft == 0) spikeList.get(i).setInActive();
    }

    public void handleBloonHitByGlue(int i, Body bloonBody) {
        glueList.get(i).slowCount--;
        for(int j = 0; j < activeBloon.size() ; j++) {
            if(activeBloon.get(j).body == bloonBody && activeBloon.get(j).speedModifier == activeBloon.get(j).baseSpeedModifier) {
                activeBloon.get(j).speedModifier *= 0.25f;
                break;
            }
        }
        if(glueList.get(i).slowCount == 0) glueList.get(i).setInActive();
    }

    public void handleBloonHitByPineapple(Body bloonBody) {
        for(int j = 0; j < activeBloon.size() ; j++) {
            if(activeBloon.get(j).body == bloonBody) {
                handleBloonDamagedAndDestroyed(j);
                break;
            }
        }
    }

    private void handleBloonRemovedFromList(Integer j, Body body) { //By using paint ;; bloons that have an index higher than the removed bloon must be lowered by 1
        for(int i = 0 ; i < bloonsInTowerRange.size() ; i++) {//2103
            for (int k = 0; k < bloonsInTowerRange.get(i).size(); k++) {//cycle through all
                if (bloonsInTowerRange.get(i).get(k) > j) {//value is more than 2
                    bloonsInTowerRange.get(i).set(k, bloonsInTowerRange.get(i).get(k) - 1);//lower by 1
                    resetBloonPriority(i);
                }
            }
            if(bloonsInTowerRange.get(i).contains(j)) {
                handleBloonInTowerRange(i, body, false);
            }
        }
    }

    private void moveBloons() {
        for(int i = 0; i < activeBloon.size() ; i++) {
            balloonPathA.derivativeAt(activeBloon.get(i).targetPosition, activeBloon.get(i).distance);
            activeBloon.get(i).distance += (Gdx.graphics.getDeltaTime() / activeBloon.get(i).targetPosition.len()) * activeBloon.get(i).speedModifier / 120;
            if(activeBloon.get(i).distance >= 1f) {
                LIVES -= activeBloon.get(i).lifeCost;
                if(LIVES <= 0) lostGame = true;
                destroyBloon(i);
            } else {
                balloonPathA.valueAt(activeBloon.get(i).targetPosition, activeBloon.get(i).distance);
                activeBloon.get(i).moveAlongPath();
            }
        }
    }

    public void sendSuperMonkeyStorm() {
        if(superMonkeyStormActive) {
            for(int i = 0; i < activeBloon.size() ; i++) {
                if("M.O.A.B".equals(activeBloon.get(i).type)) {
                    inactiveBloon.add(activeBloon.get(i));
                    activeBloon.get(i).destroyed();
                    handleBloonRemovedFromList(i, activeBloon.get(i).body);
                }
            }
            activeBloon.clear();
            activeBloon.addAll(moabList);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if(!lostGame) {
            update(delta);
            stepWorld();
        }
        b2dr.render(world, camera.combined);
        gameScreenUI.act();
        gameScreenUI.draw();
        gameScreenUI.drawDescriptions(towerBeingBought, buyingTower);
        gameScreenUI.drawDescriptions(hazardBeingBought + 8, buyingHazard);
        gameScreenUI.drawTowerUpgradeDescriptions();
        if(lostGame) gameScreenUI.showRetryButton();
        for(int i = 0 ; i < activeBloon.size() ; i++) {
            labelPosition.set(activeBloon.get(i).body.getWorldCenter(), 1);
            labelPosition.x -= 0.15f;
            labelPosition.set(camera.project(labelPosition));
            gameScreenUI.drawBloonLabel(activeBloon.get(i).type, labelPosition.x, labelPosition.y);
        }
        gameScreenUI.drawLivesAndMoney();
        camera.update();
    }

    public void reset() {
        LIVES = initialLives;
        MONEY = 650;
        activeBloon.clear();
        inactiveBloon.clear();
        amountOfTowers = 0;
        towers.clear();
        buyingTower = false;
        for(int i = 0 ; i < spawnTowerBody.size() ; i++) world.destroyBody(spawnTowerBody.get(i));
        spawnTowerBody.clear();
        bloonsInTowerRange.clear();
        priorityBloonForTower.clear();
        bloonBuffer = 0;
        monkeyBeacon.clear();
        buyingHazard = false;
        spikeList.clear();
        glueList.clear();
        pineappleList.clear();
        amountOfSpikes = 0;
        amountOfGlue = 0;
        amountOfPineapple = 0;
        superMonkeyStormActive = false;
        moabList.clear();
        roundBloonList = new String[]{"Red"};
        roundBloonCount = new int[]{13};
        ROUND = 1;
        bloonsToDuplicate.clear();
        roundBegan = false;
        lostGame = false;
        gameScreenUI.setBeginRoundVisibility(true);
        for(int i = 0 ; i < 4 ; i++) gameScreenUI.setUpgradeButtonVisibility(i, false, 0);
        world.dispose();
        world = new World(Vector2.Zero, true);
        bodyCreator.setWorld(world);
        spawnTowerBody.add(bodyCreator.createCircle(13, true, BodyDef.BodyType.DynamicBody, "SpawnCircle", GameScreen.TRACK_BIT, TRACK_BIT));
        spawnTowerBody.get(0).setTransform(4.5f, 1, 0);
        for(int i = 0 ; i < 10 ; i++) {
            bloonsToDuplicate.put(i, 0);
        }
        bodyCreator.createChainShape(paths.getPathAEdgeA(), false, BodyDef.BodyType.StaticBody, "Track0", TRACK_BIT, TRACK_BIT);
        bodyCreator.createChainShape(paths.getPathAEdgeB(), false, BodyDef.BodyType.StaticBody, "Track1", TRACK_BIT, TRACK_BIT);
        bodyCreator.createChainShape(paths.getPathA(), false, BodyDef.BodyType.StaticBody, "Track2", TRACK_BIT, TRACK_BIT);
        current = 0;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        world.dispose();

    }
}
