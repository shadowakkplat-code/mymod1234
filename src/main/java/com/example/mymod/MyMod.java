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
import net.minecraft.core.particles.ParticleTypes; // Используем для END_ROD
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
                    
                    // ИСПРАВЛЕНО: Вместо сакуры теперь генерируются белые частицы эндер-палок
                    mc.level.addParticle(ParticleTypes.END_ROD, x + offsetX, y + offsetY, z + offsetZ, speedX, speedY, speedZ);
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

            // ОБРАБОТКА 5 КАСТОМНЫХ АНИМАЦИЙ ДЛЯ ПРАВОЙ РУКИ
            if (swingProgress > 0.0f) {
                float f = Float.isFinite(swingProgress) ? (float) Math.sin((double) (swingProgress * (float) Math.PI)) : 0.0f;
                
                switch (RightHandConfig.swingMode) {
                    case 1: // 2. Неподвижный меч
                        // Нейтрализуем ванильное опускание руки вниз: принудительно двигаем матрицу обратно вверх
                        poseStack.translate(0.0D, (double)(swingProgress * 0.4F), (double)(swingProgress * 0.1F));
                        // Компенсируем ванильное вращение замаха противоположным углом
                        poseStack.mulPose(Axis.XP.rotationDegrees(swingProgress * 40.0F));
                        poseStack.mulPose(Axis.YP.rotationDegrees(-swingProgress * 20.0F));
                        break;
                        
                    case 2: // 3. Аккуратный прокрут на 360 мечом вперед
                        // Плавно крутим меч по оси Z вперед (сглаживание через прогресс замаха)
                        poseStack.mulPose(Axis.ZP.rotationDegrees(swingProgress * 360.0f));
                        // Слегка подаем вперед по оси Z во время пика прокрута
                        poseStack.translate(0.0D, 0.0D, (double)(f * 0.15F));
                        break;
                        
                    case 3: // 4. Вниз вверх только правой рукой (Резкий вертикальный рубящий удар)
                        // Сначала опускаем по синусу, затем поднимаем, игнорируя боковые уклонения
                        poseStack.translate(0.0D, (double)(-f * 0.35F), (double)(f * 0.1F));
                        poseStack.mulPose(Axis.XP.rotationDegrees(-f * 25.0F));
                        break;
                        
                    case 4: // 5. Меч плавно поворачивается на 90 градусов и делает взмах вправо и влево
                        // Плавный разворот плашмя на 90 градусов на пике удара
                        poseStack.mulPose(Axis.YP.rotationDegrees(f * 90.0F));
                        // Дополнительное горизонтальное смещение влево/вправо по оси X
                        poseStack.translate((double)(-f * 0.25F), 0.0D, 0.0D);
                        poseStack.mulPose(Axis.ZP.rotationDegrees(-f * 20.0F));
                        break;
                        
                    default: // 1. Ванилла
                        // Ничего кастомного не делаем, работает стандартная анимация игры
                        break;
                }
            }
        } 
        else if (currentArm == HumanoidArm.LEFT) {
            // ЛЕВАЯ РУКА: Статично зафиксирована в уменьшенном масштабе, не реагирует на swingMode правой руки
            poseStack.translate((double)LeftHandConfig.leftX, (double)LeftHandConfig.leftY, (double)LeftHandConfig.leftZ);
            poseStack.scale(0.275f, 0.275f, 0.275f);
        }
    }
}
