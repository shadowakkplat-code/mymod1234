package com.example.mymod;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public class MyHud {

    // 1. ПЕРЕХВАТЫВАЕМ ОТРИСОВКУ КОНКРЕТНЫХ СЛОЕВ HUD (ПРИЦЕЛ И ОПЫТ)
    @SubscribeEvent
    public void onRenderLayerPre(RenderGuiLayerEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null || mc.player == null || mc.options.hideGui) return;

        // Если сейчас будет рисоваться ПРИЦЕЛ или ШКАЛА ОПЫТА — включаем красный цвет
        if (event.getName().equals(VanillaGuiLayers.CROSSHAIR) || 
            event.getName().equals(VanillaGuiLayers.EXPERIENCE_BAR)) {
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0f, 0.0f, 0.0f, 1.0f); // Чистый красный
        }
    }

    @SubscribeEvent
    public void onRenderLayerPost(RenderGuiLayerEvent.Post event) {
        // Как только прицел или шкала опыта нарисовались — сразу возвращаем белый цвет,
        // чтобы сердечки, еда и другие элементы не окрасились в красный
        if (event.getName().equals(VanillaGuiLayers.CROSSHAIR) || 
            event.getName().equals(VanillaGuiLayers.EXPERIENCE_BAR)) {
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    // 2. ОТРИСОВКА УВЕЛИЧЕННОЙ БРОНИ (Остается стандартного цвета)
    @SubscribeEvent
    public void onRenderGuiPost(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null || mc.player == null || mc.options.hideGui) return;

        // На всякий случай гарантируем сброс цвета перед броней
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        GuiGraphics graphics = event.getGuiGraphics();
        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();

        // Отрисовка брони (Справа налево, размер 82%, без соприкосновений)
        int leftArmor = screenWidth / 2 + 75; 
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
                poseStack.scale(0.82f, 0.82f, 0.82f);
                
                graphics.renderItem(armorStack, 0, 0);
                graphics.renderItemDecorations(mc.font, armorStack, 0, 0);
                
                poseStack.popPose();
                currentX -= 24; 
            }
        }
    }
}
