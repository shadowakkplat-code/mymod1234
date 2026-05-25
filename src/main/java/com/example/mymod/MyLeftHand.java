package com.example.mymod;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;

public class MyLeftHand {

    @SubscribeEvent
    public void onRenderLeftHand(RenderHandEvent event) {
        if (event.getHand() != InteractionHand.OFF_HAND) return;

        ItemStack itemStack = event.getItemStack();
        if (itemStack.isEmpty()) return;

        String itemName = itemStack.getItem().toString().toLowerCase();
        if (itemName.contains("sword") || itemName.contains("axe") || itemName.contains("pickaxe")) {
            PoseStack poseStack = event.getPoseStack();
            
            // УМЕНЬШЕНО В 2 РАЗА: 0.275f
            poseStack.scale(0.275f, 0.275f, 0.275f);
            
            // ИСПРАВЛЕНО: Ссылаемся на переменные калибровки из ConfigScreen
            poseStack.translate(-0.24D, (double)ConfigScreen.leftY, (double)ConfigScreen.leftZ);
        }
    }
}
