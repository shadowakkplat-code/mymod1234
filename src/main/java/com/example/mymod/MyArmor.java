package com.example.mymod;

import java.lang.reflect.Method;
import net.neoforged.bus.api.SubscribeEvent;

public class MyArmor {
    // Переключено на .Pre для гарантированного вывода текстур на экран
    @SubscribeEvent
    public void onRenderArmor(net.neoforged.neoforge.client.event.RenderGuiLayerEvent.Pre event) {
        try {
            Method getLayerMethod = event.getClass().getMethod("getLayer");
            Object layer = getLayerMethod.invoke(event);
            String name = layer.toString();
            
            // Ловим слой отрисовки еды или хотбара
            if (name.contains("FOOD_LEVEL") || name.contains("food") || name.contains("HOTBAR") || name.contains("hotbar")) {
                Class<?> mcClass = Class.forName("net.minecraft.client.Minecraft");
                Object mc = mcClass.getMethod("getInstance").invoke(null);
                Object player = mcClass.getField("player").get(mc);
                
                if (player != null) {
                    Object window = mcClass.getMethod("getWindow").invoke(mc);
                    int screenWidth = (int) window.getClass().getMethod("getGuiScaledWidth").invoke(window);
                    int screenHeight = (int) window.getClass().getMethod("getGuiScaledHeight").invoke(window);
                    Object graphics = event.getGuiGraphics();
                    
                    // Координаты: ровно над полоской голода (справа над хотбаром)
                    int left = screenWidth / 2 + 91;
                    int top = screenHeight - 49; // Идеальная высота над едой
                    
                    Class<?> esClass = Class.forName("net.minecraft.world.entity.EquipmentSlot");
                    Object[] slots = {
                        esClass.getField("FEET").get(null),
                        esClass.getField("LEGS").get(null),
                        esClass.getField("CHEST").get(null),
                        esClass.getField("HEAD").get(null)
                    };
                    
                    Class<?> isClass = Class.forName("net.minecraft.world.item.ItemStack");
                    int currentX = left - 9;

                    for (Object slot : slots) {
                        Object armorStack = player.getClass().getMethod("getItemBySlot", esClass).invoke(player, slot);
                        boolean isEmpty = (boolean) armorStack.getClass().getMethod("isEmpty").invoke(armorStack);
                        
                        if (!isEmpty) {
                            // Отрисовка мини-иконки брони
                            Object pose = graphics.getClass().getMethod("pose").invoke(graphics);
                            pose.getClass().getMethod("pushPose").invoke(pose);
                            pose.getClass().getMethod("translate", float.class, float.class, float.class).invoke(pose, (float)currentX, (float)top, 0.0f);
                            pose.getClass().getMethod("scale", float.class, float.class, float.class).invoke(pose, 0.70f, 0.70f, 0.70f); // Чуть уменьшаем иконки для аккуратности
                            
                            // Вызываем метод рендеринга предмета в интерфейсе
                            graphics.getClass().getMethod("renderItem", isClass, int.class, int.class).invoke(graphics, armorStack, 0, 0);
                            pose.getClass().getMethod("popPose").invoke(pose);
                            
                            currentX -= 16; // Компактный шаг для чистых иконок
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
    }
}
