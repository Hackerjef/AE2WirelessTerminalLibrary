package pegasi_51b.ae2wct.client;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.ScreenRegistration;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.StyleManager;
import appeng.container.AEBaseContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import pegasi_51b.ae2wct.init.Menus;
import pegasi_51b.ae2wct.wirelesscraftingterminal.WirelessCraftingTerminalScreen;

import java.io.FileNotFoundException;

public class ae2wctlibclient {

    public static void setup(FMLClientSetupEvent e) {
        register(Menus.WCT, WirelessCraftingTerminalScreen::new,"/screens/wtlib/wireless_crafting_terminal.json");

  /*      ClientPlayNetworking.registerGlobalReceiver(new Identifier("ae2wtlib", "interface_terminal"), (client, handler, buf, responseSender) -> {
            buf.retain();
            client.execute(() -> {
                if (client.player == null) return;

                final Screen screen = MinecraftClient.getInstance().currentScreen;
                if (screen instanceof WITScreen) {
                    WITScreen s = (WITScreen) screen;
                    CompoundNBT tag = buf.readCompoundTag();
                    if (tag != null) s.postUpdate(tag);
                }
                buf.release();
            });
        });*/
        //registerKeybindings();
    }

  /*  static KeyBinding wct = new KeyBinding("key.ae2wtlib.wct", GLFW.GLFW_KEY_UNKNOWN, "key.category.ae2wtlib");
    static KeyBinding wpt = new KeyBinding("key.ae2wtlib.wpt", GLFW.GLFW_KEY_UNKNOWN, "key.category.ae2wtlib");
    static KeyBinding wit = new KeyBinding("key.ae2wtlib.wit", GLFW.GLFW_KEY_UNKNOWN, "key.category.ae2wtlib");

    public static void registerKeybindings() {
        ClientRegistry.registerKeyBinding(wct);
        ClientRegistry.registerKeyBinding(wpt);
        ClientRegistry.registerKeyBinding(wit);
    }

    public static void clientTick(TickEvent.ClientTickEvent e) {
        if (e.phase == TickEvent.Phase.END) {
            while (wct.isPressed()) {
      //          PacketHandler.INSTANCE.sendToServer(new C2SHotkeyPacket("crafting"));
            }
            while (wpt.isPressed()) {
     //           PacketHandler.INSTANCE.sendToServer(new C2SHotkeyPacket("pattern"));

            }
            while (wit.isPressed()) {
          //      PacketHandler.INSTANCE.sendToServer(new C2SHotkeyPacket("interface"));
            }
        }
    }*/


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