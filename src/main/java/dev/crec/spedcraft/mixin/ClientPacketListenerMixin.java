package dev.crec.spedcraft.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.crec.spedcraft.Spedcraft;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.world.inventory.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
abstract public class ClientPacketListenerMixin extends ClientCommonPacketListenerImpl {

    protected ClientPacketListenerMixin(
        Minecraft minecraft,
        Connection connection,
        CommonListenerCookie commonListenerCookie
    ) {
        super(minecraft, connection, commonListenerCookie);
    }

    @Inject(
        method = "handleContainerSetSlot",
        at = @At("TAIL")
    )
    private void spedcraft$captureOutputSlotChanges(
        ClientboundContainerSetSlotPacket packet,
        CallbackInfo ci
    ) {
        if ((Spedcraft.singleCraftTriggered || Spedcraft.isMassCraftActive())
            && this.minecraft.player != null
            && this.minecraft.screen != null
        ) {
            Spedcraft.singleCraftTriggered = false;
            if (packet.getSlot() == 0 && !packet.getItem().isEmpty()) {
                Spedcraft.lastWasFailure = false;
                var menu = this.minecraft.player.containerMenu;
                var slot = menu.getSlot(packet.getSlot());
                ((AbstractContainerScreenInvoker) this.minecraft.screen).invokeSlotClicked(slot, 0, 1, ClickType.THROW);
                Spedcraft.sendCraftingPacket(this.minecraft, this.minecraft.player);
            } else {
                Spedcraft.lastWasFailure = true;
            }
        }
    }

    @WrapMethod(method = "refreshRecipeBook")
    private void spedcraft$doNotRefreshWhenMassCrafting(ClientRecipeBook clientRecipeBook, Operation<Void> original) {
        if (!Spedcraft.isMassCraftActive()) {
            original.call(clientRecipeBook);
        }
    }
}
