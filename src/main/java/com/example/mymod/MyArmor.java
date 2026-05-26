package com.example.mymod;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MyArmor {
    // Контейнер класса
}

// ЭКРАН НА КЛАВИШУ K: НАСТРОЙКА РУК
class RightConfigScreen extends Screen {
    protected RightConfigScreen() { super(Component.literal("Настройка рук и анимации")); }

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
        int cxLeft = this.width / 2 - 120;  
        int cxRight = this.width / 2 + 20;  
        
        graphics.drawCenteredString(this.font, "[ ЛЕВАЯ РУКА ]", cxLeft + 50, cy - 90, 0x55FF55);
        graphics.drawCenteredString(this.font, "[ ПРАВАЯ РУКА ]", cxRight + 50, cy - 90, 0xFF5555);

        // ЛЕВАЯ РУКА КНОПКИ
        drawCustomButton(graphics, "^ Выше (Л)", cxLeft, cy - 70, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "v Ниже (Л)", cxLeft, cy - 45, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "-> Дальше (Л)", cxLeft, cy - 20, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "<- Ближе (Л)", cxLeft, cy + 5, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "<= Влево (Л)", cxLeft, cy + 30, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "Вправо => (Л)", cxLeft, cy + 55, 100, 20, mouseX, mouseY);
        
        // ПРАВАЯ РУКА КНОПКИ
        drawCustomButton(graphics, "^ Выше (П)", cxRight, cy - 70, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "v Ниже (П)", cxRight, cy - 45, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "-> Дальше (П)", cxRight, cy - 20, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "<- Ближе (П)", cxRight, cy + 5, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "<= Влево (П)", cxRight, cy + 30, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "Вправо => (П)", cxRight, cy + 55, 100, 20, mouseX, mouseY);
        
        String animName = RightHandConfig.swingMode == 1 ? "Анимация: Прокрут 360" : "Анимация: Ванилла";
        drawCustomButton(graphics, animName, this.width / 2 - 75, cy + 80, 150, 20, mouseX, mouseY);
        
        drawCustomButton(graphics, "[x] Закрыть", this.width / 2 - 50, cy + 105, 100, 20, mouseX, mouseY);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            int cy = this.height / 2;
            int cxLeft = this.width / 2 - 120;
            int cxRight = this.width / 2 + 20;
            
            if (mx >= cxLeft && mx < cxLeft + 100) {
                if (my >= cy - 70 && my < cy - 50) { RightHandConfig.leftY += 0.05f; return true; }
                if (my >= cy - 45 && my < cy - 25) { RightHandConfig.leftY -= 0.05f; return true; }
                if (my >= cy - 20 && my < cy) { RightHandConfig.leftZ -= 0.05f; return true; }
                if (my >= cy + 5 && my < cy + 25) { RightHandConfig.leftZ += 0.05f; return true; }
                if (my >= cy + 30 && my < cy + 50) { RightHandConfig.leftX -= 0.05f; return true; }
                if (my >= cy + 55 && my < cy + 75) { RightHandConfig.leftX += 0.05f; return true; }
            }
            
            if (mx >= cxRight && mx < cxRight + 100) {
                if (my >= cy - 70 && my < cy - 50) { RightHandConfig.rightY += 0.05f; return true; }
                if (my >= cy - 45 && my < cy - 25) { RightHandConfig.rightY -= 0.05f; return true; }
                if (my >= cy - 20 && my < cy) { RightHandConfig.rightZ -= 0.05f; return true; }
                if (my >= cy + 5 && my < cy + 25) { RightHandConfig.rightZ += 0.05f; return true; }
                if (my >= cy + 30 && my < cy + 50) { RightHandConfig.rightX -= 0.05f; return true; }
                if (my >= cy + 55 && my < cy + 75) { RightHandConfig.rightX += 0.05f; return true; }
            }
            
