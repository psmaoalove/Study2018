package com.rengh.study.util.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Created by rengh on 18-1-30.
 */

public class ShellUtils {
    private static final String TAG = "ShellUtils";

    public static void su(String... line) {
        shellImpl("su", line);
    }

    public static void sh(String... line) {
        shellImpl("sh", line);
    }

    private static void shellImpl(String shell, String... line) {
        Process proc = null;
        BufferedReader in = null;
        BufferedReader err = null;
        PrintWriter out = null;
        try {
            proc = Runtime.getRuntime().exec(shell);
            if (proc != null) {
                in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                err = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                        proc.getOutputStream())), true);
                for (String tmp : line) {
                    out.println(tmp);
                }
                out.println("exit");
                String result;
                while ((result = in.readLine()) != null) {
                    LogUtils.d(TAG, "chmod() result:" + result);
                }
                while ((result = err.readLine()) != null) {
                    LogUtils.d(TAG, "chmod() result:" + result);
                }
            }
        } catch (IOException e) {
            LogUtils.e(TAG, "chmod() IOException: " + e.getMessage());
        } catch (Exception e) {
            LogUtils.e(TAG, "chmod() Exception: " + e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (err != null) {
                try {
                    err.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                out.close();
            }
            if (proc != null) {
                try {
                    int exitValue = proc.exitValue();
                    if (0 != exitValue) {
                        LogUtils.d(TAG, "chmod() exitValue: " + exitValue);
                    }
                } catch (IllegalThreadStateException e) {
                }
                proc = null;
            }
        }
    }

}
