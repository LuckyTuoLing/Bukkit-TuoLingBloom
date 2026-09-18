package com.tuoling.tuolingbloom.command.maincommand;

import com.tuoling.tuolingbloom.actor.bloomer.Bloomer;
import com.tuoling.tuolingbloom.utils.Message;
import com.tuoling.tuolingcore.framework.command.CommandHandler;
import com.tuoling.tuolingcore.utils.CommandUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemoveCommand implements CommandHandler {

    @Override
    public void execute(Command command, CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            Message.sendSender(sender, "player-only");
            return;
        }

        if (args.length == 2) {
            Player player = (Player) sender;
            Bloomer bloomer = new Bloomer(player);
            if (bloomer.isBloomPreset(args[1])) {
                bloomer.cancelPreset(args[1]);
                Message.sendPlayer(player, "bloom-cancel", "%preset%", args[1]);
            } else {
                Message.sendPlayer(player, "not-bloomed", "%preset%", args[1]);
            }
        }
    }

    @Override
    public boolean isMatch(Command command, CommandSender sender, String[] args) {
        return new CommandUtil(command, sender, args).defaultIsMatch("remove", "bloom", "tuolingbloom")
                && args.length == 2;
    }
}
