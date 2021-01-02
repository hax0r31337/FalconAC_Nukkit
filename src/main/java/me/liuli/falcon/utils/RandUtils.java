package me.liuli.falcon.utils;

public class RandUtils {
    public static boolean RandBool() {
        return Math.random()<0.5;
    }
    public static double RandDouble(double min,double max) {
        return Math.random() * (max - min) + min;
    }
    public static int RandInt(double min,double max) {
        return (int) (Math.random() * (max - min) + min);
    }
}
