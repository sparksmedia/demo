package com.sparksmedia.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Hero {
		
	private static final int heroWidth = 64;
	private static final int heroHeight = 64;
	
	private static final int collisionWidth = 32;
	private static final int collisionHeight = 32;
	
	private static final double SPEED = 3.5;
	
	public ShapeRenderer shapeRenderer;
	private Rectangle hero;
	private Rectangle heroMove;
	private int direction;
	
	private int heroX = Gdx.graphics.getWidth() / 2;
	private int heroY = Gdx.graphics.getHeight() / 2;
	
	private int mapWidth;
	private int mapHeight;
	
	private TiledMap tiledMap;	
	private int countObjects;
	Array<Rectangle> collisionRect;
	
	private Texture texture;
	private TextureRegion currentFrame;
	TextureRegion[][] textureRegion;
	
	private final int COLS = 9;
	private final int ROWS = 4;
	
	private Animation upAnimation;
	private Animation downAnimation;
	private Animation rightAnimation;
	private Animation leftAnimation;
	
	public SpriteBatch batch;	
	private float stateTime;
	
	private Enemy enemy;
	
	public Hero() {
		hero = new Rectangle(heroX, heroY, collisionWidth, collisionHeight);
		heroMove = new Rectangle(heroX, heroY, collisionWidth, collisionHeight);
		collisionRect = new Array<Rectangle>();
		
		shapeRenderer = new ShapeRenderer();
		
		texture = new Texture(Gdx.files.internal("hero.png"));		
		textureRegion = TextureRegion.split(texture, texture.getWidth() / COLS, texture.getHeight() / ROWS);
		
        upAnimation = new Animation(0.075f, textureRegion[0]);
        downAnimation = new Animation(0.075f, textureRegion[2]);
        rightAnimation = new Animation(0.075f, textureRegion[3]);
        leftAnimation = new Animation(0.075f, textureRegion[1]);
        
		batch = new SpriteBatch();
		stateTime = 0f;
		
		enemy = new Enemy();
	}
		
	public void worldMap(int mapWidth, int mapHeight, TiledMap tiledMap) {		
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		this.tiledMap = tiledMap;
		
		checkObjects();
	}
	
	public void render() {
		int heroDirection = heroMovement();
		
		//DEBUG
		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.WHITE);
		//shapeRenderer.rect(hero.x, hero.y, hero.width, hero.height);	
		//shapeRenderer.end();
		
		stateTime += Gdx.graphics.getDeltaTime();		
		batch.begin();
		
		if(heroDirection == 1) {
			currentFrame = upAnimation.getKeyFrame(stateTime, true);
		}
		else if(heroDirection == 2) {
			currentFrame = downAnimation.getKeyFrame(stateTime, true);
		}
		else if(heroDirection == 3) {
			currentFrame = rightAnimation.getKeyFrame(stateTime, true);
		}
		else if(heroDirection == 4) {
			currentFrame = leftAnimation.getKeyFrame(stateTime, true);
		}		
		else {
			currentFrame = textureRegion[2][0];
		}

		batch.draw(currentFrame, hero.x - (collisionWidth / 2), hero.y, heroWidth, heroHeight);
		batch.end();
	}
	
	public int heroMovement() {
			
		//KEY UP
		if(Gdx.input.isKeyPressed(Keys.UP)) {
			if(heroY < mapHeight - heroHeight) {
				heroY+= SPEED;
				direction = 1;
			}
		}
		
		//KEY DOWN
		else if(Gdx.input.isKeyPressed(Keys.DOWN)) {
			if(heroY > 0) {
				heroY-= SPEED;
				direction = 2;
			}
		}
				
		//KEY LEFT
		else if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
			if(heroX < mapWidth - collisionWidth - 1) {
				heroX+= SPEED;
				direction = 3;
			}
		}
		
		//KEY RIGHT
		else if(Gdx.input.isKeyPressed(Keys.LEFT)) {
			if(heroX > 0) {
				heroX-= SPEED;
				direction = 4;
			}
		}
		
		else {
			direction = 0;
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
		
		return direction;
		
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
				collision = true;
			}
		}
		
		Rectangle enemyRect = Enemy.getRectangle();
		if(Intersector.overlaps(heroMove, enemyRect)) {
			System.out.println("HIT");
			collision = true;
			
			if(direction == 1) {
				hero.y -= collisionHeight;
			}
			else if(direction == 2) {
				hero.y += collisionHeight;
			}
			else if(direction == 3) {
				hero.x -= collisionWidth;
			}
			else if(direction == 4) {
				hero.x += collisionWidth;
			}
			else {
				if(enemy.getEnemyDirection() == 1) {
					hero.y += collisionHeight;
				}
				else if(enemy.getEnemyDirection() == 2) {
					hero.y -= collisionHeight;
				}
				else if(enemy.getEnemyDirection() == 3) {
					hero.x += collisionWidth;
				}
				else if(enemy.getEnemyDirection() == 4) {
					hero.x -= collisionWidth;
				}
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
