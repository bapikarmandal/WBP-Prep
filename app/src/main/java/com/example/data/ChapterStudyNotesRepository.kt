package com.example.data

data class StudyFormulaOrRule(
    val titleBn: String,
    val formulaOrRuleBn: String,
    val solvedExampleBn: String
)

data class StaticRevisionTableRow(
    val col1: String,
    val col2: String,
    val col3: String
)

data class StaticRevisionTable(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val headersBn: Triple<String, String, String>,
    val rows: List<StaticRevisionTableRow>
)

data class ChapterStudyNote(
    val chapterId: String,
    val subject: String,
    val summaryBulletsBn: List<String>,
    val shortcutFormulas: List<StudyFormulaOrRule>,
    val examTipBn: String
)

/**
 * Complete Exam-Cracking Study Notes, Madhyamik Math Shortcut Formulas,
 * West Bengal Special Static GK Revision Tables, and High-Frequency WBP English Notes.
 */
object ChapterStudyNotesRepository {

    val wbStaticGkTables: List<StaticRevisionTable> = listOf(
        StaticRevisionTable(
            id = "WB_RIVERS_DAMS",
            titleBn = "পশ্চিমবঙ্গের প্রধান নদনদী, উপনদী ও বাঁধ (WB Rivers & Dams)",
            titleEn = "WB Rivers, Tributaries & Dams",
            headersBn = Triple("নদী (River)", "উৎপত্তি ও উপনদী", "বাঁধ / তীরবর্তী শহর"),
            rows = listOf(
                StaticRevisionTableRow("ভাগীরথী-হুগলি", "গঙ্গোত্রী হিমবাহ; অজয়, দামোদর, রূপনারায়ণ, ময়ূরাক্ষী", "ফরাক্কা ব্যারাজ (১৯৭৫); মুর্শিদাবাদ, নবদ্বীপ, কলকাতা"),
                StaticRevisionTableRow("দামোদর (বাংলার দুঃখ)", "খামারপাত পাহাড় (ছোটনাগপুর); বরাকর, কোনার, বোকারো", "পাঞ্চেত, মাইথন (বরাকর), দুর্গাপুর ব্যারাজ; আসানসোল"),
                StaticRevisionTableRow("তিস্তা (ত্রাসের নদী)", "জেমু হিমবাহ (সিকিম); রঙ্গিত, জলঢাকা, করলা", "গজলডোবা ব্যারাজ; জলপাইগুড়ি (করলা ও তিস্তার তীরে)"),
                StaticRevisionTableRow("কংসাবতী ও কুমারী", "অযোধ্যা পাহাড় (পুরুলিয়া); কুমারী নদী", "মুকুটমণিপুর জলাধার (বাঁকুড়া — ২য় বৃহত্তম মাটির বাঁধ)"),
                StaticRevisionTableRow("ময়ূরাক্ষী নদী", "ত্রিকূট পাহাড় (ঝাড়খণ্ড); ব্রাহ্মণী, দ্বারকা, কোপাই", "ম্যাসাঞ্জোর বাঁধ (কানাডা ড্যাম) ও তিলপাড়া ব্যারাজ (সিউড়ি)"),
                StaticRevisionTableRow("মহানন্দা ও বালাসন", "মহালদিরাম পাহাড় (দার্জিলিং); মেচি, বালাসন", "শিলিগুড়ি ও ইংরেজবাজার (মালদা)")
            )
        ),
        StaticRevisionTable(
            id = "WB_PARKS_SANCTUARIES",
            titleBn = "পশ্চিমবঙ্গের জাতীয় উদ্যান ও বন্যপ্রাণী অভয়ারণ্য",
            titleEn = "WB National Parks & Wildlife Sanctuaries",
            headersBn = Triple("জাতীয় উদ্যান / অভয়ারণ্য", "জেলা (District)", "বিখ্যাত সংরক্ষিত প্রাণী"),
            rows = listOf(
                StaticRevisionTableRow("সুন্দরবন জাতীয় উদ্যান", "দক্ষিণ ২৪ পরগনা ও উত্তর ২৪ পরগনা", "রয়্যাল বেঙ্গল টাইগার, খাঁড়ির কুমির (UNESCO 1987)"),
                StaticRevisionTableRow("জলদাপাড়া জাতীয় উদ্যান", "আলিপুরদুয়ার (তোর্সা নদীর তীরে)", "একশৃঙ্গ গন্ডার (One-horned Rhinoceros)"),
                StaticRevisionTableRow("গরুমারা জাতীয় উদ্যান", "জলপাইগুড়ি (মূর্তী ও জলঢাকা নদী)", "একশৃঙ্গ গন্ডার ও হাতি"),
                StaticRevisionTableRow("বক্সা টাইগার রিজার্ভ", "আলিপুরদুয়ার", "বাঘ, মেঘলা চিতা ও এশিয়ান হাতি"),
                StaticRevisionTableRow("সিঙ্গালিলা ও নেওড়া ভ্যালি", "দার্জিলিং ও কালিম্পং", "রেড পান্ডা (Red Panda) ও হিমালয়ান ভাল্লুক"),
                StaticRevisionTableRow("বেথুয়াডহরি ও পারমাদান", "নদিয়া (বেথুয়াডহরি) ও উত্তর ২৪ পরগনা (পারমাদান)", "চিত্রা হরিণ (Spotted Deer)")
            )
        ),
        StaticRevisionTable(
            id = "WB_FOLK_DANCES",
            titleBn = "পশ্চিমবঙ্গের জেলাভিত্তিক লোকনৃত্য ও সংস্কৃতি",
            titleEn = "Folk Dances of West Bengal by District",
            headersBn = Triple("লোকনৃত্য / গান", "প্রধান জেলা", "বিশেষ বৈশিষ্ট্য"),
            rows = listOf(
                StaticRevisionTableRow("ছৌ নাচ (Chhau)", "পুরুলিয়া", "মুখোশ পরিহিত পৌরাণিক বীরত্বব্যঞ্জক নাচ (UNESCO 2010)"),
                StaticRevisionTableRow("গম্ভীরা (Gambhira)", "মালদা", "শিব বন্দনা ও সামাজিক সচেতনতামূলক মুখোশ নাচ-গান"),
                StaticRevisionTableRow("ভাওয়াইয়া ও বিষহরা", "কোচবিহার, আলিপুরদুয়ার, জলপাইগুড়ি", "উত্তরবঙ্গের রাজবংশী সমাজের ঐতিহ্যবাহী লোকগান"),
                StaticRevisionTableRow("রায়বেশে ও ঢালি", "বীরভূম, বর্ধমান ও মুর্শিদাবাদ", "প্রাচীন লাঠিখেলা ও সামরিক কসরতভিত্তিক বীরত্বপূর্ণ নৃত্য"),
                StaticRevisionTableRow("টুসু ও ভাদু পরব", "পুরুলিয়া, বাঁকুড়া, ঝাড়গ্রাম", "পৌষ সংক্রান্তিতে টুসু এবং ভাদ্র মাসে ভাদু লোকউৎসব"),
                StaticRevisionTableRow("বাউল ও কীর্তন", "বীরভূম (জয়দেব-কেন্দুলি) ও নদিয়া (নবদ্বীপ)", "একতারা ও খোল-করতাল সহযোগে আধ্যাত্মিক লোকসংগীত")
            )
        ),
        StaticRevisionTable(
            id = "HISTORICAL_BATTLES_GG",
            titleBn = "ভারতের ঐতিহাসিক যুদ্ধ ও গভর্নর জেনারেল তালিকা",
            titleEn = "Historical Battles & Governor-Generals",
            headersBn = Triple("যুদ্ধ / গভর্নর জেনারেল", "সাল (Year)", "পক্ষসমূহ / প্রধান সংস্কার ও ঘটনা"),
            rows = listOf(
                StaticRevisionTableRow("পলাশীর যুদ্ধ ও বক্সারের যুদ্ধ", "১৭৫৭ (২৩ জুন) ও ১৭৬৪", "সিরাজউদ্দৌলা বনাম ক্লাইভ (পলাশী); মীর কাসিম জোট বনাম মুনরো (বক্সার)"),
                StaticRevisionTableRow("পানিপথের ৩টি যুদ্ধ", "১৫২৬, ১৫৫৬ ও ১৭৬১", "বাবর-ইব্রাহিম লোদি (১ম); আকবর-হিমু (২য়); আবদালি-মারাঠা (৩য়)"),
                StaticRevisionTableRow("লর্ড উইলিয়াম বেন্টিঙ্ক", "১৮২৮–১৮৩৫", "ভারতের প্রথম গভর্নর জেনারেল; সতীদাহ প্রথা রদ (১৮২৯)"),
                StaticRevisionTableRow("লর্ড ডালহৌসি", "১৮৪৮–১৮৫৬", "স্বত্ববিলোপ নীতি, ভারতে প্রথম রেলপথ (১৮৫৩), বিধবা বিবাহ আইন (১৮৫৬)"),
                StaticRevisionTableRow("লর্ড ক্যানিং ও লর্ড কার্জন", "১৮৫৭ ও ১৯০৫", "প্রথম ভাইসরয় ও সিপাহী বিদ্রোহ (ক্যানিং); বঙ্গভঙ্গ ১৯০৫ (কার্জন)"),
                StaticRevisionTableRow("লর্ড মাউন্টব্যাটেন ও সি. রাজাগোপালাচারী", "১৯৪৭–১৯৫০", "স্বাধীন ভারতের প্রথম গভর্নর জেনারেল (মাউন্টব্যাটেন) ও প্রথম ভারতীয় (রাজাগোপালাচারী)")
            )
        ),
        StaticRevisionTable(
            id = "CONSTITUTION_SCIENCE_ONELINERS",
            titleBn = "ভারতীয় সংবিধানের গুরুত্বপূর্ণ ধারা ও বিজ্ঞান ওয়ান-লাইনার",
            titleEn = "Key Constitution Articles & Science One-Liners",
            headersBn = Triple("ধারা / বিজ্ঞান বিষয়", "মূল বিষয়বস্তু", "পরীক্ষায় আসার মতো শর্ট নোট"),
            rows = listOf(
                StaticRevisionTableRow("ধারা ১৪–১৮ এবং ১৭, ১৮", "সাম্যের অধিকার", "ধারা ১৭ = অস্পৃশ্যতা দূরীকরণ | ধারা ১৮ = খেতাব বর্জন"),
                StaticRevisionTableRow("ধারা ১৯, ২১ ও ২১A", "স্বাধীনতা ও শিক্ষার অধিকার", "ধারা ২১ = জীবন ও ব্যক্তিগত স্বাধীনতা | ধারা ২১A = ৬–১৪ বছর শিক্ষার অধিকার (৮৬তম সংশোধনী)"),
                StaticRevisionTableRow("ধারা ৩২ ও ধারা ২২৬", "সাংবিধানিক প্রতিবিধান (Writs)", "সুপ্রিম কোর্ট (ধারা ৩২ — সংবিধানের হৃদয় ও আত্মা) ও হাইকোর্ট (ধারা ২২৬)"),
                StaticRevisionTableRow("ধারা ৪০, ৪৪ ও ৫১A", "DPSP ও মৌলিক কর্তব্য", "ধারা ৪০ = গ্রাম পঞ্চায়েত | ধারা ৪৪ = অভিন্ন দেওয়ানি বিধি | ৫১A = মৌলিক কর্তব্য (১১টি)"),
                StaticRevisionTableRow("ধারা ৩৫২, ৩৫৬ ও ৩৬০", "জরুরি অবস্থা (Emergency)", "৩৫২ = জাতীয় জরুরি অবস্থা | ৩৫৬ = রাষ্ট্রপতির শাসন | ৩৬০ = আর্থিক জরুরি অবস্থা"),
                StaticRevisionTableRow("ভিটামিন ও অভাবজনিত রোগ", "A, B1, C, D, K", "A = রাতকানা | B1 = বেরিবেরি | C = স্কার্ভি | D = রিকেট | K = রক্ত তঞ্চনে বাধা")
            )
        ),
        StaticRevisionTable(
            id = "HIGH_FREQUENCY_ENGLISH",
            titleBn = "WBP স্পেশাল ইংরেজি: Fixed Prepositions, Phrasal Verbs & Idioms",
            titleEn = "High-Frequency WBP English Cheat Sheet",
            headersBn = Triple("শব্দ / Idiom / Phrasal Verb", "সঠিক প্রয়োগ / অর্থ", "বাংলা অর্থ ও উদাহরণ"),
            rows = listOf(
                StaticRevisionTableRow("Abide BY / Accused OF", "Fixed Preposition", "Abide by = মেনে চলা | Accused of = অভিযুক্ত (Guilty of)"),
                StaticRevisionTableRow("Die OF / Die FROM / Prefer TO", "Fixed Preposition", "রোগে মারা গেলে Die of | ক্ষতে/পরিশ্রমে Die from | Senior/Junior/Prefer-এর পর To"),
                StaticRevisionTableRow("Fond OF / Good AT / Congratulate ON", "Fixed Preposition", "Fond of = অনুরাগী | Good at = দক্ষ (ইংরেজি/গণিতে) | Congratulate on = অভিনন্দন"),
                StaticRevisionTableRow("Call OFF / Put OUT / Look AFTER", "Phrasal Verbs", "Call off = প্রত্যাহার করা | Put out = আগুন নেভানো | Look after = দেখাশোনা করা"),
                StaticRevisionTableRow("Give UP / Break OUT / Put UP WITH", "Phrasal Verbs", "Give up = ত্যাগ করা | Break out = প্রাদুর্ভাব ঘটা | Put up with = সহ্য করা (Tolerate)"),
                StaticRevisionTableRow("Bolt from the blue / Once in a blue moon", "Idioms & Phrases", "বিনা মেঘে বজ্রপাত (আকস্মিক বিপদ) / কদাচিৎ (Very rarely)")
            )
        )
    )

