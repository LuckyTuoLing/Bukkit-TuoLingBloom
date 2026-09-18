package com.tuoling.tuolingbloom.command;

import com.tuoling.tuolingbloom.TuoLingBloom;
import com.tuoling.tuolingbloom.cache.BloomerCache;
import com.tuoling.tuolingbloom.utils.PresetUtils;
import com.tuoling.tuolingcore.utils.CommandUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

//命令触发器
public final class CommandTrigger implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        TuoLingBloom main = TuoLingBloom.inst();
        main.getCommandManager().runCommand(command, commandSender, args);
        return true;
    }
    // run <preset>
    // remove <preset>
    // reload (OP only)
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (!new CommandUtil(command,sender,args).isPluginCommand("bloom","tuolingbloom")) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            List<String> subs = new ArrayList<>(Arrays.asList("run", "remove"));
            if (sender.isOp()) {
                subs.add("reload");
            }
            return filter(subs, args[0]);
        }

        if (args.length == 2 && sender instanceof Player) {
            TuoLingBloom plugin = TuoLingBloom.inst();
            if (args[0].equalsIgnoreCase("run")) {
                return filter(new ArrayList<>(PresetUtils.getAllPresetNames()), args[1]);
            }
            if (args[0].equalsIgnoreCase("remove")) {
                BloomerCache cache = plugin.getCacheManager().getBloomerCache();
                UUID uuid = ((Player) sender).getUniqueId();
                if (cache.containsRow(uuid)) {
                    return filter(new ArrayList<>(cache.row(uuid).keySet()), args[1]);
                }
            }
        }

        return Collections.emptyList();
    }

    private List<String> filter(List<String> list, String prefix) {
        if (prefix == null || prefix.isEmpty()) return list;
        List<String> result = new ArrayList<>();
        for (String s : list) {
            if (s.toLowerCase().startsWith(prefix.toLowerCase())) {
                result.add(s);
            }
        }
        return result;
    }

}
