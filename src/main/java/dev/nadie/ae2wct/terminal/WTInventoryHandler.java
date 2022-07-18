package dev.nadie.ae2wct.terminal;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class WTInventoryHandler implements IItemHandlerModifiable {

    public static final int OFFHAND = 4;

    private final PlayerInventory playerInventory;
    private final ItemStack wt;
    private final Object host;

    private static final int slotOffset = 36;
    private static final int offHandSlot = 40;

    public WTInventoryHandler(PlayerInventory playerInventory, ItemStack wt, Object host) {
        this.playerInventory = playerInventory;
        this.wt = wt;
        this.host = host;
    }

    @Override
    public int getSlots() {
        return 8;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        if (i < 4 && i >= 0) {
            return playerInventory.getStackInSlot(i + slotOffset);
        } else if (i == OFFHAND) return playerInventory.getStackInSlot(offHandSlot);
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isItemValid(int i, ItemStack itemStack) {
        if (i == OFFHAND) return playerInventory.isItemValidForSlot(offHandSlot, itemStack);
        return false;
    }

    @Override
    public ItemStack insertItem(int i, ItemStack itemStack, boolean simulation) {
        if (i < 4 && i >= 0) {
            if (!simulation) playerInventory.setInventorySlotContents(i + slotOffset, itemStack);
            return ItemStack.EMPTY;
        } else if (i == OFFHAND) {
            if (!simulation) playerInventory.setInventorySlotContents(offHandSlot, itemStack);
            return ItemStack.EMPTY;
        }
        return itemStack;
    }

    @Override
    public ItemStack extractItem(int slot, int maxCount, boolean simulation) {
        if (maxCount == 0)
            return ItemStack.EMPTY;

        ItemStack existing = getStackInSlot(slot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        int toExtract = Math.min(maxCount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract) {
            if (!simulation) {
                setStackInSlot(slot, ItemStack.EMPTY);
                //onContentsChanged(slot);
                return existing;
            } else {
                return existing.copy();
            }
        } else {
            if (!simulation) {
                setStackInSlot(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                //onContentsChanged(slot);
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        if (slot < 4 && slot >= 0) {
            playerInventory.setInventorySlotContents(slot + slotOffset, stack);
        } else if (slot == OFFHAND) {
            playerInventory.setInventorySlotContents(offHandSlot, stack);
        }
    }
}