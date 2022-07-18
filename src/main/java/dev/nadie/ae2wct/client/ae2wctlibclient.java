package dev.nadie.ae2wct.client;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.ScreenRegistration;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.StyleManager;
import appeng.container.AEBaseContainer;
import dev.nadie.ae2wct.init.Menus;
import dev.nadie.ae2wct.wirelesscraftingterminal.WirelessCraftingTerminalScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.io.FileNotFoundException;

public class ae2wctlibclient {

    public static void setup(FMLClientSetupEvent e) {
        register(Menus.WCT, WirelessCraftingTerminalScreen::new, "/screens/wtlib/wireless_crafting_terminal.json");
    }

    /**
     * Registers a screen for a given container and ensures the given style is applied after opening the screen.
     */
    private static <M extends AEBaseContainer, U extends AEBaseScreen<M>> void register(ContainerType<M> type,
                                                                                        ScreenRegistration.StyledScreenFactory<M, U> factory,
                                                                                        String stylePath) {
       // CONTAINER_STYLES.put(type, stylePath);
        ScreenManager.<M,U>registerFactory(type, (container, playerInv, title) -> {
            ScreenStyle style;
            try {
                style = StyleManager.loadStyleDoc(stylePath);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Failed to read Screen JSON file: " + stylePath + ": " + e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException("Failed to read Screen JSON file: " + stylePath, e);
            }

            return factory.create(container, playerInv, title, style);
        });
    }

}