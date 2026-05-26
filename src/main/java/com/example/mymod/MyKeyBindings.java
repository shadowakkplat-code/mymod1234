package com.example.mymod;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public class MyKeyBindings {
    public static final KeyMapping OPEN_RIGHT_CONFIG = new KeyMapping(
        "key.mymod.right_config",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_K, 
        "key.categories.mymod"
    );

    public static final KeyMapping OPEN_LEFT_CONFIG = new KeyMapping(
        "key.mymod.left_config",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_I, 
        "key.categories.mymod"
    );

    public static final KeyMapping OPEN_PARTICLE_CONFIG = new KeyMapping(
        "key.mymod.particle_config",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_J, 
        "key.categories.mymod"
    );

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(OPEN_RIGHT_CONFIG);
        event.register(OPEN_LEFT_CONFIG);
        event.register(OPEN_PARTICLE_CONFIG);
    }
}
