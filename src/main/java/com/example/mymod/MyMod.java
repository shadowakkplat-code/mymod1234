package com.example.mymod;

import java.lang.reflect.Method;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod("mymod")
public class MyMod {
    public static float localSaturation = 0.0f;
    private static int lastFoodLevel = 20;

    public MyMod() {
        // Динамическая регистрация подмодулей в обход проверок компилятора
        NeoForge.EVENT_BUS.register(this);
    }

    // --- 1. ТРЕКЕР СЫТОСТИ ---
    @SubscribeEvent
    public void onTick(Object event) {
        if (!event.getClass().getName().contains("ClientTickEvent$Post")) return;
        try {
            Class<?> mcClass = Class.forName("net.minecraft.client.Minecraft");
            Object mc = mcClass.getMethod("getInstance").invoke(null);
            Object player = mcClass.getField("player").get(mc);
            if (player != null) {
                int tickCount = player.getClass().getField("tickCount").getInt(player);
                if (tickCount % 20 == 0) {
                    Object foodData = player.getClass().getMethod("getFoodData").invoke(player);
                    int currentFood = (int) foodData.getClass().getMethod("getFoodLevel").invoke(foodData);
                    if (currentFood < lastFoodLevel) localSaturation = 0.0f;
                    lastFoodLevel = currentFood;
                    if (localSaturation > 0) localSaturation -= 0.005f;
                    else localSaturation = 0.0f;
                }
            }
        } catch (Exception ignored) {}
    }

    // --- 2. УМЕНЬШЕНИЕ ОГНЯ НА ЭКРАНЕ НА 75% ---
    @SubscribeEvent
    public void onRenderFire(Object event) {
        if (!event.getClass().getName().contains("RenderBlockScreenEffectEvent")) return;
        try {
            Object type = event.getClass().getMethod("getOverlayType").invoke(event);
            if (type.toString().equals("FIRE")) {
                Object poseStack = event.getClass().getMethod("getPoseStack").invoke(event);
                poseStack.getClass().getMethod("translate", double.class, double.class, double.class)
                         .invoke(poseStack, 0.0D, -0.35D, 0.0D);
            }
        } catch (Exception ignored) {}
    }

    // --- 3. ЛЕПЕСТКИ САКУРЫ ПРИ PvP УДАРЕ ---
    @SubscribeEvent
    public void onMouse(Object event) {
        if (!event.getClass().getName().contains("InputEvent$MouseButtonPressed")) return;
        try {
            int button = (int) event.getClass().getMethod("getButton").invoke(event);
            if (button == 0) {
                Class<?> mcClass = Class.forName("net.minecraft.client.Minecraft");
                Object mc = mcClass.getMethod("getInstance").invoke(null);
                Object hitResult = mcClass.getField("hitResult").get(mc);
                if (hitResult != null && hitResult.getClass().getMethod("getType").invoke(hitResult).toString().equals("ENTITY")) {
                    Object target = hitResult.getClass().getMethod("getEntity").invoke(hitResult);
                    Object level = mcClass.getField("level").get(mc);
                    double x = (double) target.getClass().getMethod("getX").invoke(target);
                    double y = (double) target.getClass().getMethod("getY").invoke(target);
                    double z = (double) target.getClass().getMethod("getZ").invoke(target);
                    float height = (float) target.getClass().getMethod("getBbHeight").invoke(target);
                    Class<?> ptClass = Class.forName("net.minecraft.core.particles.ParticleTypes");
                    Object cherry = ptClass.getField("CHERRY_LEAVES").get(null);
                    Method addP = level.getClass().getMethod("addParticle", Object.forName("net.minecraft.core.particles.ParticleOptions"), double.class, double.class, double.class, double.class, double.class, double.class);
                    java.util.Random r = new java.util.Random();
                    for (int i = 0; i < 15; i++) {
                        addP.invoke(level, cherry, x, y + (height / 2.0), z, (r.nextDouble()-0.5)*0.2, r.nextDouble()*0.2, (r.nextDouble()-0.5)*0.2);
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    // --- 4. ХУД БРОНИ И СЫТОСТИ НАД ЕДОЙ ---
    @SubscribeEvent
    public void onRenderGui(Object event) {
        if (!event.getClass().getName().contains("RenderGuiOverlayEvent$Post")) return;
        try {
            Object overlay = event.getClass().getMethod("getOverlay").invoke(event);
            Object id = overlay.getClass().getMethod("id").invoke(overlay);
            if (id.toString().contains("food_level") || id.toString().contains("FOOD_LEVEL")) {
                Class<?> mcClass = Class.forName("net.minecraft.client.Minecraft");
                Object mc = mcClass.getMethod("getInstance").invoke(null);
                Object player = mcClass.getField("player").get(mc);
                Object window = event.getClass().getMethod("getWindow").invoke(event);
                int screenWidth = (int) window.getClass().getMethod("getGuiScaledWidth").invoke(window);
                int screenHeight = (int) window.getClass().getMethod("getGuiScaledHeight").invoke(window);
                Object graphics = event.getClass().getMethod("getGuiGraphics").invoke(event);
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

