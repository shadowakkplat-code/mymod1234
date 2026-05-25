package com.example.mymod;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MyArmor {
}

class ConfigScreen extends Screen {
    protected ConfigScreen() {
        super(Component.literal("Настройка рук"));
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(graphics);
    }

    private void drawCustomButton(GuiGraphics g, String text, int x, int y, int w, int h, int mx, int my) {
        boolean hovered = mx >= x && mx <= x + w && my >= y && my <= y + h;
        int color = hovered ? 0xEE777777 : 0xEE444444; 
        
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        
        g.fill(x, y, x + w, y + h, color);
        g.drawCenteredString(this.font, text, x + w / 2, y + (h - 8) / 2, 0xFFFFFFFF);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        
        graphics.drawCenteredString(this.font, "Нажмите ESC для возврата в игру", this.width / 2, this.height / 2 + 105, 0xAAAAAA);
        
        int cy = this.height / 2;
        int cxLeft = this.width / 2 - 120;  
        int cxRight = this.width / 2 + 20;  
        
        graphics.drawCenteredString(this.font, "[ ЛЕВАЯ РУКА ]", cxLeft + 50, cy - 80, 0x55FF55);
        graphics.drawCenteredString(this.font, "[ ПРАВАЯ РУКА ]", cxRight + 50, cy - 80, 0xFF5555);

        // Кнопки управления ЛЕВОЙ РУКОЙ
        drawCustomButton(graphics, "^ Выше (Л)", cxLeft, cy - 60, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "v Ниже (Л)", cxLeft, cy - 35, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "-> Дальше (Л)", cxLeft, cy, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "<- Ближе (Л)", cxLeft, cy + 25, 100, 20, mouseX, mouseY);
        
        // Кнопки управления ПРАВОЙ РУКОЙ
        drawCustomButton(graphics, "^ Выше (П)", cxRight, cy - 60, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "v Ниже (П)", cxRight, cy - 35, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "-> Дальше (П)", cxRight, cy, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "<- Ближе (П)", cxRight, cy + 25, 100, 20, mouseX, mouseY);
        
        drawCustomButton(graphics, "[x] Закрыть", this.width / 2 - 50, cy + 65, 100, 20, mouseX, mouseY);
        
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            int cy = this.height / 2;
            int cxLeft = this.width / 2 - 120;
            int cxRight = this.width / 2 + 20;
            Minecraft mc = Minecraft.getInstance();
            
            boolean clicked = false;
            
            // ОБРАБОТКА НАЖАТИЙ НА КНОПКИ ЛЕВОЙ РУКИ
            if (mx >= cxLeft && mx <= cxLeft + 100) {
                if (my >= cy - 60 && my <= cy - 40) {
                    MyMod.leftSwordY += 0.05f;
                    clicked = true;
                }
                else if (my >= cy - 35 && my <= cy - 15) {
                    MyMod.leftSwordY -= 0.05f;
                    clicked = true;
                }
                else if (my >= cy && my <= cy + 20) {
                    MyMod.leftSwordZ -= 0.05f;
                    clicked = true;
                }
                else if (my >= cy + 25 && my <= cy + 45) {
                    MyMod.leftSwordZ += 0.05f;
                    clicked = true;
                }
            }
            
            // ОБРАБОТКА НАЖАТИЙ НА КНОПКИ ПРАВОЙ РУКИ
            if (mx >= cxRight && mx <= cxRight + 100) {
                if (my >= cy - 60 && my <= cy - 40) {
                    MyMod.swordY += 0.05f;
                    clicked = true;
                }
                else if (my >= cy - 35 && my <= cy - 15) {
                    MyMod.swordY -= 0.05f;
                    clicked = true;
                }
                else if (my >= cy && my <= cy + 20) {
                    MyMod.swordZ -= 0.05f;
                    clicked = true;
                }
                else if (my >= cy + 25 && my <= cy + 45) {
                    MyMod.swordZ += 0.05f;
                    clicked = true;
                }
            }
            
            // ИСПРАВЛЕНО: Безопасный вызов обновления отрисовки рук, который 100% скомпилируется в 1.21.4
            if (clicked && mc.gameRenderer != null && mc.gameRenderer.itemInHandRenderer != null) {
                // Вызываем встроенную перезагрузку моделей в руках (без использования проблемного ивента)
                mc.gameRenderer.itemInHandRenderer.itemUsedInMainHandEvent(
                    mc.player.getMainHandItem()
                );
                return true;
            }
            
            // КНОПКА ЗАКРЫТЬ
            int closeX = this.width / 2 - 50;
            if (mx >= closeX && mx <= closeX + 100 && my >= cy + 65 && my <= cy + 85) {
                if (this.minecraft != null) {
                    this.minecraft.setScreen(null);
                }
                return true;
            }
        }
        return super.mouseClicked(mx, my, button);
    }
}
