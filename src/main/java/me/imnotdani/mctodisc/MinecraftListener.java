package me.imnotdani.mctodisc;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Locale;
import java.util.Objects;

public class MinecraftListener implements Listener {

    private final Mctodisc mctodisc;

    public MinecraftListener(Mctodisc mctodisc) {
        this.mctodisc = mctodisc;
    }

    @EventHandler
    private void onServerMessageReceived(AsyncPlayerChatEvent e){
        int i = 1;
        mctodisc.sendToDiscord(e.getPlayer().getName(), e.getMessage(), i);
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e){
        int i = 1;
        mctodisc.sendToDiscord(e.getPlayer().getName(), i);
        mctodisc.setBotStatus();
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent e){
        int i = 2;
        mctodisc.sendToDiscord(e.getPlayer().getName(), i);
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent e) {
        int i = 3;
        mctodisc.sendToDiscord((Objects.requireNonNull(e.getDeathMessage()).toLowerCase(Locale.ROOT)), i);
    }
}
