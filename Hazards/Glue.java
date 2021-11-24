package com.dropkick.btd3.Hazards;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.dropkick.btd3.Helpers.BodyCreator;
import com.dropkick.btd3.Screens.GameScreen;

public class Glue {

    public Body body;
    public int slowCount = 20;
    private final Filter destroyedFilter = new Filter();
    private final Filter regularFilter = new Filter();

    public Glue(BodyCreator bodyCreator, Vector2 position, int i) {
        body = bodyCreator.createCircle(15f, true, BodyDef.BodyType.DynamicBody, "Glue" + i, GameScreen.REGULAR_PROJECTILE_BIT , (short) (GameScreen.REGULAR_BLOON_BIT | GameScreen.LEAD_BLOON_BIT | GameScreen.BLACKWHITE_BLOON_BIT));
        body.setTransform(position.cpy(), 0);
        regularFilter.categoryBits = GameScreen.REGULAR_PROJECTILE_BIT;
        regularFilter.maskBits = GameScreen.REGULAR_BLOON_BIT;
    }

    public void setActive(Vector2 position) {
        body.setType(BodyDef.BodyType.DynamicBody);
        body.setTransform(position, 0);
        body.getFixtureList().get(0).setFilterData(regularFilter);
        slowCount = 20;
    }

    public void setInActive() {
        body.getFixtureList().get(0).setUserData(destroyedFilter);
        body.setType(BodyDef.BodyType.StaticBody);
    }
}
