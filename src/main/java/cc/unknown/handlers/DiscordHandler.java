package cc.unknown.handlers;

import java.util.UUID;

import com.sun.jna.Platform;

import cc.unknown.Sakura;
import cc.unknown.util.Accessor;
import cc.unknown.util.client.ServerUtil;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiSelectWorld;

public class DiscordHandler implements Accessor {
    public boolean running = true;
    private long timeElapsed = 0;
    private volatile String discordUser = "";
    private final String joinSecret = UUID.randomUUID().toString();
    private final String spectateSecret = UUID.randomUUID().toString();

    public void start() {
        if (Platform.isWindows()) {
            this.timeElapsed = System.currentTimeMillis();
            
            DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler(user -> {
                this.discordUser = user.username;
            }).build();

            DiscordRPC.discordInitialize("1305938480802828350", handlers, true);

            new Thread("Discord RPC Callback") {
                @Override
                public void run() {
                    while (running) {
                        if (mc.thePlayer != null) {
                            if (mc.isSingleplayer()) {
                                update("", "in SinglePlayer");
                            } else if (ServerUtil.fetchAndFindServerData(mc.getCurrentServerData().serverIP)) {
                                update(getDiscordUser(), "Cheating on " + ServerUtil.serverName);
                            } else if (mc.currentScreen instanceof GuiDownloadTerrain) {
                                update("Loading World...", "");
                            }
                        } else {
                            if (mc.currentScreen instanceof GuiSelectWorld) {
                                update("Selecting World...", "");
                            } else if (mc.currentScreen instanceof GuiMultiplayer) {
                                update("In Multiplayer...", "");
                            } else if (mc.currentScreen instanceof GuiDownloadTerrain) {
                                update("Loading World...", "");
                            } else {
                                update("In MainMenu...", "");
                            }
                        }

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                        DiscordRPC.discordRunCallbacks();
                    }
                }
            }.start();
        }
    }

    public void stop() {
        running = false;
        DiscordRPC.discordShutdown();
    }

    public void update(String line1, String line2) {
        DiscordRichPresence.Builder rpc = new DiscordRichPresence.Builder(line2)
            .setDetails(line1)
            .setBigImage("sakura", "Sakura Client v" + Sakura.VERSION)
            .setParty("discord.gg/MuF4YRQFht", 1, 2000)
            .setSecrets(joinSecret, spectateSecret)
            .setStartTimestamps(timeElapsed);
        
        DiscordRPC.discordUpdatePresence(rpc.build());
    }

    private String getDiscordUser() {
        return discordUser.isEmpty() ? "Waiting for Discord..." : "User: " + discordUser;
    }
}
