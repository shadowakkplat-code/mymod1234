package com.example.mymod;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

public class MyDamage {
    
    @SubscribeEvent
    public void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Если у игрока активна анимация получения урона (hurtTime > 0)
        if (mc.player.hurtTime > 0) {
            // Принудительно обнуляем угол наклона (Roll), полностью отключая тряску экрана
            event.setRoll(0.0f);
        }
    }
}
