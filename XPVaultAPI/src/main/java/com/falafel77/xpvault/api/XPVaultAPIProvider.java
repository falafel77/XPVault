package com.falafel77.xpvault.api;

import org.jetbrains.annotations.Nullable;

/**
 * Provider للحصول على XPVault API instance
 */
public final class XPVaultAPIProvider {

    private static XPVaultAPI api;

    private XPVaultAPIProvider() {
        // منع إنشاء instances
    }

    /**
     * تعيين API instance (يتم استدعاؤها من الـ plugin الرئيسي)
     * 
     * @param apiInstance API instance
     */
    public static void setAPI(@Nullable XPVaultAPI apiInstance) {
        api = apiInstance;
    }

    /**
     * الحصول على API instance
     * 
     * @return API instance أو null إذا لم يكن متوفراً
     */
    @Nullable
    public static XPVaultAPI getAPI() {
        return api;
    }
}

