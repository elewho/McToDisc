package me.imnotdani.mctodisc;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MinecraftListener implements Listener {

    private final Mctodisc mctodisc;

    public MinecraftListener(Mctodisc mctodisc) {
        this.mctodisc = mctodisc;
    }

    @EventHandler
    private void onServerMessageReceived(AsyncChatEvent e){
        int i = 1;
        final var msg = PlainTextComponentSerializer.plainText().serialize(e.message());
        mctodisc.sendToDiscord(e.getPlayer().getName(), msg, i);
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
        mctodisc.sendToDiscord(e.getDeathMessage(), i);
    }

    @EventHandler
    private void onPlayerAdvancement(PlayerAdvancementDoneEvent e){
        try{
           int i = 4;
           if(e.getAdvancement().getDisplay() != null && e.getAdvancement().getDisplay().doesAnnounceToChat()){
               final var s = PlainTextComponentSerializer.plainText().serialize(e.message());
               mctodisc.sendToDiscord(s, i);
           }
        } catch (NullPointerException npe){
        }
    }
}
