package fibbware.coderot;

public class MathModifier {

    public static IRandomOffsetAlgorithm randomOffsetAlgoritm = null;

    public static double dadd(double a, double b) {
        if (randomOffsetAlgoritm == null) return a + b;
        return a + randomOffsetAlgoritm.OffsetAdd(b);
    }

    public static double dsub(double a, double b) {
        if (randomOffsetAlgoritm == null) return a - b;
        return a - randomOffsetAlgoritm.OffsetSub(b);
    }

    public static double dmul(double a, double b) {
        if (randomOffsetAlgoritm == null) return a * b;
        return a * randomOffsetAlgoritm.OffsetMul(b);
    }

    public static double ddiv(double a, double b) {
        if (randomOffsetAlgoritm == null) return a / b;
        return a / randomOffsetAlgoritm.OffsetDiv(b);
    }

    public static double drem(double a, double b) {
        if (randomOffsetAlgoritm == null) return a % b;
        return a % randomOffsetAlgoritm.OffsetRem(b);
    }

    public static float fadd(float a, float b) { return (float) MathModifier.dadd(a, b); }
    public static float fsub(float a, float b) { return (float) MathModifier.dsub(a, b); }
    public static float fmul(float a, float b) { return (float) MathModifier.dmul(a, b); }
    public static float fdiv(float a, float b) { return (float) MathModifier.ddiv(a, b); }
    public static float frem(float a, float b) { return (float) MathModifier.drem(a, b); }

    public static void stupid_unused_warning_hack() {
        float _ = fadd(0, 0);
        float _ = fsub(0, 0);
        float _ = fmul(0, 0);
        float _ = fdiv(0, 0);
        float _ = frem(0, 0);
    }
}
