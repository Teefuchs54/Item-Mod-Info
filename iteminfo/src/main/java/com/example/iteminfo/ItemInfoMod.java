package com.example.iteminfo;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class ItemInfoMod implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("iteminfo");
    public static KeyBinding showInfoKey;

    private static final KeyBinding.Category ITEMINFO_CATEGORY =
            KeyBinding.Category.create(Identifier.of("iteminfo", "category"));

    @Override
    public void onInitializeClient() {
        showInfoKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.iteminfo.show",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_ALT,
                ITEMINFO_CATEGORY
        ));

        LOGGER.info("Item Info Mod geladen! ALT im Inventar halten fuer Item-Infos.");
    }

    /**
     * Gibt zurück ob die ALT-Taste gerade gehalten wird.
     * Fragt GLFW direkt — funktioniert für "halten", nicht nur für kurze Drücke.
     */
    public static boolean isKeyHeld() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getWindow() == null) return false;
        return InputUtil.isKeyPressed(
                client.getWindow(),
                GLFW.GLFW_KEY_LEFT_ALT
        );
    }
}
