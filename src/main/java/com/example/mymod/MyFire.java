package com.example.mymod;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderBlockScreenEffectEvent;

public class MyFire {
    @SubscribeEvent
    public void onRenderFire(RenderBlockScreenEffectEvent event) {
        try {
            Object type = event.getOverlayType();
            if (type != null && (type.toString().contains("FIRE") || type.toString().contains("fire"))) {
                event.getPoseStack().translate(0.0D, -0.40D, 0.0D); 
            }
        } catch (Exception ignored) {}
    }
}
