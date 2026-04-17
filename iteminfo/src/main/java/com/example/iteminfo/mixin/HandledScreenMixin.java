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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 
import java.util.ArrayList;
import java.util.List;
 
@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> {
 
    @Shadow protected int x;
    @Shadow protected int y;
    @Shadow protected T handler;
 
    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY,
                          float delta, CallbackInfo ci) {
 
        if (!ItemInfoMod.isKeyHeld()) return;
 
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;
 
        for (Slot slot : this.handler.slots) {
            if (!slot.hasStack()) continue;
 
            ItemStack stack = slot.getStack();
 
            // Zeilen sammeln: Name + Verzauberungen
            List<Text> lines = new ArrayList<>();
            lines.add(stack.getName());
 
            // Verzauberungen direkt aus der Data-Component lesen (kein TooltipType nötig)
            ItemEnchantmentsComponent enchantments = stack.getEnchantments();
            for (RegistryEntry<Enchantment> entry : enchantments.getEnchantments()) {
                int level = enchantments.getLevel(entry);
                lines.add(Enchantment.getName(entry, level));
            }
 
            // Slot-Mittelpunkt auf dem Bildschirm
            int slotCenterX = this.x + slot.x + 8;
            int slotTopY    = this.y + slot.y;
 
            int lineHeight  = client.textRenderer.fontHeight + 1;
            int totalHeight = lines.size() * lineHeight + 3;
 
            // Breiteste Zeile ermitteln
            int maxWidth = 0;
            for (Text line : lines) {
                int w = client.textRenderer.getWidth(line);
                if (w > maxWidth) maxWidth = w;
            }
 
            int boxTop = slotTopY - totalHeight - 1;
 
            // Dunkler Hintergrund
            context.fill(
                slotCenterX - maxWidth / 2 - 2, boxTop,
                slotCenterX + maxWidth / 2 + 2, slotTopY - 1,
                0xAA000000
            );
 
            // Jede Zeile zeichnen
            for (int i = 0; i < lines.size(); i++) {
                int lineY = boxTop + 2 + i * lineHeight;
                int color;
                if (i == 0) {
                    // Item-Name: Farbe je nach Seltenheit
                    color = switch (stack.getRarity()) {
                        case UNCOMMON -> 0xFFFF55;
                        case RARE     -> 0x55FFFF;
                        case EPIC     -> 0xFF55FF;
                        default       -> 0xFFFFFF;
                    };
                } else {
                    // Verzauberungen: Gold
                    color = 0xFFD700;
                }
                context.drawCenteredTextWithShadow(
                    client.textRenderer,
                    lines.get(i),
                    slotCenterX,
                    lineY,
                    color
                );
            }
        }
    }
}
 
