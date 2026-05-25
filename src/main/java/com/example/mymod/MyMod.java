package com.example.mymod;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.particles.ParticleTypes;
import net.neoforged.bus.api.SubscribeEvent;
import net.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;

@Mod("mymod")
public class MyMod {
    private static boolean wasClicking = false;
    private static boolean wasKeyKDown = false;

    public MyMod() {
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new MyFire());
        NeoForge.EVENT_BUS.register(new MyHud()); 
        // Класс MyDamage полностью удален из регистрации
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
                
                java.util.Random r = new java.util.Random();
                for (int i = 0; i < 15; i++) {
                    double offsetX = (r.nextDouble() - 0.5) * 1.2;
                    double offsetZ = (r.nextDouble() - 0.5) * 1.2;
                    double offsetY = r.nextDouble() * height;
                    
                    double speedX = (r.nextDouble() - 0.5) * 0.25;
                    double speedY = r.nextDouble() * 0.15;
                    double speedZ = (r.nextDouble() - 0.5) * 0.25;
                    
                    mc.level.addParticle(ParticleTypes.CHERRY_LEAVES, x + offsetX, y + offsetY, z + offsetZ, speedX, speedY, speedZ);
                }
            }
        }
        wasClicking = isDown;

        long windowHandle = mc.getWindow().getWindow();
        boolean isKDown = GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_K) == GLFW.GLFW_PRESS;
        
        if (isKDown && !wasKeyKDown && mc.screen == null) {
            mc.setScreen(new ConfigScreen()); 
        }
        wasKeyKDown = isKDown;
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.isEmpty()) return;

        String itemName = itemStack.getItem().toString().toLowerCase();
        boolean isWeapon = itemName.contains("sword") || itemName.contains("axe") || itemName.contains("pickaxe");
        
        if (isWeapon) {
            PoseStack poseStack = event.getPoseStack();
            
            // ПРАВАЯ РУКА (MAIN_HAND) — Считывает строго RightHandConfig
            if (event.getHand() == InteractionHand.MAIN_HAND) {
                poseStack.translate(0.12D, (double)RightHandConfig.rightY, (double)RightHandConfig.rightZ);
                poseStack.scale(0.55f, 0.55f, 0.55f);
            } 
            // ЛЕВАЯ РУКА (OFF_HAND) — Считывает строго LeftHandConfig
            else if (event.getHand() == InteractionHand.OFF_HAND) {
                poseStack.translate(-0.45D, (double)LeftHandConfig.leftY, (double)LeftHandConfig.leftZ);
                poseStack.scale(0.275f, 0.275f, 0.275f);
            }
        }
    }
}
