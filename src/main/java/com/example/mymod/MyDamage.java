package com.example.mymod;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

public class MyDamage {
    
    @SubscribeEvent
    public void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Идеально выпрямляем горизонт (Roll) при получении урона
        if (mc.player.hurtTime > 0) {
            event.setRoll(0.0f);
        }
    }
}
