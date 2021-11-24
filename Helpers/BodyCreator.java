package com.dropkick.btd3.Helpers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.dropkick.btd3.Screens.GameScreen;

public class BodyCreator {

    private World world;

    public BodyCreator(World world) {
        this.world = world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public Body createBox(float dx, float dy, boolean isSensor, BodyDef.BodyType type, String userData, short CATEGORY_BIT, short MASK_BIT) {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(dx / GameScreen.PPM, dy / GameScreen.PPM);
        Body body = createBody(isSensor, shape, type, userData, CATEGORY_BIT, MASK_BIT);
        shape.dispose();
        return body;
    }

    public Body createChainShape(Vector2[] points, boolean isSensor, BodyDef.BodyType type, String userData, short CATEGORY_BIT, short MASK_BIT) {
        ChainShape shape = new ChainShape();
        shape.createChain(points);
        Body body = createBody(isSensor, shape, type, userData, CATEGORY_BIT, MASK_BIT);
        shape.dispose();
        return body;
    }

    public Body createCircle(float radius, boolean isSensor, BodyDef.BodyType type, String userData, short CATEGORY_BIT, short MASK_BIT) {
        CircleShape shape = new CircleShape();
        shape.setRadius(radius / GameScreen.PPM);
        Body body = createBody(isSensor, shape, type, userData, CATEGORY_BIT, MASK_BIT);
        shape.dispose();
        return body;
    }

    public Body createBody(boolean isSensor, Shape shape, BodyDef.BodyType type, String userData, short CATEGORY_BIT, short MASK_BIT) {
        BodyDef bdef = new BodyDef();
        bdef.type = type;
        Body body = world.createBody(bdef);
        body.createFixture(createFixture(isSensor, shape, CATEGORY_BIT, MASK_BIT)).setUserData(userData);
        return body;
    }

    public FixtureDef createFixture(Boolean isSensor, Shape shape, short CATEGORY_BIT, short MASK_BIT) {
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.isSensor = isSensor;
        fdef.filter.categoryBits = CATEGORY_BIT;
        fdef.filter.maskBits = MASK_BIT;
        return fdef;
    }

    public void deleteBody(Body body) {
        if(!world.isLocked()) {
            world.destroyBody(body);
        }
    }
}
