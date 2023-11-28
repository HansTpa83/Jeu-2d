package com.mygdx.game.gameobject;

import com.badlogic.gdx.math.Vector2;

public interface Actions {
        public int moveLeft(int velocity);
        public int moveRight(int velocity);
        public int jump(int velocity);
        public Bullet shoot(String direction, float x , float y);
}
