package com.example.mymod;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

public class MyHud {

    @SubscribeEvent
    public void onRenderGuiPost(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null || mc.player == null || mc.options.hideGui) return;

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        GuiGraphics graphics = event.getGuiGraphics();
        int screenWidth = graphics.guiWidth();
        int screenHeight = graphics.guiHeight();

        // ОТРИСОВКА УВЕЛИЧЕННОЙ БРОНИ (Справа налево, размер 82%, без соприкосновения)
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
