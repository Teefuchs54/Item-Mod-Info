package com.example.iteminfo.mixin;

import com.example.iteminfo.ItemInfoMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Zeigt Item-Namen über allen Items im Inventar an,
 * solange die ALT-Taste gehalten wird.
 *
 * Funktioniert in ALLEN Inventar-Screens:
 * - Spieler-Inventar
 * - Truhen, Fässer
 * - Werkbank, Ofen
 * - Händler, usw.
 */
@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> {

    // Zugriff auf die Position des Inventar-Fensters (oben-links)
    @Shadow protected int x;
    @Shadow protected int y;

    // Zugriff auf den ScreenHandler (enthält alle Slots)
    @Shadow protected T handler;

    /**
     * Wird am Ende jedes Render-Frames aufgerufen.
     * Wenn ALT gehalten: Namen über allen Items zeichnen.
     */
    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY,
                          float delta, CallbackInfo ci) {

        // Nur wenn ALT gedrückt ist
        if (!ItemInfoMod.isKeyHeld()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;

        // Durch alle Slots des aktuellen Inventars gehen
        for (Slot slot : this.handler.slots) {
            if (!slot.hasStack()) continue;

            ItemStack stack = slot.getStack();

            // Bildschirmposition des Slots berechnen
            // (+8 um zur Mitte des 16x16 Slots zu kommen)
            int slotCenterX = this.x + slot.x + 8;
            int slotTopY    = this.y + slot.y - 1;

            // Item-Name holen
            Text name = stack.getName();

            // Farbe je nach Seltenheit (Rarity) des Items:
            // Weiß = Common, Gelb = Uncommon, Hellblau = Rare, Lila = Epic
            int color = switch (stack.getRarity()) {
                case UNCOMMON -> Formatting.YELLOW.getColorValue()       != null
                        ? Formatting.YELLOW.getColorValue()       : 0xFFFF55;
                case RARE     -> Formatting.AQUA.getColorValue()         != null
                        ? Formatting.AQUA.getColorValue()         : 0x55FFFF;
                case EPIC     -> Formatting.LIGHT_PURPLE.getColorValue() != null
                        ? Formatting.LIGHT_PURPLE.getColorValue() : 0xFF55FF;
                default       -> 0xFFFFFF; // Weiß für normale Items
            };

            // Breite des Textes berechnen für den Hintergrund
            int textWidth = client.textRenderer.getWidth(name);

            // Dunkler Hintergrund damit Text auf jedem Hintergrund lesbar ist
            context.fill(
                slotCenterX - textWidth / 2 - 2,  slotTopY - 10,
                slotCenterX + textWidth / 2 + 2,  slotTopY - 1,
                0xAA000000  // Semi-transparentes Schwarz
            );

            // Item-Name zentriert über dem Slot zeichnen
            context.drawCenteredTextWithShadow(
                client.textRenderer,
                name,
                slotCenterX,
                slotTopY - 9,
                color
            );
        }
    }
}
