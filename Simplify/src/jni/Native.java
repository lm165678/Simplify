package jni;

import java.util.Properties;

/**
 * 这个类用于跨平台加载对应c/c++链接库
 * @author JhonKkk
 */
public class Native {
    public static int S_WIN_OS = 0x001;
    public static int S_LINUX_OS = 0x002;
    public static int getBits(){
        Properties props = System.getProperties();
        String bits=String.valueOf(props.get("sun.arch.data.model"));
        return Integer.valueOf(bits);
    }
    public static int getOS(){
        String os = System.getProperty("os.name");
        if(os.toLowerCase().startsWith("win")){
            return S_WIN_OS;
        }
        else{
            return S_LINUX_OS;
        }
    }
    public static void loadNativeLibrary(Class cl, String libPath, String libName){
        String dir=cl.getResource("").getPath().substring(1);
        int bits = getBits();
        libName += "_" + bits;
        int os = getOS();
        if(os == S_WIN_OS){
            libName += ".dll";
        }
        else if(os == S_LINUX_OS){
            libName += ".so";
        }
        String s1 = dir + libPath + libName;
        System.out.println("lib:" + s1);
        System.load(s1);
        System.out.println("加载" + libName + "完成...");
    }
}
