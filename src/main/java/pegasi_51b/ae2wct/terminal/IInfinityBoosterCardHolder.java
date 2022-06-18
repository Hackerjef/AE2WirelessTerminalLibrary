package pegasi_51b.ae2wct.terminal;

import net.minecraft.item.ItemStack;

public interface IInfinityBoosterCardHolder {

    boolean hasBoosterCard(ItemStack hostItem);

    void setBoosterCard(ItemStack hostItem, ItemStack boosterCard);

    ItemStack getBoosterCard(ItemStack hostItem);
}