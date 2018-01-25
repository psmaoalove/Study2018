package com.rengh.study.java.designPattern.singleton;

/**
 * 单例模式
 * Created by rengh on 18-1-15.
 */
public class Singleton {

    public static class Singleton1 {
        private static Singleton1 sInstance = null;

        private Singleton1() {
        }

        public static Singleton1 getInstance() {
            if (null == sInstance) {
                sInstance = new Singleton1();
            }
            return sInstance;
        }
    }

    public static class Singleton2 {
        private static Singleton2 sInstance = null;

        private Singleton2() {
        }

        public static synchronized Singleton2 getInstance() {
            if (null == sInstance) {
                sInstance = new Singleton2();
            }
            return sInstance;
        }

    }

    public static class Singleton3 {
        private static Singleton3 sInstance = null;

        private Singleton3() {
        }

        public static Singleton3 getInstance() {
            if (null == sInstance) {
                synchronized (Singleton3.class) {
                    if (null == sInstance) {
                        sInstance = new Singleton3();
                    }
                }
            }
            return sInstance;
        }
    }

    public static class Singleton4 {
        private static Singleton4 sInstance = new Singleton4();

        private Singleton4() {
        }

        public static Singleton4 getInstance() {
            return sInstance;
        }
    }

    public static class Singleton5 {
        private static Singleton5 sInstance = null;

        static {
            sInstance = new Singleton5();
        }

        private Singleton5() {
        }

        public static Singleton5 getInstance() {
            return sInstance;
        }
    }
}
