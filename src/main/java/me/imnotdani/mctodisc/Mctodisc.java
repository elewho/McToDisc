package me.imnotdani.mctodisc;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.util.Locale;

public final class Mctodisc extends JavaPlugin implements Listener {

    private TextChannel serverChatChannel = null;
    private JDA jda = null;

    private String DiscordBotToken = "";
    private String MinecraftServerChatChannelID ="";
    private String WhitelistChannelID = "";

    @Override
    public void onEnable() {
        try{
            loadConfig();
            System.out.println(MinecraftServerChatChannelID);

            System.out.println("McToDisc plugin is now starting.");
            getServer().getPluginManager().registerEvents(this, this);

                jda = JDABuilder.createDefault("ODI1MTQ1MzE4OTcwMzU5ODE5.YF5qdg.Nr_8yBesA0e3KBePqmqehPUSKT0", GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS)
                        .addEventListeners(new DiscordListener(this))
                        .setActivity(Activity.watching(Bukkit.getOnlinePlayers().size() + " gaymers online!"))
                        .build();
                jda.awaitReady();

                serverChatChannel = jda.getTextChannelById(MinecraftServerChatChannelID);

        } catch (LoginException | InterruptedException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        try{
            jda.shutdown();
            System.out.println("McToDisc plugin is now shutting down.");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private FileConfiguration loadConfig() {
        this.saveDefaultConfig();

        FileConfiguration config = this.getConfig();

        if(!this.getConfig().contains("DiscordBotToken")){
            config.set("DiscordBotToken", "");
        }
        DiscordBotToken = this.getConfig().getString("DiscordBotToken");

        if(!this.getConfig().contains("MinecraftServerChatChannelID")){
            config.set("MinecraftServerChatChannelID", "");
        }
        MinecraftServerChatChannelID = config.getString("MinecraftServerChatChannelID");

        if(!this.getConfig().contains("WhitelistChannelID")){
            config.set("WhitelistChannelID", "");
        }
        WhitelistChannelID = config.getString("WhitelistChannelID");

        config.options().copyDefaults(true);
        this.saveConfig();

        return config;
    }

    public String getWhitelistChannelID(){
        return WhitelistChannelID;
    }

    public String getMinecraftServerChatChannelID(){
        return MinecraftServerChatChannelID;
    }


    /**
     * Sends Minecraft messages to Discord
     *
     * @param user - Person sending the message
     * @param msg -  Message sent on Minecraft
     */
    private void sendToDiscord(String user, String msg){
        serverChatChannel.sendMessage("**" + user + ":** " + msg).queue();
    }

    /**
     * Sends Minecraft messages to the Discord
     *
     * @param s - Passed String; can either be name or death message
     * @param i - switch case number
     */
    private void sendToDiscord(String s, int i){
        switch(i){
            case 1: serverChatChannel.sendMessage("**" + s + " has joined the server!**").queue(); break;
            case 2: serverChatChannel.sendMessage("**" + s + " has left the server!**").queue();
                jda.getPresence().setActivity((Activity.watching(Bukkit.getOnlinePlayers().size() - 1 + " gaymers online!")));
                break;
            case 3: serverChatChannel.sendMessage(":skull_crossbones: **" + s + "**.").queue(); break;
            default: break;
        }
    }

    /**
     * Sends the whitelist command to the server console. It does not check if the username is valid.
     *
     * @param user - passed username
     */
    public void whitelistUser(String user){
        try{
            Bukkit.getScheduler().runTask(this, new Runnable(){
                @Override
                public void run(){
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist add " + user);
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Sends Discord messages to the Minecraft server.
     *
     * @param user - Person sending the message
     * @param msg - Message being sent
     */
    public void sendToServer(String user, String msg){
        Bukkit.broadcastMessage(ChatColor.BLUE + "[D] " + ChatColor.WHITE + user + ": " + msg);
    }

    /**
     * Sets the Discord bot's status.
     */
    private void setBotStatus(){
        jda.getPresence().setActivity(Activity.watching(Bukkit.getOnlinePlayers().size() + " gaymers online!"));
    }

    /* ***************************************************************************
    Event Handlers. Handles any events that happen in the Minecraft Server.
     ***************************************************************************** */

    /**
     * Handles normal chat messages sent by players.
     *
     * @param e - triggers when a player talks in the Minecraft chat.
     */
    @EventHandler
    private void onServerMessageReceived(AsyncPlayerChatEvent e){
        sendToDiscord(e.getPlayer().getName(), e.getMessage());
    }

    /**
     * Handles players joining.
     *
     * @param e - triggers when a player joins the server.
     */
    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e){
        int i = 1;
        sendToDiscord(e.getPlayer().getName(), i);
        setBotStatus();
    }

    /**
     * Handles players leaving.
     *
     * @param e - triggers when a player leaves the server.
     */
    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent e){
        int i = 2;
        sendToDiscord(e.getPlayer().getName(), i);
    }

    /**
     * Handles players dying.
     *
     * @param e - triggers when a player dies.
     */
    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent e){
        int i = 3;
        sendToDiscord((e.getDeathMessage().toLowerCase(Locale.ROOT)), i);
    }

}