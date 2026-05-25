package com.example.mymod;

import java.lang.reflect.Method;
import net.neoforged.bus.api.SubscribeEvent;

public class MyArmor {
    
    @SubscribeEvent
    public void onRenderGui(net.neoforged.neoforge.client.event.ScreenEvent.Render.Post event) {
        try {
            Class<?> mcClass = Class.forName("net.minecraft.client.Minecraft");
            Object mc = mcClass.getMethod("getInstance").invoke(null);
            
            if (mcClass.getField("screen").get(mc) != null) return;
            Object player = mcClass.getField("player").get(mc);
            
            if (player != null) {
                Object window = mcClass.getMethod("getWindow").invoke(mc);
                int screenWidth = (int) window.getClass().getMethod("getGuiScaledWidth").invoke(window);
                int screenHeight = (int) window.getClass().getMethod("getGuiScaledHeight").invoke(window);
                Object graphics = event.getGuiGraphics();
                
                int left = screenWidth / 2 + 91;
                int top = screenHeight - 49;
                
                Class<?> esClass = Class.forName("net.minecraft.world.entity.EquipmentSlot");
                Object[] slots = {
                    esClass.getField("FEET").get(null), esClass.getField("LEGS").get(null),
                    esClass.getField("CHEST").get(null), esClass.getField("HEAD").get(null)
                };
                
                Class<?> isClass = Class.forName("net.minecraft.world.item.ItemStack");
                int currentX = left - 9;

                for (Object slot : slots) {
                    Object armorStack = player.getClass().getMethod("getItemBySlot", esClass).invoke(player, slot);
                    boolean isEmpty = (boolean) armorStack.getClass().getMethod("isEmpty").invoke(armorStack);
                    
                    if (!isEmpty) {
                        Object pose = graphics.getClass().getMethod("pose").invoke(graphics);
                        pose.getClass().getMethod("pushPose").invoke(pose);
                        pose.getClass().getMethod("translate", float.class, float.class, float.class).invoke(pose, (float)currentX, (float)top, 0.0f);
                        pose.getClass().getMethod("scale", float.class, float.class, float.class).invoke(pose, 0.72f, 0.72f, 0.72f);
                        
                        graphics.getClass().getMethod("renderItem", isClass, int.class, int.class).invoke(graphics, armorStack, 0, 0);
                        pose.getClass().getMethod("popPose").invoke(pose);
                        
                        currentX -= 16;
                    }
                }
            }
        } catch (Exception ignored) {}
    }
}

// Перекодированный экран настроек калибровки без использования старых конструкторов
class ConfigScreen extends net.minecraft.client.gui.screens.Screen {
    protected ConfigScreen() {
        super(net.minecraft.network.chat.Component.literal("Sword Position Configuration"));
    }

    @Override
    protected void init() {
        try {
            Class<?> compClass = Class.forName("net.minecraft.network.chat.Component");
            Class<?> buttonClass = Class.forName("net.minecraft.client.gui.components.Button");
            Method builderMethod = buttonClass.getMethod("builder", compClass, buttonClass.getDeclaredClasses()[0]);

            // Кнопка 1: ▲ Higher (Y + 0.05)
            Object b1Text = compClass.getMethod("literal", String.class).invoke(null, "▲ Higher");
            Object builder1 = builderMethod.invoke(null, b1Text, (net.minecraft.client.gui.components.Button.OnPress) b -> MyMod.swordY += 0.05f);
            builder1.getClass().getMethod("bounds", int.class, int.class, int.class, int.class).invoke(builder1, this.width / 2 - 50, this.height / 2 - 60, 100, 20);
            this.addRenderableWidget((net.minecraft.client.gui.components.Renderable) builder1.getClass().getMethod("build").invoke(builder1));

            // Кнопка 2: ▼ Lower (Y - 0.05)
            Object b2Text = compClass.getMethod("literal", String.class).invoke(null, "▼ Lower");
            Object builder2 = builderMethod.invoke(null, b2Text, (net.minecraft.client.gui.components.Button.OnPress) b -> MyMod.swordY -= 0.05f);
            builder2.getClass().getMethod("bounds", int.class, int.class, int.class, int.class).invoke(builder2, this.width / 2 - 50, this.height / 2 - 35, 100, 20);
            this.addRenderableWidget((net.minecraft.client.gui.components.Renderable) builder2.getClass().getMethod("build").invoke(builder2));

            // Кнопка 3: ✦ Further (Z - 0.05)
            Object b3Text = compClass.getMethod("literal", String.class).invoke(null, "✦ Further");
            Object builder3 = builderMethod.invoke(null, b3Text, (net.minecraft.client.gui.components.Button.OnPress) b -> MyMod.swordZ -= 0.05f);
            builder3.getClass().getMethod("bounds", int.class, int.class, int.class, int.class).invoke(builder3, this.width / 2 - 50, this.height / 2 + 0, 100, 20);
            this.addRenderableWidget((net.minecraft.client.gui.components.Renderable) builder3.getClass().getMethod("build").invoke(builder3));

            // Кнопка 4: ⏳ Closer (Z + 0.05)
            Object b4Text = compClass.getMethod("literal", String.class).invoke(null, "⏳ Closer");
            Object builder4 = builderMethod.invoke(null, b4Text, (net.minecraft.client.gui.components.Button.OnPress) b -> MyMod.swordZ += 0.05f);
            builder4.getClass().getMethod("bounds", int.class, int.class, int.class, int.class).invoke(builder4, this.width / 2 - 50, this.height / 2 + 25, 100, 20);
            this.addRenderableWidget((net.minecraft.client.gui.components.Renderable) builder4.getClass().getMethod("build").invoke(builder4));

            // Кнопка 5: ✔ Save & Close
            Object b5Text = compClass.getMethod("literal", String.class).invoke(null, "✔ Save & Close");
            Object builder5 = builderMethod.invoke(null, b5Text, (net.minecraft.client.gui.components.Button.OnPress) b -> this.minecraft.setScreen(null));
            builder5.getClass().getMethod("bounds", int.class, int.class, int.class, int.class).invoke(builder5, this.width / 2 - 50, this.height / 2 + 65, 100, 20);
            this.addRenderableWidget((net.minecraft.client.gui.components.Renderable) builder5.getClass().getMethod("build").invoke(builder5));

        } catch (Exception ignored) {}
    }

    @Override
    public void render(net.minecraft.client.gui.GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, "⚔ PvP Client - Calibration Menu ⚔", this.width / 2, this.height / 2 - 85, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }
}
