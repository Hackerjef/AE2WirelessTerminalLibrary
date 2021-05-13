package tfar.ae2wtlib.wit;

import alexiil.mc.lib.attributes.item.FixedItemInv;
import alexiil.mc.lib.attributes.item.LimitedFixedItemInv;
import alexiil.mc.lib.attributes.item.SingleItemSlot;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.container.AEBaseContainer;
import appeng.container.ContainerLocator;
import appeng.container.slot.AppEngSlot;
import appeng.core.localization.PlayerMessages;
import appeng.helpers.DualityInterface;
import appeng.helpers.IInterfaceHost;
import appeng.helpers.InventoryAction;
import appeng.items.misc.EncodedPatternItem;
import appeng.parts.misc.InterfacePart;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.tile.misc.InterfaceTileEntity;
import appeng.util.InventoryAdaptor;
import appeng.util.helpers.ItemHandlerUtil;
import appeng.util.inv.WrapperCursorItemHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.IItemHandler;
import tfar.ae2wtlib.Config;
import tfar.ae2wtlib.net.PacketHandler;
import tfar.ae2wtlib.terminal.FixedWTInv;
import tfar.ae2wtlib.terminal.IWTInvHolder;
import tfar.ae2wtlib.util.ContainerHelper;
import tfar.ae2wtlib.wut.ItemWUT;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import net.minecraft.util.Util;

import java.util.HashMap;
import java.util.Map;

public class WITContainer extends AEBaseContainer implements IWTInvHolder {

    public static ContainerType<WITContainer> TYPE;

    public static final ContainerHelper<WITContainer, WITGuiObject> helper = new ContainerHelper<>(WITContainer::new, WITGuiObject.class);

    public static WITContainer fromNetwork(int windowId, PlayerInventory inv, PacketBuffer buf) {
        return helper.fromNetwork(windowId, inv, buf);
    }

    public static boolean open(PlayerEntity player, ContainerLocator locator) {
        return helper.open(player, locator);
    }

    private final WITGuiObject witGUIObject;
    private static long autoBase = Long.MIN_VALUE;
    private final Map<IInterfaceHost, WITContainer.InvTracker> diList = new HashMap<>();
    private final Map<Long, WITContainer.InvTracker> byId = new HashMap<>();
    private IGrid grid;
    private CompoundNBT data = new CompoundNBT();

    public WITContainer(int id, final PlayerInventory ip, final WITGuiObject anchor) {
        super(TYPE, id, ip, anchor);
        witGUIObject = anchor;

        if(isServer() && witGUIObject.getActionableNode() != null) {
            grid = witGUIObject.getActionableNode().getGrid();
        }

        bindPlayerInventory(ip, 0, 222 - /* height of player inventory */82);

        final FixedWTInv fixedWITInv = new FixedWTInv(getPlayerInv(), witGUIObject.getItemStack(), this);
        addSlot(new AppEngSlot(fixedWITInv, FixedWTInv.INFINITY_BOOSTER_CARD, 173, 129));
    }

    private double powerMultiplier = 1;
    private int ticks = 0;

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        if(!witGUIObject.rangeCheck()) {
            if(isValidContainer()) {
                getPlayerInv().player.sendSystemMessage(PlayerMessages.OutOfRange.get(), Util.DUMMY_UUID);
                ((ServerPlayerEntity) getPlayerInv().player).closeContainer();
            }
            setValidContainer(false);
        } else {
            powerMultiplier = Config.getPowerMultiplier(witGUIObject.getRange(), witGUIObject.isOutOfRange());

            if(witGUIObject.extractAEPower(1, Actionable.SIMULATE, PowerMultiplier.ONE) == 0) {
                if(isValidContainer()) {
                    getPlayerInv().player.sendSystemMessage(PlayerMessages.DeviceNotPowered.get(), Util.DUMMY_UUID);
                    ((ServerPlayerEntity) getPlayerInv().player).closeHandledScreen();
                }
                setValidContainer(false);
            }
        }

        ticks++;
        if(ticks > 10) {
            witGUIObject.extractAEPower(powerMultiplier * ticks, Actionable.MODULATE, PowerMultiplier.CONFIG);
            ticks = 0;
        }

        if(grid == null) {
            return;
        }

        int total = 0;
        boolean missing = false;

