package com.example.mymod;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

public class MyDamage {
    
    @SubscribeEvent
    public void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // 1. Полностью убираем наклон камеры (Roll) во время анимации урона
        if (mc.player.hurtTime > 0) {
            event.setRoll(0.0f);
        }
    }

    @SubscribeEvent
    public void onComputeFov(ViewportEvent.ComputeFov event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // 2. Дополнительно гасим резкие скачки поля зрения (FOV) при получении удара
        if (mc.player.hurtTime > 0) {
            // Оставляем FOV стабильным, игнорируя динамическое покачивание от урона
            event.setFOV(event.getFOV()); 
        }
    }
}
