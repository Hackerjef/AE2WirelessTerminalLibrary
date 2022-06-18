package pegasi_51b.ae2wct.net;

import appeng.container.AEBaseContainer;
import appeng.container.ContainerLocator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.network.NetworkEvent;
import pegasi_51b.ae2wct.wirelesscraftingterminal.WirelessCraftingTerminalContainer;

import java.util.function.Supplier;

public class C2SSwitchGuiPacket {

    private String id;

    public C2SSwitchGuiPacket(String id) {
        this.id = id;
    }

    public C2SSwitchGuiPacket(PacketBuffer buf) {
        id = buf.readString(32767);
    }

    public void encode(PacketBuffer buf) {
        buf.writeString(id);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        PlayerEntity player = ctx.get().getSender();

        if (player == null) return;

        ctx.get().enqueueWork(  ()->  {
            MinecraftServer server = player.getServer();
            server.execute(() -> {
                final Container c = player.openContainer;
                if(!(c instanceof AEBaseContainer)) {
                    return;
                }
                AEBaseContainer container = (AEBaseContainer) c;
                final ContainerLocator locator = container.getLocator();
                if(locator == null) {
                    return;
                }
                switch(id) {
                    case "wireless_crafting_terminal":
                        WirelessCraftingTerminalContainer.openServer(player, locator);
                        break;
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }

}
