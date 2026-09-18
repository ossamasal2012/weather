package com.osama.weather.ui.screens.privacy

/**
 * The exact bilingual privacy-policy text supplied by the app's developer.
 * Wording is untouched; only lightweight structure markers survive (## / ###
 * headers, * bullets, --- dividers, **bold** spans) so [parsePolicyMarkdown]
 * can lay each language out as properly styled sections instead of a wall of
 * raw markdown characters. Each language is split across several literals
 * and joined at runtime purely to stay well clear of the JVM class-file
 * limit on any single constant string's size — the concatenation itself
 * changes nothing about the content.
 */
object PrivacyPolicyContent {
    val ARABIC: String = listOf(ARABIC_PART_1, ARABIC_PART_2, ARABIC_PART_3).joinToString("")
    val ENGLISH: String = listOf(ENGLISH_PART_1, ENGLISH_PART_2, ENGLISH_PART_3).joinToString("")
}

private const val ARABIC_PART_1 = """
**آخر تحديث:** 16 سبتمبر 2026

**تاريخ السريان:** 16 سبتمبر 2026

---

## 1. مقدمة

مرحباً بك في **Weather App**.

نحن نحترم خصوصيتك ونلتزم بالتعامل مع معلوماتك بطريقة واضحة وشفافة. تم إعداد سياسة الخصوصية هذه لتوضيح كيفية تعامل تطبيق **Weather App** مع المعلومات عند استخدامك للتطبيق، بما في ذلك معلومات الموقع، والمعلومات التقنية اللازمة لتوفير خدمات الطقس وتحسين موثوقية التطبيق.

باستخدامك للتطبيق، فإنك تقر بأنك قرأت وفهمت سياسة الخصوصية هذه.

نحن لا نبيع معلوماتك الشخصية، ولا نستخدم بيانات الموقع أو المعلومات الشخصية لإنشاء ملفات تعريف إعلانية عن المستخدمين.

---

## 2. نطاق هذه السياسة

تنطبق سياسة الخصوصية هذه على تطبيق **Weather App** وخدماته المرتبطة مباشرة به.

توضح هذه السياسة:

* المعلومات التي قد يصل إليها التطبيق.

* كيفية استخدام المعلومات.

* كيفية التعامل مع بيانات الموقع.

* خدمات الطرف الثالث المستخدمة لتوفير بيانات الطقس.

* كيفية حماية المعلومات.

* مدة الاحتفاظ بالمعلومات.

* حقوق المستخدم.

* كيفية التواصل معنا بشأن الخصوصية.

* كيفية التعامل مع التحديثات المستقبلية للسياسة.

---

## 3. المعلومات التي قد يصل إليها التطبيق

### 3.1 بيانات الموقع

قد يطلب التطبيق إذن الوصول إلى **موقع الجهاز** حتى يتمكن من تحديد حالة الطقس والتوقعات الجوية للموقع الذي تختاره.

قد يستخدم التطبيق الموقع:

* لتحديد موقعك الحالي.

* لعرض حالة الطقس الحالية.

* لعرض توقعات الطقس بالساعة.

* لعرض التوقعات اليومية.

* لتوفير معلومات الطقس المرتبطة بموقعك.

* لتحسين تجربة اختيار الموقع داخل التطبيق.

### مهم

الوصول إلى الموقع يتم فقط عند منحك الإذن المطلوب من نظام Android.

يمكنك رفض إذن الموقع أو سحبه لاحقاً من إعدادات جهازك. وفي هذه الحالة قد تبقى بعض وظائف التطبيق متاحة، لكن قد تحتاج إلى اختيار موقع يدوياً حتى تحصل على بيانات الطقس.

لا يحتاج التطبيق إلى معرفة هويتك الحقيقية حتى يوفر لك بيانات الطقس.

### 3.2 المواقع التي تختارها يدوياً

إذا قمت بالبحث عن مدينة أو موقع داخل التطبيق، فقد يستخدم التطبيق اسم الموقع أو إحداثياته للحصول على معلومات الطقس الخاصة بذلك الموقع.

قد يتم حفظ بعض إعدادات المواقع محلياً على جهازك بهدف تسهيل استخدام التطبيق، وفقاً للوظائف المتوفرة في الإصدار المستخدم.

### 3.3 المعلومات التقنية

قد تتم معالجة بعض المعلومات التقنية الضرورية لتشغيل التطبيق والخدمات المرتبطة به، مثل:

* عنوان IP عند الاتصال بخدمات الإنترنت.

* نوع الجهاز.

* إصدار نظام التشغيل.

* معلومات الاتصال بالشبكة.

* معلومات تقنية عن الطلبات إلى خدمات الطقس.

* سجلات أخطاء أو معلومات تقنية ضرورية لتشخيص المشاكل، إذا كانت هذه الوظائف مستخدمة في التطبيق.

تُستخدم هذه المعلومات لأغراض تشغيلية وتقنية، مثل:

* توفير الخدمة.

* معالجة طلبات الطقس.

* اكتشاف الأخطاء.

* حماية الخدمات من إساءة الاستخدام.

* تحسين استقرار وموثوقية الخدمة.

لا نستخدم هذه المعلومات لإنشاء ملف شخصي إعلاني عنك.

---
"""

