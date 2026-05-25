package com.example.mymod;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MyArmor {
    // Контейнер класса
}

class RightConfigScreen extends Screen {
    protected RightConfigScreen() { super(Component.literal("Настройка правой руки")); }

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
        graphics.drawCenteredString(this.font, "Нажмите ESC для возврата в игру", this.width / 2, this.height / 2 + 125, 0xAAAAAA);
        
        int cy = this.height / 2;
        int cx = this.width / 2 - 50;  
        
        graphics.drawCenteredString(this.font, "[ ПРАВАЯ РУКА ]", this.width / 2, cy - 90, 0xFF5555);

        drawCustomButton(graphics, "^ Выше (П)", cx, cy - 70, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "v Ниже (П)", cx, cy - 45, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "-> Дальше (П)", cx, cy - 20, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "<- Ближе (П)", cx, cy + 5, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "<= Влево (П)", cx, cy + 30, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "Вправо => (П)", cx, cy + 55, 100, 20, mouseX, mouseY);
        
        drawCustomButton(graphics, "[x] Закрыть", cx, cy + 85, 100, 20, mouseX, mouseY);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            int cy = this.height / 2;
            int cx = this.width / 2 - 50;
            
            if (mx >= cx && mx < cx + 100) {
                if (my >= cy - 70 && my < cy - 50) { RightHandConfig.rightY += 0.05f; return true; }
                if (my >= cy - 45 && my < cy - 25) { RightHandConfig.rightY -= 0.05f; return true; }
                if (my >= cy - 20 && my < cy) { RightHandConfig.rightZ -= 0.05f; return true; }
                if (my >= cy + 5 && my < cy + 25) { RightHandConfig.rightZ += 0.05f; return true; }
                if (my >= cy + 30 && my < cy + 50) { RightHandConfig.rightX -= 0.05f; return true; }
                if (my >= cy + 55 && my < cy + 75) { RightHandConfig.rightX += 0.05f; return true; }
                if (my >= cy + 85 && my < cy + 105) {
                    if (this.minecraft != null) this.minecraft.setScreen(null);
                    return true;
                }
            }
        }
        return super.mouseClicked(mx, my, button);
    }
}

class LeftConfigScreen extends Screen {
    protected LeftConfigScreen() { super(Component.literal("Настройка левой руки")); }

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
        graphics.drawCenteredString(this.font, "Нажмите ESC для возврата в игру", this.width / 2, this.height / 2 + 125, 0xAAAAAA);
        
        int cy = this.height / 2;
        int cx = this.width / 2 - 50;  
        
        graphics.drawCenteredString(this.font, "[ ЛЕВАЯ РУКА ]", this.width / 2, cy - 90, 0x55FF55);

        drawCustomButton(graphics, "^ Выше (Л)", cx, cy - 70, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "v Ниже (Л)", cx, cy - 45, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "-> Дальше (Л)", cx, cy - 20, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "<- Ближе (Л)", cx, cy + 5, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "<= Влево (Л)", cx, cy + 30, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "Вправо => (Л)", cx, cy + 55, 100, 20, mouseX, mouseY);
        
        drawCustomButton(graphics, "[x] Закрыть", cx, cy + 85, 100, 20, mouseX, mouseY);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            int cy = this.height / 2;
            int cx = this.width / 2 - 50;
            
            if (mx >= cx && mx < cx + 100) {
                if (my >= cy - 70 && my < cy - 50) { LeftHandConfig.leftY += 0.05f; return true; }
                if (my >= cy - 45 && my < cy - 25) { LeftHandConfig.leftY -= 0.05f; return true; }
                if (my >= cy - 20 && my < cy) { LeftHandConfig.leftZ -= 0.05f; return true; }
                if (my >= cy + 5 && my < cy + 25) { LeftHandConfig.leftZ += 0.05f; return true; }
                if (my >= cy + 30 && my < cy + 50) { LeftHandConfig.leftX -= 0.05f; return true; }
                if (my >= cy + 55 && my < cy + 75) { LeftHandConfig.leftX += 0.05f; return true; }
                if (my >= cy + 85 && my < cy + 105) {
                    if (this.minecraft != null) this.minecraft.setScreen(null);
                    return true;
                }
            }
        }
        return super.mouseClicked(mx, my, button);
    }
}
