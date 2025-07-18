package com.falafel77.xpvault.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * الكلاس الأساسي لجميع أحداث XPVault
 */
public abstract class XPVaultEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    protected final Player player;

    public XPVaultEvent(@NotNull Player player) {
        this.player = player;
    }

    /**
     * الحصول على اللاعب المرتبط بالحدث
     * 
     * @return اللاعب
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}

