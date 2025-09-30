package com.falafel77.XPVault;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SaveXPCommand implements CommandExecutor {

    private final XPVault plugin;

    public SaveXPCommand(XPVault plugin) {
        this.plugin = plugin;
    }

    // Cooldown map for players
    private static final java.util.Map<java.util.UUID, Long> cooldowns = new java.util.HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage("only_players_can_use_command"));
            return true;
        }
        Player player = (Player) sender;
        // Check allowed worlds
        if (plugin.getAllowedWorlds() != null && !plugin.getAllowedWorlds().isEmpty() && !plugin.getAllowedWorlds().contains(player.getWorld().getName())) {
            player.sendMessage("&cThis command is not allowed in this world.");
            return true;
        }
        // Check cooldown
        if (plugin.isCooldownEnabled() && plugin.isCommandCooldownEnabled("SaveXPCommand")) {
            long now = System.currentTimeMillis();
            long last = cooldowns.getOrDefault(player.getUniqueId(), 0L);
            if (now - last < plugin.getCooldownSeconds() * 1000) {
                long wait = (plugin.getCooldownSeconds() * 1000 - (now - last)) / 1000;
                player.sendMessage("&cPlease wait " + wait + " seconds before using this command again.");
                return true;
            }
            cooldowns.put(player.getUniqueId(), now);
        }
        if (args.length < 1) {
            sender.sendMessage(plugin.getMessage("savexp_usage"));
            return true;
        }
        String arg = args[0].toLowerCase();
        long amountToSave = 0;
        boolean isLevel = false;
        long currentXP = Experience.getExp(player);
        if (arg.endsWith("l")) {
            isLevel = true;
            try {
                int levelsToSave = Integer.parseInt(arg.substring(0, arg.length() - 1));
                if (levelsToSave <= 0) {
                    sender.sendMessage(plugin.getMessage("amount_must_be_positive"));
                    return true;
                }

                // التحقق من أن اللاعب لديه مستويات كافية
                if (!ExperienceUtil.hasEnoughLevels(player, levelsToSave)) {
                    player.sendMessage(plugin.getMessage("not_enough_xp_saving_what_you_have").replace("%current_xp%", String.valueOf(currentXP)));
                    amountToSave = currentXP; // حفظ كل XP المتاح
                } else {
                    // الحساب الصحيح: حساب XP المطلوب لخصم المستويات المحددة
                    amountToSave = ExperienceUtil.getXPForLevelDeduction(player, levelsToSave);
                }

            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.getMessage("amount_must_be_number") + ". " + plugin.getMessage("savexp_usage"));
                return true;
            }
        } else {
            try {
                amountToSave = Long.parseLong(arg);
            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.getMessage("amount_must_be_number") + ". " + plugin.getMessage("savexp_usage"));
                return true;
            }
        }

        if (amountToSave <= 0) {
            sender.sendMessage(plugin.getMessage("amount_must_be_positive"));
            return true;
        }

        // التحقق من وجود XP كافي
        if (!isLevel && currentXP < amountToSave) {
            player.sendMessage(plugin.getMessage("not_enough_xp_saving_what_you_have").replace("%current_xp%", String.valueOf(currentXP)));
            amountToSave = currentXP; // حفظ كل XP المتاح
        }

        if (amountToSave <= 0) {
            sender.sendMessage("ليس لديك XP للحفظ!");
            return true;
        }

        // حفظ XP الحالي قبل العملية
        long xpBeforeSaving = currentXP;
        
        // خصم XP من اللاعب باستخدام ExperienceUtil
        ExperienceUtil.changePlayerXP(player, -amountToSave);
        
        // التحقق من المقدار الفعلي المخصوم
        long actualXPAfter = ExperienceUtil.getTotalXP(player);
        long actualXPSaved = xpBeforeSaving - actualXPAfter;

        // حفظ المقدار الفعلي في البنك
        long savedXP = this.plugin.getXpManager().getPlayerSavedXP(player);
        this.plugin.getXpManager().setPlayerSavedXP(player, savedXP + actualXPSaved);

        if (isLevel) {
            int levelsEquivalent = Experience.getIntLevelFromExp(actualXPSaved);
            player.sendMessage(plugin.getMessage("xp_levels_saved")
                .replace("%levels%", String.valueOf(levelsEquivalent))
                .replace("%amount%", String.valueOf(actualXPSaved))
                .replace("%current_xp%", String.valueOf(actualXPAfter)));
        } else {
            player.sendMessage(plugin.getMessage("xp_saved")
                .replace("%amount%", String.valueOf(actualXPSaved))
                .replace("%current_xp%", String.valueOf(actualXPAfter)));
        }
        return true;
    }
}