package jni;

public class Cqslim {
    static {
        Native.loadNativeLibrary(Cqslim.class, "native/", "SimpleQSlim");
    }
    public static native CqsMesh qslimRun(float[] vertices, int[] faces, float qsValue);
    public static native CqsMesh qslimRun(CqsMesh cqsMesh, float qsValue);
}