private const val ARABIC_PART_2 = """
## 4. بيانات الطقس ومعلومات الموقع

تطبيق Weather App يحتاج إلى إرسال معلومات الموقع أو الموقع الذي اخترته إلى خدمة بيانات الطقس حتى يستطيع الحصول على توقعات الطقس.

يستخدم التطبيق **Open-Meteo** كمصدر لبيانات الطقس.

بحسب سياسة Open-Meteo الحالية، قد تتم معالجة معلومات تقنية مثل عناوين IP لأغراض تشغيل الخدمة ومنع إساءة الاستخدام، وقد تتضمن سجلات الخادم معلومات جغرافية مرتبطة بطلبات API لأغراض استكشاف الأخطاء وإصلاحها. وتذكر Open-Meteo أن سجلاتها الفردية تُحذف بعد 90 يوماً وفقاً لسياستها الحالية.

يمكنك الاطلاع على سياسة Open-Meteo الخاصة بشكل مستقل من خلال موقعها الرسمي.

---

## 5. جودة الهواء

قد يوفر التطبيق معلومات مرتبطة بجودة الهواء أو مؤشرات جودة الهواء عندما تكون هذه الميزة متاحة في الإصدار المستخدم.

عند استخدام هذه الميزة، قد يتم إرسال الموقع الذي اخترته إلى خدمة البيانات المطلوبة للحصول على معلومات جودة الهواء المرتبطة بذلك الموقع.

لا يستخدم التطبيق معلومات جودة الهواء لإنشاء ملف شخصي صحي عن المستخدم.

ولا ينبغي اعتبار معلومات جودة الهواء أو الطقس المقدمة من التطبيق بديلاً عن المشورة الطبية أو التحذيرات الرسمية للجهات المختصة.

---

## 6. هل نجمع معلومات شخصية؟

**لا يطلب Weather App إنشاء حساب شخصي لاستخدام وظائف الطقس الأساسية.**

لا نطلب منك، لأجل الوظائف الأساسية للتطبيق:

* اسمك الحقيقي.

* رقم هاتفك.

* عنوان منزلك.

* جهات اتصالك.

* كلمات المرور الخاصة بك.

* رسائل SMS.

* صورك الشخصية.

* ملفاتك الشخصية.

* معلومات الدفع.

إذا تمت إضافة أي وظيفة مستقبلية تتطلب معلومات شخصية، فسيتم تحديث سياسة الخصوصية هذه لتوضيح نوع البيانات والغرض من استخدامها قبل أو عند تقديم تلك الوظيفة، حسب ما تقتضيه القوانين والسياسات المعمول بها.

---

## 7. الإعلانات

**إذا كان الإصدار الحالي من Weather App لا يحتوي على إعلانات أو شبكات إعلانية، فلا نستخدم معلوماتك الشخصية لعرض إعلانات مخصصة.**

لا تستخدم هذه السياسة لإخفاء أي SDK إعلاني. إذا تمت إضافة إعلانات إلى التطبيق مستقبلاً، فسيتم تحديث سياسة الخصوصية وبيانات Google Play ذات الصلة لتوضيح كيفية تعامل مزود الإعلانات مع البيانات.

---

## 8. خدمات الطرف الثالث

قد يعتمد التطبيق على خدمات خارجية ضرورية لتوفير بعض الوظائف.

الخدمة الأساسية المستخدمة لتوفير بيانات الطقس هي:

**Open-Meteo**

تخضع الخدمات الخارجية لسياساتها وشروطها الخاصة. نحن ننصح المستخدم بمراجعة سياسات الخصوصية الخاصة بهذه الخدمات لفهم كيفية تعاملها مع البيانات التي قد تتلقاها أثناء تقديم الخدمة.

لا نبيع بيانات المستخدمين إلى هذه الخدمات.

---

## 9. مشاركة المعلومات

نحن لا نبيع أو نؤجر أو نتاجر بالمعلومات الشخصية للمستخدمين.

قد يتم نقل المعلومات الضرورية فقط إلى مزودي الخدمات الذين يحتاج التطبيق إلى التواصل معهم لتوفير الوظائف الأساسية، مثل خدمات بيانات الطقس.

قد يتم أيضاً الكشف عن معلومات إذا كان ذلك:

* مطلوباً بموجب القانون.

* ضرورياً للامتثال لأمر قضائي أو طلب قانوني صحيح.

* ضرورياً لحماية حقوق أو أمن الخدمة أو المستخدمين.

* ضرورياً لمنع الاحتيال أو إساءة الاستخدام أو الهجمات التقنية.

نحن لا نستخدم بيانات الموقع لأغراض تسويقية شخصية.

---

## 10. تخزين البيانات على الجهاز

قد يخزن التطبيق بعض المعلومات محلياً على جهازك لتوفير تجربة أفضل، مثل:

* إعدادات التطبيق.

* الوحدة المستخدمة لدرجة الحرارة.

* المواقع التي اختارها المستخدم، إذا كانت ميزة المواقع المحفوظة متوفرة.

* بعض تفضيلات العرض.

هذه البيانات المحلية تبقى على جهازك ما لم يقم التطبيق أو نظام التشغيل بحذفها، أو تقوم أنت بحذف بيانات التطبيق أو إلغاء تثبيته.

---

## 11. الاحتفاظ بالبيانات وحذفها

نحن نتبع مبدأ **تقليل البيانات**، أي عدم الاحتفاظ بالبيانات لفترة أطول مما هو ضروري للغرض الذي جُمعت من أجله.

إذا كانت بعض المعلومات مخزنة محلياً على جهازك، يمكنك عادةً حذفها من خلال:

* حذف بيانات التطبيق من إعدادات Android.

* إلغاء تثبيت التطبيق.

* حذف المواقع أو الإعدادات المحفوظة من داخل التطبيق، إذا كانت هذه الوظيفة متاحة.

أما البيانات التي تتم معالجتها من خلال خدمات خارجية، فتخضع لفترات الاحتفاظ وسياسات مزودي تلك الخدمات.

وبالنسبة إلى Open-Meteo، تشير سياستها الحالية إلى أن سجلات الخادم الفردية يتم حذفها بعد 90 يوماً، بينما قد يتم الاحتفاظ ببيانات إحصائية مجمعة وفقاً لسياساتها.

---

## 12. أمان المعلومات

نحن نتخذ إجراءات تقنية وتنظيمية معقولة للمساعدة في حماية المعلومات من:

* الوصول غير المصرح به.

* التعديل غير المصرح به.

* الكشف غير المصرح به.

* الاستخدام غير المصرح به.

* الفقدان أو التخريب.

يتم استخدام وسائل اتصال آمنة عند توفرها، مثل **HTTPS/TLS**، لحماية البيانات أثناء انتقالها عبر الشبكة.

ومع ذلك، لا توجد وسيلة لنقل البيانات عبر الإنترنت أو تخزينها يمكن ضمان أنها آمنة بنسبة 100%.

---
"""

