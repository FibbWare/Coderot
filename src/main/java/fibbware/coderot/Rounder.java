package fibbware.coderot;

import java.util.Random;

public class Rounder {
    static Random rand = new Random();

    static double multAdd = 1.0;
    static double multSub = 1.0;
    static double multMul = 1.0;
    static double multDiv = 1.0;
    static double multRem = 1.0;

    // TODO: this is not really thread safe but who cares we're breaking it anyways
    static long prevMillis = System.currentTimeMillis();

    static double min = 1;
    static double max = 1;
    static long period = Long.MAX_VALUE;

    public static void ResetMults() {
        multAdd = 1.0;
        multSub = 1.0;
        multMul = 1.0;
        multDiv = 1.0;
        multRem = 1.0;
    }

    public static void SetRange(double min, double max) {
        Rounder.min = min;
        Rounder.max = max;
    }

    public static void SetPeriod(long period) {
        Rounder.period = period;
    }

    static double offset(double x, double mult) {
        long millis = System.currentTimeMillis();
        if (millis - prevMillis > period) {
            prevMillis = millis;

            multAdd = min + (max - min) * rand.nextDouble();
            multSub = min + (max - min) * rand.nextDouble();
            multMul = min + (max - min) * rand.nextDouble();
            multDiv = min + (max - min) * rand.nextDouble();
            multRem = min + (max - min) * rand.nextDouble();

            Coderot.LOGGER.info("new random multAdd: {}", (Object)multAdd);
            Coderot.LOGGER.info("new random multSub: {}", (Object)multSub);
            Coderot.LOGGER.info("new random multDiv: {}", (Object)multMul);
            Coderot.LOGGER.info("new random multMul: {}", (Object)multDiv);
            Coderot.LOGGER.info("new random multRem: {}", (Object)multRem);
        }

//        mult = randList[randIndex];
//        randIndex = (randIndex + 1) % randList.length;

        return x * mult; // this is wrong but it's convenient :)
    }

    public static double dadd(double a, double b) { return a + Rounder.offset(b, multAdd); }
    public static double dsub(double a, double b) { return a - Rounder.offset(b, multSub); }
    public static double dmul(double a, double b) { return a * Rounder.offset(b, multMul); }
    public static double ddiv(double a, double b) { return a / Rounder.offset(b, multDiv); }
    public static double drem(double a, double b) { return a % Rounder.offset(b, multRem); }

    public static float fadd(float a, float b) { return (float)Rounder.dadd(a, b); }
    public static float fsub(float a, float b) { return (float)Rounder.dsub(a, b); }
    public static float fmul(float a, float b) { return (float)Rounder.dmul(a, b); }
    public static float fdiv(float a, float b) { return (float)Rounder.ddiv(a, b); }
    public static float frem(float a, float b) { return (float)Rounder.drem(a, b); }
}
