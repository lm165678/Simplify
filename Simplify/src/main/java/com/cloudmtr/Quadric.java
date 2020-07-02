package com.cloudmtr;

public class Quadric {
    public static Quadric fromPoints(Vector3f p1, Vector3f p2, Vector3f p3){
        Vector3f p1p3 = p2.subtract(p1);
        Vector3f p2p3 = p3.subtract(p1);
        Vector3f n = p1p3.cross(p2p3);
        Quadric q = new Quadric();
        n.normalizeLocal();
        return q;
    }
    private float[] m_m = null;
    public Quadric(){
        m_m = new float[10];
        setZero();
    }
    private final void setZero(){
        for(int i = 0;i < 10;i++)m_m[i] = 0;
    }
    protected Quadric set(float m11, float m12, float m13, float m14, float m22, float m23, float m24, float m33, float m34, float m44){
        this.m_m[0] = m11;
        this.m_m[1] = m12;
        this.m_m[2] = m13;
        this.m_m[3] = m14;
        this.m_m[4] = m22;
        this.m_m[5] = m23;
        this.m_m[6] = m24;
        this.m_m[7] = m33;
        this.m_m[8] = m34;
        this.m_m[9] = m44;
        return this;
    }
    public Quadric makePlane(float a, float b, float c, float d){
        return this.set(
                a * a, a * b, a * c, a * d,
                b * b, b * c, b * d,
                c * c, c * d,
                d * d
        );
    }
    public float det(int a11, int a12, int a13, int a21, int a22, int a23, int a31, int a32, int a33){
        float det =

                this.m_m[ a11 ] * this.m_m[ a22 ] * this.m_m[ a33 ]

                + this.m_m[ a13 ] * this.m_m[ a21 ] * this.m_m[ a32 ]

                + this.m_m[ a12 ] * this.m_m[ a23 ] * this.m_m[ a31 ]

                - this.m_m[ a13 ] * this.m_m[ a22 ] * this.m_m[ a31 ]

                - this.m_m[ a11 ] * this.m_m[ a23 ] * this.m_m[ a32 ]

                - this.m_m[ a12 ] * this.m_m[ a21 ] * this.m_m[ a33 ];

        return det;
    }
    public Quadric add(Quadric n){
        return new Quadric().set(
        this.m_m[0] + n.m_m[0],
        this.m_m[1] + n.m_m[1],
        this.m_m[2] + n.m_m[2],
        this.m_m[3] + n.m_m[3],
        this.m_m[4] + n.m_m[4],
        this.m_m[5] + n.m_m[5],
        this.m_m[6] + n.m_m[6],
        this.m_m[7] + n.m_m[7],
        this.m_m[8] + n.m_m[8],
        this.m_m[9] + n.m_m[9]
        );
    }
    public void addSelf(Quadric n){
        this.m_m[0]+=n.m_m[0];   this.m_m[1]+=n.m_m[1];   this.m_m[2]+=n.m_m[2];   this.m_m[3]+=n.m_m[3];
        this.m_m[4]+=n.m_m[4];   this.m_m[5]+=n.m_m[5];   this.m_m[6]+=n.m_m[6];   this.m_m[7]+=n.m_m[7];
        this.m_m[8]+=n.m_m[8];   this.m_m[9]+=n.m_m[9];
    }

    /**
     * 评估一个顶点
     * @param v
     * @return
     */
    public float evaluate(Vector3f v){
        float[] m = this.m_m;
        float x = v.x;
        float y = v.y;
        float z = v.z;
        return m[0]*x*x + 2*m[1]*x*y + 2*m[2]*x*z + 2*m[3]*x + m[4]*y*y
                + 2*m[5]*y*z + 2*m[6]*y + m[7]*z*z + 2*m[8]*z + m[9];
    }

    /**
     * 找到最佳点
     * @return
     */
    public float[] optimize(){
        float det = this.det(0, 1, 2, 1, 4, 5, 2, 5, 7);
        if (Math.abs(det) < 1e-12) return null;
        float[] p = new float[3];
        p[0] = -1/det*(this.det(1, 2, 3, 4, 5, 6, 5, 7 , 8));
        p[1] =  1/det*(this.det(0, 2, 3, 1, 5, 6, 2, 7 , 8));
        p[2] = -1/det*(this.det(0, 1, 3, 1, 4, 6, 2, 5,  8));
        return p;
    }
}
