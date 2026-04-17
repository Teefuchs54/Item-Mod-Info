package com.example.iteminfo.mixin;

import com.example.iteminfo.ItemInfoMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> {

    // Kein @Shadow mehr - das verursachte den Fehler!
    // drawForeground wird bereits mit verschobenem Koordinatensystem aufgerufen,
    // daher brauchen wir x/y nicht separat.

    @Inject(method = "drawForeground", at = @At("HEAD"))
    private void onDrawForeground(DrawContext context, int mouseX, int mouseY, CallbackInfo ci) {

        if (!ItemInfoMod.isKeyHeld()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;

        // Handler über public Methode holen - kein @Shadow nötig
        HandledScreen<?> screen = (HandledScreen<?>) (Object) this;
        ScreenHandler handler = screen.getScreenHandler();

        for (Slot slot : handler.slots) {
            if (!slot.hasStack()) continue;

            ItemStack stack = slot.getStack();

            // Zeilen: Name + Verzauberungen
            List<Text> lines = new ArrayList<>();
            lines.add(stack.getName());

            ItemEnchantmentsComponent enchantments = stack.getEnchantments();
            for (RegistryEntry<Enchantment> entry : enchantments.getEnchantments()) {
                int level = enchantments.getLevel(entry);
                lines.add(Enchantment.getName(entry, level));
            }

            // In drawForeground sind die Koordinaten bereits um (screenX, screenY) verschoben
            int cx   = slot.x + 8;
            int topY = slot.y;

            int lh     = client.textRenderer.fontHeight + 1;
            int totalH = lines.size() * lh + 3;
            int maxW   = 0;
            for (Text t : lines) maxW = Math.max(maxW, client.textRenderer.getWidth(t));

            int boxTop = topY - totalH - 1;

            // Hintergrund
            context.fill(cx - maxW / 2 - 2, boxTop, cx + maxW / 2 + 2, topY - 1, 0xAA000000);

            // Text zeichnen
            for (int i = 0; i < lines.size(); i++) {
                int color = (i == 0) ? switch (stack.getRarity()) {
                    case UNCOMMON -> 0xFFFF55;
                    case RARE     -> 0x55FFFF;
                    case EPIC     -> 0xFF55FF;
                    default       -> 0xFFFFFF;
                } : 0xFFD700;
                context.drawCenteredTextWithShadow(
                    client.textRenderer, lines.get(i), cx, boxTop + 2 + i * lh, color);
            }
        }
    }
}
