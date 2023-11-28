package com.mygdx.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.physics.box2d.BodyDef.*;
import static com.badlogic.gdx.physics.box2d.BodyDef.BodyType.*;

public class TileMapHelper {

    public TmxMapLoader mapLoader;
    public TiledMap tiledMap;
    private GameScreen gameScreen;

    int countLevel = 0;
    public String actualLevel;

    public TileMapHelper(GameScreen gameScreen){
        this.gameScreen = gameScreen;
    }

    /*private List<ShapeType> shapeTypes = new ArrayList<>();

    public List<ShapeType> getShapeTypes() {
        return shapeTypes;
    }

    public void setShapeTypes(List<ShapeType> shapeTypes) {
        this.shapeTypes = shapeTypes;
    }
*/
    public OrthogonalTiledMapRenderer setupMap(int countLevel){
        actualLevel = "Levels/Level" + countLevel +".tmx";
        mapLoader = new TmxMapLoader();
        if(tiledMap != null){

        }

        tiledMap = mapLoader.load(Gdx.files.internal(actualLevel).file().getAbsolutePath());
        List<Shape> levelDesign = parseMapObjects(tiledMap.getLayers().get("Ld").getObjects());
        List<Shape> flag = parseMapObjects(tiledMap.getLayers().get("Flag").getObjects());
        List<ShapeType> shapeTypes = new ArrayList<>();

        for (Shape shape: levelDesign){
            shapeTypes.add(new ShapeType(shape, false));
        }
        for (Shape shape: flag){
            shapeTypes.add(new ShapeType(shape, true));
        }

        return new OrthogonalTiledMapRenderer(tiledMap);
    }

    private List<Shape> parseMapObjects(MapObjects mapObjects){
        List<Shape> shapes= new ArrayList<>();
        for(MapObject mapObject : mapObjects){
            if(mapObject instanceof PolygonMapObject){
                Shape shape = createStaticBody((PolygonMapObject) mapObject);
                shapes.add(shape);
            }
        }
        return shapes;
    }

    private  Shape createStaticBody(PolygonMapObject polygonMapObject){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = StaticBody;
        //System.out.println(gameScreen.getWorld());
        Body body = gameScreen.getWorld().createBody((bodyDef));
        Shape shape = createPolygonShape(polygonMapObject);
        body.createFixture(shape, 1000);
        shape.dispose();
        body.setUserData(this);
        return shape;
    }
    private Shape createPolygonShape(PolygonMapObject polygonMapObject) {
        float[] vertices = polygonMapObject.getPolygon().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for(int i =0; i< vertices.length/2; i++){
            Vector2 current = new Vector2(vertices[i*2]/ 32.0f,vertices[i*2+1]/32.0f);
            worldVertices[i] = current;
        }
        PolygonShape shape = new PolygonShape();
        shape.set(worldVertices);
        return shape;
    }
}

