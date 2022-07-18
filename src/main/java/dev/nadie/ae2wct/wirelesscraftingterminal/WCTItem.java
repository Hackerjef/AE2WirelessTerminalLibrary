package dev.nadie.ae2wct.wirelesscraftingterminal;

import appeng.container.ContainerLocator;
import appeng.core.AEConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import dev.nadie.ae2wct.terminal.AbstractWirelessTerminalItem;
import dev.nadie.ae2wct.AE2WirelessCraftingTerminal;

public class WCTItem extends AbstractWirelessTerminalItem {

    public WCTItem() {
        super(AEConfig.instance().getWirelessTerminalBattery(), new Properties().group(AE2WirelessCraftingTerminal.ITEM_GROUP).maxStackSize(1));
    }

    @Override
    public void open(final PlayerEntity player, final ContainerLocator locator) {
        WirelessCraftingTerminalContainer.openServer(player, locator);
    }

    @Override
    public boolean canHandle(ItemStack is) {
        return is.getItem() instanceof WCTItem;
    }

}