package com.dropkick.btd3.Towers.Monkeys;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.dropkick.btd3.Helpers.BodyCreator;
import com.dropkick.btd3.Screens.GameScreen;
import com.dropkick.btd3.Towers.Tower;

public class SpikePult extends Tower {

    private int piercing = 6;
    private int pierced;

    private final Body[] spike = new Body[3];
    private final Vector2[] spikePosition = new Vector2[3];
    private final Vector2[] spikeRotation = new Vector2[2];
    private final Vector2[] spikeTarget = new Vector2[2];

    private float bufferTime;
    private boolean fired;

    private float rollDistance = 1;
    private boolean multipleProjectiles;

    private final Filter notShot = new Filter();
    private final Filter shot = new Filter();

    public SpikePult(BodyCreator bodyCreator, int i, Vector2 position) {
        super(bodyCreator, i, 100, position);
        monkeyTower = true;
        for(int j = 0 ; j < 3 ; j++) {
            spike[j] = bodyCreator.createCircle(10, true, BodyDef.BodyType.DynamicBody, "Projectile" + i, GameScreen.NOTHING_BIT, GameScreen.NOTHING_BIT);
        }
        notShot.categoryBits = GameScreen.NOTHING_BIT;
        notShot.maskBits = GameScreen.NOTHING_BIT;
        shot.categoryBits = (short) (GameScreen.REGULAR_PROJECTILE_BIT | GameScreen.BLACKWHITE_PROJECTILE_BIT);
        shot.maskBits = (short) (GameScreen.REGULAR_BLOON_BIT | GameScreen.BLACKWHITE_BLOON_BIT);
        spikePosition[0] = position.cpy();
        spikePosition[1] = new Vector2(position.x + 0.2f, position.y);
        spikePosition[2] = new Vector2(position.x - 0.2f, position.y);
        spikeRotation[0] = new Vector2();
        spikeRotation[1] = new Vector2();
        spikeTarget[0] = new Vector2();
        spikeTarget[1] = new Vector2();
        resetSpikeRotation();
        hasPiercing = true;
    }

    private void resetSpikeRotation() {
        for (int i = 0 ; i < 3 ; i++) {
            spike[i].setTransform(spikePosition[i], 0);
        }
    }

    @Override
    public void attackBloon(Vector2 targetingPosition, float dt) {//ok this is a bit of math

        if(!fired) {
            //First we would get the arctangent of y/x to find theta to get the body to look at the target, (float) Math.atan2(position.y - targetingPosition.y, position.x - targetingPosition.x);
            //instead we get the clockwise 90 degree rotation of this angle by switching the x & y values
            float facing = (float) Math.atan2((position.x - targetingPosition.x), (position.y - targetingPosition.y));

            spike[0].setTransform(spikePosition[0], facing);

            //then we use the axes rotation formula to find positioning offset: x * cos(theta) + y * sin(theta), -x * sin(theta) + y * cos(theta), theta is facing or facing 2, x is (+/- 0.2), y is 0
            spikeRotation[0].set((float) (0.2f * Math.cos(facing)), (float) (-0.2f * Math.sin(facing)));
            spikeRotation[1].set((float) (-0.2f * Math.cos(facing)), (float) (0.2f * Math.sin(facing)));

            spike[1].setTransform(spikeRotation[0].x + spikePosition[0].x, spikeRotation[0].y + spikePosition[0].y, 0);
            spike[2].setTransform(spikeRotation[1].x + spikePosition[0].x, spikeRotation[1].y + spikePosition[0].y, 0);

            if(bufferTime >= 1.65f * reloadSpeed) {
                fired = true;
                if(multipleProjectiles) {
                    for(int i = 0 ; i < 2 ; i++) {
                        spikeTarget[i].set(spikeRotation[i].x + targetingPosition.x, spikeRotation[i].y + targetingPosition.y);
                        spike[i + 1].setLinearVelocity(spikeTarget[i].sub(spike[i + 1].getWorldCenter()).scl((1 / spikeTarget[i].len()) * 5));
                    }
                }
                spike[0].setLinearVelocity(targetingPosition.sub(position).scl((1 / targetingPosition.len()) * 5));
                for(int i = 0 ; i < (multipleProjectiles ? 3 : 1) ; i++) spike[i].getFixtureList().get(0).setFilterData(shot);
            } else {
                bufferTime += dt;
            }
        }
    }

    @Override
    public void hitBloon(Body projectileBody) {
        pierced++;
        if(piercing - pierced == 0) for(int i = 0 ; i < 3 ; i++) spike[i].getFixtureList().get(0).setFilterData(notShot);
    }

    @Override
    public void handleProjectiles(float dt) {
        if(fired) bufferTime += dt;
        if (bufferTime >= 1.65f + (0.9f * rollDistance)) {
            resetPult();
        }
    }

    @Override
    public String info() {
        return null;
    }

    @Override
    public void firstUpgrade() {
        increaseRange(0.18f);
        upgraded[0] = true;
    }

    @Override
    public void secondUpgrade() {
        piercing = 20;
        rollDistance = 1.85f;
        upgraded[1] = true;
    }

    @Override
    public void thirdUpgrade() {
        reloadSpeed *= 0.82f;
        upgraded[2] = true;
    }

    @Override
    public void fourthUpgrade() {
        multipleProjectiles = true;
        upgraded[3] = true;
    }

    private void resetPult() {
        for(int i = 0 ; i < 3 ; i++) {
            spike[i].setLinearVelocity(0, 0);
        }
        for(int i = 0 ; i < (multipleProjectiles ? 3 : 1); i++) spike[i].getFixtureList().get(0).setFilterData(notShot);
        resetSpikeRotation();
        fired = false;
        pierced = 0;
        reset = false;
        bufferTime = 0;
    }
}
