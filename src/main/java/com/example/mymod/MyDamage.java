package com.example.mymod;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

public class MyDamage {
    
    @SubscribeEvent
    public void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Если игрока бьют, принудительно гасим наклон камеры
        if (mc.player.hurtTime > 0) {
            event.setRoll(0.0f);
            event.setPitch(event.getPitch());
            event.setYaw(event.getYaw());
        }
    }

    @SubscribeEvent
    public void onRenderHandShake(ViewportEvent.RenderHandEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.hurtTime > 0) {
            // Визуально обнуляем время урона на кадр рендера, чтобы руки не дергались
            mc.player.hurtTime = 0;
        }
    }
}
