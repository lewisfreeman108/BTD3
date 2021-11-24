package com.dropkick.btd3.Towers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.dropkick.btd3.Helpers.BodyCreator;
import com.dropkick.btd3.Screens.GameScreen;

public class IceTower extends Tower {

    private final Body inRangeDetection;

    private float bufferTime;
    private boolean fired;
    private float freezeTime = 3;

    private boolean popChanceActive;
    private boolean slow;

    private boolean disabledFreezing;


    public IceTower(BodyCreator bodyCreator, int i, Vector2 position) {
        super(bodyCreator, i, 50, position);
        inRangeDetection = bodyCreator.createCircle(60, true, BodyDef.BodyType.DynamicBody, "IceTower" + i, GameScreen.REACH_BIT, (short) (GameScreen.REGULAR_BLOON_BIT | GameScreen.LEAD_BLOON_BIT | GameScreen.REGULAR_PROJECTILE_BIT | GameScreen.BLACKWHITE_BLOON_BIT));
        inRangeDetection.setTransform(-3, 3, 0);
    }

    @Override
    public void attackBloon(Vector2 attackingPosition, float dt) {
        if(!fired) bufferTime += dt;
        if(bufferTime >= 1.5f && !fired) {
            inRangeDetection.setTransform(position, 0);
            fired = true;
        }
    }

    @Override
    public void hitBloon(Body projectileBody) {
    }

    @Override
    public void handleProjectiles(float dt) {
        if(fired) bufferTime += dt;
        if(bufferTime >= 1.55f && !disabledFreezing) {
            inRangeDetection.getFixtureList().get(0).setUserData("Nothing");
            disabledFreezing = true;
        }
        if(bufferTime >= 1.5f + freezeTime) {
            fired = false;
            bufferTime = 0;
            inRangeDetection.getFixtureList().get(0).setUserData("IceTower" + towerID);
            inRangeDetection.setTransform(-3, 3, 0);
            disabledFreezing = false;
        }
    }

    @Override
    public String info() {
        String string = popChanceActive ? "Pop" : "";
        String string2 = slow ? "Slow" : "";
        return string + string2;
    }

    @Override
    public void firstUpgrade() {
        freezeTime *= 2;
        upgraded[0] = true;
    }

    @Override
    public void secondUpgrade() {
        slow = true;
        upgraded[1] = true;
    }

    @Override
    public void thirdUpgrade() {
        String userData = inRangeDetection.getFixtureList().get(0).getUserData().toString();
        increaseRange(0.5f);
        inRangeDetection.destroyFixture(inRangeDetection.getFixtureList().get(0));
        CircleShape shape = new CircleShape();
        shape.setRadius(90f / GameScreen.PPM);
        inRangeDetection.createFixture(bodyCreator.createFixture(true, shape, GameScreen.REACH_BIT, (short) (GameScreen.REGULAR_BLOON_BIT | GameScreen.LEAD_BLOON_BIT | GameScreen.REGULAR_PROJECTILE_BIT | GameScreen.BLACKWHITE_BLOON_BIT))).setUserData(userData);
        shape.dispose();
        upgraded[2] = true;
    }

    @Override
    public void fourthUpgrade() {
        popChanceActive = true;
        upgraded[3] = true;
    }

}
