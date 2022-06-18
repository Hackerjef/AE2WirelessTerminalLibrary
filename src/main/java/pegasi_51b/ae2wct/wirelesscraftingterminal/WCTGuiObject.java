package pegasi_51b.ae2wct.wirelesscraftingterminal;

import appeng.api.features.IWirelessTermHandler;
import appeng.api.implementations.guiobjects.IPortableCell;
import appeng.api.implementations.tiles.IViewCellStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import pegasi_51b.ae2wct.init.Menus;
import pegasi_51b.ae2wct.terminal.WTGuiObject;

public class WCTGuiObject extends WTGuiObject implements IPortableCell , IViewCellStorage {

    public WCTGuiObject(final IWirelessTermHandler wh, final ItemStack is, final PlayerEntity ep, int inventorySlot) {
        super(wh, is, ep, inventorySlot);
    }

    @Override
    public ContainerType<?> getType() {
        return Menus.WCT;
    }
}