package com.ray3k.gdxparticleeditor.runnables;

import com.ray3k.gdxparticleeditor.Utils;

public class ReloadImagesRunnable implements Runnable {

    @Override
    public void run () {
        Utils.reloadSprites();
    }
}