private const val ARABIC_PART_3 = """
## 13. صلاحيات التطبيق

قد يطلب التطبيق بعض الصلاحيات وفقاً للميزات التي تستخدمها.

### الموقع

يُستخدم لتحديد موقعك وعرض الطقس المرتبط به.

### الإنترنت

يُستخدم للوصول إلى بيانات الطقس والمعلومات المطلوبة من خدمات الإنترنت.

لا يطلب التطبيق صلاحيات لا يحتاجها لوظائفه الأساسية.

---

## 14. التحكم بالموقع

يمكنك التحكم في صلاحية الموقع من إعدادات Android في أي وقت.

يمكنك:

* السماح بالوصول إلى الموقع.

* رفض الوصول.

* تغيير مستوى صلاحية الموقع وفق الخيارات التي يوفرها إصدار Android لديك.

عند إيقاف صلاحية الموقع، قد لا يتمكن التطبيق من معرفة موقعك تلقائياً، ولكن يمكنك استخدام البحث أو اختيار موقع يدوياً إذا كانت هذه الوظائف متاحة.

---

## 15. خصوصية الأطفال

تم تصميم Weather App كتطبيق معلومات عن الطقس وليس كتطبيق لجمع المعلومات الشخصية من الأطفال.

نحن لا نطلب من المستخدمين تقديم معلومات شخصية حساسة لاستخدام الوظائف الأساسية للتطبيق.

إذا كان هناك أي استخدام للتطبيق من قبل الأطفال، فنحن نشجع أولياء الأمور أو الأوصياء على توعية الأطفال بعدم مشاركة المعلومات الشخصية مع أي خدمة عبر الإنترنت دون إشراف مناسب.

---

## 16. حقوق المستخدم

بحسب القوانين المعمول بها في منطقتك، قد تكون لديك حقوق معينة تتعلق ببياناتك، بما في ذلك، عند انطباقها:

* معرفة البيانات التي تتم معالجتها.

* طلب الوصول إلى البيانات.

* طلب تصحيح البيانات غير الصحيحة.

* طلب حذف البيانات.

* الاعتراض على بعض أنواع المعالجة.

* سحب بعض الأذونات أو الموافقات.

* التحكم في صلاحية الوصول إلى الموقع من إعدادات الجهاز.

يمكنك التواصل معنا إذا كان لديك استفسار يتعلق ببياناتك أو خصوصيتك.

---

## 17. عدم بيع البيانات

**Weather App لا يبيع المعلومات الشخصية للمستخدمين.**

كما لا نستخدم بيانات الموقع التي يتم الحصول عليها لتشغيل وظيفة الطقس بهدف بيعها إلى أطراف أخرى أو إنشاء ملفات تعريف إعلانية شخصية.

---

## 18. دقة معلومات الطقس

بيانات الطقس المقدمة من التطبيق تعتمد على مصادر وخدمات خارجية، وقد تكون هناك حالات تكون فيها البيانات:

* غير دقيقة بشكل كامل.

* متأخرة.

* غير متوفرة.

* مختلفة عن الظروف الفعلية في موقع معين.

لذلك لا ينبغي الاعتماد على التطبيق وحده في القرارات التي تتطلب معلومات رسمية أو دقيقة للغاية، خصوصاً في حالات الطقس الخطرة أو الطوارئ.

عند وجود تحذيرات جوية رسمية، يُنصح بالاعتماد على الجهات الرسمية المختصة.

---

## 19. التغييرات على سياسة الخصوصية

قد نقوم بتحديث سياسة الخصوصية هذه من وقت لآخر بسبب:

* إضافة ميزات جديدة.

* تغيير الخدمات المستخدمة.

* تحديث متطلبات Google Play.

* تغييرات قانونية أو تنظيمية.

* تحسين ممارسات الخصوصية والأمان.

عند إجراء تغييرات مهمة، سيتم تحديث تاريخ "آخر تحديث" الموجود في أعلى هذه الصفحة.

استمرارك في استخدام التطبيق بعد نشر التعديلات يعني أنك اطلعت على السياسة المحدثة، بالقدر الذي يسمح به القانون المعمول به.

---

## 20. التواصل معنا

إذا كانت لديك أسئلة أو طلبات أو استفسارات تتعلق بالخصوصية، يمكنك التواصل معنا عبر:

**Developer / المطور:** [OSSAMA]

**App / التطبيق:** Weather App

**Privacy Email / البريد الإلكتروني للخصوصية:** [weather_app1@proton.me]

يرجى استخدام عنوان واضح عند التواصل، مثل:

**Privacy Request – Weather App**

وسنبذل جهداً معقولاً للرد على استفسارات الخصوصية في أقرب وقت ممكن.

---

## 21. الموافقة

باستخدامك لتطبيق Weather App، فإنك تقر بأنك قرأت سياسة الخصوصية هذه وفهمت كيفية تعامل التطبيق مع البيانات والمعلومات الموضحة فيها.

إذا كنت لا توافق على هذه الممارسات، يمكنك التوقف عن استخدام التطبيق أو تعطيل الأذونات المطلوبة من إعدادات جهازك.

---

**© 2026 Weather App. All rights reserved.**
"""

