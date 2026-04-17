package com.example.iteminfo;
 
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
@Environment(EnvType.CLIENT)
public class ItemInfoMod implements ClientModInitializer {
 
    public static final Logger LOGGER = LoggerFactory.getLogger("iteminfo");
    public static KeyBinding showInfoKey;
 
    @Override
    public void onInitializeClient() {
        // Tastenbelegung: Standard = LINKE ALT-Taste
        showInfoKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.iteminfo.show",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_ALT,
                "category.iteminfo"
        ));
 
        LOGGER.info("Item Info Mod geladen! ALT im Inventar halten für Item-Infos.");
    }
 
    /**
     * Gibt zurück ob die ALT-Taste gerade gehalten wird.
     * Verwendet InputUtil direkt — isPressed() erkennt nur kurze Drücke,
     * nicht das Halten einer Taste.
     */
    public static boolean isKeyHeld() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getWindow() == null) return false;
        return InputUtil.isKeyPressed(
                client.getWindow().getHandle(),
                GLFW.GLFW_KEY_LEFT_ALT
        );
    }
}
 
