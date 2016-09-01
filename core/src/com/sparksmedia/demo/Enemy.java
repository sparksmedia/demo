package com.sparksmedia.demo;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Enemy {
	
	private static final int enemyWidth = 64;
	private static final int enemyHeight = 64;
	
	public static boolean enemyAlive = true;
	private static int enemyHP = 100;
	public static int enemyAtk = 10;
	public static int heroAtk;
	
	public static int dieTime = 0;	
	
	private static final int collisionWidth = 32;
	private static final int collisionHeight = 48;
	
	private static final double SPEED = 1;
	private static int direction = 1;
	
	public ShapeRenderer shapeRenderer;
	private static Rectangle enemy;
	private static Rectangle enemyRange;
	
	private int enemyX = Gdx.graphics.getWidth() / 2 + 200;
	private int enemyY = Gdx.graphics.getHeight() / 2;
	
	private int COLS = 9;
	private int ROWS = 4;
	
	private Texture texture;
	private TextureRegion[][] textureRegion;
	private TextureRegion currentFrame;
	
	private Texture die;
	private TextureRegion[][] dieRegion;
	
	private static Color enemyHitColor;
	private static long enemyHitTime;
	
	private Animation upAnimation;
	private Animation downAnimation;
	private Animation rightAnimation;
	private Animation leftAnimation;
	
	private Animation dieAnimation;
	
	public SpriteBatch batch;
	private BitmapFont font;
	private float stateTime;
	private static int randomNum;
	
	public Enemy() {
		shapeRenderer = new ShapeRenderer();
		enemy = new Rectangle(enemyX, enemyY, collisionWidth, collisionHeight);
		enemyRange = new Rectangle(enemyX - collisionWidth, enemyY - collisionHeight, collisionWidth * 7, collisionHeight * 7);
		enemyHitColor = Color.WHITE;
		
		texture = new Texture(Gdx.files.internal("enemy.png"));		
		textureRegion = TextureRegion.split(texture, texture.getWidth() / COLS, texture.getHeight() / ROWS);
		
		die = new Texture(Gdx.files.internal("enemy-die.png"));
		dieRegion = TextureRegion.split(die, die.getWidth() / 6, die.getHeight());
		
        upAnimation = new Animation(0.075f, textureRegion[0]);
        downAnimation = new Animation(0.075f, textureRegion[2]);
        rightAnimation = new Animation(0.075f, textureRegion[3]);
        leftAnimation = new Animation(0.075f, textureRegion[1]);
        
        dieAnimation = new Animation(0.075f, dieRegion[0]);
        
		batch = new SpriteBatch();
		font = new BitmapFont();
		
		stateTime = 0f;
		enemyHitTime = System.currentTimeMillis();
	}
	
	public void render() {			
		//DEBUG
		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.rect(enemy.x, enemy.y, enemy.width, enemy.height);
		//shapeRenderer.end();		
		//shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		//shapeRenderer.rect(enemyRange.x, enemyRange.y, enemyRange.width, enemyRange.height);
		//shapeRenderer.end();
		
		stateTime += Gdx.graphics.getDeltaTime();
		
		if(System.currentTimeMillis() - enemyHitTime > 1000) {
			enemyHitColor = Color.WHITE;
		}
		
		if(enemyAlive == true) {
			enemyMovement();
		}		
		
		
		
		if(enemyHP <= 0) {
			currentFrame = dieAnimation.getKeyFrame(stateTime, true);
			
			if(dieTime == 12) {
				currentFrame = dieRegion[0][5];
			}
			else {
				dieTime++;
			}
		}
			
		batch.begin();		
		
		if(enemyHitColor == Color.RED){
			font.draw(batch, ""+heroAtk+"", enemy.x + (collisionWidth / 2), enemy.y + enemyHeight);
			
		}
		
		batch.setColor(enemyHitColor);
		batch.draw(currentFrame, enemy.x - (collisionWidth / 2), enemy.y, enemyWidth, enemyHeight);
		batch.end();
			
	}
	
	public void enemyMovement() {
		
		//UP
		if(enemy.y >= enemyRange.y + enemyRange.height - enemy.height) {
			enemy.y -= 1;
			enemyRange();
		}
		//DOWN
		if(enemy.y <= enemyRange.y) {
			enemy.y += 1;
			enemyRange();
		}
		//RIGHT
		if(enemy.x >= enemyRange.x + enemyRange.width - enemy.width) {
			enemy.x -= 1;
			enemyRange();
		}
		//LEFT
		if(enemy.x <= enemyRange.x) {
			enemy.x += 1;
			enemyRange();
		}
		
		if(direction == 0) {
			currentFrame = textureRegion[2][0];
		}		
		if(direction == 1) {
			currentFrame = upAnimation.getKeyFrame(stateTime, true);
			enemy.y += SPEED;
		}
		else if(direction == 2) {
			currentFrame = downAnimation.getKeyFrame(stateTime, true);	
			enemy.y -= SPEED;
		}
		else if(direction == 3) {
			currentFrame = rightAnimation.getKeyFrame(stateTime, true);
			enemy.x += SPEED;
		}
		else if(direction == 4) {
			currentFrame = leftAnimation.getKeyFrame(stateTime, true);
			enemy.x -= SPEED;
		}
	}
	
	public void enemyRange() {
		int prevDirection = direction;
		int newDirection = randomNum(1, 4);
		
		if(prevDirection != newDirection) {
			direction = newDirection;
		}
	}
	
	public static int randomNum(int min, int max) {
		Random rand = new Random();		
		randomNum = rand.nextInt((max - min) + 1) + min;		
		return randomNum;
	}
	
	public int getEnemyDirection() {
		return direction;
	}
	
	public static Rectangle getRectangle() {
		return enemy;
	}
	
	public static void hitEnemy(int value) {
		
		heroAtk = value;
		enemyHitTime = System.currentTimeMillis();
		enemyHitColor = Color.RED;		
		enemyHP -= heroAtk;
		
		if(enemyHP <= 0) {
			enemyAlive = false;
		}
	}

}
