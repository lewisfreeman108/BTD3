package com.dropkick.btd3.Towers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.dropkick.btd3.Helpers.BodyCreator;
import com.dropkick.btd3.Screens.GameScreen;

public class Cannon extends Tower {

    private final Body projectile;
    private final Body explosion;
    private final Body[] fragment = new Body[8];
    private final Vector2[] fragVeloc = new Vector2[8];

    private boolean exploded;
    private boolean fired;
    private float bufferTime;
    private float fragTime;
    private boolean fragTimeGo;
    private boolean fragOn;
    private float projectileSpeed = 7f;
    private float fireTime = 1.59f;

    public Cannon(BodyCreator bodyCreator, int i, Vector2 position) {
        super(bodyCreator, i, 120, position);

        projectile = bodyCreator.createCircle(7f, true, BodyDef.BodyType.DynamicBody, "Projectile" + i, (short)(GameScreen.REGULAR_PROJECTILE_BIT | GameScreen.LEAD_PROJECTILE_BIT | GameScreen.FROZEN_PROJECTILE_BIT), (short)(GameScreen.REGULAR_BLOON_BIT | GameScreen.LEAD_BLOON_BIT | GameScreen.FROZEN_BLOON_BIT));
        explosion = bodyCreator.createCircle(35f, true, BodyDef.BodyType.DynamicBody, "Nothing", (short)(GameScreen.REGULAR_PROJECTILE_BIT | GameScreen.LEAD_PROJECTILE_BIT | GameScreen.FROZEN_PROJECTILE_BIT), (short)(GameScreen.REGULAR_BLOON_BIT | GameScreen.LEAD_BLOON_BIT | GameScreen.FROZEN_BLOON_BIT));

        for(int j = 0 ; j < 8 ; j++) {
            fragment[j] = bodyCreator.createCircle(2f, true, BodyDef.BodyType.DynamicBody, "Projectile" + i, (short)(GameScreen.REGULAR_PROJECTILE_BIT | GameScreen.LEAD_PROJECTILE_BIT | GameScreen.FROZEN_PROJECTILE_BIT), (short)(GameScreen.REGULAR_BLOON_BIT | GameScreen.LEAD_BLOON_BIT | GameScreen.FROZEN_BLOON_BIT));
            fragment[j].setTransform(position, 0);
        }

        projectile.setTransform(position, 0);
        explosion.setTransform(position, 0);
        
        float val = (float) Math.sqrt(0.5f);

        fragVeloc[0] = new Vector2(0, 1).scl(4f);
        fragVeloc[1] = new Vector2(val, val).scl(4f);
        fragVeloc[2] = new Vector2(1, 0).scl(4f);
        fragVeloc[3] = new Vector2(val, -val).scl(4f);
        fragVeloc[4] = new Vector2(0, -1).scl(4f);
        fragVeloc[5] = new Vector2(-val, -val).scl(4f);
        fragVeloc[6] = new Vector2(-1, 0).scl(4f);
        fragVeloc[7] = new Vector2(-val, val).scl(4f);
    }

    @Override
    public void attackBloon(Vector2 targetingPosition, float dt) {
        if(!fired) {
            float facing = (float) Math.atan2(targetingPosition.y - position.y, targetingPosition.x - position.x);
            projectile.setTransform(position, facing);
            if(bufferTime >= fireTime) {
                projectile.setLinearVelocity(targetingPosition.sub(position).scl((1 / targetingPosition.len()) * projectileSpeed));//using 1 / len() to get a constant velocity
                fired = true;
            }
            bufferTime += dt;
        }
    }

    @Override
    public void hitBloon(Body projectileBody) {
        for(int i = 0 ; i < 8 ; i++) {
            if(fragment[i] == projectileBody) {
                fragment[i].getFixtureList().get(0).setUserData("HitBloon");
                return;
            }
        }
        if(projectile == projectileBody) {
            projectile.getFixtureList().get(0).setUserData("HitBloon");
            exploded = true;
        }
    }

    @Override
    public void handleProjectiles(float dt) {
        if(fired) bufferTime += dt;
        if(fragTimeGo) fragTime += dt;
        if(exploded) {
            explosion.getFixtureList().get(0).setUserData("Projectile" + towerID);
            explosion.setTransform(projectile.getWorldCenter(), 0);
            if(fragOn) {
                for(int i = 0 ; i < 8 ; i++) {
                    fragment[i].setTransform(projectile.getWorldCenter(), 0);
                    fragment[i].setLinearVelocity(fragVeloc[i]);
                    fragTimeGo = true;
                }
            }
            exploded = false;
        }
        if((!fragTimeGo && bufferTime >= fireTime + .65f) || fragTime >= 0.5f) {
            resetCannon();
        }
    }

    @Override
    public String info() {
        return null;
    }

    @Override
    public void firstUpgrade() {
        String userData = projectile.getFixtureList().get(0).getUserData().toString();
        explosion.destroyFixture(explosion.getFixtureList().get(0));
        CircleShape shape = new CircleShape();
        shape.setRadius(70f / GameScreen.PPM);
        explosion.createFixture(bodyCreator.createFixture(true, shape, (short)(GameScreen.REGULAR_PROJECTILE_BIT | GameScreen.LEAD_PROJECTILE_BIT | GameScreen.FROZEN_PROJECTILE_BIT), (short)(GameScreen.REGULAR_BLOON_BIT | GameScreen.LEAD_BLOON_BIT | GameScreen.FROZEN_BLOON_BIT))).setUserData(userData);
        shape.dispose();
        upgraded[0] = true;
    }

    @Override
    public void secondUpgrade() {
        fragOn = true;
        upgraded[1] = true;
    }

    @Override
    public void thirdUpgrade() {
        increaseRange(0.1f);
        upgraded[2] = true;
    }

    @Override
    public void fourthUpgrade() {
        String userData = projectile.getFixtureList().get(0).getUserData().toString();
        projectileSpeed = 15f;
        fireTime = 1.33f;
        projectile.destroyFixture(projectile.getFixtureList().get(0));
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(15f / GameScreen.PPM, 4f / GameScreen.PPM);
        projectile.createFixture(bodyCreator.createFixture(true, shape, (short)(GameScreen.REGULAR_PROJECTILE_BIT | GameScreen.LEAD_PROJECTILE_BIT | GameScreen.FROZEN_PROJECTILE_BIT), (short)(GameScreen.REGULAR_BLOON_BIT | GameScreen.LEAD_BLOON_BIT | GameScreen.FROZEN_BLOON_BIT))).setUserData(userData);
        shape.dispose();
        upgraded[3] = true;
    }

    private void resetCannon() {
        projectile.setTransform(position, 0);
        projectile.setLinearVelocity(0, 0);
        projectile.getFixtureList().get(0).setUserData("Projectile" + towerID);
        explosion.setTransform(position, 0);
        explosion.setLinearVelocity(0, 0);
        explosion.getFixtureList().get(0).setUserData("Nothing");

        for(int i = 0 ; i < 8 ; i++) {
            fragment[i].setLinearVelocity(0, 0);
            fragment[i].setTransform(position, 0);
            fragment[i].getFixtureList().get(0).setUserData("Projectile" + towerID);
        }
        fired = false;
        reset = false;
        bufferTime = 0;
        fragTime = 0;
        fragTimeGo = false;
    }
}
