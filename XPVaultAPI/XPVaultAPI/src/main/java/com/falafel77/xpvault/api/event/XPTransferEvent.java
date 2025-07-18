package com.falafel77.xpvault.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * حدث يتم إطلاقه عند نقل XP بين اللاعبين
 */
public class XPTransferEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player sender;
    private final Player receiver;
    private final long amount;

    public XPTransferEvent(@NotNull Player sender, @NotNull Player receiver, long amount) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
    }

    /**
     * الحصول على اللاعب المرسل
     * 
     * @return اللاعب المرسل
     */
    @NotNull
    public Player getSender() {
        return sender;
    }

    /**
     * الحصول على اللاعب المستقبل
     * 
     * @return اللاعب المستقبل
     */
    @NotNull
    public Player getReceiver() {
        return receiver;
    }

    /**
     * الحصول على كمية XP المنقولة
     * 
     * @return كمية XP المنقولة
     */
    public long getAmount() {
        return amount;
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

