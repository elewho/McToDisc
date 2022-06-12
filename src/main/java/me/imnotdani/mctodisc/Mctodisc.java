package me.imnotdani.mctodisc;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class Mctodisc extends JavaPlugin {

    private TextChannel serverChatChannel = null;
    private final Logger logger = this.getLogger();
    private JDA jda = null;
    private final MinecraftListener minecraftListener = new MinecraftListener(this);
    private String discordBotToken = "",  minecraftServerChatChannelID ="", whitelistChannelID = "", botChannelID = "'";

    @Override
    public void onEnable() {
        try{
            loadConfig();
            getServer().getPluginManager().registerEvents(minecraftListener, this);
            jda = JDABuilder.createDefault(discordBotToken, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS)
                    .disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOTE)
                    .addEventListeners(new DiscordListener(this))
                        .setActivity(Activity.watching(Bukkit.getOnlinePlayers().size() + " gaymers online!"))
                        .build();
                jda.awaitReady();
                serverChatChannel = jda.getTextChannelById(minecraftServerChatChannelID);
                serverChatChannel.sendMessage("The server is starting up! Hold on tight.").queue();
        } catch (LoginException | InterruptedException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        try{
            serverChatChannel.sendMessage("The server is shutting down.").queue();
            jda.shutdown();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadConfig() {
        logger.info("**** Checking if Mctodisc config file exists. ****");
        File configFile = new File(getDataFolder(), "config.yml");

        if(!configFile.exists()){
            logger.info("**** Config file not found. Creating Mctodisc config. ****");
            saveResource("config.yml", false);
        }
        logger.info("**** Loading Mctodisc config. ****");
        this.saveDefaultConfig();
        FileConfiguration config = this.getConfig();

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

    /**
     * Sends normal chat messages to Discord
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
//            case 2: serverChatChannel.sendMessage(":exploding_head: **" + user + " " + msg + "**").queue(); break;
            default: break;
        }
    }

    /**
     * Sends Minecraft events messages to the Discord
     *
     * @param s- Passed String; can either be name, death message or advnacement
     * @param i - switch case number
     * 1: join, 2: leave, 3: death, 4: advancement
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
        return getServer().getOfflinePlayer(user);
    }

    public List<String> getOnlinePlayers(){
        ArrayList<Player> onlinePlayers = new ArrayList(Bukkit.getOnlinePlayers());
        ArrayList<String> playerNames = new ArrayList<>();

        for (Player p : onlinePlayers) {
            playerNames.add(p.getName());
        }
        return playerNames;
    }

    public String getWhitelistChannelID(){
        return whitelistChannelID;
    }

    public String getMinecraftServerChatChannelID(){
        return minecraftServerChatChannelID;
    }

    public String getBotChannelID(){ return botChannelID; }
}
