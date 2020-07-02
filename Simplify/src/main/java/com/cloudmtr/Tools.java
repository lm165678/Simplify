package com.cloudmtr;

import java.util.ArrayList;
import java.util.List;

public class Tools {
    public final static List<List> simplify(List<Integer> indices, List<Float> positions, int target_count){
        Simplify.Vertex[] vertices = new Simplify.Vertex[positions.size() / 3];
        for(int i = 0,j = 0;i < positions.size();i+=3,j++){
            vertices[j] = new Simplify.Vertex();
            vertices[j].m_p.x = positions.get(i);
            vertices[j].m_p.y = positions.get(i + 1);
            vertices[j].m_p.z = positions.get(i + 2);
        }

        Simplify.Triangle[] triangles = new Simplify.Triangle[indices.size() / 3];
        for(int i = 0, j = 0;i < indices.size();i+=3, j++){
            triangles[j] = new Simplify.Triangle(indices.get(i), indices.get(i + 1), indices.get(i + 2));
        }
        Simplify.Result result = new Simplify().simplify(vertices, triangles, target_count, new Simplify.SimplifyOptions());
        if(result != null){
            List<Integer> rindices = new ArrayList<>();
            List<Float> rpositions = new ArrayList<>();
            Simplify.Triangle t = null;
            int idx = 0;
            for(int i = 0;i < result.m_triangles.length;i++){
                t = result.m_triangles[i];
                idx = i * 3;
                rpositions.add(result.m_vertices[t.m_indicse[0]].m_p.x);
                rpositions.add(result.m_vertices[t.m_indicse[0]].m_p.y);
                rpositions.add(result.m_vertices[t.m_indicse[0]].m_p.z);
                rpositions.add(result.m_vertices[t.m_indicse[1]].m_p.x);
                rpositions.add(result.m_vertices[t.m_indicse[1]].m_p.y);
                rpositions.add(result.m_vertices[t.m_indicse[1]].m_p.z);
                rpositions.add(result.m_vertices[t.m_indicse[2]].m_p.x);
                rpositions.add(result.m_vertices[t.m_indicse[2]].m_p.y);
                rpositions.add(result.m_vertices[t.m_indicse[2]].m_p.z);
                rindices.add(idx);
                rindices.add(idx + 1);
                rindices.add(idx + 2);
            }
            List<List> r = new ArrayList<>();
            r.add(rindices);
            r.add(rpositions);
            return r;
        }
        return null;
    }
}
