package com.example.mymod;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RenderHandEvent;

public class RightHandRenderer {
    public static void render(RenderHandEvent event, ItemStack stack) {
        if (event.getHand() == InteractionHand.MAIN_HAND) {
            PoseStack poseStack = event.getPoseStack();
            poseStack.pushPose();
            
            poseStack.translate(0.12D, (double)RightHandConfig.rightY, (double)RightHandConfig.rightZ);
            poseStack.scale(0.55f, 0.55f, 0.55f);
            
            poseStack.popPose(); // ИСПРАВЛЕНО: Закрываем стек матрицы
        }
    }
}
