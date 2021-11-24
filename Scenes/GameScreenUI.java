package com.dropkick.btd3.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dropkick.btd3.Helpers.UICreator;
import com.dropkick.btd3.Screens.GameScreen;

import java.util.ArrayList;

public class GameScreenUI extends Stage {

    private final SpriteBatch batch;

    private final TextButton[] upgradeButton = new TextButton[4];
    private final TextButton beginRoundButton;

    private final String[] hazard = new String[]{"S", "G", "P"};

    private final BitmapFont arialSmall;

    private final boolean[] upgradeVisible = new boolean[4];
    private final TextButton sMonkeyStormButton;
    private int towerBeingUpgraded;

    private final ArrayList<String[]> towerDescriptions = new ArrayList<>();
    private final ArrayList<String[]> upgradeDescriptions = new ArrayList<>();

    private final int[] towerTypeCost = new int[]{255, 365, 525, 610, 740, 435, 1020, 4080};
    private final ArrayList<int[]> towerTypeUpgradeCosts = new ArrayList<>();

    private final float[] upgradeY = new float[]{Gdx.graphics.getHeight() / 2f - 1.5f * (Gdx.graphics.getHeight() / (20f * Gdx.graphics.getHeight() / Gdx.graphics.getWidth())), Gdx.graphics.getHeight() / 2f - 1.5f * (Gdx.graphics.getHeight() / (20f * Gdx.graphics.getHeight() / Gdx.graphics.getWidth())), Gdx.graphics.getHeight() / 2f + 0.5f * (Gdx.graphics.getHeight() / (20f * Gdx.graphics.getHeight() / Gdx.graphics.getWidth())), Gdx.graphics.getHeight() / 2f + 0.5f * (Gdx.graphics.getHeight() / (20f * Gdx.graphics.getHeight() / Gdx.graphics.getWidth()))};

    private final TextButton retryButton;

