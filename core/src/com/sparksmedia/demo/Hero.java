package com.sparksmedia.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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
import com.badlogic.gdx.utils.TimeUtils;

public class Hero {
		
	private static final int heroWidth = 64;
	private static final int heroHeight = 64;
	private static final double SPEED = 3;
	
	private static boolean heroAlive = true;
	private static int heroHP = 100;
	private static int heroAtk = 10;
	
	private int direction = 2;
	private boolean heroMoving = false;
	private int heroX = Gdx.graphics.getWidth() / 2;
	private int heroY = Gdx.graphics.getHeight() / 2;
	
	private static final int collisionWidth = 32;
	private static final int collisionHeight = 32;
		
	public ShapeRenderer shapeRenderer;
	public ShapeRenderer healthBar;
	private Rectangle hero;
	private Rectangle heroMove;		
	
	private Circle heroArea;
	private int heroAreaWidth = 100;
	
	private Color heroHitColor;
	private long heroHitTime;
	private static long heroAtkTime;
	private static int dieTime = 0;
	
	private Rectangle heroWeapon;
	private boolean heroAttack;
	private long heroWeaponTime;
	private float heroWeaponX = 0;
	private float heroWeaponY = 0;
	
	private int mapWidth;
	private int mapHeight;
	
	private int countObjects;
	private Array<Rectangle> collisionRect;
	
	private Texture texture;
	private TextureRegion[][] textureRegion;
	private TextureRegion currentFrame;	
	
	private Texture weapon;
	private TextureRegion[][] weaponRegion;
	
	private Texture die;
	private TextureRegion[][] dieRegion;
	
	private Animation upAnimation;
	private Animation downAnimation;
	private Animation rightAnimation;
	private Animation leftAnimation;
	
	private Animation weaponUpAnimation;
	private Animation weaponDownAnimation;
	private Animation weaponRightAnimation;
	private Animation weaponLeftAnimation;
	
	private Animation dieAnimation;
	
	public SpriteBatch batch;	
	private float stateTime;

	private Enemy enemy;

	public Hero() {
		hero = new Rectangle(heroX, heroY, collisionWidth, collisionHeight);
		heroMove = new Rectangle(heroX, heroY, collisionWidth, collisionHeight);
		heroArea = new Circle(hero.x + (hero.width / 2), hero.y + (hero.height / 2), heroAreaWidth);
			
		heroHitColor = Color.WHITE;
		heroHitTime = System.currentTimeMillis();
		
		heroWeapon = new Rectangle();	
		heroWeaponTime = System.currentTimeMillis();
		heroAttack = false;
		heroAtkTime = System.currentTimeMillis();
		
		collisionRect = new Array<Rectangle>();		
		shapeRenderer = new ShapeRenderer();
		healthBar = new ShapeRenderer();
		
		texture = new Texture(Gdx.files.internal("hero.png"));		
		textureRegion = TextureRegion.split(texture, texture.getWidth() / 9, texture.getHeight() / 4);
		
		weapon = new Texture(Gdx.files.internal("weapon-spear.png"));
		weaponRegion = TextureRegion.split(weapon, weapon.getWidth() / 8, weapon.getHeight() / 4);
		
		die = new Texture(Gdx.files.internal("hero-die.png"));
		dieRegion = TextureRegion.split(die, die.getWidth() / 6, die.getHeight());
				
        upAnimation = new Animation(0.075f, textureRegion[0]);
        downAnimation = new Animation(0.075f, textureRegion[2]);
        rightAnimation = new Animation(0.075f, textureRegion[3]);
        leftAnimation = new Animation(0.075f, textureRegion[1]);
        
		weaponUpAnimation = new Animation(0.075f, weaponRegion[0]);
		weaponDownAnimation = new Animation(0.075f, weaponRegion[2]);
		weaponRightAnimation = new Animation(0.075f, weaponRegion[3]);
		weaponLeftAnimation = new Animation(0.075f, weaponRegion[1]);
		
		dieAnimation = new Animation(0.075f, dieRegion[0]);
		
		batch = new SpriteBatch();
		stateTime = 0f;

		enemy = new Enemy();
		System.out.println("HERO");
	}

	public void getMap(int mapWidth, int mapHeight, Array<Rectangle> collisionRect) {
		this.mapWidth = mapWidth;
		this.mapHeight =  mapHeight;
		this.collisionRect = collisionRect;		
	}
	
	public void render() {
							
		stateTime += Gdx.graphics.getDeltaTime();		
		batch.begin();
		
		if(heroAlive == true) {
			heroMovement();
		}
		
		heroAnimation();
		heroAttack();
										
		batch.setColor(heroHitColor);
		batch.draw(currentFrame, hero.x - (collisionWidth / 2), hero.y, heroWidth, heroHeight);		
		batch.end();
		
		renderDebug();
	}
	
