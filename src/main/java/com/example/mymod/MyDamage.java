package com.example.mymod;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

public class MyDamage {
    
    @SubscribeEvent
    public void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Если игрока бьют, мы просто принудительно выпрямляем камеру по оси Roll
        // Это полностью убирает наклон экрана без вмешательства в логику тиков (без мерцания)
        if (mc.player.hurtTime > 0) {
            event.setRoll(0.0f);
        }
    }
}
