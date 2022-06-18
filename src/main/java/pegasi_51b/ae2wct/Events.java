package pegasi_51b.ae2wct;

import net.minecraftforge.event.TickEvent;
import pegasi_51b.ae2wct.wirelesscraftingterminal.magnet_card.MagnetHandler;

public class Events {

    public static void serverTick(TickEvent.PlayerTickEvent e) {
        if (e.phase == TickEvent.Phase.START && !e.player.world.isRemote) {
            MagnetHandler.doMagnet(e.player);
        }
    }
}
