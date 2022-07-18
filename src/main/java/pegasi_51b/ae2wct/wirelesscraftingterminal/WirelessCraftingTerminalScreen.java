package pegasi_51b.ae2wct.wirelesscraftingterminal;

import appeng.api.config.ActionItems;
import appeng.client.gui.me.items.ItemTerminalScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ActionButton;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class WirelessCraftingTerminalScreen extends ItemTerminalScreen<WirelessCraftingTerminalContainer> {

    public WirelessCraftingTerminalScreen(WirelessCraftingTerminalContainer container, PlayerInventory playerInventory, ITextComponent title, ScreenStyle style) {
        super(container, playerInventory, title,style);
        ActionButton clearBtn = new ActionButton(ActionItems.STASH, (btn) -> container.clearCraftingGrid());
        clearBtn.setHalfSize(true);
        widgets.add("clearCraftingGrid", clearBtn);
    }

    @Override
    public void drawBG(MatrixStack matrices, final int offsetX, final int offsetY, final int mouseX, final int mouseY, float partialTicks) {
        super.drawBG(matrices, offsetX, offsetY, mouseX, mouseY, partialTicks);
    }

    @Override
    public void drawFG(MatrixStack matrices, final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        super.drawFG(matrices, offsetX, offsetY, mouseX, mouseY);
    }

}