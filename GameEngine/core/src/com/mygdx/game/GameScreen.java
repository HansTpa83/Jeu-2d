package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.gameobject.Bullet;
import com.mygdx.game.gameobject.Enemies;
import com.mygdx.game.gameobject.Enemy;
import com.mygdx.game.gameobject.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class GameScreen extends ScreenAdapter {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private World world;
    private Enemies worldEnemies;
    int nextLevel =1;
    private Music music;
    private Box2DDebugRenderer box2DDebugRenderer;
    private Player player;
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
    private TileMapHelper tileMapHelper;

    public GameScreen(OrthographicCamera camera) {
        this.camera = camera;
        this.batch = new SpriteBatch();
        this.world = new World(new Vector2(0, -25), false);
        this.box2DDebugRenderer = new Box2DDebugRenderer();
        this.tileMapHelper = new TileMapHelper(this);
        this.orthogonalTiledMapRenderer = tileMapHelper.setupMap(1);

        setPlayer(new Player(new Vector2(1, 6), world));
        setWorldEnemies(
                new Enemies(world,
                        new ArrayList<>(
                                Arrays.asList(
                                        new Vector2(12.5f, 3.2f),
                                        new Vector2(15, 4)
                                )
                        )
                ));
    }

    public void setOrthogonalTiledMapRenderer(OrthogonalTiledMapRenderer orthogonalTiledMapRenderer) {
        this.orthogonalTiledMapRenderer = orthogonalTiledMapRenderer;
    }

    private void update(float delta) {

        if (worldEnemies.getEnemies().size() == 0) {
            this.world = new World(new Vector2(0, -25), false);
            System.out.println("Go Next");
            nextLevel++;
            this.orthogonalTiledMapRenderer = tileMapHelper.setupMap(nextLevel);
            setPlayer(new Player(new Vector2(1, 6), world));
            setWorldEnemies(
                    new Enemies(world,
                            new ArrayList<>(
                                    Arrays.asList(
                                            new Vector2(12.5f, 3.2f),
                                            new Vector2(15, 4)
                                    )
                            )
                    ));


        }
        world.step(1 / 60f, 6, 2);
        cameraUpdate();
        batch.setProjectionMatrix(camera.combined);
        orthogonalTiledMapRenderer.setView(camera);
        player.update(delta);
        if (player.getHp() <= 0){
            Gdx.app.exit();
        }
        worldEnemies.update(delta);
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    private void cameraUpdate() {
        Vector3 position = new Vector3(0, 0, 0);
        position.x = (float) Math.round(player.getBody().getPosition().x * 32f);
        position.y = (float) Math.round(player.getBody().getPosition().y * 32f);
        camera.position.set(position);
        camera.update();
    }

    @Override
    public void render(float delta) {
        this.update(delta);

        Gdx.gl.glClearColor(40 / 255f, 40 / 255f, 40 / 255f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        orthogonalTiledMapRenderer.render();


        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.draw(batch);
        worldEnemies.draw(batch, camera);
        batch.end();
        music = Boot.manager.get("Audio/Music/LevelMusic.mp3", Music.class);
        music.setVolume(0.2f);
        music.setLooping(true);
        music.play();
//        box2DDebugRenderer.render(world, camera.combined.scl(32.0f));
    }

    public World getWorld() {
        return world;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setWorldEnemies(Enemies worldEnemies) {
        this.worldEnemies = worldEnemies;
    }
}
