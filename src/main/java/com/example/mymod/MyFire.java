package com.example.mymod;

import net.neoforged.bus.api.SubscribeEvent;

public class MyFire {
    @SubscribeEvent
    public void onRenderFire(net.neoforged.neoforge.client.event.RenderBlockScreenEffectEvent event) {
        try {
            Object type = event.getOverlayType();
            if (type.toString().contains("FIRE") || type.toString().contains("fire")) {
                event.getPoseStack().translate(0.0D, -0.40D, 0.0D); // Ровно 84%
            }
        } catch (Exception ignored) {}
    }
}
