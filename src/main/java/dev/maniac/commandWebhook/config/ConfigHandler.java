package dev.maniac.commandWebhook.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class ConfigHandler {
    private static ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

    public static final Server SERVER = new Server(SERVER_BUILDER);

    public static final ForgeConfigSpec SERVER_SPEC = SERVER_BUILDER.build();

    public static class Server {
        public final ForgeConfigSpec.ConfigValue<List<String>> commandWebhookURLs;
        public final ForgeConfigSpec.ConfigValue<List<String>> chatWebhookURLs;

        Server(ForgeConfigSpec.Builder builder) {
            builder.push("Server");

            String descCmd = "Webhook URLs for the mod to send command usages to";
            commandWebhookURLs = builder.comment(descCmd).define("commandWebhookURLs", new ArrayList<>());

            String descChat = "Webhook URLs for the mod to send chat to";
            chatWebhookURLs = builder.comment(descChat).define("chatWebhookURLs", new ArrayList<>());

            builder.pop();
        }
    }
}
