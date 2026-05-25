package com.example.mymod;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

public class MyDamage {
    
    @SubscribeEvent
    public void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Вместо изменения hurtTime мы просто принудительно обнуляем углы поворота камеры.
        // Это полностью убирает эффект тряски на экране, при этом не создавая мерцаний текстур.
        if (mc.player.hurtTime > 0) {
            event.setRoll(0.0f);
            event.setPitch(event.getPitch());
            event.setYaw(event.getYaw());
        }
    }
}
