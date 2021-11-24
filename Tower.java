package com.dropkick.btd3.Towers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Shape;
import com.dropkick.btd3.Helpers.BodyCreator;
import com.dropkick.btd3.Screens.GameScreen;

public abstract class Tower {

    int cost;
    public boolean reset;
    public Body detectionRange;
    public Vector2 position;
    protected int towerID;
    protected boolean[] upgraded = new boolean[]{false, false, false, false};
    protected BodyCreator bodyCreator;
    protected float rangePercentage = 1;
    protected float reloadSpeed = 1;

    public int type;
    public boolean monkeyTower;

    protected short currentCategoryBit;
    protected short currentMaskBit;

    private final Filter filter = new Filter();

    public boolean[] upgradedByBeaconTower = new boolean[2];

    public boolean hasPiercing;

    public Tower(BodyCreator bodyCreator, int i, float reachRadius, Vector2 position) {
        towerID = i;
        detectionRange = bodyCreator.createCircle(reachRadius, true, BodyDef.BodyType.DynamicBody, "DetectionCircleTower" + i, GameScreen.REACH_BIT, (short) (GameScreen.REGULAR_BLOON_BIT | GameScreen.LEAD_BLOON_BIT | GameScreen.BLACKWHITE_BLOON_BIT | GameScreen.FROZEN_BLOON_BIT));
        detectionRange.setTransform(position.x, position.y, 0);
        this.bodyCreator = bodyCreator;
        this.position = position.cpy();
    }

    public abstract void attackBloon(Vector2 attackingPosition, float dt);

    public abstract void hitBloon(Body projectileBody);

    public abstract void handleProjectiles(float dt);

    public abstract String info();

    public void increaseRange(float percentageIncrease) {
        float newRange = detectionRange.getFixtureList().get(0).getShape().getRadius() * (1 + percentageIncrease);
        detectionRange.destroyFixture(detectionRange.getFixtureList().get(0));
        Shape shape = new CircleShape();
        shape.setRadius(newRange);
        detectionRange.createFixture(bodyCreator.createFixture(true, shape, GameScreen.REACH_BIT, (short) (GameScreen.REGULAR_BLOON_BIT | GameScreen.LEAD_BLOON_BIT | GameScreen.FROZEN_BLOON_BIT | GameScreen.BLACKWHITE_BLOON_BIT))).setUserData("DetectionCircleTower" + towerID);
        shape.dispose();
    }

    public void buyTower() {
        GameScreen.MONEY -= cost;
    }

    public boolean upgradeBought(int i) {
        return upgraded[i];
    }

    public abstract void firstUpgrade();
    public abstract void secondUpgrade();
    public abstract void thirdUpgrade();
    public abstract void fourthUpgrade();

    protected void changeBits(Body body, short CATEGORY_BIT, short MASK_BIT) {
        currentCategoryBit = CATEGORY_BIT;
        currentMaskBit = MASK_BIT;
        filter.categoryBits = CATEGORY_BIT;
        filter.maskBits = MASK_BIT;
        body.getFixtureList().get(0).setFilterData(filter);
    }

    public void decreaseReloadSpeed(float percentage) {
        reloadSpeed *= (1 - (percentage / 100));
    }

}
