package com.mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector2f;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.component.IconComponent;
import com.simsilica.lemur.component.QuadBackgroundComponent;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication implements ActionListener {

    private BulletAppState bulletAppState;
    private CharacterControl physicsCharacter;
    final private Vector3f walkDirection = new Vector3f(0,0,0);
    final private Vector3f viewDirection = new Vector3f(0,0,0);
    private boolean leftStrafe = false, rightStrafe = false, forward = false, backward = false;
    private boolean gameRunning = false;
    static Main app;
    float[] coords = new float[4]; //brings back starting coords and finish coords
    boolean restarted = false;
    
    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Maze Game");
        settings.setResolution(1600, 900);
        
        app = new Main();
        app.setShowSettings(false);
        app.setSettings(settings);
        app.start();
    }
    
    private void setupKeys() {
        inputManager.addMapping("Strafe Left", 
                new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Strafe Right", 
                new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Walk Forward", 
                new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Walk Backward", 
                new KeyTrigger(KeyInput.KEY_S));
        inputManager.addListener(this, "Strafe Left", "Strafe Right");
        inputManager.addListener(this, "Walk Forward", "Walk Backward");
        inputManager.addListener(this, "Jump");
        
        inputManager.setCursorVisible(false);
    }
    
    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }
    
    private void startGame(String type) {
        gameRunning = true;
        
        // activate physics
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        // init a physical test scene
        coords = Maze.createWorld(rootNode, assetManager, bulletAppState.getPhysicsSpace(), type);
        setupKeys();

        // Add a physics character to the world
        physicsCharacter = new CharacterControl(new CapsuleCollisionShape(0.5f, 1.8f), .1f);
        physicsCharacter.setPhysicsLocation(new Vector3f(0, 1.0f, 0.0f));
        Node characterNode = new Node("character node");
        characterNode.addControl(physicsCharacter);
        getPhysicsSpace().add(physicsCharacter);
        rootNode.attachChild(characterNode);
        //teleports the player to a specific location, this is going to be EXTREMELY useful with starting locations
        physicsCharacter.warp(new Vector3f(coords[0], 1.0f, coords[1]));

        // set forward camera node that follows the character
        CameraNode camNode = new CameraNode("CamNode", cam);
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        camNode.setLocalTranslation(new Vector3f(0.0f, 1.0f,-1.0f));
        characterNode.attachChild(camNode);
    }
    
    private void startMenu() {
        // Initialize the globals access so that the defualt
        // components can find what they need.
        GuiGlobals.initialize(this);
    
        // Create a simple container for our elements
        Container myWindow = new Container();
        
        QuadBackgroundComponent background = new QuadBackgroundComponent(ColorRGBA.DarkGray, 10, 20);

        
        myWindow.setBackground(background);
        guiNode.attachChild(myWindow);
            
        // Put it somewhere that we will see it
        // Note: Lemur GUI elements grow down from the upper left corner.
        Vector3f pref = myWindow.getPreferredSize().mult(0.5f);
        myWindow.setLocalTranslation(settings.getWidth() * 0.5f - 26f * pref.x, settings.getHeight() * 0.7f + pref.y, 0);
        
        IconComponent iconLogo = new IconComponent("Icons/mazegame_logo.png", 1f, 0, 0, 0, paused);
        Label labelLogo = myWindow.addChild(new Label(""));
        labelLogo.setInsets(new Insets3f(0, 0, 50f, 0));
        labelLogo.setIcon(iconLogo);
        
        Button buttonStartDeepslateMaze = myWindow.addChild(new Button("Start Deepslate Maze"));
        IconComponent iconDeepslate = new IconComponent("Icons/deepslate_ico.png", 0.20f, 0, -15, 0, paused);
        buttonStartDeepslateMaze.setIcon(iconDeepslate);
        buttonStartDeepslateMaze.setInsets(new Insets3f(0, 0, 50f, 0));
        buttonStartDeepslateMaze.addClickCommands(new Command<Button>() {
            @Override
            public void execute( Button source ) {
                startGame("Deepslate");
                guiNode.detachChild(myWindow);
            }
        });
        
        
        Button buttonStartStrongholdMaze = myWindow.addChild(new Button("Start Stronghold Maze"));
        buttonStartStrongholdMaze.setInsets(new Insets3f(0, 0, 25f, 0));
        IconComponent iconStronghold = new IconComponent("Icons/stonebricks_ico.png", 0.20f, 0, -15, 0, paused);
        buttonStartStrongholdMaze.setIcon(iconStronghold);
        buttonStartStrongholdMaze.addClickCommands(new Command<Button>() {
            @Override
            public void execute( Button source ) {
                startGame("Stronghold");
                guiNode.detachChild(myWindow);
            }
        });
        
        Button buttonExit = myWindow.addChild(new Button("Exit Game"));
        buttonExit.setInsets(new Insets3f(0, 0, 0f, 0));
        buttonExit.addClickCommands(new Command<Button>() {
            @Override
            public void execute( Button source ) {
                app.stop();
            }
        });
    }

    @Override
    public void simpleInitApp() {
        startMenu();
    }
    
    @Override
    public void onAction(String binding, boolean value, float tpf) {
        switch (binding) {
            case "Strafe Left":
                leftStrafe = value;
                break;
            case "Strafe Right":
                rightStrafe = value;
                break;
            case "Walk Forward":
                forward = value;
                break;
            case "Walk Backward":
                backward = value;
                break;
            default:
                break;
        }
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        if (!gameRunning) {
            return;
        }
        
        Vector3f camDir = cam.getDirection().mult(0.2f);
        Vector3f camLeft = cam.getLeft().mult(0.2f);
        viewDirection.set(camDir);
        walkDirection.set(0, 0, 0);
        
        if (leftStrafe) {
            walkDirection.addLocal(camLeft);
        }
        else if (rightStrafe) {
            walkDirection.addLocal(camLeft.negate());
        }
        
        if (forward) {
            walkDirection.addLocal(camDir);
        }
        else if (backward) {
            walkDirection.addLocal(camDir.negate());
        }
        
        physicsCharacter.setWalkDirection(walkDirection);
        physicsCharacter.setViewDirection(viewDirection);
        
        checkForFinish(coords);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    public void checkForFinish(float[] coords) {
        //finish coordinates. currently stuck here rn
        if ((physicsCharacter.getPhysicsLocation().x <= (coords[2] + 1.0f)) && (physicsCharacter.getPhysicsLocation().x >= (coords[2] - 1.0f))
            && (physicsCharacter.getPhysicsLocation().z <= (coords[3] + 1.0f)) && (physicsCharacter.getPhysicsLocation().z >= (coords[3] - 1.0f))) {
                gameRunning = false;
                stateManager.cleanup();
                physicsCharacter.warp(new Vector3f(coords[0], 1.0f, coords[1]));
                inputManager.clearMappings();
                startMenu();
        }
    }
}
