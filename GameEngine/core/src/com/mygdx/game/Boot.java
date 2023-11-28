package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class Boot extends Game {
    public static  Boot INSTANCE;
    private int widthScreen, heigthScreen;
    private OrthographicCamera orthographicCamera;

    public Boot(){
        INSTANCE = this;
    }

    public static AssetManager manager;

    @Override
    public void create() {
        widthScreen = Gdx.graphics.getWidth();
        heigthScreen = Gdx.graphics.getHeight();
        orthographicCamera = new OrthographicCamera();
        orthographicCamera.setToOrtho(false, widthScreen, heigthScreen);

        // gère le son du jeu
        manager = new AssetManager();
        manager.load("Audio/Music/LevelMusic.mp3", Music.class);
        manager.load("Audio/Sound/Shoot.wav", Sound.class);
        manager.load("Audio/Sound/BulletHit.wav", Sound.class);
        manager.load("Audio/Sound/Jump.mp3", Sound.class);
//        manager.load("Audio/Sound/PlayerHit.wav", Sound.class);
        manager.load("Audio/Sound/EnemyDeath0.wav", Sound.class);
        manager.load("Audio/Sound/EnemyDeath1.wav", Sound.class);
        manager.load("Audio/Sound/EnemyDeath2.wav", Sound.class);

        manager.finishLoading();

        setScreen(new GameScreen(orthographicCamera ));
    }


}
