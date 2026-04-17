package com.example.iteminfo.mixin;

import com.example.iteminfo.ItemInfoMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.item.TooltipType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> {

    @Inject(method = "drawForeground", at = @At("HEAD"))
    private void onDrawForeground(DrawContext context, int mouseX, int mouseY, CallbackInfo ci) {
        if (!ItemInfoMod.isKeyHeld()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) return;

        HandledScreen<?> screen = (HandledScreen<?>) (Object) this;
        ScreenHandler handler = screen.getScreenHandler();

        for (Slot slot : handler.slots) {
            if (!slot.hasStack()) continue;
            ItemStack stack = slot.getStack();

            try {
                // Voller Tooltip: Name + Verzauberungen + alles andere
                List<Text> tooltip = stack.getTooltip(
                    Item.TooltipContext.create(client.world),
                    client.player,
                    TooltipType.BASIC
                );
                if (tooltip.isEmpty()) continue;

                // Max. 4 Zeilen (Name + 3 weitere Infos)
                List<Text> lines = tooltip.subList(0, Math.min(tooltip.size(), 4));

                // Breite berechnen
                int maxWidth = 0;
                for (Text line : lines) {
                    int w = client.textRenderer.getWidth(line);
                    if (w > maxWidth) maxWidth = w;
                }

                int padding = 4;
                int lineHeight = 10;
                int boxW = maxWidth + padding * 2;
                int boxH = lines.size() * lineHeight + padding;
                int boxX = slot.x + 8 - boxW / 2;
                int boxY = slot.y - boxH - 2;

                // Hintergrund
                context.fill(boxX, boxY, boxX + boxW, boxY + boxH, 0xCC000000);

                // Zeilen zeichnen
                for (int i = 0; i < lines.size(); i++) {
                    context.drawTextWithShadow(
                        client.textRenderer,
                        lines.get(i),
                        boxX + padding,
                        boxY + padding / 2 + i * lineHeight,
                        0xFFFFFF
                    );
                }
            } catch (Exception ignored) {}
        }
    }
}
