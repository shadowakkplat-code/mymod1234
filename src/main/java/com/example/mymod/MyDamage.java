package com.example.mymod;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;

public class MyDamage {
    
    @SubscribeEvent
    public void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Полностью гасим наклон камеры при получении урона
        if (mc.player.hurtTime > 0) {
            event.setRoll(0.0f);
        }
    }

    @SubscribeEvent
    public void onRenderHandShake(RenderHandEvent event) {
        Minecraft mc = Minecraft.getInstance();
        // Сбрасываем счетчик тряски рук во время рендеринга предметов
        if (mc.player != null && mc.player.hurtTime > 0) {
            mc.player.hurtTime = 0;
        }
    }
}
