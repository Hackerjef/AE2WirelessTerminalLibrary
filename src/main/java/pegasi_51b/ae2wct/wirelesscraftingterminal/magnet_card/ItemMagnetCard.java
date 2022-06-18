package pegasi_51b.ae2wct.wirelesscraftingterminal.magnet_card;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import pegasi_51b.ae2wct.terminal.AbstractWirelessTerminalItem;
import pegasi_51b.ae2wct.terminal.SlotType;

import java.util.List;

public class ItemMagnetCard extends Item {

    public ItemMagnetCard(Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(final ItemStack stack, final World world, final List<ITextComponent> lines, final ITooltipFlag advancedTooltips) {
        super.addInformation(stack, world, lines, advancedTooltips);
        lines.add(new TranslationTextComponent("item.ae2wctlib.magnet_card.desc"));
    }

    public static void saveMagnetSettings(ItemStack magnetCardHolder, MagnetSettings magnetSettings) {
        ItemStack magnetCard = AbstractWirelessTerminalItem.getSavedSlot(magnetCardHolder, SlotType.magnetCard);
        magnetSettings.saveTo(magnetCard);
        AbstractWirelessTerminalItem.setSavedSlot(magnetCardHolder, magnetCard,  SlotType.magnetCard);
    }

    public static MagnetSettings loadMagnetSettings(ItemStack magnetCardHolder) {
        ItemStack magnetCard = AbstractWirelessTerminalItem.getSavedSlot(magnetCardHolder,  SlotType.magnetCard);
        return MagnetSettings.from(magnetCard);
    }

    public static boolean isActiveMagnet(ItemStack magnetCardHolder) {
        if(magnetCardHolder.isEmpty()) return false;
        MagnetSettings settings = loadMagnetSettings(magnetCardHolder);
        return settings.magnetMode.isActive();
    }

    public static boolean isPickupME(ItemStack magnetCardHolder) {
        if(magnetCardHolder.isEmpty()) return false;
        MagnetSettings settings = loadMagnetSettings(magnetCardHolder);
        return settings.magnetMode == MagnetMode.PICKUP_ME;
    }
}