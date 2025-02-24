package dev.crec.spedcraft;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ToggleKeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.lwjgl.glfw.GLFW;

import java.util.function.BiConsumer;

public class Spedcraft implements ClientModInitializer {

    public static RecipeDisplayId lastSelectedItem;
    public static boolean failedLastAttempt = false;

    public static final KeyMapping massCraftBind = KeyBindingHelper.registerKeyBinding(new ToggleKeyMapping(
        "Mass Craft",
        GLFW.GLFW_KEY_M,
        "Spedcraft",
        () -> true
    ));

    private static final FireOnChange<Boolean> massCraftToggleListener = new FireOnChange<>(
        false, (client, newValue) -> {
        if (client.player == null) return;

        if (newValue) {
            client.player.displayClientMessage(Component.literal("Mass Craft Enabled"), true);
        } else {
            client.player.displayClientMessage(Component.literal("Mass Craft Disabled"), true);
        }
    }
    );

    public static boolean singleCraftTriggered = false;

    public static boolean isMassCraftActive() {
        return massCraftBind.isDown();
    }

    public static void sendCraftingPacket(Minecraft minecraft, Player player) {
        if (!Spedcraft.isMassCraftActive() || minecraft.gameMode == null || Spedcraft.lastSelectedItem == null) {
            return;
        }
        minecraft.gameMode.handlePlaceRecipe(
            player.containerMenu.containerId,
            Spedcraft.lastSelectedItem,
            true
        );
    }

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            if (Spedcraft.isMassCraftActive()) {
                Spedcraft.sendCraftingPacket(client, client.player);
            }

            massCraftToggleListener.fire(client, massCraftBind.isDown());
        });
    }

    private static class FireOnChange<T> {
        private T lastValue;
        private final BiConsumer<Minecraft, T> callback;

        public FireOnChange(T lastValue, BiConsumer<Minecraft, T> callback) {
            this.lastValue = lastValue;
            this.callback = callback;
        }

        public void fire(Minecraft client, T value) {
            if (this.lastValue == value) {
                return;
            }
            this.lastValue = value;
            callback.accept(client, value);
        }
    }
}
