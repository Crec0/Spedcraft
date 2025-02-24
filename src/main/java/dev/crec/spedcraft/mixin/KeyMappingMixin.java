package dev.crec.spedcraft.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.crec.spedcraft.Spedcraft;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(KeyMapping.class)
public class KeyMappingMixin {
    @WrapOperation(
        method = "set",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;setDown(Z)V")
    )
    private static void setDown(KeyMapping instance, boolean bl, Operation<Void> original) {
        if (instance == Spedcraft.massCraftBind) {
            instance.setDown(true);
        } else {
            original.call(instance, bl);
        }
    }
}
