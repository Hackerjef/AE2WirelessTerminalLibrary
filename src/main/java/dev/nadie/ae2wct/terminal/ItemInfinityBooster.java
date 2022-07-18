package dev.nadie.ae2wct.terminal;

import dev.nadie.ae2wct.AE2WirelessCraftingTerminal;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class ItemInfinityBooster extends Item {
    public ItemInfinityBooster() {
        super(new Properties().group(AE2WirelessCraftingTerminal.ITEM_GROUP).maxStackSize(1));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(final ItemStack stack, final World world, final List<ITextComponent> lines, final ITooltipFlag advancedTooltips) {
        super.addInformation(stack, world, lines, advancedTooltips);
        lines.add(new TranslationTextComponent("item.ae2wctlib.infinity_booster_card.desc"));
    }
}