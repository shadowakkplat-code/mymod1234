package com.example.mymod;

import java.lang.reflect.Method;
import net.neoforged.bus.api.SubscribeEvent;

public class MyArmor {
    @SubscribeEvent
    public void onRenderArmor(net.neoforged.neoforge.client.event.RenderGuiLayerEvent.Post event) {
        try {
            Method getLayerMethod = event.getClass().getMethod("getLayer");
            Object layer = getLayerMethod.invoke(event);
            if (!layer.toString().contains("HOTBAR")) return;

            Class<?> mcClass = Class.forName("net.minecraft.client.Minecraft");
            Object mc = mcClass.getMethod("getInstance").invoke(null);
            Object player = mcClass.getField("player").get(mc);
            
            if (player != null) {
                Object window = mcClass.getMethod("getWindow").invoke(mc);
                int screenWidth = (int) window.getClass().getMethod("getGuiScaledWidth").invoke(window);
                int screenHeight = (int) window.getClass().getMethod("getGuiScaledHeight").invoke(window);
                Object graphics = event.getClass().getMethod("getGuiGraphics").invoke(event);
                
                int left = screenWidth / 2 + 91;
                int top = screenHeight - 51;
                Class<?> esClass = Class.forName("net.minecraft.world.entity.EquipmentSlot");
                Object[] slots = {
                    esClass.getField("FEET").get(null), esClass.getField("LEGS").get(null),
                    esClass.getField("CHEST").get(null), esClass.getField("HEAD").get(null)
                };
                
                Object font = mcClass.getField("font").get(mc);
                Class<?> isClass = Class.forName("net.minecraft.world.item.ItemStack");
                int currentX = left - 9;

                for (Object slot : slots) {
                    Object armorStack = player.getClass().getMethod("getItemBySlot", esClass).invoke(player, slot);
                    boolean isEmpty = (boolean) armorStack.getClass().getMethod("isEmpty").invoke(armorStack);
                    
                    if (!isEmpty) {
                        Object pose = graphics.getClass().getMethod("pose").invoke(graphics);
                        pose.getClass().getMethod("pushPose").invoke(pose);
                        pose.getClass().getMethod("translate", float.class, float.class, float.class).invoke(pose, (float)currentX, (float)top, 0.0f);
                        pose.getClass().getMethod("scale", float.class, float.class, float.class).invoke(pose, 0.7f, 0.7f, 0.7f);
                        graphics.getClass().getMethod("renderFakeItem", isClass, int.class, int.class).invoke(graphics, armorStack, 0, 0);
                        pose.getClass().getMethod("popPose").invoke(pose);

                        if ((boolean) armorStack.getClass().getMethod("isDamageableItem").invoke(armorStack)) {
                            int maxDmg = (int) armorStack.getClass().getMethod("getMaxDamage").invoke(armorStack);
                            int currentDurability = maxDmg - (int) armorStack.getClass().getMethod("getDamageValue").invoke(armorStack);
                            
                            pose.getClass().getMethod("pushPose").invoke(pose);
                            pose.getClass().getMethod("translate", float.class, float.class, float.class).invoke(pose, (float)(currentX - 16), (float)(top + 1), 0.0f);
                            pose.getClass().getMethod("scale", float.class, float.class, float.class).invoke(pose, 0.6f, 0.6f, 0.6f);
                            int color = ((float) currentDurability / maxDmg < 0.25f) ? 0xFFFF5555 : 0xFFFFFFFF;
                            graphics.getClass().getMethod("drawString", font.getClass(), String.class, float.class, float.class, int.class, boolean.class)
                                    .invoke(graphics, font, String.valueOf(currentDurability), 0.0f, 2.0f, color, true);
                            pose.getClass().getMethod("popPose").invoke(pose);
                        }
                        currentX -= 26;
                    }
                }
            }
        } catch (Exception ignored) {}
    }
}