    public GameScreenUI(final GameScreen screen, SpriteBatch batch, float modifier) {
        Gdx.input.setInputProcessor(this);
        this.batch = batch;
        FreeTypeFontGenerator arial = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/Arial/arial.ttf"));
        UICreator uiCreator = new UICreator();
        arialSmall = uiCreator.createFont(arial, Color.WHITE, Gdx.graphics.getWidth() / 50 * (Gdx.graphics.getWidth() / Gdx.graphics.getHeight()));
        BitmapFont arialBig = uiCreator.createFont(arial, Color.WHITE, Gdx.graphics.getWidth() / 25 * (Gdx.graphics.getWidth() / Gdx.graphics.getHeight()));
        TextButton[] buyButton = new TextButton[8];
        String[] tower = new String[]{"DM", "TS", "BM", "SP", "C", "IB", "MB", "SM"};
        for(int i = 0 ; i < 4 ; i++) {
            final Integer ii = i;
            for(int j = 0 ; j < 2 ; j++) {
                final Integer jj = j;
                buyButton[i + (4 * j)] = uiCreator.createTextButton(arialSmall, tower[i + (4 * j)], Gdx.graphics.getWidth() / 30f, Gdx.graphics.getWidth() / 30f, (Gdx.graphics.getWidth() * 2.8f / 4) + (Gdx.graphics.getWidth() / 30f) * (1.5f * i), (Gdx.graphics.getHeight() * 3.5f / 4f - 1.5f * (Gdx.graphics.getHeight() / (30f * Gdx.graphics.getHeight() / Gdx.graphics.getWidth())) * j),
                        new ClickListener() {
                            @Override
                            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                                if(GameScreen.MONEY - towerTypeCost[ii + (4 * jj)] > 0) {
                                    screen.towerBeingBoughtCost = towerTypeCost[ii + (4 * jj)];
                                    screen.buyingTower = true;
                                }
                                screen.buyingHazard = false;
                                screen.towerBeingBought = ii + (4 * jj);
                                return true;
                            }
                        });
                addActor(buyButton[i + (4 * j)]);
                buyButton[i + (4 * j)].setDebug(true);
            }
        }
        sMonkeyStormButton = uiCreator.createTextButton(arialSmall, "??", Gdx.graphics.getWidth() / 30f, Gdx.graphics.getWidth() / 30f, (Gdx.graphics.getWidth() * 2.8f / 4) + (Gdx.graphics.getWidth() / 30f) * (1.5f * 3), (Gdx.graphics.getHeight() * 3.5f / 4f - 1.5f * (Gdx.graphics.getHeight() / (30f * Gdx.graphics.getHeight() / Gdx.graphics.getWidth())) * 2),
                new ClickListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        screen.sendSuperMonkeyStorm();
                        return true;
                    }
                });
        addActor(sMonkeyStormButton);
        sMonkeyStormButton.setDebug(true);

        for(int i = 0; i < towerTypeCost.length ; i++) towerTypeCost[i] *= modifier;

        towerDescriptions.add(new String[]{"Dart Monkey", "Cost: " + towerTypeCost[0], "Shoots a single dart.", "Can upgrade to", "piercing darts, razor", "darts and long range", "darts."});
        towerDescriptions.add(new String[]{"Tack Shooter", "Cost: " + towerTypeCost[1], "Shoots voley of tacks", "in 8 directions. Can", "upgrade to a blade", "shooter."});
        towerDescriptions.add(new String[]{"Boomerang Monkey", "Cost: " + towerTypeCost[2], "Throws a boomerang", "that follows a curved", "path back to the", "tower. Can pop", "multiple bloons at", "once. Upgrades to", "glaive thrower."});
        towerDescriptions.add(new String[]{"Spike-o-Pult", "Cost: " + towerTypeCost[3], "Hurls a huge spiked", "ball that pops", "everything it touches.", "Can upgrade range,", "speed, and multi-shot."});
        towerDescriptions.add(new String[]{"Cannon", "Cost: " + towerTypeCost[4], "Launches a bomb that", "explodes on impact.", "Can upgrade to bigger", "bombs and longer", "range, and Missile", "Launcher."});
        towerDescriptions.add(new String[]{"Ice Ball", "Cost: " + towerTypeCost[5], "Freezes nearby", "bloons. Frozen bloons", "are immune to", "anything sharp. Can", "upgrade to increased", "freeze time, and larger", "freeze radius"});
        towerDescriptions.add(new String[]{"Monkey Beacon", "Cost: " + towerTypeCost[6], "Increases the attack", "range of all monkeys", "within the beacon", "area. Can upgrade to", "increase attack speed", "in the area, and grants", "access to the most", "powerful ability ever"});
        towerDescriptions.add(new String[]{"Super Monkey", "Cost: " + towerTypeCost[7], "Super monkey shoots", "incredibly fast. Can", "upgrade to epic range,", "laser vision, and more!"});

        towerTypeUpgradeCosts.add(new int[]{90, 90, 145, 120});
        towerTypeUpgradeCosts.add(new int[]{205, 185, 100, 100});
        towerTypeUpgradeCosts.add(new int[]{275, 185, 155, 120});
        towerTypeUpgradeCosts.add(new int[]{255, 840, 255, 585});
        towerTypeUpgradeCosts.add(new int[]{440, 225, 205, 215});
        towerTypeUpgradeCosts.add(new int[]{255, 255, 205, 295});
        towerTypeUpgradeCosts.add(new int[]{425, 1257, 2125, 850});
        towerTypeUpgradeCosts.add(new int[]{850, 1190, 2975, 3400});

        upgradeDescriptions.add(new String[]{"Makes the dart monkey shoot further than normal. $90", "Gives the dart monkey even more throwing distance. $90", "Darts can pop 2 bloons instead of 1. $145", "Darts can pop 3 bloons instead of 2. $120"});
        upgradeDescriptions.add(new String[]{"Makes tack shoot faster. $205", "Shoots blades instead of tacks! $185", "Covers a larger area than normal. $100", "Covers a huge range! $100"});
        upgradeDescriptions.add(new String[]{"Boomerangs will hit up to 7 bloons at once. $275", "Glaives slice through up to 12 bloons at once! $185", "Sonic boomerangs smash through frozen bloons! $155", "Thermite boomerangs melt through lead bloons! $120"});
        upgradeDescriptions.add(new String[]{"Increases maximum range of Spike-o-pult. $255", "Spike-o-pult balls roll much further. $840", "Can shoot faster than normal. $255", "Shoots 3 spikey balls at once! $585"});
        upgradeDescriptions.add(new String[]{"Bombs hit a larger area. $440", "Fragments fly out and pop even more bloons! $225", "Can shoot bombs further than normal. $205", "Shoots fast missiles instead of bombs that go faster, further and pop more! $215"});
        upgradeDescriptions.add(new String[]{"Bloons stay frozen for longer. $255", "Slows down bloons even then after thawing out. $255", "Increases freeze radius. $205", "Freezes so violently that sometimes pops bloons once before freezing! $295"});
        upgradeDescriptions.add(new String[]{"Gives monkey beacon a wider area of effect. $425", "Inspires all by giving a shoot range increase to nearby monkeys. $1257", "Can call Super Monkey Storms! $2125", "Unleashes a wave of Super Monkeys! $850"});
        upgradeDescriptions.add(new String[]{"Super range can cover nearly half the screen. $850", "Epic range allows almost whole map coverage! $1190", "Lasers can pop 2 bloons at once and pop frozen bloons. $2975", "Plasma vaporises everything it touches! $3400"});


        for(int i = 0 ; i < 4 ; i++) {
            final int ii = i;
            upgradeButton[i] = uiCreator.createTextButton(arialBig, String.valueOf(i + 1), Gdx.graphics.getWidth() / 20f, Gdx.graphics.getWidth() / 20f, 0, upgradeY[i],
                    new ClickListener() {
                        @Override
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            if(GameScreen.MONEY - towerTypeUpgradeCosts.get(towerBeingUpgraded)[ii] > 0) {
                                screen.upgradeSelectedTower(ii);
                                GameScreen.MONEY -= towerTypeUpgradeCosts.get(towerBeingUpgraded)[ii];
                            }
                            return true;
                        }
                    });
            addActor(upgradeButton[i]);
            upgradeButton[i].setDebug(true);
            setUpgradeButtonVisibility(i, false, towerBeingUpgraded);
        }

        TextButton[] buyHazardButton = new TextButton[4];
        for(int i = 0 ; i < 3 ; i++) {
            final Integer ii = i;
            buyHazardButton[i] = uiCreator.createTextButton(arialSmall, hazard[i], Gdx.graphics.getWidth() / 30f, Gdx.graphics.getWidth() / 30f, (Gdx.graphics.getWidth() * 2.8f / 4) + (Gdx.graphics.getWidth() / 30f) * (1.5f * i), (Gdx.graphics.getHeight() * 3.5f / 4f - 1.5f * (Gdx.graphics.getHeight() / (30f * Gdx.graphics.getHeight() / Gdx.graphics.getWidth())) * 2),
                    new ClickListener() {
                        @Override
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            screen.hazardBeingBought = ii;
                            screen.buyingTower = false;
                            if(GameScreen.MONEY - 30 >= 0) screen.buyingHazard = true;
                            return true;
                        }
                    });
            addActor(buyHazardButton[i]);
            buyHazardButton[i].setDebug(true);
        }

        towerDescriptions.add(new String[]{"Spikes", "Cost: 30", "Place these road", "spikes on the track to", "pop bloons. Can pop", "10 bloons before", "wearing out. Road", "spikes only last until", "the end of the round."});
        towerDescriptions.add(new String[]{"Monkey Glue", "Cost: 30", "Monkey glue slows", "down bloons. Each", "blob can slow down 20", "bloons before wearing", "out. Monkey glue only", "lasts until the end of", "the round."});
        towerDescriptions.add(new String[]{"Pineapple", "Cost: 30", "Like all healthy food,", "pineapples explode", "violently shortly after", "being placed, so don't put", "any down until", "you want to blow up", "some bloons."});


        beginRoundButton = uiCreator.createTextButton(arialSmall, "Start Round", Gdx.graphics.getWidth() / (30f / 5.5f), Gdx.graphics.getWidth() / 30f, (Gdx.graphics.getWidth() * 2.8f / 4), (Gdx.graphics.getHeight() / 15f),
                new ClickListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        screen.roundBegan = true;
                        screen.releaseFirstBloon();
                        setBeginRoundVisibility(false);
                        return true;
                    }
                });
        beginRoundButton.setDebug(true);
        addActor(beginRoundButton);

        retryButton = uiCreator.createTextButton(arialBig, "Retry", Gdx.graphics.getWidth() / 3f, Gdx.graphics.getHeight() / 15f, Gdx.graphics.getWidth() / 2f - Gdx.graphics.getWidth() / 6f, Gdx.graphics.getHeight() / 2f - Gdx.graphics.getHeight() / 30f, new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                screen.reset();
                retryButton.setVisible(false);
                retryButton.setDisabled(true);
                return true;
            }
        });
        retryButton.setVisible(false);
        retryButton.setDisabled(true);
        retryButton.setDebug(true);
        addActor(retryButton);
    }

    public void setBeginRoundVisibility(boolean visible) {
        beginRoundButton.setVisible(visible);
        beginRoundButton.setDisabled(!visible);
    }

    public void setUpgradeButtonVisibility(int i, boolean visible, int type) {
        upgradeButton[i].setVisible(visible);
        upgradeButton[i].setDisabled(!visible);
        upgradeVisible[i] = visible;
        towerBeingUpgraded = type;
    }

    public void drawDescriptions(int thingBeingBought, boolean buyingSomething) {
        batch.begin();
        if(buyingSomething) {
            if (Gdx.input.getX() > Gdx.graphics.getWidth() / 1.45 && Gdx.input.getY() < Gdx.graphics.getHeight() / 3.25f) {
                for(int i = 0 ; i < (towerDescriptions.get(thingBeingBought).length) ; i++) arialSmall.draw(batch, towerDescriptions.get(thingBeingBought)[i], Gdx.graphics.getWidth() * 2.8f / 4f, (Gdx.graphics.getHeight() * 3.5f / 4f - 1.5f * (Gdx.graphics.getHeight() / (30f * Gdx.graphics.getHeight() / Gdx.graphics.getWidth())) * 2.4f) - Gdx.graphics.getHeight() / 25f * i);
            } else {
                arialSmall.draw(batch, "CANCEL PURCHASE", (Gdx.graphics.getWidth() * 2.8f / 4) , Gdx.graphics.getHeight() / 3.95f);
            }
        }
        batch.end();
    }

    public void drawTowerUpgradeDescriptions() {
        batch.begin();
        for(int i = 0 ; i < 4 ; i++) if(upgradeVisible[i]) arialSmall.draw(batch, upgradeDescriptions.get(towerBeingUpgraded)[i], Gdx.graphics.getWidth() / 15f, upgradeY[i] + Gdx.graphics.getWidth() / 30f);
        batch.end();
    }

    public void drawBloonLabel(String text, float x, float y) {
        batch.begin();
        arialSmall.draw(batch, text, x, y);
        batch.end();
    }

    public void drawLivesAndMoney() {
        batch.begin();
        arialSmall.draw(batch, "Lives: " + GameScreen.LIVES, Gdx.graphics.getWidth() / 20f, Gdx.graphics.getHeight() - Gdx.graphics.getWidth() / 40f);
        arialSmall.draw(batch, "Money: " + GameScreen.MONEY, Gdx.graphics.getWidth() / 6.5f, Gdx.graphics.getHeight() - Gdx.graphics.getWidth() / 40f);
        arialSmall.draw(batch, "Round:" + GameScreen.ROUND, Gdx.graphics.getWidth() * 6.3f / 7f, Gdx.graphics.getHeight() - Gdx.graphics.getWidth() / 40f);

        batch.end();
    }

    public void showRetryButton() {
        retryButton.setVisible(true);
        retryButton.setDisabled(false);
    }

    public void secretUnlocked() {
        sMonkeyStormButton.setText("SS");
    }

}
