package com.example.data

enum class RevisionDeckType(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val subtitleBn: String,
    val subtitleEn: String,
    val badgeTextBn: String,
    val badgeTextEn: String
) {
    WB_STATIC_GK(
        id = "wb_static_gk",
        titleBn = "পশ্চিমবঙ্গ স্ট্যাটিক জিকে",
        titleEn = "West Bengal Static GK",
        subtitleBn = "নদ-নদী, বাঁধ, অভয়ারণ্য, লোকনৃত্য ও ঐতিহাসিক স্থান",
        subtitleEn = "Rivers, Dams, Wildlife Sanctuaries, Folk Dances, Historical Places",
        badgeTextBn = "ডেক ১ • WB GK",
        badgeTextEn = "Deck 1 • WB GK"
    ),
    MATH_SHORTCUTS(
        id = "math_shortcuts",
        titleBn = "মাধ্যমিক অঙ্ক শর্টকাট সূত্র",
        titleEn = "Madhyamik Math Shortcut Formulas",
        subtitleBn = "লাভ-ক্ষতি, সময় ও কার্য, সরল/চক্রবৃদ্ধি সুদ, পরিমিতি ২ডি",
        subtitleEn = "Profit & Loss, Time & Work, SI/CI, Mensuration 2D",
        badgeTextBn = "ডেক ২ • Math Tricks",
        badgeTextEn = "Deck 2 • Math Tricks"
    ),
    WBP_ENGLISH(
        id = "wbp_english",
        titleBn = "হাই-ফ্রিকোয়েন্সি WBP ইংলিশ",
        titleEn = "High-Frequency WBP English",
        subtitleBn = "Fixed Prepositions, Phrasal Verbs, Idioms, One-Word",
        subtitleEn = "Fixed Prepositions, Phrasal Verbs, Idioms, One-Word Substitutions",
        badgeTextBn = "ডেক ৩ • WBP English",
        badgeTextEn = "Deck 3 • WBP English"
    )
}

data class PocketRevisionCard(
    val id: String,
    val deck: RevisionDeckType,
    val categoryTagBn: String,
    val categoryTagEn: String,
    val titleBn: String,
    val titleEn: String,
    val frontSummaryBn: List<String>,
    val frontSummaryEn: List<String>,
    val backShortcutOrTipBn: String,
    val backShortcutOrTipEn: String,
    val pyqHighlightBn: String,
    val pyqHighlightEn: String
)

object PocketCardsRepository {

