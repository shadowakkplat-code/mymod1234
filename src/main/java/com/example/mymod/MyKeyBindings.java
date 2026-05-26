package com.example.mymod;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public class MyKeyBindings {
    // Создаем официальные объекты клавиш, которые увидит Minecraft
    public static final KeyMapping OPEN_RIGHT_CONFIG = new KeyMapping(
        "key.mymod.right_config", // Название в файле локализации
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_K,          // Кнопка по умолчанию K
        "key.categories.mymod"    // Название категории в меню настроек
    );

    public static final KeyMapping OPEN_LEFT_CONFIG = new KeyMapping(
        "key.mymod.left_config",  // Название в файле локализации
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_J,          // Кнопка по умолчанию J
        "key.categories.mymod"    // Название категории в меню настроек
    );

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        // Регистрируем клавиши в движке NeoForge
        event.register(OPEN_RIGHT_CONFIG);
        event.register(OPEN_LEFT_CONFIG);
    }
}
