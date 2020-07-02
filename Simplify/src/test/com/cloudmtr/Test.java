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
    public final static void writePos(String filename, List<Float> pos) throws IOException {
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
    public final static void writeInd(String filename, List<Integer> ind) throws IOException {
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
    public final static void main(String[] args) throws Exception {
        String path = Test.class.getResource("").getPath().replace("com/cloudmtr","resouces");
        float[] positions = getPositions(path+"\\positions.txt");
        int[] indices = getIndices(path+"\\indices.txt");
//        System.out.println("positions:"+ Arrays.toString(positions));
        System.out.println("顶点数量:" + positions.length / 3);
        System.out.println("索引数量:" + indices.length);
        int srcCount = indices.length / 3;
        float s = .9f;
        int tarCount = (int) (srcCount * s);
        System.out.println("原生Tri数量:" + srcCount);
        System.out.println("目标Tri数量:" + tarCount);
        System.out.println("优化比例:" + s);
        List<Float> fp = new ArrayList<Float>();
        for(float d : positions){
            fp.add((float)d);
        }
        List<Integer> it = new ArrayList<>();
        for(int i : indices){
            it.add(i);
        }
        List<List> r = Tools.simplify(it, fp, tarCount);
        CqsMesh te = Cqslim.qslimRun(positions,indices, .3f);
        System.out.println("结果:" + r.size());
        System.out.println("Tri数量:" + r.get(0).size() / 3);
//        System.out.println("顶点数量:" + r.get(1).size() / 3);
        writePos(path+"\\sqPosFile.txt", r.get(1));
        writeInd(path+"\\sqIndFile.txt", r.get(0));
//        System.out.println("indices:" + r.get(0));
//        System.out.println("positions:" + r.get(1));

        System.out.print(te.getFaces());
        System.out.print(te.getVertices());
    }
}
