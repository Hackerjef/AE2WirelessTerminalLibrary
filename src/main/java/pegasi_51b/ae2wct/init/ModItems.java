package pegasi_51b.ae2wct.init;

import net.minecraft.item.Item;
import pegasi_51b.ae2wct.terminal.ItemInfinityBooster;
import pegasi_51b.ae2wct.wirelesscraftingterminal.WCTItem;
import pegasi_51b.ae2wct.wirelesscraftingterminal.magnet_card.ItemMagnetCard;
import pegasi_51b.ae2wct.AE2WirelessCraftingTerminal;

public class ModItems {
    public static final WCTItem CRAFTING_TERMINAL = new WCTItem();
    public static final ItemInfinityBooster INFINITY_BOOSTER_CARD = new ItemInfinityBooster();
    public static final ItemMagnetCard MAGNET_CARD = new ItemMagnetCard(new Item.Properties().group(AE2WirelessCraftingTerminal.ITEM_GROUP).maxStackSize(1));
}
