package cn.qfys521.deviceinfos

import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.telephony.SubscriptionInfo
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Locale

class Main : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        val scrollView = ScrollView(this)
        val linearLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        val textView = TextView(this)
        val button = Button(this).apply {
            text = "Action"
        }
        val sb = StringBuilder()

        val context = applicationContext
        val statFs = StatFs(Environment.getExternalStorageDirectory().path)
        val totalSize = statFs.totalBytes
        val availableSize = statFs.availableBytes

        sb.appendDeviceInfo()
        sb.appendMemoryInfo()
        sb.appendStorageInfo(totalSize, availableSize)
        sb.appendFileInfo(context)
        sb.appendCpuInfo()
        sb.appendLocalCode()
        textView.text = sb.toString()
        textView.movementMethod = ScrollingMovementMethod()
        linearLayout.addView(textView)
        linearLayout.addView(button)
        scrollView.addView(linearLayout)
        setContentView(scrollView)

        textView.setOnLongClickListener {
            showLongClickDialog(textView, sb)
            true
        }

        button.setOnClickListener {
            showLongClickDialog(textView, sb)
        }
    }

    private fun StringBuilder.appendDeviceInfo() {
        append("Device Name: ").append(deviceName).append("\n")
            .append("Model: ").append(modelName).append("\n")
            .append("OS Version: ").append(systemVersion).append("\n")
            .append("Brand: ").append(brand).append("\n")
            .append("Manufacturer: ").append(manufacturer).append("\n")
            .append("SDK Version: ").append(sDKVersion).append("\n")
            .append("System Language: ").append(systemLanguage).append("\n")
            .append("Supported ABIs: ").append(Build.SUPPORTED_ABIS.contentToString()).append("\n")
    }

    @SuppressLint("ServiceCast")
    private fun StringBuilder.appendMemoryInfo() {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val mmr = memoryInfo.totalMem / (1024 * 1024)
        append("Device Memory: ").append(mmr / 1024).append(".").append(mmr % 1024).append("GB").append("\n")
    }

    private fun StringBuilder.appendStorageInfo(totalSize: Long, availableSize: Long) {
        append("Total Storage: ").append(getUnit(totalSize.toDouble())).append("\n")
        append("Used Size: ").append(getUnit(availableSize.toDouble())).append("\n")
        append("Available Size: ").append(getUnit((totalSize - availableSize).toDouble())).append("\n")
    }

    private fun StringBuilder.appendFileInfo(context: Context) {
        append("File list in current directory: ").append(context.fileList().contentToString()).append("\n")
        append("Current file path: ").append(context.filesDir).append("\n")
    }

    private fun StringBuilder.appendCpuInfo() {
        try {
            val process = Runtime.getRuntime().exec("getprop ro.product.cpu.abilist64")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = reader.readLine()
            append("Supported Architectures (getprop): ").append(output).append("\n")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun StringBuilder.appendLocalCode(){
        val telephonyManager = getSystemService(TELEPHONY_SERVICE) as android.telephony.TelephonyManager

        val localDefaultCountryCode = Locale.getDefault().country
        val telephonyCountryCode = telephonyManager.networkCountryIso.uppercase()
        val contextCountryCode = resources.configuration.locales[0].country
        val phoneSimOperatorName = telephonyManager.simOperatorName
        val phoneSimOperator = telephonyManager.simOperator

        append("Local Default Country Code: ").append(localDefaultCountryCode).append("\n")
        append("Telephony Country Code: ").append(telephonyCountryCode).append("\n")
        append("Context Country Code: ").append(contextCountryCode).append("\n")
        append("SIM Operator Name: ").append(phoneSimOperatorName).append("\n")
        append("SIM Operator: ").append(phoneSimOperator).append("\n")
    }

    private fun showLongClickDialog(textView: TextView, sb: StringBuilder) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Choose Action")
            .setMessage("Expand or Cancel")
            .setIcon(R.mipmap.sym_def_app_icon)
            .setPositiveButton("Copy") { _, _ ->
                copyToClipboard(textView.text.toString())
            }
            .setNegativeButton("Cancel", null)
            .setNeutralButton("Expand") { _, _ ->
                showLanguageListDialog(textView, sb)
            }
            .create()
        alertDialog.show()
    }

    private fun showLanguageListDialog(textView: TextView, sb: StringBuilder) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Choose Action")
            .setMessage(systemLanguageList.contentToString())
            .setIcon(R.mipmap.sym_def_app_icon)
            .setPositiveButton("Add") { _, _ ->
                sb.append("Supported Languages: ").append(systemLanguageList.contentToString())
                textView.text = sb.toString()
            }
            .setNegativeButton("Collapse") { _, _ ->
                val str = sb.toString().replaceFirst("Supported Languages: ", "").replaceFirst(systemLanguageList.contentToString(), "")
                sb.clear().append(str)
                textView.text = sb.toString()
            }
            .create()
        alertDialog.show()
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("text", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Copied to clipboard.", Toast.LENGTH_SHORT).show()
    }

    private fun getUnit(size: Double): String {
        var size1 = size
        var index = 0
        while (size1 > 1024 && index < units.size - 1) {
            size1 /= 1024
            index++
        }
        return String.format(Locale.getDefault(), " %.2f %s", size1, units[index])
    }

    private val units = arrayOf("B", "KB", "MB", "GB", "TB")

    companion object {
        val deviceName: String get() = Build.DEVICE
        val modelName: String get() = Build.MODEL
        val systemVersion: String get() = Build.VERSION.RELEASE
        val brand: String get() = Build.BRAND
        val manufacturer: String get() = Build.MANUFACTURER
        val sDKVersion: String get() = Build.VERSION.SDK_INT.toString()
        val systemLanguage: String get() = Locale.getDefault().language
        val systemLanguageList: Array<Locale> get() = Locale.getAvailableLocales()
    }
}