        final IActionHost host = getActionHost();
        if(host != null) {
            final IGridNode agn = host.getActionableNode();
            if(agn != null && agn.isActive()) {
                for(final IGridNode gn : grid.getMachines(InterfaceTileEntity.class)) {
                    if(gn.isActive()) {
                        final IInterfaceHost ih = (IInterfaceHost) gn.getMachine();
                        if(ih.getInterfaceDuality().getConfigManager().getSetting(Settings.INTERFACE_TERMINAL) == YesNo.NO) {
                            continue;
                        }

                        final WITContainer.InvTracker t = diList.get(ih);

                        if(t == null) {
                            missing = true;
                        } else {
                            final DualityInterface dual = ih.getInterfaceDuality();
                            if(!t.name.equals(dual.getTermName())) {
                                missing = true;
                            }
                        }

                        total++;
                    }
                }

                for(final IGridNode gn : grid.getMachines(InterfacePart.class)) {
                    if(gn.isActive()) {
                        final IInterfaceHost ih = (IInterfaceHost) gn.getMachine();
                        if(ih.getInterfaceDuality().getConfigManager().getSetting(Settings.INTERFACE_TERMINAL) == YesNo.NO)
                            continue;

                        final WITContainer.InvTracker t = diList.get(ih);

                        if(t == null) missing = true;
                        else {
                            final DualityInterface dual = ih.getInterfaceDuality();
                            if(!t.name.equals(dual.getTermName())) {
                                missing = true;
                            }
                        }
                        total++;
                    }
                }
            }
        }

        if(total != diList.size() || missing) regenList(data);
        else {
            for(final Map.Entry<IInterfaceHost, WITContainer.InvTracker> en : diList.entrySet()) {
                final WITContainer.InvTracker inv = en.getValue();
                for(int x = 0; x < inv.server.getSlots(); x++)
                    if(isDifferent(inv.server.getStackInSlot(x), inv.client.getStackInSlot(x))) addItems(data, inv, x, 1);
            }
        }

