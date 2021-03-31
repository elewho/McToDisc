package me.imnotdani.mctodisc;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

public final class Mctodisc extends JavaPlugin {

    private TextChannel serverChatChannel = null;
    private JDA jda = null;
    private final MinecraftListener minecraftListener = new MinecraftListener(this);
    private String discordBotToken = "",  minecraftServerChatChannelID ="", whitelistChannelID = "", botChannelID = "'";

    @Override
    public void onEnable() {
        try{
            loadConfig();
            System.out.println("McToDisc plugin is now starting.");
            getServer().getPluginManager().registerEvents(minecraftListener, this);
            jda = JDABuilder.createDefault("", GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS)
                        .addEventListeners(new DiscordListener(this))
                        .setActivity(Activity.watching(Bukkit.getOnlinePlayers().size() + " gaymers online!"))
                        .build();
                jda.awaitReady();

                serverChatChannel = jda.getTextChannelById(minecraftServerChatChannelID);

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

    private void loadConfig() {
        this.saveDefaultConfig();

        FileConfiguration config = this.getConfig();

        if(!config.contains("DiscordBotToken")){
            config.set("DiscordBotToken", "");
        }
        discordBotToken = this.getConfig().getString("DiscordBotToken");

        if(!config.contains("MinecraftServerChatChannelID")){
            config.set("MinecraftServerChatChannelID", "");
        }
        minecraftServerChatChannelID = config.getString("MinecraftServerChatChannelID");

        if(!config.contains("WhitelistChannelID")){
            config.set("WhitelistChannelID", "");
        }
        whitelistChannelID = config.getString("WhitelistChannelID");

        if(!config.contains("BotChannelID")){
            config.set("BotChannelID", "");
        }
        botChannelID = config.getString("BotChannelID");

        config.options().copyDefaults(true);
        this.saveConfig();
    }

    public String getWhitelistChannelID(){
        return whitelistChannelID;
    }

    public String getMinecraftServerChatChannelID(){
        return minecraftServerChatChannelID;
    }

    public String getBotChannelID(){ return botChannelID; }

    /**
     * Sends Minecraft messages to Discord
     *
     * @param user - Person sending the message
     * @param msg -  Message sent on Minecraft
     * @param i - switch case
     *
     * 1: normal chat message
     * 2: advancement message
     *
     */
    public void sendToDiscord(String user, String msg, int i){
        switch(i){
            case 1: serverChatChannel.sendMessage("**" + user + ":** " + msg).queue(); break;
            //case 2: serverChatChannel.sendMessage(":trophy: **" + user + " has made the advancement [" + msg + "]!**").queue(); break;
            default: break;

        }
    }

    /**
     * Sends Minecraft messages to the Discord
     *
     * @param s- Passed String; can either be name or death message
     * @param i - switch case number
     * 1: join, 2: leave, 3: death
     *
     */
    public void sendToDiscord(String s, int i){
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
            Bukkit.getScheduler().runTask(this, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist add " + user));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<String> getOnlinePlayers(){
        ArrayList<Player> onlinePlayers = new ArrayList(Bukkit.getOnlinePlayers());
        ArrayList<String> playerNames = new ArrayList<String>();
        System.out.println(onlinePlayers);

        if (onlinePlayers != null) {
            for (Player p : onlinePlayers) {
                playerNames.add(p.getName());
            }
            return playerNames;
        }
        return null;
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
    public void setBotStatus(){
        jda.getPresence().setActivity(Activity.watching(Bukkit.getOnlinePlayers().size() + " gaymers online!"));
    }

    public OfflinePlayer getPlayer(String user) {
        OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(user);
        if (offlinePlayer != null) {
            return offlinePlayer;
        }
        return null;
    }
}