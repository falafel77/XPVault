package com.falafel77.XPVault;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RetrieveXPCommand implements CommandExecutor {

    private final XPVault plugin;

    public RetrieveXPCommand(XPVault plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("only_players_can_use_command"));
            return true;
        }

        Player player = (Player) sender;
        long savedXP = this.plugin.getXpManager().getPlayerSavedXP(player);

        if (args.length == 0) {
            // استعادة كل XP المحفوظ
            if (savedXP <= 0) {
                player.sendMessage(plugin.getMessage("no_saved_xp"));
                return true;
            }

            // حفظ XP الحالي قبل العملية
            long currentXPBeforeAll = ExperienceUtil.getTotalXP(player);
            
            // إضافة XP للاعب باستخدام ExperienceUtil
            ExperienceUtil.changePlayerXP(player, savedXP);
            
            // التحقق من المقدار الفعلي المضاف
            long actualXPAfterAll = ExperienceUtil.getTotalXP(player);
            long actualXPRetrievedAll = actualXPAfterAll - currentXPBeforeAll;
            
            // تصفير البنك بناء على المقدار الفعلي المستعاد
            this.plugin.getXpManager().getCustomConfig().set(player.getUniqueId().toString() + ".xp", savedXP - actualXPRetrievedAll);
            this.plugin.getXpManager().saveCustomConfig();
            
            player.sendMessage(plugin.getMessage("all_xp_retrieved").replace("%amount%", String.valueOf(actualXPRetrievedAll)));
            return true;

        } else if (args.length == 1) {
            String arg = args[0].toLowerCase();
            long amountToRetrieve = 0;
            boolean isLevel = false;

            if (arg.endsWith("l")) {
                isLevel = true;
                try {
                    int levelsToRetrieve = Integer.parseInt(arg.substring(0, arg.length() - 1));
                    if (levelsToRetrieve <= 0) {
                        sender.sendMessage(plugin.getMessage("amount_must_be_positive"));
                        return true;
                    }
                    
                    // الحساب الصحيح: تحويل المستويات إلى XP للإعطاء
                    // للاستعادة نحسب XP المطلوب لرفع المستويات من المستوى الحالي
                    amountToRetrieve = ExperienceUtil.getXPForLevelAdvancement(player, levelsToRetrieve);

                } catch (NumberFormatException e) {
                    sender.sendMessage(plugin.getMessage("amount_must_be_number") + ". " + plugin.getMessage("retrievexp_usage"));
                    return true;
                }
            } else {
                try {
                    amountToRetrieve = Long.parseLong(arg);
                } catch (NumberFormatException e) {
                    sender.sendMessage(plugin.getMessage("amount_must_be_number") + ". " + plugin.getMessage("retrievexp_usage"));
                    return true;
                }
            }

            if (amountToRetrieve <= 0) {
                sender.sendMessage(plugin.getMessage("amount_must_be_positive"));
                return true;
            }

            // التحقق من وجود XP محفوظ كافي
            if (savedXP < amountToRetrieve) {
                player.sendMessage(plugin.getMessage("not_enough_saved_xp")
                    .replace("%saved_xp%", String.valueOf(savedXP))
                    .replace("%amount%", String.valueOf(amountToRetrieve)));
                return true;
            }

            // حفظ XP الحالي قبل العملية
            long currentXPBeforePartial = ExperienceUtil.getTotalXP(player);
            
            // إضافة XP للاعب باستخدام ExperienceUtil
            ExperienceUtil.changePlayerXP(player, amountToRetrieve);
            
            // التحقق من المقدار الفعلي المضاف
            long actualXPAfterPartial = ExperienceUtil.getTotalXP(player);
            long actualXPRetrievedPartial = actualXPAfterPartial - currentXPBeforePartial;
            
            // خصم المقدار الفعلي من البنك
            long newSavedXPPartial = savedXP - actualXPRetrievedPartial;
            this.plugin.getXpManager().getCustomConfig().set(player.getUniqueId().toString() + ".xp", newSavedXPPartial);
            this.plugin.getXpManager().saveCustomConfig();

            if (isLevel) {
                int levelsEquivalent = Experience.getIntLevelFromExp(actualXPRetrievedPartial);
                player.sendMessage(plugin.getMessage("partial_xp_retrieved_levels")
                    .replace("%amount%", String.valueOf(actualXPRetrievedPartial))
                    .replace("%levels%", String.valueOf(levelsEquivalent))
                    .replace("%remaining_xp%", String.valueOf(newSavedXPPartial)));
            } else {
                player.sendMessage(plugin.getMessage("partial_xp_retrieved")
                    .replace("%amount%", String.valueOf(actualXPRetrievedPartial))
                    .replace("%remaining_xp%", String.valueOf(newSavedXPPartial)));
            }
            return true;
        } else {
            sender.sendMessage(plugin.getMessage("retrievexp_usage"));
            return true;
        }
    }
}