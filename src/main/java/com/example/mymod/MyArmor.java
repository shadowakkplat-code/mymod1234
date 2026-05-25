package com.example.mymod;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

public class MyArmor {
    
    @SubscribeEvent
    public void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        
        if (mc.screen != null || mc.player == null) return;

        GuiGraphics graphics = event.getGuiGraphics();
        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();
        
        // 1. ОТРИСОВКА ИКОНОК БРОНИ
        // Корректируем начальную позицию, чтобы растянуть броню на всю длину шкалы голода
        int leftArmor = screenWidth / 2 + 15; 
        int topArmor = screenHeight - 51; // Чуть приподняли для увеличенного размера
        
        EquipmentSlot[] slots = {
            EquipmentSlot.FEET, 
            EquipmentSlot.LEGS,
            EquipmentSlot.CHEST, 
            EquipmentSlot.HEAD
        };
        
        int currentX = leftArmor;

        for (EquipmentSlot slot : slots) {
            ItemStack armorStack = mc.player.getItemBySlot(slot);
            
            if (!armorStack.isEmpty()) {
                PoseStack poseStack = graphics.pose();
                poseStack.pushPose();
                
                poseStack.translate(currentX, topArmor, 0.0f);
                // ИСПРАВЛЕНО: Увеличили размер иконок на 5% (с 0.72f до 0.77f)
                poseStack.scale(0.77f, 0.77f, 0.77f);
                
                graphics.renderItem(armorStack, 0, 0);
                graphics.renderItemDecorations(mc.font, armorStack, 0, 0);
                
                poseStack.popPose();
                // ИСПРАВЛЕНО: Шаг увеличен до 24 пикселей, чтобы растянуть на всю длину панели
                currentX += 24; 
            }
        }

        // 2. ОТРИСОВКА ТЕКСТА СЫТОСТИ (SATURATION)
        float saturation = mc.player.getFoodData().getSaturationLevel();
        // Форматируем до одного знака после запятой (например: "14.5")
        String satText = String.format("%.1f", saturation); 
        
        // Координаты текста ровно по центру над шкалой еды
        int satX = screenWidth / 2 + 50; 
        int satY = screenHeight - 48; 
        
        // Рисуем текст сытости золотым цветом с красивой черной тенью
        graphics.drawString(mc.font, satText, satX, satY, 0xFFFF55, true);
    }
}

class ConfigScreen extends Screen {
    protected ConfigScreen() {
        super(Component.literal("Sword Config"));
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