    fun getStudyNoteForChapter(chapter: WbpChapterInfo): ChapterStudyNote {
        return when (chapter.subject.uppercase()) {
            "MATH" -> buildMathChapterNote(chapter)
            "REASONING" -> buildReasoningChapterNote(chapter)
            "ENGLISH" -> buildEnglishChapterNote(chapter)
            else -> buildGkChapterNote(chapter)
        }
    }

    private fun buildMathChapterNote(chapter: WbpChapterInfo): ChapterStudyNote {
        return when (chapter.chapterId) {
            "MATH_CH_01" -> ChapterStudyNote(
                chapterId = chapter.chapterId,
                subject = "MATH",
                summaryBulletsBn = listOf(
                    "ভগ্নাংশ থেকে শতকরা রূপান্তর: ১/২ = ৫০%, ১/৩ = ৩৩.৩৩%, ১/৪ = ২৫%, ১/৫ = ২০%, ১/৮ = ১২.৫%।",
                    "কোনো দ্রব্যের মূল্য R% বৃদ্ধি পেলে খরচ অপরিবর্তিত রাখতে ব্যবহার কমাতে হবে = [R ÷ (100 + R)] × 100%।",
                    "মূল্য R% হ্রাস পেলে ব্যবহার বাড়াতে হবে = [R ÷ (100 - R)] × 100%।"
                ),
                shortcutFormulas = listOf(
                    StudyFormulaOrRule(
                        titleBn = "শর্টকাট সূত্র ১: মূল্য বৃদ্ধি ও ব্যবহার হ্রাস",
                        formulaOrRuleBn = "ব্যবহার হ্রাস% = [R / (100 + R)] × 100%",
                        solvedExampleBn = "উদাহরণ: চিনির দাম ২৫% বাড়লে ব্যবহার কমাতে হবে = (২৫ ÷ ১২৫) × ১০০% = ২০%।"
                    ),
                    StudyFormulaOrRule(
                        titleBn = "শর্টকাট সূত্র ২: পরপর দুবার শতকরা পরিবর্তন (Successive Change)",
                        formulaOrRuleBn = "মোট পরিবর্তন% = [X + Y + (XY ÷ 100)]%",
                        solvedExampleBn = "উদাহরণ: আয় প্রথমে ২০% বেড়ে পরে ১০% কমলে মোট পরিবর্তন = [২০ - ১০ - (২০০/১০০)]% = +৮% বৃদ্ধি।"
                    )
                ),
                examTipBn = "WBP পরীক্ষায় ২০% ও ২৫% দিয়ে প্রশ্ন সবচেয়ে বেশি আসে: ২০% বাড়লে ১৬⅔% কমে; ২৫% বাড়লে ২০% কমে!"
            )

            "MATH_CH_02", "MATH_CH_03" -> ChapterStudyNote(
                chapterId = chapter.chapterId,
                subject = "MATH",
                summaryBulletsBn = listOf(
                    "লাভ বা ক্ষতি সর্বদা ক্রয়মূল্যের (Cost Price - CP) ওপর হিসাব করা হয়, এবং ছাড় (Discount) সর্বদা ধার্যমূল্যের (Marked Price - MP) ওপর হিসাব হয়।",
                    "xটি দ্রব্যের ক্রয়মূল্য = yটি দ্রব্যের বিক্রয়মূল্য হলে লাভ/ক্ষতি% = [(x - y) ÷ y] × 100%।",
                    "পরপর x% ও y% দুটি ক্রমিক ছাড়ের সমতুল্য একক ছাড় = [x + y - (xy ÷ 100)]%।"
                ),
                shortcutFormulas = listOf(
                    StudyFormulaOrRule(
                        titleBn = "শর্টকাট সূত্র ১: ক্রয়মূল্য ও ধার্যমূল্যের সরাসরি অনুপাত",
                        formulaOrRuleBn = "CP : MP = (100 - Discount%) : (100 + Profit%)",
                        solvedExampleBn = "উদাহরণ: ১০% ছাড় দিয়েও ২০% লাভ করতে হলে CP : MP = (১০০ - ১০) : (১০০ + ২০) = ৯০ : ১২০ = ৩ : ৪।"
                    ),
                    StudyFormulaOrRule(
                        titleBn = "শর্টকাট সূত্র ২: সমতুল্য বাট্টা (Successive Discount)",
                        formulaOrRuleBn = "Equivalent Discount = [D₁ + D₂ - (D₁×D₂)/100]%",
                        solvedExampleBn = "উদাহরণ: ২০% ও ১০% ক্রমিক ছাড়ের সমতুল্য ছাড় = ২০ + ১০ - ২ = ২৮%।"
                    )
                ),
                examTipBn = "অসাধু ব্যবসায়ী ১ কেজির বদলে ৯০০ গ্রাম দিলে লাভ% = (১০০ ÷ ৯০০) × ১০০% = ১১.১১%!"
            )

            "MATH_CH_06", "MATH_CH_07" -> ChapterStudyNote(
                chapterId = chapter.chapterId,
                subject = "MATH",
                summaryBulletsBn = listOf(
                    "সরল সুদ (Simple Interest): I = (P × R × T) ÷ 100; আসল + সুদ = সুদ-আসল (Amount)।",
                    "কোনো মূলধন T বছরে n গুণ হলে সুদের হার R = [(n - 1) × 100] ÷ T %।",
                    "২ বছরের চক্রবৃদ্ধি সুদ (CI) ও সরল সুদের (SI) পার্থক্য = P × (R ÷ 100)²।"
                ),
                shortcutFormulas = listOf(
                    StudyFormulaOrRule(
                        titleBn = "শর্টকাট সূত্র ১: ২ বছরের CI ও SI-এর পার্থক্য",
                        formulaOrRuleBn = "পার্থক্য (D) = P × (R / 100)²",
                        solvedExampleBn = "উদাহরণ: ১০% হারে ৫০০০ টাকার ২ বছরের CI ও SI-এর পার্থক্য = ৫০০০ × (১০/১০০)² = ৫০ টাকা।"
                    ),
                    StudyFormulaOrRule(
                        titleBn = "শর্টকাট সূত্র ২: টাকা দ্বিগুণ বা তিনগুণ হওয়া",
                        formulaOrRuleBn = "R × T = (গুণ - 1) × 100",
                        solvedExampleBn = "উদাহরণ: কোনো টাকা ১০ বছরে দ্বিগুণ হলে সুদের হার = (২ - ১)×১০০ ÷ ১০ = ১০%।"
                    )
                ),
                examTipBn = "চক্রবৃদ্ধি সুদে কোনো টাকা ৪ বছরে দ্বিগুণ হলে ৮ গুণ (২³) হতে সময় লাগবে = ৪ × ৩ = ১২ বছর!"
            )

            "MATH_CH_10", "MATH_CH_11" -> ChapterStudyNote(
                chapterId = chapter.chapterId,
                subject = "MATH",
                summaryBulletsBn = listOf(
                    "গতিবেগ রূপান্তর: কিমি/ঘণ্টা থেকে মিটার/সেকেন্ড করতে (৫/১৮) দিয়ে গুণ; মিটার/সেকেন্ড থেকে কিমি/ঘণ্টা করতে (১৮/৫) দিয়ে গুণ।",
                    "ট্রেন যখন খুঁটি বা মানুষকে অতিক্রম করে তখন দূরত্ব = ট্রেনের নিজের দৈর্ঘ্য; সেতু/প্ল্যাটফর্ম অতিক্রম করলে দূরত্ব = (ট্রেন + সেতুর দৈর্ঘ্য)।",
                    "নৌকার বেগ = (অনুকূলে বেগ + প্রতিকূলে বেগ) ÷ ২; স্রোতের বেগ = (অনুকূলে বেগ - প্রতিকূলে বেগ) ÷ ২।"
                ),
                shortcutFormulas = listOf(
                    StudyFormulaOrRule(
                        titleBn = "শর্টকাট সূত্র ১: গড় গতিবেগ (যাতায়াতের দূরত্ব সমান হলে)",
                        formulaOrRuleBn = "Average Speed = (2 × X × Y) ÷ (X + Y)",
                        solvedExampleBn = "উদাহরণ: ৪০ কিমি/ঘণ্টা বেগে গিয়ে ৬০ কিমি/ঘণ্টা বেগে ফিরলে গড় বেগ = (২×৪০×৬০) ÷ ১০০ = ৪৮ কিমি/ঘণ্টা।"
                    ),
                    StudyFormulaOrRule(
                        titleBn = "শর্টকাট সূত্র ২: নৌকা ও স্রোতের বেগ",
                        formulaOrRuleBn = "নৌকা = (D + U)/2 | স্রোত = (D - U)/2",
                        solvedExampleBn = "উদাহরণ: অনুকূলে ১৪ কিমি/ঘণ্টা ও প্রতিকূলে ৬ কিমি/ঘণ্টা হলে নৌকার বেগ = (১৪+৬)/২ = ১০ কিমি/ঘণ্টা।"
                    )
                ),
                examTipBn = "১৮ কিমি/ঘণ্টা = ৫ মি/সেকেন্ড, ৩৬ কিমি/ঘণ্টা = ১০ মি/সেকেন্ড, ৫৪ কিমি/ঘণ্টা = ১৫ মি/সেকেন্ড, ৭২ কিমি/ঘণ্টা = ২০ মি/সেকেন্ড—মুখস্থ রাখলে ১০ সেকেন্ড বাঁচবে!"
            )

            "MATH_CH_12", "MATH_CH_13" -> ChapterStudyNote(
                chapterId = chapter.chapterId,
                subject = "MATH",
                summaryBulletsBn = listOf(
                    "A একা X দিনে এবং B একা Y দিনে কাজ করলে একত্রে সময় লাগবে = (X × Y) ÷ (X + Y) দিন।",
                    "ল.সা.গু (LCM) পদ্ধতিতে মোট কাজ ধরে নিলে ভগ্নাংশ ছাড়াই মুখে মুখে উত্তর বের হয়।",
                    "M₁ × D₁ × H₁ ÷ W₁ = M₂ × D₂ × H₂ ÷ W₂ (ঐকিক নিয়মের মাস্টার সূত্র)।"
                ),
                shortcutFormulas = listOf(
                    StudyFormulaOrRule(
                        titleBn = "শর্টকাট সূত্র ১: দুজনে একত্রে কাজ বা দুটি নল",
                        formulaOrRuleBn = "সম্মিলিত সময় = (X × Y) ÷ (X + Y)",
                        solvedExampleBn = "উদাহরণ: A ১০ দিনে ও B ১৫ দিনে করলে একত্রে = (১০ × ১৫) ÷ ২৫ = ৬ দিনে।"
                    ),
                    StudyFormulaOrRule(
                        titleBn = "শর্টকাট সূত্র ২: একটি নল ভরে ও অপরটি খালি করে",
                        formulaOrRuleBn = "পূর্ণ হতে সময় = (X × Y) ÷ (Y - X)",
                        solvedExampleBn = "উদাহরণ: ২০ মিনিটে ভরে ও ৩০ মিনিটে খালি হলে পূর্ণ হবে = (২০ × ৩০) ÷ ১০ = ৬০ মিনিটে।"
                    )
                ),
                examTipBn = "পুরুষ ও মহিলার অঙ্কে 'অথবা (OR)' থাকলে সমান ধরে কর্মদক্ষতার অনুপাত বের করুন!"
            )

            else -> ChapterStudyNote(
                chapterId = chapter.chapterId,
                subject = "MATH",
                summaryBulletsBn = listOf(
                    "অধ্যায়: ${chapter.titleBn} (${chapter.titleEn}) — মাধ্যমিক পাটিগণিত ও পরিমিতির গুরুত্বপূর্ণ সূত্রাবলী।",
                    "দুটি সংখ্যার গুণফল = ল.সা.গু (LCM) × গ.সা.গু (HCF); অনুপাত a:b এবং গ.সা.গু H হলে ল.সা.গু = H × a × b।",
                    "আয়তক্ষেত্রের ক্ষেত্রফল = দৈর্ঘ্য × প্রস্থ, পরিসীমা = ২(দৈর্ঘ্য + প্রস্থ); বৃত্তের ক্ষেত্রফল = πr², পরিধি = 2πr।"
                ),
                shortcutFormulas = listOf(
                    StudyFormulaOrRule(
                        titleBn = "শর্টকাট সূত্র ১: অনুপাত ও গ.সা.গু থেকে ল.সা.গু",
                        formulaOrRuleBn = "LCM = HCF × অনুপাতের পদদ্বয়ের গুণফল",
                        solvedExampleBn = "উদাহরণ: অনুপাত ৩:৪ এবং গ.সা.গু ৫ হলে ল.সা.গু = ৫ × ৩ × ৪ = ৬০।"
                    ),
                    StudyFormulaOrRule(
                        titleBn = "শর্টকাট সূত্র ২: প্রথম n সংখ্যক স্বাভাবিক সংখ্যার গড় ও যোগফল",
                        formulaOrRuleBn = "যোগফল = n(n+1)/2 | গড় = (n+1)/2",
                        solvedExampleBn = "উদাহরণ: ১ থেকে ৫০ পর্যন্ত সংখ্যার গড় = (৫০ + ১)/২ = ২৫.৫।"
                    )
                ),
                examTipBn = "প্রতিটি প্রশ্নে অপশন এলিমিনেশন ও ইউনিট ডিজিট (একক ঘরের অঙ্ক) ব্যবহার করে দ্রুত উত্তর মেলান!"
            )
        }
    }

