package com.example.mymod;

import java.lang.reflect.Method;
import net.neoforged.bus.api.SubscribeEvent;

public class MyFood {
    public static float localSaturation = 20.0f;
    private static int lastFoodLevel = 20;

    @SubscribeEvent
    public void onRenderFood(net.neoforged.neoforge.client.event.RenderGuiLayerEvent.Post event) {
        try {
            Method getLayerMethod = event.getClass().getMethod("getLayer");
            Object layer = getLayerMethod.invoke(event);
            if (!layer.toString().contains("FOOD_LEVEL")) return;

            Class<?> mcClass = Class.forName("net.minecraft.client.Minecraft");
            Object mc = mcClass.getMethod("getInstance").invoke(null);
            Object player = mcClass.getField("player").get(mc);
            
            if (player != null) {
                int tickCount = player.getClass().getField("tickCount").getInt(player);
                Object foodData = player.getClass().getMethod("getFoodData").invoke(player);
                int currentFood = (int) foodData.getClass().getMethod("getFoodLevel").invoke(foodData);
                float satLevel = (float) foodData.getClass().getMethod("getSaturationLevel").invoke(foodData);
                
                if (satLevel > 0) localSaturation = satLevel;
                else if (tickCount % 20 == 0) {
                    if (currentFood < lastFoodLevel) localSaturation = 0.0f;
                    if (localSaturation > 0) localSaturation -= 0.01f;
                }
                lastFoodLevel = currentFood;

                Object window = mcClass.getMethod("getWindow").invoke(mc);
                int screenWidth = (int) window.getClass().getMethod("getGuiScaledWidth").invoke(window);
                int screenHeight = (int) window.getClass().getMethod("getGuiScaledHeight").invoke(window);
                Object graphics = event.getGuiGraphics();
                
                int left = screenWidth / 2 + 91;
                int top = screenHeight - 39;
                
                Class<?> rlClass = Class.forName("net.minecraft.resources.ResourceLocation");
                Object icons = rlClass.getMethod("withDefaultNamespace", String.class).invoke(null, "textures/gui/icons.png");
                Class<?> rsClass = Class.forName("com.mojang.blaze3d.systems.RenderSystem");
                rsClass.getMethod("setShaderTexture", int.class, rlClass).invoke(null, 0, icons);
                
                Method blit = graphics.getClass().getMethod("blit", rlClass, int.class, int.class, int.class, int.class, int.class, int.class);
                for (int i = 0; i < 10; ++i) {
                    if (localSaturation > i * 2) {
                        int bx = left - i * 8 - 9;
                        if (localSaturation - (i * 2) >= 2) blit.invoke(graphics, icons, bx, top, 16, 27, 9, 9);
                        else blit.invoke(graphics, icons, bx, top, 25, 27, 9, 9);
                    }
                }
            }
        } catch (Exception ignored) {}
    }
}
