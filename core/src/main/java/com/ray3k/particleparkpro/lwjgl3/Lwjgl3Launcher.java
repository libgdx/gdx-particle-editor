package com.ray3k.particleparkpro.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.ray3k.particleparkpro.Core;

public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return;
        try {
            createApplication();
        } catch (Throwable throwable) {
            Gdx.app.error(Lwjgl3Launcher.class.getName(), "Error while running application", throwable);
        }
    }

    private static Lwjgl3Application createApplication() {
        var core = new Core();
        return new Lwjgl3Application(core, getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle(Core.DEFAULT_WINDOW_TITLE);
        configuration.useVsync(true);
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate);
        configuration.setBackBufferConfig(8, 8, 8, 8, 16, 0, 10);
        configuration.setWindowedMode(1000, 950);
        configuration.setWindowIcon("icon128.png", "icon64.png", "icon32.png", "icon16.png");
        configuration.setWindowListener(Core.windowListener);
        return configuration;
    }
}
