package pegasi_51b.ae2wct.init;

import appeng.container.ContainerLocator;
import appeng.container.ContainerOpener;
import appeng.core.Api;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.event.RegistryEvent;
import pegasi_51b.ae2wct.WTConfig;
import pegasi_51b.ae2wct.wirelesscraftingterminal.WirelessCraftingTerminalContainer;
import pegasi_51b.ae2wct.AE2WirelessCraftingTerminal;

public class Menus {
    public static ContainerType<WirelessCraftingTerminalContainer> WCT = new ContainerType<>(WirelessCraftingTerminalContainer::openClient);

    public static void menus(RegistryEvent.Register<ContainerType<?>> e) {
        AE2WirelessCraftingTerminal.register("wireless_crafting_terminal",WCT,e.getRegistry());

        ContainerOpener.addOpener(WCT, (new CheckedOpener(WirelessCraftingTerminalContainer::openServer))::open);

        Api.instance().registries().charger().addChargeRate(ModItems.CRAFTING_TERMINAL, WTConfig.getChargeRate());
    }

    private static class CheckedOpener {
        private final UncheckedOpener opener;
        public CheckedOpener(UncheckedOpener opener) {
            this.opener = opener;
        }

        public boolean open(PlayerEntity player, ContainerLocator locator) {
            if (!(player instanceof ServerPlayerEntity)) {
                // Cannot open containers on the client or for non-players
                return false;
            }

            if (!locator.hasItemIndex()) {
                return false;
            }

            opener.open(player, locator);
            return true;
        }
    }

    @FunctionalInterface
    public interface UncheckedOpener {
        void open(PlayerEntity player, ContainerLocator locator);
    }
}
