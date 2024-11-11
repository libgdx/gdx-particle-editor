package com.ray3k.gdxparticleeditor;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowListener;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.ScaledNumericValue;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ray3k.gdxparticleeditor.Listeners.WindowListener;
import com.ray3k.gdxparticleeditor.runnables.*;
import com.ray3k.gdxparticleeditor.shortcuts.KeyMap;
import com.ray3k.gdxparticleeditor.shortcuts.Shortcut;
import com.ray3k.gdxparticleeditor.shortcuts.ShortcutManager;
import com.ray3k.gdxparticleeditor.widgets.Toast;
import com.ray3k.gdxparticleeditor.widgets.poptables.PopExport;
import com.ray3k.gdxparticleeditor.widgets.tables.ClassicTable;
import com.ray3k.gdxparticleeditor.widgets.tables.WelcomeTable;
import com.ray3k.gdxparticleeditor.widgets.tables.WizardTable;
import com.ray3k.stripe.PopTable;
import com.ray3k.stripe.ViewportWidget;
import space.earlygrey.shapedrawer.ShapeDrawer;

import static com.ray3k.gdxparticleeditor.PresetActions.welcomeAction;
import static com.ray3k.gdxparticleeditor.PreviewSettings.*;
import static com.ray3k.gdxparticleeditor.Settings.*;
import static com.ray3k.gdxparticleeditor.Utils.*;
import static com.ray3k.gdxparticleeditor.widgets.panels.PreviewPanel.*;
import static com.ray3k.gdxparticleeditor.widgets.panels.PreviewPanel.zoomLevelIndex;
import static com.ray3k.gdxparticleeditor.widgets.panels.PreviewPanel.zoomLevels;

/**
 * The primary ApplicationAdapter for the app. It also holds static variables for the utility objects shared by all
 * classes.
 */
public class Core extends ApplicationAdapter {
    public static Skin skin;

    /**
     * The stage used for all UI elements except for pop up dialogs and tooltips. This is to ensure that anything that
     * can overlap the ParticleEffect preview is rendered above it correctly.
     */
    public static Stage stage;

    /**
     * The stage used for pop up dialogs and tooltips. This is to ensure that anything that can overlap the
     * ParticleEffect preview is rendered above it correctly.
     */
    public static Stage foregroundStage;

    public static SpriteBatch spriteBatch;

    /***
     * Indicates if a screen resize has happened during this frame. Used for transitions to ensure positions are correct.
     */
    public static boolean windowResized;

    /**
     * Viewport used for the UI encompassing the entire window.
     */
    public static ScreenViewport viewport;

    /**
     * Used for the ParticleEffect preview. This viewport is repositioned and scaled based on the viewport widget
     * embedded in the UI.
     */
    public static ScreenViewport previewViewport;

    /**
     * Camera for the ParticleEffect preview. This is repositionable by the user.
     */
    public static OrthographicCamera previewCamera;

    /**
     * The container holding all UI elements.
     */
    public static Container<Actor> root;

    /**
     * The version number prepended with "ver " to be used throughout the app.
     */
    public static String version;

    /**
     *  The version number read directly from file.
     */
    public static String versionRaw;

    /**
     * The background color of the app.
     */
    private final Color bgColor = new Color();

    public static ShapeDrawer shapeDrawer;

    /**
     * The widget used to contain the preview viewport within the layout of the currently active screen.
     */
    public static ViewportWidget viewportWidget;

    /**
     * The object that handles the drawing of the ParticleEffect preview.
     */
    public static ParticlePreview particlePreview;

    /**
     * The persistent preferences for the app. This includes keyboard shortcuts, scale, save options, and more.
     */
    public static Preferences preferences;

    /**
     * The particle effect used for preview and for saving to file.
     */
    public static ParticleEffect particleEffect;

    /**
     * The collection of all emitters for the particle effect. When an emitter is disabled, it is marked as false in
     * this map and removed from the particleEffect. All active emitters are exported when the user clicks "Save".
     */
    public static OrderedMap<ParticleEmitter, Boolean> activeEmitters;

    /**
     * The currently selected emitter. The user can edit the properties of this emitter in the EmitterPropertiesPanel.
     */
    public static ParticleEmitter selectedEmitter;

    /**
     * A map that associates an emitter with a defaultAngle. When an emitter is deactivated, the angle is stored
     * temporarily in the app in case the user activates it later. This is only necessary with angle values.
     */
    public static ObjectMap<ParticleEmitter, ScaledNumericValue> defaultAngleMap = new ObjectMap<>();

