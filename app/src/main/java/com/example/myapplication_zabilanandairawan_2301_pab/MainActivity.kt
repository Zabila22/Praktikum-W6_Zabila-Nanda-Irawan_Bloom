package com.example.myapplication_zabilanandairawan_2301_pab

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar
import java.util.Locale

// BLOOM COLOR PALETTE
private val Pink = Color(0xFFE8A6BC)
private val SoftPink = Color(0xFFFFEDF3)
private val Cream = Color(0xFFFFFAF5)
private val Lavender = Color(0xFFF0EAFB)
private val Purple = Color(0xFF9278B5)
private val DarkText = Color(0xFF514453)
private val MutedText = Color(0xFF958797)
private val SoftGreen = Color(0xFFE6F3E9)
private val BorderPink = Color(0xFFF5DCE5)

private val moodOptions = listOf(
    "😄" to "Bahagia",
    "😌" to "Tenang",
    "😐" to "Biasa",
    "😔" to "Sedih",
    "😫" to "Lelah"
)

private val affirmationOptions = listOf(
    "Aku berhak beristirahat tanpa merasa bersalah.",
    "Aku sudah berusaha sebaik yang aku bisa hari ini.",
    "Tidak apa-apa untuk berjalan perlahan.",
    "Aku pantas mendapatkan kebahagiaan.",
    "Sedikit kemajuan tetaplah sebuah kemajuan."
)

private val activityOptions = listOf(
    "Mendengarkan lagu favorit 🎧",
    "Menikmati udara segar 🌿",
    "Membaca buku kesukaan 📚",
    "Menulis hal yang disyukuri ✍️",
    "Beristirahat sejenak ☁️"
)

private val habitOptions = listOf(
    "Minum air putih",
    "Merapikan tempat tidur",
    "Bergerak atau olahraga ringan",
    "Membaca buku",
    "Menulis jurnal",
    "Tidur tepat waktu"
)

data class MoodEntry(
    val emoji: String,
    val mood: String,
    val note: String,
    val date: String
)

// LOCAL STORAGE
private fun prefs(context: Context) =
    context.getSharedPreferences("bloom_local_data", Context.MODE_PRIVATE)

private fun todayKey(): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

private fun todayLabel(): String =
    SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(Date())

private fun currentTime(): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

private fun loadMoods(context: Context): List<MoodEntry> {
    val raw = prefs(context).getString("mood_history", "[]") ?: "[]"

    return try {
        val array = JSONArray(raw)
        (0 until array.length()).map { index ->
            val obj = array.getJSONObject(index)
            MoodEntry(
                emoji = obj.optString("emoji"),
                mood = obj.optString("mood"),
                note = obj.optString("note"),
                date = obj.optString("date")
            )
        }
    } catch (_: Exception) {
        emptyList()
    }
}

private fun saveMood(context: Context, entry: MoodEntry) {
    val array = JSONArray()

    loadMoods(context).forEach { old ->
        array.put(
            JSONObject().apply {
                put("emoji", old.emoji)
                put("mood", old.mood)
                put("note", old.note)
                put("date", old.date)
            }
        )
    }

    array.put(
        JSONObject().apply {
            put("emoji", entry.emoji)
            put("mood", entry.mood)
            put("note", entry.note)
            put("date", entry.date)
        }
    )

    prefs(context).edit()
        .putString("mood_history", array.toString())
        .apply()
}

private fun loadFavorites(context: Context): Set<String> {
    val raw = prefs(context).getString("favorites", null)
        ?: return emptySet()

    return try {
        val array = JSONArray(raw)
        (0 until array.length()).map { array.getString(it) }.toSet()
    } catch (_: Exception) {
        emptySet()
    }
}

private fun saveFavorites(context: Context, favorites: Set<String>) {
    val array = JSONArray()
    favorites.forEach { array.put(it) }

    prefs(context).edit()
        .putString("favorites", array.toString())
        .apply()
}

private fun loadHabits(context: Context): Set<String> {
    val raw = prefs(context)
        .getString("habits_${todayKey()}", null) ?: return emptySet()

    return try {
        val array = JSONArray(raw)
        (0 until array.length()).map { array.getString(it) }.toSet()
    } catch (_: Exception) {
        emptySet()
    }
}

