package com.example.mymod;

import java.lang.reflect.Method;
import net.neoforged.bus.api.SubscribeEvent;

public class MyArmor {
    
    // СОВЕРШЕННО ДРУГОЙ СПОСОБ: Отрисовка брони через стадию рендеринга мира (невозможно скрыть)
    @SubscribeEvent
    public void onRenderStage(net.neoforged.neoforge.client.event.RenderStageEvent event) {
        try {
            // Рисуем в самой финальной стадии вывода кадра на монитор
            if (event.getStage().toString().contains("AFTER_LEVEL")) {
                Class<?> mcClass = Class.forName("net.minecraft.client.Minecraft");
                Object mc = mcClass.getMethod("getInstance").invoke(null);
                
                if (mcClass.getField("screen").get(mc) != null) return;
                Object player = mcClass.getField("player").get(mc);
                
                if (player != null) {
                    Object window = mcClass.getMethod("getWindow").invoke(mc);
                    int screenWidth = (int) window.getClass().getMethod("getGuiScaledWidth").invoke(window);
                    int screenHeight = (int) window.getClass().getMethod("getGuiScaledHeight").invoke(window);
                    
                    // Получаем актуальный системный графический контекст
                    Object graphics = mcClass.getField("gui").get(mc).getClass().getMethod("getGuiGraphics").invoke(mcClass.getField("gui").get(mc));
                    
                    int left = screenWidth / 2 + 91;
                    int top = screenHeight - 49; // Точные координаты над полоской еды
                    
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
                            Object pose = graphics.getClass().getMethod("pose").invoke(graphics);
                            pose.getClass().getMethod("pushPose").invoke(pose);
                            pose.getClass().getMethod("translate", float.class, float.class, float.class).invoke(pose, (float)currentX, (float)top, 0.0f);
                            pose.getClass().getMethod("scale", float.class, float.class, float.class).invoke(pose, 0.72f, 0.72f, 0.72f);
                            
                            graphics.getClass().getMethod("renderItem", isClass, int.class, int.class).invoke(graphics, armorStack, 0, 0);
                            pose.getClass().getMethod("popPose").invoke(pose);
                            currentX -= 16;
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
    }
}

class ConfigScreen extends net.minecraft.client.gui.screens.Screen {
    protected ConfigScreen() {
        super(net.minecraft.network.chat.Component.literal("Sword Config"));
    }

    // Исправленный метод отрисовки кнопок со сбросом цвета шейдера
    private void drawCustomButton(net.minecraft.client.gui.GuiGraphics g, String text, int x, int y, int w, int h, int mx, int my) {
        try {
            boolean hovered = mx >= x && mx <= x + w && my >= y && my <= y + h;
            int color = hovered ? 0xEE777777 : 0xEE444444; // Делаем кнопки светлыми и яркими
            
            // Включаем прозрачность и принудительно сбрасываем цвета рендеринга в шейдере
            Class<?> rsClass = Class.forName("com.mojang.blaze3d.systems.RenderSystem");
            rsClass.getMethod("enableBlend").invoke(null);
            rsClass.getMethod("setShaderColor", float.class, float.class, float.class, float.class).invoke(null, 1.0f, 1.0f, 1.0f, 1.0f);
            
            g.fill(x, y, x + w, y + h, color);
            g.drawCenteredString(this.font, text, x + w / 2, y + (h - 8) / 2, 0xFFFFFFFF);
        } catch (Exception ignored) {}
    }

    @Override
    public void render(net.minecraft.client.gui.GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, "⚔ PvP Client - Calibration Menu ⚔", this.width / 2, this.height / 2 - 85, 0xFFFFFF);
        graphics.drawCenteredString(this.font, "Press ESC to return to game", this.width / 2, this.height / 2 + 95, 0xAAAAAA);
        
        int cx = this.width / 2 - 50;
        int cy = this.height / 2;
        drawCustomButton(graphics, "▲ Higher", cx, cy - 60, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "▼ Lower", cx, cy - 35, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "✦ Further", cx, cy, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "⏳ Closer", cx, cy + 25, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "✔ Close", cx, cy + 65, 100, 20, mouseX, mouseY);
        
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            int cx = this.width / 2 - 50;
            int cy = this.height / 2;
            
            if (mx >= cx && mx <= cx + 100) {
                if (my >= cy - 60 && my <= cy - 40) {
                    MyMod.swordY += 0.05f;
                    return true;
                }
                else if (my >= cy - 35 && my <= cy - 15) {
                    MyMod.swordY -= 0.05f;
                    return true;
                }
                else if (my >= cy && my <= cy + 20) {
                    MyMod.swordZ -= 0.05f;
                    return true;
                }
                else if (my >= cy + 25 && my <= cy + 45) {
                    MyMod.swordZ += 0.05f;
                    return true;
                }
                else if (my >= cy + 65 && my <= cy + 85) {
                    this.minecraft.setScreen(null);
                    return true;
                }
            }
        }
        return super.mouseClicked(mx, my, button);
    }
}
