package com.example.mymod.left;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RenderHandEvent;

public class LeftHandRenderer {
    public static void render(RenderHandEvent event, ItemStack stack) {
        if (event.getHand() == InteractionHand.OFF_HAND) {
            PoseStack poseStack = event.getPoseStack();
            poseStack.pushPose();
            
            // Сдвиг влево и применение кастомных кнопок из меню
            poseStack.translate(-0.45D, (double)LeftHandConfig.leftY, (double)LeftHandConfig.leftZ);
            // Жесткое уменьшение левой руки ровно в два раза (0.275f)
            poseStack.scale(0.275f, 0.275f, 0.275f);
        }
    }
}
