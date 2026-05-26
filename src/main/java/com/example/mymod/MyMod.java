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
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod("mymod")
public class MyMod {
    private static boolean wasClicking = false;
    private static boolean wasKeyKDown = false;
    private static boolean wasKeyJDown = false;
    
    private static final java.util.Random RANDOM = new java.util.Random();

    public MyMod(IEventBus modEventBus) {
        modEventBus.addListener(MyKeyBindings::registerKeys);
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new MyFire());
        NeoForge.EVENT_BUS.register(new MyHud()); 
    }

    private ParticleOptions getSelectedParticle(int mode) {
        if (mode == 1) return ParticleTypes.HEART;           
        if (mode == 2) return ParticleTypes.FLAME;           
        if (mode == 3) return ParticleTypes.WITCH;           
        if (mode == 4) return ParticleTypes.SOUL_FIRE_FLAME; 
        return ParticleTypes.END_ROD;                        
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
                
                ParticleOptions particle = getSelectedParticle(RightHandConfig.particleMode);
                
                // Спавним ровно 50 мощных PvP-частиц выбранного типа
                for (int i = 0; i < 50; i++) {
                    double offsetX = (RANDOM.nextDouble() - 0.5) * 1.5;
                    double offsetZ = (RANDOM.nextDouble() - 0.5) * 1.5;
                    double offsetY = RANDOM.nextDouble() * height;
                    
                    double speedX = (RANDOM.nextDouble() - 0.5) * 0.4;
                    double speedY = RANDOM.nextDouble() * 0.3;
                    double speedZ = (RANDOM.nextDouble() - 0.5) * 0.4;
                    
                    mc.level.addParticle(particle, x + offsetX, y + offsetY, z + offsetZ, speedX, speedY, speedZ);
                }
            }
        }
        wasClicking = isDown;
        
        boolean isKDown = MyKeyBindings.OPEN_RIGHT_CONFIG.isDown();
        if (isKDown && !wasKeyKDown && mc.screen == null) {
            mc.setScreen(new RightConfigScreen()); 
        }
        wasKeyKDown = isKDown;

        boolean isJDown = MyKeyBindings.OPEN_LEFT_CONFIG.isDown();
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

        // ИСПРАВЛЕНО ДЛЯ 1.21.4: Модифицируем нативную матрицу БЕЗ отмены ивента.
        // Это полностью возвращает уменьшение предметов, перемещение рук по осям и независимость K от J.
        if (currentArm == HumanoidArm.RIGHT) {
            poseStack.pushPose(); // Изолируем правую руку от левой
            
            // Применяем X, Y, Z калибровку и масштаб правой руки из меню K
            poseStack.translate((double)RightHandConfig.rightX, (double)RightHandConfig.rightY, (double)RightHandConfig.rightZ);
            poseStack.scale(0.55f, 0.55f, 0.55f);

            // Анимация идеального прокрута на месте
            if (RightHandConfig.swingMode == 1 && swingProgress > 0.0f) {
                poseStack.translate(0.0D, (double)(swingProgress * 0.4F), (double)(swingProgress * 0.1F));
                poseStack.mulPose(Axis.XP.rotationDegrees(swingProgress * 40.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(-swingProgress * 20.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(swingProgress * 360.0f));
            }
            
            poseStack.popPose(); // Сбрасываем изменения матрицы, чтобы они не улетели на левую руку
        } 
        else if (currentArm == HumanoidArm.LEFT) {
            poseStack.pushPose(); // Изолируем левую руку от правой
            
            // Применяем X, Y, Z калибровку и уменьшенный в два раза масштаб левой руки из меню K
            poseStack.translate((double)RightHandConfig.leftX, (double)RightHandConfig.leftY, (double)RightHandConfig.leftZ);
            poseStack.scale(0.275f, 0.275f, 0.275f);
            
            poseStack.popPose(); // Сбрасываем изменения матрицы
        }
    }
}
