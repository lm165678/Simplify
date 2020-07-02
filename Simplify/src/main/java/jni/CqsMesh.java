package jni;

public class CqsMesh {
    public float[] m_vertices;
    public int[] m_faces;

    public CqsMesh(float[] vertices, int[] faces) {
        m_vertices = vertices;
        m_faces = faces;
    }

    public float[] getVertices() {
        return m_vertices;
    }

    public void setVertices(float[] vertices) {
        m_vertices = vertices;
    }

    public int[] getFaces() {
        return m_faces;
    }

    public void setFaces(int[] faces) {
        m_faces = faces;
    }
}