private fun saveHabits(context: Context, habits: Set<String>) {
    val array = JSONArray()
    habits.forEach { array.put(it) }

    prefs(context).edit()
        .putString("habits_${todayKey()}", array.toString())
        .apply()
}

private fun loadHabitsForDate(context: Context, dateKey: String): Set<String> {
    val raw = prefs(context).getString("habits_$dateKey", "[]") ?: "[]"
    return try {
        val array = JSONArray(raw)
        (0 until array.length()).map { array.getString(it) }.toSet()
    } catch (_: Exception) {
        emptySet()
    }
}

private data class HabitDay(
    val key: String,
    val label: String,
    val completed: Int
)

private fun lastSevenDays(context: Context): List<HabitDay> {
    val calendar = java.util.Calendar.getInstance()
    calendar.firstDayOfWeek = java.util.Calendar.MONDAY
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val labelFormatter = SimpleDateFormat("EEE, dd MMM", Locale("id", "ID"))
    val days = mutableListOf<HabitDay>()
    calendar.add(java.util.Calendar.DAY_OF_YEAR, -6)
    repeat(7) {
        val date = calendar.time
        val key = formatter.format(date)
        days.add(HabitDay(key, labelFormatter.format(date), loadHabitsForDate(context, key).size))
        calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
    }
    return days
}

private fun habitStreak(context: Context, habit: String): Int {
    val calendar = java.util.Calendar.getInstance()
    var streak = 0
    // A streak starts today if the habit is done today; otherwise check from yesterday.
    if (habit !in loadHabitsForDate(context, todayKey())) {
        calendar.add(java.util.Calendar.DAY_OF_YEAR, -1)
    }
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    repeat(366) {
        val key = formatter.format(calendar.time)
        if (habit in loadHabitsForDate(context, key)) {
            streak++
            calendar.add(java.util.Calendar.DAY_OF_YEAR, -1)
        } else {
            return streak
        }
    }
    return streak
}

// MAIN ACTIVITY
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = android.graphics.Color.rgb(255, 250, 245)
        window.navigationBarColor = android.graphics.Color.WHITE

        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Purple,
                    secondary = Pink,
                    background = Cream,
                    surface = Color.White,
                    onBackground = DarkText,
                    onSurface = DarkText
                )
            ) {
                BloomApp(applicationContext)
            }
        }
    }
}

@Composable
fun BloomApp(context: Context) {
    var selectedTab by remember { mutableIntStateOf(0) }

    var userName by remember {
        mutableStateOf(
            prefs(context).getString("user_name", "Sahabat Bloom")
                ?: "Sahabat Bloom"
        )
    }

    var moods by remember { mutableStateOf(loadMoods(context)) }
    var favorites by remember { mutableStateOf(loadFavorites(context)) }
    var habits by remember { mutableStateOf(loadHabits(context)) }

    val tabLabels = listOf("Home", "Mood", "Favorites", "Profile")

    Scaffold(
        containerColor = Cream,
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(78.dp),
                containerColor = Color.White,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars
            ) {
                tabLabels.forEachIndexed { index, label ->
                    val icon = when (index) {
                        0 -> Icons.Default.Home
                        1 -> Icons.Default.SelfImprovement
                        2 -> Icons.Default.Favorite
                        else -> Icons.Default.Person
                    }

                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                icon,
                                contentDescription = label,
                                modifier = Modifier.size(23.dp)
                            )
                        },
                        label = {
                            Text(label, fontSize = 10.sp)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Purple,
                            selectedTextColor = Purple,
                            unselectedIconColor = MutedText,
                            unselectedTextColor = MutedText,
                            indicatorColor = Lavender
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Cream)
                .padding(padding)
        ) {
            when (selectedTab) {
                0 -> HomeScreen(
                    context = context,
                    userName = userName,
                    moods = moods,
                    habits = habits,
                    onMoodClick = { selectedTab = 1 },
                    onHabitChange = { habit, checked ->
                        habits = if (checked) habits + habit else habits - habit
                        saveHabits(context, habits)
                    }
                )

                1 -> MoodScreen(
                    moods = moods,
                    onSave = { entry ->
                        saveMood(context, entry)
                        moods = loadMoods(context)
                    }
                )

                2 -> FavoritesScreen(
                    favorites = favorites,
                    onFavoriteChange = { item ->
                        favorites = if (item in favorites) {
                            favorites - item
                        } else {
                            favorites + item
                        }
                        saveFavorites(context, favorites)
                    }
                )

                3 -> ProfileScreen(
                    userName = userName,
                    totalMoods = moods.size,
                    onSaveName = { newName ->
                        userName = newName
                        prefs(context).edit()
                            .putString("user_name", newName)
                            .apply()
                    }
                )
            }
        }
    }
}

