package com.mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;

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
    
    public static void main(String[] args) {
        Main app = new Main();
        app.setShowSettings(false);
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
    
    private void startGame() {
        gameRunning = true;
        
        // activate physics
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        // init a physical test scene
        Maze.createWorld(rootNode, assetManager, bulletAppState.getPhysicsSpace());
        setupKeys();

        // Add a physics character to the world
        physicsCharacter = new CharacterControl(new CapsuleCollisionShape(0.5f, 1f), .1f);
        physicsCharacter.setPhysicsLocation(new Vector3f(0, 0, 0));
        Node characterNode = new Node("character node");
        characterNode.addControl(physicsCharacter);
        getPhysicsSpace().add(physicsCharacter);
        rootNode.attachChild(characterNode);

        // set forward camera node that follows the character
        CameraNode camNode = new CameraNode("CamNode", cam);
        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        camNode.setLocalTranslation(new Vector3f(0, 0, -1.0f));
        characterNode.attachChild(camNode);
    }
    
    private void startMenu() {
        // Initialize the globals access so that the defualt
        // components can find what they need.
        GuiGlobals.initialize(this);
    
        // Create a simple container for our elements
        Container myWindow = new Container();
        guiNode.attachChild(myWindow);
            
        // Put it somewhere that we will see it
        // Note: Lemur GUI elements grow down from the upper left corner.
        myWindow.setLocalTranslation(300, 300, 0);
        
        Button clickMe = myWindow.addChild(new Button("Start Maze"));
        clickMe.addClickCommands(new Command<Button>() {
            @Override
            public void execute( Button source ) {
                startGame();
                guiNode.detachChild(myWindow);
            }
        });
    }

    @Override
    public void simpleInitApp() {
        startMenu();
        //startGame();
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
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}

