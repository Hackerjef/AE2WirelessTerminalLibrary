package pegasi_51b.ae2wct.net;

import appeng.container.ContainerLocator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.network.NetworkEvent;
import pegasi_51b.ae2wct.init.ModItems;
import pegasi_51b.ae2wct.util.ContainerHelper;
import pegasi_51b.ae2wct.wirelesscraftingterminal.WCTItem;

import java.util.function.Supplier;

public class C2SHotkeyPacket {

    private String terminalName;

    public C2SHotkeyPacket(String terminalName) {
        this.terminalName = terminalName;
    }

    public C2SHotkeyPacket(PacketBuffer buf) {
        terminalName = buf.readString(32767);
    }

    public void encode(PacketBuffer buf) {
        buf.writeString(terminalName);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        PlayerEntity player = ctx.get().getSender();

        if (player == null) return;

        ctx.get().enqueueWork(  ()->  {
                    MinecraftServer server = player.getServer();
            server.execute(() -> {
                if (terminalName.equalsIgnoreCase("crafting")) {
                    PlayerInventory inv = player.inventory;
                    int slot = -1;
                    for (int i = 0; i < inv.getSizeInventory(); i++) {
                        ItemStack terminal = inv.getStackInSlot(i);
                        if (terminal.getItem() instanceof WCTItem) {
                            slot = i;
                            break;
                        }
                    }
                    if (slot == -1) return;
                    ContainerLocator locator = ContainerHelper.getContainerLocatorForSlot(slot);
                    ModItems.CRAFTING_TERMINAL.open(player, locator);
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }

}
