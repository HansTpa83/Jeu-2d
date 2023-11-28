package com.mygdx.game.gameobject;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;

public class Enemies  {
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<Enemy> enemiesToDelete = new ArrayList<>();
    private ArrayList<Vector2> enemiesPositions = new ArrayList<>();

    public Enemies(World world, ArrayList<Vector2> enemiesPositions) {
        this.enemiesPositions = enemiesPositions;

         for(Vector2 enemy : enemiesPositions){
             enemies.add(new Enemy(enemy, world));
        }
    }

    public void update(float delta){
        for(Enemy enemy : enemies){
            enemy.update(delta);
            if (enemy.isRemovable()){
                enemiesToDelete.add(enemy);
                enemy.bullets.removeAll(enemy.bullets);
            }
        }
        enemies.removeAll(enemiesToDelete);
    }

    public void draw(SpriteBatch batch, OrthographicCamera camera){
        for(Enemy enemy : enemies){
            enemy.draw(batch, camera);
        }
    }


    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public ArrayList<Enemy> getEnemiesToDelete() {
        return enemiesToDelete;
    }
}
