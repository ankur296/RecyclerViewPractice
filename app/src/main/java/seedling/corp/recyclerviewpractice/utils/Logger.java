package seedling.corp.recyclerviewpractice.utils;

import android.util.Log;

public final class Logger {
    private static final String TAG = "recyclerviewpractice";
    public static final boolean LOG_ENABLE = true;
    public static final boolean TEMP_CODE = true;


    private Logger() {
    }

    private static String buildMsg(String msg) {
        StringBuilder buffer = new StringBuilder();

            final StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];

            buffer.append("[ ");
            buffer.append(Thread.currentThread().getName());
            buffer.append(": ");
            buffer.append(stackTraceElement.getFileName());
            buffer.append(": ");
            buffer.append(stackTraceElement.getLineNumber());
            buffer.append(": ");
            buffer.append(stackTraceElement.getMethodName());

        buffer.append("() ] _____ ");

        buffer.append(msg);

        return buffer.toString();
    }


//    public static void v(String msg) {
//        if (LOG_ENABLE)
//            Log.v(TAG, buildMsg(msg));
//    }
//
//
//    public static void d(String msg) {
//        if (LOG_ENABLE)
//            Log.d(TAG, buildMsg(msg));
//    }
//
//
//    public static void i(String msg) {
//        if (LOG_ENABLE && Log.isLoggable(TAG, Log.INFO)) {
//            Log.i(TAG, buildMsg(msg));
//        }
//    }
//
//
//    public static void w(String msg) {
//        if (LOG_ENABLE && Log.isLoggable(TAG, Log.WARN)) {
//            Log.w(TAG, buildMsg(msg));
//        }
//    }
//
//
//    public static void w(String msg, Exception e) {
//        if (LOG_ENABLE && Log.isLoggable(TAG, Log.WARN)) {
//            Log.w(TAG, buildMsg(msg), e);
//        }
//    }
//

    public static void e(String msg) {
        if (LOG_ENABLE)
            Log.e(TAG, buildMsg(msg));
    }

//
//    public static void e(String msg, Exception e) {
//        if (LOG_ENABLE && Log.isLoggable(TAG, Log.ERROR)) {
//            Log.e(TAG, buildMsg(msg), e);
//        }
//    }
}
