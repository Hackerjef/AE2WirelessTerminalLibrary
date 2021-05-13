package tfar.ae2wtlib.terminal;

import tfar.ae2wtlib.AE2WirelessCraftingTerminals;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;

import java.util.List;

public class ItemInfinityBooster extends Item {
    public ItemInfinityBooster() {
        super(new FabricItemSettings().group(AE2WirelessCraftingTerminals.ITEM_GROUP).maxCount(1));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(final ItemStack stack, final World world, final List<Text> lines, final TooltipContext advancedTooltips) {
        super.appendTooltip(stack, world, lines, advancedTooltips);
        lines.add(new TranslatableText("item.ae2wtlib.infinity_booster_card.desc"));
    }
}