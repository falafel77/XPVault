package com.falafel77.xpvault.api.event;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * حدث يتم إطلاقه عند تعيين XP (إدارياً)
 */
public class XPSetEvent extends XPVaultEvent {

    private final long oldAmount;
    private final long newAmount;

    public XPSetEvent(@NotNull Player player, long oldAmount, long newAmount) {
        super(player);
        this.oldAmount = oldAmount;
        this.newAmount = newAmount;
    }

    /**
     * الحصول على كمية XP القديمة
     * 
     * @return كمية XP القديمة
     */
    public long getOldAmount() {
        return oldAmount;
    }

    /**
     * الحصول على كمية XP الجديدة
     * 
     * @return كمية XP الجديدة
     */
    public long getNewAmount() {
        return newAmount;
    }

    /**
     * الحصول على الفرق في كمية XP
     * 
     * @return الفرق (موجب إذا زادت، سالب إذا نقصت)
     */
    public long getDifference() {
        return newAmount - oldAmount;
    }
}

