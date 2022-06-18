package pegasi_51b.ae2wct.wirelesscraftingterminal.magnet_card;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import pegasi_51b.ae2wct.wirelesscraftingterminal.CraftingTerminalHandler;

import java.util.List;

public class MagnetHandler {
    public static void doMagnet(PlayerEntity player) {
        ItemStack magnetCardHolder = CraftingTerminalHandler.getCraftingTerminalHandler(player).getCraftingTerminal();
        if (ItemMagnetCard.isActiveMagnet(magnetCardHolder)) {
            List<ItemEntity> entityItems = player.world.getEntitiesWithinAABB(ItemEntity.class, player.getBoundingBox().grow(16.0D));
            for (ItemEntity entityItemNearby : entityItems) {
                if (!player.isSneaking()) {
                    entityItemNearby.onCollideWithPlayer(player);
                }
            }
        }
    }
}