// REUSABLE UI COMPONENTS
@Composable
fun BloomHeader(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SoftPink)
            .padding(horizontal = 22.dp, vertical = 25.dp)
    ) {
        Text(
            "b l o o m  🌷",
            color = Purple,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(12.dp))

        Text(
            title,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            lineHeight = 33.sp
        )

        Spacer(Modifier.height(6.dp))

        Text(
            subtitle,
            fontSize = 13.sp,
            color = MutedText,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun BloomCard(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            content = content
        )
    }
}

@Composable
fun SectionTitle(title: String, subtitle: String? = null) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            title,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText
        )
        if (subtitle != null) {
            Text(subtitle, fontSize = 12.sp, color = MutedText)
        }
    }
}

@Composable
fun SoftButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Purple)
    ) {
        Text(text, fontWeight = FontWeight.SemiBold)
    }
}

// HOME SCREEN
@Composable
fun HomeScreen(
    context: Context,
    userName: String,
    moods: List<MoodEntry>,
    habits: Set<String>,
    onMoodClick: () -> Unit,
    onHabitChange: (String, Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            BloomHeader(
                "Hello, $userName ♡",
                "Semoga hari ini membawa hal-hal baik untukmu."
            )
        }

        item {
            Column(
                modifier = Modifier.padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                BloomCard(color = Lavender) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "A little reminder",
                                color = Purple,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "You are growing\nat your own pace.",
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 28.sp,
                                color = DarkText
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Tidak perlu terburu-buru. Kamu sudah melakukan yang terbaik.",
                                fontSize = 12.sp,
                                color = MutedText,
                                lineHeight = 19.sp
                            )
                        }
                        Text("🌷", fontSize = 44.sp)
                    }
                }

                BloomCard(color = Color.White) {
                    Text(
                        "A small check-in 💗",
                        fontSize = 12.sp,
                        color = Purple,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Bagaimana perasaanmu hari ini?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(5.dp))
                    Text(
                        "Luangkan waktu sejenak untuk dirimu.",
                        fontSize = 12.sp,
                        color = MutedText
                    )
                    Spacer(Modifier.height(14.dp))
                    SoftButton(
                        "Catat mood sekarang",
                        onClick = onMoodClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionTitle(
                        "Little habits 🌱",
                        "Langkah kecil setiap hari"
                    )
                    Text(
                        "${(habits.size * 100 / habitOptions.size)}%",
                        color = Purple,
                        fontWeight = FontWeight.Bold
                    )
                }

                LinearProgressIndicator(
                    progress = {
                        habits.size.toFloat() / habitOptions.size
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(CircleShape),
                    color = Purple,
                    trackColor = BorderPink
                )

                // WEEKLY PROGRESS
                val weekDays = lastSevenDays(context)
                val weeklyPossible = habitOptions.size * 7
                val weeklyCompleted = weekDays.sumOf { it.completed }
                val weeklyPercent = if (weeklyPossible == 0) 0 else
                    (weeklyCompleted * 100 / weeklyPossible)

                BloomCard(color = Lavender) {
                    SectionTitle(
                        "Progres mingguan 🌷",
                        "Pantau kebiasaanmu selama 7 hari terakhir."
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "$weeklyPercent%",
                            fontSize = 27.sp,
                            fontWeight = FontWeight.Bold,
                            color = Purple
                        )
                        Text(
                            "$weeklyCompleted dari $weeklyPossible checklist",
                            fontSize = 11.sp,
                            color = MutedText
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { weeklyPercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(CircleShape),
                        color = Purple,
                        trackColor = BorderPink
                    )
                    Spacer(Modifier.height(16.dp))
                    weekDays.forEach { day ->
                        val percent = if (habitOptions.isEmpty()) 0f
                        else day.completed.toFloat() / habitOptions.size
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                day.label,
                                modifier = Modifier.width(100.dp),
                                fontSize = 11.sp,
                                color = DarkText
                            )
                            LinearProgressIndicator(
                                progress = { percent },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .clip(CircleShape),
                                color = Pink,
                                trackColor = Color.White
                            )
                            Text(
                                "${day.completed}/${habitOptions.size}",
                                modifier = Modifier.width(38.dp),
                                textAlign = TextAlign.End,
                                fontSize = 10.sp,
                                color = MutedText
                            )
                        }
                    }
                }

                // HABIT HISTORY
                BloomCard(color = SoftPink) {
                    SectionTitle(
                        "Riwayat kebiasaan 📖",
                        "Checklist yang tersimpan untuk 7 hari terakhir."
                    )
                    Spacer(Modifier.height(10.dp))
                    weekDays.asReversed().forEach { day ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    day.label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    if (day.completed == habitOptions.size)
                                        "Semua kebiasaan selesai 💗"
                                    else
                                        "${day.completed} dari ${habitOptions.size} kebiasaan selesai",
                                    fontSize = 11.sp,
                                    color = MutedText
                                )
                            }
                            Text(
                                "${if (habitOptions.isEmpty()) 0 else day.completed * 100 / habitOptions.size}%",
                                color = Purple,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                habitOptions.forEach { habit ->
                    HabitRow(
                        habit = habit,
                        checked = habit in habits,
                        streak = habitStreak(context, habit),
                        onCheckedChange = { onHabitChange(habit, it) }
                    )
                }

                SectionTitle("Your latest mood ☁️")

                if (moods.isEmpty()) {
                    BloomCard(color = SoftPink) {
                        Text("🌸", fontSize = 30.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Belum ada catatan mood",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Catatan pertamamu akan muncul di sini.",
                            fontSize = 12.sp,
                            color = MutedText
                        )
                    }
                } else {
                    val latest = moods.last()
                    BloomCard(color = SoftPink) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(latest.emoji, fontSize = 36.sp)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    latest.mood,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                                Text(
                                    latest.date,
                                    fontSize = 11.sp,
                                    color = MutedText
                                )
                            }
                        }
                        if (latest.note.isNotBlank()) {
                            Spacer(Modifier.height(10.dp))
                            Text(latest.note, lineHeight = 21.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HabitRow(
    habit: String,
    checked: Boolean,
    streak: Int,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (checked) SoftGreen else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = Purple)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    habit,
                    color = DarkText,
                    fontSize = 13.sp
                )
                Text(
                    "🔥 $streak hari berturut-turut",
                    color = MutedText,
                    fontSize = 10.sp
                )
            }
            if (checked) {
                Text("✓", color = Purple, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(6.dp))
            }
        }
    }
}

