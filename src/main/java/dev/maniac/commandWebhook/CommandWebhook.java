package dev.maniac.commandWebhook;

import dev.maniac.commandWebhook.config.ConfigHandler;
import dev.maniac.commandWebhook.server.Sender;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CommandWebhook.MODID)
public class CommandWebhook {
    // Directly reference a log4j logger.
    public static final String MODID = "commandwebhook";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public CommandWebhook() {
        // Register ourselves for server and other game events we are interested in
        FMLJavaModLoadingContext.get().getModEventBus().register(this);

        //Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(
                () -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true)
        );
    }

    @SubscribeEvent
    public void serverSetup(final FMLDedicatedServerSetupEvent event) {
        // some preinit code
        MinecraftForge.EVENT_BUS.register(new Sender());
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConfigHandler.SERVER_SPEC);
        LOGGER.info("Command Webhook events registered!");
    }
}
