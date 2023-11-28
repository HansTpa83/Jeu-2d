package com.mygdx.game.gameobject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Boot;

public class Enemy extends LivingEntities{
    private float stateTime;
    public Sprite enemyIdle;
    private TextureRegion currentFrame;
    private EnemyState currentState;
    private EnemyState previousState;
    private Animation shootAnimation;
    private float shootInterval = .8f;
    private float shootTimer;
    private boolean isRemovable;
    private boolean isLeft;
    private boolean isActivated;

    private enum EnemyState {
        IDLE, SHOOT
    }

    public Enemy(Vector2 position, World world){
        super("basicEnemy", position, 5, 1, 4, true, world, ENTITIES_CATEGORIE.LIVING_ENTITIES.getValue(), ENTITIES_CATEGORIE.BULLET_PLAYER.getValue());
        body.setUserData(this);
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("playerAnimations/enemy.txt"));
        TextureAtlas.AtlasRegion region = atlas.findRegion("idle");
        Array<TextureAtlas.AtlasRegion> shootingFrames = atlas.findRegions("shoot");
        shootAnimation = new Animation(0.28f, shootingFrames, Animation.PlayMode.LOOP);
        currentFrame = region;
        currentState = EnemyState.IDLE;
        shootTimer = 0;

        enemyIdle = new Sprite(region);
        enemyIdle.setScale(0.7f);
        enemyIdle.setOrigin(enemyIdle.getWidth() / 2, enemyIdle.getHeight() / 2);
        enemyIdle.setPosition(body.getPosition().x * 32 - enemyIdle.getWidth() / 1.65f, body.getPosition().y * 32 - enemyIdle.getHeight() / 2f);
    }

    @Override
    public void update(float delta) {
            stateTime = previousState == currentState ? stateTime + delta : 0;
            shootTimer += delta;

            setAction();
            setCurrentAnimation(stateTime);
            previousState = currentState;
            enemyIdle.setPosition(body.getPosition().x * 32 - enemyIdle.getWidth() / 1.65f, body.getPosition().y * 32 - enemyIdle.getHeight() / 2f);

            for(Bullet bullet : bullets){
                bullet.update(delta);

                if(bullet.isRemovable()){
                    bulletsToDelete.add(bullet);
                }
            }
            bullets.removeAll(bulletsToDelete);

            if (isRemovable) {
                for(Bullet bullet : bullets){
                    level.destroyBody(bullet.getBody());
                }
                bullets.removeAll(bullets);
                Boot.manager.get("Audio/Sound/EnemyDeath2.wav", Sound.class).play();

                level.destroyBody(body);
            }
    }

    public void shoot(){
        bullets.add(new Bullet(body.getPosition().x - 1, body.getPosition().y + 0.23f, level, "RIGHT", false, damages));
    }

    public void draw(SpriteBatch batch, OrthographicCamera camera){
        batch.draw(currentFrame, enemyIdle.getX(), enemyIdle.getY(), enemyIdle.getOriginX(), enemyIdle.getOriginY(),
                enemyIdle.getWidth(), enemyIdle.getHeight(), enemyIdle.getScaleX(), enemyIdle.getScaleY(), enemyIdle.getRotation());

        isEnemyVisible(camera);

        for(Bullet bullet : bullets){
            bullet.render(batch);
        }
    }

    private void setCurrentAnimation(float delta){
        switch (currentState) {
            case IDLE:
                currentFrame = enemyIdle;
                break;
            case SHOOT:
                currentFrame = (TextureRegion) shootAnimation.getKeyFrame(delta);
//                if ( isLeft && !currentFrame.isFlipX()) {
//                    currentFrame.flip(true, false);
//                } else if (!isLeft && currentFrame.isFlipX()) {
//                    currentFrame.flip(true, false);
//                }d
                break;
        }
    }

    public void isEnemyVisible(OrthographicCamera camera) {
        float enemyX = body.getPosition().x * 32;
        float enemyY = body.getPosition().y * 32;

        isActivated = camera.frustum.pointInFrustum(new Vector3(enemyX, enemyY, 0));
    }

    @Override
    public void playerMovement(){}

    @Override
    protected void setAction(){
        if(shootTimer < shootInterval && isActivated) {
            currentState = EnemyState.SHOOT;
        } else if (shootTimer >= shootInterval && isActivated) {
            shootTimer = 0;
            shoot();
        } else {
            currentState = EnemyState.IDLE;
        }
    }

    public void setRemovable(boolean removable) {
        isRemovable = removable;
    }

    public boolean isRemovable() {
        return isRemovable;
    }
}
