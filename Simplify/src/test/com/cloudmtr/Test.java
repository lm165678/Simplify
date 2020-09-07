package com.cloudmtr;

import jni.CqsMesh;
import jni.Cqslim;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static float[] getPositions(String filename) throws FileNotFoundException, IOException{
        File file = new File(filename);
        FileInputStream fis = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while((line = br.readLine()) != null){
            sb.append(line);
        }
        line = sb.toString();
//        line = line.substring(0, line.length());
        String[] r = line.split(",");
        float[] rf = new float[r.length];
        for(int i = 0;i < r.length;i++){
            rf[i] = Float.valueOf(r[i]);
        }
        return rf;
    }
    public static int[] getIndices(String filename) throws FileNotFoundException, IOException{
        File file = new File(filename);
        FileInputStream fis = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while((line = br.readLine()) != null){
            sb.append(line);
        }
        line = sb.toString();
//        line = line.substring(0, line.length());
        String[] r = line.split(",");
        int[] rf = new int[r.length];
        for(int i = 0;i < r.length;i++){
            rf[i] = Integer.valueOf(r[i]);
        }
        return rf;
    }
    public final static void writePos(String filename, float[] pos) throws IOException {
        File file = new File(filename);
        FileOutputStream fis = new FileOutputStream(file);
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fis));
        StringBuilder sb = new StringBuilder();
        for(Float f : pos){
            sb.append(f + ",");
        }
        String line = sb.toString();
        line = line.substring(0, line.length() - 1);
        bufferedWriter.write(line);
        bufferedWriter.flush();
        bufferedWriter.close();
        bufferedWriter = null;
    }
    public final static void writeInd(String filename, int[] ind) throws IOException {
        File file = new File(filename);
        FileOutputStream fis = new FileOutputStream(file);
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fis));
        StringBuilder sb = new StringBuilder();
        for(Integer f : ind){
            sb.append(f + ",");
        }
        String line = sb.toString();
        line = line.substring(0, line.length() - 1);
        bufferedWriter.write(line);
        bufferedWriter.flush();
        bufferedWriter.close();
        bufferedWriter = null;
    }


    public final static void testWrite() throws IOException {
//        String path = Test.class.getResource("").getPath().replace("com/cloudmtr","resouces");
        float[] positions = getPositions("C:\\Users\\Lenovo\\Documents\\WeChat Files\\wxid_p511eou3mwmo22\\FileStorage\\File\\2020-08\\CoordinateConverDemo\\posFile.txt");
        int[] indices = getIndices("C:\\Users\\Lenovo\\Documents\\WeChat Files\\wxid_p511eou3mwmo22\\FileStorage\\File\\2020-08\\CoordinateConverDemo\\indFile.txt");
//        System.out.println("positions:"+ Arrays.toString(positions));
        System.out.println("顶点数量:" + positions.length / 3);
        System.out.println("索引数量:" + indices.length);
        int srcCount = indices.length / 3;
        float s = .3f;
        int tarCount = (int) (srcCount * s);
        System.out.println("原生Tri数量:" + srcCount);
        System.out.println("目标Tri数量:" + tarCount);

        System.out.println("优化比例:" + s);
        // 注释掉这一行,然后单独查看内存,会发现java层运行10次占用了4G多内存
        // 撤销注释,运行10次,发现最终内存停留在4G多,c/c++层没有内存泄漏
        CqsMesh te = Cqslim.qslimRun(positions,indices, .3f);
        if(te!=null) {
            System.out.println("te.getFaces.size:" + te.getFaces().length);
            System.out.println("te.getVertices.size:" + te.getVertices().length);
//            writeInd("D:\\JhonKkk\\Other\\current\\sindFile2.txt", te.getFaces());
//            writePos("D:\\JhonKkk\\Other\\current\\sposFile2.txt", te.getVertices());
        }
        positions = null;
        indices = null;
        te = null;
    }

    public final static void main(String[] args) throws Exception {
//        System.out.println("========================================>1");
//        testWrite();
//        System.out.println("========================================>2");
//        testWrite();
        for(int i = 0;i < 10;i++){
            int finalI = i;
            System.out.println("========================================>" + (finalI + 1));
            try {
                testWrite();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("执行GC!!!!!!!!!!!!!!!!!!");
            Runtime.getRuntime().gc();
        }
        while(true){
            Thread.sleep(1000L);
        }
    }
}
