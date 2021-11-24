package com.dropkick.btd3.Towers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.dropkick.btd3.Helpers.BodyCreator;
import com.dropkick.btd3.Screens.GameScreen;

public class TackShooter extends Tower {

    private final Body[] projectiles = new Body[8];
    private final int[] pierced = new int[8];
    private int piercing = 1;

    private boolean fired;
    private float bufferTime;

    private final Vector2[] shootingDirection = new Vector2[8];
    private final float[] facingDirection = new float[8];
    private float shootTime = 1.66f;//1.66 regular, 1.66 * 0.67 for increase

    private final int towerID;
    private float rangeMultiplier = 1f;//1, 1.14571428571f, 1.29285714286f

    public TackShooter(BodyCreator bodyCreator, int i, Vector2 position) {
        super(bodyCreator, i,70, position);//70 reg, 80.2, 90.5
        towerID = i;

        float val = (float) Math.sqrt(0.5f);

        shootingDirection[0] = new Vector2(0, 1).scl(7.5f);
        shootingDirection[1] = new Vector2(val, val).scl(7.5f);
        shootingDirection[2] = new Vector2(1, 0).scl(7.5f);
        shootingDirection[3] = new Vector2(val, -val).scl(7.5f);
        shootingDirection[4] = new Vector2(0, -1).scl(7.5f);
        shootingDirection[5] = new Vector2(-val, -val).scl(7.5f);
        shootingDirection[6] = new Vector2(-1, 0).scl(7.5f);
        shootingDirection[7] = new Vector2(-val, val).scl(7.5f);

        facingDirection[0] = (float) Math.atan2(1, 0);
        facingDirection[1] = (float) Math.atan2(val, val);
        facingDirection[2] = (float) Math.atan2(0, 1);
        facingDirection[3] = (float) Math.atan2(-val, val);
        facingDirection[4] = (float) Math.atan2(-1, 0);
        facingDirection[5] = (float) Math.atan2(-val, -val);
        facingDirection[6] = (float) Math.atan2(0, -1);
        facingDirection[7] = (float) Math.atan2(val, -val);

        for(int j = 0 ; j < 8 ; j++) {
            projectiles[j] = bodyCreator.createBox(6f, 1f, true, BodyDef.BodyType.DynamicBody, "Projectile" + i, (short) (GameScreen.REGULAR_PROJECTILE_BIT | GameScreen.BLACKWHITE_PROJECTILE_BIT), (short) (GameScreen.REGULAR_BLOON_BIT | GameScreen.BLACKWHITE_BLOON_BIT));
            projectiles[j].setTransform(position, facingDirection[j]);
        }
    }

    @Override
    public void attackBloon(Vector2 attackingPosition, float dt) {
        if(!fired) bufferTime += dt;
        if(bufferTime >= shootTime && !fired) {
            for(int i = 0; i < 8 ; i++) {
                projectiles[i].setLinearVelocity(shootingDirection[i]);
            }
            fired = true;
        }
    }

    @Override
    public void hitBloon(Body projectileBody) {
        for(int i = 0 ; i < 8 ; i++) {
            if(projectiles[i] == projectileBody) {
                pierced[i]++;
                if(piercing - pierced[i] == 0){
                    projectiles[i].getFixtureList().get(0).setUserData("Hit");
                }
                break;
            }
        }
    }

    @Override
    public void handleProjectiles(float dt) {
        if(fired) bufferTime += dt;
        if (bufferTime >= shootTime + (0.28f * rangeMultiplier)) {
            resetTackShooter();
        }
    }

    @Override
    public String info() {
        return null;
    }

    @Override
    public void firstUpgrade() {
        shootTime *= 0.67f;
        upgraded[0] = true;
    }

    @Override
    public void secondUpgrade() {
        hasPiercing = true;
        piercing = 2;
        createBlades(upgraded[2] ? 12f : 7f);
        upgraded[1] = true;
    }

    @Override
    public void thirdUpgrade() {
        if(upgraded[1]) createBlades(12f);
        increaseRange(0.15f);
        rangeMultiplier *= 1.15f;
        upgraded[2] = true;
    }

    @Override
    public void fourthUpgrade() {
        increaseRange(0.1f);
        rangeMultiplier *= 1.1f;
        upgraded[3] = true;
    }

    private void createBlades(float radius) {
        CircleShape shape = new CircleShape();
        shape.setRadius(radius / GameScreen.PPM);
        for(int i = 0 ; i < 8 ; i++) {
            projectiles[i].destroyFixture(projectiles[i].getFixtureList().get(0));
            projectiles[i].createFixture(bodyCreator.createFixture(true, shape, GameScreen.REGULAR_PROJECTILE_BIT, GameScreen.REGULAR_BLOON_BIT)).setUserData("Projectile" + towerID);
        }
        shape.dispose();
    }

    private void resetTackShooter() {
        for(int i = 0 ; i < 8 ; i++) {
            projectiles[i].setLinearVelocity(0, 0);
            projectiles[i].setTransform(position, facingDirection[i]);
            projectiles[i].getFixtureList().get(0).setUserData("Projectile" + towerID);
            pierced[i] = 0;
        }
        fired = false;
        reset = false;
        bufferTime = 0;
    }
}