	public void heroAnimation() {
		
		if(direction == 1) {
			if(heroAttack == true) {
				currentFrame = weaponUpAnimation.getKeyFrame(stateTime, true);
			}
			else if(heroMoving == true) {
				currentFrame = upAnimation.getKeyFrame(stateTime, true);
			}
			else {
				currentFrame = textureRegion[0][0];
			}			
		}
		else if(direction == 2) {
			if(heroAttack == true) {
				currentFrame = weaponDownAnimation.getKeyFrame(stateTime, true);
			}
			else if(heroMoving == true) {
				currentFrame = downAnimation.getKeyFrame(stateTime, true);
			}
			else {
				currentFrame = textureRegion[2][0];
			}			
		}
		else if(direction == 3) {
			if(heroAttack == true) {
				currentFrame = weaponRightAnimation.getKeyFrame(stateTime, true);
			}
			else if(heroMoving == true) {
				currentFrame = rightAnimation.getKeyFrame(stateTime, true);
			}
			else {
				currentFrame = textureRegion[3][0];
			}			
		}
		else if(direction == 4) {
			if(heroAttack == true) {
				currentFrame = weaponLeftAnimation.getKeyFrame(stateTime, true);
			}
			else if(heroMoving == true) {
				currentFrame = leftAnimation.getKeyFrame(stateTime, true);
			}
			else {
				currentFrame = textureRegion[1][0];
			}			
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
		
		if(System.currentTimeMillis() - heroHitTime > 1000) {
			heroHitColor = Color.WHITE;
		}
	}
	
	public void heroMovement() {
		
		//KEY UP
		if(Gdx.input.isKeyPressed(Keys.UP)) {
			if(heroY < mapHeight - heroHeight) {
				heroY+= SPEED;
				direction = 1;
				heroMoving = true;
			}
		}
		
		//KEY DOWN
		if(Gdx.input.isKeyPressed(Keys.DOWN)) {
			if(heroY > 0) {
				heroY-= SPEED;
				direction = 2;
				heroMoving = true;
			}
		}
				
		//KEY LEFT
		if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
			if(heroX < mapWidth - collisionWidth - 1) {
				heroX+= SPEED;
				direction = 3;
				heroMoving = true;
			}
		}
		
		//KEY RIGHT
		if(Gdx.input.isKeyPressed(Keys.LEFT)) {
			if(heroX > 0) {
				heroX-= SPEED;
				direction = 4;
				heroMoving = true;
			}
		}
		
		//SPACE
		if(Gdx.input.isKeyPressed(Keys.SPACE)) {
			heroWeaponTime = System.currentTimeMillis();
			heroAttack = true;
		}
		
		//NO KEY
		if(!Gdx.input.isKeyPressed(Keys.ANY_KEY)) {
			heroMoving = false;
		}
				
		//MOVE
		heroMove.x = heroX;
		heroMove.y = heroY;
				
		//CHECK NEXT MOVE
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
	
	public void heroAttack() {
		
		if(heroAttack == true) {
			
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
		
		if(heroAttack == true && System.currentTimeMillis() - heroWeaponTime > 500) {
			heroWeapon = new Rectangle();
			heroAttack = false;
		}
	}
	
	public boolean checkCollision() {
		
		boolean collision = false;
				
		//OBJECTS
		countObjects = collisionRect.size;

		for (int i=0; i < countObjects; i++) {		
			if(Intersector.overlaps(heroMove, collisionRect.get(i))) {
				collision = true;
			}
		}
		
		//ENEMY
		heroAttackArea();
		
		Rectangle enemyRect = Enemy.getRectangle();
		if(Intersector.overlaps(heroMove, enemyRect) && Enemy.getStatus()) {
			collision = true;
			
			hitHero(enemy.getAtk());
			
			heroHitColor = Color.RED;
			heroHitTime = TimeUtils.millis();
			
			if(heroMoving == true) {
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
			}
			else {
				if(enemy.getEnemyDirection() == 1) {
					hero.y += collisionHeight * 2;
				}
				else if(enemy.getEnemyDirection() == 2) {
					hero.y -= collisionHeight * 2;
				}
				else if(enemy.getEnemyDirection() == 3) {
					hero.x -= collisionWidth * 2;
				}
				else if(enemy.getEnemyDirection() == 4) {
					hero.x += collisionWidth * 2;
				}
			}
		}
		
		//ATTACK
		if(System.currentTimeMillis() - heroAtkTime > 800) {			
			if(Intersector.overlaps(heroWeapon, enemyRect) && Enemy.getStatus()) {
				heroAtkTime = System.currentTimeMillis();	
				Enemy.hitEnemy(heroAtk);
			}
			
		}
		
		return collision;
		
	}
	
	public void heroAttackArea() {
		heroArea.x = hero.x + (hero.width / 2);
		heroArea.y = hero.y + (hero.height / 2);
		
		if(Intersector.overlaps(heroArea, enemy.enemyAttackArea()) && Enemy.getStatus()) {
			enemy.enemyAttack(hero.x, hero.y, true);
		}
		else {
			enemy.enemyAttack(0, 0, false);
		}
	}
	
	public void hitHero(int value) {
		heroHP -= value;
		
		if(heroHP <= 0) {
			heroAlive = false;
			direction = 0;
		}
	}
	
	public void HPBar() {
		
		healthBar.begin(ShapeRenderer.ShapeType.Filled);
		healthBar.setColor(Color.WHITE);
		healthBar.rect(Gdx.graphics.getWidth() - 211, Gdx.graphics.getHeight() - 31, 202, 22);
		
		if(heroHP <= 30) {
			healthBar.setColor(Color.RED);
		}
		else {
			healthBar.setColor(Color.GREEN);
		}
		
		healthBar.rect(Gdx.graphics.getWidth() - 210, Gdx.graphics.getHeight() - 30, heroHP * 2, 20);		
		healthBar.end();
		
	}
	
	public int getX() {
		return heroX;
	}
	
	public int getY() {
		return heroY;
	}
	
	public void renderDebug() {		
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(Color.BLUE);
		shapeRenderer.circle(heroArea.x, heroArea.y, heroArea.radius);	
		shapeRenderer.end();
	}

}
