package com.sparksmedia.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Hero {
		
	private static final int BLOCK = 32;
	private static final int SPEED = BLOCK / 10;
	
	private int heroX = Gdx.graphics.getWidth() / 2;
	private int heroY = Gdx.graphics.getHeight() / 2;
	
	private int mapWidth;
	private int mapHeight;
	
	public void worldMap(int mapWidth, int mapHeight) {		
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
	}
	
	public void render(ShapeRenderer shapeRenderer) {
		heroMovement();		
		shapeRenderer.rect(heroX, heroY, 32, 32);
	}
	
	public void heroMovement() {
		
		if(Gdx.input.isKeyPressed(Keys.UP)) {
			if(heroY < mapHeight - BLOCK - 2) {
				heroY+= SPEED;
			}
		}
		
		if(Gdx.input.isKeyPressed(Keys.DOWN)) {
			if(heroY > 0) {
				heroY-= SPEED;
			}
		}
				
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
			if(heroX < mapWidth - BLOCK - 3) {
				heroX+= SPEED;
			}
		}
		
		if(Gdx.input.isKeyPressed(Keys.LEFT)) {
			if(heroX > 0) {
				heroX-= SPEED;
			}
		}
	}
	
	public int getX() {
		return heroX;
	}
	
	public int getY() {
		return heroY;
	}
}
