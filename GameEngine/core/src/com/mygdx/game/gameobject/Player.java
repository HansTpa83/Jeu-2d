package com.mygdx.game.gameobject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Boot;

public class Player extends LivingEntities{
    private enum PlayerState {
      IDLE, RUNNING_LEFT, RUNNING_RIGHT, JUMPING, SHOOT, CROUCH
    }
    public Body playerBody;
    private float stateTime;
    public Sprite playerIdle;
    private TextureAtlas atlas;
    private TextureAtlas atlasJump;
    private TextureAtlas atlasCrouch;
    private boolean isLeft = false;
    private TextureAtlas atlasShoot;
    private Animation shootAnimation;
    private PlayerState currentState;
    private PlayerState previousState;
    private Animation runningAnimation;
    private Animation crouchAnimation;
    private TextureRegion currentFrame;
    private Animation jumpingAnimation;
    static final float RUN_FRAME_TIME = 1/12f;
    static final float JUMP_FRAME_TIME = 1/10f;
    static final float SHOOT_FRAME_TIME = 1/6f;

    public Player(Vector2 position, World world){
        super("Player", position, 5, 1, 4, false, world, ENTITIES_CATEGORIE.LIVING_ENTITIES.getValue(), ENTITIES_CATEGORIE.BULLET_ENEMY.getValue());
        body.setUserData(this);
        atlas = new TextureAtlas(Gdx.files.internal("playerAnimations/run.txt"));
        atlasJump = new TextureAtlas(Gdx.files.internal("playerAnimations/jump.txt"));
        atlasShoot = new TextureAtlas(Gdx.files.internal("playerAnimations/shoot.txt"));
        atlasCrouch = new TextureAtlas(Gdx.files.internal( "playerAnimations/crouch.txt"));

        TextureAtlas.AtlasRegion region = atlas.findRegion("idle");
        playerIdle = new Sprite(region);
        playerIdle.setScale(0.5f);
        playerIdle.setOrigin(playerIdle.getWidth() / 2, playerIdle.getHeight() / 2);

        Array<TextureAtlas.AtlasRegion> runningFrames = atlas.findRegions("run");
        runningAnimation = new Animation(RUN_FRAME_TIME, runningFrames, Animation.PlayMode.LOOP);

        Array<TextureAtlas.AtlasRegion> jumpingFrames = atlasJump.findRegions("jump");
        jumpingAnimation = new Animation(JUMP_FRAME_TIME, jumpingFrames, Animation.PlayMode.LOOP);

        Array<TextureAtlas.AtlasRegion> shootFrames = atlasShoot.findRegions("shoot");
        shootAnimation = new Animation(SHOOT_FRAME_TIME, shootFrames, Animation.PlayMode.LOOP);

        Array<TextureAtlas.AtlasRegion> crouchFrames = atlasCrouch.findRegions("crouch");
        crouchAnimation = new Animation(SHOOT_FRAME_TIME, crouchFrames, Animation.PlayMode.NORMAL);

        currentState = PlayerState.IDLE;
    }

    @Override
    protected void playerMovement(){
        int velocityX = 0;
        int velocityY = 0;

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            velocityX = moveLeft(velocityX);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            velocityX = moveRight(velocityX);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            currentState = PlayerState.CROUCH;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && body.getLinearVelocity().y == 0) {
            velocityY = jump(velocityY);
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
            body.applyLinearImpulse(new Vector2(0, body.getMass()*10), body.getPosition(), true);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && (currentState == PlayerState.IDLE || currentState == PlayerState.JUMPING)) {
            if (!isLeft) {
                bullets.add(shoot("LEFT", body.getPosition().x + 1, body.getPosition().y + 0.50f));
            } else {
                bullets.add(shoot("RIGHT", body.getPosition().x - 1, body.getPosition().y + 0.50f));
            }

            currentState = PlayerState.SHOOT;
        }

