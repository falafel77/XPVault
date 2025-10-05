package com.falafel77.XPVault;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import java.util.Collections;
import java.util.List;

public class ReloadTabCompleter implements TabCompleter {
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // لا توجد اقتراحات لأمر reload
        return Collections.emptyList();
    }
}
