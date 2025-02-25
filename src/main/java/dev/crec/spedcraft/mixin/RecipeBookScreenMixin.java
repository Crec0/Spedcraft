package dev.crec.spedcraft.mixin;

import dev.crec.spedcraft.Spedcraft;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.RecipeBookMenu;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({CraftingScreen.class, InventoryScreen.class})
abstract public class RecipeBookScreenMixin<T extends RecipeBookMenu> extends AbstractRecipeBookScreen<T> {
    public RecipeBookScreenMixin(
        T recipeBookMenu,
        RecipeBookComponent<?> recipeBookComponent,
        Inventory inventory,
        Component component
    ) {
        super(recipeBookMenu, recipeBookComponent, inventory, component);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (Spedcraft.masscraftBind.matches(keyCode, scanCode)) {
            Spedcraft.masscraftBind.setDown(true);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        Spedcraft.haltOperation(this.minecraft);
        super.onClose();
    }
}
