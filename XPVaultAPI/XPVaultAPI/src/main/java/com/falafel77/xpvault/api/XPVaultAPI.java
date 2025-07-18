package com.falafel77.xpvault.api;

import com.falafel77.xpvault.api.event.EventManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * XPVault API - الواجهة الرئيسية للتفاعل مع plugin XPVault
 * 
 * يوفر هذا الـ API طرق آمنة للتفاعل مع نظام XP Vault
 * بما في ذلك حفظ واسترجاع وإدارة XP للاعبين
 */
public interface XPVaultAPI {

    /**
     * الحصول على instance من XPVault API
     * 
     * @return XPVaultAPI instance أو null إذا لم يكن الـ plugin مُفعل
     */
    @Nullable
    static XPVaultAPI getInstance() {
        return XPVaultAPIProvider.getAPI();
    }

    /**
     * الحصول على Event Manager للتعامل مع الأحداث
     * 
     * @return EventManager instance
     */
    @NotNull
    EventManager getEventManager();

    /**
     * الحصول على كمية XP المحفوظة للاعب
     * 
     * @param player اللاعب
     * @return كمية XP المحفوظة
     */
    long getSavedXP(@NotNull Player player);

    /**
     * الحصول على كمية XP المحفوظة للاعب باستخدام UUID
     * 
     * @param playerUUID UUID الخاص باللاعب
     * @return كمية XP المحفوظة
     */
    long getSavedXP(@NotNull UUID playerUUID);

    /**
     * حفظ XP للاعب
     * 
     * @param player اللاعب
     * @param amount كمية XP المراد حفظها
     * @return true إذا تم الحفظ بنجاح، false إذا فشل
     */
    boolean saveXP(@NotNull Player player, long amount);

    /**
     * استرجاع XP للاعب
     * 
     * @param player اللاعب
     * @param amount كمية XP المراد استرجاعها
     * @return true إذا تم الاسترجاع بنجاح، false إذا فشل
     */
    boolean retrieveXP(@NotNull Player player, long amount);

    /**
     * تعيين كمية XP محددة للاعب (إدارية)
     * 
     * @param player اللاعب
     * @param amount كمية XP الجديدة
     * @return true إذا تم التعيين بنجاح، false إذا فشل
     */
    boolean setXP(@NotNull Player player, long amount);

    /**
     * إضافة XP للاعب (إدارية)
     * 
     * @param player اللاعب
     * @param amount كمية XP المراد إضافتها
     * @return true إذا تمت الإضافة بنجاح، false إذا فشلت
     */
    boolean addXP(@NotNull Player player, long amount);

    /**
     * إزالة XP من اللاعب (إدارية)
     * 
     * @param player اللاعب
     * @param amount كمية XP المراد إزالتها
     * @return true إذا تمت الإزالة بنجاح، false إذا فشلت
     */
    boolean removeXP(@NotNull Player player, long amount);

    /**
     * إعادة تعيين XP للاعب إلى الصفر (إدارية)
     * 
     * @param player اللاعب
     * @return true إذا تم إعادة التعيين بنجاح، false إذا فشل
     */
    boolean resetXP(@NotNull Player player);

    /**
     * نقل XP من لاعب إلى آخر
     * 
     * @param sender اللاعب المرسل
     * @param receiver اللاعب المستقبل
     * @param amount كمية XP المراد نقلها
     * @return true إذا تم النقل بنجاح، false إذا فشل
     */
    boolean transferXP(@NotNull Player sender, @NotNull Player receiver, long amount);

    /**
     * التحقق من صحة الـ API
     * 
     * @return true إذا كان الـ API يعمل بشكل صحيح
     */
    boolean isValid();

    /**
     * الحصول على إصدار الـ API
     * 
     * @return إصدار الـ API
     */
    @NotNull
    String getVersion();
}

