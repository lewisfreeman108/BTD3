package com.dropkick.btd3.Hazards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.dropkick.btd3.Helpers.BodyCreator;
import com.dropkick.btd3.Screens.GameScreen;

public class Pineapple {

    public final Body explosion;
    private boolean exploded;
    private final Filter regularFilter = new Filter();
    private final Filter destroyedFilter = new Filter();
    private float bufferTime;
    public boolean active = true;

    public Pineapple(BodyCreator bodyCreator, Vector2 position, int i) {
        regularFilter.categoryBits = (short) (GameScreen.REGULAR_PROJECTILE_BIT | GameScreen.FROZEN_PROJECTILE_BIT | GameScreen.LEAD_PROJECTILE_BIT);
        regularFilter.maskBits = (short) (GameScreen.REGULAR_BLOON_BIT | GameScreen.FROZEN_BLOON_BIT | GameScreen.LEAD_BLOON_BIT);
        destroyedFilter.categoryBits = GameScreen.NOTHING_BIT;
        destroyedFilter.maskBits = GameScreen.NOTHING_BIT;
        explosion = bodyCreator.createCircle(70, true, BodyDef.BodyType.DynamicBody, "Pineapple" + i, GameScreen.NOTHING_BIT, GameScreen.NOTHING_BIT);
        explosion.setTransform(position, 0);
    }

    public void setActive(Vector2 position) {
        explosion.setTransform(position, 0);
        explosion.setType(BodyDef.BodyType.DynamicBody);
        active = true;
    }

    public void countDown() {
        if(active) {
            bufferTime += Gdx.graphics.getDeltaTime();
            if(!exploded && bufferTime >= 3) {
                explosion.getFixtureList().get(0).setFilterData(regularFilter);
                exploded = true;
            }
            if(bufferTime >= 3.3f ) {
                explosion.getFixtureList().get(0).setFilterData(destroyedFilter);
                exploded = false;
                bufferTime = 0;
                active = false;
                explosion.setType(BodyDef.BodyType.StaticBody);
                explosion.setTransform(6, 1, 0);
            }
        }
    }
}
