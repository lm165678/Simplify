import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
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
    public final static void main(String[] args) throws IOException {
        float[] positions = getPositions("D:\\JhonKkk\\MyWork\\Java\\QSLim\\Simplify\\src\\positions.txt");
        int[] indices = getIndices("D:\\JhonKkk\\MyWork\\Java\\QSLim\\Simplify\\src\\indices.txt");
//        System.out.println("positions:"+ Arrays.toString(positions));
        System.out.println("顶点数量:" + positions.length);
        System.out.println("索引数量:" + indices.length);
        int srcCount = positions.length / 3;
        float s = .3f;
        int tarCount = (int) (srcCount * s);
        System.out.println("原生顶点数量:" + srcCount);
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
        System.out.println("结果:" + r.size());
        System.out.println("索引数量:" + r.get(0).size());
        System.out.println("顶点数量:" + r.get(1).size());
        System.out.println("indices:" + r.get(0));
        System.out.println("positions:" + r.get(1));
    }
}
