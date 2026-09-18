package com.tuoling.tuolingbloom.command;

import com.tuoling.tuolingbloom.command.maincommand.BloomCommand;
import com.tuoling.tuolingbloom.command.maincommand.HelpCommand;
import com.tuoling.tuolingbloom.command.maincommand.RemoveCommand;
import com.tuoling.tuolingbloom.command.maincommand.ReloadCommand;
import com.tuoling.tuolingcore.framework.command.CommandHandler;
import com.tuoling.tuolingcore.framework.manager.ValueManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandManager extends ValueManager<CommandHandler> {

    public CommandManager() {
        register(new BloomCommand(), new RemoveCommand(), new ReloadCommand(),new HelpCommand());
    }

    public void runCommand(Command command, CommandSender sender, String[] args) {
        for (CommandHandler commandHandler : delegate()) {
            if (commandHandler.isMatch(command, sender, args)) {
                commandHandler.execute(command, sender, args);
                break;
            }
        }
    }
}
