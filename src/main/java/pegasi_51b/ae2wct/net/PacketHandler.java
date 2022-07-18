package pegasi_51b.ae2wct.net;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import pegasi_51b.ae2wct.AE2WirelessCraftingTerminal;

public class PacketHandler {

    public static SimpleChannel INSTANCE;
    static int i = 0;

    public static void registerPackets() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(AE2WirelessCraftingTerminal.MODID, AE2WirelessCraftingTerminal.MODID), () -> "1.0", s -> true, s -> true);

        INSTANCE.registerMessage(i++, C2SCycleTerminalPacket.class,
                (message, buffer) -> {},
                buffer -> new C2SCycleTerminalPacket(),
                C2SCycleTerminalPacket::handle);

        INSTANCE.registerMessage(i++, C2SSwitchGuiPacket.class,
                C2SSwitchGuiPacket::encode,
                C2SSwitchGuiPacket::new,
                C2SSwitchGuiPacket::handle);
    }
}
