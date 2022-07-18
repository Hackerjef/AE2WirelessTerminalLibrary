package dev.nadie.ae2wct.terminal;

import appeng.api.config.AccessRestriction;
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
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.DimensionalCoord;
import appeng.api.util.IConfigManager;
import appeng.container.interfaces.IInventorySlotAware;
import appeng.core.Api;
import appeng.tile.networking.WirelessTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class WTGuiObject implements IGuiItemObject, IEnergySource, IActionHost, IInventorySlotAware {

    // What the fuck java cant rename imports .-. https://stackoverflow.com/a/68089103
    private static class boosterItems extends dev.nadie.aeinfinitybooster.setup.ModItems {}

    private final FixedViewCellInventory fixedViewCellInventory;
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

    public WTGuiObject(final IWirelessTermHandler wh, final ItemStack is, final PlayerEntity ep, int inventorySlot) {
        String encryptionKey = wh.getEncryptionKey(is);
        effectiveItem = is;
        fixedViewCellInventory = new FixedViewCellInventory(is);
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
        this.sqRange = this.myRange = Double.MAX_VALUE;

        if (this.targetGrid != null && this.itemStorage != null) {
            if (this.myWap != null) {
                if (this.myWap.getGrid() == this.targetGrid && this.testWap(this.myWap)) {
                    return true;
                }
                return false;
            }

            final IMachineSet tw = this.targetGrid.getMachines(WirelessTileEntity.class);

            this.myWap = null;

            for (final IGridNode n : tw) {
                final IWirelessAccessPoint wap = (IWirelessAccessPoint) n.getMachine();
                if (this.testWap(wap)) {
                    this.myWap = wap;
                }
            }

            return this.myWap != null;
        }
        return false;
    }

    public double getRange() {
        return myRange;
    }

    private boolean isOutOfRange;

    public boolean isOutOfRange() {
        return isOutOfRange;
    }

    private boolean testWap(final IWirelessAccessPoint wap) {
        AtomicBoolean _has_range = new AtomicBoolean(false);

        wap.getGrid().getMachines(WirelessTileEntity.class).forEach(iGridNode -> {
            WirelessTileEntity wirelessBlockEntity = (WirelessTileEntity) iGridNode.getMachine();

            // TODO: ModItems.INFINITY_CARD KEKW
            if (wirelessBlockEntity.getInternalInventory().getStackInSlot(0).getItem() == boosterItems.DIMENSION_CARD.get()) {
                myRange = 16;
                _has_range.set(true);
                return;
            }

            if (this.myPlayer.world.getDimensionKey().getLocation().toString().equals(wap.getLocation().getWorld().getDimensionKey().getLocation().toString())) {
                if (wirelessBlockEntity.getInternalInventory().getStackInSlot(0).getItem() == boosterItems.INFINITY_CARD.get()) {
                    myRange = 16;
                    _has_range.set(true);
                    return;
                }
            }
        });

        if (_has_range.get()) {
            return true;
        }

        double rangeLimit = wap.getRange();
        rangeLimit *= rangeLimit;

        final DimensionalCoord dc = wap.getLocation();

        if(dc.getWorld() == myPlayer.world) {
            final double offX = dc.x - myPlayer.getPosX();
            final double offY = dc.y - myPlayer.getPosY();
            final double offZ = dc.z - myPlayer.getPosZ();

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



    public <T extends IAEStack<T>> IMEMonitor<T> getInventory(IStorageChannel<T> channel) {
        return sg.getInventory(channel);
    }

    public void addListener(final IMEMonitorHandlerReceiver<IAEItemStack> l, final Object verificationToken) {
        if(itemStorage != null) {
            itemStorage.addListener(l, verificationToken);
        }
    }

    public void removeListener(final IMEMonitorHandlerReceiver<IAEItemStack> l) {
        if(itemStorage != null) {
            itemStorage.removeListener(l);
        }
    }

    public IItemList<IAEItemStack> getAvailableItems(final IItemList<IAEItemStack> out) {
        if(itemStorage != null) {
            return itemStorage.getAvailableItems(out);
        }
        return out;
    }

    public IItemList<IAEItemStack> getStorageList() {
        if(itemStorage != null) {
            return itemStorage.getStorageList();
        }
        return null;
    }

    public AccessRestriction getAccess() {
        if(itemStorage != null) {
            return itemStorage.getAccess();
        }
        return AccessRestriction.NO_ACCESS;
    }

    public boolean isPrioritized(final IAEItemStack input) {
        if(itemStorage != null) {
            return itemStorage.isPrioritized(input);
        }
        return false;
    }

    public boolean canAccept(final IAEItemStack input) {
        if(itemStorage != null) {
            return itemStorage.canAccept(input);
        }
        return false;
    }

    public int getPriority() {
        if(itemStorage != null) {
            return itemStorage.getPriority();
        }
        return 0;
    }

    public int getSlot() {
        if(itemStorage != null) {
            return itemStorage.getSlot();
        }
        return 0;
    }

    public boolean validForPass(final int i) {
        return itemStorage.validForPass(i);
    }

    public IAEItemStack injectItems(final IAEItemStack input, final Actionable type, final IActionSource src) {
        if(itemStorage != null) {
            return itemStorage.injectItems(input, type, src);
        }
        return input;
    }

    public IAEItemStack extractItems(final IAEItemStack request, final Actionable mode, final IActionSource src) {
        if(itemStorage != null) {
            return itemStorage.extractItems(request, mode, src);
        }
        return null;
    }

    public IStorageChannel getChannel() {
        if(itemStorage != null) {
            return itemStorage.getChannel();
        }
        return Api.instance().storage().getStorageChannel(IItemStorageChannel.class);
    }

    public FixedViewCellInventory getViewCellStorage() { //FIXME viemcells
        return fixedViewCellInventory;
    }


    public abstract ContainerType<?> getType();

}