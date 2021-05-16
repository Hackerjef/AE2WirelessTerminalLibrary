package tfar.ae2wtlib.mixin;

import appeng.container.AEBaseContainer;
import appeng.container.ContainerLocator;
import appeng.core.sync.network.INetworkInfo;
import appeng.core.sync.packets.InventoryActionPacket;
import appeng.helpers.InventoryAction;
import net.minecraft.entity.player.ServerPlayerEntity;
import tfar.ae2wtlib.wct.WCTContainer;
import tfar.ae2wtlib.wpt.WPatternTContainer;
import tfar.ae2wtlib.util.WirelessCraftAmountContainer;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryActionPacket.class)
public class InvActionPacket {

    @Shadow
    @Final
    private InventoryAction action;

    @Inject(method = "serverPacketData", at = @At(value = "TAIL"), require = 1, allow = 1, remap = false)
    public void serverPacketData(INetworkInfo manager, PlayerEntity player, CallbackInfo ci) {
        if(action == InventoryAction.AUTO_CRAFT) {
            final ServerPlayerEntity sender = (ServerPlayerEntity) player;
            if(sender.openContainer instanceof WCTContainer || sender.openContainer instanceof WPatternTContainer) {
                final AEBaseContainer baseContainer = (AEBaseContainer) sender.openContainer;
                final ContainerLocator locator = baseContainer.getLocator();
                if(locator != null) {
                    WirelessCraftAmountContainer.open(player, locator);

                    if(sender.openContainer instanceof WirelessCraftAmountContainer) {
                        final WirelessCraftAmountContainer cca = (WirelessCraftAmountContainer) sender.openContainer;

                        if(baseContainer.getTargetStack() != null) {
                            cca.getCraftingItem().putStack(baseContainer.getTargetStack().asItemStackRepresentation());
                            // This is the *actual* item that matters, not the display item above
                            cca.setItemToCraft(baseContainer.getTargetStack());
                        }
                        cca.detectAndSendChanges();
                    }
                }
            }
        }
    }
}