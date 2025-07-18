package com.falafel77.xpvault.api.event;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * حدث يتم إطلاقه عند استرجاع XP
 */
public class XPRetrievedEvent extends XPVaultEvent {

    private final long amount;
    private final long newTotal;

    public XPRetrievedEvent(@NotNull Player player, long amount, long newTotal) {
        super(player);
        this.amount = amount;
        this.newTotal = newTotal;
    }

    /**
     * الحصول على كمية XP المسترجعة
     * 
     * @return كمية XP المسترجعة
     */
    public long getAmount() {
        return amount;
    }

    /**
     * الحصول على إجمالي XP الجديد بعد الاسترجاع
     * 
     * @return إجمالي XP الجديد
     */
    public long getNewTotal() {
        return newTotal;
    }

    /**
     * الحصول على إجمالي XP القديم قبل الاسترجاع
     * 
     * @return إجمالي XP القديم
     */
    public long getOldTotal() {
        return newTotal + amount;
    }
}

