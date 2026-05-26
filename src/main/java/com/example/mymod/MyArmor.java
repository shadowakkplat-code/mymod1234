package com.example.mymod;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MyArmor { }

class RightConfigScreen extends Screen {
    public RightConfigScreen() { super(Component.literal("Настройка ПРАВОЙ руки")); }

    private void drawCustomButton(GuiGraphics g, String text, int x, int y, int w, int h, int mx, int my) {
        boolean hovered = mx >= x && mx <= x + w && my >= y && my <= y + h;
        g.fill(x, y, x + w, y + h, hovered ? 0xEE777777 : 0xEE444444);
        g.drawCenteredString(this.font, text, x + w / 2, y + (h - 8) / 2, 0xFFFFFFFF);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, "Нажмите ESC для возврата в игру", this.width / 2, this.height / 2 + 100, 0xAAAAAA);
        int cy = this.height / 2; int cx = this.width / 2 - 50;  
        graphics.drawCenteredString(this.font, "[ ПРАВАЯ РУКА (K) ]", this.width / 2, cy - 90, 0xFF5555);
        drawCustomButton(graphics, "^ Выше (П)", cx, cy - 70, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "v Ниже (П)", cx, cy - 45, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "-> Дальше (П)", cx, cy - 20, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "<- Ближе (П)", cx, cy + 5, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "<= Влево (П)", cx, cy + 30, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "Вправо => (П)", cx, cy + 55, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "Размер (П): -" + RightHandConfig.rightScalePercent + "%", cx, cy + 80, 100, 20, mouseX, mouseY);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            int cy = this.height / 2; int cx = this.width / 2 - 50;
            if (mx >= cx && mx <= cx + 100) {
                if (my >= cy - 70 && my <= cy - 50) { RightHandConfig.rightY += 0.05f; return true; }
                if (my >= cy - 45 && my <= cy - 25) { RightHandConfig.rightY -= 0.05f; return true; }
                if (my >= cy - 20 && my <= cy) { RightHandConfig.rightZ -= 0.05f; return true; }
                if (my >= cy + 5 && my <= cy + 25) { RightHandConfig.rightZ += 0.05f; return true; }
                if (my >= cy + 30 && my <= cy + 50) { RightHandConfig.rightX -= 0.05f; return true; }
                if (my >= cy + 55 && my <= cy + 75) { RightHandConfig.rightX += 0.05f; return true; }
                if (my >= cy + 80 && my <= cy + 100) { RightHandConfig.rightScalePercent = (RightHandConfig.rightScalePercent + 10) % 100; return true; }
            }
        }
        return super.mouseClicked(mx, my, button);
    }
}

class LeftConfigScreen extends Screen {
    public LeftConfigScreen() { super(Component.literal("Настройка ЛЕВОЙ руки")); }

    private void drawCustomButton(GuiGraphics g, String text, int x, int y, int w, int h, int mx, int my) {
        boolean hovered = mx >= x && mx <= x + w && my >= y && my <= y + h;
        g.fill(x, y, x + w, y + h, hovered ? 0xEE777777 : 0xEE444444);
        g.drawCenteredString(this.font, text, x + w / 2, y + (h - 8) / 2, 0xFFFFFFFF);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, "Нажмите ESC для возврата в игру", this.width / 2, this.height / 2 + 100, 0xAAAAAA);
        int cy = this.height / 2; int cx = this.width / 2 - 50;  
        graphics.drawCenteredString(this.font, "[ ЛЕВАЯ РУКА (I) ]", this.width / 2, cy - 90, 0x55FF55);
        drawCustomButton(graphics, "^ Выше (Л)", cx, cy - 70, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "v Ниже (Л)", cx, cy - 45, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "-> Дальше (Л)", cx, cy - 20, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "<- Ближе (Л)", cx, cy + 5, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "<= Влево (Л)", cx, cy + 30, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "Вправо => (Л)", cx, cy + 55, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "Размер (Л): -" + RightHandConfig.leftScalePercent + "%", cx, cy + 80, 100, 20, mouseX, mouseY);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            int cy = this.height / 2; int cx = this.width / 2 - 50;
            if (mx >= cx && mx <= cx + 100) {
                if (my >= cy - 70 && my <= cy - 50) { RightHandConfig.leftY += 0.05f; return true; }
                if (my >= cy - 45 && my <= cy - 25) { RightHandConfig.leftY -= 0.05f; return true; }
                if (my >= cy - 20 && my <= cy) { RightHandConfig.leftZ -= 0.05f; return true; }
                if (my >= cy + 5 && my <= cy + 25) { RightHandConfig.leftZ += 0.05f; return true; }
                if (my >= cy + 30 && my <= cy + 50) { RightHandConfig.leftX -= 0.05f; return true; }
                if (my >= cy + 55 && my <= cy + 75) { RightHandConfig.leftX += 0.05f; return true; }
                if (my >= cy + 80 && my <= cy + 100) { RightHandConfig.leftScalePercent = (RightHandConfig.leftScalePercent + 10) % 100; return true; }
            }
        }
        return super.mouseClicked(mx, my, button);
    }
}
