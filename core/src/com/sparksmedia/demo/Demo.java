package com.sparksmedia.demo;

import com.badlogic.gdx.Game;

public class Demo extends Game {
    
	@Override
	public void create () {
		setScreen(new GameScreen(this));
	}
}
