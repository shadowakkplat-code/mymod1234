package com.example.mymod;

import java.lang.reflect.Method;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod("mymod")
public class MyMod {
    public static float localSaturation = 20.0f;
    private static int lastFoodLevel = 20;
    private static boolean wasClicking = false;

    public MyMod() {
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new GuiHandler());
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
                
                Object foodData = player.getClass().getMethod("getFoodData").invoke(player);
                int currentFood = (int) foodData.getClass().getMethod("getFoodLevel").invoke(foodData);
                float satLevel = (float) foodData.getClass().getMethod("getSaturationLevel").invoke(foodData);
                
                if (satLevel > 0) {
                    localSaturation = satLevel;
                } else if (tickCount % 20 == 0) {
                    if (currentFood < lastFoodLevel) localSaturation = 0.0f;
                    if (localSaturation > 0) localSaturation -= 0.01f;
                }
                lastFoodLevel = currentFood;

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

    @SubscribeEvent
    public void onRenderFire(net.neoforged.neoforge.client.event.RenderBlockScreenEffectEvent event) {
        try {
            Object type = event.getOverlayType();
            if (type.toString().contains("FIRE") || type.toString().contains("fire")) {
                event.getPoseStack().translate(0.0D, -0.43D, 0.0D);
            }
        } catch (Exception ignored) {}
    }
}

class GuiHandler {
    @SubscribeEvent
    public void onRenderGui(net.neoforged.neoforge.client.event.RenderGuiLayerEvent.Post event) {
        try {
            Object layer = event.getLayer();
            String layerName = layer.toString();
            
            if (layerName.contains("FOOD_LEVEL") || layerName.contains("food") || layerName.contains("HOTBAR") || layerName.contains("hotbar")) {
                Class<?> mcClass = Class.forName("net.minecraft.client.Minecraft");
                Object mc = mcClass.getMethod("getInstance").invoke(null);
                Object player = mcClass.getField("player").get(mc);
                
                if (player != null) {
                    // Исправлено: получаем окно и размеры экрана напрямую через Minecraft
                    Object window = mcClass.getMethod("getWindow").invoke(mc);
                    int screenWidth = (int) window.getClass().getMethod("getGuiScaledWidth").invoke(window);
                    int screenHeight = (int) window.getClass().getMethod("getGuiScaledHeight").invoke(window);
                    
                    Method getGuiGraphicsMethod = event.getClass().getMethod("getGuiGraphics");
                    Object graphics = getGuiGraphicsMethod.invoke(event);
                    
                    int left = screenWidth / 2 + 91;
                    int top = screenHeight - 39;
                    
                    Class<?> rlClass = Class.forName("net.minecraft.resources.ResourceLocation");
                    Object icons = rlClass.getMethod("withDefaultNamespace", String.class).invoke(null, "textures/gui/icons.png");
                    
                    Class<?> rsClass = Class.forName("com.mojang.blaze3d.systems.RenderSystem");
                    rsClass.getMethod("setShaderTexture", int.class, rlClass).invoke(null, 0, icons);
                    
                    Method blit = graphics.getClass().getMethod("blit", rlClass, int.class, int.class, int.class, int.class, int.class, int.class);
                    for (int i = 0; i < 10; ++i) {
                        if (MyMod.localSaturation > i * 2) {
                            int bx = left - i * 8 - 9;
                            if (MyMod.localSaturation - (i * 2) >= 2) blit.invoke(graphics, icons, bx, top, 16, 27, 9, 9);
                            else blit.invoke(graphics, icons, bx, top, 25, 27, 9, 9);
                        }
                    }

                    int armorTop = top - 12;
                    int currentX = left - 9;
                    Class<?> esClass = Class.forName("net.minecraft.world.entity.EquipmentSlot");
                    Object[] slots = {
                        esClass.getField("FEET").get(null),
                        esClass.getField("LEGS").get(null),
                        esClass.getField("CHEST").get(null),
                        esClass.getField("HEAD").get(null)
                    };
                    
                    Object font = mcClass.getField("font").get(mc);
                    Class<?> isClass = Class.forName("net.minecraft.world.item.ItemStack");

                    for (Object slot : slots) {
                        Object armorStack = player.getClass().getMethod("getItemBySlot", esClass).invoke(player, slot);
                        boolean isEmpty = (boolean) armorStack.getClass().getMethod("isEmpty").invoke(armorStack);
                        
                        if (!isEmpty) {
                            Object pose = graphics.getClass().getMethod("pose").invoke(graphics);
                            pose.getClass().getMethod("pushPose").invoke(pose);
                            pose.getClass().getMethod("translate", float.class, float.class, float.class).invoke(pose, (float)currentX, (float)(armorTop - 1), 0.0f);
                            pose.getClass().getMethod("scale", float.class, float.class, float.class).invoke(pose, 0.7f, 0.7f, 0.7f);
                            
                            graphics.getClass().getMethod("renderFakeItem", isClass, int.class, int.class).invoke(graphics, armorStack, 0, 0);
                            pose.getClass().getMethod("popPose").invoke(pose);

                            boolean isDamageable = (boolean) armorStack.getClass().getMethod("isDamageableItem").invoke(armorStack);
                            if (isDamageable) {
                                int maxDmg = (int) armorStack.getClass().getMethod("getMaxDamage").invoke(armorStack);
                                int dmgVal = (int) armorStack.getClass().getMethod("getDamageValue").invoke(armorStack);
                                int currentDurability = maxDmg - dmgVal;

                                pose.getClass().getMethod("pushPose").invoke(pose);
                                pose.getClass().getMethod("translate", float.class, float.class, float.class).invoke(pose, (float)(currentX - 16), (float)armorTop, 0.0f);
                                pose.getClass().getMethod("scale", float.class, float.class, float.class).invoke(pose, 0.6f, 0.6f, 0.6f);
                                
                                int color = 0xFFFFFFFF;
                                float durRatio = (float) currentDurability / maxDmg;
                                if (durRatio < 0.25f) color = 0xFFFF5555;
                                
                                graphics.getClass().getMethod("drawString", font.getClass(), String.class, float.class, float.class, int.class, boolean.class)
                                        .invoke(graphics, font, String.valueOf(currentDurability), 0.0f, 2.0f, color, true);
                                pose.getClass().getMethod("popPose").invoke(pose);
                            }
                            currentX -= 26;
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
    }
}