        if (!Gdx.input.isKeyPressed(Input.Keys.DOWN) && !Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT) &&!Gdx.input.isKeyPressed(Input.Keys.UP) && !Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            currentState = PlayerState.IDLE;
        }

        body.setLinearVelocity(velocityX * speed, body.getLinearVelocity().y < 25 ? body.getLinearVelocity().y : 25);
    }

    public Bullet shoot(String direction, float x, float y) {
        Boot.manager.get("Audio/Sound/Shoot.wav", Sound.class).play();
        return new Bullet(x, y, level, direction, true,damages);
    }

    protected void ResizeBody() {
        Fixture playerFixture = body.getFixtureList().first();
        if (currentState == PlayerState.CROUCH) {
            float crouchedWidth = 6 / 14f;
            float crouchedHeight = 6.0f / 6.5f;

            PolygonShape newShape = new PolygonShape();
            newShape.setAsBox(crouchedWidth, crouchedHeight / 2.5f);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = newShape;
            fixtureDef.filter.categoryBits = categoryBits;
            fixtureDef.filter.maskBits = maskBits;
            body.destroyFixture(playerFixture);
            playerFixture = body.createFixture(fixtureDef);

            newShape.dispose();
        } else {
            float originalWidth = 6 / 14f;
            float originalHeight = 6 / 6.5f;

            PolygonShape newShape = new PolygonShape();
            newShape.setAsBox(originalWidth, originalHeight);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = newShape;
            fixtureDef.filter.categoryBits = categoryBits;
            fixtureDef.filter.maskBits = maskBits;
            body.destroyFixture(playerFixture);
            playerFixture = body.createFixture(fixtureDef);

            newShape.dispose();
        }
    }

    protected void setAction(){
        if (body.getLinearVelocity().y > 0 && currentState != PlayerState.SHOOT){
            currentState = PlayerState.JUMPING;
        } else if (body.getLinearVelocity().x < 0){
            currentState = PlayerState.RUNNING_LEFT;
        }  else if (body.getLinearVelocity().x > 0){
            currentState = PlayerState.RUNNING_RIGHT;
        }
    }

    @Override
    public void update(float delta){
        stateTime = previousState == currentState ? stateTime+ delta : 0;
        playerMovement();
        setAction();
        if(currentState == PlayerState.CROUCH || previousState == PlayerState.CROUCH){
            ResizeBody();
        }
        setCurrentAnimation(stateTime);
        previousState = currentState;
        playerIdle.setPosition(body.getPosition().x * 32 - playerIdle.getWidth() / 2.1f,
                body.getPosition().y * 32 - playerIdle.getHeight() / 2.1f);

        for(Bullet bullet : bullets){
            bullet.update(delta);

            if(bullet.isRemovable()){
                bulletsToDelete.add(bullet);
            }
        }
        bullets.removeAll(bulletsToDelete);
    }

    public void draw(SpriteBatch batch){
        batch.draw(currentFrame, playerIdle.getX(), playerIdle.getY(), playerIdle.getOriginX(), playerIdle.getOriginY(),
                playerIdle.getWidth(), playerIdle.getHeight(), playerIdle.getScaleX(), playerIdle.getScaleY(), playerIdle.getRotation());

        for(Bullet bullet : bullets){
           bullet.render(batch);
        }
    }

    private void setCurrentAnimation(float delta){
        switch (currentState) {
            case IDLE:
                currentFrame = playerIdle;
                if (previousState == PlayerState.RUNNING_LEFT && !currentFrame.isFlipX()) {
                    currentFrame.flip(true, false);
                } else if (previousState == PlayerState.RUNNING_RIGHT && currentFrame.isFlipX()){
                    currentFrame.flip(true, false);
                }
                break;
            case RUNNING_LEFT:
                currentFrame = (TextureRegion) runningAnimation.getKeyFrame(delta, true);
                isLeft = true;
                if (!currentFrame.isFlipX()) {
                    currentFrame.flip(true, false);
                }
                break;
            case RUNNING_RIGHT:
                currentFrame = (TextureRegion) runningAnimation.getKeyFrame(delta, true);
                isLeft = false;
                if (currentFrame.isFlipX()) {
                    currentFrame.flip(true, false);
                }
                break;
            case JUMPING:
                currentFrame = (TextureRegion) jumpingAnimation.getKeyFrame(delta);
                if ( isLeft && !currentFrame.isFlipX()) {
                    currentFrame.flip(true, false);
                } else if (!isLeft && currentFrame.isFlipX()) {
                    currentFrame.flip(true, false);
                }
                break;
            case SHOOT:
                currentFrame = (TextureRegion) shootAnimation.getKeyFrame(delta);
                if ( isLeft && !currentFrame.isFlipX()) {
                    currentFrame.flip(true, false);
                } else if (!isLeft && currentFrame.isFlipX()) {
                    currentFrame.flip(true, false);
                }
                break;
            case CROUCH:
                currentFrame = (TextureRegion) crouchAnimation.getKeyFrame(delta);
                if ( isLeft && !currentFrame.isFlipX()) {
                    currentFrame.flip(true, false);
                } else if (!isLeft && currentFrame.isFlipX()) {
                    currentFrame.flip(true, false);
                }
                break;
        }
    }
}
