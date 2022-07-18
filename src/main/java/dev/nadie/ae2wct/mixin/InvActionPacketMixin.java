package dev.nadie.ae2wct.mixin;

import appeng.core.sync.network.INetworkInfo;
import appeng.core.sync.packets.InventoryActionPacket;
import appeng.helpers.InventoryAction;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InventoryActionPacket.class,remap = false)
public class InvActionPacketMixin {

    @Shadow
    @Final
    private InventoryAction action;

    @Inject(method = "serverPacketData", at = @At(value = "TAIL"), require = 1, allow = 1, remap = false)
    public void serverPacketData(INetworkInfo manager, PlayerEntity player, CallbackInfo ci) {
        if(action == InventoryAction.AUTO_CRAFT) {

        }
    }
}