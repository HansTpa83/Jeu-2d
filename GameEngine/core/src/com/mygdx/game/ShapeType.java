package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Shape;

public class ShapeType {
    private Shape shape;
    private boolean isSpike;

    public ShapeType(Shape shape, boolean isSpike) {
        this.shape = shape;
        this.isSpike = isSpike;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public boolean isSpike() {
        return isSpike;
    }

    public void setSpike(boolean spike) {
        isSpike = spike;
    }
}
