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
import java.util.Random;

public class Maze {
    /**
     * A private constructor to inhibit instantiation of this class.
     */
    private Maze() {
    }

    public static float[] createWorld(Node rootNode, AssetManager assetManager, PhysicsSpace space, String type, AmbientLight ambientLight) {
        rootNode.addLight(ambientLight);
        
        Material diamondOre = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        diamondOre.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Ores/diamond.png"));
        
        Material ironOre = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        ironOre.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Ores/iron.png"));
        
        Material oak_plank = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        oak_plank.setTexture("DiffuseMap", assetManager.loadTexture("Textures/oak_plank.png"));
        
        Material cobblestone = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        cobblestone.setTexture("DiffuseMap", assetManager.loadTexture("Textures/cobblestone.png"));
        
        Material endPortal = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        endPortal.setTexture("DiffuseMap", assetManager.loadTexture("Textures/end_portal.png"));
        
        //follows a right-handed coordinate system, go right and down to increase in i and j respectively
        //also keeping maze maps in another file to keep this one cleaner.
        float[][] maze = returnMazeMap(type);

        float[] returnArray = new float[4];
        
        for (int j = 0; j < maze.length; j++) {
            for (int i = 0; i < maze[j].length; i++) {
                if (maze[j][i] == 0) { //this is the floor
                    createFloor(rootNode, assetManager, space, loadTexture(rootNode, assetManager, space, type, ambientLight)[0], "floor", i, j);
                    createFloor(rootNode, assetManager, space, loadTexture(rootNode, assetManager, space, type, ambientLight)[0], "ceiling", i, j);
                }
                else if (maze[j][i] == 1) { //this is a wall
                    createWall(rootNode, assetManager, space, loadTexture(rootNode, assetManager, space, type, ambientLight)[1], i, j);
                }
                else if (maze[j][i] == 2) { //this is a doorway
                    createFloor(rootNode, assetManager, space, loadTexture(rootNode, assetManager, space, type, ambientLight)[0], "floor", i, j);
                    createDoorway(rootNode, assetManager, space, loadTexture(rootNode, assetManager, space, type, ambientLight)[1], i, j, 1);
                }
                else if (maze[j][i] == 3) { //this is a random block
                    createFloor(rootNode, assetManager, space, loadTexture(rootNode, assetManager, space, type, ambientLight)[0], "floor", i, j);
                    createFloor(rootNode, assetManager, space, loadTexture(rootNode, assetManager, space, type, ambientLight)[0], "ceiling", i, j);
                    createBlock(rootNode, assetManager, space, diamondOre, i, j, 0);
                }
                else if (maze[j][i] == 4) { //this is a random block stacked 2 high 
                    createFloor(rootNode, assetManager, space, loadTexture(rootNode, assetManager, space, type, ambientLight)[0], "floor", i, j);
                    createFloor(rootNode, assetManager, space, loadTexture(rootNode, assetManager, space, type, ambientLight)[0], "ceiling", i, j);
                    createBlock(rootNode, assetManager, space, ironOre, i, j, 1);
                }
                else if (maze[j][i] == 5) { //for the stronghold, set oak plank roof 
                    createFloor(rootNode, assetManager, space, loadTexture(rootNode, assetManager, space, type, ambientLight)[0], "floor", i, j);
                    createFloor(rootNode, assetManager, space, loadTexture(rootNode, assetManager, space, type, ambientLight)[0], "oak_ceiling", i, j);
                    createFloor(rootNode, assetManager, space, oak_plank, "oak", i, j);
                }
                else if (maze[j][i] == 6) { //for the stronghold, set cobblestone stuff
                    createWall(rootNode, assetManager, space, cobblestone, i, j);
                    createFloor(rootNode, assetManager, space, loadTexture(rootNode, assetManager, space, type, ambientLight)[0], "oak_ceiling", i, j);
                }
                else if (maze[j][i] == 7) { //for the stronghold, set cobblestone stuff
                    createFloor(rootNode, assetManager, space, loadTexture(rootNode, assetManager, space, type, ambientLight)[0], "floor", i, j);
                    createFloor(rootNode, assetManager, space, loadTexture(rootNode, assetManager, space, type, ambientLight)[0], "oak_ceiling", i, j);
                    createBlock(rootNode, assetManager, space, cobblestone, i, j, 0);
                }
                else if (maze[j][i] == 8) { //this is the starting location, spawn player here
                    //blocks are in multiples of 2 so gotta multiply it by that same amount
                    createFloor(rootNode, assetManager, space, loadTexture(rootNode, assetManager, space, type, ambientLight)[0], "floor", i, j);
                    createFloor(rootNode, assetManager, space, loadTexture(rootNode, assetManager, space, type, ambientLight)[0], "ceiling", i, j);
                    returnArray[0] = (float)i*2.0f;
                    returnArray[1] = (float)j*2.0f;
                }
                else if (maze[j][i] == 9) { //this is the finish location, end game here
                    Material floorFinishMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
                    floorFinishMaterial.setTexture("DiffuseMap", assetManager.loadTexture("Textures/finishBlock.png"));
                    createFloor(rootNode, assetManager, space, floorFinishMaterial, "floor", i, j);
                    createFloor(rootNode, assetManager, space, loadTexture(rootNode, assetManager, space, type, ambientLight)[0], "oak_ceiling", i, j);
                    returnArray[2] = (float)i*2.0f;
                    returnArray[3] = (float)j*2.0f;
                }
                else if (maze[j][i] == 10) { //for the stronghold, set larger walls
                    createBlock(rootNode, assetManager, space, loadTexture(rootNode, assetManager, space, type, ambientLight)[1], i, j, 6);
                } 
                else if (maze[j][i] == 11) { //for the stronghold, set larger walls
                    createDoorway(rootNode, assetManager, space, loadTexture(rootNode, assetManager, space, type, ambientLight)[1], i, j, 5);
                    createFloor(rootNode, assetManager, space, loadTexture(rootNode, assetManager, space, type, ambientLight)[0], "floor", i, j);
                }  
                else if (maze[j][i] == 12) { //for the stronghold, set higher ceiling
                    createFloor(rootNode, assetManager, space, loadTexture(rootNode, assetManager, space, type, ambientLight)[0], "oak_ceiling", i, j);
                    createFloor(rootNode, assetManager, space, loadTexture(rootNode, assetManager, space, type, ambientLight)[0], "floor", i, j);
                }  
                else if (maze[j][i] == 13) { //for the stronghold, set higher ceiling
                    createFloor(rootNode, assetManager, space, loadTexture(rootNode, assetManager, space, type, ambientLight)[0], "oak_ceiling", i, j);
                    createFloor(rootNode, assetManager, space, endPortal, "floor", i, j);
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
        float blockWidth = 0.25f;
        switch (location) {
            case "floor":
                yLevel = -0.25f;
                break;
            case "ceiling":
                yLevel = 6.25f;
                break;
            case "oak":
                yLevel = 5.0f;
                blockWidth = 1.0f;
                break;
            case "oak_ceiling":
                yLevel = 13.25f;
            default:
                break;
        }
        Box floorBox = new Box(1, blockWidth, 1);
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
    
    //method to set individual blocks
        //method to generate walls
    public static void createBlock (Node rootNode, AssetManager assetManager, PhysicsSpace space, Material wallMaterial, 
            int value1, int value2, int value3) {
        int x = value1, z = value2, y = value3;
        Box box = new Box(1, 1, 1);
        for (y = 0; y <= value3; y++) {
            Geometry boxGeometry = new Geometry("Box", box);
            boxGeometry.setMaterial(wallMaterial);
            boxGeometry.setLocalTranslation((x*2f), (1+y*2f) , (z*2f));
            boxGeometry.addControl(new RigidBodyControl(new MeshCollisionShape(box), 0));
            rootNode.attachChild(boxGeometry);
            space.add(boxGeometry);
        }
    }
    
    //method for doorways
    public static void createDoorway (Node rootNode, AssetManager assetManager, PhysicsSpace space, Material wallMaterial, 
            int value1, int value2, int value3) {
        int x = value1, z = value2, y = value3;
        for (int i = 0; i < y; i++) {
            Box box = new Box(1, 1, 1);
            Geometry boxGeometry = new Geometry("Box", box);
            boxGeometry.setMaterial(wallMaterial);
            boxGeometry.setLocalTranslation((x*2f), (5+i*2f) , (z*2f));
            boxGeometry.addControl(new RigidBodyControl(new MeshCollisionShape(box), 0));
            rootNode.attachChild(boxGeometry);
            space.add(boxGeometry);
        }
    }
    
    public static Material[] loadTexture(Node rootNode, AssetManager assetManager, PhysicsSpace space, String type, AmbientLight ambientLight) {
        String floorTexturePath = "";
        String wallTexturePath = "";
        
        if (type.equals("Deepslate")) {
            ambientLight.setColor(ColorRGBA.White.mult(0.5f));
            floorTexturePath = "Textures/stoneFloor.jpg";
            wallTexturePath = "Textures/stoneWall.jpg";
        }
        else if (type.equals("Stronghold")) {
            Random random = new Random();
            int randnum = random.nextInt(9);
            if (randnum == 0 || randnum == 1 || randnum == 2 || randnum == 3 || randnum == 4 || randnum == 5 || randnum == 6) {
                ambientLight.setColor(ColorRGBA.White.mult(0.25f));
                floorTexturePath = "Textures/stone_bricks.png";
                wallTexturePath = "Textures/stone_bricks.png";
            }
            else if (randnum == 7) {
                ambientLight.setColor(ColorRGBA.White.mult(0.25f));
                floorTexturePath = "Textures/cracked_stone_bricks.png";
                wallTexturePath = "Textures/cracked_stone_bricks.png";
            }
            else if (randnum == 8) {
                ambientLight.setColor(ColorRGBA.White.mult(0.25f));
                floorTexturePath = "Textures/mossy_stone_bricks.png";
                wallTexturePath = "Textures/mossy_stone_bricks.png";
            }
        }

        Material floorMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        floorMaterial.setTexture("DiffuseMap", assetManager.loadTexture(floorTexturePath));
        
        Material wallMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        wallMaterial.setTexture("DiffuseMap", assetManager.loadTexture(wallTexturePath));
        
        Material[] returnMats = new Material[2];
        returnMats[0] = floorMaterial;
        returnMats[1] = wallMaterial;
        
        return returnMats;
    }
}