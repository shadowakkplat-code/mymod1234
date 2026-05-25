package com.example.mymod;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.particles.ParticleTypes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;

@Mod("mymod")
public class MyMod {
    private static boolean wasClicking = false;
    private static boolean wasKeyKDown = false;
    private static boolean wasKeyJDown = false;
    
    private static final java.util.Random RANDOM = new java.util.Random();

    public MyMod() {
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new MyFire());
        NeoForge.EVENT_BUS.register(new MyHud()); 
    }

    @SubscribeEvent
    public void onClientTickPost(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        boolean isDown = mc.options.keyAttack.isDown();
        if (isDown && !wasClicking) {
            HitResult hitResult = mc.hitResult;
            if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
                Entity target = ((EntityHitResult) hitResult).getEntity();
                
                double x = target.getX();
                double y = target.getY();
                double z = target.getZ();
                float height = target.getBbHeight();
                
                for (int i = 0; i < 15; i++) {
                    double offsetX = (RANDOM.nextDouble() - 0.5) * 1.2;
                    double offsetZ = (RANDOM.nextDouble() - 0.5) * 1.2;
                    double offsetY = RANDOM.nextDouble() * height;
                    
                    double speedX = (RANDOM.nextDouble() - 0.5) * 0.25;
                    double speedY = RANDOM.nextDouble() * 0.15;
                    double speedZ = (RANDOM.nextDouble() - 0.5) * 0.25;
                    
                    mc.level.addParticle(ParticleTypes.CHERRY_LEAVES, x + offsetX, y + offsetY, z + offsetZ, speedX, speedY, speedZ);
                }
            }
        }
        wasClicking = isDown;

        long windowHandle = mc.getWindow().getWindow();
        
        boolean isKDown = GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_K) == GLFW.GLFW_PRESS;
        if (isKDown && !wasKeyKDown && mc.screen == null) {
            mc.setScreen(new RightConfigScreen()); 
        }
        wasKeyKDown = isKDown;

        boolean isJDown = GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_J) == GLFW.GLFW_PRESS;
        if (isJDown && !wasKeyJDown && mc.screen == null) {
            mc.setScreen(new LeftConfigScreen()); 
        }
        wasKeyJDown = isJDown;
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        
        ItemStack itemStack = event.getItemStack();
        if (itemStack.isEmpty()) return;

        PoseStack poseStack = event.getPoseStack();
        
        HumanoidArm mainArm = mc.player.getMainArm();
        HumanoidArm currentArm = (event.getHand() == InteractionHand.MAIN_HAND) ? mainArm : mainArm.getOpposite();
        
        if (currentArm == HumanoidArm.RIGHT) {
            poseStack.pushPose();
            poseStack.translate((double)RightHandConfig.rightX, (double)RightHandConfig.rightY, (double)RightHandConfig.rightZ);
            poseStack.scale(0.55f, 0.55f, 0.55f);
        } 
        else if (currentArm == HumanoidArm.LEFT) {
            poseStack.pushPose();
            poseStack.translate((double)LeftHandConfig.leftX, (double)LeftHandConfig.leftY, (double)LeftHandConfig.leftZ);
            poseStack.scale(0.275f, 0.275f, 0.275f);
        }
    }
}