    /**
     * The currently loaded vert shader for the ParticleEffect preview.
     */
    public static FileHandle vertShaderFile;

    /**
     * The currently loaded frag shader for the ParticleEffect preview.
     */
    public static FileHandle fragShaderFile;

    /**
     * The shader program for the ParticleEffect preview.
     */
    public static ShaderProgram shaderProgram;

    /**
     * The time that has passed since the last frame. This is passed to the shader.
     */
    public static float time;

    /**
     * The collection of all the image fileHandles loaded for the ParticleEffect. The FileHandles are keyed by the name
     * of the file.
     */
    public static ObjectMap<String, FileHandle> fileHandles;

    /**
     * The collection of all sprites created for the ParticleEffect. A sprite is created for every image loaded. Each
     * sprite is keyed by its associated file name.
     */
    public static ObjectMap<String, Sprite> sprites;

    /**
     * The default name to be used when the user saves the ParticleEffect.
     */
    public static String defaultFileName;

    /**
     * The currently opened particle file handle.
     */
    public static FileHandle openFileFileHandle;

    /**
     * The ShortcutManager used to detect key combinations.
     */
    public static ShortcutManager shortcutManager;

    /**
     * The KeyMap used in conjunction with the shortcutManager to detect key combinations.
     */
    public static KeyMap keyMap;

    /**
     * The Runnable used to save the particle effect. A file chooser is opened for the user to select a file path and name.
     */
    public static SaveAsRunnable saveAsRunnable;

    /**
     * The Runnable used to save the particle effect. If the particle effect hasn't been saved before, a file chooser is
     * opened for the user to select a file path and name.
     */
    public static SaveRunnable saveRunnable;

    /**
     * The Runnable used to open a particle effect from a file. A file chooser is opened for the user to select a file
     * path and name.
     */
    public static OpenRunnable openRunnable;

    /**
     * The Runnable used to open a particle effect from a file and merge it with the existing one. A file chooser is
     * opened for the user to select a file path and name.
     */
    public static MergeRunnable mergeRunnable;

    /**
     * The Runnable used to open a file chooser to select any number of images to be added to the currently selected
     * emitter.
     */
    public static ImagesRunnable imagesRunnable;

    /**
     * The Runnable used to switch the visible mode without having to go to the WelcomeScreen.
     */
    public static SwitchModeRunnable switchModeRunnable;

    /**
     * The Runnable used to reload the emitter images.
     */
    public static ReloadImagesRunnable reloadImagesRunnable;

    /**
     * The maximum number of particles recorded in 5 second intervals. Used for the preview stats and the SummaryPanel.
     */
    public static int maxParticleCount;

    /**
     * The number of decimal places used in the spinners used throughout the app.
     */
    public static final int SPINNER_DECIMAL_PLACES = 2;

    /**
     * A name detailing the currently opened table: "Welcome", "Classic", and "Wizard".
     */
    public static String openTable;

    /**
     * The default window title that is displayed at the top of the GLFW window.
     */
    public static final String DEFAULT_WINDOW_TITLE = "GDX Particle Editor - Untitled Particle";

    /**
     * Indicates if changes have been made to the currently open file.
     */
    public static boolean unsavedChangesMade = false;

    /**
     * A window listener that intercepts a close request to determine if the user would like to save the particle effect
     * if there are any unsaved changes.
     */
    public static Lwjgl3WindowListener windowListener = new WindowListener();

    /**
     * Indicates if the application window can be closed. This is used in conjunction with windowListener to prevent
     * the user from closing without the option to save their changes.
     */
    public static boolean allowClose = true;

    /**
     * The currently displayed Toast. Is null when there is no Toast visible.
     */
    public static Toast currentToast;

    /**
     * The queue of Toasts that haven't been displayed yet.
     */
    public static Array<Toast> toastQueue;

    /**
     * The list of all open tooltips that is automatically updated as tooltips are opened and closed.
     */
    public static Array<PopTable> tooltips;

    private final static Vector2 temp = new Vector2();

