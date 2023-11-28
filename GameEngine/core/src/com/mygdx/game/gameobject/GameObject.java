package com.mygdx.game.gameobject;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public interface GameObject extends Actions {
    enum ENTITIES_CATEGORIE {
        BULLET_ENEMY(1),
        BULLET_PLAYER(2),
        LIVING_ENTITIES(4);

        private final short value;

        ENTITIES_CATEGORIE(int value) {
            this.value = (short) value;
        }

        public int getValue() {
            return value;
        }
    }

    public Vector2 getPosition();
    public void setPosition(Vector2 position);

    public Sprite getImg();
}
