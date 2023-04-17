package com.mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import static com.mygame.mazeMaps.returnMazeMap;

public class Maze {
    /**
     * A private constructor to inhibit instantiation of this class.
     */
    private Maze() {
    }

    /**
     * @param rootNode where lights and geometries should be added
     * @param assetManager for loading assets
     * @param space where collision objects should be added
     */

    public static float[] createWorld(Node rootNode, AssetManager assetManager, PhysicsSpace space, String type, AmbientLight ambientLight) {
        String floorTexturePath = "";
        String wallTexturePath = "";
        
        if (type.equals("Deepslate")) {
            ambientLight.setColor(ColorRGBA.White.mult(0.5f));
            floorTexturePath = "Textures/stoneFloor.jpg";
            wallTexturePath = "Textures/stoneWall.jpg";
        }
        else if (type.equals("Stronghold")) {
            ambientLight.setColor(ColorRGBA.White.mult(0.25f));
            floorTexturePath = "Textures/stone_bricks.png";
            wallTexturePath = "Textures/stone_bricks.png";
        }
        
        rootNode.addLight(ambientLight);

        Material floorMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        floorMaterial.setTexture("DiffuseMap", assetManager.loadTexture(floorTexturePath));
        
        Material wallMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        wallMaterial.setTexture("DiffuseMap", assetManager.loadTexture(wallTexturePath));
        
        //follows a right-handed coordinate system, go right and down to increase in j and i respectively
        //also keeping maze maps in another file to keep this one cleaner.
        float[][] maze = returnMazeMap(type);

        float[] returnArray = new float[4];
        
        for (int j = 0; j < maze.length; j++) {
            for (int i = 0; i < maze[j].length; i++) {
                if (maze[j][i] == 0) { //this is the floor
                    createFloor(rootNode, assetManager, space, floorMaterial, "floor", i, j);
                    createFloor(rootNode, assetManager, space, floorMaterial, "ceiling", i, j);
                }
                else if (maze[j][i] == 1) { //this is a wall
                    createWall(rootNode, assetManager, space, wallMaterial, i, j);
                }
                else if (maze[j][i] == 2) { //this is a doorway
                    createFloor(rootNode, assetManager, space, floorMaterial, "floor", i, j);
                    createDoorway(rootNode, assetManager, space, wallMaterial, i, j);
                }
                else if (maze[j][i] == 8) { //this is the starting location, spawn player here
                    //blocks are in multiples of 2 so gotta multiply it by that same amount
                    createFloor(rootNode, assetManager, space, floorMaterial, "floor", i, j);
                    createFloor(rootNode, assetManager, space, floorMaterial, "ceiling", i, j);
                    returnArray[0] = (float)i*2.0f;
                    returnArray[1] = (float)j*2.0f;
                }
                else if (maze[j][i] == 9) {
                    Material floorFinishMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
                    floorFinishMaterial.setTexture("DiffuseMap", assetManager.loadTexture("Textures/finishBlock.png"));
                    createFloor(rootNode, assetManager, space, floorFinishMaterial, "floor", i, j);
                    createFloor(rootNode, assetManager, space, floorMaterial, "ceiling", i, j);
                    returnArray[2] = (float)i*2.0f;
                    returnArray[3] = (float)j*2.0f;
                }
            }
        }
        return returnArray; //sends starting coordinates back to the main program
    }
    
    /*Here, I got tired of seeing the same chunk of code dozens of times. Made these methods to condense the lines.*/
    //method to generate floors
    public static void createFloor (Node rootNode, AssetManager assetManager, PhysicsSpace space, Material floorMaterial, 
            String location, int value1, int value2) {
        int i = value1, j = value2; //i is the taken x coordinate, j in the taken z coordinate.
        float yLevel = 0; //determines which height the floor tiles are being placed at.
        if (location.equals("floor")) {
            yLevel = -0.25f;
        }
        else if (location.equals("ceiling")){
            yLevel = 6.25f;
        }
        Box floorBox = new Box(1, 0.25f, 1);
        Geometry floorGeometry = new Geometry("Floor", floorBox);
        floorGeometry.setMaterial(floorMaterial);
        floorGeometry.setLocalTranslation((i*2f), yLevel, (j*2f));
        floorGeometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(floorGeometry);
        space.add(floorGeometry);
    }
    
    //method to generate walls
    public static void createWall (Node rootNode, AssetManager assetManager, PhysicsSpace space, Material wallMaterial, 
            int value1, int value2) {
        int x = value1, z = value2, y = 0;
        Box box = new Box(1, 1, 1);
        for (y = 0; y <= 2; y++) { //stack 3 boxes on each other to simulate a "wall"
            Geometry boxGeometry = new Geometry("Box", box);
            boxGeometry.setMaterial(wallMaterial);
            boxGeometry.setLocalTranslation((x*2f), (1+y*2f) , (z*2f));
            boxGeometry.addControl(new RigidBodyControl(new MeshCollisionShape(box), 0));
            rootNode.attachChild(boxGeometry);
            space.add(boxGeometry);
        }
    }
    public static void createDoorway (Node rootNode, AssetManager assetManager, PhysicsSpace space, Material wallMaterial, 
            int value1, int value2) {
        int x = value1, z = value2, y = 0;
        Box box = new Box(1, 1, 1);
        Geometry boxGeometry = new Geometry("Box", box);
        boxGeometry.setMaterial(wallMaterial);
        boxGeometry.setLocalTranslation((x*2f), (5f) , (z*2f));
        boxGeometry.addControl(new RigidBodyControl(new MeshCollisionShape(box), 0));
        rootNode.attachChild(boxGeometry);
        space.add(boxGeometry);
    }
}