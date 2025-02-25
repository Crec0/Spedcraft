package dev.crec.spedcraft.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.crec.spedcraft.Spedcraft;
import net.minecraft.client.ToggleKeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ToggleKeyMapping.class)
public class ToggleKeyMappingMixin {
    @WrapOperation(method = "setDown", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;setDown(Z)V"))
    private void ss(ToggleKeyMapping instance, boolean bl, Operation<Void> original) {
        if (instance == Spedcraft.masscraftBind) {
            if (Spedcraft.isOnValidScreen()) {
                original.call(instance, bl);
            } else {
                original.call(instance, false);
            }
        } else {
            original.call(instance, bl);
        }
    }
}
