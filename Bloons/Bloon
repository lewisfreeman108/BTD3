package com.dropkick.btd3.Bloons;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.dropkick.btd3.Helpers.BodyCreator;
import com.dropkick.btd3.Screens.GameScreen;

public class Bloon {

    private final BodyCreator bodyCreator;
    int hp;
    public int lifeCost;

    public float baseSpeedModifier;
    public float speedModifier;
    public Body body;

    public Vector2 targetPosition = new Vector2();
    public float distance;

    private final Filter destroyedFilter = new Filter();
    private final Filter frozenFilter = new Filter();
    private final Filter regularFilter = new Filter();

    public String type;

    public int poppingReward;

    private final Vector2 distanceFromNextPoint = new Vector2();

    public Body previouslyHitByProjectile;

    public Bloon(BodyCreator bodyCreator, String type) {
        this.bodyCreator = bodyCreator;
        this.type = type;
        create(type);
        body = bodyCreator.createCircle(10, true, BodyDef.BodyType.KinematicBody, "Bloon", regularFilter.categoryBits, regularFilter.maskBits);
        frozenFilter.categoryBits = GameScreen.FROZEN_BLOON_BIT;
        frozenFilter.maskBits = GameScreen.FROZEN_PROJECTILE_BIT;
        destroyedFilter.categoryBits = GameScreen.NOTHING_BIT;
        destroyedFilter.maskBits = GameScreen.NOTHING_BIT;
        body.setSleepingAllowed(false);
    }

    public Bloon cpy() {
        Bloon bloon = new Bloon(bodyCreator, type);
        bloon.body.setTransform(body.getWorldCenter().cpy(), 0);
        bloon.distance = distance - 0.005f;
        return bloon;
    }

    public boolean hitAndDestroyed() {
        hp--;
        if(hp <= 0) body.getFixtureList().get(0).setFilterData(destroyedFilter);
        return hp <= 0;
    }

    public void create(String type) {
        this.type = type;
        short CATEGORY_BIT = GameScreen.REGULAR_BLOON_BIT;
        short MASK_BIT = (short)(GameScreen.REACH_BIT | GameScreen.REGULAR_PROJECTILE_BIT);
        switch (type) {
            case "Red":
                poppingReward = 1;
                baseSpeedModifier = 1;
                lifeCost = 1;
                break;
            case "Blue":
                poppingReward = 2;
                baseSpeedModifier = 1.4f;
                lifeCost = 2;
                break;
            case "Green":
                poppingReward = 3;
                baseSpeedModifier = 1.8f;
                lifeCost = 3;
                break;
            case "Yellow":
                poppingReward = 4;
                baseSpeedModifier = 3.2f;
                lifeCost = 4;
                break;
            case "Black"://Immune to explosions
                poppingReward = 9;
                CATEGORY_BIT = GameScreen.BLACKWHITE_BLOON_BIT;
                MASK_BIT = (short)(GameScreen.REACH_BIT | GameScreen.BLACKWHITE_PROJECTILE_BIT);
                baseSpeedModifier = 1.8f;
                lifeCost = 9;
                break;
            case "White"://Immune to explosions
                poppingReward = 9;
                CATEGORY_BIT = GameScreen.BLACKWHITE_BLOON_BIT;
                MASK_BIT = (short)(GameScreen.REACH_BIT | GameScreen.BLACKWHITE_PROJECTILE_BIT);
                baseSpeedModifier = 2f;
                lifeCost = 9;
                break;
            case "Lead"://Immune to sharp stuff
                poppingReward = 19;
                CATEGORY_BIT = GameScreen.LEAD_BLOON_BIT;
                MASK_BIT = (short)(GameScreen.REACH_BIT | GameScreen.LEAD_PROJECTILE_BIT);
                baseSpeedModifier = 1;
                lifeCost = 19;
                break;
            case "Rainbow":
                poppingReward = 37;
                baseSpeedModifier = 2.2f;
                lifeCost = 37;
                break;
            case "Ceramic":
                poppingReward = 73;
                baseSpeedModifier = 2.5f;
                lifeCost = 75;
                break;
            case "M.O.A.B":
                poppingReward = 292;
                baseSpeedModifier = 1;
                lifeCost = 301;
                break;
        }
        switch (type) {
            case "Ceramic":
                hp = 10;
                break;
            case "M.O.A.B":
                hp = 140;
                break;
            default:
                hp = 1;
                break;
        }
        speedModifier = baseSpeedModifier;
        regularFilter.categoryBits = CATEGORY_BIT;
        regularFilter.maskBits = MASK_BIT;
        if(body != null) body.getFixtureList().get(0).setFilterData(regularFilter);
    }

    public void destroyed() {
        body.setLinearVelocity(0, 0);
        distance = 0;
    }

    public void moveAlongPath() {
        distanceFromNextPoint.set(targetPosition.sub(body.getWorldCenter()));
        if("M.O.A.B".equals(type)) body.setTransform(body.getWorldCenter(), (float) Math.atan2(distanceFromNextPoint.y, distanceFromNextPoint.x));
        body.setLinearVelocity(distanceFromNextPoint.scl(100));
    }

    public void handleFrozen() {
        speedModifier = 0;
        body.getFixtureList().get(0).setFilterData(frozenFilter);
    }

    public void handleUnFrozen(float modifier) {
        if(speedModifier == 0) {
            speedModifier = baseSpeedModifier * modifier;
            body.getFixtureList().get(0).setFilterData(regularFilter);
        }
    }

}
