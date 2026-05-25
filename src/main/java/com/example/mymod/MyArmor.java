package com.example.mymod;

import java.lang.reflect.Method;
import net.neoforged.bus.api.SubscribeEvent;

public class MyArmor {
    @SubscribeEvent
    public void onRenderArmor(net.neoforged.neoforge.client.event.RenderGuiLayerEvent.Post event) {
        try {
            Method getLayerMethod = event.getClass().getMethod("getLayer");
            Object layer = getLayerMethod.invoke(event);
            String name = layer.toString();
            
            // Привязываемся строго к финальному слою хотбара, когда экран уже готов к рисованию
            if (name.contains("HOTBAR") || name.contains("hotbar")) {
                Class<?> mcClass = Class.forName("net.minecraft.client.Minecraft");
                Object mc = mcClass.getMethod("getInstance").invoke(null);
                Object player = mcClass.getField("player").get(mc);
                
                if (player != null) {
                    Object window = mcClass.getMethod("getWindow").invoke(mc);
                    int screenWidth = (int) window.getClass().getMethod("getGuiScaledWidth").invoke(window);
                    int screenHeight = (int) window.getClass().getMethod("getGuiScaledHeight").invoke(window);
                    Object graphics = event.getGuiGraphics();
                    
                    // Позиция: ровно над вашими окорочками еды (правая часть экрана)
                    int left = screenWidth / 2 + 91;
                    int top = screenHeight - 53;
                    
                    Class<?> esClass = Class.forName("net.minecraft.world.entity.EquipmentSlot");
                    Object[] slots = {
                        esClass.getField("FEET").get(null),
                        esClass.getField("LEGS").get(null),
                        esClass.getField("CHEST").get(null),
                        esClass.getField("HEAD").get(null)
                    };
                    
                    Object font = mcClass.getField("font").get(mc);
                    Class<?> isClass = Class.forName("net.minecraft.world.item.ItemStack");
                    Class<?> itemRenderTypeClass = Class.forName("net.minecraft.world.item.ItemDisplayContext");
                    Object guiContext = itemRenderTypeClass.getField("GUI").get(null);
                    
                    int currentX = left - 9;

                    for (Object slot : slots) {
                        Object armorStack = player.getClass().getMethod("getItemBySlot", esClass).invoke(player, slot);
                        boolean isEmpty = (boolean) armorStack.getClass().getMethod("isEmpty").invoke(armorStack);
                        
                        if (!isEmpty) {
                            // 1. Официальный метод отрисовки 3D-иконки брони для 1.21.4
                            Object pose = graphics.getClass().getMethod("pose").invoke(graphics);
                            pose.getClass().getMethod("pushPose").invoke(pose);
                            pose.getClass().getMethod("translate", float.class, float.class, float.class).invoke(pose, (float)currentX, (float)top, 0.0f);
                            pose.getClass().getMethod("scale", float.class, float.class, float.class).invoke(pose, 0.75f, 0.75f, 0.75f);
                            
                            // Вызываем актуальный метод renderItem, который принудительно выведет вещь на экран
                            graphics.getClass().getMethod("renderItem", isClass, int.class, int.class).invoke(graphics, armorStack, 0, 0);
                            pose.getClass().getMethod("popPose").invoke(pose);

                            // 2. Отображение точных PvP-цифр прочности "Текущая/Максимальная"
                            if ((boolean) armorStack.getClass().getMethod("isDamageableItem").invoke(armorStack)) {
                                int maxDmg = (int) armorStack.getClass().getMethod("getMaxDamage").invoke(armorStack);
                                int currentDurability = maxDmg - (int) armorStack.getClass().getMethod("getDamageValue").invoke(armorStack);
                                
                                pose.getClass().getMethod("pushPose").invoke(pose);
                                // Сдвигаем влево под иконку и уменьшаем размер текста, чтобы "X/Y" выглядело компактно
                                pose.getClass().getMethod("translate", float.class, float.class, float.class).invoke(pose, (float)(currentX - 25), (float)(top + 3), 0.0f);
                                pose.getClass().getMethod("scale", float.class, float.class, float.class).invoke(pose, 0.5f, 0.5f, 0.5f);
                                
                                String textDurability = currentDurability + "/" + maxDmg;
                                int color = ((float) currentDurability / maxDmg < 0.25f) ? 0xFFFF5555 : 0xFFFFFFFF;
                                
                                graphics.getClass().getMethod("drawString", font.getClass(), String.class, float.class, float.class, int.class, boolean.class)
                                        .invoke(graphics, font, textDurability, 0.0f, 0.0f, color, true);
                                pose.getClass().getMethod("popPose").invoke(pose);
                            }
                            currentX -= 36; // Идеальное PvP-расстояние между элементами сета
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
    }
}
