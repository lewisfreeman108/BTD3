package com.dropkick.btd3.Helpers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.dropkick.btd3.Screens.GameScreen;

public class ContactListener implements com.badlogic.gdx.physics.box2d.ContactListener {

    private Fixture fixtureA;
    private Fixture fixtureB;

    public boolean[] towerTouchingTrack = new boolean[3];
    public boolean towerTouchingTower;

    private final GameScreen gameScreen;

    public ContactListener(GameScreen screen) {
        gameScreen = screen;
    }

    private boolean collision(String userdata1, String userdata2) {
        return (userdata1.equals(fixtureA.getUserData()) || userdata1.equals(fixtureB.getUserData()) && (userdata2.equals(fixtureA.getUserData()) || userdata2.equals(fixtureB.getUserData())));
    }

    @Override
    public void beginContact(Contact contact) {
        fixtureA = contact.getFixtureA();
        fixtureB = contact.getFixtureB();

        for(int i = 0 ; i < 3 ; i++) {
            if (collision("Track" + i, "SpawnCircle")) towerTouchingTrack[i] = true;
        }

        if("SpawnCircle".equals(fixtureA.getUserData()) && "SpawnCircle".equals(fixtureB.getUserData())) towerTouchingTower = true;

        for(int i = 0 ; i < gameScreen.amountOfTowers ; i++) {
            if (collision("DetectionCircleTower" + i, "Bloon")) gameScreen.handleBloonInTowerRange(i, ("Bloon".equals(fixtureA.getUserData()) ? fixtureA : fixtureB).getBody(), true);
            if (collision("Projectile" + i, "Bloon")) gameScreen.handleBloonHit(i, ("Bloon".equals(fixtureA.getUserData()) ? fixtureA : fixtureB).getBody(), ("Bloon".equals(fixtureA.getUserData()) ? fixtureB : fixtureA).getBody());
            if (collision("IceTower" + i, "Bloon")) gameScreen.handleBloonInIceTowerRange(true, ("Bloon".equals(fixtureA.getUserData()) ? fixtureA : fixtureB).getBody(), i);
        }
        for (int i = 0 ; i < gameScreen.amountOfSpikes ; i++) {
            if (collision("Spikes" + i, "Bloon")) gameScreen.handleBloonHitBySpikes(i, ("Bloon".equals(fixtureA.getUserData()) ? fixtureA : fixtureB).getBody());
        }
        for(int i = 0 ; i < gameScreen.amountOfGlue ; i++) {
            if (collision("Glue" + i, "Bloon")) gameScreen.handleBloonHitByGlue(i, ("Bloon".equals(fixtureA.getUserData()) ? fixtureA : fixtureB).getBody());
        }
        for(int i = 0 ; i < gameScreen.amountOfPineapple ; i++) {
            if (collision("Pineapple" + i, "Bloon")) gameScreen.handleBloonHitByPineapple(("Bloon".equals(fixtureA.getUserData()) ? fixtureA : fixtureB).getBody());
        }
    }

    @Override
    public void endContact(Contact contact) {
        fixtureA = contact.getFixtureA();
        fixtureB = contact.getFixtureB();

        for(int i = 0 ; i < 3 ; i++) {
            if (collision("Track" + i, "SpawnCircle")) towerTouchingTrack[i] = false;
        }

        if("SpawnCircle".equals(fixtureA.getUserData()) && "SpawnCircle".equals(fixtureB.getUserData())) towerTouchingTower = false;

        for(int i = 0 ; i < gameScreen.amountOfTowers ; i++) {
            if (collision("DetectionCircleTower" + i, "Bloon")) gameScreen.handleBloonInTowerRange(i, ("Bloon".equals(fixtureA.getUserData()) ? fixtureA : fixtureB).getBody(), false);
            if (collision("IceTower" + i, "Bloon")) gameScreen.handleBloonInIceTowerRange(false, ("Bloon".equals(fixtureA.getUserData()) ? fixtureA : fixtureB).getBody(), i);
            if (collision("Projectile" + i, "Bloon")) gameScreen.handleBlooniFramesOver(("Bloon".equals(fixtureA.getUserData()) ? fixtureA : fixtureB).getBody());
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) { }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
