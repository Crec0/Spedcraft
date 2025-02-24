package dev.crec.spedcraft.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.crec.spedcraft.Spedcraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookComponent.class)
public abstract class RecipeBookComponentMixin {
    @Shadow protected Minecraft minecraft;

    @Inject(
        method = "tryPlaceRecipe",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;handlePlaceRecipe(ILnet/minecraft/world/item/crafting/display/RecipeDisplayId;Z)V"
        )
    )
    private void spedcraft$handleClick(
        RecipeCollection recipeCollection,
        RecipeDisplayId recipeDisplayId,
        CallbackInfoReturnable<Boolean> cir
    ) {
        Spedcraft.lastSelectedItem = recipeDisplayId;
        if (Screen.hasAltDown() && Screen.hasShiftDown()) {
            Spedcraft.singleCraftTriggered = true;
            Spedcraft.sendCraftingPacket(this.minecraft, this.minecraft.player);
        }
    }

    @WrapMethod(method = "tick")
    private void spedcraft$tick$stopUpdatingRecipeBookWhenMassCrafting(Operation<Void> original) {
        if (!Spedcraft.isMassCraftActive()) {
            original.call();
        }
    }

    @WrapMethod(method = "slotClicked")
    private void spedcraft$slotClicked$stopUpdatingRecipeBookWhenMassCrafting(Slot slot, Operation<Void> original) {
        if (!Spedcraft.isMassCraftActive()) {
            original.call(slot);
        }
    }
}