private const val ENGLISH_PART_1 = """
**Last Updated:** September 16, 2026

**Effective Date:** September 16, 2026

---

## 1. Introduction

Welcome to **Weather App**.

We respect your privacy and are committed to handling information in a transparent and responsible manner.

This Privacy Policy explains how **Weather App** handles information when you use the application, including location information and technical information required to provide weather services and maintain the reliability and security of the application.

By using Weather App, you acknowledge that you have read and understood this Privacy Policy.

We do not sell users' personal information, and we do not use location information or personal information to create advertising profiles about users.

---

## 2. Scope of This Privacy Policy

This Privacy Policy applies to the **Weather App** and the services directly associated with it.

It explains:

* What information the application may access.

* How information is used.

* How location information is handled.

* Third-party services used to provide weather data.

* How information is protected.

* How long information may be retained.

* Your privacy rights.

* How to contact us.

* How changes to this Privacy Policy are handled.

---

## 3. Information the App May Access

### 3.1 Location Information

Weather App may request access to your device's **location** to determine weather conditions and forecasts for your current location.

Location information may be used to:

* Determine your current location.

* Display current weather conditions.

* Provide hourly forecasts.

* Provide daily forecasts.

* Provide weather information relevant to your location.

* Improve the location-selection experience.

### Important

Location access is only available after you grant the relevant Android permission.

You can deny or revoke location permission at any time through your device settings.

If location access is disabled, some features may not work automatically, but you may still be able to search for or manually select a location where supported.

The application does not require your real-world identity to provide basic weather information.

### 3.2 Locations Selected Manually

If you search for a city or location within the application, the selected location name or geographic coordinates may be used to retrieve weather information for that location.

Certain location preferences may be stored locally on your device where supported by the application.

### 3.3 Technical Information

Certain technical information may be processed when the application communicates with online services, including:

* IP address.

* Device type.

* Operating system version.

* Network information.

* Technical information associated with weather requests.

* Error or diagnostic information, where applicable.

This information may be used for:

* Providing the service.

* Processing weather requests.

* Diagnosing technical problems.

* Preventing abuse.

* Maintaining security.

* Improving reliability and stability.

We do not use this information to create personalized advertising profiles.

---
"""

