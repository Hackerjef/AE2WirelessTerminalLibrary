package pegasi_51b.ae2wct.mixin;

import appeng.api.config.Actionable;
import appeng.api.features.ILocatable;
import appeng.api.networking.IGrid;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.core.Api;
import appeng.me.helpers.PlayerSource;
import appeng.util.item.AEItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pegasi_51b.ae2wct.wirelesscraftingterminal.CraftingTerminalHandler;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

    @Shadow
    @Final
    public PlayerEntity player;

    @Inject(method = "addItemStackToInventory(Lnet/minecraft/item/ItemStack;)Z", at = @At(value = "INVOKE"), require = 1, allow = 1)
    public void insertStackInME(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if(stack.isEmpty()) return;
        CraftingTerminalHandler CTHandler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
        ItemStack terminal = CTHandler.getCraftingTerminal();
    }
}