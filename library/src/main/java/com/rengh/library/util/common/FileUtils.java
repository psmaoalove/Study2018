
package com.rengh.library.util.common;

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by rengh on 17-5-29.
 */
public class FileUtils {
    private static final String TAG = "FileUtils";

    /**
     * 获取Assets目录文件的输入流
     */
    public static InputStream getAssetsFileInputStream(Context context, String name) {
        InputStream in = null;
        try {
            in = context.getAssets().open(name);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return in;
    }

    /**
     * 复制asset文件到指定目录
     *
     * @param oldPath asset下的路径
     * @param newPath SD卡下保存路径
     */
    public static boolean copyAssets(Context context, String oldPath, String newPath) {
        boolean success = false;
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = context.getAssets().open(oldPath);
            fos = new FileOutputStream(new File(newPath));
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
                fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
            }
            fos.flush();// 刷新缓冲区
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return success;
    }

    /**
     * 判断指定文件是否存在
     */
    public static boolean isExists(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        return file.exists();
    }

    /**
     * 创建指定文件
     */
    public static void createFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        File file = new File(path);
        if (!file.exists()) {
            try {
                String parent = file.getParent();
                if (!TextUtils.isEmpty(parent)) {
                    File dir = new File(parent);
                    if (!dir.exists()) {
                        LogUtils.i(TAG, "create parent " + parent + " " + dir.mkdirs());
                    }
                }
                LogUtils.i(TAG, "create file " + path + " " + file.createNewFile());
            } catch (IOException e) {
                LogUtils.i(TAG, "Cann't create file:" + path + ", IOException:" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * 检查目录是否存在，不存在则创建
     */
    public static boolean checkDirExists(String dirName) {
        File file = new File(dirName);
        if (file.exists() && file.isDirectory()) {
            return true;
        }
        if (!file.exists()) {
            file.mkdirs();
        }
        if (!file.isDirectory()) {
            file.delete();
            file.mkdirs();
        }
        if (file.isDirectory()) {
            return true;
        }
        return false;
    }

    public static boolean write(String fileName, String content, boolean apend) {
        boolean success = false;
        if (TextUtils.isEmpty(fileName) || TextUtils.isEmpty(content)) {
            LogUtils.e(TAG, "path=" + fileName + ", content=" + content);
            return success;
        }
        if (!apend) {
            deleteDirOrFile(fileName);
        }
        if (!isExists(fileName)) {
            createFile(fileName);
        }
        RandomAccessFile randomFile = null;
        try {
            randomFile = new RandomAccessFile(fileName, "rw");
            long fileLength = 0l;
            if (apend) {
                fileLength = randomFile.length();
            }
            randomFile.seek(fileLength);
            randomFile.writeBytes(content + "\r\n");
            success = true;
        } catch (Exception e) {
            LogUtils.e(TAG, "Exception " + e.getMessage());
        } finally {
            if (null != randomFile) {
                try {
                    randomFile.close();
                } catch (IOException e) {
                }
            }
        }
        return success;
    }

    /**
     * 读取文件内容
     */
    public static String getContent(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        InputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return getContent(in);
    }

    /**
     * 读取文件内容
     */
    public static String getContent(InputStream in) {
        if (in == null) {
            return null;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder builder = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return builder.toString();
    }

    /**
     * 删除目录及目录下的文件
     */
    public static boolean deleteDirOrFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        if (!file.exists()) {
            return true;
        }
        return deleteDirOrFile(file);
    }

    /**
     * 删除目录及目录下的文件
     */
    public static boolean deleteDirOrFile(File file) {
        if (!file.exists()) {
            return true;
        }
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDirOrFile(children[i]);
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return file.delete();
    }

    /**
     * 重命名文件
     *
     * @param resPath 原文件路径名称
     * @param desPath 新文件路径名称
     * @return true或false
     */
    public static boolean renameTo(String resPath, String desPath) {
        if (TextUtils.isEmpty(resPath) || TextUtils.isEmpty(desPath)) {
            return false;
        }
        File resFile = new File(resPath);
        File desFile = new File(desPath);
        if (!resFile.exists()) {
            return false;
        }
        if (desFile.exists() && !desFile.delete()) {
            return false;
        }
        return resFile.renameTo(desFile);
    }

    /**
     * 拷贝文件
     */
    public static boolean copyFile(String resPath, String desPath) {
        if (TextUtils.isEmpty(resPath) || TextUtils.isEmpty(desPath)) {
            return false;
        }
        File resFile = new File(resPath);
        File desFile = new File(desPath);
        return copyFile(resFile, desFile);
    }

    /**
     * 拷贝文件
     */
    public static boolean copyFile(File resFile, File desFile) {
        FileChannel inc = null;
        FileChannel out = null;
        RandomAccessFile fos = null;
        FileInputStream in = null;
        boolean isSuccess = false;
        try {
            if (!resFile.exists()) {
                return false;
            }
            if (desFile.exists()) {
                desFile.delete();
            }
            desFile.createNewFile();

            fos = new RandomAccessFile(desFile, "rw");
            in = new FileInputStream(resFile);
            inc = in.getChannel();
            out = fos.getChannel();
            if (resFile.length() != 0l) {
                ByteBuffer bb = ByteBuffer.allocate(1024 * 100);
                int read = 0;
                int cursum = 0;
                while ((read = inc.read(bb)) != -1) {
                    bb.flip();
                    out.write(bb);
                    bb.clear();
                    cursum += read;
                    if (cursum >= 1024 * 128) {
                        ThreadUtils.sleep(10);
                        cursum = 0;
                    }
                }
                if (fos.getFD().valid()) {
                    fos.getFD().sync();
                }
                isSuccess = true;
            }
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
            if (inc != null) {
                try {
                    inc.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return isSuccess;
    }

    /**
     * 获取文件最后修改时间
     */
    public static long getFileLastModifyTime(String path) {
        if (TextUtils.isEmpty(path)) {
            return -1l;
        }
        File file = new File(path);
        if (!file.exists()) {
            return -1l;
        }
        return file.lastModified();
    }

    public static long getDirSize(String path) {
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            return file.getTotalSpace();
        } else {
            return 0;
        }
    }

    public static long getDirFreeSize(String path) {
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            return file.getFreeSpace();
        } else {
            return 0;
        }
    }

    public static long getFileSize(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            return file.length();
        } else {
            return 0;
        }
    }

    /**
     * 获取文件类型
     *
     * @see FileType
     */
    public static int getFileType(String filePath) {
        return FileType.getFileType(filePath);
    }

    /**
     * 清除空格、制表符等
     */
    public static String removeWhitespace(String content) {
        if (content == null || "".equals(content)) {
            return null;
        }
        return content.replaceAll("\\s*", "");
    }

    /**
     * 修改目录或文件的权限，递归修改
     *
     * @param path 文件或目录的路径
     * @param permission 需要设置的权限
     */
    public static void chmod(String path, String permission) {
        if (!isExists(path)) {
            return;
        }
        Process proc = null;
        BufferedReader in = null;
        BufferedReader err = null;
        PrintWriter out = null;
        try {
            proc = Runtime.getRuntime().exec("sh");
            if (proc != null) {
                in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                err = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                        proc.getOutputStream())), true);
                out.println("chmod -R " + permission + " " + path);
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

    /**
     * 根据文件头信息，判断文件类型
     */
    public static class FileType {
        // 图片
        public static final int FILE_TYPE_IMAGE_JPG = 1;
        public static final int FILE_TYPE_IMAGE_PNG = 2;
        public static final int FILE_TYPE_IMAGE_BMP = 3;
        public static final int FILE_TYPE_IMAGE_WEBP = 4;

        // 动图
        public static final int FILE_TYPE_IMAGE_GIF = 5;

        // 视频
        public static final int FILE_TYPE_VIDEO_TS = 6;
        public static final int FILE_TYPE_VIDEO_MP4 = 7;

        public static final HashMap<String, Integer> mFileTypes = new HashMap<String, Integer>();

        static {
            // 图片
            mFileTypes.put("FFD8FF", FILE_TYPE_IMAGE_JPG);
            mFileTypes.put("89504E", FILE_TYPE_IMAGE_PNG);
            mFileTypes.put("474946", FILE_TYPE_IMAGE_GIF);
            mFileTypes.put("424DF2", FILE_TYPE_IMAGE_BMP);
            mFileTypes.put("524946", FILE_TYPE_IMAGE_WEBP);
            // 视频
            mFileTypes.put("474011", FILE_TYPE_VIDEO_TS);
            mFileTypes.put("000000", FILE_TYPE_VIDEO_MP4);
        }

        public static int getFileType(String filePath) {
            String value = getFileHeader(filePath);
            if (null != value) {
                return mFileTypes.get(value);
            } else {
                return -1;
            }
        }

        // 获取文件头信息
        public static String getFileHeader(String filePath) {
            FileInputStream is = null;
            String value = null;
            try {
                is = new FileInputStream(filePath);
                byte[] b = new byte[3];
                is.read(b, 0, b.length);
                value = bytesToHexString(b);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != is) {
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                }
            }
            System.out.println("path=" + filePath + ", header=" + value);
            return value;
        }

        private static String bytesToHexString(byte[] src) {
            if (src == null || src.length <= 0) {
                return null;
            }
            StringBuilder builder = new StringBuilder();
            String hv;
            for (int i = 0; i < src.length; i++) {
                hv = Integer.toHexString(src[i] & 0xFF).toUpperCase(Locale.getDefault());
                if (hv.length() < 2) {
                    builder.append(0);
                }
                builder.append(hv);
            }
            return builder.toString();
        }
    }

}
