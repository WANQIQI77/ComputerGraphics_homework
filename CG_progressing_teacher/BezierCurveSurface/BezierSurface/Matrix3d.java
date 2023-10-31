import java.awt.Point;


public class Matrix3d
{
	public static final int LEFT_MULTIPLIER = 0; 
	public static final int RIGHT_MULTIPLIER = 1;

	float a00,a01,a02,a03;
	float a10,a11,a12,a13;
	float a20,a21,a22,a23;
	float a30,a31,a32,a33;

	public Matrix3d()
	{
		identity();	
	}
	
	//����λ��
	public void identity()
	{
		a00 = 1.0f; a01 = 0.0f; a02 = 0.0f; a03 = 0.0f;
		a10 = 0.0f; a11 = 1.0f; a12 = 0.0f; a13 = 0.0f;
		a20 = 0.0f; a21 = 0.0f; a22 = 1.0f; a23 = 0.0f;
		a30 = 0.0f; a31 = 0.0f; a32 = 0.0f; a33 = 1.0f;
	}
	
	//�������������Ƿ����
	public boolean isEqual(Matrix3d matrix)
	{
		if((matrix.a00 <= a00 + 0.000001f) && (matrix.a00 >= a00 - 0.000001f) &&
		   (matrix.a01 <= a01 + 0.000001f) && (matrix.a01 >= a01 - 0.000001f) &&
		   (matrix.a02 <= a02 + 0.000001f) && (matrix.a02 >= a02 - 0.000001f) &&
		   (matrix.a03 <= a03 + 0.000001f) && (matrix.a03 >= a03 - 0.000001f) &&
		   
		   (matrix.a10 <= a10 + 0.000001f) && (matrix.a10 >= a10 - 0.000001f) &&
		   (matrix.a11 <= a11 + 0.000001f) && (matrix.a11 >= a11 - 0.000001f) &&
		   (matrix.a12 <= a12 + 0.000001f) && (matrix.a12 >= a12 - 0.000001f) &&
		   (matrix.a13 <= a13 + 0.000001f) && (matrix.a13 >= a13 - 0.000001f) &&
		   
		   (matrix.a20 <= a20 + 0.000001f) && (matrix.a20 >= a20 - 0.000001f) &&
		   (matrix.a21 <= a21 + 0.000001f) && (matrix.a21 >= a21 - 0.000001f) &&
		   (matrix.a22 <= a22 + 0.000001f) && (matrix.a22 >= a22 - 0.000001f) &&
		   (matrix.a23 <= a23 + 0.000001f) && (matrix.a23 >= a23 - 0.000001f) &&
		   
		   (matrix.a30 <= a30 + 0.000001f) && (matrix.a30 >= a30 - 0.000001f) &&
		   (matrix.a31 <= a31 + 0.000001f) && (matrix.a31 >= a31 - 0.000001f) &&
		   (matrix.a32 <= a32 + 0.000001f) && (matrix.a32 >= a32 - 0.000001f) &&
		   (matrix.a33 <= a33 + 0.000001f) && (matrix.a33 >= a33 - 0.000001f))
				return true;
		
		return false;
	   
	}
	
	
	/*
		��任������������ϵ�����,
	*/
	
	//ƽ��
	public  void translate(float x,float y,float z)
	{
		a30 += x;
		a31 += y;
		a32 += z;
	}
	
	//����
	public void scale(float factor)
	{
		a00 *= factor;
		a11 *= factor;
		a22 *= factor;
	}
	
	//����
	public void scale(float sx,float sy,float sz)
	{
		a00 *= sx;
		a11 *= sy;
		a22 *= sz;
	}
	
