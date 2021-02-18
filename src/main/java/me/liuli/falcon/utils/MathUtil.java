package me.liuli.falcon.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtil {
    public static boolean randBool() {
        return Math.random() < 0.5;
    }

    public static double randDouble(double min, double max) {
        return Math.random() * (max - min) + min;
    }

    public static int randInt(double min, double max) {
        return (int) (Math.random() * (max - min) + min);
    }

    public static double roundDouble(double value, int scale) {
        return new BigDecimal(value).setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }

    public static double xzCalc(double x, double z) {
        return Math.sqrt(x * x + z * z);
    }
}
