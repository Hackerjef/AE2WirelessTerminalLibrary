package pegasi_51b.ae2wct;

import appeng.api.features.IRegistryContainer;
import appeng.api.features.IWirelessTermRegistry;
import appeng.core.Api;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import pegasi_51b.ae2wct.client.ae2wctlibclient;
import pegasi_51b.ae2wct.init.Menus;
import pegasi_51b.ae2wct.init.ModItems;
import pegasi_51b.ae2wct.net.PacketHandler;

@Mod(value = AE2WirelessCraftingTerminal.MODID)
public class AE2WirelessCraftingTerminal {
    public static final String MODID = "ae2wctlib";

    public static final ItemGroup ITEM_GROUP = new ItemGroup(MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.CRAFTING_TERMINAL);
        }
    };

    public AE2WirelessCraftingTerminal() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addGenericListener(Item.class,this::items);
        bus.addGenericListener(ContainerType.class, Menus::menus);
        bus.addListener(this::common);
        if (FMLEnvironment.dist.isClient()) {
            bus.addListener(ae2wctlibclient::setup);
        }
    }

    private void common(FMLCommonSetupEvent e) {
        PacketHandler.registerPackets();
        IRegistryContainer iRegistryContainer = Api.instance().registries();

        IWirelessTermRegistry iWirelessTermRegistry = iRegistryContainer.wireless();

        iWirelessTermRegistry.registerWirelessHandler(ModItems.CRAFTING_TERMINAL);

    }


    public void items(RegistryEvent.Register<Item> e) {
        register("wireless_crafting_terminal", ModItems.CRAFTING_TERMINAL, e.getRegistry());
    }

    public static <T extends IForgeRegistryEntry<T>> T register(String name,T obj,IForgeRegistry<T> registry) {
        registry.register(obj.setRegistryName(new ResourceLocation(MODID,name)));
        return obj;
    }
}