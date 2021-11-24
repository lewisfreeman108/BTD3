package com.dropkick.btd3.Hazards;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.dropkick.btd3.Helpers.BodyCreator;
import com.dropkick.btd3.Screens.GameScreen;

public class Spikes {

    public int hitsLeft = 10;
    public final Body body;
    private final Filter destroyedFilter = new Filter();
    private final Filter regularFilter = new Filter();

    public Spikes(BodyCreator bodyCreator, Vector2 position, int i){
        body = bodyCreator.createCircle(15f, true, BodyDef.BodyType.DynamicBody, "Spikes" + i, (short)(GameScreen.REGULAR_PROJECTILE_BIT | GameScreen.BLACKWHITE_PROJECTILE_BIT), (short) (GameScreen.BLACKWHITE_BLOON_BIT | GameScreen.REGULAR_BLOON_BIT));
        body.setTransform(position.cpy(), 0);
        regularFilter.categoryBits = GameScreen.REGULAR_PROJECTILE_BIT;
        regularFilter.maskBits = GameScreen.REGULAR_BLOON_BIT;

    }
    public void setActive(Vector2 position) {
        body.setTransform(position, 0);
        body.setType(BodyDef.BodyType.DynamicBody);
        body.getFixtureList().get(0).setFilterData(regularFilter);
        hitsLeft = 10;
    }

    public void setInActive() {
        body.getFixtureList().get(0).setUserData(destroyedFilter);
        body.setType(BodyDef.BodyType.StaticBody);
    }
}
