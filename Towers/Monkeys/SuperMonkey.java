package com.dropkick.btd3.Towers.Monkeys;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.dropkick.btd3.Helpers.BodyCreator;
import com.dropkick.btd3.Screens.GameScreen;
import com.dropkick.btd3.Towers.Tower;

public class SuperMonkey extends Tower {

    private int projectileType = 0;

    private final Body[] projectile = new Body[40];

    private final float[] bufferTime = new float[20];
    private final float[] fireTime = new float[20];
    private final boolean[] fired = new boolean[20];

    private int currentProjectile;

    private final Vector2[] laserOffset = new Vector2[]{new Vector2(), new Vector2()};
    private final Vector2[] laserTarget = new Vector2[]{new Vector2(), new Vector2()};

    public SuperMonkey(BodyCreator bodyCreator, int i, Vector2 position) {
        super(bodyCreator,i, 125, position);
        monkeyTower = true;
        for(int j = 0 ; j < 20 ; j++) {
            projectile[j] = bodyCreator.createBox(7.5f, 2.5f, true, BodyDef.BodyType.DynamicBody, "Projectile" + i, (short) (GameScreen.REGULAR_PROJECTILE_BIT | GameScreen.BLACKWHITE_PROJECTILE_BIT), (short) (GameScreen.REGULAR_BLOON_BIT | GameScreen.BLACKWHITE_BLOON_BIT));
            projectile[j].setTransform(position, 0);
        }
    }

    @Override
    public void attackBloon(Vector2 targetPosition, float dt) {
        fireTime[currentProjectile] += dt;
        float facing = (float) Math.atan2((position.y - targetPosition.y), (position.x - targetPosition.x));
        float facing2 = (float) Math.atan2((position.x - targetPosition.x), (position.y - targetPosition.y));//rotate 90 degrees (anti clockwise I think)
        if(fireTime[currentProjectile] >= 0.059f * reloadSpeed) {
            switch (projectileType) {
                case 0:
                case 2:
                    sendProjectile(facing, projectile[currentProjectile], position, targetPosition);
                    break;
                case 1:
                   //x * cos(theta) + y * sin(theta), -x * sin(theta) + y * cos(theta)
                    laserOffset[0].set((float) (-0.1f * Math.cos(facing2)), (float) (0.1f * Math.sin(facing2)));
                    laserOffset[1].set((float) (0.1f * Math.cos(facing2)), (float) (-0.1f * Math.sin(facing2)));

                    projectile[currentProjectile].setTransform(laserOffset[0].x + position.x, laserOffset[0].y + position.y, facing);
                    projectile[currentProjectile + 20].setTransform(laserOffset[1].x + position.x, laserOffset[1].y + position.y, facing);

                    laserTarget[0].set(laserOffset[0].x + targetPosition.x, laserOffset[0].y + targetPosition.y);
                    laserTarget[1].set(laserOffset[1].x + targetPosition.x, laserOffset[1].y + targetPosition.y);

                    sendProjectile(facing, projectile[currentProjectile], projectile[currentProjectile].getWorldCenter(), laserTarget[0]);
                    sendProjectile(facing, projectile[currentProjectile + 20], projectile[currentProjectile + 20].getWorldCenter(), laserTarget[1]);
                    break;
            }
           reset();
        }
    }

    private void sendProjectile(float facing, Body body, Vector2 position, Vector2 targetingPosition) {
        body.setTransform(position, facing);
        body.setLinearVelocity(targetingPosition.sub(body.getWorldCenter()).scl((1 / targetingPosition.len()) * 15));//using 1 / len() to get a constant velocity
    }
    private void reset() {
        fired[currentProjectile] = true;
        fireTime[currentProjectile] = 0;
        bufferTime[currentProjectile] = 0;
        currentProjectile ++;
        if(currentProjectile == 20) currentProjectile = 0;
    }

    @Override
    public void hitBloon(Body projectileBody) {
        if(projectileType == 2) return;
        for(int i = 0 ; i < 20 + (20 * projectileType) ; i++) {
            if(projectile[i] == projectileBody) {
                projectile[i].getFixtureList().get(0).setUserData("Nothing");
                return;
            }
        }
    }

    @Override
    public void handleProjectiles(float dt) {
        for(int i = 0 ; i < 20 ; i++) {
            if(fired[i]) {
                bufferTime[i] += dt;
                if (bufferTime[i] >= 1) {
                    projectile[i].setLinearVelocity(0, 0);
                    projectile[i].setTransform(position, 0);
                    projectile[i].getFixtureList().get(0).setUserData("Projectile" + towerID);
                    fired[i] = false;
                }
            }
        }
    }

    @Override
    public String info() {
        return null;
    }

    @Override
    public void firstUpgrade() {
        increaseRange(0.4f);
        upgraded[0] = true;
    }

    @Override
    public void secondUpgrade() {
        increaseRange(0.3f);
        upgraded[1] = true;
    }

    @Override
    public void thirdUpgrade() {
        projectileType = 1;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(9f / GameScreen.PPM, 1f / GameScreen.PPM);
        for(int i = 0 ; i < 20 ; i++) {
            projectile[i].destroyFixture(projectile[i].getFixtureList().get(0));
            projectile[i].createFixture(bodyCreator.createFixture(true, shape, (short) (GameScreen.REGULAR_PROJECTILE_BIT | GameScreen.BLACKWHITE_PROJECTILE_BIT), (short)(GameScreen.REGULAR_BLOON_BIT | GameScreen.FROZEN_BLOON_BIT | GameScreen.LEAD_BLOON_BIT | GameScreen.BLACKWHITE_BLOON_BIT))).setUserData("Projectile" + towerID);
            projectile[i + 20] = bodyCreator.createBox(9f, 1f, true, BodyDef.BodyType.DynamicBody, "Projectile" + towerID, (short) (GameScreen.REGULAR_PROJECTILE_BIT | GameScreen.BLACKWHITE_PROJECTILE_BIT), (short)(GameScreen.REGULAR_BLOON_BIT | GameScreen.FROZEN_BLOON_BIT | GameScreen.LEAD_BLOON_BIT | GameScreen.BLACKWHITE_BLOON_BIT));
        }
        shape.dispose();
        upgraded[2] = true;
    }

    @Override
    public void fourthUpgrade() {
        projectileType = 2;
        CircleShape shape = new CircleShape();
        shape.setRadius(10f / GameScreen.PPM);
        for(int i = 0 ; i < 20 ; i++) {
            bodyCreator.deleteBody(projectile[i + 20]);
            projectile[i].destroyFixture(projectile[i].getFixtureList().get(0));
            projectile[i].createFixture(bodyCreator.createFixture(true, shape, (short) (GameScreen.REGULAR_PROJECTILE_BIT | GameScreen.BLACKWHITE_PROJECTILE_BIT), (short)(GameScreen.REGULAR_BLOON_BIT | GameScreen.FROZEN_BLOON_BIT | GameScreen.LEAD_BLOON_BIT | GameScreen.BLACKWHITE_BLOON_BIT))).setUserData("Projectile" + towerID);
            projectile[i].setTransform(projectile[i].getWorldCenter().add(laserOffset[0]), projectile[i].getAngle());
        }
        shape.dispose();
        upgraded[3] = true;
        hasPiercing = true;
    }

}
