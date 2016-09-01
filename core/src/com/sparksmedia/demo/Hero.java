package com.sparksmedia.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.utils.TimeUtils;

public class Hero {
		
	private static final int heroWidth = 64;
	private static final int heroHeight = 64;
	
	private static final int collisionWidth = 32;
	private static final int collisionHeight = 32;
	
	private static final double SPEED = 3.5;
	
	public ShapeRenderer shapeRenderer;
	private Rectangle hero;
	private Rectangle heroMove;
	
	private int heroX = Gdx.graphics.getWidth() / 2;
	private int heroY = Gdx.graphics.getHeight() / 2;
	
	private int direction;
	private Color heroHitColor;
	private long heroHitTime;
	
	private Rectangle heroWeapon;
	private boolean heroAttack;
	private long heroWeaponTime;
	private float heroWeaponX = 0;
	private float heroWeaponY = 0;
	
	private int mapWidth;
	private int mapHeight;
	
	private TiledMap tiledMap;	
	private int countObjects;
	Array<Rectangle> collisionRect;
	
	private Texture texture;
	private TextureRegion[][] textureRegion;
	private TextureRegion currentFrame;	
	
	private Texture weapon;
	private TextureRegion[][] weaponRegion;
	private TextureRegion weaponFrame;		
	
	private final int COLS = 9;
	private final int ROWS = 4;
	
	private Animation upAnimation;
	private Animation downAnimation;
	private Animation rightAnimation;
	private Animation leftAnimation;
	
	private Animation weaponUpAnimation;
	private Animation weaponDownAnimation;
	private Animation weaponRightAnimation;
	private Animation weaponLeftAnimation;
	
	public SpriteBatch batch;	
	private float stateTime;
	
	private Enemy enemy;
	
	public Hero() {
		hero = new Rectangle(heroX, heroY, collisionWidth, collisionHeight);
		heroMove = new Rectangle(heroX, heroY, collisionWidth, collisionHeight);
			
		heroHitColor = Color.WHITE;
		heroHitTime = System.currentTimeMillis();
		
		heroWeapon = new Rectangle();	
		heroAttack = false;
		heroWeaponTime = System.currentTimeMillis();
		
		collisionRect = new Array<Rectangle>();		
		shapeRenderer = new ShapeRenderer();
		
		texture = new Texture(Gdx.files.internal("hero.png"));		
		textureRegion = TextureRegion.split(texture, texture.getWidth() / COLS, texture.getHeight() / ROWS);
		currentFrame = textureRegion[2][0];
		
		weapon = new Texture(Gdx.files.internal("weapon-spear.png"));
		weaponRegion = TextureRegion.split(weapon, weapon.getWidth() / 8, weapon.getHeight() / ROWS);
		weaponFrame = weaponRegion[2][4];
				
        upAnimation = new Animation(0.075f, textureRegion[0]);
        downAnimation = new Animation(0.075f, textureRegion[2]);
        rightAnimation = new Animation(0.075f, textureRegion[3]);
        leftAnimation = new Animation(0.075f, textureRegion[1]);
        
		weaponUpAnimation = new Animation(0.075f, weaponRegion[0]);
		weaponDownAnimation = new Animation(0.075f, weaponRegion[2]);
		weaponRightAnimation = new Animation(0.075f, weaponRegion[3]);
		weaponLeftAnimation = new Animation(0.075f, weaponRegion[1]);
		
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
		//shapeRenderer.rect(hero.x, hero.y, hero.width, hero.height);	
		//shapeRenderer.end();
				
		stateTime += Gdx.graphics.getDeltaTime();		
		batch.begin();
		
		if(heroDirection == 1) {
			currentFrame = upAnimation.getKeyFrame(stateTime, true);
			weaponFrame = weaponUpAnimation.getKeyFrame(stateTime, true);
		}
		else if(heroDirection == 2) {
			currentFrame = downAnimation.getKeyFrame(stateTime, true);
			weaponFrame = weaponDownAnimation.getKeyFrame(stateTime, true);
		}
		else if(heroDirection == 3) {
			currentFrame = rightAnimation.getKeyFrame(stateTime, true);
			weaponFrame = weaponRightAnimation.getKeyFrame(stateTime, true);
		}
		else if(heroDirection == 4) {
			currentFrame = leftAnimation.getKeyFrame(stateTime, true);
			weaponFrame = weaponLeftAnimation.getKeyFrame(stateTime, true);
		}
					
		if(System.currentTimeMillis() - heroHitTime > 2000) {
			heroHitColor = Color.WHITE;
		}
				
		if(heroAttack == true && System.currentTimeMillis() - heroWeaponTime > 500) {
			heroWeapon = new Rectangle();
			heroAttack = false;
		}
							
		batch.setColor(heroHitColor);		
		
		if(heroAttack == true) {
			batch.draw(weaponFrame, hero.x - (collisionWidth / 2), hero.y, heroWidth, heroHeight);
		}
		else {
			batch.draw(currentFrame, hero.x - (collisionWidth / 2), hero.y, heroWidth, heroHeight);
		}
		
		batch.end();
				
		//DEBUG
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(Color.BLACK);
		shapeRenderer.rect(heroWeapon.x, heroWeapon.y, heroWeapon.width, heroWeapon.height);	
		shapeRenderer.end();
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
		if(Gdx.input.isKeyPressed(Keys.DOWN)) {
			if(heroY > 0) {
				heroY-= SPEED;
				direction = 2;
			}
		}
				
		//KEY LEFT
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
			if(heroX < mapWidth - collisionWidth - 1) {
				heroX+= SPEED;
				direction = 3;
			}
		}
		
