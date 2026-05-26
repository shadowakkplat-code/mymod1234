package com.example.mymod;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

class HandConfigScreen extends Screen {
    private final boolean isLeftHand;

    public HandConfigScreen(boolean isLeftHand) { 
        super(Component.literal(isLeftHand ? "Настройка ЛЕВОЙ руки" : "Настройка ПРАВОЙ руки")); 
        this.isLeftHand = isLeftHand;
    }

    private void drawCustomButton(GuiGraphics g, String text, int x, int y, int w, int h, int mx, int my) {
        boolean hovered = mx >= x && mx <= x + w && my >= y && my <= y + h;
        g.fill(x, y, x + w, y + h, hovered ? 0xEE777777 : 0xEE444444);
        g.drawCenteredString(this.font, text, x + w / 2, y + (h - 8) / 2, 0xFFFFFFFF);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, "Нажмите ESC для возврата в игру", this.width / 2, this.height / 2 + 100, 0xAAAAAA);
        
        int cy = this.height / 2;
        int cx = this.width / 2 - 50;  
        
        String title = isLeftHand ? "[ ЛЕВАЯ РУКА (I) ]" : "[ ПРАВАЯ РУКА (K) ]";
        int color = isLeftHand ? 0x55FF55 : 0xFF5555;
        String handLabel = isLeftHand ? "(Л)" : "(П)";
        int currentScale = isLeftHand ? MyModConfig.leftScalePercent : MyModConfig.rightScalePercent;

        graphics.drawCenteredString(this.font, title, this.width / 2, cy - 90, color);

        drawCustomButton(graphics, "^ Выше " + handLabel, cx, cy - 70, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "v Ниже " + handLabel, cx, cy - 45, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "-> Дальше " + handLabel, cx, cy - 20, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "<- Ближе " + handLabel, cx, cy + 5, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "<= Влево " + handLabel, cx, cy + 30, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "Вправо => " + handLabel, cx, cy + 55, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "Размер: -" + currentScale + "%", cx, cy + 80, 100, 20, mouseX, mouseY);
        
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            int cy = this.height / 2;
            int cx = this.width / 2 - 50;
            
            if (mx >= cx && mx <= cx + 100) {
                if (my >= cy - 70 && my <= cy - 50) { if (isLeftHand) MyModConfig.leftY += 0.05f; else MyModConfig.rightY += 0.05f; return true; }
                if (my >= cy - 45 && my <= cy - 25) { if (isLeftHand) MyModConfig.leftY -= 0.05f; else MyModConfig.rightY -= 0.05f; return true; }
                if (my >= cy - 20 && my <= cy) { if (isLeftHand) MyModConfig.leftZ -= 0.05f; else MyModConfig.rightZ -= 0.05f; return true; }
                if (my >= cy + 5 && my <= cy + 25) { if (isLeftHand) MyModConfig.leftZ += 0.05f; else MyModConfig.rightZ += 0.05f; return true; }
                if (my >= cy + 30 && my <= cy + 50) { if (isLeftHand) MyModConfig.leftX -= 0.05f; else MyModConfig.rightX -= 0.05f; return true; }
                if (my >= cy + 55 && my <= cy + 75) { if (isLeftHand) MyModConfig.leftX += 0.05f; else MyModConfig.rightX += 0.05f; return true; }
                if (my >= cy + 80 && my <= cy + 100) {
                    if (isLeftHand) MyModConfig.leftScalePercent = (MyModConfig.leftScalePercent + 10) % 100;
                    else MyModConfig.rightScalePercent = (MyModConfig.rightScalePercent + 10) % 100;
                    return true;
                }
            }
        }
        return super.mouseClicked(mx, my, button);
    }
}

// ДОПИСАННОЕ МЕНЮ ЧАСТИЦ (Клавиша J)
class ParticleConfigScreen extends Screen {
    protected ParticleConfigScreen() { super(Component.literal("Сетка PvP Частиц")); }

    private void drawGridButton(GuiGraphics g, String text, int x, int y, int w, int h, int mx, int my, boolean active) {
        boolean hovered = mx >= x && mx <= x + w && my >= y && my <= y + h;
        int color = active ? 0xEE22AA22 : (hovered ? 0xEE777777 : 0xEE333333); 
        RenderSystem.enableBlend();
        g.fill(x, y, x + w, y + h, color);
        g.drawCenteredString(this.font, text, x + w / 2, y + (h - 8) / 2, 0xFFFFFFFF);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, "[ СЕТКА ПВП ЧАСТИЦ (J) ]", this.width / 2, 20, 0x55FFFF);
        graphics.drawCenteredString(this.font, "Нажмите ESC для сохранения", this.width / 2, this.height - 30, 0xAAAAAA);

        int startX = this.width / 2 - 130; 
        int startY = 45;
        
        for (int i = 0; i < 56; i++) {
            int row = i / 8;
            int col = i % 8;
            int x = startX + col * 32;
            int y = startY + row * 24;
            boolean active = (MyModConfig.activeParticleId == i);
            drawGridButton(graphics, String.valueOf(i + 1), x, y, 28, 20, mouseX, mouseY, active);
        }
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            int startX = this.width / 2 - 130;
            int startY = 45;
            for (int i = 0; i < 56; i++) {
                int row = i / 8;
                int col = i % 8;
                int x = startX + col * 32;
                int y = startY + row * 24;
                if (mx >= x && mx <= x + 28 && my >= y && my <= y + 20) {
                    MyModConfig.activeParticleId = i; // Меняем активную частицу при клике
                    return true;
                }
            }
        }
        return super.mouseClicked(mx, my, button);
    }
}