    private fun buildReasoningChapterNote(chapter: WbpChapterInfo): ChapterStudyNote {
        return ChapterStudyNote(
            chapterId = chapter.chapterId,
            subject = "REASONING",
            summaryBulletsBn = listOf(
                "অধ্যায়: ${chapter.titleBn} (${chapter.titleEn}) — WBP/KP প্রিলি ও মেইন পরীক্ষায় ২৫ নম্বরের জি.আই-তে ফুল মার্কস তোলার কৌশল।",
                "ইংরেজি বর্ণমালার অবস্থান মনে রাখার কোড: EJOTY (E=5, J=10, O=15, T=20, Y=25) এবং CFILORUX (3-এর গুণিতক)।",
                "বিপরীত অক্ষর (Opposite Letters) এর যোগফল সর্বদা ২৭ হয় (যেমন A=1 ও Z=26; B=2 ও Y=25; C=3 ও X=24)।"
            ),
            shortcutFormulas = listOf(
                StudyFormulaOrRule(
                    titleBn = "শর্টকাট সূত্র ১: ক্রম ও র‍্যাঙ্কিং (Order & Ranking)",
                    formulaOrRuleBn = "মোট ব্যক্তি (Total) = (Left + Right) - 1",
                    solvedExampleBn = "উদাহরণ: ৪০ জনের সারিতে বাঁদিক থেকে ১৪তম হলে ডানদিক থেকে = (৪০ - ১৪) + ১ = ২৭তম।"
                ),
                StudyFormulaOrRule(
                    titleBn = "শর্টকাট সূত্র ২: ঘড়ির কোণ ও দর্পণ প্রতিবিম্ব",
                    formulaOrRuleBn = "কোণ θ = |30H - 5.5M| এবং দর্পণ সময় = 11:60 - প্রদত্ত সময়",
                    solvedExampleBn = "উদাহরণ: ৪টে ২০ মিনিটে কোণ = |৩০×৪ - ৫.৫×২০| = |১২০ - ১১০| = ১০°।"
                )
            ),
            examTipBn = "সিরিজ ও মিসিং নাম্বারের প্রশ্নে আগে পার্থক্য (Difference) দেখুন, আর বড় সংখ্যা থাকলে বর্গ (Square) বা ঘন (Cube) চেক করুন!"
        )
    }

