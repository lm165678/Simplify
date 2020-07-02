package com.cloudmtr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Simplify {
    public static class SimplifyOptions{
        public float m_agressiveness = 7.F;
        public float m_update = 5.F;
        public boolean m_recompute = false;
    }
    public static class Triangle{
        public int[] m_indicse;
        public double[] m_err = new double[4];
        public boolean m_deleted = false;
        public boolean m_dirty = false;
        public Vector3f m_n = new Vector3f();
        public Triangle(int a, int b, int c){
            m_indicse = new int[]{a, b, c};
        }
    }
    public static class Vertex{
        public Vector3f m_p = new Vector3f();
        public int m_tstart = 0;
        public int m_tcount = 0;
        public Quadric m_q = null;
        public boolean m_border = false;
    }
    public static class Ref{
        public int m_tid = 0;
        public int m_tvertex = 0;
    }
    protected Vertex[] m_vertices = null;
    protected Triangle[] m_triangles = null;
    protected List<Ref> m_refs = null;
    public static class Result{
        Vertex[] m_vertices;
        Triangle[] m_triangles;

        public Result(Vertex[] vertices, Triangle[] triangles) {
            m_vertices = vertices;
            m_triangles = triangles;
        }

        public Vertex[] getVertices() {
            return m_vertices;
        }

        public void setVertices(Vertex[] vertices) {
            m_vertices = vertices;
        }

        public Triangle[] getTriangles() {
            return m_triangles;
        }

        public void setTriangles(Triangle[] triangles) {
            m_triangles = triangles;
        }
    }
    public final Result simplify(Vertex[] vertices, Triangle[] triangles, int target_count, SimplifyOptions options){
        this.m_vertices = vertices;
        this.m_triangles = triangles;
        return simplify_mesh(target_count, options);
    }
    public final Result simplify_mesh(int target_count, SimplifyOptions options){
        float agressiveness = options.m_agressiveness;
        float update = options.m_update;
        boolean recompute = options.m_recompute;
        int deleted_triangles = 0;
        int triangle_count = m_triangles.length;
        System.out.println("triangle_count:" + triangle_count + ",target_count:" + target_count);
        Vector3f p = new Vector3f();
        int i = 0, j = 0;
        Map<Integer, Boolean> deleted0 = new HashMap<>();
        Map<Integer, Boolean> deleted1 = new HashMap<>();
        //init
        for(i = m_triangles.length - 1;i >= 0;i--){
            m_triangles[i].m_deleted = false;
        }
        for(int iteration = 0;iteration < 100;iteration++){
            if(triangle_count - deleted_triangles <= target_count)
                break;
            //间隔更新mesh
            if(iteration % update == 0){
                update_mesh(iteration, recompute);
            }
            //清除脏标志
            for(i = m_triangles.length - 1;i >= 0;i--){
                m_triangles[i].m_dirty = false;
            }
            double threshold = 0.000000001 * Math.pow(iteration + 3, agressiveness);
            for(i = m_triangles.length - 1;i >= 0;i--){
                Triangle t = m_triangles[i];
                if(t.m_err[3] >= threshold || t.m_deleted || t.m_dirty)
                    continue;
                for(j = 0;j < 3;j++){
                    if(t.m_err[j] >= threshold)
                        continue;
//                    System.out.println("进入2!!!!");
                    int i0 = t.m_indicse[j];
                    int i1 = t.m_indicse[(j + 1) % 3];
                    Vertex v0 = m_vertices[i0];
                    Vertex v1 = m_vertices[i1];
                    //边界检查
                    if(v0.m_border || v1.m_border)
                        continue;
//                    System.out.println("进入3!!!!");
                    deleted0.clear();
                    deleted1.clear();
                    calculate_error(v0, v1, p);
                    //翻转时不移除
                    if(flipped(p, i0, i1, v0, v1, deleted0)){
//                        System.out.println("翻转跳过1");
                        continue;
                    }
                    if(flipped(p, i1, i0, v1, v0, deleted1)){
//                        System.out.println("翻转跳过2");
                        continue;
                    }

                    //没有翻转，所以删除边缘
                    v0.m_p.set(p);
                    v0.m_q.addSelf(v1.m_q);
                    int tstart = m_refs.size();
                    deleted_triangles += update_triangles(i0, v0, deleted0);
                    deleted_triangles += update_triangles(i0, v1, deleted1);
                    int tcount = m_refs.size() - tstart;
                    v0.m_tstart = tstart;
                    v0.m_tcount = tcount;
                    break;
                }

                //结束?
                if(triangle_count - deleted_triangles <= target_count){
                    break;
                }
            }
        }
        System.out.println("deleted_triangles:" + deleted_triangles);
        return compact_mesh();
    }
    public final boolean flipped(Vector3f p, int i0, int i1, Vertex v0, Vertex v1, Map<Integer, Boolean> deleted){
        for(int k = 0;k < v0.m_tcount;k++){
            Ref ref = m_refs.get(v0.m_tstart + k);
            Triangle t = m_triangles[ref.m_tid];
            if(t.m_deleted)continue;
            int s = ref.m_tvertex;
            int id1 = t.m_indicse[(s+1) % 3];
            int id2 = t.m_indicse[(s+2) % 3];

            if(id1 == i1 || id2 == i1){
                //删除?
                deleted.put(k, true);
                continue;
            }

            Vector3f d1 = m_vertices[id1].m_p.subtract(p);
            Vector3f d2 = m_vertices[id2].m_p.subtract(p);
            d1.normalizeLocal();
            d2.normalizeLocal();

            if(Math.abs(d1.dot(d2)) > 0.99999){
                return true;
            }

            Vector3f n = d1.cross(d2);
            n.normalizeLocal();
            deleted.put(k, false);
            if(n.dot(t.m_n) < .2){
                return true;
            }
        }
        return false;
    }
    public final int update_triangles(int i0, Vertex v, Map<Integer, Boolean> deleted){
        int deleted_triangles = 0;
        Vector3f p = new Vector3f();
        for(int k = 0;k < v.m_tcount;k++){
            Ref r = m_refs.get(v.m_tstart + k);
            Triangle t = m_triangles[r.m_tid];
            Vertex[] pts = new Vertex[t.m_indicse.length];
            int in = 0;
            for(int ind : t.m_indicse){
                pts[in++] = m_vertices[ind];
            }

            if(t.m_deleted)continue;

            if(deleted.get(k)){
                t.m_deleted = true;
                deleted_triangles++;
                continue;
            }

            t.m_indicse[r.m_tvertex] = i0;
            t.m_dirty = true;
            t.m_err[0] = calculate_error(pts[0], pts[1], p);
            t.m_err[1] = calculate_error(pts[1], pts[2], p);
            t.m_err[2] = calculate_error(pts[2], pts[0], p);
            t.m_err[3] = Math.min(t.m_err[0], Math.min(t.m_err[1], t.m_err[2]));
            m_refs.add(r);
        }
//        System.out.println("删除....");
        return deleted_triangles;
    }
    public final void update_mesh(int iteration, boolean recompute){
        int i = 0, j = 0, k = 0;
        if(iteration > 0){
            //压缩紧凑三角形
            int dst = 0;
            for(i = 0;i < m_triangles.length;i++){
                if(!m_triangles[i].m_deleted){
                    m_triangles[dst++] = m_triangles[i];
                }
            }
            Triangle[] nt = new Triangle[dst];
            for(int ii = 0;ii < dst;ii++){
                nt[ii] = m_triangles[ii];
//                m_triangles[iii] = null;
            }
            m_triangles = nt;
        }
        //
        //按平面和边误差初始化二次曲面
        //
        //开始时必需（迭代==0）
        //简化过程中不需要重新计算，
        //但主要是改善了闭合网格的结果
        //
        if(recompute || iteration == 0){
            for(i = 0;i < m_vertices.length;i++){
                m_vertices[i].m_q = new Quadric();
            }
            for(i = 0;i < m_triangles.length;i++){
                Triangle t = m_triangles[i];
                Vector3f[] p = new Vector3f[t.m_indicse.length];
                int pi = 0;
                for(int ind : t.m_indicse){
                    p[pi++] = m_vertices[ind].m_p;
                }
                Vector3f p10 = p[1].subtract(p[0]);
                Vector3f p20 = p[2].subtract(p[0]);
                Vector3f n = p10.cross(p20);
                Quadric q = new Quadric();
                t.m_n = n.normalizeLocal();
                q.makePlane(t.m_n.x, t.m_n.y, t.m_n.z, -t.m_n.dot(p[0]));
                for(j = 0;j < 3;j++){
                    m_vertices[t.m_indicse[j]].m_q.addSelf(q);
                }
            }

            for(i = 0;i < m_triangles.length;i++){
                Triangle t = m_triangles[i];
                Vector3f p = new Vector3f();
                Vertex[] pts = new Vertex[t.m_indicse.length];
                int id = 0;
                for(int ind : t.m_indicse){
                    pts[id++] = m_vertices[ind];
                }
                for(j = 0;j < 3;j++){
                    Vertex v1 = pts[j];
                    Vertex v2 = pts[(j+1) % 3];
                    t.m_err[j] = calculate_error(v1, v2, p);
                }
                t.m_err[3] = Math.min(Math.min(t.m_err[0], t.m_err[1]), t.m_err[2]);
            }
        }
        for(i = 0;i < m_vertices.length;i++){
            m_vertices[i].m_tstart = 0;
            m_vertices[i].m_tcount = 0;
        }

        for(i = 0;i < m_triangles.length;i++){
            Triangle t = m_triangles[i];
            for(j = 0;j < 3;j++){
                m_vertices[t.m_indicse[j]].m_tcount++;
            }
        }
        int tstart = 0;
        for(i = 0;i < m_vertices.length;i++){
            Vertex v = m_vertices[i];
            v.m_tstart = tstart;
            tstart += v.m_tcount;
            v.m_tcount = 0;
        }
        m_refs = new ArrayList<>();
        int size = m_triangles.length * 3;
        for(i = 0;i < size;i++){
//            m_refs[i] = new Ref();
            m_refs.add(new Ref());
        }
        for(i = 0;i < m_triangles.length;i++){
            Triangle t = m_triangles[i];
            for(j = 0;j < 3;j++){
                Vertex v = m_vertices[t.m_indicse[j]];
                m_refs.get(v.m_tstart + v.m_tcount).m_tid = i;
                m_refs.get(v.m_tstart + v.m_tcount).m_tvertex = j;
                v.m_tcount++;
            }
        }

        if(iteration == 0){
            List<Integer> vcount = null;
            List<Integer> vids = null;
            for(i = m_vertices.length - 1;i >= 0;i--){
                m_vertices[i].m_border = false;
            }

            for(i = m_vertices.length - 1;i >= 0;i--){
                Vertex v = m_vertices[i];
                vcount = new ArrayList<>();
                vids = new ArrayList<>();
                for(j = v.m_tcount - 1;j >= 0;j--){
                    int kk = m_refs.get(v.m_tstart + j).m_tid;
                    Triangle t = m_triangles[kk];
                    for(k = 0;k < 3;k++){
                        int ofs = 0;
                        int id = t.m_indicse[k];

                        while(ofs < vcount.size()){
                            if(vids.get(ofs) == id)break;
                            ofs++;
                        }

                        if(ofs == vcount.size()){
                            vcount.add(1);
                            vids.add(id);
                        }
                        else{
                            int tt = vcount.get(ofs);
                            tt++;
                            vcount.set(ofs, tt);
                        }
                    }
                }

                for(j = vcount.size() - 1;j >= 0;j--){
                    if(vcount.get(j) == 1){
                        m_vertices[vids.get(j)].m_border = true;
                    }
                }
            }
        }
    }

    /**
     * 计算二次误差
     * @param v1
     * @param v2
     * @param p
     * @return
     */
    public final double calculate_error(Vertex v1, Vertex v2, Vector3f p){
        Quadric q = v1.m_q.add(v2.m_q);
        boolean border = v1.m_border & v2.m_border;
        float[] target = q.optimize();
        float error = 0;
        if(!border && target != null){
            Vector3f t = new Vector3f(target[0], target[1], target[2]);
            error = q.evaluate(t);
            p.set(t);
        }
        else{
            Vector3f v3 = v1.m_p.interpolateLocal(v2.m_p, .5f);
            float err1 = q.evaluate(v1.m_p);
            float err2 = q.evaluate(v2.m_p);
            float err3 = q.evaluate(v3);
            error = Math.max(Math.max(err1, err2), err3);
            if(error == err1)p.set(v1.m_p);
            if(error == err2)p.set(v2.m_p);
            if(error == err3)p.set(v3);
        }
        return error;
    }

    /**
     * 更新mesh
     * @return
     */
    public final Result compact_mesh(){
        int dst = 0;
        Result result = null;
        for(int i = 0;i < m_vertices.length;i++){
            m_vertices[i].m_tcount = 0;
        }
        Triangle triangle = null;
        for(int i = 0;i < m_triangles.length;i++){
            triangle = m_triangles[i];
            if(triangle.m_deleted)continue;
            m_triangles[dst++] = m_triangles[i];
            for(int j = 0;j < 3;j++){
                m_vertices[triangle.m_indicse[j]].m_tcount = 1;
            }
        }
        //删除从dst开始的所有元素
        Triangle[] ntr = new Triangle[dst];
        for(int i = 0;i < dst;i++){
            ntr[i] = m_triangles[i];
//            m_triangles[dst] = null;
        }
        dst = 0;
        for(int i = 0;i < m_vertices.length;i++){
            if(m_vertices[i].m_tcount > 0){
                m_vertices[i].m_tstart = dst;
                m_vertices[dst].m_p = m_vertices[i].m_p;
                dst++;
            }
        }
        for(int i = 0;i < ntr.length;i++){
            triangle = ntr[i];
            for(int j = 0;j < 3;j++){
                triangle.m_indicse[j] = m_vertices[triangle.m_indicse[j]].m_tstart;
            }
        }
        Vertex[] nvr = new Vertex[dst];
        //删除从dst开始的所有顶点
        for(int i = 0;i < dst;i++){
            nvr[i] = m_vertices[i];
//            m_vertices[dst] = null;
        }
        result = new Result(nvr, ntr);
        return result;
    }
}
