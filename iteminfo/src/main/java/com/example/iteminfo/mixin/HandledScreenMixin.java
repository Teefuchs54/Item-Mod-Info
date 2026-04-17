package com.example.iteminfo.mixin;

import com.example.iteminfo.ItemInfoMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> {

    @Inject(method = "drawForeground", at = @At("HEAD"))
    private void onDrawForeground(DrawContext context, int mouseX, int mouseY, CallbackInfo ci) {
        if (!ItemInfoMod.isKeyHeld()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;

        HandledScreen<?> screen = (HandledScreen<?>) (Object) this;
        ScreenHandler handler = screen.getScreenHandler();

        for (Slot slot : handler.slots) {
            if (!slot.hasStack()) continue;

            ItemStack stack = slot.getStack();
            Text name = stack.getName();
            int textWidth = client.textRenderer.getWidth(name);
            int slotCX = slot.x + 8;
            int slotTY = slot.y - 1;

            // Dunkler Hintergrund
            context.fill(slotCX - textWidth / 2 - 2, slotTY - 10,
                         slotCX + textWidth / 2 + 2, slotTY - 1,
                         0xAA000000);

            // Item-Name
            context.drawCenteredTextWithShadow(
                    client.textRenderer, name, slotCX, slotTY - 9, 0xFFFFFF);
        }
    }
}
