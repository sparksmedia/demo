package com.sparksmedia.demo.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.sparksmedia.demo.Demo;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Demo";
		config.width = 800;
		config.height = 480;
		new LwjglApplication(new Demo(), config);
	}
}
