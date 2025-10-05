package com.falafel77.XPVault;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GiveXPTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            // اقتراح أسماء اللاعبين
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
                    
        } else if (args.length == 2) {
            List<String> amounts = new ArrayList<>();
            
            // مقادير XP
            amounts.add("10");
            amounts.add("100");
            amounts.add("500");
            amounts.add("1000");
            amounts.add("5000");
            amounts.add("10000");
            
            // مقادير المستويات
            amounts.add("1l");
            amounts.add("5l");
            amounts.add("10l");
            amounts.add("20l");
            amounts.add("50l");
            amounts.add("100l");
            
            // اقتراح ديناميكي
            String input = args[1];
            List<String> dynamicAmounts = new ArrayList<>(amounts);
            try {
                int num = Integer.parseInt(input);
                dynamicAmounts.clear();
                dynamicAmounts.add(num + "l");
            } catch (NumberFormatException ignored) {}
            
            return dynamicAmounts.stream()
                    .filter(amount -> amount.toLowerCase().startsWith(input.toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
