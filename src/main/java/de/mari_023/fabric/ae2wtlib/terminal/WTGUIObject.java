package de.mari_023.fabric.ae2wtlib.terminal;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.features.ILocatable;
import appeng.api.features.IWirelessTermHandler;
import appeng.api.implementations.guiobjects.IGuiItemObject;
import appeng.api.implementations.tiles.IWirelessAccessPoint;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IMachineSet;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.DimensionalCoord;
import appeng.api.util.IConfigManager;
import appeng.container.interfaces.IInventorySlotAware;
import appeng.core.Api;
import appeng.tile.networking.WirelessBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class WTGUIObject implements IGuiItemObject, IEnergySource, IActionHost, IInventorySlotAware {

    private final ItemStack effectiveItem;
    private IGrid targetGrid;
    private final IWirelessTermHandler wth;
    private final PlayerEntity myPlayer;
    private IMEMonitor<IAEItemStack> itemStorage;
    private IWirelessAccessPoint myWap;
    private double sqRange = Double.MAX_VALUE;
    private double myRange = Double.MAX_VALUE;
    private IStorageGrid sg;
    private final int inventorySlot;

    public WTGUIObject(final IWirelessTermHandler wh, final ItemStack is, final PlayerEntity ep, int inventorySlot) {
        String encryptionKey = wh.getEncryptionKey(is);
        effectiveItem = is;
        myPlayer = ep;
        wth = wh;
        this.inventorySlot = inventorySlot;

        ILocatable obj = null;

        try {
            final long encKey = Long.parseLong(encryptionKey);
            obj = Api.instance().registries().locatable().getLocatableBy(encKey);
        } catch(final NumberFormatException ignored) {}

        if(obj instanceof IActionHost) {
            final IGridNode n = ((IActionHost) obj).getActionableNode();
            if(n != null) {
                targetGrid = n.getGrid();
                sg = targetGrid.getCache(IStorageGrid.class);
                itemStorage = sg.getInventory(Api.instance().storage().getStorageChannel(IItemStorageChannel.class));
            }
        }
    }

    public boolean rangeCheck() {
        boolean hasBoosterCard = ((IInfinityBoosterCardHolder) effectiveItem.getItem()).hasBoosterCard(effectiveItem);
        sqRange = myRange = Double.MAX_VALUE;

        if(targetGrid != null && itemStorage != null) {
            if(myWap != null) {
                if(myWap.getGrid() == targetGrid) {
                    return testWap(myWap) || hasBoosterCard;
                }
                return hasBoosterCard;
            }

            final IMachineSet tw = targetGrid.getMachines(WirelessBlockEntity.class);

            myWap = null;

            for(final IGridNode n : tw) {
                final IWirelessAccessPoint wap = (IWirelessAccessPoint) n.getMachine();
                if(testWap(wap)) {
                    myWap = wap;
                }
            }

            return myWap != null || hasBoosterCard;
        }
        return hasBoosterCard;
    }

    public double getRange() {
        return myRange;
    }

    private boolean isOutOfRange;

    public boolean isOutOfRange() {
        return isOutOfRange;
    }

    private boolean testWap(final IWirelessAccessPoint wap) {
        double rangeLimit = wap.getRange();
        rangeLimit *= rangeLimit;

        final DimensionalCoord dc = wap.getLocation();

        if(dc.getWorld() == myPlayer.world) {
            final double offX = dc.x - myPlayer.getX();
            final double offY = dc.y - myPlayer.getY();
            final double offZ = dc.z - myPlayer.getZ();

            final double r = offX * offX + offY * offY + offZ * offZ;
            if(r < rangeLimit && sqRange > r) {
                if(wap.isActive()) {
                    sqRange = r;
                    myRange = Math.sqrt(r);
                    isOutOfRange = false;
                    return true;
                }
            }
        }
        isOutOfRange = true;
        return false;
    }

    public IStorageGrid getIStorageGrid() {
        return sg;
    }

    public IMEMonitor<IAEItemStack> getItemStorage() {
        return itemStorage;
    }

    @Override
    public IGridNode getActionableNode() {
        rangeCheck();
        if(myWap != null) {
            return myWap.getActionableNode();
        }
        return null;
    }

    @Override
    public int getInventorySlot() {
        return inventorySlot;
    }

    @Override
    public double extractAEPower(final double amt, final Actionable mode, final PowerMultiplier usePowerMultiplier) {
        if(wth != null && effectiveItem != null) {
            if(mode == Actionable.SIMULATE) {
                return wth.hasPower(myPlayer, amt, effectiveItem) ? amt : 0;
            }
            return wth.usePower(myPlayer, amt, effectiveItem) ? amt : 0;
        }
        return 0.0;
    }

    @Override
    public ItemStack getItemStack() {
        return effectiveItem;
    }

    public PlayerEntity getPlayer() {
        return myPlayer;
    }

    public IConfigManager getConfigManager() {
        return wth.getConfigManager(getItemStack());
    }
}