package com.example.mymod;

import java.lang.reflect.Method;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod("mymod")
public class MyMod {
    private static boolean wasClicking = false;

    public MyMod() {
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new MyFire());
        NeoForge.EVENT_BUS.register(new MyArmor());
    }

    @SubscribeEvent
    public void onClientTick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event) {
        try {
            Class<?> mcClass = Class.forName("net.minecraft.client.Minecraft");
            Object mc = mcClass.getMethod("getInstance").invoke(null);
            Object player = mcClass.getField("player").get(mc);
            Object level = mcClass.getField("level").get(mc);
            
            if (player != null && level != null) {
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
                            double offsetX = (r.nextDouble() - 0.5) * 1.2;
                            double offsetZ = (r.nextDouble() - 0.5) * 1.2;
                            double offsetY = r.nextDouble() * height;
                            
                            double speedX = (r.nextDouble() - 0.5) * 0.25;
                            double speedY = r.nextDouble() * 0.15;
                            double speedZ = (r.nextDouble() - 0.5) * 0.25;
                            
                            addP.invoke(level, cherry, x + offsetX, y + offsetY, z + offsetZ, speedX, speedY, speedZ);
                        }
                    }
                }
                wasClicking = isDown;
            }
        } catch (Exception ignored) {}
    }

    // Идеальное PvP положение меча: выше на 10% и вперед на 45%
    @SubscribeEvent
    public void onRenderHand(net.neoforged.neoforge.client.event.RenderHandEvent event) {
        try {
            Object itemStack = event.getItemStack();
            String itemName = itemStack.getClass().getMethod("getItem").invoke(itemStack).toString();
            
            if (itemName.contains("sword") || itemName.contains("axe") || itemName.contains("pickaxe") ||
                itemName.contains("Sword") || itemName.contains("Axe") || itemName.contains("Pickaxe")) {
                
                com.mojang.blaze3d.vertex.PoseStack poseStack = event.getPoseStack();
                poseStack.scale(0.55f, 0.55f, 0.55f); // Сохраняем аккуратный маленький размер
                
                // Сдвигаем по оси Y вверх (на +0.10) и по оси Z вперед (на -0.45)
                poseStack.translate(0.12D, 0.10D, -0.45D); 
            }
        } catch (Exception ignored) {}
    }
}
