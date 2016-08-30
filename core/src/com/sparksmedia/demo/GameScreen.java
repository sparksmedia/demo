package com.sparksmedia.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class GameScreen extends ScreenAdapter {
	
	private Hero hero;
	private OrthographicCamera camera;
	
	private TiledMap tiledMap;
	private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
	
	private static final int WORLD_WIDTH = 800;
	private static final int WORLD_HEIGHT = 480;
	private static final int BLOCK = 32;
	private static final int SPEED = BLOCK / 10;
		
	private int cameraX = WORLD_WIDTH / 2;
	private int cameraY = WORLD_HEIGHT / 2;
	private int mapWidth;
	private int mapHeight;
	
	public GameScreen(Demo demo) {
	}
	
	@Override
	public void show() {		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
		camera.position.set(WORLD_WIDTH / 2 - WORLD_WIDTH, WORLD_HEIGHT / 2, 0);
		camera.update();
		
		tiledMap = new TmxMapLoader().load("map.tmx");
		orthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
		
		MapProperties prop = tiledMap.getProperties();
		mapWidth = BLOCK * prop.get("width", Integer.class);
		mapHeight = BLOCK * prop.get("height", Integer.class);
		
		hero = new Hero();
		hero.worldMap(mapWidth, mapHeight, tiledMap);		
	}

	@Override
	public void render(float delta) {		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		cameraMovement();
		
		int[] backgroundLayers = { 0, 1 };
		int[] foregroundLayers = { 2 };
		
		orthogonalTiledMapRenderer.setView(camera);
		orthogonalTiledMapRenderer.render(backgroundLayers);
		
		hero.shapeRenderer.setProjectionMatrix(camera.projection);
		hero.shapeRenderer.setTransformMatrix(camera.view);	
		
		hero.batch.setProjectionMatrix(camera.projection);
		hero.batch.setTransformMatrix(camera.view);
		
		hero.render();		
		orthogonalTiledMapRenderer.render(foregroundLayers);
	}
	
	public void cameraMovement() {
		
		int heroX = hero.getX();
		int heroY = hero.getY();
				
		if(Gdx.input.isKeyPressed(Keys.UP)) {
			if(heroY > cameraY && cameraY < mapHeight - (WORLD_HEIGHT / 2) - 1) {
				cameraY+= SPEED;
			}
		}
		
		if(Gdx.input.isKeyPressed(Keys.DOWN)) {
			if(heroY < cameraY && cameraY > (WORLD_HEIGHT / 2)) {
				cameraY-= SPEED;
			}
		}
				
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
			if(heroX > cameraX && cameraX < mapWidth - (WORLD_WIDTH / 2) - 2) {
				cameraX+= SPEED;
			}
		}
		
		if(Gdx.input.isKeyPressed(Keys.LEFT)) {
			if(heroX < cameraX && cameraX > (WORLD_WIDTH / 2)) {
				cameraX-= SPEED;
			}
		}
		
		camera.position.y = cameraY;
		camera.position.x = cameraX;
		camera.update();
	}
	
}

			