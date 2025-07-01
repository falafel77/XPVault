# XPVault Plugin

This plugin allows players to save and retrieve their experience points (XP), in addition to administrative commands for managing XP.

## Commands:

### `/savexp <amount>`
- **Description:** Saves a specified amount of a player's XP.
- **Usage:** `/savexp <amount>`
- **Aliases:** `sxp`

### `/givexp <player> <amount>`
- **Description:** Gives XP to a player.
- **Usage:** `/givexp <player> <amount>`
- **Aliases:** `gxp`

### `/retrievexp [amount]`
- **Description:** Retrieves saved XP for a player. Optionally specify an amount.
- **Usage:** `/retrievexp [amount]`
- **Aliases:** `rxp`

### `/adminxp <resetall|set|add|remove> [player] [amount]`
- **Description:** Administrative commands for managing saved XP.
- **Usage:** `/adminxp <resetall|set|add|remove> [player] [amount]`
- **Required Permission:** `xpvault.admin`

### `/checkxp`
- **Description:** Checks your current XP and stored XP.
- **Usage:** `/checkxp`
- **Aliases:** `cxp`

## PlaceholderAPI Support

XPVault currently supports PlaceholderAPI for displaying saved XP. You can use the following variables:

-   `%xpvault_saved_xp%`: To display the player's saved XP.

## Installation

1.  Place the `XPVault-1.0-SNAPSHOT.jar` file in your Minecraft server's `plugins` folder.
2.  Restart or reload the server.
3.  (Optional) If you wish to use PlaceholderAPI variables, ensure PlaceholderAPI is installed on your server.

## License

This plugin is protected by a special license that prohibits decompilation, sale, or redistribution without explicit permission from the developer. All rights reserved.

---

# XPVault Plugin (باللغة العربية)

هذا الـ plugin يسمح للاعبين بحفظ واستعادة نقاط الخبرة (XP) الخاصة بهم، بالإضافة إلى أوامر إدارية للتحكم في الخبرة.

## الأوامر:

### `/savexp <amount>`
- **الوصف:** يحفظ كمية محددة من نقاط الخبرة للاعب.
- **الاستخدام:** `/savexp <amount>`
- **الأسماء المستعارة:** `sxp`

### `/givexp <player> <amount>`
- **الوصف:** يعطي نقاط خبرة للاعب.
- **الاستخدام:** `/givexp <player> <amount>`
- **الأسماء المستعارة:** `gxp`

### `/retrievexp [amount]`
- **الوصف:** يستعيد نقاط الخبرة المحفوظة للاعب. يمكن تحديد كمية اختيارية.
- **الاستخدام:** `/retrievexp [amount]`
- **الأسماء المستعارة:** `rxp`

### `/adminxp <resetall|set|add|remove> [player] [amount]`
- **الوصف:** أوامر إدارية لإدارة نقاط الخبرة المحفوظة.
- **الاستخدام:** `/adminxp <resetall|set|add|remove> [player] [amount]`
- **الصلاحية المطلوبة:** `xpvault.admin`

### `/checkxp`
- **الوصف:** يتحقق من نقاط الخبرة الحالية ونقاط الخبرة المخزنة لديك.
- **الاستخدام:** `/checkxp`
- **الأسماء المستعارة:** `cxp`

## دعم PlaceholderAPI

يدعم XPVault حاليًا PlaceholderAPI لعرض نقاط الخبرة المحفوظة. يمكنك استخدام المتغيرات التالية:

-   `%xpvault_saved_xp%`: لعرض نقاط الخبرة المحفوظة للاعب.

## التثبيت

1.  ضع ملف `XPVault-1.0-SNAPSHOT.jar` في مجلد `plugins` الخاص بخادم Minecraft الخاص بك.
2.  أعد تشغيل أو أعد تحميل الخادم.
3.  (اختياري) إذا كنت ترغب في استخدام متغيرات PlaceholderAPI، تأكد من تثبيت PlaceholderAPI على الخادم الخاص بك.

## الترخيص

هذا الـ plugin محمي بموجب ترخيص خاص يمنع تفكيكه، بيعه، أو إعادة توزيعه دون إذن صريح من المطور. جميع الحقوق محفوظة.


