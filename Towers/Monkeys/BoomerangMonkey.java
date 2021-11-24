package com.dropkick.btd3.Towers.Monkeys;

import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.dropkick.btd3.Helpers.BodyCreator;
import com.dropkick.btd3.Screens.GameScreen;
import com.dropkick.btd3.Towers.Tower;

public class BoomerangMonkey extends Tower {

    private final Body projectile;

    private final CatmullRomSpline<Vector2> boomerangPath;
    private float distance;

    private boolean fired;
    private float bufferTime;

    private int pierced;
    private int piercing = 3;

    private final Vector2 targetPosition = new Vector2();

    private final Vector2[] controlPoint;
    private final Vector2[] adjustedPoint;

    public BoomerangMonkey(BodyCreator bodyCreator, int i, Vector2 position) {
        super(bodyCreator, i, 100, position);
        monkeyTower = true;

        currentCategoryBit = (short) (GameScreen.REGULAR_PROJECTILE_BIT | GameScreen.BLACKWHITE_PROJECTILE_BIT);
        currentMaskBit = (short) (GameScreen.REGULAR_BLOON_BIT | GameScreen.BLACKWHITE_BLOON_BIT);
        projectile = bodyCreator.createBox(2f, 11f, true, BodyDef.BodyType.DynamicBody, "Projectile" + i, currentCategoryBit, currentMaskBit);

        controlPoint = new Vector2[]{new Vector2(0, 0), new Vector2(50f / GameScreen.PPM, -30f / GameScreen.PPM), new Vector2(100f / GameScreen.PPM, -24f / GameScreen.PPM), new Vector2(120f / GameScreen.PPM, 0), new Vector2(100f / GameScreen.PPM, 24f / GameScreen.PPM), new Vector2(50f / GameScreen.PPM, 30f / GameScreen.PPM)};
        adjustedPoint = new Vector2[]{new Vector2(), new Vector2(), new Vector2(), new Vector2(), new Vector2(), new Vector2()};
        boomerangPath = new CatmullRomSpline<>();
        projectile.setTransform(position, 0);
        hasPiercing = true;
    }

    @Override
    public void attackBloon(Vector2 attackingPosition, float dt) {
        bufferTime += dt;
        if(bufferTime >= 0.75f * reloadSpeed && !fired) {
            fired = true;
            float facing = (float) Math.atan2((position.y - attackingPosition.y), -(position.x - attackingPosition.x));
            float facing2 = (float) Math.atan2((position.y - attackingPosition.y), (position.x - attackingPosition.x));

            for(int i = 0 ; i < 6 ; i++) {
                //x * cos(theta) + y * sin(theta), -x * sin(theta) + y * cos(theta)
                adjustedPoint[i].set((float) ((controlPoint[i].x * Math.cos(facing)) + (controlPoint[i].y * Math.sin(facing))), (float) ((-controlPoint[i].x * Math.sin(facing)) + (controlPoint[i].y * Math.cos(facing))));
            }
            boomerangPath.set(adjustedPoint, true);
            projectile.setTransform(position, facing2);
            projectile.getFixtureList().get(0).setUserData("Projectile" + towerID);
            projectile.setAngularVelocity(33);
        }
    }

    @Override
    public void hitBloon(Body projectileBody) {
        pierced++;
        if(piercing - pierced == 0) projectile.getFixtureList().get(0).setUserData("Hit");
    }

    @Override
    public void handleProjectiles(float dt) {
        if(fired) {
            boomerangPath.derivativeAt(targetPosition, distance);
            distance += dt / (2.06f * 1.33f) / targetPosition.len();//2.06 is trial+error, temp sol
            if(distance >= 1) {
                resetBoomerang();
            } else {
                boomerangPath.valueAt(targetPosition, distance);
                projectile.setLinearVelocity(targetPosition.sub(projectile.getWorldCenter()).add(position).scl(100));
            }
        }
    }

    @Override
    public String info() {
        return null;
    }

    @Override
    public void firstUpgrade() {
        piercing = 7;
        upgraded[0] = true;
    }

    @Override
    public void secondUpgrade() {
        piercing = 12;
        Shape shape = new CircleShape();
        shape.setRadius(13f / GameScreen.PPM);
        projectile.destroyFixture(projectile.getFixtureList().get(0));
        projectile.createFixture(bodyCreator.createFixture(true, shape, currentCategoryBit, currentMaskBit)).setUserData("Projectile" + towerID);
        shape.dispose();
        upgraded[1] = true;
    }

    @Override
    public void thirdUpgrade() {
        changeBits(projectile, (short) (GameScreen.FROZEN_PROJECTILE_BIT | GameScreen.REGULAR_PROJECTILE_BIT | GameScreen.BLACKWHITE_PROJECTILE_BIT),
                (short) (GameScreen.REGULAR_BLOON_BIT | GameScreen.FROZEN_BLOON_BIT | GameScreen.BLACKWHITE_BLOON_BIT));
        upgraded[2] = true;
    }

    @Override
    public void fourthUpgrade() {
        changeBits(projectile, (short) (GameScreen.LEAD_PROJECTILE_BIT | GameScreen.FROZEN_PROJECTILE_BIT | GameScreen.REGULAR_PROJECTILE_BIT | GameScreen.BLACKWHITE_PROJECTILE_BIT),
                (short) (GameScreen.LEAD_BLOON_BIT | GameScreen.REGULAR_BLOON_BIT | GameScreen.FROZEN_BLOON_BIT | GameScreen.BLACKWHITE_BLOON_BIT));
        upgraded[3] = true;
    }

    private void resetBoomerang() {
        projectile.setTransform(position, 0);
        projectile.setLinearVelocity(0, 0);
        projectile.setAngularVelocity(0);
        fired = false;
        distance = 0;
        bufferTime = 0;
    }
}
