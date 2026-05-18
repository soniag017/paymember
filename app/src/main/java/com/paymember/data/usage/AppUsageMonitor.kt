package com.paymember.data.usage

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import android.os.Process
import com.paymember.data.model.BillingPeriod
import com.paymember.data.model.SubscriptionEntity
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Locale

data class AppUsageInsight(
    val subscriptionId: Int,
    val serviceName: String,
    val packageName: String,
    val lastUsedDate: LocalDate?,
    val daysSinceLastUse: Long?,
    val monthlyCost: Double
)

class AppUsageMonitor(context: Context) {
    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences("pm_usage_monitor", Context.MODE_PRIVATE)

    fun isEnabled(): Boolean = prefs.getBoolean(KEY_ENABLED, false)

    fun setEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ENABLED, enabled).apply()
    }

    fun hasUsageAccess(): Boolean {
        val appOps = appContext.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                appContext.packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                appContext.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun findDormantSubscriptions(
        subscriptions: List<SubscriptionEntity>,
        staleDays: Long = 90,
        today: LocalDate = LocalDate.now()
    ): List<AppUsageInsight> {
        if (!isEnabled() || !hasUsageAccess()) return emptyList()

        val usageStatsManager = appContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val zone = ZoneId.systemDefault()
        val endMillis = System.currentTimeMillis()
        val startMillis = today.minusDays(180)
            .atStartOfDay(zone)
            .toInstant()
            .toEpochMilli()
        val lastUsedByPackage = usageStatsManager
            .queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startMillis, endMillis)
            .orEmpty()
            .groupBy { it.packageName }
            .mapValues { entry -> entry.value.maxOfOrNull { it.lastTimeUsed } ?: 0L }

        return subscriptions.mapNotNull { subscription ->
            val packages = packagesFor(subscription.serviceName)
            if (packages.isEmpty()) return@mapNotNull null

            val lastUsedMillis = packages
                .mapNotNull { packageName -> lastUsedByPackage[packageName] }
                .maxOrNull()
                ?.takeIf { it > 0L }
            val lastUsedDate = lastUsedMillis?.let {
                Instant.ofEpochMilli(it).atZone(zone).toLocalDate()
            }
            val daysSinceLastUse = lastUsedDate?.let { ChronoUnit.DAYS.between(it, today) }

            if (daysSinceLastUse != null && daysSinceLastUse < staleDays) return@mapNotNull null

            AppUsageInsight(
                subscriptionId = subscription.id,
                serviceName = subscription.serviceName,
                packageName = packages.first(),
                lastUsedDate = lastUsedDate,
                daysSinceLastUse = daysSinceLastUse,
                monthlyCost = subscription.monthlyEquivalent()
            )
        }.sortedWith(compareByDescending<AppUsageInsight> { it.daysSinceLastUse ?: Long.MAX_VALUE }
            .thenByDescending { it.monthlyCost })
    }

    private fun SubscriptionEntity.monthlyEquivalent(): Double {
        return if (period == BillingPeriod.MONTHLY) price else price / 12.0
    }

    private fun packagesFor(serviceName: String): List<String> {
        val normalized = serviceName.lowercase(Locale.ROOT)
        return packageMap.entries.firstOrNull { (keys, _) ->
            keys.any { key -> normalized.contains(key) }
        }?.value.orEmpty()
    }

    private companion object {
        private const val KEY_ENABLED = "usage_monitor_enabled"

        private val packageMap = mapOf(
            listOf("netflix") to listOf("com.netflix.mediaclient"),
            listOf("prime video", "amazon prime") to listOf("com.amazon.avod.thirdpartyclient"),
            listOf("max", "hbo") to listOf("com.wbd.stream", "com.hbo.hbonow"),
            listOf("disney") to listOf("com.disney.disneyplus"),
            listOf("filmin") to listOf("com.filmin.filmin"),
            listOf("skyshowtime") to listOf("com.skyshowtime.skyshowtime.google"),
            listOf("movistar") to listOf("es.plus.yomvi"),
            listOf("dazn") to listOf("com.dazn"),
            listOf("crunchyroll") to listOf("com.crunchyroll.crunchyroid"),
            listOf("spotify") to listOf("com.spotify.music"),
            listOf("apple music") to listOf("com.apple.android.music"),
            listOf("youtube music") to listOf("com.google.android.apps.youtube.music"),
            listOf("youtube premium", "youtube") to listOf("com.google.android.youtube"),
            listOf("amazon music") to listOf("com.amazon.mp3"),
            listOf("deezer") to listOf("deezer.android.app"),
            listOf("tidal") to listOf("com.aspiro.tidal"),
            listOf("audible") to listOf("com.audible.application"),
            listOf("uber") to listOf("com.ubercab"),
            listOf("glovo") to listOf("com.glovo"),
            listOf("just eat") to listOf("com.justeat.app.es"),
            listOf("google one") to listOf("com.google.android.apps.subscriptions.red"),
            listOf("dropbox") to listOf("com.dropbox.android"),
            listOf("microsoft 365") to listOf("com.microsoft.office.officehubrow", "com.microsoft.office.officehub"),
            listOf("notion") to listOf("notion.id"),
            listOf("canva") to listOf("com.canva.editor"),
            listOf("slack") to listOf("com.Slack"),
            listOf("figma") to listOf("com.figma.mirror"),
            listOf("todoist") to listOf("com.todoist"),
            listOf("duolingo") to listOf("com.duolingo"),
            listOf("coursera") to listOf("org.coursera.android"),
            listOf("skillshare") to listOf("com.skillshare.Skillshare"),
            listOf("kindle") to listOf("com.amazon.kindle"),
            listOf("strava") to listOf("com.strava"),
            listOf("calm") to listOf("com.calm.android"),
            listOf("headspace") to listOf("com.getsomeheadspace.android"),
            listOf("peloton") to listOf("com.onepeloton.callisto"),
            listOf("revolut") to listOf("com.revolut.revolut"),
            listOf("n26") to listOf("de.number26.android"),
            listOf("roblox") to listOf("com.roblox.client")
        )
    }
}