    val allCards: List<PocketRevisionCard> = listOf(
        // ==========================================
        // DECK A: WEST BENGAL STATIC GK (5 Cards)
        // ==========================================
        PocketRevisionCard(
            id = "wbgk_rivers",
            deck = RevisionDeckType.WB_STATIC_GK,
            categoryTagBn = "নদ-নদী (Rivers of WB)",
            categoryTagEn = "Rivers of West Bengal",
            titleBn = "পশ্চিমবঙ্গের প্রধান নদ-নদী ও উৎস",
            titleEn = "Major Rivers & Origins of West Bengal",
            frontSummaryBn = listOf(
                "• দামোদর (বাংলার দুঃখ): উৎস ছোটনাগপুর মালভূমির খামারপাত পাহাড়; প্রধান উপনদী বরাকর।",
                "• তিস্তা (ত্রাসের নদী): উৎস সিকিমের জেমু হিমবাহ; জলপাইগুড়ি ও কালিম্পংয়ের জীবনরেখা।",
                "• সুবর্ণরেখা ও কংসাবতী: পশ্চিমের মালভূমি অঞ্চলের নদী; মুকুটমণিপুরে কংসাবতী ও কুমারী নদীর মিলন।",
                "• তোর্ষা, জলঢাকা, রায়ডাক ও সংকোশ: উত্তরবঙ্গের প্রধান নদী (সংকোশ পশ্চিমবঙ্গ ও আসামের সীমানা নির্ধারণ করে)।"
            ),
            frontSummaryEn = listOf(
                "• Damodar (Sorrow of Bengal): Originates at Khamarpat Hill (Chotanagpur); main tributary is Barakar.",
                "• Teesta (River of Horror): Originates at Zemu Glacier (Sikkim); lifeline of North Bengal.",
                "• Subarnarekha & Kangsabati: Western plateau rivers; Kangsabati meets Kumari at Mukutmanipur.",
                "• Torsha, Jaldhaka, Raidak & Sankosh: Key North Bengal rivers (Sankosh forms WB-Assam boundary)."
            ),
            backShortcutOrTipBn = "মনে রাখার ট্রিক: উত্তরবঙ্গের পূর্বতম নদী 'সংকোশ' এবং সুন্দরবনের আতঙ্ক নদী 'মাতলা' (জোয়ারের জলে পুষ্ট)। অজয় নদ বীরভূম ও পশ্চিম বর্ধমানের সীমানা তৈরি করেছে।",
            backShortcutOrTipEn = "Memory Trick: Easternmost river of WB is 'Sankosh'; tidal river of Sundarbans is 'Matla'. Ajay River forms the boundary between Birbhum and Paschim Bardhaman.",
            pyqHighlightBn = "WBP PYQ: 'বাংলার দুঃখ' বলা হয় দামোদর নদকে; তিস্তা নদীর উৎপত্তি জেমু হিমবাহ থেকে।",
            pyqHighlightEn = "WBP PYQ: Damodar is called 'Sorrow of Bengal'; Teesta originates from Zemu Glacier."
        ),
        PocketRevisionCard(
            id = "wbgk_dams",
            deck = RevisionDeckType.WB_STATIC_GK,
            categoryTagBn = "বাঁধ ও বহুমুখী প্রকল্প (Dams)",
            categoryTagEn = "Dams & Multipurpose Projects",
            titleBn = "গুরুত্বপূর্ণ বাঁধ ও সেচ প্রকল্প",
            titleEn = "Key Dams, Barrages & Hydel Projects",
            frontSummaryBn = listOf(
                "• ম্যাসাঞ্জোর বাঁধ (কানাডা বাঁধ): ময়ূরাক্ষী নদীর ওপর অবস্থিত (তিলপাড়া ব্যারেজ সিউড়ির কাছে)।",
                "• মুকুটমণিপুর বাঁধ (বাঁকুড়া): কংসাবতী ও কুমারী নদীর সংযোগস্থলে অবস্থিত ভারতের দ্বিতীয় বৃহত্তম মাটির বাঁধ।",
                "• ডিভিসি (DVC - ১৯৪৮): আমেরিকার টেনেসি ভ্যালি (TVA)-এর আদলে নির্মিত; তিলাইয়া, মাইথন, পাঞ্চেত ও কোনার বাঁধ।",
                "• ফারাক্কা ব্যারেজ (১৯৭৫): মুর্শিদাবাদ জেলায় গঙ্গা নদীর ওপর নির্মিত (দৈর্ঘ্য প্রায় ২,২৪৫ মিটার)।"
            ),
            frontSummaryEn = listOf(
                "• Massanjore Dam (Canada Dam): Built across Mayurakshi River (Tilpara Barrage near Suri).",
                "• Mukutmanipur Dam (Bankura): At the confluence of Kangsabati & Kumari; 2nd largest earthen dam in India.",
                "• DVC (1948): Modelled on US Tennessee Valley Authority (TVA); includes Tilaiya, Maithon, Panchet & Konar dams.",
                "• Farakka Barrage (1975): Built across the Ganga in Murshidabad (length ~2,245m)."
            ),
            backShortcutOrTipBn = "মনে রাখার ট্রিক: 'মাইথন = বরাকর নদী', 'পাঞ্চেত = দামোদর নদ', 'ম্যাসাঞ্জোর = ময়ূরাক্ষী নদী'। পুরুলিয়ার অযোধ্যা পাহাড়ে রয়েছে পাম্পড স্টোরেজ জলবিদ্যুৎ প্রকল্প (PPSP)।",
            backShortcutOrTipEn = "Memory Trick: 'Maithon = Barakar River', 'Panchet = Damodar River', 'Massanjore = Mayurakshi River'. Purulia Pumped Storage Project (PPSP) is at Ayodhya Hills.",
            pyqHighlightBn = "WBP/KP PYQ: ম্যাসাঞ্জোর বাঁধ কোন নদীর ওপর অবস্থিত? উত্তর: ময়ূরাক্ষী নদী।",
            pyqHighlightEn = "WBP/KP PYQ: On which river is Massanjore Dam located? Ans: Mayurakshi River."
        ),
        PocketRevisionCard(
            id = "wbgk_wildlife",
            deck = RevisionDeckType.WB_STATIC_GK,
            categoryTagBn = "জাতীয় উদ্যান ও অভয়ারণ্য (Wildlife)",
            categoryTagEn = "National Parks & Wildlife Sanctuaries",
            titleBn = "পশ্চিমবঙ্গের ৬টি জাতীয় উদ্যান ও অভয়ারণ্য",
            titleEn = "6 National Parks & Key Sanctuaries of WB",
            frontSummaryBn = listOf(
                "• সুন্দরবন জাতীয় উদ্যান: রয়েল বেঙ্গল টাইগার; ১৯৮৭ সালে UNESCO ওয়ার্ল্ড হেরিটেজ ও ২০১৯ সালে রামসার সাইট।",
                "• জলদাপাড়া (আলিপুরদুয়ার) ও গরুমারা (জলপাইগুড়ি): একশৃঙ্গ গন্ডারের জন্য বিখ্যাত।",
                "• সিঙ্গালিলা জাতীয় উদ্যান (দার্জিলিং): পশ্চিমবঙ্গের উচ্চতম জাতীয় উদ্যান; রেড পান্ডার আবাসস্থল।",
                "• বক্সা টাইগার রিজার্ভ (আলিপুরদুয়ার) ও নেওড়া ভ্যালি (কালিম্পং); বেথুয়াডহরি (নদিয়া) ও বিভূতিভূষণ/পারমাদান (উঃ ২৪ পরগনা)।"
            ),
            frontSummaryEn = listOf(
                "• Sundarban NP: Royal Bengal Tiger; UNESCO World Heritage (1987) & Ramsar Site (2019).",
                "• Jaldapara (Alipurduar) & Gorumara (Jalpaiguri): Famous for One-Horned Rhinoceros.",
                "• Singalila NP (Darjeeling): Highest National Park in WB; famous for Red Panda.",
                "• Buxa Tiger Reserve (Alipurduar) & Neora Valley (Kalimpong); Bethuadahari (Nadia) & Parmadan (North 24 Pgs)."
            ),
            backShortcutOrTipBn = "মনে রাখার ট্রিক: পূর্ব কলকাতা জলাভূমি (২০০২) এবং সুন্দরবন (২০১৯)—পশ্চিমবঙ্গে মোট ২টি রামসার সাইট (Ramsar Sites) রয়েছে।",
            backShortcutOrTipEn = "Memory Trick: West Bengal has 2 Ramsar Sites — East Kolkata Wetlands (2002) and Sundarban Wetland (2019).",
            pyqHighlightBn = "WBP PYQ: বক্সা ব্যাঘ্র প্রকল্প কোন জেলায় অবস্থিত? উত্তর: আলিপুরদুয়ার।",
            pyqHighlightEn = "WBP PYQ: In which district is Buxa Tiger Reserve located? Ans: Alipurduar."
        ),
        PocketRevisionCard(
            id = "wbgk_folk_dances",
            deck = RevisionDeckType.WB_STATIC_GK,
            categoryTagBn = "লোকনৃত্য ও সংস্কৃতি (Folk Culture)",
            categoryTagEn = "Folk Dances & Heritage of WB",
            titleBn = "পশ্চিমবঙ্গের বিখ্যাত লোকনৃত্য ও জেলা",
            titleEn = "Famous Folk Dances & Associated Districts",
            frontSummaryBn = listOf(
                "• ছৌ নাচ (পুরুলিয়া): মুখোশ নৃত্য; ২০১০ সালে UNESCO Intangible Cultural Heritage স্বীকৃতি লাভ করে।",
                "• গম্ভীরা (মালদা): চৈত্র সংক্রান্তিতে শিবের গাজন উপলক্ষে সামাজিক ব্যঙ্গাত্মক লোকনাট্য ও নৃত্য।",
                "• আলকাপ (মুর্শিদাবাদ ও মালদা) এবং বাউল গান (বীরভূম ও নদিয়া — ২০০৫ সালে UNESCO স্বীকৃতি)।",
                "• রায়বেঁশে (বীরভূম ও বর্ধমান): প্রাচীন রণনৃত্য বা সামরিক নৃত্য; টুসু ও ভাদু (পুরুলিয়া ও বাঁকুড়া)।"
            ),
            frontSummaryEn = listOf(
                "• Chhau Dance (Purulia): Martial mask dance; inscribed in UNESCO Intangible Cultural Heritage in 2010.",
                "• Gambhira (Malda): Devotional & socio-satirical folk performance during Chaitra Sankranti.",
                "• Alkap (Murshidabad/Malda) & Baul Tradition (Birbhum/Nadia — UNESCO recognition in 2005).",
                "• Raibenshe (Birbhum/Burdwan): Traditional martial folk dance; Tusu & Bhadu (Purulia/Bankura)."
            ),
            backShortcutOrTipBn = "মনে রাখার ট্রিক: পুরুলিয়ার ছৌ নাচের বিখ্যাত শিল্পী গম্ভীর সিং মুড়া (পদ্মশ্রী)। রায়বেঁশে নৃত্যের পুনরুজ্জীবন ঘটান গুরুসদয় দত্ত (ব্রতচারী আন্দোলনের প্রতিষ্ঠাতা)।",
            backShortcutOrTipEn = "Memory Trick: Padma Shri Gambhir Singh Mura is the legendary exponent of Purulia Chhau. Gurusaday Dutt revived Raibenshe & founded the Bratachari movement.",
            pyqHighlightBn = "WBP PYQ: 'গম্ভীরা' পশ্চিমবঙ্গের কোন জেলার লোকনৃত্য? উত্তর: মালদা।",
            pyqHighlightEn = "WBP PYQ: 'Gambhira' is the folk dance of which district? Ans: Malda."
        ),
        PocketRevisionCard(
            id = "wbgk_historical_places",
            deck = RevisionDeckType.WB_STATIC_GK,
            categoryTagBn = "ঐতিহাসিক স্থান (Historical Places)",
            categoryTagEn = "Historical Places & Archaeology",
            titleBn = "পশ্চিমবঙ্গের ঐতিহাসিক ও প্রত্নতাত্ত্বিক স্থান",
            titleEn = "Historical Monuments & Archaeological Sites",
            frontSummaryBn = listOf(
                "• পলাশী (নদিয়া): ভাগীরথী নদীর তীরে ২৩ জুন ১৭৫৭ সালে পলাশীর যুদ্ধ (সিরাজউদ্দৌলা বনাম রবার্ট ক্লাইভ)।",
                "• হাজারদুয়ারি ও কাটরা মসজিদ (মুর্শিদাবাদ): নবাব নাজিম হুমায়ুন জাঁ (হাজারদুয়ারি) ও মুর্শিদকুলি খাঁ (কাটরা মসজিদ)।",
                "• আদিনা মসজিদ, বড়সোনা মসজিদ ও দাখিল দরওয়াজা (গৌড়/মালদা): সুলতানি আমলের স্থাপত্য।",
                "• বিষ্ণুপুর (বাঁকুড়া - মল্ল রাজাদের টেরাকোটা মন্দির, রাসমঞ্চ), পাণ্ডু রাজার ঢিবি (পূর্ব বর্ধমান) ও চন্দ্রকেতুগড় (উঃ ২৪ পরগনা)।"
            ),
            frontSummaryEn = listOf(
                "• Plassey (Nadia): Battle of Plassey on 23 June 1757 on the banks of Bhagirathi.",
                "• Hazarduari & Katra Mosque (Murshidabad): Built under Nawab Nazim Humayun Jah & Murshid Quli Khan.",
                "• Adina Mosque, Bara Sona Mosque & Dakhil Darwaza (Gour, Malda): Sultanate era monuments.",
                "• Bishnupur (Bankura - Malla Terracotta temples), Pandu Rajar Dhibi (Purba Bardhaman) & Chandraketugarh."
            ),
            backShortcutOrTipBn = "মনে রাখার ট্রিক: পাণ্ডু রাজার ঢিবি অজয় নদের তীরে অবস্থিত তাম্রপ্রস্তর যুগের (Chalcolithic) প্রত্নক্ষেত্র; চন্দ্রকেতুগড় বিদ্যাধরী নদীর তীরে অবস্থিত।",
            backShortcutOrTipEn = "Memory Trick: Pandu Rajar Dhibi is a Chalcolithic site in the Ajay River valley; Chandraketugarh lies along the Bidyadhari River.",
            pyqHighlightBn = "WBP PYQ: আদিনা মসজিদ কোন জেলায় অবস্থিত? উত্তর: মালদা (পাণ্ডুয়া)।",
            pyqHighlightEn = "WBP PYQ: In which district is Adina Mosque located? Ans: Malda (Pandua)."
        ),

        // ==========================================
        // DECK B: MADHYAMIK MATH SHORTCUT FORMULAS (4 Cards)
        // ==========================================
        PocketRevisionCard(
            id = "math_profit_loss",
            deck = RevisionDeckType.MATH_SHORTCUTS,
            categoryTagBn = "লাভ ও ক্ষতি (Profit & Loss)",
            categoryTagEn = "Profit & Loss Shortcuts",
            titleBn = "লাভ ও ক্ষতির ৩০-সেকেন্ডের শর্টকাট সূত্র",
            titleEn = "30-Second Profit & Loss Shortcut Formulas",
            frontSummaryBn = listOf(
                "• ক্রমিক ছাড় (Successive Discount): x% ও y% দুটি ক্রমিক ছাড়ের সমতুল্য ছাড় = [x + y - (xy / 100)]%",
                "• ক্রয়মূল্য = বিক্রয়মূল্য: A-টি দ্রব্যের ক্রয়মূল্য = B-টি দ্রব্যের বিক্রয়মূল্য হলে, লাভ/ক্ষতি% = [(A - B) / B] × 100%",
                "• সমান বিক্রয়মূল্য (Same SP): একটিতে x% লাভ ও অপরটিতে x% ক্ষতি হলে সর্বদা ক্ষতি হবে = (x² / 100)%",
                "• অসৎ ব্যবসায়ী (False Weight): 1000 গ্রামের বদলে W গ্রাম বাটখারা দিলে লাভ% = [(1000 - W) / W] × 100%"
            ),
            frontSummaryEn = listOf(
                "• Successive Discount: Equivalent single discount for x% and y% = [x + y - (xy / 100)]%",
                "• CP of A articles = SP of B articles: Profit/Loss % = [(A - B) / B] × 100%",
                "• Same SP with +x% Profit & -x% Loss: Always results in Net Loss % = (x² / 100)%",
                "• Dishonest Dealer (False Weight W instead of 1000g): Gain % = [(1000 - W) / W] × 100%"
            ),
            backShortcutOrTipBn = "উদাহরণ ট্রিক: ২০% ও ১০% ক্রমিক ছাড়ের সমতুল্য ছাড় = ২০ + ১০ - (২০০/১০০) = ২৮%। আবার ১৫টি ফলের ক্রয়মূল্য = ১২টির বিক্রয়মূল্য হলে লাভ = (১৫-১২)/১২ × ১০০ = ২৫%!",
            backShortcutOrTipEn = "Quick Example: 20% + 10% successive discount = 20 + 10 - (200/100) = 28%. If CP of 15 = SP of 12, Profit % = (15-12)/12 × 100 = 25%!",
            pyqHighlightBn = "WBP Constable ফোকাস: বিক্রয়মূল্য সমান থাকলে ১০% লাভ ও ১০% ক্ষতিতে মোটের ওপর ১% ক্ষতি হয়।",
            pyqHighlightEn = "WBP Constable Focus: Equal SP with 10% gain and 10% loss always gives 1% overall loss."
        ),
        PocketRevisionCard(
            id = "math_time_work",
            deck = RevisionDeckType.MATH_SHORTCUTS,
            categoryTagBn = "সময় ও কার্য (Time & Work)",
            categoryTagEn = "Time & Work Shortcuts",
            titleBn = "সময়, কার্য ও নল-চৌবাচ্চা শর্টকাট",
            titleEn = "Time & Work / Pipes & Cisterns Formulas",
            frontSummaryBn = listOf(
                "• দুজনে একত্রে কাজ: A একা x দিনে ও B একা y দিনে করলে, (A + B) একত্রে করে = (x × y) / (x + y) দিনে।",
                "• একজনের একা সময়: (A + B) একত্রে x দিনে এবং A একা y দিনে করলে, B একা করে = (x × y) / (y - x) দিনে।",
                "• M₁D₁H₁ সূত্র: (M₁ × D₁ × H₁) / W₁ = (M₂ × D₂ × H₂) / W₂  [M = মানুষ, D = দিন, H = ঘণ্টা, W = কাজের পরিমাণ]",
                "• কর্মদক্ষতা (Efficiency): কর্মদক্ষতার অনুপাত A : B = m : n হলে, গৃহীত সময়ের অনুপাত হবে n : m (ব্যস্তানুপাতিক)।"
            ),
            frontSummaryEn = listOf(
                "• Combined Work: If A takes x days and B takes y days, (A + B) together take = (x × y) / (x + y) days.",
                "• Individual Work: If (A + B) take x days and A alone takes y days, B alone takes = (x × y) / (y - x) days.",
                "• Chain Rule Formula: (M₁ × D₁ × H₁) / W₁ = (M₂ × D₂ × H₂) / W₂",
                "• Efficiency vs Time: If efficiency ratio of A : B is m : n, then time taken ratio is n : m."
            ),
            backShortcutOrTipBn = "ল.সা.গু (LCM) মেথড: ভগ্নাংশ এড়াতে দিনগুলির ল.সা.গু-কে 'মোট কাজ (Total Units)' ধরুন। যেমন ১০ দিন ও ১৫ দিনের ল.সা.গু = ৩০ ইউনিট (দক্ষতা ৩ ও ২, মোট সময় = ৩০/৫ = ৬ দিন)।",
            backShortcutOrTipEn = "LCM Method: Take the LCM of days as 'Total Work Units' to avoid fractions. E.g., for 10 & 15 days, LCM = 30 units (daily rates 3 & 2, total time = 30/5 = 6 days).",
            pyqHighlightBn = "WBP PYQ: A, B-এর ৩ গুণ কর্মক্ষম এবং একটি কাজ B-এর থেকে ২০ দিন কম সময়ে করে → সময়ের অনুপাত ১:৩, পার্থক্য ২ ইউনিট = ২০ দিন, তাই A করে ১০ দিনে ও B করে ৩০ দিনে!",
            pyqHighlightEn = "WBP PYQ: A is 3x as efficient as B and finishes 20 days earlier → Time ratio 1:3, diff 2 parts = 20 days, so A = 10 days, B = 30 days!"
        ),
        PocketRevisionCard(
            id = "math_si_ci",
            deck = RevisionDeckType.MATH_SHORTCUTS,
            categoryTagBn = "সরল ও চক্রবৃদ্ধি সুদ (SI & CI)",
            categoryTagEn = "Simple & Compound Interest",
            titleBn = "সরল ও চক্রবৃদ্ধি সুদের নিমেষে সমাধান",
            titleEn = "Simple & Compound Interest Rapid Formulas",
            frontSummaryBn = listOf(
                "• ২ বছরের CI ও SI-এর পার্থক্য: পার্থক্য (D) = P × (R / 100)²  ⇒  আসল (P) = D × (100 / R)²",
                "• ৩ বছরের CI ও SI-এর পার্থক্য: D = P × (R / 100)² × [(300 + R) / 100]",
                "• সরল সুদে টাকা n গুণ হওয়া: T বছরে সুদে-আসলে n গুণ হলে, সুদের হার R = [(n - 1) × 100] / T",
                "• চক্রবৃদ্ধি সুদে দ্বিগুণ/ঘাত বৃদ্ধি: T বছরে ২ গুণ হলে, 2³ = ৮ গুণ হতে সময় লাগবে = (3 × T) বছর!"
            ),
            frontSummaryEn = listOf(
                "• 2-Year CI vs SI Difference: Diff (D) = P × (R / 100)²  ⇒  Principal (P) = D × (100 / R)²",
                "• 3-Year CI vs SI Difference: D = P × (R / 100)² × [(300 + R) / 100]",
                "• Money becoming n-times in SI: R = [(n - 1) × 100] / T; for m-times: T₂ = [(m - 1) / (n - 1)] × T₁",
                "• CI Power Rule: If a sum doubles (2¹) in T years at CI, it becomes 8 times (2³) in (3 × T) years!"
            ),
            backShortcutOrTipBn = "উদাহরণ ট্রিক: বার্ষিক ১০% হারে ২ বছরের চক্রবৃদ্ধি ও সরল সুদের পার্থক্য ৫০ টাকা হলে আসল P = ৫০ × (১০০/১০)² = ৫০ × ১০০ = ৫,০০০ টাকা ( মাত্র ৫ সেকেন্ডে উত্তর)!",
            backShortcutOrTipEn = "Quick Example: If 2-year CI-SI diff at 10% p.a. is ₹50, Principal P = 50 × (100/10)² = 50 × 100 = ₹5,000 in 5 seconds!",
            pyqHighlightBn = "WBP SI/Constable PYQ: চক্রবৃদ্ধি সুদে কোনো টাকা ৪ বছরে দ্বিগুণ হলে, কত বছরে ৮ গুণ হবে? উত্তর: ৮ = ২³, তাই ৪ × ৩ = ১২ বছরে।",
            pyqHighlightEn = "WBP SI/Constable PYQ: If money doubles in 4 years at CI, when will it become 8 times? Ans: 8 = 2³, so 4 × 3 = 12 years."
        ),
        PocketRevisionCard(
            id = "math_mensuration_2d",
            deck = RevisionDeckType.MATH_SHORTCUTS,
            categoryTagBn = "পরিমিতি ২ডি (Mensuration 2D)",
            categoryTagEn = "Mensuration 2D Formulas",
            titleBn = "পরিমিতি (ত্রিভুজ, চতুর্ভুজ ও বৃত্ত) শর্টকাট",
            titleEn = "Mensuration 2D (Triangle, Quad & Circle)",
            frontSummaryBn = listOf(
                "• সমবাহু ত্রিভুজ (বাহু a): ক্ষেত্রফল = (√3 / 4)a², উচ্চতা (h) = (√3 / 2)a, উচ্চতা থেকে ক্ষেত্রফল = h² / √3",
                "• সমবাহু ত্রিভুজের পরিব্যাসার্ধ (R) ও অন্তর্ব্যাসার্ধ (r): R : r = 2 : 1 এবং ক্ষেত্রফলের অনুপাত = 4 : 1",
                "• বর্গক্ষেত্র ও রম্বস: বর্গক্ষেত্রের কর্ণ d = a√2, ক্ষেত্রফল = d² / 2; রম্বসের ক্ষেত্রফল = ½ × d₁ × d₂",
                "• ক্ষেত্রফলের শতকরা পরিবর্তন: বাহু বা ব্যাসার্ধ x% বৃদ্ধি পেলে ক্ষেত্রফল বৃদ্ধি = [2x + (x² / 100)]%"
            ),
            frontSummaryEn = listOf(
                "• Equilateral Triangle (side a): Area = (√3 / 4)a², Height (h) = (√3 / 2)a, Area from height = h² / √3",
                "• Circumradius (R) : Inradius (r) of Equilateral Triangle = 2 : 1 (Area ratio = 4 : 1)",
                "• Square & Rhombus: Square diagonal d = a√2, Area = d² / 2; Rhombus Area = ½ × d₁ × d₂",
                "• Area % Change: If side or radius increases by x%, Area increases by [2x + (x² / 100)]%"
            ),
            backShortcutOrTipBn = "মনে রাখার ট্রিক: বৃত্তের ব্যাসার্ধ বা বর্গক্ষেত্রের বাহু ১০% বাড়লে ক্ষেত্রফল বাড়ে = ২(১০) + (১০০/১০০) = ২১%! আয়তক্ষেত্রের পরিসীমা = 2(দৈর্ঘ্য + প্রস্থ) এবং কর্ণ = √(দৈর্ঘ্য² + প্রস্থ²)।",
            backShortcutOrTipEn = "Memory Trick: If radius/side increases by 10%, area increases by 2(10) + (100/100) = 21%! Rectangle diagonal = √(l² + b²).",
            pyqHighlightBn = "WBP PYQ: একটি রম্বসের দুটি কর্ণ ১৬ সেমি ও ১২ সেমি হলে ক্ষেত্রফল = ½ × ১৬ × ১২ = ৯৬ বর্গসেমি এবং বাহু = √(৮² + ৬²) = ১০ সেমি।",
            pyqHighlightEn = "WBP PYQ: Rhombus with diagonals 16 cm & 12 cm has Area = ½ × 16 × 12 = 96 cm² and Side = √(8² + 6²) = 10 cm."
        ),

        // ==========================================
        // DECK C: HIGH-FREQUENCY WBP ENGLISH (4 Cards)
        // ==========================================
        PocketRevisionCard(
            id = "eng_fixed_prepositions",
            deck = RevisionDeckType.WBP_ENGLISH,
            categoryTagBn = "Fixed Prepositions (ফিক্সড প্রিপোজিশন)",
            categoryTagEn = "High-Frequency Fixed Prepositions",
            titleBn = "WBP পরীক্ষায় বারবার আসা Fixed Prepositions",
            titleEn = "Must-Know Fixed Prepositions for WBP/KP",
            frontSummaryBn = listOf(
                "• Abide by (মেনে চলা) | Accused of (অভিযুক্ত) | Acquitted of (নির্দোষ প্রমাণিত) | Guilty of (দোষী)",
                "• Senior / Junior / Superior / Inferior / Prior / Prefer + TO ('than' কখনোই বসবে না!)",
                "• Die of (রোগে মৃত্যু - died of cholera) | Die from (অতিরিক্ত কাজ/ক্ষত/বিষে মৃত্যু - died from overwork)",
                "• Blind in (এক চোখে অন্ধ - blind in left eye) | Blind to (নিজের সন্তান বা কারো দোষ না দেখা)"
            ),
            frontSummaryEn = listOf(
                "• Abide by (obey) | Accused of (charged with) | Acquitted of (declared innocent) | Guilty of",
                "• Senior / Junior / Superior / Inferior / Prior / Prefer + TO (Never use 'than'!)",
                "• Die of (a disease - died of malaria) | Die from (overwork / wound / poison)",
                "• Blind in (one/both eyes) | Blind to (someone's faults) | Congratulate ON | Insist ON"
            ),
            backShortcutOrTipBn = "এক্সাম হল ট্রিক: 'Prefer tea TO coffee', 'Fond OF music', 'Prevent FROM going', 'Abstain FROM smoking', 'Good AT English', 'Angry WITH a person' কিন্তু 'Angry AT behaviour'!",
            backShortcutOrTipEn = "Exam Hall Trick: 'Prefer tea TO coffee', 'Fond OF music', 'Prevent FROM going', 'Abstain FROM', 'Good AT English', 'Angry WITH a person' vs 'Angry AT behaviour'!",
            pyqHighlightBn = "WBP Constable PYQ: He is senior ___ me in service. উত্তর: to (than নয়)।",
            pyqHighlightEn = "WBP Constable PYQ: He is senior ___ me in service. Ans: to (not 'than')."
        ),
        PocketRevisionCard(
            id = "eng_phrasal_verbs",
            deck = RevisionDeckType.WBP_ENGLISH,
            categoryTagBn = "Phrasal Verbs (গ্রুপ ভার্ব)",
            categoryTagEn = "Top WBP Phrasal Verbs",
            titleBn = "সবচেয়ে গুরুত্বপূর্ণ Phrasal Verbs (বাংলা অর্থসহ)",
            titleEn = "High-Yield Phrasal Verbs with Meanings",
            frontSummaryBn = listOf(
                "• Call off = প্রত্যাহার বা বাতিল করা (Cancel) | Call in = ডেকে পাঠানো (Summon a doctor/police)",
                "• Give up = ত্যাগ করা (Abandon a habit) | Give in = বশ্যতা স্বীকার করা (Surrender / Yield)",
                "• Look into = তদন্ত করা (Investigate) | Look after = দেখাশোনা করা | Look down upon = ঘৃণা করা (Despise)",
                "• Put out = আগুন/আলো নেভানো (Extinguish) | Put off = স্থগিত রাখা (Postpone) | Put up with = সহ্য করা (Tolerate)"
            ),
            frontSummaryEn = listOf(
                "• Call off = Cancel (a strike/match) | Call in = Summon / Send for (a doctor)",
                "• Give up = Abandon / Quit | Give in = Surrender / Yield | Give away = Distribute",
                "• Look into = Investigate | Look after = Take care of | Look down upon = Hate / Despise",
                "• Put out = Extinguish (fire) | Put off = Postpone | Put up with = Tolerate / Endure"
            ),
            backShortcutOrTipBn = "আরও ৪টি মাস্ট-রিভিশন: • Break out = হঠাৎ ছড়িয়ে পড়া (মহামারী/আগুন - Cholera broke out) • Bring up = লালন-পালন করা (Rear) • Pass away = মারা যাওয়া (Die) • Turn down = প্রত্যাখ্যান করা (Reject)।",
            backShortcutOrTipEn = "Bonus Top 4: • Break out = Spread suddenly (epidemic/war) • Bring up = Rear/educate • Pass away = Die • Turn down = Reject an offer.",
            pyqHighlightBn = "WBP PYQ: The police will 'look into' the matter. ('Look into' মানে Investigate বা তদন্ত করা)।",
            pyqHighlightEn = "WBP PYQ: The police will 'look into' the matter. ('Look into' = Investigate)."
        ),
        PocketRevisionCard(
            id = "eng_idioms",
            deck = RevisionDeckType.WBP_ENGLISH,
            categoryTagBn = "Idioms & Phrases (বাগধারা)",
            categoryTagEn = "Idioms & Phrases",
            titleBn = "কমন Idioms & Phrases (এক নজরে অর্থ)",
            titleEn = "Most Repeated Idioms & Phrases",
            frontSummaryBn = listOf(
                "• A bolt from the blue = বিনা মেঘে বজ্রপাত (An unexpected & sudden shock/calamity)",
                "• Once in a blue moon = কদাচিৎ / খুব কম (Very rarely)",
                "• At the eleventh hour = একেবারে শেষ মুহূর্তে (At the very last moment)",
                "• Beat about the bush = আসল কথা এড়িয়ে ঘুরিয়ে বলা (Talk indirectly without coming to the point)"
            ),
            frontSummaryEn = listOf(
                "• A bolt from the blue = An unexpected & sudden calamity or shock",
                "• Once in a blue moon = Very rarely",
                "• At the eleventh hour = At the very last moment",
                "• Beat about the bush = Approach a subject indirectly without coming to the point"
            ),
            backShortcutOrTipBn = "আরও গুরুত্বপূর্ণ Idioms: • Crocodile tears = মায়াকান্না (False sympathy) • A red letter day = স্মরণীয় বা শুভ দিন (An important joyful day) • Burn the midnight oil = রাত জেগে কঠোর পড়াশোনা করা • By leaps and bounds = খুব দ্রুতগতিতে (Very rapidly)।",
            backShortcutOrTipEn = "More High-Yield Idioms: • Crocodile tears = Pretended sorrow • A red letter day = Memorable day • Burn the midnight oil = Work/study late into the night • By leaps and bounds = Very rapidly.",
            pyqHighlightBn = "WBP PYQ: 'A red letter day' means — An important and joyful day (স্মরণীয় দিন)।",
            pyqHighlightEn = "WBP PYQ: 'A red letter day' means — An important and joyful day."
        ),
        PocketRevisionCard(
            id = "eng_one_word",
            deck = RevisionDeckType.WBP_ENGLISH,
            categoryTagBn = "One-Word Substitution (এক কথায় প্রকাশ)",
            categoryTagEn = "One-Word Substitutions",
            titleBn = "পরীক্ষায় কমন যোগ্য One-Word Substitutions",
            titleEn = "High-Frequency One-Word Substitutions",
            frontSummaryBn = listOf(
                "• A remedy for all diseases (সর্বরোগহর ঔষধ) = Panacea",
                "• One who looks at the bright side of things = Optimist (আশাবাদী) | Dark side = Pessimist (নিরাশাবাদী)",
                "• One who loves mankind = Philanthropist (মানবপ্রেমিক) | Hates mankind = Misanthrope",
                "• Handwriting that cannot be read = Illegible (অস্পষ্ট) | Cannot be corrected = Incorrigible"
            ),
            frontSummaryEn = listOf(
                "• A remedy for all diseases = Panacea",
                "• One who looks at the bright side = Optimist | Dark side = Pessimist",
                "• Lover of mankind = Philanthropist | Hater of mankind = Misanthrope",
                "• Handwriting that cannot be read = Illegible | Beyond correction = Incorrigible"
            ),
            backShortcutOrTipBn = "আরও মনে রাখুন: • Life history written by oneself = Autobiography (আত্মজীবনী) • One who knows everything = Omniscient (সর্বজ্ঞ) • Present everywhere = Omnipresent (সর্বব্যাপী) • A person who does not believe in God = Atheist (নাস্তিক)।",
            backShortcutOrTipEn = "Also Remember: • Life story written by oneself = Autobiography • All-knowing = Omniscient • Present everywhere = Omnipresent • Does not believe in God = Atheist.",
            pyqHighlightBn = "WBP PYQ: 'A remedy for all diseases' — উত্তর: Panacea।",
            pyqHighlightEn = "WBP PYQ: 'A remedy for all diseases' — Ans: Panacea."
        )
    )

    fun getCardsForDeck(deck: RevisionDeckType): List<PocketRevisionCard> =
        allCards.filter { it.deck == deck }
}
