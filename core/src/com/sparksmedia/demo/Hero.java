package com.sparksmedia.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Hero {
		
	private static final int BLOCK = 32;
	private static final int SPEED = BLOCK / 10;
	
	private Rectangle hero;
	private Rectangle heroMove;
	
	private int heroX = Gdx.graphics.getWidth() / 2;
	private int heroY = Gdx.graphics.getHeight() / 2;
	
	private int mapWidth;
	private int mapHeight;
	
	private TiledMap tiledMap;	
	private int countObjects;
	Array<Rectangle> collisionRect;
		
	public Hero() {
		hero = new Rectangle(heroX, heroY, BLOCK, BLOCK);
		heroMove = new Rectangle(heroX, heroY, BLOCK, BLOCK);
		collisionRect = new Array<Rectangle>();
	}
	
	public void worldMap(int mapWidth, int mapHeight, TiledMap tiledMap) {		
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		this.tiledMap = tiledMap;
		
		checkObjects();
	}
	
	public void render(ShapeRenderer shapeRenderer) {
		heroMovement();		
		shapeRenderer.setColor(Color.WHITE);
		shapeRenderer.rect(hero.x, hero.y, hero.width, hero.height);
	}
	
	public void heroMovement() {
		
		//KEY UP
		if(Gdx.input.isKeyPressed(Keys.UP)) {
			if(heroY < mapHeight - BLOCK - 2) {
				heroY+= SPEED;
			}
		}
		
		//KEY DOWN
		if(Gdx.input.isKeyPressed(Keys.DOWN)) {
			if(heroY > 0) {
				heroY-= SPEED;
			}
		}
				
		//KEY LEFT
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
			if(heroX < mapWidth - BLOCK - 1) {
				heroX+= SPEED;
			}
		}
		
		//KEY RIGHT
		if(Gdx.input.isKeyPressed(Keys.LEFT)) {
			if(heroX > 0) {
				heroX-= SPEED;
			}
		}
		
		//MOVE
		heroMove.x = heroX;
		heroMove.y = heroY;
		
		//CHECK COLLISION
		if(checkCollision() == false) {
			hero.x = heroX;
			hero.y = heroY;
		}
		else {
			heroX = (int) hero.x;
			heroY = (int) hero.y;
			heroMove.x = hero.x;
			heroMove.y = hero.y;
		}
		
				
	}
	
	public void checkObjects() {
		MapObjects collisionObjects = tiledMap.getLayers().get("objects").getObjects();
		countObjects = collisionObjects.getCount();
		
		for (int i=0; i < countObjects; i++) {
			RectangleMapObject obj = (RectangleMapObject) collisionObjects.get(i);			
			Rectangle rect = obj.getRectangle();
			collisionRect.add(new Rectangle(rect.x, rect.y, rect.width, rect.height));
		}
	}
	
	public boolean checkCollision() {
		
		boolean collision = false;
				
		for (int i=0; i < countObjects; i++) {		
			if(Intersector.overlaps(heroMove, collisionRect.get(i))) {
				System.out.println("HIT");
				collision = true;
			}
		}
		
		return collision;
		
	}
	
	public int getX() {
		return heroX;
	}
	
	public int getY() {
		return heroY;
	}
}
