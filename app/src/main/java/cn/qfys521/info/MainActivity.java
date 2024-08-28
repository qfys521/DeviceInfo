package cn.qfys521.info;

import android.app.*;
import android.widget.TextView;
import java.util.Locale;
import java.util.Arrays;
import android.widget.ScrollView;
import android.text.method.ScrollingMovementMethod;
import android.content.Context;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.view.WindowManager;
import android.view.View;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.widget.Toast;
import cn.qfys521.info.MainActivity;
import android.hardware.display.DisplayManager;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.os.*;

@SuppressWarnings("all")
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScrollView scrollView = new ScrollView(this);
        TextView tw = new TextView(this);
        StringBuilder sb = new StringBuilder();
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());

        long totalSize = statFs.getTotalBytes();
        long availableSize = statFs.getAvailableBytes();
        sb.append("设备名称: ").append(getDeviceName()).append("\n").append("设备型号: ").append(getModelName()).append("\n")
                .append("系统版本: ").append(getSystemVersion()).append("\n").append("厂商: ").append(getBrand()).append("\n")
                .append("制造商: ").append(getManufacturer()).append("\n").append("SDK版本: ").append(getSDKVersion())
                .append("\n").append("系统语言: ").append(getSystemLanguage()).append("\n").append("支持的ABI列表: ")
                .append(Arrays.toString(Build.SUPPORTED_ABIS)).append("\n");
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        long mmr = memoryInfo.totalMem / (1024 * 1024);

        sb.append("总存储: " + getUnit(totalSize)).append("\n");
        sb.append("已用大小: " + getUnit(availableSize)).append("\n");
        sb.append("可用大小: " + getUnit(totalSize - availableSize)).append("\n");
        // long totalMemory = runtime.totalMemory();
        sb.append("设备内存: " + (mmr / 1024) + "." + mmr % 1024 + "GB").append("\n");

        try {
            java.lang.Process process = Runtime.getRuntime().exec("getprop ro.product.cpu.abilist64");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            output.append(reader.readLine());
            sb.append("设备支持的架构(getprop): " + output.toString()).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        sb.append("支持的语言列表: ").append(Arrays.toString(getSystemLanguageList()));
        tw.setText(sb.toString());
        // tw.

        scrollView.setVerticalScrollBarEnabled(true);

        // 设置滚动方式
        tw.setMovementMethod(new ScrollingMovementMethod());
        setContentView(tw);
        tw.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", tw.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(v.getContext(), "已复制到剪切板。", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        /**
        Display display = this.getDisplay(); // API 30+
        Display.Mode mode = display.getMode();
        float[] refreshRates = mode.getAlternativeRefreshRates();
        boolean willbeSeamless = Arrays.asList(refreshRates).contains(newRefreshRate);

        // Set the frame rate even if the transition will not be seamless.
        surface.setFrameRate(newRefreshRate, FRAME_RATE_COMPATIBILITY_FIXED_SOURCE, CHANGE_FRAME_RATE_ALWAYS);
*/
    }

    /**
     * 设备名称
     *
     * @return 设备名称
     */
    public static String getDeviceName() {
        return android.os.Build.DEVICE;
    }

    /**
    * 设备型号
    *
    * @return 设备型号
    */
    public static String getModelName() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取厂商
     *
     * @return 厂商
     */
    public static String getBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取设备制造商
     *
     * @return 制造商
     */
    public static String getManufacturer() {
        return android.os.Build.MANUFACTURER;
    }

    /**
    * SDK 版本
    * @return
    */
    public static String getSDKVersion() {

        return android.os.Build.VERSION.SDK;
    }

    /**
    * 获取当前手机系统语言。
    *
    * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
    */
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return  语言列表
     */
    public static Locale[] getSystemLanguageList() {
        return Locale.getAvailableLocales();
    }

    private String getUnit(double size) {

        int index = 0;
        while (size > 1024 && index < 4) {

            size = size / 1024;
            index++;
        }
        return String.format(Locale.getDefault(), " %.2f %s", size, units[index]);
    }

    private String[] units = { "B", "KB", "MB", "GB", "TB" };
}