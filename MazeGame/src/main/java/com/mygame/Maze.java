package com.mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

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

    public static float[] createWorld(Node rootNode, AssetManager assetManager, PhysicsSpace space, String type) {
        String floorTexturePath = "";
        String wallTexturePath = "";
        
        if (type.equals("Deepslate")) {
            floorTexturePath = "Textures/stoneFloor.jpg";
            wallTexturePath = "Textures/stoneWall.jpg";
        }
        else if (type.equals("Stronghold")) {
            floorTexturePath = "Textures/stone_bricks.png";
            wallTexturePath = "Textures/stone_bricks.png";
        }

        Material floorMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        floorMaterial.setTexture("ColorMap", assetManager.loadTexture(floorTexturePath));
        
        Material wallMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        wallMaterial.setTexture("ColorMap", assetManager.loadTexture(wallTexturePath));
        
        //follows a right-handed coordinate system, go right and down to increase in j and i respectively
        float[][] maze = {{1, 1, 1, 1, 1, 1, 1},
                        {1, 0, 0, 0, 0, 0, 1},
                        {1, 0, 0, 0, 0, 0, 1},
                        {1, 0, 0, 2, 0, 0, 1},
                        {1, 0, 0, 0, 0, 0, 1},
                        {1, 0, 0, 0, 0, 0, 1},
                        {1, 1, 1, 0, 1, 1, 1},
                        {1, 1, 1, 0, 1, 1, 1},
                        {1, 1, 1, 0, 1, 1, 1},
                        {1, 0, 0, 0, 0, 0, 1},
                        {1, 0, 0, 0, 0, 0, 1},
                        {1, 0, 0, 0, 0, 0, 1},
                        {1, 0, 0, 0, 0, 0, 1},
                        {1, 0, 0, 0, 0, 0, 1},
                        {1, 1, 1, 1, 1, 1, 1}};
        
        //generate floor and ceiling
        for (int j = 0; j < maze.length; j++) {
            for (int i = 0; i < maze[j].length; i++) {
                createFloor(rootNode, assetManager, space, floorMaterial, "floor", i, j);
                createFloor(rootNode, assetManager, space, floorMaterial, "ceiling", i, j);
            }
        }
        
        float[] returnArray = new float[2];
        
        for (int j = 0; j < maze.length; j++) {
            for (int i = 0; i < maze[j].length; i++) {
                if (maze[j][i] == 0) { //this is the floor, do nothing
                }
                else if (maze[j][i] == 1) { //this is a wall, actually do something
                    createWall(rootNode, assetManager, space, wallMaterial, i, j);
                }
                else if (maze[j][i] == 2) { //this is the starting location, spawn player here
                    //blocks are in multiples of 2 so gotta multiply it by that same amount
                    returnArray[0] = (float)i*2.0f;
                    returnArray[1] = (float)j*2.0f;
                }
            }
        }
        return returnArray;
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
            yLevel = 5.25f;
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
}