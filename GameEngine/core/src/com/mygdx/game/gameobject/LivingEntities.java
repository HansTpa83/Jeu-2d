package com.mygdx.game.gameobject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Boot;

import java.util.ArrayList;

abstract class LivingEntities implements GameObject {
    protected Body body;
    protected Sprite img;
    protected String name;
    protected World level;
    protected Vector2 position;
    protected boolean isStatic;
    protected int hp, speed, damages;
    protected short categoryBits;
    protected short maskBits;
    ArrayList<Bullet> bullets = new ArrayList<>();
    ArrayList<Bullet> bulletsToDelete = new ArrayList<>();

    public LivingEntities(String name, Vector2 position, int hp, int damages, int speed, boolean isStatic, World level, int category, int maskBits){
        this.hp = hp;
        this.name = name;
        this.speed = speed;
        this.level = level;
        this.damages = damages;
        this.isStatic = isStatic;
        this.position = position;
        this.categoryBits = (short)category;
        this.maskBits = (short)maskBits;

        initLivingEntities();
    }

    private void initLivingEntities(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = isStatic ? BodyDef.BodyType.StaticBody : BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x, position.y );
        bodyDef.fixedRotation = true;
        Body body = level.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(6.0f / 14f, 6.0f / 6.5f);

        FixtureDef fixturedef = new FixtureDef();
        fixturedef.shape = shape;

        System.out.println("CAT : "+categoryBits);
        System.out.println("MASK : "+maskBits);

        fixturedef.filter.categoryBits = categoryBits;
        fixturedef.filter.maskBits = maskBits;

        body.createFixture(fixturedef);
        shape.dispose();

        this.body = body;
    }

    abstract void update(float delta);
    abstract void playerMovement();
    abstract void setAction();


    // SETTER
    public void takeDamage(int damage){
        hp -= damage;
        Boot.manager.get("Audio/Sound/BulletHit.wav", Sound.class).play();
    }

    // GETTER
    public int getHp() { return hp;}
    public Body getBody(){ return body; }
    public String getName() {return name;}
    public int getSpeed() { return speed; }
    public int getDamages() { return damages;}
    public short getCategoryBits() { return categoryBits; }

    // OVERRIDE
    @Override
    public int moveLeft(int velocity) { return velocity-1;}
    @Override
    public int moveRight(int velocity) { return velocity+1;}
    @Override
    public int jump(int velocity) {
        Boot.manager.get("Audio/Sound/Jump.mp3", Sound.class).play();
        return velocity+1;
    }
    @Override
    public Bullet shoot(String direction, float x, float y) {
        Boot.manager.get("Audio/Sound/Shoot.wav", Sound.class).play();
        return new Bullet(x, y, level, direction, true,damages);
    }
    @Override
    public Vector2 getPosition(){
        return new Vector2(body.getPosition());
    }
    @Override
    public void setPosition(Vector2 newPosition){
        position = newPosition ;
    }
    @Override
    public Sprite getImg(){
        return img;
    }

}
