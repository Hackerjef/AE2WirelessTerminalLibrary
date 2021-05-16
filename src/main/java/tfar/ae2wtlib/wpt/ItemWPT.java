package tfar.ae2wtlib.wpt;

import appeng.container.ContainerLocator;
import appeng.core.AEConfig;
import tfar.ae2wtlib.AE2WirelessTerminals;
import tfar.ae2wtlib.terminal.IInfinityBoosterCardHolder;
import tfar.ae2wtlib.terminal.ItemWT;
import net.minecraft.entity.player.PlayerEntity;

public class ItemWPT extends ItemWT implements IInfinityBoosterCardHolder {

    public ItemWPT() {
        super(AEConfig.instance().getWirelessTerminalBattery(), new Properties().group(AE2WirelessTerminals.ITEM_GROUP).maxStackSize(1));
    }

    @Override
    public void open(final PlayerEntity player, final ContainerLocator locator) {
        WPatternTContainer.open(player, locator);
    }
}