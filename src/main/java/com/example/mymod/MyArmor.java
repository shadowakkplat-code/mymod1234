package com.example.mymod;

import java.lang.reflect.Method;
import net.neoforged.bus.api.SubscribeEvent;

public class MyArmor {
    
    // Абсолютно стабильный рендеринг иконок брони через ScreenEvent (невозможно скрыть или заблокировать)
    @SubscribeEvent
    public void onRenderGui(net.neoforged.neoforge.client.event.ScreenEvent.Render.Post event) {
        try {
            Class<?> mcClass = Class.forName("net.minecraft.client.Minecraft");
            Object mc = mcClass.getMethod("getInstance").invoke(null);
            
            // Рисуем ХУД только если открыт пустой игровой экран (нет открытых инвентарей или чата)
            if (mcClass.getField("screen").get(mc) != null) return;
            Object player = mcClass.getField("player").get(mc);
            
            if (player != null) {
                Object window = mcClass.getMethod("getWindow").invoke(mc);
                int screenWidth = (int) window.getClass().getMethod("getGuiScaledWidth").invoke(window);
                int screenHeight = (int) window.getClass().getMethod("getGuiScaledHeight").invoke(window);
                Object graphics = event.getGuiGraphics();
                
                // Координаты: строго над шкалой голода (правая сторона хотбара)
                int left = screenWidth / 2 + 91;
                int top = screenHeight - 49;
                
                Class<?> esClass = Class.forName("net.minecraft.world.entity.EquipmentSlot");
                Object[] slots = {
                    esClass.getField("FEET").get(null), esClass.getField("LEGS").get(null),
                    esClass.getField("CHEST").get(null), esClass.getField("HEAD").get(null)
                };
                
                Class<?> isClass = Class.forName("net.minecraft.world.item.ItemStack");
                int currentX = left - 9;

                for (Object slot : slots) {
                    Object armorStack = player.getClass().getMethod("getItemBySlot", esClass).invoke(player, slot);
                    boolean isEmpty = (boolean) armorStack.getClass().getMethod("isEmpty").invoke(armorStack);
                    
                    if (!isEmpty) {
                        // Отрисовываем чистую, аккуратную мини-иконку надетой на игрока брони
                        Object pose = graphics.getClass().getMethod("pose").invoke(graphics);
                        pose.getClass().getMethod("pushPose").invoke(pose);
                        pose.getClass().getMethod("translate", float.class, float.class, float.class).invoke(pose, (float)currentX, (float)top, 0.0f);
                        pose.getClass().getMethod("scale", float.class, float.class, float.class).invoke(pose, 0.72f, 0.72f, 0.72f);
                        
                        graphics.getClass().getMethod("renderItem", isClass, int.class, int.class).invoke(graphics, armorStack, 0, 0);
                        pose.getClass().getMethod("popPose").invoke(pose);
                        
                        currentX -= 16; // Компактный PvP шаг между элементами сета
                    }
                }
            }
        } catch (Exception ignored) {}
    }
}

// Кастомный интерактивный класс экрана настроек калибровки меча
class ConfigScreen extends net.minecraft.client.gui.screens.Screen {
    protected ConfigScreen() {
        super(net.minecraft.network.chat.Component.literal("Sword Position Configuration"));
    }

    @Override
    protected void init() {
        try {
            Class<?> buttonClass = Class.forName("net.minecraft.client.gui.components.Button");
            Class<?> compClass = Class.forName("net.minecraft.network.chat.Component");
            
            // Кнопка 1: Поднять меч ВЫШЕ (Y + 0.05)
            Object btnUp = buttonClass.getConstructor(int.class, int.class, int.class, int.class, compClass, buttonClass.getDeclaredClasses()[0])
                .newInstance(this.width / 2 - 50, this.height / 2 - 60, 100, 20, compClass.getMethod("literal", String.class).invoke(null, "▲ Higher"), (b) -> {
                    MyMod.swordY += 0.05f;
                });
                
            // Кнопка 2: Опустить меч НИЖЕ (Y - 0.05)
            Object btnDown = buttonClass.getConstructor(int.class, int.class, int.class, int.class, compClass, buttonClass.getDeclaredClasses()[0])
                .newInstance(this.width / 2 - 50, this.height / 2 - 35, 100, 20, compClass.getMethod("literal", String.class).invoke(null, "▼ Lower"), (b) -> {
                    MyMod.swordY -= 0.05f;
                });

            // Кнопка 3: Отодвинуть меч ДАЛЬШЕ (Z - 0.05)
            Object btnFar = buttonClass.getConstructor(int.class, int.class, int.class, int.class, compClass, buttonClass.getDeclaredClasses()[0])
                .newInstance(this.width / 2 - 50, this.height / 2 + 0, 100, 20, compClass.getMethod("literal", String.class).invoke(null, "✦ Further"), (b) -> {
                    MyMod.swordZ -= 0.05f;
                });

            // Кнопка 4: Приблизить меч БЛИЖЕ (Z + 0.05)
            Object btnClose = buttonClass.getConstructor(int.class, int.class, int.class, int.class, compClass, buttonClass.getDeclaredClasses()[0])
                .newInstance(this.width / 2 - 50, this.height / 2 + 25, 100, 20, compClass.getMethod("literal", String.class).invoke(null, "⏳ Closer"), (b) -> {
                    MyMod.swordZ += 0.05f;
                });

            // Кнопка 5: НАЗАД В ИГРУ (Закрыть меню)
            Object btnBack = buttonClass.getConstructor(int.class, int.class, int.class, int.class, compClass, buttonClass.getDeclaredClasses()[0])
                .newInstance(this.width / 2 - 50, this.height / 2 + 65, 100, 20, compClass.getMethod("literal", String.class).invoke(null, "✔ Save & Close"), (b) -> {
                    this.minecraft.setScreen(null);
                });

            this.addRenderableWidget((net.minecraft.client.gui.components.events.GuiEventListener) btnUp);
            this.addRenderableWidget((net.minecraft.client.gui.components.events.GuiEventListener) btnDown);
            this.addRenderableWidget((net.minecraft.client.gui.components.events.GuiEventListener) btnFar);
            this.addRenderableWidget((net.minecraft.client.gui.components.events.GuiEventListener) btnClose);
            this.addRenderableWidget((net.minecraft.client.gui.components.events.GuiEventListener) btnBack);
        } catch (Exception ignored) {}
    }

    @Override
    public void render(net.minecraft.client.gui.GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, "⚔ PvP Client - Calibration Menu ⚔", this.width / 2, this.height / 2 - 85, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }
}
