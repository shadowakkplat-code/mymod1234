package com.example.mymod;

import java.lang.reflect.Method;
import net.neoforged.bus.api.SubscribeEvent;

public class MyHud {
    public static float localSaturation = 20.0f;
    private static int lastFoodLevel = 20;

    @SubscribeEvent
    public void onClientTick(net.neoforged.neoforge.client.event.ClientTickEvent.Post event) {
        try {
            Class<?> mcClass = Class.forName("net.minecraft.client.Minecraft");
            Object mc = mcClass.getMethod("getInstance").invoke(null);
            Object player = mcClass.getField("player").get(mc);
            
            if (player != null) {
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
            }
        } catch (Exception ignored) {}
    }

    @SubscribeEvent
    public void onRenderGui(net.neoforged.neoforge.client.event.RenderGuiLayerEvent.Post event) {
        try {
            Method getLayerMethod = event.getClass().getMethod("getLayer");
            Object layer = getLayerMethod.invoke(event);
            String layerName = layer.toString();
            
            if (layerName.contains("FOOD_LEVEL") || layerName.contains("food") || layerName.contains("HOTBAR") || layerName.contains("hotbar")) {
                Class<?> mcClass = Class.forName("net.minecraft.client.Minecraft");
                Object mc = mcClass.getMethod("getInstance").invoke(null);
                Object player = mcClass.getField("player").get(mc);
                
                if (player != null) {
                    Method getWindowMethod = event.getClass().getMethod("getWindow");
                    Object window = getWindowMethod.invoke(event);
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
