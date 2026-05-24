package com.example.mymod;

import java.lang.reflect.Method;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod("mymod")
public class MyMod {
    public static float localSaturation = 0.0f;
    private static int lastFoodLevel = 20;
    private static boolean wasClicking = false;

    public MyMod() {
        // Регистрируем только один безопасный обработчик в шине событий NeoForge
        NeoForge.EVENT_BUS.register(this);
    }

    // Единственное безопасное событие, которое NeoForge примет без проверки типов
    @SubscribeEvent
    public void onClientTick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event) {
        try {
            Class<?> mcClass = Class.forName("net.minecraft.client.Minecraft");
            Object mc = mcClass.getMethod("getInstance").invoke(null);
            Object player = mcClass.getField("player").get(mc);
            Object level = mcClass.getField("level").get(mc);
            
            if (player != null && level != null) {
                int tickCount = player.getClass().getField("tickCount").getInt(player);
                
                // 1. ОБРАБОТКА СЫТОСТИ (РАЗ В СЕКУНДУ)
                if (tickCount % 20 == 0) {
                    Object foodData = player.getClass().getMethod("getFoodData").invoke(player);
                    int currentFood = (int) foodData.getClass().getMethod("getFoodLevel").invoke(foodData);
                    if (currentFood < lastFoodLevel) localSaturation = 0.0f;
                    lastFoodLevel = currentFood;
                    if (localSaturation > 0) localSaturation -= 0.005f;
                    else localSaturation = 0.0f;
                }

                // 2. ОТСЛЕЖИВАНИЕ НАЖАТИЯ МЫШКИ ДЛЯ САКУРЫ
                Object options = mcClass.getField("options").get(mc);
                Object keyAttack = options.getClass().getField("keyAttack").get(options);
                boolean isDown = (boolean) keyAttack.getClass().getMethod("isDown").invoke(keyAttack);
                
                if (isDown && !wasClicking) {
                    Object hitResult = mcClass.getField("hitResult").get(mc);
                    if (hitResult != null && hitResult.getClass().getMethod("getType").invoke(hitResult).toString().equals("ENTITY")) {
                        Object target = hitResult.getClass().getMethod("getEntity").invoke(hitResult);
                        double x = (double) target.getClass().getMethod("getX").invoke(target);
                        double y = (double) target.getClass().getMethod("getY").invoke(target);
                        double z = (double) target.getClass().getMethod("getZ").invoke(target);
                        float height = (float) target.getClass().getMethod("getBbHeight").invoke(target);
                        
                        Class<?> ptClass = Class.forName("net.minecraft.core.particles.ParticleTypes");
                        Object cherry = ptClass.getField("CHERRY_LEAVES").get(null);
                        
                        Method addP = level.getClass().getMethod("addParticle", Class.forName("net.minecraft.core.particles.ParticleOptions"), double.class, double.class, double.class, double.class, double.class, double.class);
                        java.util.Random r = new java.util.Random();
                        for (int i = 0; i < 15; i++) {
                            addP.invoke(level, cherry, x, y + (height / 2.0), z, (r.nextDouble()-0.5)*0.2, r.nextDouble()*0.2, (r.nextDouble()-0.5)*0.2);
                        }
                    }
                }
                wasClicking = isDown;
            }
        } catch (Exception ignored) {}
    }
}