            if (mx >= this.width / 2 - 75 && mx < this.width / 2 + 75 && my >= cy + 80 && my < cy + 100) {
                RightHandConfig.swingMode = RightHandConfig.swingMode == 1 ? 0 : 1;
                return true;
            }

            if (mx >= this.width / 2 - 50 && mx < this.width / 2 + 50 && my >= cy + 105 && my < cy + 125) {
                if (this.minecraft != null) this.minecraft.setScreen(null);
                return true;
            }
        }
        return super.mouseClicked(mx, my, button);
    }
}

// ЭКРАН НА КЛАВИШУ J: ВЫБОР ЧАСТИЦ (Исправлена ширина кнопок и хитбоксы клика)
class LeftConfigScreen extends Screen {
    protected LeftConfigScreen() { super(Component.literal("Выбор PvP эффекта частиц")); }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(graphics);
    }

    private void drawCustomButton(GuiGraphics g, String text, int x, int y, int w, int h, int mx, int my, boolean active) {
        boolean hovered = mx >= x && mx <= x + w && my >= y && my <= y + h;
        int color = active ? 0xEE22AA22 : (hovered ? 0xEE777777 : 0xEE444444); 
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        g.fill(x, y, x + w, y + h, color);
        g.drawCenteredString(this.font, text, x + w / 2, y + (h - 8) / 2, 0xFFFFFFFF);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, "Нажмите ESC для возврата", this.width / 2, this.height / 2 + 90, 0xAAAAAA);
        
        int cy = this.height / 2;
        int cx = this.width / 2 - 75; // Кнопка шириной 150 пикселей, центрируем (150 / 2 = 75)
        
        graphics.drawCenteredString(this.font, "==== ТИП PvP ЧАСТИЦ (ИХ БУДЕТ 50) ====", this.width / 2, cy - 80, 0xFFFF55);

        // Исправлено: ширина кнопок выставлена в 150, чтобы соответствовать хитбоксу
        drawCustomButton(graphics, "1. Искры Энда", cx, cy - 55, 150, 20, mouseX, mouseY, RightHandConfig.particleMode == 0);
        drawCustomButton(graphics, "2. Крит-Сердечки", cx, cy - 30, 150, 20, mouseX, mouseY, RightHandConfig.particleMode == 1);
        drawCustomButton(graphics, "3. Огненный Взрыв", cx, cy - 5, 150, 20, mouseX, mouseY, RightHandConfig.particleMode == 2);
        drawCustomButton(graphics, "4. Ведьмин Дым", cx, cy + 20, 150, 20, mouseX, mouseY, RightHandConfig.particleMode == 3);
        drawCustomButton(graphics, "5. Огненные Души", cx, cy + 45, 150, 20, mouseX, mouseY, RightHandConfig.particleMode == 4);
        
        drawCustomButton(graphics, "[x] Закрыть", this.width / 2 - 50, cy + 70, 100, 20, mouseX, mouseY, false);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            int cy = this.height / 2;
            int cx = this.width / 2 - 75;

            // ИСПРАВЛЕНО: Хитбоксы кликов теперь идеально совпадают со всеми 5 кнопками выбора частиц
            if (mx >= cx && mx <= cx + 150) {
                if (my >= cy - 55 && my <= cy - 35) { RightHandConfig.particleMode = 0; return true; }
                if (my >= cy - 30 && my <= cy - 10) { RightHandConfig.particleMode = 1; return true; }
                if (my >= cy - 5 && my <= cy + 15) { RightHandConfig.particleMode = 2; return true; }
                if (my >= cy + 20 && my <= cy + 40) { RightHandConfig.particleMode = 3; return true; }
                if (my >= cy + 45 && my <= cy + 65) { RightHandConfig.particleMode = 4; return true; }
            }

            if (mx >= this.width / 2 - 50 && mx <= this.width / 2 + 50 && my >= cy + 70 && my <= cy + 90) {
                if (this.minecraft != null) this.minecraft.setScreen(null);
                return true;
            }
        }
        return super.mouseClicked(mx, my, button);
    }
}
