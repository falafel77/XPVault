package com.falafel77.xpvault.api.event;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * حدث يتم إطلاقه عند حفظ XP
 */
public class XPSavedEvent extends XPVaultEvent {

    private final long amount;
    private final long newTotal;

    public XPSavedEvent(@NotNull Player player, long amount, long newTotal) {
        super(player);
        this.amount = amount;
        this.newTotal = newTotal;
    }

    /**
     * الحصول على كمية XP المحفوظة
     * 
     * @return كمية XP المحفوظة
     */
    public long getAmount() {
        return amount;
    }

    /**
     * الحصول على إجمالي XP الجديد بعد الحفظ
     * 
     * @return إجمالي XP الجديد
     */
    public long getNewTotal() {
        return newTotal;
    }

    /**
     * الحصول على إجمالي XP القديم قبل الحفظ
     * 
     * @return إجمالي XP القديم
     */
    public long getOldTotal() {
        return newTotal - amount;
    }
}