    @Override
    public void create() {
        sizeWindowToScreenHeight(950/1080f, 1000/950f);

        versionRaw = Gdx.files.classpath("version").readString();
        version = "ver " + versionRaw;
        defaultFileName = "particle.p";

        preferences = Gdx.app.getPreferences("GDX Particle Editor");
        fileHandles = new ObjectMap<>();
        sprites = new ObjectMap<>();

        logFile = Gdx.files.external(".gdxparticleeditor/log.txt");
        logFile.mkdirs();
        logFile.delete();
        Gdx.app.setApplicationLogger(new TextFileApplicationLogger(logFile));

        PreviewSettings.initializeSettings();

        viewport = new ScreenViewport();
        previewCamera = new OrthographicCamera();
        previewViewport = new ScreenViewport(previewCamera);
        viewportWidget = new ViewportWidget(previewViewport);
        spriteBatch = new SpriteBatch();
        stage = new Stage(viewport, spriteBatch);
        foregroundStage = new Stage(viewport, spriteBatch);

        openRunnable = new OpenRunnable();
        mergeRunnable = new MergeRunnable();
        saveAsRunnable = new SaveAsRunnable();
        saveRunnable = new SaveRunnable();
        saveAsRunnable.setSaveRunnable(saveRunnable);
        saveRunnable.setSaveAsRunnable(saveAsRunnable);
        imagesRunnable = new ImagesRunnable();
        switchModeRunnable = new SwitchModeRunnable();
        reloadImagesRunnable = new ReloadImagesRunnable();

        initKeyMap();
        shortcutManager = new ShortcutManager();
        shortcutManager.setKeyMap(keyMap);

        updateViewportScale(valueToUIscale(preferences.getFloat(NAME_SCALE, DEFAULT_SCALE)));
        previewViewport.setUnitsPerPixel(zoomLevels.get(zoomLevelIndex) / getPixelsPerMeter());

        SkinLoader.loadSkin();
        shapeDrawer = new ShapeDrawer(spriteBatch, skin.getRegion("white-pixel"));
        particlePreview = new ParticlePreview();

        toastQueue = new Array<>();

        tooltips = new Array<>();

        activeEmitters = new OrderedMap<>();
        loadParticle(Gdx.files.internal("flame.p"));
        selectedEmitter = particleEffect.getEmitters().first();

        Listeners.initializeListeners();

        bgColor.set(skin.getColor("bg"));

        populate(null);

        initShaderProgram();
        updateWindowTitle();

        //Set the initial FPS
        if (preferences.contains(NAME_FPS)) {
            var fps = preferences.getInteger(NAME_FPS);
            Gdx.graphics.setForegroundFPS(fps);
            Gdx.graphics.setVSync(fps == Gdx.graphics.getDisplayMode().refreshRate);
        }
    }

