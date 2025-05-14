package cn.qfys521.deviceinfos

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Locale

class Main : Activity() {

    private lateinit var infoTextView: TextView
    private lateinit var actionButton: Button
    private val sb = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        collectDeviceInfo()
        setupListeners()
        setupDynamicShortcuts()
        addPinnedShortcut()
    }

    private fun initViews() {
        infoTextView = findViewById(R.id.text_view)
        actionButton = findViewById(R.id.action_button)
    }

    @SuppressLint("ServiceCast")
    private fun collectDeviceInfo() {
        // Storage info
        val statFs = StatFs(Environment.getExternalStorageDirectory().path)
        val totalSize = statFs.totalBytes
        val availableSize = statFs.availableBytes

        // Memory info
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        with(sb) {
            // Device Info
            append(getString(R.string.hdr_device_info))
            append(getString(R.string.device_name, Build.DEVICE))
            append(getString(R.string.device_model, Build.MODEL))
            append(getString(R.string.os_version, Build.VERSION.RELEASE))
            append(getString(R.string.brand, Build.BRAND))
            append(getString(R.string.manufacturer, Build.MANUFACTURER))
            append(getString(R.string.sdk_version, Build.VERSION.SDK_INT))
            append(getString(R.string.system_language, Locale.getDefault().language))
            append(getString(R.string.supported_abis, Build.SUPPORTED_ABIS.contentToString()))

            // Memory Info
            append(getString(R.string.hdr_memory_info))
            val totalMemMb = memoryInfo.totalMem / (1024 * 1024)
            val totalMemGb = totalMemMb / 1024.0
            append(getString(R.string.total_memory, "%.2f".format(totalMemGb)))

            // Storage Info
            append(getString(R.string.hdr_storage_info))
            append(getString(R.string.total_storage, getUnit(totalSize.toDouble())))
            append(getString(R.string.available_storage, getUnit(availableSize.toDouble())))
            append(getString(R.string.used_storage, getUnit((totalSize - availableSize).toDouble())))

            // File Info
            append(getString(R.string.hdr_file_info))
            append(getString(R.string.app_files, fileList().contentToString()))
            append(getString(R.string.files_path, filesDir.toString()))

            // CPU Info
            append(getString(R.string.hdr_cpu_info))
            try {
                val process = Runtime.getRuntime().exec("getprop ro.product.cpu.abilist64")
                BufferedReader(InputStreamReader(process.inputStream)).use {
                    val cpus = it.readLine() ?: "N/A"
                    append(getString(R.string.cpu_abis, cpus))
                }
            } catch (e: IOException) {
                append(getString(R.string.cpu_info_error, e.message ?: ""))
            }

            // Localization Info
            append(getString(R.string.hdr_localization_info))
            val telephonyManager = getSystemService(TELEPHONY_SERVICE) as android.telephony.TelephonyManager
            append(getString(
                R.string.sim_country_code,
                telephonyManager.networkCountryIso.uppercase(Locale.getDefault())
            ))
            append(getString(
                R.string.sim_operator,
                telephonyManager.simOperatorName,
                telephonyManager.simOperator
            ))
            append(getString(
                R.string.locale_country,
                resources.configuration.locales[0].country
            ))
        }

        infoTextView.text = sb.toString()
    }

    private fun setupListeners() {
        infoTextView.setOnLongClickListener {
            showActionDialog()
            true
        }

        actionButton.setOnClickListener {
            showActionDialog()
        }
    }

    private fun showActionDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_title)
            .setMessage(R.string.dialog_message)
            .setPositiveButton(R.string.copy) { _, _ ->
                copyToClipboard(infoTextView.text.toString())
            }
            .setNeutralButton(R.string.expand) { _, _ ->
                showLanguageListDialog()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showLanguageListDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.languages_title)
            .setItems(Locale.getAvailableLocales().map { it.displayName }.toTypedArray(), null)
            .setPositiveButton(R.string.add) { _, _ ->
                sb.append(getString(R.string.supported_languages_header))
                    .append(Locale.getAvailableLocales().joinToString { it.displayName })
                infoTextView.text = sb.toString()
            }
            .setNegativeButton(R.string.collapse) { _, _ ->
                try {
                    val header = getString(R.string.supported_languages_header).trim()
                    val start = sb.indexOf(header)
                    if (start >= 0) {
                        sb.delete(start, sb.length)
                        infoTextView.text = sb.toString()
                    }
                } catch (_: Exception) { }
            }
            .show()
    }

    private fun copyToClipboard(text: String) {
        (getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).apply {
            setPrimaryClip(ClipData.newPlainText("device_info", text))
            Toast.makeText(this@Main, R.string.copy_success, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupDynamicShortcuts() {
        val shortcutManager = getSystemService(ShortcutManager::class.java)
        val shortcut = ShortcutInfo.Builder(this, "device_info_shortcut")
            .setShortLabel(getString(R.string.shortcut_short))
            .setLongLabel(getString(R.string.shortcut_long))
            .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
            .setIntent(Intent(this, Main::class.java).apply {
                action = Intent.ACTION_VIEW
            })
            .build()
        shortcutManager.dynamicShortcuts = listOf(shortcut)
    }

    private fun addPinnedShortcut() {
        getSystemService(ShortcutManager::class.java)?.let { shortcutManager ->
            if (shortcutManager.isRequestPinShortcutSupported) {
                // 构造带 action 的 Intent
                val intent = Intent(this, Main::class.java).apply {
                    action = Intent.ACTION_MAIN
                    addCategory(Intent.CATEGORY_LAUNCHER)
                }

                val shortcut = ShortcutInfo.Builder(this, "pinned_shortcut")
                    .setShortLabel(getString(R.string.pinned_shortcut))
                    .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
                    .setIntent(intent)
                    .build()

                shortcutManager.requestPinShortcut(shortcut, null)
            }
        }
    }


    private fun getUnit(size: Double): String {
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        var calculatedSize = size
        var unitIndex = 0

        while (calculatedSize > 1024 && unitIndex < units.lastIndex) {
            calculatedSize /= 1024
            unitIndex++
        }
        return "%.2f %s".format(Locale.getDefault(), calculatedSize, units[unitIndex])
    }
}