	//����
	public void scale(float x,float y,float z,float sx,float sy,float sz)
	{
		/*
		Matrix3d transMatrix = new Matrix3d();
			
		transMatrix.a00 = sx;
		transMatrix.a11 = sy;
		transMatrix.a22 = sz;
		
		transMatrix.a30 = (1.0 - sx) * x;
		transMatrix.a31 = (1.0 - sy) * y;
		transMatrix.a32 = (1.0 - sz) * z;
		this.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);		
		
		*/
		
		//��Ե������,
		//��һ��,������ϵƽ������Ե�;
		//�ڶ���,�������ű任;
		//������,������ϵƽ�ƻ�ԭ����λ��
		
		Matrix3d transMatrix = new Matrix3d();
		transMatrix.translate(-x,-y,-z);
		
		//ֻ��һ��������scale����ֻ�ı�������Խ����ϵ�Ԫ��,�������Ӧʹ�þ���˷�
		Matrix3d scaleMatrix = new Matrix3d();
		scaleMatrix.scale(sx,sy,sz);
		transMatrix.multiply(scaleMatrix,Matrix3d.RIGHT_MULTIPLIER);		
		
		transMatrix.translate(x,y,z);
		this.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);		
		
	}
	
	//����
	public void scale(Vertex3d vertex,float sx,float sy,float sz)
	{
		Matrix3d transMatrix = new Matrix3d();
		transMatrix.translate(-vertex.x,-vertex.y,-vertex.z);
		
		//ֻ��һ��������scale����ֻ�ı�������Խ����ϵ�Ԫ��,�������Ӧʹ�þ���˷�
		Matrix3d scaleMatrix = new Matrix3d();
		scaleMatrix.scale(sx,sy,sz);
		
		transMatrix.multiply(scaleMatrix,Matrix3d.RIGHT_MULTIPLIER);		
		
		transMatrix.translate(vertex.x,vertex.y,vertex.z);
		this.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);		
	}
	
	//��Z����ת
	public void rotateZ(float theta)
	{
		float rad = (float)(Math.PI / 180.0);
		theta *= rad; 
		Matrix3d transMatrix = new Matrix3d();
		transMatrix.a00 = (float)Math.cos(theta);
		transMatrix.a01 = (float)Math.sin(theta);
		
		transMatrix.a10 = (float)(-1.0 * Math.sin(theta));
		transMatrix.a11 = (float)Math.cos(theta);
		this.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);		
	}
	
	//��Ը�������Z����ת
	public void rotateZ(float x,float y,float z,float theta)
	{
		Matrix3d transMatrix = new Matrix3d();
		transMatrix.translate(-x,-y,-z);
		transMatrix.rotateZ(theta);
		transMatrix.translate(x,y,z);
		this.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);		
	}
	
	//��Ը�������Z����ת,�������X,Y��Ľ�����ͬ
	public void rotateZ(Vertex3d vertex,float theta)
	{
		Matrix3d transMatrix = new Matrix3d();
		transMatrix.translate(-vertex.x,-vertex.y,-vertex.z);
		transMatrix.rotateZ(theta);
		transMatrix.translate(vertex.x,vertex.y,vertex.z);
		this.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);		

	}
	
	//��Y����ת
	public void rotateY(float theta)
	{
		float rad = (float)(Math.PI / 180.0);
		theta *= rad;
		Matrix3d transMatrix = new Matrix3d();
		transMatrix.a00 = (float)Math.cos(theta);
		transMatrix.a02 = (float)(-1.0 * Math.sin(theta));
		
		transMatrix.a20 = (float)Math.sin(theta);
		transMatrix.a22 = (float)Math.cos(theta);
		this.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);		
	}
	
	//��Y����ת
	public void rotateY(float x,float y,float z,float theta)
	{
		Matrix3d transMatrix = new Matrix3d();
		transMatrix.translate(-x,-y,-z);
		transMatrix.rotateY(theta);
		transMatrix.translate(x,y,z);
		this.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);		
	}
	
	//��Y����ת
	public void rotateY(Vertex3d vertex,float theta)
	{
		Matrix3d transMatrix = new Matrix3d();
		transMatrix.translate(-vertex.x,-vertex.y,-vertex.z);
		transMatrix.rotateY(theta);
		transMatrix.translate(vertex.x,vertex.y,vertex.z);
		this.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);		
	}
	
	//��X����ת
	public void rotateX(float theta)
	{
		float rad = (float)(Math.PI / 180.0);
		theta *= rad;
		Matrix3d transMatrix = new Matrix3d();
		transMatrix.a11 = (float)Math.cos(theta);
		transMatrix.a12 = (float)Math.sin(theta);
		
		transMatrix.a21 = (float)(-1.0 * Math.sin(theta));
		transMatrix.a22 = (float)Math.cos(theta);
		this.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);		
	}
	
	//��X����ת
	public void rotateX(float x,float y,float z,float theta)
	{
		Matrix3d transMatrix = new Matrix3d();
		transMatrix.translate(-x,-y,-z);
		transMatrix.rotateX(theta);
		transMatrix.translate(x,y,z);
		this.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);
	}
	
	//��X����ת
	public void rotateX(Vertex3d vertex,float theta)
	{
		Matrix3d transMatrix = new Matrix3d();
		transMatrix.translate(-vertex.x,-vertex.y,-vertex.z);
		transMatrix.rotateX(theta);
		transMatrix.translate(vertex.x,vertex.y,vertex.z);
		this.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);
	}
	
	//�����������ת
	public void rotate(Vertex3d vertex,Vector3d axis,float theta)
	{
		float rad = (float)(Math.PI / 180.0);
		theta *= rad;
		
		Vector3d vector = new Vector3d(axis.x,axis.y,axis.z);
		vector.unitize();
		float a = vector.x;
		float b = vector.y;
		float c = vector.z;
		
		float mag = b * b + c * c;
		Matrix3d transMatrix = new Matrix3d();
		if(mag <= 0.000001f)
		{
			transMatrix.translate(-vertex.x,-vertex.y,-vertex.z);
			
			//��ʱ,��X����ת
			theta *= a;	//because a = 1.0 or -1.0
			transMatrix.rotateX(vertex,theta);
			
			transMatrix.translate(vertex.x,vertex.y,vertex.z);
			this.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);		
			
			return;
		}
		
		/*
		��һ��,������ϵƽ������vertex;
		�ڶ�����AB������yozƽ���ͶӰ�������ɵ�ƽ����x����ת���Ƕ�Ϊ
				AB������yozƽ���ͶӰ����z��֮�нǣ�
		����������AB��y����ת�Ƕ�;
		���Ĳ���������z����תtheta;
		���岽�����߲����ֱ�����������һ������任��
		
		*/
		Matrix3d trans = new Matrix3d();
		mag = (float)Math.sqrt(mag);
		transMatrix.identity();
		transMatrix.translate(-vertex.x,-vertex.y,-vertex.z);
		
		
		//rotate X ,the cos and sin of the angle
		trans.a11 = c / mag;	trans.a12 = b / mag;
		trans.a21 = - b / mag;	trans.a22 = c / mag;
		
		transMatrix.multiply(trans,Matrix3d.RIGHT_MULTIPLIER);
		
		trans.identity();
		
		//rotate Y ,the cos and sin of the angle
		trans.a00 = mag;	trans.a02 = a;
		trans.a20 = -a;		trans.a22 = mag;
		transMatrix.multiply(trans,Matrix3d.RIGHT_MULTIPLIER);
		transMatrix.rotateZ(theta);
		
		
		trans.identity();
		trans.a00 = mag;	trans.a02 = -a;
		trans.a20 = a;		trans.a22 = mag;
		transMatrix.multiply(trans,Matrix3d.RIGHT_MULTIPLIER);
		
		trans.identity();
		trans.a11 = c / mag;	trans.a12 = -b / mag;
		trans.a21 = b / mag;	trans.a22 = c / mag;
		
		transMatrix.multiply(trans,Matrix3d.RIGHT_MULTIPLIER);
		transMatrix.translate(vertex.x,vertex.y,vertex.z);
		this.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);		
	}
	
	//���X�����
	public void skewX(float sy, float sz)
	{
		Matrix3d transMatrix = new Matrix3d();
		transMatrix.a10 = sy;
		transMatrix.a20 = sz;
		
		this.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);		
	}
	
	//���X�����
	public void skewX(float x,float y,float z,float sy, float sz)
	{
		Matrix3d transMatrix = new Matrix3d();
		transMatrix.translate(-x,-y,-z);
		transMatrix.skewX(sy,sz);
		transMatrix.translate(x,y,z);
		this.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);		
		
	}
	
	//���Y�����
	public void skewX(Vertex3d vertex,float sy, float sz)
	{
		Matrix3d transMatrix = new Matrix3d();
		transMatrix.translate(-vertex.x,-vertex.y,-vertex.z);
		transMatrix.skewX(sy,sz);
		transMatrix.translate(vertex.x,vertex.y,vertex.z);
		this.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);		
		
	}
	
	//���Y�����
	public void skewY(float sx, float sz)
	{
		Matrix3d transMatrix = new Matrix3d();
		transMatrix.a01 = sx;
		transMatrix.a21 = sz;
		
		this.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);		
	}
	
	//���Y�����
	public void skewY(float x,float y,float z,float sx, float sz)
	{
		Matrix3d transMatrix = new Matrix3d();
		transMatrix.translate(-x,-y,-z);
		transMatrix.skewY(sx,sz);
		transMatrix.translate(x,y,z);
		this.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);		
		
	}
	
	//���Z�����
	public void skewY(Vertex3d vertex,float sx, float sz)
	{
		Matrix3d transMatrix = new Matrix3d();
		transMatrix.translate(-vertex.x,-vertex.y,-vertex.z);
		transMatrix.skewY(sx,sz);
		transMatrix.translate(vertex.x,vertex.y,vertex.z);
		this.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);		
		
	}
	
	//���Z�����
	public void skewZ(float sx, float sy)
	{
		Matrix3d transMatrix = new Matrix3d();
		transMatrix.a02 = sx;
		transMatrix.a12 = sy;
		
		this.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);		
	}
	
	//���Z�����
	public void skewZ(float x,float y,float z,float sx, float sy)
	{
		Matrix3d transMatrix = new Matrix3d();
		transMatrix.translate(-x,-y,-z);
		transMatrix.skewZ(sx,sy);
		transMatrix.translate(x,y,z);
		this.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);		
		
	}
	
	//���Z�����
	public void skewZ(Vertex3d vertex,float sx, float sy)
	{
		Matrix3d transMatrix = new Matrix3d();
		transMatrix.translate(-vertex.x,-vertex.y,-vertex.z);
		transMatrix.skewZ(sx,sy);
		transMatrix.translate(vertex.x,vertex.y,vertex.z);
		this.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);		
	}
	
	//�����������XOYƽ�淴��
	public void reflexXOY()
	{
		Matrix3d transMatrix = new Matrix3d();
		transMatrix.scale(1.0f,1.0f,-1.0f);

		this.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);
	}
	
	//���YOZƽ�淴��
	public void reflexYOZ()
	{
		Matrix3d transMatrix = new Matrix3d();
		transMatrix.scale(-1.0f,1.0f,1.0f);

		this.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);
	}
	
	//���ZOXƽ�淴��
	public void reflexZOX()
	{
		Matrix3d transMatrix = new Matrix3d();
		transMatrix.scale(1.0f,-1.0f,1.0f);

		this.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);
	}
	
	//���X�ᷴ��
	public void reflexX()
	{
		Matrix3d transMatrix = new Matrix3d();
		transMatrix.scale(1.0f,-1.0f,-1.0f);

		this.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);
	}
	
	//���Y�ᷴ��
	public void reflexY()
	{
		Matrix3d transMatrix = new Matrix3d();
		transMatrix.scale(-1.0f,1.0f,-1.0f);

		this.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);
	}
	
	//���Z�ᷴ��
	public void reflexZ()
	{
		Matrix3d transMatrix = new Matrix3d();
		transMatrix.scale(-1.0f,-1.0f,1.0f);

		this.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);
	}
	
	//�����������ԭ�㷴��
	public void reflexO()
	{
		Matrix3d transMatrix = new Matrix3d();
		transMatrix.scale(-1.0f,-1.0f,-1.0f);
		this.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);
	}
	
	//�任�����û�
	public void replace(Matrix3d matrix)
	{
		this.identity();
		this.multiply(matrix,Matrix3d.RIGHT_MULTIPLIER);		
	}
	
	//����˷�,����˷������㽻����
	public void multiply(Matrix3d matrix,int position)
	{
		Matrix3d A = null;
		Matrix3d B = null;
		
		if(position == Matrix3d.LEFT_MULTIPLIER)
		{
			A = matrix;
			B = this;
		}
		else if(position == Matrix3d.RIGHT_MULTIPLIER )
		{
			A = this;
			B = matrix;
		}
		
		float c00 = A.a00 * B.a00 + A.a01 * B.a10 + A.a02 * B.a20;
		float c01 = A.a00 * B.a01 + A.a01 * B.a11 + A.a02 * B.a21;
		float c02 = A.a00 * B.a02 + A.a01 * B.a12 + A.a02 * B.a22;
		
		float c10 = A.a10 * B.a00 + A.a11 * B.a10 + A.a12 * B.a20;
		float c11 = A.a10 * B.a01 + A.a11 * B.a11 + A.a12 * B.a21;
		float c12 = A.a10 * B.a02 + A.a11 * B.a12 + A.a12 * B.a22;
		
		float c20 = A.a20 * B.a00 + A.a21 * B.a10 + A.a22 * B.a20;
		float c21 = A.a20 * B.a01 + A.a21 * B.a11 + A.a22 * B.a21;
		float c22 = A.a20 * B.a02 + A.a21 * B.a12 + A.a22 * B.a22;
		
		float c30 = A.a30 * B.a00 + A.a31 * B.a10 + A.a32 * B.a20 + B.a30;
		float c31 = A.a30 * B.a01 + A.a31 * B.a11 + A.a32 * B.a21 + B.a31;
		float c32 = A.a30 * B.a02 + A.a31 * B.a12 + A.a32 * B.a22 + B.a32;
		
		a00 = c00;	a01 = c01;	a02 = c02;
		a10 = c10;	a11 = c11;	a12 = c12;
		a20 = c20;	a21 = c21;	a22 = c22;
		a30 = c30;	a31 = c31;	a32 = c32;
	}

	//���������굽�۲�����ı任����
	public void toViewCoordinate(float theta,float phi,float r)
	{
		float rad = (float)(Math.PI / 180.0);
		float thta = theta * rad;
		float fai = phi * rad;
		
		Matrix3d transMatrix = new Matrix3d();
		float x = (float)(r * Math.cos(fai) * Math.cos(thta));
		float y = (float)(r * Math.cos(fai) * Math.sin(thta));
		float z = (float)(r * Math.sin(fai));
		
		transMatrix.translate(-x,-y,-z);
		transMatrix.rotateZ(90.0f - theta);
		transMatrix.rotateX(-90.0f - phi);
		
		Matrix3d toLeft = new Matrix3d();
		toLeft.a00 = -1.0f;
		transMatrix.multiply(toLeft,Matrix3d.RIGHT_MULTIPLIER);		
		
		/*
		//����ֻ�ı������ϵ�Ԫ��,�����������������������������佫���������
		transMatrix.scale(-1.0f,1.0f,1.0f);
		*/
		
		this.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);		
	}

	//��ȡ�任��ĵ�,һ������Ĺ��߹���
	public Vertex3d transform(Vertex3d vertex)
	{
		float x = vertex.x * a00 + vertex.y * a10 + vertex.z * a20 + a30;
		float y = vertex.x * a01 + vertex.y * a11 + vertex.z * a21 + a31;
		float z = vertex.x * a02 + vertex.y * a12 + vertex.z * a22 + a32;
		
		return (new Vertex3d(x,y,z));
	}
	
	//viewDistanceΪ�۲������Ļ����ϵ��ԭ��ľ���
	public Point perspective(Vertex3d vertex,float viewDistance)
	{
		Vertex3d leftCoord = this.transform(vertex);
		
		float xe = leftCoord.x;
		float ye = leftCoord.y;
		float ze = leftCoord.z;

		float sign = 1.0f;
		if(ze < 0.0f)sign = -1.0f;
		if(Math.abs(ze) <= 0.000001f) ze = sign * 0.0001f;
		
		int xs = (int)(xe * viewDistance / ze);
		int ys = (int)(ye * viewDistance / ze);
		
		ys = -ys;
		
		return (new Point(xs,ys));
	}
	
	/*
		.perspectiveWatch()----�÷���͸��ͶӰ�۲�����ϵ�µĵ�
		.vertexΪ�۲�����ϵ�µ�һ����.
	*/
	public Point perspectiveWatch(Vertex3d vertex,float viewDistance)
	{
			
		float xe = vertex.x;
		float ye = vertex.y;
		float ze = vertex.z;

		float sign = 1.0f;
		if(ze < 0.0f)sign = -1.0f;
		if(Math.abs(ze) <= 0.000001f) ze = sign * 0.0001f;
		
		int xs = (int)(xe * viewDistance / ze);
		int ys = (int)(ye * viewDistance / ze);
		
		ys = -ys;
		
		return (new Point(xs,ys));
	}
	
	
	
}
