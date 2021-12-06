package me.imnotdani.mctodisc;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;

import java.util.List;

public class DiscordListener extends ListenerAdapter {

    private final Mctodisc mctodisc;
    public DiscordListener(Mctodisc mctodisc){
        this.mctodisc = mctodisc;
    }

    /**
     * Waits for message to be sent in a specific text channel.
     *
     * @param event - for any guild messages read by bot
     */
    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        boolean isBot = event.getAuthor().isBot();
        TextChannel textChannel = event.getChannel();
        String user = event.getAuthor().getName(), rawMsg = event.getMessage().getContentRaw(), channelID = textChannel.getId();
        if(!isBot){
            try{
                if(channelID.equals(mctodisc.getMinecraftServerChatChannelID())){
                    mctodisc.sendToServer(user, rawMsg);
                }

                if(channelID.equals(mctodisc.getBotChannelID())){
                    if(rawMsg.startsWith("!")){
                        String []msgSplit = rawMsg.split(" ");
                        int size = msgSplit.length;
                        switch(msgSplit[0]){
                            case "!mhelp": listCommands(textChannel); break;
                            case "!mstats": if(size!= 2){
                                textChannel.sendMessage("Wrong parameters given. Please try again.").queue();
                                break;
                            } else{ listStats(msgSplit[1], textChannel); } break;
                            case "!mlistOn": listOnlinePlayers(textChannel); break;
                            default: break;
                        }
                    }
                }
            } catch(InsufficientPermissionException ipe){
                ipe.printStackTrace();
            }
        }
    }

    /**
     * Waits for a reaction to be added in a specific text channel. Only mods should be able to add reaction.
     *
     * @param event - sees when there's a reaction
     */
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event){
        boolean isBot = event.getUser().isBot();
        TextChannel channel = event.getChannel();
        String channelID  = channel.getId(), rawMsg = event.retrieveMessage().complete().getContentRaw();
        int sizeOfMsg = rawMsg.split(" ").length; //Checks that message is ONLY a username.

        if(channelID.equals(mctodisc.getWhitelistChannelID())) {
            if (sizeOfMsg > 1) {
                channel.sendMessage("Please make sure that you only enter your in-game name and nothing else.").queue();
                return;
            }

            if(!isBot && sizeOfMsg == 1){
                mctodisc.whitelistUser(rawMsg);
            }
        }
    }

    private void listCommands(TextChannel channel){
        channel.sendMessage("**List of available commands:**\n\n" +
                "`!mlistOn` - lists the names of players online right now\n" +
                "`!mstats <username>` - lists statistics of a given player (eg. `!mstats artemercy`)\n" +
                "`!mhelp` - to bring up this menu").queue();
    }

    private void listStats(String user, TextChannel channel){
        try{
            OfflinePlayer player = mctodisc.getPlayer(user);

            channel.sendMessage("**Player:** " + user +
                    "\n\n:skull_crossbones:  **Deaths:** " + player.getStatistic(Statistic.DEATHS) +
                    "\n\n:dancer:  **Times jumped:** " + player.getStatistic(Statistic.JUMP) +
                    "\n\n:person_kneeling:  **Sneak time:** " + player.getStatistic(Statistic.SNEAK_TIME) +
                    "\n\n:money_with_wings:  **Villager trades:** " + player.getStatistic(Statistic.TRADED_WITH_VILLAGER) +
                    "\n\n:cake:  **Cake slices eaten: ** " + player.getStatistic(Statistic.CAKE_SLICES_EATEN)).queue();
        } catch(NullPointerException npe){
            channel.sendMessage("Player not found.").queue();
            npe.printStackTrace();
        }
    }

    private void listOnlinePlayers(TextChannel channel){
        List<String> playerNames = mctodisc.getOnlinePlayers();
        int size = playerNames.size();
        StringBuilder toPrint;

        if(size == 0){
            toPrint = new StringBuilder("There are currently no players online right now.");
        } else if (size == 1) {
            toPrint = new StringBuilder("There is only 1 player online: " + playerNames.get(0));
        } else{
            toPrint = new StringBuilder("There are currently **" + size + " players** online right now:\n");
            for(String s : playerNames){
                toPrint.append(s).append("\n");
            }
        }
        channel.sendMessage(toPrint.toString()).queue();
    }
}