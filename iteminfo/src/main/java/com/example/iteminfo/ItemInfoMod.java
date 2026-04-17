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
                GLFW.GLFW_KEY_LEFT_CONTROL,
                ITEMINFO_CATEGORY
        ));

        LOGGER.info("Item Info Mod geladen! CTRL im Inventar halten fuer Item-Infos.");
    }

    /**
     * Fragt GLFW direkt ob CTRL gerade gehalten wird.
     * Zuverlässiger als isPressed() oder InputUtil.
     */
    public static boolean isKeyHeld() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return false;
        long handle = client.getWindow().getHandle();
        return GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS
            || GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;
    }
}
