package djs.game.ball.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import djs.game.ball.CGame;
import djs.game.ball.CPlayer;

public class DesktopLauncher {
	// constants
	private static final String TAG = DesktopLauncher.class.toString();

	// functions
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 750 / 2;
		config.height = 1334 / 2;
		config.fullscreen = false;
		config.vSyncEnabled = true;
		config.forceExit = false;
		config.resizable = false;
		config.title = "Ball";
		new LwjglApplication(new CGame(null), config);
	}
}
