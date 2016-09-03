package com.sparksmedia.demo;

import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Map {

	private final int BLOCK = 32;
	
	private TiledMap tiledMap;
	private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
	
	private int mapWidth;
	private int mapHeight;
	
	private int countObjects;
	private Array<Rectangle> collisionRect;
	
	public Map() {		
		tiledMap = new TmxMapLoader().load("map.tmx");
		orthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
		
		MapProperties prop = tiledMap.getProperties();
		mapWidth = BLOCK * prop.get("width", Integer.class);
		mapHeight = BLOCK * prop.get("height", Integer.class);
		
		collisionRect = new Array<Rectangle>();	
		mapObjects();
		System.out.println("MAP");
	}
		
	public Array<Rectangle> mapObjects() {
		MapObjects collisionObjects = tiledMap.getLayers().get("objects").getObjects();
		countObjects = collisionObjects.getCount();
		
		for (int i=0; i < countObjects; i++) {
			RectangleMapObject obj = (RectangleMapObject) collisionObjects.get(i);			
			Rectangle rect = obj.getRectangle();
			collisionRect.add(new Rectangle(rect.x, rect.y, rect.width, rect.height));
		}
		
		return collisionRect;
	}
	
	public int getMapWidth() {
		return mapWidth;
	}
	
	public int getMapHeight() {
		return mapHeight;
	}
	
	public OrthogonalTiledMapRenderer getMapRenderer() {
		return orthogonalTiledMapRenderer;
	}
	
	public TiledMap getTiledMap() {
		return tiledMap;
	}
}