    private fun buildEnglishChapterNote(chapter: WbpChapterInfo): ChapterStudyNote {
        return ChapterStudyNote(
            chapterId = chapter.chapterId,
            subject = "ENGLISH",
            summaryBulletsBn = listOf(
                "অধ্যায়: ${chapter.titleBn} (${chapter.titleEn}) — WBP ও KP মেইন এবং প্রিলি পরীক্ষার ১০ নম্বরের হাই-স্কোরিং গ্রামার ও ভোকাবুলারি।",
                "Senior, Junior, Superior, Inferior, Prior, Prefer—এই শব্দগুলির পর সর্বদা 'to' বসে (কখনই 'than' বসে না)।",
                "Voice Change-এ Tense কখনও পরিবর্তন হয় না (শুধু Active V2 থাকলে was/were + V3 হয়), কিন্তু Narration Change-এ Past Tense পরিবর্তিত হয়।"
            ),
            shortcutFormulas = listOf(
                StudyFormulaOrRule(
                    titleBn = "গোল্ডেন রুল ১: Subject-Verb Agreement",
                    formulaOrRuleBn = "'One of the + Plural Noun + Singular Verb' এবং 'Neither...nor / Either...or'-এ কাছের Subject অনুযায়ী Verb বসে।",
                    solvedExampleBn = "উদাহরণ: One of the boys IS absent (are নয়)।"
                ),
                StudyFormulaOrRule(
                    titleBn = "গোল্ডেন রুল ২: No sooner...than / Hardly...when",
                    formulaOrRuleBn = "No sooner had...than | Hardly/Scarcely had...when",
                    solvedExampleBn = "উদাহরণ: No sooner had the police arrived THAN the thief ran away."
                )
            ),
            examTipBn = "Fixed Prepositions (Die of, Abide by, Fond of, Accused of, Good at) থেকে প্রতি বছর ২–৩টি প্রশ্ন হুবহু কমন আসে!"
        )
    }