		//KEY RIGHT
		if(Gdx.input.isKeyPressed(Keys.LEFT)) {
			if(heroX > 0) {
				heroX-= SPEED;
				direction = 4;
			}
		}
		
		//SPACE
		if(Gdx.input.isKeyPressed(Keys.SPACE)) {
			heroWeaponTime = System.currentTimeMillis();
			heroAttack = true;
		}
		
		//NO KEY
		if(Gdx.input.isKeyPressed(Keys.ANY_KEY)) {
		}
		
		//MOVE
		heroMove.x = heroX;
		heroMove.y = heroY;
		
		//WEAPON
		if(heroAttack) {
			
			if(direction == 0 || direction == 2) {
				heroWeaponX = hero.x;
				heroWeaponY = hero.y - collisionHeight;
			}
			if(direction == 1) {
				heroWeaponX = hero.x;
				heroWeaponY = hero.y + collisionHeight;
			}
			else if(direction == 3) {
				heroWeaponX = hero.x + collisionWidth;
				heroWeaponY = hero.y;
			}
			else if(direction == 4) {
				heroWeaponX = hero.x - collisionHeight;
				heroWeaponY = hero.y;
			}
			
			heroWeapon.x = heroWeaponX;
			heroWeapon.y = heroWeaponY;
			heroWeapon.width = hero.width;
			heroWeapon.height = hero.height;
		}
		
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
			collision = true;
			
			heroHitColor = Color.RED;
			heroHitTime = TimeUtils.millis();
			
			if(direction == 1) {
				hero.y -= collisionHeight * 2;
			}
			else if(direction == 2) {
				hero.y += collisionHeight * 2;
			}
			else if(direction == 3) {
				hero.x -= collisionWidth * 2;
			}
			else if(direction == 4) {
				hero.x += collisionWidth * 2;
			}
			else {
				if(enemy.getEnemyDirection() == 1) {
					hero.y += collisionHeight * 2;
				}
				else if(enemy.getEnemyDirection() == 2) {
					hero.y -= collisionHeight * 2;
				}
				else if(enemy.getEnemyDirection() == 3) {
					hero.x += collisionWidth * 2;
				}
				else if(enemy.getEnemyDirection() == 4) {
					hero.x -= collisionWidth * 2;
				}
			}
		}
		
		if(Intersector.overlaps(heroWeapon, enemyRect)) {
			System.out.println("HIT");
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