private const val ENGLISH_PART_2 = """
## 4. Weather Data and Location Information

Weather App uses **Open-Meteo** as a source of weather data.

In order to provide weather information, the application may send the selected location or relevant geographic coordinates to the weather service.

According to Open-Meteo's current privacy information, its free API may process non-personal technical information such as IP addresses for technical purposes and abuse prevention. Its server logs may also contain geographic coordinates for troubleshooting purposes, and Open-Meteo states that individual log files are deleted after 90 days under its current policy.

Open-Meteo operates independently from Weather App and has its own terms and privacy practices.

---

## 5. Air Quality Information

Where available, Weather App may provide air-quality information or air-quality indicators.

When this feature is used, the selected location may be sent to the relevant data service to retrieve air-quality information for that location.

Weather App does not use air-quality information to create a personal health profile about users.

Weather and air-quality information should not be considered medical advice or a replacement for official emergency or health guidance.

---

## 6. Personal Information

**Weather App does not require users to create an account to use its core weather features.**

For basic functionality, we do not require you to provide:

* Your real name.

* Phone number.

* Home address.

* Contacts.

* Passwords.

* SMS messages.

* Personal photographs.

* Personal files.

* Payment information.

If future features require personal information, this Privacy Policy will be updated to explain the relevant information and purposes of processing, as required by applicable laws and platform policies.

---

## 7. Advertising

**If the current version of Weather App does not contain advertisements or advertising SDKs, the application does not use personal information for personalized advertising.**

This Privacy Policy does not authorize undisclosed advertising SDKs or tracking technologies.

If advertising is introduced in a future version, this Privacy Policy and the relevant Google Play disclosures will be updated to explain the applicable data practices.

---

## 8. Third-Party Services

Weather App may rely on third-party services that are necessary to provide certain features.

The primary weather data service used by the application is:

**Open-Meteo**

Third-party services operate under their own privacy policies and terms.

Users are encouraged to review the applicable third-party policies to understand how information may be handled by those services.

We do not sell users' personal information to third-party service providers.

---

## 9. Sharing of Information

We do not sell, rent, or trade users' personal information.

Information may be transmitted to service providers when necessary to provide core application functionality, such as obtaining weather information.

Information may also be disclosed where reasonably necessary:

* To comply with applicable law.

* To comply with a valid legal request or court order.

* To protect the rights or security of the application or its users.

* To prevent fraud, abuse, or technical attacks.

We do not use location information for personalized marketing purposes.

---

## 10. Data Stored on Your Device

The application may store certain information locally on your device to improve your experience, such as:

* Application preferences.

* Temperature-unit preferences.

* Saved locations, where supported.

* Display preferences.

* Other local settings required by the application.

Locally stored information remains on your device unless it is removed by the application, the operating system, or by you.

---

## 11. Data Retention and Deletion

We follow a **data-minimization principle**, meaning that information should not be retained for longer than reasonably necessary for its intended purpose.

If information is stored locally by the application, you can generally remove it by:

* Clearing the application's data through Android settings.

* Uninstalling the application.

* Removing saved locations or settings through the application, where such controls are available.

Information processed by third-party services is subject to the retention policies of those services.

Open-Meteo currently states that individual server log files are deleted after 90 days, while aggregated statistical information may be retained according to its applicable policies.

---

## 12. Information Security

We use reasonable technical and organizational measures designed to protect information against:

* Unauthorized access.

* Unauthorized modification.

* Unauthorized disclosure.

* Unauthorized use.

* Loss or destruction.

Where supported, communications with online services use secure protocols such as **HTTPS/TLS**.

However, no method of transmission or electronic storage can be guaranteed to be completely secure.

---
"""