        if(!data.isEmpty()) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeCompoundTag(data);
\            ServerPlayNetworking.send((ServerPlayerEntity) getPlayerInv().player, new Identifier("ae2wtlib", "interface_terminal"), buf);
            data = new CompoundNBT();
        }
    }

    @Override
    public void doAction(final ServerPlayerEntity player, final InventoryAction action, final int slot, final long id) {
        final WITContainer.InvTracker inv = byId.get(id);
        if(inv != null) {
            final ItemStack is = inv.server.getStackInSlot(slot);
            final boolean hasItemInHand = !player.inventory.getCursorStack().isEmpty();

            final InventoryAdaptor playerHand = new AdaptorFixedInv(new WrapperCursorItemHandler(player.inventory));

            // Create a wrapper around the targeted slot that will only allow insertions of
            // patterns
            LimitedFixedItemInv limitedSlotInv = inv.server.createLimitedFixedInv();
            limitedSlotInv.getAllRule().filterInserts(this::isValidPattern);
            SingleItemSlot theSlot = limitedSlotInv.getSlot(slot);

            switch(action) {
                case PICKUP_OR_SET_DOWN:
                    if(hasItemInHand) {
                        ItemStack inSlot = theSlot.get();
                        if(inSlot.isEmpty()) {
                            player.inventory.setCursorStack(theSlot.insert(player.inventory.getCursorStack()));
                        } else {
                            inSlot = inSlot.copy();
                            final ItemStack inHand = player.inventory.getCursorStack().copy();

                            theSlot.set(ItemStack.EMPTY);
                            player.inventory.setCursorStack(ItemStack.EMPTY);

                            player.inventory.setCursorStack(theSlot.insert(inHand.copy()));

                            if(player.inventory.getCursorStack().isEmpty()) {
                                player.inventory.setCursorStack(inSlot);
                            } else {
                                player.inventory.setCursorStack(inHand);
                                theSlot.set(inSlot);
                            }
                        }
                    } else theSlot.set(playerHand.addItems(theSlot.get()));
                    break;

                case SPLIT_OR_PLACE_SINGLE:
                    if(hasItemInHand) {
                        ItemStack extra = playerHand.removeItems(1, ItemStack.EMPTY, null);
                        if(!extra.isEmpty()) extra = theSlot.insert(extra);
                        if(!extra.isEmpty()) playerHand.addItems(extra);
                    } else if(!is.isEmpty()) {
                        ItemStack extra = theSlot.extract((is.getCount() + 1) / 2);
                        if(!extra.isEmpty()) extra = playerHand.addItems(extra);
                        if(!extra.isEmpty()) theSlot.insert(extra);
                    }
                    break;

                case SHIFT_CLICK:
                    final InventoryAdaptor playerInv = InventoryAdaptor.getAdaptor(player);
                    theSlot.put(playerInv.addItems(theSlot.get()));
                    break;

                case MOVE_REGION:
                    final InventoryAdaptor playerInvAd = InventoryAdaptor.getAdaptor(player);
                    for(int x = 0; x < inv.server.getSlots(); x++)
                        ItemHandlerUtil.setStackInSlot(inv.server, x, playerInvAd.addItems(inv.server.getStackInSlot(x)));
                    break;

                case CREATIVE_DUPLICATE:
                    if(player.isCreative() && !hasItemInHand)
                        player.inventory.setItemStack(is.isEmpty() ? ItemStack.EMPTY : is.copy());
                    break;

                default:
                    return;
            }

            updateHeld(player);
        }
    }

    private boolean isValidPattern(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof EncodedPatternItem;
    }

    private void regenList(final CompoundNBT data) {
        byId.clear();
        diList.clear();

        final IActionHost host = getActionHost();
        if(host != null) {
            final IGridNode agn = host.getActionableNode();
            if(agn != null && agn.isActive()) {
                for(final IGridNode gn : grid.getMachines(InterfaceTileEntity.class)) {
                    final IInterfaceHost ih = (IInterfaceHost) gn.getMachine();
                    final DualityInterface dual = ih.getInterfaceDuality();
                    if(gn.isActive() && dual.getConfigManager().getSetting(Settings.INTERFACE_TERMINAL) == YesNo.YES)
                        diList.put(ih, new WITContainer.InvTracker(dual, dual.getPatterns(), dual.getTermName()));
                }

                for(final IGridNode gn : grid.getMachines(InterfacePart.class)) {
                    final IInterfaceHost ih = (IInterfaceHost) gn.getMachine();
                    final DualityInterface dual = ih.getInterfaceDuality();
                    if(gn.isActive() && dual.getConfigManager().getSetting(Settings.INTERFACE_TERMINAL) == YesNo.YES)
                        diList.put(ih, new WITContainer.InvTracker(dual, dual.getPatterns(), dual.getTermName()));
                }
            }
        }

        data.putBoolean("clear", true);

        for(final Map.Entry<IInterfaceHost, WITContainer.InvTracker> en : diList.entrySet()) {
            final WITContainer.InvTracker inv = en.getValue();
            byId.put(inv.which, inv);
            addItems(data, inv, 0, inv.server.getSlotCount());
        }
    }

    private boolean isDifferent(final ItemStack a, final ItemStack b) {
        if(a.isEmpty() && b.isEmpty()) return false;
        if(a.isEmpty() || b.isEmpty()) return true;
        return !ItemStack.areEqual(a, b);
    }

    private void addItems(final CompoundNBT data, final WITContainer.InvTracker inv, final int offset, final int length) {
        final String name = '=' + Long.toString(inv.which, Character.MAX_RADIX);
        final CompoundNBT tag = data.getCompound(name);

        if(tag.isEmpty()) {
            tag.putLong("sortBy", inv.sortBy);
            tag.putString("un", ITextComponent.Serializer.toJson(inv.name));
        }

        for(int x = 0; x < length; x++) {
            final CompoundNBT itemNBT = new CompoundNBT();

            final ItemStack is = inv.server.getInvStack(x + offset);

            // "update" client side.
            ItemHandlerUtil.setStackInSlot(inv.client, x + offset, is.isEmpty() ? ItemStack.EMPTY : is.copy());

            if(!is.isEmpty()) is.toTag(itemNBT);

            tag.put(Integer.toString(x + offset), itemNBT);
        }
        data.put(name, tag);
    }

    private static class InvTracker {
        private final long sortBy;
        private final long which = autoBase++;
        private final ITextComponent name;
        private final IItemHandler client;
        private final IItemHandler server;

        public InvTracker(final DualityInterface dual, final IItemHandler patterns, final ITextComponent name) {
            this.server = patterns;
            this.client = new AppEngInternalInventory(null, server.getSlots());
            this.name = name;
            sortBy = dual.getSortValue();
        }
    }


    public boolean isWUT() {
        return witGUIObject.getItemStack().getItem() instanceof ItemWUT;
    }
}