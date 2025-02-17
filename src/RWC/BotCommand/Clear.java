package RWC.BotCommand;

import java.util.List;

import RWC.Bot.Config;

/**
* Some changes
*/

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.internal.utils.PermissionUtil;

/**
 * Clear class for the clear command
 * Can only be used by a user with the MESSAGE_MANAGE permission 
 * @author steum
 *
 */
public class Clear extends AbstractCommand{
	/**
	 * Method to run event of clear command
	 */
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		
		//command entered by user
		String[] args = event.getMessage().getContentRaw().split("\\s+");
		
		//checking command syntax
		if(args[0].equalsIgnoreCase(Config.prefix + getName())) {
			//Embed to print messages
			EmbedBuilder clear = new EmbedBuilder();
			
			//check if command has the right number of arguments
			if(args.length < 2) {
				
				clear.setTitle("⚠Syntax Error");
				clear.setDescription("Please tell me how many messages I should delete");
				clear.addField("Example", Config.prefix + "" + "clear 50",false);
				clear.setColor(0xeb3434);
				
				event.getChannel().sendMessage(clear.build()).queue();
				clear.clear();
			} else if (!PermissionUtil.checkPermission(event.getChannel(),event.getMember(), Permission.MESSAGE_MANAGE)) {
				event.getChannel().sendMessage("You do not have permission to use this command").queue();
			}
			else {
				
				// try & catch blocks because discord doesn't allow to delete more than 100 messages or messages older than 2 weeks
				try {
					
				// retrieve message of the channel where command was written	
				List<Message> messages = event.getChannel().getHistory().retrievePast(Integer.parseInt(args[1]) + 1).complete();
				/*
				 * Deletes messages depending on amount.
				 * If the amount of messages to be deleted are between 2-100, call deleteMessages(Collection<Message> messages)
				 * else, call Message.delete(), since the former cannot delete a single message.
				 */
				if (messages.size() == 2) {
					messages.get(1).delete().queue();
				} else {
					event.getChannel().deleteMessages(messages.subList(1, messages.size())).queue();
				}
				
				//success in deletion
				clear.setTitle("✅SUCCESS");
				clear.setDescription("Successfully deleted. Hooray!🎉");
				clear.setColor(0x34eb6e);
				
				clear.setFooter("Here you go!",event.getMember().getUser().getAvatarUrl());
				event.getChannel().sendMessage(clear.build()).queue();
				clear.clear();
				
				}
				catch(IllegalArgumentException e) {
					//Embeds to print messages in case of error (more than 100 messages or > 2 weeks old)
					if(e.toString().startsWith("java.lang.IllegalArgumentException: Message retrieval")) {
						EmbedBuilder error = new EmbedBuilder();
						error.setTitle("🛑Too many messages");
						error.setDescription("I can't delete more than 100 messages. Sorry!😁");
						error.setColor(0xeb3434);
						event.getChannel().sendMessage(error.build()).queue();
						error.clear();
					}
					else {
						EmbedBuilder error = new EmbedBuilder();
						error.setTitle("❌Old messages");
						error.setDescription("Either you just asked me to delete a 2 weeks or older message or there are no messages left for me to delete. Hmm?🤔");
						error.setColor(0xeb3434);
						event.getChannel().sendMessage(error.build()).queue();
						error.clear();
					}
				}
			}
		}
	}

	@Override
	public String getName() {
		return "Clear";
	}
	
	@Override
	public String getArgs() {
		return "[amount]";
	}

	@Override
	public String getDescription() {
		return "Deletes previous messages";
	}
	
	@Override
	public String getExample() {
		return "Argument " + getArgs() + ": The amount of messages to be deleted. Must be between 1-100 inclusive."
				+ "\nThis command deletes a specified amount of previous messages, but cannot delete ones older than 2 weeks"
				+ "\n\nExample:\n" + Config.prefix + "" + getName() + " 5 will delete 5 messages";
	}

}
