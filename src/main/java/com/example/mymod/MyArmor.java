package com.example.mymod;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MyArmor {
    // Данный класс используется как контейнер для ConfigScreen. Отрисовка HUD вынесена в MyHud.java
}

class ConfigScreen extends Screen {
    protected ConfigScreen() {
        super(Component.literal("Настройка меча"));
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
        
        // ИСПРАВЛЕНО: Строка с верхним заголовком полностью удалена!
        
        // Нижняя подсказка для удобства оставлена
        graphics.drawCenteredString(this.font, "Нажмите ESC для возврата в игру", this.width / 2, this.height / 2 + 95, 0xAAAAAA);
        
        int cx = this.width / 2 - 50;
        int cy = this.height / 2;
        
        drawCustomButton(graphics, "^ Выше", cx, cy - 60, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "v Ниже", cx, cy - 35, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "-> Дальше", cx, cy, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "<- Ближе", cx, cy + 25, 100, 20, mouseX, mouseY);
        drawCustomButton(graphics, "[x] Закрыть", cx, cy + 65, 100, 20, mouseX, mouseY);
        
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
                    if (this.minecraft != null) {
                        this.minecraft.setScreen(null);
                    }
                    return true;
                }
            }
        }
        return super.mouseClicked(mx, my, button);
    }
}
