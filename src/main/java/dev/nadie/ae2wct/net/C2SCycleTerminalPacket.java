package dev.nadie.ae2wct.net;

import appeng.container.AEBaseContainer;
import appeng.container.ContainerLocator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SCycleTerminalPacket {

    public void encode(PacketBuffer buf) {

    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        PlayerEntity player = ctx.get().getSender();

        if (player == null) return;

        ctx.get().enqueueWork(  ()->  {
            final Container screenHandler = player.openContainer;

            if(!(screenHandler instanceof AEBaseContainer)) return;

            final AEBaseContainer container = (AEBaseContainer) screenHandler;
            final ContainerLocator locator = container.getLocator();
            ItemStack item = player.inventory.getStackInSlot(locator.getItemIndex());
        });
        ctx.get().setPacketHandled(true);
    }

}
