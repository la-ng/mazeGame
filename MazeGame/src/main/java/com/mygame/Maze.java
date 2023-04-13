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

    public static void createWorld(Node rootNode, AssetManager assetManager, PhysicsSpace space) {
        PointLight light = new PointLight();
        light.setColor(ColorRGBA.Yellow);
        light.setRadius(4f);
        
        rootNode.addLight(light);

        Material floorMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        floorMaterial.setTexture("ColorMap", assetManager.loadTexture("Textures/stoneFloor.jpg"));
        
        Material wallMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        wallMaterial.setTexture("ColorMap", assetManager.loadTexture("Textures/stoneWall.jpg"));
        
        //floor
        for (int i = -25; i <= 25; i++) {
            for (int j = -25; j <= 25; j++) {
                Box floorBox = new Box(1, 0.25f, 1);
                Geometry floorGeometry = new Geometry("Floor", floorBox);
                floorGeometry.setMaterial(floorMaterial);
                floorGeometry.setLocalTranslation((i*2), -0.25f, (j*2));
                floorGeometry.addControl(new RigidBodyControl(0));
                rootNode.attachChild(floorGeometry);
                space.add(floorGeometry);
            }
        }
        
        //ceiling
        for (int i = -25; i <= 25; i++) {
            for (int j = -25; j <= 25; j++) {
                Box floorBox = new Box(1, 0.25f, 1);
                Geometry floorGeometry = new Geometry("Floor", floorBox);
                floorGeometry.setMaterial(floorMaterial);
                floorGeometry.setLocalTranslation((i*2), 5.25f, (j*2));
                floorGeometry.addControl(new RigidBodyControl(0));
                rootNode.attachChild(floorGeometry);
                space.add(floorGeometry);
            }
        }
        
        //walls
        for (int j = 0; j <= 2; j++) {
            for (int i = -25; i <= 25; i++){
                Box box = new Box(1, 1, 1);
                Geometry boxGeometry = new Geometry("Box", box);
                boxGeometry.setMaterial(wallMaterial);
                boxGeometry.setLocalTranslation((i*2f), (1+j*2f) , -50f);
                boxGeometry.addControl(new RigidBodyControl(new MeshCollisionShape(box), 0));
                rootNode.attachChild(boxGeometry);
                space.add(boxGeometry);
            }
            for (int i = -25; i <= 25; i++){
                //immovable Box with mesh collision shape
                Box box = new Box(1, 1, 1);
                Geometry boxGeometry = new Geometry("Box", box);
                boxGeometry.setMaterial(wallMaterial);
                boxGeometry.setLocalTranslation((i*2f), (1+j*2f), 50f);
                boxGeometry.addControl(new RigidBodyControl(new MeshCollisionShape(box), 0));
                rootNode.attachChild(boxGeometry);
                space.add(boxGeometry);
            }
            for (int i = -25; i <= 25; i++){
                //immovable Box with mesh collision shape
                Box box = new Box(1, 1, 1);
                Geometry boxGeometry = new Geometry("Box", box);
                boxGeometry.setMaterial(wallMaterial);
                boxGeometry.setLocalTranslation(-50f, (1+j*2f), (i*2));
                boxGeometry.addControl(new RigidBodyControl(new MeshCollisionShape(box), 0));
                rootNode.attachChild(boxGeometry);
                space.add(boxGeometry);
            }
            for (int i = -25; i <= 25; i++){
                //immovable Box with mesh collision shape
                Box box = new Box(1, 1, 1);
                Geometry boxGeometry = new Geometry("Box", box);
                boxGeometry.setMaterial(wallMaterial);
                boxGeometry.setLocalTranslation(50f, (1+j*2f), (i*2));
                boxGeometry.addControl(new RigidBodyControl(new MeshCollisionShape(box), 0));
                rootNode.attachChild(boxGeometry);
                space.add(boxGeometry);
            }
        }
    }
}