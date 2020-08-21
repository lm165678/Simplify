package jni;

import java.io.*;
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
    public static void loadNativeLibrary(Class cl, String libPath, String libName) {
        String dir=cl.getResource("").getPath();
        int bits = getBits();
        libName += "_x" + bits;
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
        /*try{

            String nativeTempDir = System.getProperty("java.io.tmpdir");
            String libraryPath = nativeTempDir+File.separator+libName;
            File extractedLibFile = new File(libraryPath);
            if(extractedLibFile.exists()){
                extractedLibFile.delete();
            }
            //String s1 = "E:\\workspace\\sources\\simplify\\out\\production\\Simplify\\jni\\native\\SimpleQSlim_64.dll";//dir + libPath + libName;
            InputStream in = cl.getResourceAsStream(libPath + libName);
            //FileInputStream in = new FileInputStream(new File(s1));
            BufferedInputStream reader = new BufferedInputStream(in);
            FileOutputStream writer = new FileOutputStream(extractedLibFile);
            byte[] buffer = new byte[1024];

            while (reader.read(buffer) > 0){
                writer.write(buffer);
            }
            writer.flush();
            writer.close();
            reader.close();
            System.out.println("lib:" + extractedLibFile.getAbsolutePath());
            System.load(extractedLibFile.getAbsolutePath());
            System.out.println("加载" + libName + "完成...");
        }catch (Exception e){

            e.printStackTrace();
        }*/
    }
}
