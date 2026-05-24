package com.example.mymod;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderBlockScreenEffectEvent;
import net.neoforged.neoforge.client.event.RenderGuiOverlayEvent;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

@Mod("mymod")
public class MyMod {
    public MyMod() {
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new ClientFoodTracker());
        NeoForge.EVENT_BUS.register(new ClientActionEvents());
        NeoForge.EVENT_BUS.register(new ModClientEvents());
        NeoForge.EVENT_BUS.register(new ModSakuraParticles());
    }

    public static class ClientFoodTracker {
        public static float localSaturation = 0.0f;
        private static int lastFoodLevel = 20;

        public static void onFoodEaten(ItemStack stack) {
            if (stack.getItem().isEdible()) {
                // В 1.21.4 компоненты еды извлекаются через метод .get()
                FoodProperties props = stack.get(DataComponents.FOOD);
                if (props != null) {
                    localSaturation = Math.min(20.0f, localSaturation + (props.nutrition() * 2.0f * props.saturation()));
                }
            }
        }

        @SubscribeEvent
        public void tick(ClientTickEvent.Post event) {
            Player player = Minecraft.getInstance().player;
            if (player != null && player.tickCount % 20 == 0) {
                int currentFood = player.getFoodData().getFoodLevel();
                if (currentFood < lastFoodLevel) localSaturation = 0.0f;
                lastFoodLevel = currentFood;
                if (localSaturation > 0) localSaturation -= 0.005f;
                else localSaturation = 0.0f;
            }
        }
    }

    public static class ClientActionEvents {
        @SubscribeEvent
        public void onRenderFireOverlay(RenderBlockScreenEffectEvent event) {
            if (event.getOverlayType() == RenderBlockScreenEffectEvent.OverlayType.FIRE) {
                event.getPoseStack().translate(0.0D, -0.35D, 0.0D);
            }
        }

        @SubscribeEvent
        public void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
            if (event.getEntity() instanceof Player && event.getEntity().level().isClientSide() && event.getEntity() == Minecraft.getInstance().player) {
                ClientFoodTracker.onFoodEaten(event.getItem());
            }
        }
    }

    public static class ModClientEvents {
        private static final ResourceLocation ICONS = ResourceLocation.withDefaultNamespace("textures/gui/icons.png");

        @SubscribeEvent
        public void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
            if (event.getOverlay().id().equals(VanillaGuiOverlay.FOOD_LEVEL.id())) {
                Minecraft mc = Minecraft.getInstance();
                Player player = mc.player;
                if (player != null && !player.isSpectator()) {
                    GuiGraphics graphics = event.getGuiGraphics();
                    int left = event.getWindow().getGuiScaledWidth() / 2 + 91;
                    int top = event.getWindow().getGuiScaledHeight() - 39;

                    RenderSystem.setShaderTexture(0, ICONS);
                    for (int i = 0; i < 10; ++i) {
                        if (ClientFoodTracker.localSaturation > i * 2) {
                            int x = left - i * 8 - 9;
                            if (ClientFoodTracker.localSaturation - (i * 2) >= 2) graphics.blit(ICONS, x, top, 16, 27, 9, 9);
                            else graphics.blit(ICONS, x, top, 25, 27, 9, 9);
                        }
                    }

                    int armorTop = top - 12;
                    int currentX = left - 9;
                    EquipmentSlot[] slots = {EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};

                    for (EquipmentSlot slot : slots) {
                        ItemStack armor = player.getItemBySlot(slot);
                        if (!armor.isEmpty()) {
                            graphics.pose().pushPose();
                            graphics.pose().translate(currentX, armorTop - 1, 0);
                            graphics.pose().scale(0.7f, 0.7f, 0.7f);
                            graphics.renderFakeItem(armor, 0, 0);
                            graphics.pose().popPose();

                            if (armor.isDamageableItem()) {
                                int dur = armor.getMaxDamage() - armor.getDamageValue();
                                int color = 0xFFFFFFFF;
                                if ((float)dur/armor.getMaxDamage() < 0.25f) color = 0xFFFF5555;
                                graphics.pose().pushPose();
                                graphics.pose().translate(currentX - 16, armorTop, 0);
                                graphics.pose().scale(0.6f, 0.6f, 0.6f);
                                graphics.drawString(mc.font, String.valueOf(dur), 0, 2, color, true);
                                graphics.pose().popPose();
                            }
                            currentX -= 26;
                        }
                    }
                }
            }
        }
    }

    public static class ModSakuraParticles {
        @SubscribeEvent
        public void onMouseClick(InputEvent.MouseButtonPressed.Pre event) {
            Minecraft mc = Minecraft.getInstance();
            if (event.getButton() == 0 && mc.player != null && mc.level != null && mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.ENTITY) {
                net.minecraft.world.entity.Entity target = ((EntityHitResult) mc.hitResult).getEntity();
                for (int i = 0; i < 15; i++) {
                    mc.level.addParticle(ParticleTypes.CHERRY_LEAVES, target.getX(), target.getY() + (target.getBbHeight()/2.0), target.getZ(), (mc.level.random.nextDouble()-0.5)*0.2, mc.level.random.nextDouble()*0.2, (mc.level.random.nextDouble()-0.5)*0.2);
                }
            }
        }
    }
}

