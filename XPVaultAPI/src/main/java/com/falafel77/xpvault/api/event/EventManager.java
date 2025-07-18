package com.falafel77.xpvault.api.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Event Manager لإدارة أحداث XPVault API
 */
public class EventManager {

    /**
     * إطلاق حدث حفظ XP
     * 
     * @param player اللاعب الذي حفظ XP
     * @param amount كمية XP المحفوظة
     * @param newTotal إجمالي XP الجديد
     */
    public void callXPSavedEvent(@NotNull Player player, long amount, long newTotal) {
        XPSavedEvent event = new XPSavedEvent(player, amount, newTotal);
        Bukkit.getPluginManager().callEvent(event);
    }

    /**
     * إطلاق حدث استرجاع XP
     * 
     * @param player اللاعب الذي استرجع XP
     * @param amount كمية XP المسترجعة
     * @param newTotal إجمالي XP الجديد
     */
    public void callXPRetrievedEvent(@NotNull Player player, long amount, long newTotal) {
        XPRetrievedEvent event = new XPRetrievedEvent(player, amount, newTotal);
        Bukkit.getPluginManager().callEvent(event);
    }

    /**
     * إطلاق حدث تعيين XP
     * 
     * @param player اللاعب
     * @param oldAmount كمية XP القديمة
     * @param newAmount كمية XP الجديدة
     */
    public void callXPSetEvent(@NotNull Player player, long oldAmount, long newAmount) {
        XPSetEvent event = new XPSetEvent(player, oldAmount, newAmount);
        Bukkit.getPluginManager().callEvent(event);
    }

    /**
     * إطلاق حدث نقل XP
     * 
     * @param sender اللاعب المرسل
     * @param receiver اللاعب المستقبل
     * @param amount كمية XP المنقولة
     */
    public void callXPTransferEvent(@NotNull Player sender, @NotNull Player receiver, long amount) {
        XPTransferEvent event = new XPTransferEvent(sender, receiver, amount);
        Bukkit.getPluginManager().callEvent(event);
    }

    /**
     * إيقاف Event Manager
     */
    public void shutdown() {
        // يمكن إضافة منطق تنظيف هنا إذا لزم الأمر
    }
}

