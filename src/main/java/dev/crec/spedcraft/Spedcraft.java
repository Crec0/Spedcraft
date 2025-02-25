package dev.crec.spedcraft;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ToggleKeyMapping;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.lwjgl.glfw.GLFW;


public class Spedcraft implements ClientModInitializer {

    public static RecipeDisplayId lastSelectedItem;
    public static boolean lastWasFailure = false;
    public static boolean singleCraftTriggered = false;
    private static boolean lastTickState = false;

    public static final KeyMapping masscraftBind = KeyBindingHelper.registerKeyBinding(
        new ToggleKeyMapping("Mass Craft", GLFW.GLFW_KEY_M, "Spedcraft", () -> true)
    );

    public static boolean isMassCraftActive() {
        return masscraftBind.isDown();
    }

    public static void haltOperation(Minecraft client) {
        lastSelectedItem = null;
        lastWasFailure = false;

        if (isMassCraftActive()) {
            masscraftBind.setDown(true);
        }
        if (client.player != null && lastTickState) {
            client.player.displayClientMessage(Component.literal("Mass Craft Disabled"), false);
            lastTickState = false;
        }
    }

    public static void sendCraftingPacket(Minecraft minecraft, Player player) {
        if (!isMassCraftActive() || minecraft.gameMode == null || lastSelectedItem == null) {
            return;
        }
        minecraft.gameMode.handlePlaceRecipe(
            player.containerMenu.containerId,
            lastSelectedItem,
            true
        );
    }

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            if (!(client.screen instanceof CraftingScreen || client.screen instanceof InventoryScreen)) return;

            var currentState = isMassCraftActive();
            if (currentState) {
                sendCraftingPacket(client, client.player);
            }

            if (currentState != lastTickState) {
                var text = Component.literal("Mass Craft " + (currentState ? "Enabled" : "Disabled"));
                client.player.displayClientMessage(text, false);
            }

            lastTickState = currentState;
        });
    }
}
