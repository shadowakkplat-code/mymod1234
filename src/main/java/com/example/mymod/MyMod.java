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

    // Метод спавна 8 разных частиц в зависимости от выбранного набора (0-4)
    private void spawnComboParticles(Minecraft mc, Entity target, float height, int mode) {
        double x = target.getX();
        double y = target.getY();
        double z = target.getZ();

        // ИСПРАВЛЕНО: Заменили ненайденные частицы на проверенные FIREWORK и DRIPPING_WATER
        ParticleOptions[][] particleCombos = {
            // Набор 1: Мистический Эндер
            { ParticleTypes.END_ROD, ParticleTypes.PORTAL, ParticleTypes.REVERSE_PORTAL, ParticleTypes.DRAGON_BREATH, ParticleTypes.CHERRY_LEAVES, ParticleTypes.ENCHANT, ParticleTypes.SQUID_INK, ParticleTypes.GLOW },
            // Набор 2: Крит и Сердца
            { ParticleTypes.HEART, ParticleTypes.CRIT, ParticleTypes.ENCHANTED_HIT, ParticleTypes.DAMAGE_INDICATOR, ParticleTypes.ANGRY_VILLAGER, ParticleTypes.HAPPY_VILLAGER, ParticleTypes.FIREWORK, ParticleTypes.SNOWFLAKE },
            // Набор 3: Огненный Ад
            { ParticleTypes.FLAME, ParticleTypes.SMALL_FLAME, ParticleTypes.LAVA, ParticleTypes.SOUL_FIRE_FLAME, ParticleTypes.SMOKE, ParticleTypes.LARGE_SMOKE, ParticleTypes.SOUL, ParticleTypes.CAMPFIRE_COSY_SMOKE },
            // Набор 4: Проклятие Ведьмы
            { ParticleTypes.WITCH, ParticleTypes.POOF, ParticleTypes.BUBBLE, ParticleTypes.RAIN, ParticleTypes.MYCELIUM, ParticleTypes.EFFECT, ParticleTypes.INSTANT_EFFECT, ParticleTypes.WHITE_SMOKE },
            // Набор 5: Хранитель Душ
            { ParticleTypes.SOUL_FIRE_FLAME, ParticleTypes.SOUL, ParticleTypes.SCULK_SOUL, ParticleTypes.SCULK_CHARGE_POP, ParticleTypes.DRIPPING_WATER, ParticleTypes.GLOW_SQUID_INK, ParticleTypes.UNDERWATER, ParticleTypes.WHITE_ASH }
        };

        int selectedIndex = (mode >= 0 && mode < 5) ? mode : 0;
        ParticleOptions[] currentCombo = particleCombos[selectedIndex];

        // Спавним 12 частиц на один удар
        for (int i = 0; i < 12; i++) {
            double offsetX = (RANDOM.nextDouble() - 0.5) * 1.2;
            double offsetZ = (RANDOM.nextDouble() - 0.5) * 1.2;
            double offsetY = RANDOM.nextDouble() * height;
            
            double speedX = (RANDOM.nextDouble() - 0.5) * 0.25;
            double speedY = RANDOM.nextDouble() * 0.15;
            double speedZ = (RANDOM.nextDouble() - 0.5) * 0.25;
            
            ParticleOptions randomParticleFromCombo = currentCombo[RANDOM.nextInt(8)];
            mc.level.addParticle(randomParticleFromCombo, x + offsetX, y + offsetY, z + offsetZ, speedX, speedY, speedZ);
        }
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
                spawnComboParticles(mc, target, target.getBbHeight(), RightHandConfig.particleMode);
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
        InteractionHand hand = event.getHand();
        
        HumanoidArm mainArm = mc.player.getMainArm();
        HumanoidArm currentArm = (hand == InteractionHand.MAIN_HAND) ? mainArm : mainArm.getOpposite();
        
        float swingProgress = event.getSwingProgress();

        if (currentArm == HumanoidArm.RIGHT) {
            poseStack.translate((double)RightHandConfig.rightX, (double)RightHandConfig.rightY, (double)RightHandConfig.rightZ);
            poseStack.scale(0.55f, 0.55f, 0.55f);

            if (RightHandConfig.swingMode == 1 && swingProgress > 0.0f) {
                poseStack.translate(0.0D, (double)(swingProgress * 0.4F), (double)(swingProgress * 0.1F));
                poseStack.mulPose(Axis.XP.rotationDegrees(swingProgress * 40.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(-swingProgress * 20.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(swingProgress * 360.0f));
            }
        } 
        else if (currentArm == HumanoidArm.LEFT) {
            poseStack.translate((double)RightHandConfig.leftX, (double)RightHandConfig.leftY, (double)RightHandConfig.leftZ);
            poseStack.scale(0.275f, 0.275f, 0.275f);
        }
    }
}
