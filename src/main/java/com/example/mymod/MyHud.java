package com.example.mymod;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

public class MyHud {
    private static final ResourceLocation GUI_ICONS = ResourceLocation.withDefaultNamespace("textures/gui/icons.png");

    @SubscribeEvent
    public void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null || mc.player == null || mc.options.hideGui) return;

        GuiGraphics graphics = event.getGuiGraphics();
        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();

        // 1. КРАСНАЯ ОБВОДКА ШКАЛЫ ЕДЫ (СЫТОСТЬ)
        float saturation = mc.player.getFoodData().getSaturationLevel();
        if (saturation > 0) {
            RenderSystem.enableBlend();
            // ИСПРАВЛЕНО: Чистый красный цвет фильтра для обводки
            RenderSystem.setShaderColor(1.0f, 0.0f, 0.0f, 1.0f); 
            
            int foodX = screenWidth / 2 + 91;
            int foodY = screenHeight - 39;
            int activeHearts = (int) Math.ceil(saturation / 2.0f);

            for (int i = 0; i < Math.min(activeHearts, 10); i++) {
                int x = foodX - i * 8 - 9;
                graphics.blit(RenderType::guiTextured, GUI_ICONS, x, foodY, 16.0f, 27.0f, 9, 9, 256, 256);
            }
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }

        // 2. УВЕЛИЧЕННАЯ БРОНЯ (Справа налево, без соприкосновения)
        int leftArmor = screenWidth / 2 + 75; 
        // ИСПРАВЛЕНО: Сдвинули на 3 пикселя выше (-54 вместо -51), чтобы крупные иконки не задевали еду
        int topArmor = screenHeight - 54; 
        
        EquipmentSlot[] slots = {
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET
        };
        
        int currentX = leftArmor;

        for (EquipmentSlot slot : slots) {
            ItemStack armorStack = mc.player.getItemBySlot(slot);
            
            if (!armorStack.isEmpty()) {
                PoseStack poseStack = graphics.pose();
                poseStack.pushPose();
                
                poseStack.translate(currentX, topArmor, 0.0f);
                // ИСПРАВЛЕНО: Сделали иконки еще чуть больше, но безопасно для интерфейса
                poseStack.scale(0.82f, 0.82f, 0.82f);
                
                graphics.renderItem(armorStack, 0, 0);
                graphics.renderItemDecorations(mc.font, armorStack, 0, 0);
                
                poseStack.popPose();
                currentX -= 24; 
            }
        }
    }
}