    private fun buildGkChapterNote(chapter: WbpChapterInfo): ChapterStudyNote {
        return ChapterStudyNote(
            chapterId = chapter.chapterId,
            subject = "GK",
            summaryBulletsBn = listOf(
                "অধ্যায়: ${chapter.titleBn} (${chapter.titleEn}) — ২০০৫ থেকে বর্তমান পর্যন্ত সমস্ত WBP ও KP পরীক্ষার সবচেয়ে গুরুত্বপূর্ণ জিকে পয়েন্ট।",
                "পশ্চিমবঙ্গের ভূগোল: সর্বোচ্চ শৃঙ্গ সান্দাকফু (৩৬৩৬ মিটার), কর্কটক্রান্তি রেখা গেছে ৫টি জেলার ওপর দিয়ে (পুরুলিয়া, বাঁকুড়া, পূর্ব ও পশ্চিম বর্ধমান, নদিয়া)।",
                "ভারতের সংবিধান ও ইতিহাস: মৌলিক অধিকার (ধারা ১২–৩৫, পার্ট III), মৌলিক কর্তব্য (ধারা ৫১A, পার্ট IVA), এবং জাতীয় আন্দোলনের প্রধান অধিবেশন।"
            ),
            shortcutFormulas = listOf(
                StudyFormulaOrRule(
                    titleBn = "স্মার্ট মেমরি ট্রিক ১: পশ্চিমবঙ্গের কর্কটক্রান্তি রেখার জেলা",
                    formulaOrRuleBn = "মনে রাখার কোড: 'নদিয়ার পূর্বে বাপুর বোন' (নদিয়া, পূর্ব বর্ধমান, পশ্চিম বর্ধমান, বাঁকুড়া, পুরুলিয়া)",
                    solvedExampleBn = "প্রশ্ন: কৃষ্ণনগর, দুর্গাপুর, আসানসোল ও আদ্রা শহরের ওপর দিয়ে কর্কটক্রান্তি রেখা গেছে।"
                ),
                StudyFormulaOrRule(
                    titleBn = "স্মার্ট মেমরি ট্রিক ২: মুঘল সম্রাটদের ক্রম",
                    formulaOrRuleBn = "কোড: BHAJSA (Babur ➔ Humayun ➔ Akbar ➔ Jahangir ➔ Shah Jahan ➔ Aurangzeb)",
                    solvedExampleBn = "পানিপথের ১ম যুদ্ধ (১৫২৬, বাবর), হলদিঘাটের যুদ্ধ (১৫৭৬, আকবর)।"
                )
            ),
            examTipBn = "পশ্চিমবঙ্গের সরকারি প্রকল্প (কন্যাশ্রী ২০১৩, সবুজ সাথী, স্বাস্থ্য সাথী, লক্ষ্মীর ভাণ্ডার) ও লোকনৃত্য থেকে ২–৩ নম্বর নিশ্চিত থাকে!"
        )
    }
}
