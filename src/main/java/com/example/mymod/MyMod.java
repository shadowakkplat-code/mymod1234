package com.example.mymod;

import java.lang.reflect.Method;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod("mymod")
public class MyMod {
    private static boolean wasClicking = false;
    
    // Динамические настройки положения меча (покупатель сможет менять их в игре!)
    public static float swordY = 0.10f;
    public static float swordZ = -0.45f;

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
                int tickCount = player.getClass().getField("tickCount").getInt(player);
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

    // Рендеринг меча с учётом кастомных настроек из меню
    @SubscribeEvent
    public void onRenderHand(net.neoforged.neoforge.client.event.RenderHandEvent event) {
        try {
            Object itemStack = event.getItemStack();
            String itemName = itemStack.getClass().getMethod("getItem").invoke(itemStack).toString();
            
            if (itemName.contains("sword") || itemName.contains("axe") || itemName.contains("pickaxe") ||
                itemName.contains("Sword") || itemName.contains("Axe") || itemName.contains("Pickaxe")) {
                
                com.mojang.blaze3d.vertex.PoseStack poseStack = event.getPoseStack();
                poseStack.scale(0.55f, 0.55f, 0.55f);
                
                // Применяем переменные swordY и swordZ, которые меняются кнопками!
                poseStack.translate(0.12D, (double)swordY, (double)swordZ); 
            }
        } catch (Exception ignored) {}
    }

    // Хак: Создаем и добавляем кнопку настроек в левый верхний угол при нажатии на ESC
    @SubscribeEvent
    public void onScreenInit(net.neoforged.neoforge.client.event.ScreenEvent.Init.Post event) {
        try {
            Object screen = event.getScreen();
            String screenName = screen.getClass().getName();
            
            // Если открыто ванильное меню паузы (PauseScreen / ESC)
            if (screenName.contains("PauseScreen") || screenName.contains("pause")) {
                Class<?> buttonClass = Class.forName("net.minecraft.client.gui.components.Button");
                Class<?> componentClass = Class.forName("net.minecraft.network.chat.Component");
                Object buttonText = componentClass.getMethod("literal", String.class).invoke(null, "⚔ PvP Mod Config");
                
                // Создаем кнопку в верхнем левом углу: X=10, Y=10, Ширина=110, Высота=20
                Object pvpButton = buttonClass.getConstructor(int.class, int.class, int.class, int.class, componentClass, buttonClass.getDeclaredClasses()[0])
                    .newInstance(10, 10, 110, 20, buttonText, (ButtonAction) -> {
                        try {
                            // При клике открываем наше кастомное меню калибровки
                            Class<?> mcClass = Class.forName("net.minecraft.client.Minecraft");
                            Object mc = mcClass.getMethod("getInstance").invoke(null);
                            mcClass.getMethod("setScreen", Class.forName("net.minecraft.client.gui.screens.Screen")).invoke(mc, new ConfigScreen());
                        } catch (Exception ignored) {}
                    });
                
                // Добавляем созданную кнопку на экран меню паузы
                event.addListener(pvpButton);
            }
        } catch (Exception ignored) {}
    }
}
