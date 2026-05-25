package com.example.mymod;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

public class MyDamage {
    
    @SubscribeEvent
    public void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Если игрок получает урон, принудительно убираем наклон камеры (Roll)
        if (mc.player.hurtTime > 0) {
            event.setRoll(0.0f);
        }
    }

    @SubscribeEvent
    public void onScreenShake(net.neoforged.neoforge.client.event.ViewportEvent.RenderHandEvent event) {
        Minecraft mc = Minecraft.getInstance();
        // Дополнительное гашение тряски рук при ударе
        if (mc.player != null && mc.player.hurtTime > 0) {
             event.setCanceled(true); 
        }
    }
}