    public static void populate(String openTable) {
        stage.clear();
        foregroundStage.clear();

        root = new Container<>();
        root.setFillParent(true);
        root.fill();
        stage.addActor(root);

        stage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (event.getTarget().getListeners().contains(Listeners.noCaptureKeyboardFocusListener, true)) return false;
                if (stage.getKeyboardFocus() == null) return false;
                if (event.getTarget() == stage.getKeyboardFocus()) return false;
                if (event.getTarget().isDescendantOf(stage.getKeyboardFocus())) return false;

                stage.setKeyboardFocus(null);
                return false;
            }
        });

        stage.addListener(shortcutManager);

        Core.openTable = openTable;
        if (Core.openTable == null) Core.openTable = preferences.getString(NAME_OPEN_TO_SCREEN, DEFAULT_OPEN_TO_SCREEN);

        switch (Core.openTable) {
            case "Welcome":
                var welcomeTable = new WelcomeTable();
                root.setActor(welcomeTable);
                welcomeTable.addAction(welcomeAction(welcomeTable));
                break;
            case "Classic":
                var classicTable = new ClassicTable();
                root.setActor(classicTable);
                classicTable.addAction(welcomeAction(classicTable));
                break;
            case "Wizard":
                var wizardTable = new WizardTable();
                root.setActor(wizardTable);
                wizardTable.addAction(welcomeAction(wizardTable));
                break;
        }

        Gdx.input.setInputProcessor(stage);
    }

    private void initKeyMap () {
        keyMap = new KeyMap();
        Array<Shortcut> shortcuts = new Array<>();

        shortcuts.add(createShortcut("Undo", "Keyboard shortcut for the Undo action.", DEFAULT_UNDO_PRIMARY_KEYBIND, null, GLOBAL_SCOPE, new UndoShortcutRunnable()));
        shortcuts.add(createShortcut("Redo", "Keyboard shortcut for the Redo action.", DEFAULT_REDO_PRIMARY_KEYBIND, DEFAULT_REDO_SECONDARY_KEYBIND, GLOBAL_SCOPE, new RedoShortcutRunnable()));
        shortcuts.add(createShortcut("Save", "Keyboard shortcut for the Save action.", DEFAULT_SAVE_PRIMARY_KEYBIND, null, GLOBAL_SCOPE, saveRunnable));
        shortcuts.add(createShortcut("Save As", "Keyboard shortcut for the Save As action.", DEFAULT_SAVE_AS_PRIMARY_KEYBIND, null, GLOBAL_SCOPE, saveAsRunnable));
        shortcuts.add(createShortcut("Export", "Keyboard shortcut for the Export action.", DEFAULT_EXPORT_PRIMARY_KEYBIND, null, GLOBAL_SCOPE, () -> {
            var pop = new PopExport();
            pop.show(foregroundStage);
        }));
        shortcuts.add(createShortcut("Open", "Keyboard shortcut for the Open action.", DEFAULT_OPEN_PRIMARY_KEYBIND, null, GLOBAL_SCOPE, openRunnable));
        shortcuts.add(createShortcut("Switch Mode", "Keyboard shortcut to switch the visible mode.", DEFAULT_SWITCH_MODE_KEYBIND, null, GLOBAL_SCOPE, switchModeRunnable));
        shortcuts.add(createShortcut("Reload Images", "Keyboard shortcut to reload the emitter images.", DEFAULT_RELOAD_IMAGES_KEYBIND, null, GLOBAL_SCOPE, reloadImagesRunnable));

        // Classic only keybinds
//        shortcuts.add(createShortcut("(Classic) Classic", "Hello Classic", primaryKeybind, secondaryKeybind, CLASSIC_SCOPE, () -> System.out.println("Hello Classic")));

        // Wizard only keybinds
//        shortcuts.add(createShortcut("(Wizard) Hello", "Save things", primaryKeybind, secondaryKeybind, WIZARD_SCOPE, () -> System.out.println("Hello Wizard")));

        keyMap.addAll(shortcuts);
    }

    public static void initShaderProgram() {
        var vertex = vertShaderFile == null ? spriteBatch.getShader().getVertexShaderSource() : vertShaderFile.readString();
        var frag = fragShaderFile == null ? spriteBatch.getShader().getFragmentShaderSource() : fragShaderFile.readString();
        shaderProgram = new ShaderProgram(vertex, frag);
        ShaderProgram.pedantic = false;
    }

    public static void updateWindowTitle() {
        var title = openFileFileHandle == null ? Core.DEFAULT_WINDOW_TITLE : "GDX Particle Editor - " + openFileFileHandle.name();
        if (unsavedChangesMade || openFileFileHandle == null) title += "*";
        Gdx.graphics.setTitle(title);
    }

    @Override
    public void resize(int width, int height) {
        if (width + height > 0) {
            viewport.update(width, height, true);
            windowResized = true;
        }
    }

    @Override
    public void render() {
        ScreenUtils.clear(bgColor);

        stage.act();
        viewport.apply();
        stage.draw();

        time += Gdx.graphics.getDeltaTime();
        spriteBatch.setShader(shaderProgram);
        if (shaderProgram != null) {
            shaderProgram.bind();
            shaderProgram.setUniformf("u_amount", 10);
            shaderProgram.setUniformf("u_speed", .5f);
            shaderProgram.setUniformf("u_time", time);
        }
        viewportWidget.updateViewport(false);
        particlePreview.render();
        spriteBatch.setShader(null);

        //Draw the previewButtonsTable under the foreground but above the particle preview
        viewport.apply();
        if (previewStack != null) {
            spriteBatch.begin();
            spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

            float oldX = previewStack.getX();
            float oldY = previewStack.getY();
            temp.set(oldX, oldY);
            previewStack.getParent().localToStageCoordinates(temp);
            previewStack.setPosition(temp.x, temp.y);
            previewStack.setColor(Color.WHITE);
            previewStack.draw(spriteBatch, 1f);
            previewStack.setPosition(oldX, oldY);
            previewStack.setColor(1, 1, 1, 0);
            spriteBatch.end();
        }

        foregroundStage.act();
        foregroundStage.draw();

        windowResized = false;
    }
}
