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
                Object graphics = event.getGuiGraphics();
                
                int left = screenWidth / 2 + 91;
                int top = screenHeight - 51; // Координаты ровно над шкалой еды
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
                        // 1. Отрисовка мини-иконки надетой брони
                        Object pose = graphics.getClass().getMethod("pose").invoke(graphics);
                        pose.getClass().getMethod("pushPose").invoke(pose);
                        pose.getClass().getMethod("translate", float.class, float.class, float.class).invoke(pose, (float)currentX, (float)top, 0.0f);
                        pose.getClass().getMethod("scale", float.class, float.class, float.class).invoke(pose, 0.7f, 0.7f, 0.7f);
                        graphics.getClass().getMethod("renderFakeItem", isClass, int.class, int.class).invoke(graphics, armorStack, 0, 0);
                        pose.getClass().getMethod("popPose").invoke(pose);

                        // 2. Отображение прочности в формате "Текущая / Максимальная"
                        if ((boolean) armorStack.getClass().getMethod("isDamageableItem").invoke(armorStack)) {
                            int maxDmg = (int) armorStack.getClass().getMethod("getMaxDamage").invoke(armorStack);
                            int currentDurability = maxDmg - (int) armorStack.getClass().getMethod("getDamageValue").invoke(armorStack);
                            
                            pose.getClass().getMethod("pushPose").invoke(pose);
                            // Сдвигаем влево и уменьшаем шрифт для красивого компактного вывода "X / Y"
                            pose.getClass().getMethod("translate", float.class, float.class, float.class).invoke(pose, (float)(currentX - 24), (float)(top + 2), 0.0f);
                            pose.getClass().getMethod("scale", float.class, float.class, float.class).invoke(pose, 0.5f, 0.5f, 0.5f);
                            
                            String textDurability = currentDurability + "/" + maxDmg;
                            int color = ((float) currentDurability / maxDmg < 0.25f) ? 0xFFFF5555 : 0xFFFFFFFF;
                            
                            graphics.getClass().getMethod("drawString", font.getClass(), String.class, float.class, float.class, int.class, boolean.class)
                                    .invoke(graphics, font, textDurability, 0.0f, 0.0f, color, true);
                            pose.getClass().getMethod("popPose").invoke(pose);
                        }
                        currentX -= 34; // Увеличиваем шаг, чтобы длинный текст прочности не налезал друг на друга
                    }
                }
            }
        } catch (Exception ignored) {}
    }
}
