package dev.maniac.commandWebhook.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import dev.maniac.commandWebhook.CommandWebhook;
import dev.maniac.commandWebhook.config.ConfigHandler;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class Sender {
    private final CloseableHttpClient http = HttpClientBuilder.create().build();
    private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    @SubscribeEvent
    public void onCommand(final CommandEvent event) {
        for (String webhookURL : ConfigHandler.SERVER.commandWebhookURLs.get()) {
            if (webhookURL.isEmpty()) {
                return;
            }

            String jsonMessage = gson.toJson(new WebhookCommand(
                    event.getParseResults().getContext().getSource().getDisplayName().getString(),
                    event.getParseResults().getReader().getString()
            ));

            HttpUriRequest postReq = RequestBuilder.post()
                    .setUri(webhookURL)
                    .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                    .setEntity(new StringEntity(jsonMessage, ContentType.APPLICATION_JSON))
                    .build();

            try {
                HttpResponse res = http.execute(postReq);

                if (res.getStatusLine().getStatusCode() >= 400) {
                    throw new Exception(EntityUtils.toString(res.getEntity()));
                }
            } catch (Exception e) {
                CommandWebhook.LOGGER.error(e.getMessage());
            }
        }
    }

    @SubscribeEvent
    public void onChatMessage(ServerChatEvent event) {
        for (String webhookURL : ConfigHandler.SERVER.chatWebhookURLs.get()) {
            if (webhookURL.isEmpty()) {
                return;
            }

            String jsonMessage = gson.toJson(new WebhookChat(event.getUsername(), event.getMessage()));

            HttpUriRequest postReq = RequestBuilder.post()
                    .setUri(webhookURL)
                    .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                    .setEntity(new StringEntity(jsonMessage, ContentType.APPLICATION_JSON))
                    .build();

            try {
                HttpResponse res = http.execute(postReq);

                if (res.getStatusLine().getStatusCode() >= 400) {
                    throw new Exception(EntityUtils.toString(res.getEntity()));
                }
            } catch (Exception e) {
                CommandWebhook.LOGGER.error(e.getMessage());
            }
        }
    }

    protected class WebhookCommand {
        @Expose
        public final List<Embed> embeds = new ArrayList<>(1);

        public WebhookCommand(String title, String description) {
            embeds.add(new Embed(title, description));
        }

        protected class Embed {
            @Expose
            public final String type = "rich";
            @Expose
            public final String title;
            @Expose
            public final String description;

            Embed(String title, String description) {
                this.title = title;
                this.description = description;
            }
        }
    }

    protected class WebhookChat {
        @Expose
        public final String username;
        @Expose
        public final String content;

        WebhookChat(String username, String content) {
            this.username = username;
            this.content = content;
        }
    }
}
