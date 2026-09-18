package com.tuoling.tuolingbloom.command.maincommand;

import com.tuoling.tuolingbloom.actor.bloomer.Bloomer;
import com.tuoling.tuolingbloom.utils.Message;
import com.tuoling.tuolingbloom.utils.PresetUtils;
import com.tuoling.tuolingcore.framework.command.CommandHandler;
import com.tuoling.tuolingcore.utils.CommandUtil;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;



public class BloomCommand implements CommandHandler {

    @Override
    public void execute(Command command, CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            Message.sendSender(sender, "player-only");
            return;
        }
            Player player = (Player) sender;
            String presetName = args[1];

            if (!new PresetUtils(presetName).exists()) {
                Message.sendPlayer(player, "preset-not-exist", "%preset%", presetName);
                return;
            }
            Bloomer bloomer = new Bloomer(player);
            bloomer.bloom(presetName);
    }

    @Override
    public boolean isMatch(Command command, CommandSender sender, String[] args) {
        return new CommandUtil(command,sender,args).defaultIsMatch("run","bloom","tuolingbloom")
                && args.length == 2;

    }

}
