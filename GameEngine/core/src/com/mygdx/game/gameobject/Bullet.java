package com.mygdx.game.gameobject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.Boot;

import java.util.ArrayList;

public class Bullet<T> implements ContactListener{
    private Body body;
    private float x, y;
    private T fixtureA;
    private T fixtureB;
    private World level;
    private int damages;
    private Player player;
    private String direction;
    private Sprite bulletSprite;
    private Texture bulletTexture;
    private boolean isPlayerBullet;
    private boolean isRemovable = false;
    private boolean bulletsCollided = false;

    public Bullet (float x, float y, World level, String direction, boolean isPlayerBullet, int damages){
        level.setContactListener(this);
        this.y = y;
        this.x = x;
        this.level = level;
        this.damages = damages;
        this.direction = direction;
        this.isPlayerBullet = isPlayerBullet;

        setBulletSprite();
        setBulletPhysic();
        body.setUserData(this);
    }

    private void setBulletSprite(){
        bulletTexture = new Texture("playerAnimations/bullet.png");
        bulletSprite = new Sprite(bulletTexture);
    }

    private void setBulletPhysic(){
        BodyDef bulletBody = new BodyDef();
        bulletBody.type = BodyDef.BodyType.DynamicBody;
        bulletBody.position.set(x,y);
        bulletBody.fixedRotation = true;
        body = level.createBody(bulletBody);
        body.setGravityScale(0.0f);
        bulletSprite.setPosition(body.getPosition().x, body.getPosition().y);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(3.0f / 14f, 3.0f / 14f);

        FixtureDef fixturedef = new FixtureDef();
        fixturedef.shape = shape;
        fixturedef.density= 50;
        fixturedef.filter.categoryBits = isPlayerBullet ? (short) GameObject.ENTITIES_CATEGORIE.BULLET_PLAYER.getValue() : (short) GameObject.ENTITIES_CATEGORIE.BULLET_ENEMY.getValue();
        fixturedef.filter.maskBits = (short) GameObject.ENTITIES_CATEGORIE.LIVING_ENTITIES.getValue() ;

        body.createFixture(fixturedef);

        shape.dispose();
    }

    public void update(float deltaTime){
        x = "RIGHT".equals(direction) ? -8f:  8f;
        body.setLinearVelocity(x,0);
        bulletSprite.setPosition(body.getPosition().x * 32 - bulletSprite.getWidth() / 2.1f, body.getPosition().y * 32 - bulletSprite.getHeight() / 2.1f);

        if (isRemovable) {
            level.destroyBody(body);
        }
    }

    private void getCollisionType(Fixture myFixture, String fixtureName){
        T fixture = (T) myFixture.getBody().getUserData();
        if (fixture instanceof Bullet ) {
            if (fixtureName.equals("A")){
                fixtureA = fixture;
            } else {
                fixtureB = fixture;
            }
        } else if (fixture instanceof Player){
            if (fixtureName.equals("A")){
                fixtureA = fixture;
            } else {
                fixtureB = fixture;
            }
        } else if(fixture instanceof Enemy){
            if (fixtureName.equals("A")){
                fixtureA = fixture;
            } else {
                fixtureB = fixture;
            }
        } else {
            if (fixtureName.equals("A")){
                fixtureA = null;
            } else {
                fixtureB = null;
            }
        }
    }

    private void handleBulletsCollisions() {
        ((Bullet) fixtureA).setRemovable(true);
        ((Bullet) fixtureB).setRemovable(true);
    }

    private void handleMapCollision() {
        Bullet bulletFixture = fixtureB instanceof Bullet ? ((Bullet) fixtureB) : ((Bullet) fixtureA);
        bulletFixture.setRemovable(true);
    }

    private void handlePlayerCollision() {
        Player playerFixture = fixtureA instanceof Player ? ((Player) fixtureA) : ((Player) fixtureB);
        Bullet bulletFixture = fixtureB instanceof Bullet ? ((Bullet) fixtureB) : ((Bullet) fixtureA);

        playerFixture.takeDamage(damages);
        bulletFixture.setRemovable(true);

        if (playerFixture.getHp() <= 0){
            Boot.manager.get("Audio/Sound/EnemyDeath1.wav", Sound.class).play();

            System.out.println("PLAYER DEAD");
        }
    }

    private void handleEnemyCollision() {
        Enemy enemyFixture = fixtureA instanceof Enemy ? ((Enemy) fixtureA) : ((Enemy) fixtureB);
        Bullet bulletFixture = fixtureB instanceof Bullet ? ((Bullet) fixtureB) : ((Bullet) fixtureA);

        enemyFixture.takeDamage(damages);

        bulletFixture.setRemovable(true);

        if (enemyFixture.getHp() <= 0){
            enemyFixture.setRemovable(true);
        }

    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        getCollisionType(fixtureA, "A");
        getCollisionType(fixtureB, "B");
        System.out.println(this.fixtureA);
        System.out.println(this.fixtureB);
        if ((this.fixtureB instanceof Bullet && this.fixtureA instanceof Player) || (this.fixtureB instanceof Player && this.fixtureA instanceof Bullet)) {
            handlePlayerCollision();
        } else if ((this.fixtureB instanceof Bullet && this.fixtureA instanceof Enemy) || (this.fixtureB instanceof Enemy && this.fixtureA instanceof Bullet)) {
            handleEnemyCollision();
        } else if ((this.fixtureB instanceof Bullet && this.fixtureA == null) || (this.fixtureB == null && this.fixtureA instanceof Bullet)){
            handleMapCollision();
        }
    }

    @Override
    public void endContact(Contact contact) {}

    @Override
    public void postSolve (Contact contact, ContactImpulse impulse){};

    @Override
    public void preSolve (Contact contact, Manifold oldManifold){};

    public void render(SpriteBatch batch){
        batch.draw(bulletTexture, bulletSprite.getX(), bulletSprite.getY());
    }

    //GETTER
    public int getDamages() {
        return damages;
    }
    public float getY() {
        return y;
    }
    public float getX() {
        return x;
    }
    public boolean isPlayerBullet() {return isPlayerBullet;}
    public boolean isRemovable() {return isRemovable;}
    public Body getBody() {return body;}

    //SETTER
    public void setRemovable(boolean removable) {
        isRemovable = removable;
    }
}
