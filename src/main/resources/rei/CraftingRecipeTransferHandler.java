package pegasi_51b.ae2wct.rei;

import dev.nadie.ae2wct.wirelesscraftingterminal.WirelessCraftingTerminalContainer;
import me.shedaniel.rei.api.AutoTransferHandler;
import me.shedaniel.rei.api.RecipeDisplay;

public class CraftingRecipeTransferHandler extends RecipeTransferHandler<WirelessCraftingTerminalContainer> {

    public CraftingRecipeTransferHandler(Class<WirelessCraftingTerminalContainer> containerClass) {
        super(containerClass);
    }

    @Override
    protected AutoTransferHandler.Result doTransferRecipe(WirelessCraftingTerminalContainer container, RecipeDisplay recipe, AutoTransferHandler.Context context) {
        return null;
    }

    @Override
    protected boolean isCrafting() {
        return true;
    }
}