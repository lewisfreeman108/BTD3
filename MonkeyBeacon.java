package com.dropkick.btd3.Towers;

import com.badlogic.gdx.math.Vector2;

public class MonkeyBeacon {

    public Vector2 position;
    public boolean[] upgraded = new boolean[4];
    public float range = 77.5f;

    public MonkeyBeacon(Vector2 position) {
        this.position = position;
    }

    public void increaseRange() {
        range = 100;
        upgraded[0] = true;
    }

}
