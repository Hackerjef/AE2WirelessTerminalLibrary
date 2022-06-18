package pegasi_51b.ae2wct.wirelesscraftingterminal;

import appeng.api.config.ActionItems;
import appeng.client.gui.Icon;
import appeng.client.gui.me.items.ItemTerminalScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ActionButton;
import appeng.client.gui.widgets.IconButton;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import pegasi_51b.ae2wct.net.PacketHandler;
import pegasi_51b.ae2wct.net.server.C2SDeleteTrashPacket;
import pegasi_51b.ae2wct.net.server.C2SSetMagnetModePacket;
import pegasi_51b.ae2wct.util.ItemButton;
import pegasi_51b.ae2wct.wirelesscraftingterminal.magnet_card.MagnetMode;
import pegasi_51b.ae2wct.wirelesscraftingterminal.magnet_card.MagnetSettings;

public class WirelessCraftingTerminalScreen extends ItemTerminalScreen<WirelessCraftingTerminalContainer> {

    ItemButton magnetCardToggleButton;

    public WirelessCraftingTerminalScreen(WirelessCraftingTerminalContainer container, PlayerInventory playerInventory, ITextComponent title, ScreenStyle style) {
        super(container, playerInventory, title,style);
        ActionButton clearBtn = new ActionButton(ActionItems.STASH, (btn) -> container.clearCraftingGrid());
        clearBtn.setHalfSize(true);
        widgets.add("clearCraftingGrid", clearBtn);
        IconButton deleteButton = new IconButton(btn -> delete()) {
            @Override
            protected Icon getIcon() {
                return Icon.CONDENSER_OUTPUT_TRASH;
            }
        };
        deleteButton.setHalfSize(true);
        deleteButton.setMessage(new TranslationTextComponent("gui.ae2wctlib.emptytrash").appendString("\n")
                .appendSibling(new TranslationTextComponent("gui.ae2wctlib.emptytrash.desc")));
        widgets.add("emptyTrash", deleteButton);

        magnetCardToggleButton = new ItemButton(new ResourceLocation("ae2wctlib", "textures/magnet_card.png"), btn -> setMagnetMode());
        magnetCardToggleButton.setHalfSize(true);
        widgets.add("magnetCardToggleButton", magnetCardToggleButton);
        resetMagnetSettings();

        widgets.add("player", new PlayerWidget());
    }

    @Override
    public void drawBG(MatrixStack matrices, final int offsetX, final int offsetY, final int mouseX, final int mouseY, float partialTicks) {
        super.drawBG(matrices, offsetX, offsetY, mouseX, mouseY, partialTicks);
    }

    @Override
    public void drawFG(MatrixStack matrices, final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        super.drawFG(matrices, offsetX, offsetY, mouseX, mouseY);
    }

    private void delete() {
        PacketHandler.INSTANCE.sendToServer(new C2SDeleteTrashPacket());
    }

    private MagnetSettings magnetSettings = null;

    public void resetMagnetSettings() {
        magnetSettings = container.getMagnetSettings();
        setMagnetModeText();
    }

    private void setMagnetMode() {
        switch(magnetSettings.magnetMode) {
            case NO_CARD:
                return;
            case OFF:
                magnetSettings.magnetMode = MagnetMode.PICKUP_INVENTORY;
                break;
            case PICKUP_INVENTORY:
                magnetSettings.magnetMode = MagnetMode.PICKUP_ME;
                break;
            case PICKUP_ME:
                magnetSettings.magnetMode = MagnetMode.OFF;
                break;
        }
        setMagnetModeText();
        PacketHandler.INSTANCE.sendToServer(new C2SSetMagnetModePacket(magnetSettings.magnetMode));
    }

    private void setMagnetModeText() {
        switch(magnetSettings.magnetMode) {
            case NO_CARD:
                magnetCardToggleButton.setVisibility(false);
                return;
            case OFF:
                magnetCardToggleButton.setMessage(new TranslationTextComponent("gui.ae2wctlib.magnetcard").appendString("\n").appendSibling(new TranslationTextComponent("gui.ae2wctlib.magnetcard.desc.off")));
                break;
            case PICKUP_INVENTORY:
                magnetCardToggleButton.setMessage(new TranslationTextComponent("gui.ae2wctlib.magnetcard").appendString("\n").appendSibling(new TranslationTextComponent("gui.ae2wctlib.magnetcard.desc.inv")));
                break;
            case PICKUP_ME:
                magnetCardToggleButton.setMessage(new TranslationTextComponent("gui.ae2wctlib.magnetcard").appendString("\n").appendSibling(new TranslationTextComponent("gui.ae2wctlib.magnetcard.desc.me")));
                break;
        }
        magnetCardToggleButton.setVisibility(true);
    }

    //todo jei
   /* @Override
    public List<Rectangle> getExclusionZones() {
        List<Rectangle> zones = super.getExclusionZones();
        zones.add(new Rectangle(guiTop + 195, y, 24,ySize - 110));
        return zones;
    }*/
}