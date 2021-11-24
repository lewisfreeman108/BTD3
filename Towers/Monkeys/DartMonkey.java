package com.dropkick.btd3.Towers.Monkeys;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.dropkick.btd3.Helpers.BodyCreator;
import com.dropkick.btd3.Screens.GameScreen;
import com.dropkick.btd3.Towers.Tower;

public class DartMonkey extends Tower {

    private final Body dart;
    boolean fired;
    float bufferTime;
    int piercing = 1;
    int pierced;

    public DartMonkey(BodyCreator bodyCreator, int i, Vector2 touchPosition) {
        super(bodyCreator, i, 100, touchPosition);
        monkeyTower = true;
        dart = bodyCreator.createBox(7.5f, 2.5f, true, BodyDef.BodyType.DynamicBody, "Projectile" + i, (short) (GameScreen.REGULAR_PROJECTILE_BIT | GameScreen.BLACKWHITE_PROJECTILE_BIT), (short) (GameScreen.REGULAR_BLOON_BIT | GameScreen.BLACKWHITE_BLOON_BIT));
        dart.setTransform(touchPosition, 0);
    }

    @Override
    public void attackBloon(Vector2 targetingPosition, float dt) {
        if(bufferTime >= 0.5f * reloadSpeed && !fired) {
            float facing = (float) Math.atan2((targetingPosition.y - dart.getWorldCenter().y), (targetingPosition.x - dart.getWorldCenter().x));
            dart.setTransform(dart.getPosition(), facing);
            dart.setLinearVelocity(targetingPosition.sub(dart.getWorldCenter()).scl((1 / targetingPosition.len()) * 15));//using 1 / len() to get a constant velocity
            fired = true;
        } else if(!fired) {
            bufferTime += dt;
        }
    }

    @Override
    public void hitBloon(Body projectileBody) {
        pierced++;
        if(piercing - pierced == 0) reset = true;
    }

    @Override
    public void handleProjectiles(float dt) {
        if (reset) resetDart(); //Can't move or destroy bodies while the world is stepping (during collision detection)
        if (fired) bufferTime += dt;
        if (bufferTime >= 0.5f + (0.075f * rangePercentage)) {
            resetDart();
        }
    }

    @Override
    public String info() {
        return null;
    }

    @Override
    public void firstUpgrade() {
        increaseRange(0.25f);
        rangePercentage *= 1.25f;
        upgraded[0] = true;
    }

    @Override
    public void secondUpgrade() {
        increaseRange(0.15f);
        rangePercentage *= 1.15f;
        upgraded[1] = true;
    }

    @Override
    public void thirdUpgrade() {
        hasPiercing = true;
        piercing = 2;
        upgraded[2] = true;
    }

    @Override
    public void fourthUpgrade() {
        piercing = 3;
        upgraded[3] = true;
    }

    private void resetDart() {
        dart.setLinearVelocity(0, 0);
        dart.setTransform(position, 0);
        fired = false;
        pierced = 0;
        reset = false;
        bufferTime = 0;
    }
}
