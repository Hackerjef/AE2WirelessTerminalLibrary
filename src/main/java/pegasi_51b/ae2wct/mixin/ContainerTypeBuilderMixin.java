package pegasi_51b.ae2wct.mixin;

import appeng.container.ContainerLocator;
import appeng.container.implementations.ContainerTypeBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pegasi_51b.ae2wct.terminal.AbstractWirelessTerminalItem;
import pegasi_51b.ae2wct.wirelesscraftingterminal.WCTGuiObject;
import pegasi_51b.ae2wct.wirelesscraftingterminal.WCTItem;

@Mixin(value = ContainerTypeBuilder.class, remap = false)
public class ContainerTypeBuilderMixin<I> {
    @Shadow
    @Final
    private Class<I> hostInterface;

    @Inject(method = "getHostFromPlayerInventory", at = @At(value = "HEAD"), cancellable = true)
    private void getWirelessHostFromPlayerInventory(PlayerEntity player, ContainerLocator locator, CallbackInfoReturnable<I> cir) {
        ItemStack it = player.inventory.getStackInSlot(locator.getItemIndex());

        if (it.isEmpty()) {
            return;
        }

        // FIXME: this shouldn't be hardcoded
        if (it.getItem() instanceof AbstractWirelessTerminalItem) {
            AbstractWirelessTerminalItem awti = (AbstractWirelessTerminalItem)it.getItem();
            if (awti instanceof WCTItem) {
                cir.setReturnValue(hostInterface.cast(new WCTGuiObject(awti, it, player, locator.getItemIndex())));
            }
        }
    }
}