private const val ENGLISH_PART_3 = """
## 13. App Permissions

Depending on the features you use, Weather App may request certain permissions.

### Location

Used to determine your location and provide relevant weather information.

### Internet

Used to retrieve weather information and communicate with required online services.

The application does not request permissions that are unnecessary for its core functionality.

---

## 14. Location Controls

You can control the application's location permission through your Android device settings at any time.

You may:

* Allow location access.

* Deny location access.

* Change the available location permission settings provided by your version of Android.

If location access is disabled, Weather App may not be able to automatically determine your location. You may still be able to search for or manually select a location where supported.

---

## 15. Children's Privacy

Weather App is designed as a weather-information application and is not intended to collect unnecessary personal information from children.

The application does not require sensitive personal information for its core weather functionality.

Where children use the application, parents and guardians are encouraged to teach children not to share personal information with online services without appropriate supervision.

---

## 16. Your Privacy Rights

Depending on applicable law, you may have certain rights regarding your information, including where applicable:

* The right to know what information is processed.

* The right to request access to information.

* The right to request correction of inaccurate information.

* The right to request deletion of information.

* The right to object to certain processing.

* The right to withdraw certain permissions or consent.

* The right to control location access through your device settings.

You may contact us regarding questions or requests concerning your information.

---

## 17. No Sale of Personal Information

**Weather App does not sell users' personal information.**

We do not use location information obtained for the application's weather functionality to sell such information to other parties or create personalized advertising profiles.

---

## 18. Weather Information Accuracy

Weather information provided by Weather App relies on external data sources and services.

Weather information may sometimes be:

* Inaccurate.

* Delayed.

* Unavailable.

* Different from actual local conditions.

Weather App should therefore not be relied upon as the sole source of information for decisions requiring official or highly precise weather information, particularly during severe weather or emergency situations.

For official weather warnings and emergency information, users should rely on the appropriate official authorities.

---

## 19. Changes to This Privacy Policy

We may update this Privacy Policy from time to time due to:

* New application features.

* Changes to third-party services.

* Changes to Google Play requirements.

* Legal or regulatory changes.

* Improvements to privacy and security practices.

When changes are made, the "Last Updated" date at the top of this Privacy Policy will be updated.

Your continued use of Weather App after an updated policy is published constitutes acknowledgment of the updated policy to the extent permitted by applicable law.

---

## 20. Contact Us

If you have questions, concerns, or requests regarding privacy, you may contact us at:

**Developer:** [OSSAMA]

**Application:** Weather App

**Privacy Email:** [weather_app1@proton.me]

For privacy-related requests, please use a clear subject such as:

**Privacy Request – Weather App**

We will make reasonable efforts to respond to privacy inquiries in a timely manner.

---

## 21. Acknowledgment

By using Weather App, you acknowledge that you have read and understood this Privacy Policy and the ways in which the application handles information as described above.

If you do not agree with these practices, you may stop using the application or disable the relevant permissions through your device settings.

---

**© 2026 Weather App. All rights reserved.**
"""
