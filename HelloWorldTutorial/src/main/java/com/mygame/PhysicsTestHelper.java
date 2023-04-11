/*
 * Copyright (c) 2009-2021 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.mygame;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.GImpactCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;

/**
 *
 * @author normenhansen
 */
public class PhysicsTestHelper {
    /**
     * A private constructor to inhibit instantiation of this class.
     */
    private PhysicsTestHelper() {
    }

    /**
     * creates a simple physics test world with a floor, an obstacle and some test boxes
     *
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