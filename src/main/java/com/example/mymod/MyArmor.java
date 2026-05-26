package com.example.mymod;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MyArmor {
    // Контейнер класса
}

class RightConfigScreen extends Screen {
    protected RightConfigScreen() { super(Component.literal("Настройка рук")); }

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
        graphics.drawCenteredString(this.font, "Нажмите ESC для возврата в игру", this.width / 2, this.height / 2 + 120, 0xAAAAAA);
        
        int cy = this.height / 2;
        int cxLeft = this.width / 2 - 120;  
        int cxRight = this.width / 2 + 20;  
        
        graphics.drawCenteredString(this.font, "[ ЛЕВАЯ РУКА ]", cxLeft + 50, cy - 90, 0x55FF55);
        graphics.drawCenteredString(this.font, "[ ПРАВАЯ РУКА ]", cxRight + 50, cy - 90, 0xFF5555);

        // КНОПКИ ЛЕВОЙ РУКИ
        drawCustomButton(graphics, "^ Выше (Л)", cxLeft, cy - 70, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "v Ниже (Л)", cxLeft, cy - 45, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "-> Дальше (Л)", cxLeft, cy - 20, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "<- Ближе (Л)", cxLeft, cy + 5, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "<= Влево (Л)", cxLeft, cy + 30, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "Вправо => (Л)", cxLeft, cy + 55, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "Размер (Л): -" + RightHandConfig.leftScalePercent + "%", cxLeft, cy + 80, 100, 20, mouseX, mouseY);
        
        // КНОПКИ ПРАВОЙ РУКИ
        drawCustomButton(graphics, "^ Выше (П)", cxRight, cy - 70, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "v Ниже (П)", cxRight, cy - 45, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "-> Дальше (П)", cxRight, cy - 20, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "<- Ближе (П)", cxRight, cy + 5, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "<= Влево (П)", cxRight, cy + 30, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "Вправо => (П)", cxRight, cy + 55, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "Размер (П): -" + RightHandConfig.rightScalePercent + "%", cxRight, cy + 80, 100, 20, mouseX, mouseY);
        
        drawCustomButton(graphics, "[x] Закрыть", this.width / 2 - 50, cy + 105, 100, 20, mouseX, mouseY);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            int cy = this.height / 2;
            int cxLeft = this.width / 2 - 120;
            int cxRight = this.width / 2 + 20;
            
            // Клики левой руки
            if (mx >= cxLeft && mx < cxLeft + 100) {
                if (my >= cy - 70 && my < cy - 50) { RightHandConfig.leftY += 0.05f; return true; }
                if (my >= cy - 45 && my < cy - 25) { RightHandConfig.leftY -= 0.05f; return true; }
                if (my >= cy - 20 && my < cy) { RightHandConfig.leftZ -= 0.05f; return true; }
                if (my >= cy + 5 && my < cy + 25) { RightHandConfig.leftZ += 0.05f; return true; }
                if (my >= cy + 30 && my < cy + 50) { RightHandConfig.leftX -= 0.05f; return true; }
                if (my >= cy + 55 && my < cy + 75) { RightHandConfig.leftX += 0.05f; return true; }
                if (my >= cy + 80 && my < cy + 100) {
                    RightHandConfig.leftScalePercent = (RightHandConfig.leftScalePercent + 10) % 100;
                    return true;
                }
            }
            
            // Клики правой руки
            if (mx >= cxRight && mx < cxRight + 100) {
                if (my >= cy - 70 && my < cy - 50) { RightHandConfig.rightY += 0.05f; return true; }
                if (my >= cy - 45 && my < cy - 25) { RightHandConfig.rightY -= 0.05f; return true; }
                if (my >= cy - 20 && my < cy) { RightHandConfig.rightZ -= 0.05f; return true; }
                if (my >= cy + 5 && my < cy + 25) { RightHandConfig.rightZ += 0.05f; return true; }
                if (my >= cy + 30 && my < cy + 50) { RightHandConfig.rightX -= 0.05f; return true; }
                if (my >= cy + 55 && my < cy + 75) { RightHandConfig.rightX += 0.05f; return true; }
                if (my >= cy + 80 && my < cy + 100) {
                    RightHandConfig.rightScalePercent = (RightHandConfig.rightScalePercent + 10) % 100;
                    return true;
                }
            }

            if (mx >= this.width / 2 - 50 && mx <= this.width / 2 + 50 && my >= cy + 105 && my < cy + 125) {
                if (this.minecraft != null) this.minecraft.setScreen(null);
                return true;
            }
        }
        return super.mouseClicked(mx, my, button);
    }
}

class LeftConfigScreen extends Screen {
    protected LeftConfigScreen() { super(Component.literal("Сетка PvP Частиц")); }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(graphics);
    }

    private void drawGridButton(GuiGraphics g, String text, int x, int y, int w, int h, int mx, int my, boolean active) {
        boolean hovered = mx >= x && mx <= x + w && my >= y && my <= y + h;
        int color = active ? 0xEE22AA22 : (hovered ? 0xEE777777 : 0xEE333333); 
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        g.fill(x, y, x + w, y + h, color);
        g.drawCenteredString(this.font, text, x + w / 2, y + (h - 8) / 2, active ? 0xFFFFFFFF : 0xDDDDDD);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        
        int cy = this.height / 2;
        graphics.drawCenteredString(this.font, "==== СЕТКА PvP ЧАСТИЦ (7 СТОЛБИКОВ × 8 КНОПОК) ====", this.width / 2, cy - 110, 0xFFFF55);

        int startX = this.width / 2 - 256; 
        int startY = cy - 85;
        
        int buttonId = 0;
        for (int col = 0; col < 7; col++) {
            for (int row = 0; row < 8; row++) {
                int btnX = startX + (col * 73);
                int btnY = startY + (row * 22);
                
                boolean isActive = (RightHandConfig.activeParticleId == buttonId);
                drawGridButton(graphics, "P-" + (buttonId + 1), btnX, btnY, 70, 18, mouseX, mouseY, isActive);
                buttonId++;
            }
        }
        
        drawCustomButton(graphics, "[x] Готово", this.width / 2 - 50, cy + 100, 100, 20, mouseX, mouseY);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void drawCustomButton(GuiGraphics g, String text, int x, int y, int w, int h, int mx, int my) {
        boolean hovered = mx >= x && mx <= x + w && my >= y && my <= y + h;
        g.fill(x, y, x + w, y + h, hovered ? 0xEE777777 : 0xEE444444);
        g.drawCenteredString(this.font, text, x + w / 2, y + (h - 8) / 2, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            int cy = this.height / 2;
            int startX = this.width / 2 - 256;
            int startY = cy - 85;

            int buttonId = 0;
            for (int col = 0; col < 7; col++) {
                for (int row = 0; row < 8; row++) {
                    int btnX = startX + (col * 73);
                    int btnY = startY + (row * 22);

                    if (mx >= btnX && mx <= btnX + 70 && my >= btnY && my <= btnY + 18) {
                        RightHandConfig.activeParticleId = buttonId; 
                        return true;
                    }
                    buttonId++;
                }
            }

            if (mx >= this.width / 2 - 50 && mx <= this.width / 2 + 50 && my >= cy + 100 && my <= cy + 120) {
                if (this.minecraft != null) this.minecraft.setScreen(null);
                return true;
            }
        }
        return super.mouseClicked(mx, my, button);
    }
}
