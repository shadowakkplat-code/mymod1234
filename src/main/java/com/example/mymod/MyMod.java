package com.example.mymod;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
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
        if (mc.player == null || mc.level == null) return;
        
        ItemStack itemStack = event.getItemStack();
        if (itemStack.isEmpty()) return;

        PoseStack poseStack = event.getPoseStack();
        InteractionHand hand = event.getHand();
        
        HumanoidArm mainArm = mc.player.getMainArm();
        HumanoidArm currentArm = (hand == InteractionHand.MAIN_HAND) ? mainArm : mainArm.getOpposite();
        
        float swingProgress = event.getSwingProgress();

        if (currentArm == HumanoidArm.RIGHT) {
            poseStack.translate((double)RightHandConfig.rightX, (double)RightHandConfig.rightY, (double)RightHandConfig.rightZ);
            poseStack.scale(0.55f, 0.55f, 0.55f);

            if (swingProgress > 0.0f) {
                // ИСПРАВЛЕНО: Заменили Math.isFinite на Float.isFinite
                float f = Float.isFinite(swingProgress) ? (float) Math.sin((double) (swingProgress * (float) Math.PI)) : 0.0f;
                
                switch (RightHandConfig.swingMode) {
                    case 1: // Прокрут (360 градусов)
                        poseStack.mulPose(Axis.ZP.rotationDegrees(swingProgress * 360.0f));
                        break;
                    case 2: // Олдскул 1.7
                        poseStack.translate(0.0D, (double)(f * 0.15F), 0.0D);
                        poseStack.mulPose(Axis.XP.rotationDegrees(-f * 30.0F));
                        break;
                    case 3: // Короткий PvP-удар
                        poseStack.translate(0.0D, 0.0D, (double)(f * 0.1F));
                        break;
                    case 4: // Боковой взмах
                        poseStack.mulPose(Axis.YP.rotationDegrees(f * 40.0F));
                        break;
                    case 5: // Снизу вверх
                        poseStack.translate(0.0D, (double)(f * 0.2F), 0.0D);
                        poseStack.mulPose(Axis.XP.rotationDegrees(f * 20.0F));
                        break;
                    case 6: // Тяжелый замах
                        poseStack.mulPose(Axis.ZP.rotationDegrees(-f * 45.0F));
                        poseStack.mulPose(Axis.XP.rotationDegrees(-f * 15.0F));
                        break;
                }
            }
        } 
        else if (currentArm == HumanoidArm.LEFT) {
            poseStack.translate((double)LeftHandConfig.leftX, (double)LeftHandConfig.leftY, (double)LeftHandConfig.leftZ);
            poseStack.scale(0.275f, 0.275f, 0.275f);
        }
    }
}
