package com.tuoling.tuolingbloom.command.maincommand;

import com.tuoling.tuolingbloom.utils.Message;
import com.tuoling.tuolingcore.framework.command.CommandHandler;
import com.tuoling.tuolingcore.utils.CommandUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class HelpCommand implements CommandHandler {

    @Override
    public boolean isMatch(Command command, CommandSender sender, String[] args) {
        if (!new CommandUtil(command, sender, args).isPluginCommand("bloom","tuolingbloom")){
            return false;
        }
        if (args.length == 0) {
            return true;
        }
        // 单参数且无后续参数时显示帮助(如 /bloom run, /bloom remove 缺少预设名)
        if (args.length == 1) {
            return args[0].equalsIgnoreCase("run")
                    || args[0].equalsIgnoreCase("remove")
                    || (!args[0].equalsIgnoreCase("reload"));
        }
        return false;
    }

    @Override
    public void execute(Command command, CommandSender sender, String[] args) {
        Message.sendHelp(sender);
    }

}
