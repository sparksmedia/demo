package com.sparksmedia.demo;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Enemy {
	
	private static final int enemyWidth = 64;
	private static final int enemyHeight = 64;
	private static double speed = 1;
	
	private static boolean enemyAlive = true;
	private static int enemyHP = 100;
	private static int enemyAtk = 10;
	
	private static int direction = 3;	
	
	private static int heroAtk;
	private int enemyX = Gdx.graphics.getWidth() / 2 + 200;
	private int enemyY = Gdx.graphics.getHeight() / 2 + 200;
		
	private static final int collisionWidth = 32;
	private static final int collisionHeight = 48;
	
	public ShapeRenderer shapeRenderer;
	public ShapeRenderer enemyHPBar;
	private static Rectangle enemy;
	
	private static Circle enemyArea;
	private int enemyAreaWidth = 200;
	
	private static Color enemyHitColor;
	private static long enemyHitTime;
	
	private static int dieTime = 0;
	
	private Texture texture;
	private TextureRegion[][] textureRegion;
	private TextureRegion currentFrame;
	
	private Texture die;
	private TextureRegion[][] dieRegion;
	
	private Animation upAnimation;
	private Animation downAnimation;
	private Animation rightAnimation;
	private Animation leftAnimation;
	
	private Animation dieAnimation;
	
	public SpriteBatch batch;
	
	private float stateTime;
	private static int randomNum;
	
	private int countObjects;
	private int mapWidth;
	private int mapHeight;
	private Array<Rectangle> collisionRect;
	
	public Enemy() {
		shapeRenderer = new ShapeRenderer();
		enemyHPBar = new ShapeRenderer();
		
		enemy = new Rectangle(enemyX, enemyY, collisionWidth, collisionHeight);
		enemyArea = new Circle(enemy.x + (enemy.width / 2), enemy.y + (enemy.height / 2), enemyAreaWidth);
		
		enemyHitColor = Color.WHITE;
		enemyHitTime = System.currentTimeMillis();
		
		texture = new Texture(Gdx.files.internal("enemy.png"));		
		textureRegion = TextureRegion.split(texture, texture.getWidth() / 9, texture.getHeight() / 4);
		
		die = new Texture(Gdx.files.internal("enemy-die.png"));
		dieRegion = TextureRegion.split(die, die.getWidth() / 6, die.getHeight());
		
        upAnimation = new Animation(0.075f, textureRegion[0]);
        downAnimation = new Animation(0.075f, textureRegion[2]);
        rightAnimation = new Animation(0.075f, textureRegion[3]);
        leftAnimation = new Animation(0.075f, textureRegion[1]);
        
        dieAnimation = new Animation(0.075f, dieRegion[0]);
        
		batch = new SpriteBatch();		
		stateTime = 0f;		
		
		System.out.println("ENEMY");
	}
	
	public void render() {
		
		stateTime += Gdx.graphics.getDeltaTime();
		batch.begin();
		
		if(enemyAlive == true) {
			enemyMovement();
			enemyAttackArea();
		}
		
		enemyAnimation();
				
		batch.setColor(enemyHitColor);
		batch.draw(currentFrame, enemy.x - (collisionWidth / 2), enemy.y, enemyWidth, enemyHeight);
		batch.end();
		
		enemyHPBar();
		
		debug();			
	}
	
	public void enemyAnimation() {
		
		if(System.currentTimeMillis() - enemyHitTime > 1000) {
			enemyHitColor = Color.WHITE;
		}
			
		if(direction == 1) {
			currentFrame = upAnimation.getKeyFrame(stateTime, true);
			enemy.y += speed;
		}
		else if(direction == 2) {
			currentFrame = downAnimation.getKeyFrame(stateTime, true);	
			enemy.y -= speed;
		}
		else if(direction == 3) {
			currentFrame = rightAnimation.getKeyFrame(stateTime, true);
			enemy.x += speed;
		}
		else if(direction == 4) {
			currentFrame = leftAnimation.getKeyFrame(stateTime, true);
			enemy.x -= speed;
		}
		else if(direction == 0) {
			if(dieTime == 12) {
				currentFrame = dieRegion[0][5];
			}
			else {
				currentFrame = dieAnimation.getKeyFrame(stateTime, true);
				dieTime++;
			}
		}
	}
	
	public void enemyMovement() {
		boolean collision = false;
		
		countObjects = collisionRect.size;

		for (int i=0; i < countObjects; i++) {		
			if(Intersector.overlaps(enemy, collisionRect.get(i))) {
				collision = true;
			}
		}
				
		if(	enemy.y >= mapHeight - collisionHeight || 
			enemy.y <= 0 || 
			enemy.x >= mapWidth - 
			enemyWidth || enemy.x <= 0) {
			
			collision = true;
		}
		
		if(collision == true) {
			enemyCollision();
		}

	}
	
	public Circle enemyAttackArea() {
		enemyArea.x = enemy.x + (enemy.width / 2);
		enemyArea.y = enemy.y + (enemy.height / 2);
		
		return enemyArea;
	}
	
	public void enemyAttack(float heroX, float heroY, boolean active) {
	
		if(active == true) {
			speed = 2;
			
			if(enemy.y < heroY) {
				direction = 1;
				enemy.y += speed - 1;
			}
			else if(enemy.x < heroX) {
				direction =  3;
			}
			else if(enemy.y > heroY) {
				direction = 2;
			}
			else if(enemy.x > heroX) {
				direction = 4;
			}
			
		}
		else {
			speed = 1;
		}
	}

	
	public void enemyCollision() {
		
		if(direction == 1) {
			enemy.y -= 1;
		}
		else if(direction == 2) {
			enemy.y += 1;
		}
		else if(direction == 3) {
			enemy.x -= 1;
		}
		else if(direction == 4) {
			enemy.x += 1;
		}
		
		int prevDirection = direction;
		int newDirection = randomNum(1, 4);
		
		if(prevDirection != newDirection) {
			direction = newDirection;
		}
	}
	
	public void getMap(int mapWidth, int mapHeight, Array<Rectangle> collisionRect) {
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		this.collisionRect = collisionRect;
		
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
	
	public static boolean getStatus() {
		return enemyAlive;
	}
	
	public int getAtk() {
		return enemyAtk;
	}
	
	public static void hitEnemy(int value) {
		
		heroAtk = value;
		enemyHitTime = System.currentTimeMillis();
		enemyHitColor = Color.RED;		
		enemyHP -= heroAtk;
		
		if(enemyHP <= 0) {
			enemyAlive = false;
			direction = 0;
		}
	}
	
	public void enemyHPBar() {		
				
		if(enemyHitColor == Color.RED){
			float enemyLifeLeft = (float)enemyHP / (float)100;
			
			enemyHPBar.begin(ShapeRenderer.ShapeType.Filled);
			enemyHPBar.setColor(Color.WHITE);
			enemyHPBar.rect(enemy.x, enemy.y + enemy.height + 5, enemy.width, 5);			

			if(enemyHP <= 30) {
				enemyHPBar.setColor(Color.RED);
			}
			else {
				enemyHPBar.setColor(Color.GREEN);
			}
			
			enemyHPBar.rect(enemy.x + 1, enemy.y + enemy.height + 6, enemy.width * enemyLifeLeft, 3);		
			enemyHPBar.end();
		}
		
	}
	
	public void debug() {
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(Color.RED);
		shapeRenderer.circle(enemyArea.x, enemyArea.y, enemyArea.radius);
		shapeRenderer.end();
	}

}
