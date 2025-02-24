package dev.crec.spedcraft.mixin;

import dev.crec.spedcraft.Spedcraft;
import net.minecraft.world.inventory.CraftingMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingMenu.class)
public class CraftingMenuMixin {
    @Inject(
        method = "removed",
        at = @At("TAIL")
    )
    private void onRemoved(CallbackInfo ci) {
        Spedcraft.haltOperation();
    }
}
