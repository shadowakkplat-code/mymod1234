package com.example.mymod;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
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
    
    private static final ParticleOptions[] PARTICLE_REGISTRY = {
        ParticleTypes.END_ROD, ParticleTypes.PORTAL, ParticleTypes.REVERSE_PORTAL, ParticleTypes.DRAGON_BREATH, ParticleTypes.CHERRY_LEAVES, ParticleTypes.ENCHANT, ParticleTypes.SQUID_INK, ParticleTypes.GLOW,
        ParticleTypes.HEART, ParticleTypes.CRIT, ParticleTypes.ENCHANTED_HIT, ParticleTypes.DAMAGE_INDICATOR, ParticleTypes.ANGRY_VILLAGER, ParticleTypes.HAPPY_VILLAGER, ParticleTypes.FIREWORK, ParticleTypes.SNOWFLAKE,
        ParticleTypes.FLAME, ParticleTypes.SMALL_FLAME, ParticleTypes.LAVA, ParticleTypes.SOUL_FIRE_FLAME, ParticleTypes.SMOKE, ParticleTypes.LARGE_SMOKE, ParticleTypes.SOUL, ParticleTypes.CAMPFIRE_COSY_SMOKE,
        ParticleTypes.WITCH, ParticleTypes.POOF, ParticleTypes.BUBBLE, ParticleTypes.RAIN, ParticleTypes.MYCELIUM, ParticleTypes.EFFECT, ParticleTypes.INSTANT_EFFECT, ParticleTypes.WHITE_SMOKE,
        ParticleTypes.SOUL_FIRE_FLAME, ParticleTypes.SOUL, ParticleTypes.SCULK_SOUL, ParticleTypes.SCULK_CHARGE_POP, ParticleTypes.DRIPPING_WATER, ParticleTypes.GLOW_SQUID_INK, ParticleTypes.UNDERWATER, ParticleTypes.WHITE_ASH,
        ParticleTypes.FIREWORK, ParticleTypes.CLOUD, ParticleTypes.EXPLOSION, ParticleTypes.POOF, ParticleTypes.SPORE_BLOSSOM_AIR, ParticleTypes.FALLING_WATER, ParticleTypes.FALLING_LAVA, ParticleTypes.NOTE,
        ParticleTypes.ASH, ParticleTypes.BUBBLE_POP, ParticleTypes.PORTAL, ParticleTypes.CRIMSON_SPORE, ParticleTypes.WARPED_SPORE, ParticleTypes.FALLING_LAVA, ParticleTypes.FALLING_WATER, ParticleTypes.GLOW
    };

    public MyMod(IEventBus modEventBus) {
        modEventBus.addListener(MyKeyBindings::registerKeys);
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
                
                int id = RightHandConfig.activeParticleId;
                ParticleOptions selectedParticle = (id >= 0 && id < 56) ? PARTICLE_REGISTRY[id] : ParticleTypes.END_ROD;
                
                for (int i = 0; i < 12; i++) {
                    double offsetX = (RANDOM.nextDouble() - 0.5) * 1.2;
                    double offsetZ = (RANDOM.nextDouble() - 0.5) * 1.2;
                    double offsetY = RANDOM.nextDouble() * height;
                    
                    double speedX = (RANDOM.nextDouble() - 0.5) * 0.25;
                    double speedY = RANDOM.nextDouble() * 0.15;
                    double speedZ = (RANDOM.nextDouble() - 0.5) * 0.25;
                    
                    mc.level.addParticle(selectedParticle, x + offsetX, y + offsetY, z + offsetZ, speedX, speedY, speedZ);
                }
            }
        }
        wasClicking = isDown;
        
        boolean isKDown = MyKeyBindings.OPEN_RIGHT_CONFIG.isDown();
        if (isKDown && !wasKeyKDown && mc.screen == null) { mc.setScreen(new RightConfigScreen()); }
        wasKeyKDown = isKDown;

        boolean isJDown = MyKeyBindings.OPEN_LEFT_CONFIG.isDown();
        if (isJDown && !wasKeyJDown && mc.screen == null) { mc.setScreen(new LeftConfigScreen()); }
        wasKeyJDown = isJDown;
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        
        ItemStack itemStack = event.getItemStack();
        if (itemStack.isEmpty()) return;

        PoseStack poseStack = event.getPoseStack();
        
        HumanoidArm mainArm = mc.player.getMainArm();
        HumanoidArm currentArm = (event.getHand() == InteractionHand.MAIN_HAND) ? mainArm : mainArm.getOpposite();
        
        float swingProgress = event.getSwingProgress();

        if (currentArm == HumanoidArm.RIGHT) {
            float rightScaleMultiplier = 1.0f - (RightHandConfig.rightScalePercent / 100.0f);
            
            poseStack.translate((double)RightHandConfig.rightX, (double)RightHandConfig.rightY, (double)RightHandConfig.rightZ);
            poseStack.scale(rightScaleMultiplier, rightScaleMultiplier, rightScaleMultiplier);

            if (RightHandConfig.swingMode == 1 && swingProgress > 0.0f) {
                poseStack.translate(0.0D, (double)(swingProgress * 0.4F), (double)(swingProgress * 0.1F));
                poseStack.mulPose(Axis.XP.rotationDegrees(swingProgress * 40.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(-swingProgress * 20.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(-swingProgress * 360.0f)); 
            }
        } 
        else if (currentArm == HumanoidArm.LEFT) {
            float leftScaleMultiplier = 1.0f - (RightHandConfig.leftScalePercent / 100.0f);
            
            poseStack.translate((double)RightHandConfig.leftX, (double)RightHandConfig.leftY, (double)RightHandConfig.leftZ);
            poseStack.scale(leftScaleMultiplier, leftScaleMultiplier, leftScaleMultiplier);
        }
    }
}