// MOOD SCREEN
@Composable
fun MoodScreen(
    moods: List<MoodEntry>,
    onSave: (MoodEntry) -> Unit
) {
    var selectedMood by remember {
        mutableStateOf<Pair<String, String>?>(null)
    }
    var note by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            BloomHeader(
                "Mood Journal 🌸",
                "Berikan ruang untuk semua perasaanmu."
            )
        }

        item {
            Column(
                modifier = Modifier.padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                BloomCard {
                    SectionTitle(
                        "How are you feeling?",
                        "Pilih yang paling menggambarkan perasaanmu."
                    )
                    Spacer(Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        moodOptions.forEach { option ->
                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (selectedMood == option) Lavender
                                        else Cream
                                    )
                                    .clickable {
                                        selectedMood = option
                                        message = ""
                                    }
                                    .padding(horizontal = 5.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(option.first, fontSize = 27.sp)
                                Spacer(Modifier.height(5.dp))
                                Text(
                                    option.second,
                                    fontSize = 9.sp,
                                    color = DarkText
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(22.dp))
                    Text(
                        "A little note about today",
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = note,
                        onValueChange = {
                            note = it
                            message = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Tuliskan apa yang kamu rasakan...")
                        },
                        minLines = 3,
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Purple,
                            unfocusedBorderColor = BorderPink,
                            focusedContainerColor = Cream,
                            unfocusedContainerColor = Cream
                        )
                    )

                    Spacer(Modifier.height(14.dp))
                    SoftButton(
                        "Simpan mood ♡",
                        onClick = {
                            val choice = selectedMood
                            if (choice == null) {
                                message = "Pilih mood terlebih dahulu, ya."
                            } else {
                                onSave(
                                    MoodEntry(
                                        emoji = choice.first,
                                        mood = choice.second,
                                        note = note.trim(),
                                        date = "${todayLabel()} • ${currentTime()}"
                                    )
                                )
                                selectedMood = null
                                note = ""
                                message = "Mood berhasil disimpan 💗"
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (message.isNotBlank()) {
                        Spacer(Modifier.height(10.dp))
                        Text(message, color = Purple, fontSize = 12.sp)
                    }
                }
                // STAGE 3: MOOD CALENDAR
                var calendarMonth by remember {
                    mutableIntStateOf(Calendar.getInstance().get(Calendar.MONTH))
                }
                var calendarYear by remember {
                    mutableIntStateOf(Calendar.getInstance().get(Calendar.YEAR))
                }
                val monthCalendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, calendarYear)
                    set(Calendar.MONTH, calendarMonth)
                    set(Calendar.DAY_OF_MONTH, 1)
                }
                val daysInMonth = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                val firstDayOffset = (monthCalendar.get(Calendar.DAY_OF_WEEK) + 5) % 7
                val moodByDate = moods.groupBy { it.date.substringBefore(" • ") }
                val monthTitle = SimpleDateFormat(
                    "MMMM yyyy",
                    Locale("id", "ID")
                ).format(monthCalendar.time)

                BloomCard(color = Lavender) {
                    SectionTitle(
                        "Kalender mood 🗓️",
                        "Lihat hari-hari ketika kamu mencatat perasaanmu."
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "‹",
                            modifier = Modifier
                                .clickable {
                                    if (calendarMonth == Calendar.JANUARY) {
                                        calendarMonth = Calendar.DECEMBER
                                        calendarYear--
                                    } else {
                                        calendarMonth--
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            fontSize = 26.sp,
                            color = Purple
                        )
                        Text(
                            monthTitle.replaceFirstChar { it.uppercase() },
                            fontWeight = FontWeight.Bold,
                            color = DarkText
                        )
                        Text(
                            text = "›",
                            modifier = Modifier
                                .clickable {
                                    if (calendarMonth == Calendar.DECEMBER) {
                                        calendarMonth = Calendar.JANUARY
                                        calendarYear++
                                    } else {
                                        calendarMonth++
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            fontSize = 26.sp,
                            color = Purple
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                    val weekdays = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")

                    Row(modifier = Modifier.fillMaxWidth()) {
                        weekdays.forEach { day ->
                            Text(
                                day,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp,
                                color = MutedText
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    val cellCount = ((firstDayOffset + daysInMonth + 6) / 7) * 7

                    for (weekStart in 0 until cellCount step 7) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            for (column in 0..6) {
                                val dayNumber = weekStart + column - firstDayOffset + 1
                                val validDay = dayNumber in 1..daysInMonth

                                val dateLabel = if (validDay) {
                                    SimpleDateFormat(
                                        "dd MMM yyyy",
                                        Locale("id", "ID")
                                    ).format(
                                        Calendar.getInstance().apply {
                                            set(Calendar.YEAR, calendarYear)
                                            set(Calendar.MONTH, calendarMonth)
                                            set(Calendar.DAY_OF_MONTH, dayNumber)
                                        }.time
                                    )
                                } else {
                                    ""
                                }

                                val dayMoods = moodByDate[dateLabel].orEmpty()

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(2.dp)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (validDay && dayMoods.isNotEmpty()) {
                                                SoftPink
                                            } else {
                                                Color.White
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (validDay) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                "$dayNumber",
                                                fontSize = 11.sp,
                                                color = DarkText
                                            )
                                            if (dayMoods.isNotEmpty()) {
                                                Text(
                                                    dayMoods.last().emoji,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Warna pink menandakan hari dengan catatan mood.",
                        fontSize = 10.sp,
                        color = MutedText
                    )
                }

                // STAGE 4: WEEKLY MOOD CHART
                val chartDays = (6 downTo 0).map { offset ->
                    val dayCalendar = Calendar.getInstance().apply {
                        add(Calendar.DAY_OF_YEAR, -offset)
                    }
                    val key = SimpleDateFormat(
                        "dd MMM yyyy",
                        Locale("id", "ID")
                    ).format(dayCalendar.time)

                    Triple(
                        SimpleDateFormat("EEE", Locale("id", "ID"))
                            .format(dayCalendar.time),
                        key,
                        moodByDate[key].orEmpty()
                    )
                }

                val maxMoodCount = (chartDays.maxOfOrNull { it.third.size } ?: 0)
                    .coerceAtLeast(1)

                BloomCard(color = SoftPink) {
                    SectionTitle(
                        "Grafik mood mingguan 📊",
                        "Jumlah catatan mood yang kamu simpan setiap hari."
                    )
                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        chartDays.forEach { day ->
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Text(
                                    "${day.third.size}",
                                    fontSize = 10.sp,
                                    color = DarkText
                                )
                                Spacer(Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(20.dp)
                                        .height(
                                            (12 + (day.third.size * 70 / maxMoodCount)).dp
                                        )
                                        .clip(
                                            RoundedCornerShape(
                                                topStart = 7.dp,
                                                topEnd = 7.dp
                                            )
                                        )
                                        .background(
                                            if (day.third.isNotEmpty()) Purple
                                            else BorderPink
                                        )
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    day.first,
                                    fontSize = 9.sp,
                                    color = MutedText
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Total ${chartDays.sumOf { it.third.size }} catatan mood dalam 7 hari terakhir.",
                        fontSize = 11.sp,
                        color = MutedText
                    )
                }

                SectionTitle(
                    "Mood memories 📖",
                    "Lihat kembali perjalanan perasaanmu."
                )

                if (moods.isEmpty()) {
                    BloomCard(color = Lavender) {
                        Text("☁️", fontSize = 30.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("Belum ada kenangan mood.")
                        Text(
                            "Catatanmu akan tersimpan di sini.",
                            color = MutedText,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        items(moods.reversed()) { entry ->
            BloomCard(
                modifier = Modifier.padding(horizontal = 18.dp),
                color = Color.White
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(SoftPink, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(entry.emoji, fontSize = 29.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            entry.mood,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            entry.date,
                            fontSize = 11.sp,
                            color = MutedText
                        )
                    }
                }
                if (entry.note.isNotBlank()) {
                    Spacer(Modifier.height(12.dp))
                    Text(entry.note, lineHeight = 21.sp)
                }
            }
        }
    }
}
// FAVORITES SCREEN
@Composable
fun FavoritesScreen(
    favorites: Set<String>,
    onFavoriteChange: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            BloomHeader(
                "Little Favorites 💗",
                "Simpan hal-hal kecil yang membuatmu bahagia."
            )
        }

        item {
            Column(
                modifier = Modifier.padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                BloomCard(color = Lavender) {
                    Text("Your comfort corner ☁️", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(5.dp))
                    Text(
                        "Kamu bisa kembali ke sini kapan pun membutuhkan pengingat yang baik.",
                        fontSize = 12.sp,
                        color = MutedText,
                        lineHeight = 19.sp
                    )
                }
                SectionTitle("Daily affirmations ✨")
            }
        }

        items(affirmationOptions) { affirmation ->
            BloomCard(
                modifier = Modifier.padding(horizontal = 18.dp),
                color = if (affirmation in favorites) SoftPink else Color.White
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "“",
                        fontSize = 34.sp,
                        color = Pink,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        affirmation,
                        modifier = Modifier.weight(1f),
                        lineHeight = 21.sp,
                        fontSize = 13.sp
                    )
                    IconButton(
                        onClick = { onFavoriteChange(affirmation) }
                    ) {
                        Icon(
                            if (affirmation in favorites) Icons.Default.Favorite
                            else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorit",
                            tint = Pink
                        )
                    }
                }
            }
        }

        item {
            Text(
                "Self-care activities 🌿",
                modifier = Modifier.padding(horizontal = 18.dp),
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
        }

        items(activityOptions) { activity ->
            BloomCard(
                modifier = Modifier.padding(horizontal = 18.dp),
                color = if (activity in favorites) Lavender else Color.White
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        activity,
                        modifier = Modifier.weight(1f),
                        fontSize = 13.sp
                    )
                    IconButton(
                        onClick = { onFavoriteChange(activity) }
                    ) {
                        Icon(
                            if (activity in favorites) Icons.Default.Favorite
                            else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorit",
                            tint = Purple
                        )
                    }
                }
            }
        }

        item {
            BloomCard(
                modifier = Modifier.padding(horizontal = 18.dp),
                color = SoftGreen
            ) {
                Text("♡ ${favorites.size} item tersimpan")
                Text(
                    "Simpan afirmasi atau aktivitas yang kamu sukai.",
                    fontSize = 12.sp,
                    color = MutedText
                )
            }
        }
    }
}

// PROFILE SCREEN
@Composable
fun ProfileScreen(
    userName: String,
    totalMoods: Int,
    onSaveName: (String) -> Unit
) {
    var nameInput by remember(userName) {
        mutableStateOf(userName)
    }
    var message by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            BloomHeader(
                "My Little Space 🌷",
                "Kenali dirimu dan hargai setiap langkahmu."
            )
        }

        item {
            Column(
                modifier = Modifier.padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                BloomCard(color = Lavender) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .background(SoftPink, CircleShape)
                            .align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🌸", fontSize = 48.sp)
                    }

                    Spacer(Modifier.height(15.dp))
                    Text(
                        userName,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(5.dp))
                    Text(
                        "Growing at your own pace ✨",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = MutedText,
                        fontSize = 12.sp
                    )
                }

                BloomCard {
                    SectionTitle(
                        "About you ♡",
                        "Nama panggilan yang ingin kamu gunakan."
                    )
                    Spacer(Modifier.height(14.dp))

                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = {
                            nameInput = it
                            message = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nama panggilan") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Purple,
                            unfocusedBorderColor = BorderPink
                        )
                    )

                    Spacer(Modifier.height(12.dp))
                    SoftButton(
                        "Simpan profil",
                        onClick = {
                            if (nameInput.isNotBlank()) {
                                onSaveName(nameInput.trim())
                                message = "Nama berhasil disimpan 💗"
                            } else {
                                message = "Nama tidak boleh kosong."
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (message.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(message, color = Purple, fontSize = 12.sp)
                    }
                }

                BloomCard(color = SoftPink) {
                    SectionTitle("Your Bloom journey 🌱")
                    Spacer(Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White, RoundedCornerShape(18.dp))
                                .padding(14.dp)
                        ) {
                            Text("🌸", fontSize = 25.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "$totalMoods",
                                fontSize = 23.sp,
                                fontWeight = FontWeight.Bold,
                                color = Purple
                            )
                            Text("Mood dicatat", fontSize = 11.sp, color = MutedText)
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White, RoundedCornerShape(18.dp))
                                .padding(14.dp)
                        ) {
                            Text("💗", fontSize = 25.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Bloom",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = Purple
                            )
                            Text("Personal space", fontSize = 11.sp, color = MutedText)
                        }
                    }
                }

                BloomCard {
                    Text("A gentle reminder 💌", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Kamu tidak perlu membandingkan perjalananmu dengan orang lain. " +
                                "Tumbuhlah sesuai waktumu, satu hari demi satu hari.",
                        color = MutedText,
                        lineHeight = 22.sp,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}