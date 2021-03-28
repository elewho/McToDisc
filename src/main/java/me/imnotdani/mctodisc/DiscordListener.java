package me.imnotdani.mctodisc;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordListener extends ListenerAdapter {

    private Mctodisc mctodisc;
    public DiscordListener(Mctodisc mctodisc){
        this.mctodisc = mctodisc;
    }

    /**
     * Waits for message to be sent in a specific text channel.
     *
     * @param event
     */
    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        boolean isBot = event.getAuthor().isBot();
        String user = event.getAuthor().getName(), rawMsg = event.getMessage().getContentRaw(), channel = event.getChannel().getId();

        if(!isBot){
            try{
                if(channel.equals(mctodisc.getMinecraftServerChatChannelID())){
                    mctodisc.sendToServer(user, rawMsg);
                }
            } catch(InsufficientPermissionException ipe){
                ipe.printStackTrace();
            }
        }
    }

    /**
     * Waits for a reaction to be added in a specific text channel. Only mods should be able to add reaction.
     *
     * @param event
     */
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event){
        boolean isBot = event.getUser().isBot();
        TextChannel channel = event.getChannel();
        String channelID  = event.getChannel().getId();
        String rawMsg = event.retrieveMessage().complete().getContentRaw();
        int sizeofMsg = event.retrieveMessage().complete().getContentRaw().split(" ").length; //Checks that message is ONLY a username.

        if(channelID.equals(mctodisc.getWhitelistChannelID())) {
            if (sizeofMsg > 1) {
                channel.sendMessage("Please make sure that you only enter your in-game name and nothing else.").queue();
                return;
            }

            if(!isBot && sizeofMsg == 1){
                mctodisc.whitelistUser(rawMsg);
            }
        }
    }
}