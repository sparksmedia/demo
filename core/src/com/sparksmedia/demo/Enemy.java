package com.sparksmedia.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Enemy {
	
	private static final int enemyWidth = 64;
	private static final int enemyHeight = 64;
	
	private static final int collisionWidth = 32;
	private static final int collisionHeight = 48;
	
	public ShapeRenderer shapeRenderer;
	private Rectangle enemy;
	
	private int enemyX = Gdx.graphics.getWidth() / 2 + 100;
	private int enemyY = Gdx.graphics.getHeight() / 2 + 100;
	
	private int COLS = 9;
	private int ROWS = 4;
	
	private Texture texture;
	private TextureRegion[][] textureRegion;
	private TextureRegion currentFrame;
	
	private Animation upAnimation;
	private Animation downAnimation;
	private Animation rightAnimation;
	private Animation leftAnimation;
	
	public SpriteBatch batch;
	private float stateTime;
	
	public Enemy() {
		shapeRenderer = new ShapeRenderer();
		enemy = new Rectangle(enemyX, enemyY, collisionWidth, collisionHeight);
		
		texture = new Texture(Gdx.files.internal("enemy.png"));		
		textureRegion = TextureRegion.split(texture, texture.getWidth() / COLS, texture.getHeight() / ROWS);
		
        upAnimation = new Animation(0.075f, textureRegion[0]);
        downAnimation = new Animation(0.075f, textureRegion[2]);
        rightAnimation = new Animation(0.075f, textureRegion[3]);
        leftAnimation = new Animation(0.075f, textureRegion[1]);
        
		batch = new SpriteBatch();
		stateTime = 0f;
	}
	
	public void render() {
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(Color.RED);
		shapeRenderer.rect(enemy.x, enemy.y, enemy.width, enemy.height);
		shapeRenderer.end();
		
		stateTime += Gdx.graphics.getDeltaTime();		
		batch.begin();
		
		currentFrame = downAnimation.getKeyFrame(stateTime, true);		
		batch.draw(currentFrame, enemy.x - (collisionWidth / 2), enemy.y, enemyWidth, enemyHeight);
		batch.end();
	}
	
	public Rectangle getRectangle() {
		return enemy;
	}

}
