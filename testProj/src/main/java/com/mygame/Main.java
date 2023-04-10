package com.mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl.ControlDirection;

/**
 * A walking physical character followed by a 3rd person camera. (No animation.)
 * @author normenhansen, zathras
 */
public class Main extends SimpleApplication implements ActionListener {

  private BulletAppState bulletAppState;
  private CharacterControl physicsCharacter;
  final private Vector3f walkDirection = new Vector3f(0,0,0);
  final private Vector3f viewDirection = new Vector3f(0,0,0);
  private boolean leftStrafe = false, rightStrafe = false, forward = false, backward = false, 
          leftRotate = false, rightRotate = false;

  public static void main(String[] args) {
    Main app = new Main();
    app.start();
  }

    private void setupKeys() {
        inputManager.addMapping("Strafe Left", 
                new KeyTrigger(KeyInput.KEY_Q), 
                new KeyTrigger(KeyInput.KEY_Z));
        inputManager.addMapping("Strafe Right", 
                new KeyTrigger(KeyInput.KEY_E),
                new KeyTrigger(KeyInput.KEY_X));
        inputManager.addMapping("Rotate Left", 
                new KeyTrigger(KeyInput.KEY_A), 
                new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Rotate Right", 
                new KeyTrigger(KeyInput.KEY_D), 
                new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("Walk Forward", 
                new KeyTrigger(KeyInput.KEY_W), 
                new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("Walk Backward", 
                new KeyTrigger(KeyInput.KEY_S),
                new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("Jump", 
                new KeyTrigger(KeyInput.KEY_SPACE), 
                new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addMapping("Shoot", 
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, "Strafe Left", "Strafe Right");
        inputManager.addListener(this, "Rotate Left", "Rotate Right");
        inputManager.addListener(this, "Walk Forward", "Walk Backward");
        inputManager.addListener(this, "Jump", "Shoot");
    }
  @Override
  public void simpleInitApp() {
    // activate physics
    bulletAppState = new BulletAppState();
    stateManager.attach(bulletAppState);

    // init a physical test scene
    PhysicsTestHelper.createPhysicsTestWorldSoccer(rootNode, assetManager, bulletAppState.getPhysicsSpace());
    setupKeys();

    // Add a physics character to the world
    physicsCharacter = new CharacterControl(new CapsuleCollisionShape(0.5f, 1f), .1f);
    physicsCharacter.setPhysicsLocation(new Vector3f(0, 1, 0));
    Node characterNode = new Node("character node");
    characterNode.addControl(physicsCharacter);
    getPhysicsSpace().add(physicsCharacter);
    rootNode.attachChild(characterNode);

    // set forward camera node that follows the character
    CameraNode camNode = new CameraNode("CamNode", cam);
    camNode.setControlDir(ControlDirection.SpatialToCamera);
    camNode.setLocalTranslation(new Vector3f(0, 0, -1.0f));
    characterNode.attachChild(camNode);

  }

   @Override
    public void simpleUpdate(float tpf) {
        Vector3f camDir = cam.getDirection().mult(0.2f);
        Vector3f camLeft = cam.getLeft().mult(0.2f);
        camDir.y = 0;
        camLeft.y = 0;
        viewDirection.set(camDir);
        walkDirection.set(0, 0, 0);
        if (leftStrafe) {
            walkDirection.addLocal(camLeft);
        } else
        if (rightStrafe) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (leftRotate) {
            viewDirection.addLocal(camLeft.mult(tpf));
        } else
        if (rightRotate) {
            viewDirection.addLocal(camLeft.mult(tpf).negate());
        }
        if (forward) {
            walkDirection.addLocal(camDir);
        } else
        if (backward) {
            walkDirection.addLocal(camDir.negate());
        }
        physicsCharacter.setWalkDirection(walkDirection);
        physicsCharacter.setViewDirection(viewDirection);
    }

  @Override
    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Strafe Left")) {
            if (value) {
                leftStrafe = true;
            } else {
                leftStrafe = false;
            }
        } else if (binding.equals("Strafe Right")) {
            if (value) {
                rightStrafe = true;
            } else {
                rightStrafe = false;
            }
        } else if (binding.equals("Rotate Left")) {
            if (value) {
                leftRotate = true;
            } else {
                leftRotate = false;
            }
        } else if (binding.equals("Rotate Right")) {
            if (value) {
                rightRotate = true;
            } else {
                rightRotate = false;
            }
        } else if (binding.equals("Walk Forward")) {
            if (value) {
                forward = true;
            } else {
                forward = false;
            }
        } else if (binding.equals("Walk Backward")) {
            if (value) {
                backward = true;
            } else {
                backward = false;
            }
        } else if (binding.equals("Jump")) {
            physicsCharacter.jump();
        }
    }

  private PhysicsSpace getPhysicsSpace() {
    return bulletAppState.getPhysicsSpace();
  }

  @Override
  public void simpleRender(RenderManager rm) {
    //TODO: add render code
  }
}

