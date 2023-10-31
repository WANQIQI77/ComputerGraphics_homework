

import java.awt.Point;
import java.awt.Graphics;
import java.awt.Color;
import java.util.Vector;

public class Cube
{
	//����,�ֱ��Ӧ��X,Y,Z��
	float width = 10.0f;		
	float length = 10.0f;
	float height = 10.0f;
	
	//���ζ���
	Vertex3d vertex[] = new Vertex3d[8];
	
	//����͸��֮��ĵ�
	Point viewPoint[] = new Point[8];
	
	//������˽ṹ
	Facet facet[] = new Facet[6];
	
	//�ߵ����˽ṹ
	Edge edge[] = new Edge[12];
	
	//���α任�ı任����
	Matrix3d transGeometry = new Matrix3d();

	//��8�����͸������
	Vertex3d vertex_to_ground[] = new Vertex3d[8];
	
	//�������Ƿ����������Ӱ
	boolean enable_shadow = false;
	
	public Cube(float width,float length,float height)
	{
		this.width = width;
		this.length = length;
		this.height = height;
		init();
		
	}
	
	private void init()
	{
		float w = this.width / 2.0f;
		float l = this.length / 2.0f;
		float h = this.height / 2.0f;
		
		//x����Ϊ���,y����Ϊ����,z����߶�
		
		vertex[0] = new Vertex3d(-w,-l,-h);
		vertex[1] = new Vertex3d(-w,l,-h);
		vertex[2] = new Vertex3d(-w,l,h);
		vertex[3] = new Vertex3d(-w,-l,h);
		vertex[4] = new Vertex3d(w,-l,-h);
		vertex[5] = new Vertex3d(w,l,-h);
		vertex[6] = new Vertex3d(w,l,h);
		vertex[7] = new Vertex3d(w,-l,h);
		
		//��¼ÿ�����������ڵ���,��Ȼ�������湲ͬһ������
		vertex[0].residence.addElement(new Integer(0));
		vertex[0].residence.addElement(new Integer(2));
		vertex[0].residence.addElement(new Integer(3));
		
		vertex[1].residence.addElement(new Integer(0));
		vertex[1].residence.addElement(new Integer(3));
		vertex[1].residence.addElement(new Integer(4));
		
		vertex[2].residence.addElement(new Integer(1));
		vertex[2].residence.addElement(new Integer(3));
		vertex[2].residence.addElement(new Integer(4));
		
		vertex[3].residence.addElement(new Integer(1));
		vertex[3].residence.addElement(new Integer(2));
		vertex[3].residence.addElement(new Integer(3));
		
		vertex[4].residence.addElement(new Integer(0));
		vertex[4].residence.addElement(new Integer(2));
		vertex[4].residence.addElement(new Integer(5));
		
		vertex[5].residence.addElement(new Integer(0));
		vertex[5].residence.addElement(new Integer(4));
		vertex[5].residence.addElement(new Integer(5));
		
		vertex[6].residence.addElement(new Integer(1));
		vertex[6].residence.addElement(new Integer(4));
		vertex[6].residence.addElement(new Integer(5));
		
		vertex[7].residence.addElement(new Integer(1));
		vertex[7].residence.addElement(new Integer(2));
		vertex[7].residence.addElement(new Integer(5));
		
		
		//����Ϊ����,����,����,����,����,ǰ��:

		facet[0] = new Facet4(0,1,5,4);
		facet[1] = new Facet4(3,7,6,2);
		facet[2] = new Facet4(3,0,4,7);
		facet[3] = new Facet4(0,3,2,1);
		facet[4] = new Facet4(1,2,6,5);
		facet[5] = new Facet4(4,5,6,7);
		
		//����߶���
		edge[0] = new Edge(0,1,0,3);
		edge[1] = new Edge(1,2,3,4);
		edge[2] = new Edge(2,3,1,3);
		edge[3] = new Edge(3,0,2,3);

		edge[4] = new Edge(3,7,1,2);
		edge[5] = new Edge(2,6,1,4);
		edge[6] = new Edge(1,5,0,4);
		edge[7] = new Edge(0,4,0,2);
		
		edge[8] = new Edge(4,5,0,5);
		edge[9] = new Edge(5,6,4,5);
		edge[10] = new Edge(6,7,1,5);
		edge[11] = new Edge(7,4,2,5);
		
		initFacetNormal();
		vertexNormal();
		
	}
	
	//��ʼ����ķ���.
	public void initFacetNormal()
	{
		facet[0].normal = new Vector3d(0.0f,0.0f,-1.0f);
		facet[1].normal = new Vector3d(0.0f,0.0f,1.0f);
		facet[2].normal = new Vector3d(0.0f,-1.0f,0.0f);
		facet[3].normal = new Vector3d(-1.0f,0.0f,0.0f);
		facet[4].normal = new Vector3d(0.0f,1.0f,0.0f);
		facet[5].normal = new Vector3d(1.0f,0.0f,0.0f);
	}

	//���㶥�㷨��
	public void vertexNormal()
	{
		for (int i = 0;i < 8;i++)
		{
			int index_0 = ((Integer)(vertex[i].residence.elementAt(0))).intValue();
			int index_1 = ((Integer)(vertex[i].residence.elementAt(1))).intValue();
			int index_2 = ((Integer)(vertex[i].residence.elementAt(2))).intValue();
			
			Vector3d normal = new Vector3d(facet[index_0].normal.x,facet[index_0].normal.y,facet[index_0].normal.z);
			normal = normal.add(facet[index_1].normal);
			normal = normal.add(facet[index_2].normal);
			normal = normal.numberMultiply(1.0f / 3.0f);
			normal.unitize();
			vertex[i].normal = normal;
		}
	}
	
	//���óߴ��С
	public void setSize(float width,float length,float height)
	{
		this.width = width;
		this.length = length;
		this.height = height;
	}
	
	
	//���õ�index��С����ɫ
	public void setFacetColor(int index,ColorFloat color)
	{
		ColorFloat clrFloat = new ColorFloat(color.red,color.green,color.blue,color.alpha);
		facet[index].color = clrFloat;
	}
	
	
	//���õ�index������ɫ
	public void setEdgeColor(int index,ColorFloat color)
	{
		ColorFloat clrFloat = new ColorFloat(color.red,color.green,color.blue,color.alpha);
		edge[index].color = clrFloat;
	}
	
	//���õ�index��������ɫ
	public void setVertexColor(int index,ColorFloat color)
	{
		ColorFloat clrFloat = new ColorFloat(color.red,color.green,color.blue,color.alpha);
		vertex[index].color = clrFloat;

	}

	//�÷���ʵ�ּ��α任
	
	public void transform(Matrix3d transMatrix)
	{
		//vertex�����澭���α任֮��ĵ�
		for(int i = 0;i < vertex.length;i++)
		{
			
			Vertex3d vTemp = transMatrix.transform(vertex[i]);
			
			vertex[i].x = vTemp.x;
			vertex[i].y = vTemp.y;
			vertex[i].z = vTemp.z;
				
		}
		
		transGeometry.multiply(transMatrix,Matrix3d.RIGHT_MULTIPLIER);
		
		//����������������Ӱ,��Ӧ�ü�¼���α任��Ķ���,�Ա㽫����͸�ӵ�������
		if(enable_shadow)
		{
			for(int i = 0;i < 8;i++)
			{
				vertex_to_ground[i] = new Vertex3d(0.0f,0.0f,0.0f);
				vertex_to_ground[i].x = vertex[i].x;
				vertex_to_ground[i].y = vertex[i].y;
				vertex_to_ground[i].z = vertex[i].z;
			}
		}
		
	}


	
	/*
	.��������õ�����:
		(1)�������굽�۲�����ı任��
		(2)͸��ͶӰ����ı任
	�Ľ����
	.toViewCoord��������Ϊһ���������굽�۲������
		�任�任���󣬾���������������任����
	.�õ���ķ��ߺ���Ŀɼ���
	.û�н����ߵĿɼ��ԵĴ���
	.����������Cube��transGeometry�ṩ����
		toViewCoord����Watcher�ṩ����Ϊ����һ��
		����������ֻ��һ���̶��Ĺ۲���
	*/
	public void perspective(Matrix3d toViewCoord,float screenDistance)
	{
		
		int i =0;

		Vertex3d leftCoord[] = new Vertex3d[8];
		
		//�۲�任
		for( i = 0;i < vertex.length ; i++)
				leftCoord[i] = toViewCoord.transform(vertex[i]);
		
		//����͸��
		for(i = 0;i < vertex.length ;i++)
			viewPoint[i] = toViewCoord.perspectiveWatch(leftCoord[i],screenDistance);

		//����С��ķ��ߺ���Ŀɼ���.
		simpleVisibilityTest(leftCoord);
	}
	
	/*
	����������صķ�������һ��������ȣ�
		�����϶���һ����͸�ӵ���Ļ�ϵĵ����ƽ��
		�Ĺ��ܣ�����������ϵ��(0,0,0)��һ���任��
		��Ļ�ϵ�viewCenter�㡣
	���ǻ��ƵĻ���.
	*/
	
	public void perspective(Matrix3d toViewCoord,float screenDistance,Point viewCenter)
	{
		
		int i =0;
		/*
			.Ϊ������Ŀɼ��Բ�����Ҳ��Ӧ�ù۲�����ϵ�ĵ�,�����ظ�����,���Ӧ��¼��Ϊ
				�м�������Ĺ۲�����ĵ�
		*/
		Vertex3d leftCoord[] = new Vertex3d[8];
		
		
		//�۲�任
		for( i = 0;i < vertex.length ; i++)
				leftCoord[i] = toViewCoord.transform(vertex[i]);
		
		//�任����Ļ����ϵ
		for(i = 0;i < vertex.length ;i++)
		{
			viewPoint[i] = toViewCoord.perspectiveWatch(leftCoord[i],screenDistance);
			viewPoint[i].translate(viewCenter.x,viewCenter.y);
		}
	

		//����С��ķ��ߺ���Ŀɼ���
		simpleVisibilityTest(leftCoord);
	}
	
	/*
	������صķ���,�ֲ���ͬ��
	��һ,�����������ɵ�,���ȼ��㾭�����α任��ĵ�,
		�ټ��㼸�α任��ĵ��ڹ۲�����ϵ�еĵ�,���Ž��õ�͸�ӵ�	
		��Ļ����ϵ,�������ӿڱ任
	�ڶ�,��֧����Ӱ�Ļ���,�����Դ洢���α任��ĵ�
	����,Ч������,��Ϊ��û���ظ�����

	*/
	public void perspective(Matrix3d toViewCoord,ViewFinder viewfinder)
	{
		int i = 0;
		
		Vertex3d leftCoord[] = new Vertex3d[8];
		
		//�����α任��ĵ�任���۲�����ϵ,��ʱvertex_clone��¼���۲�任�ĵ�
		for(i = 0;i < 8;i++)
			leftCoord[i] = toViewCoord.transform(vertex[i]);

		
		//�任����Ļ����ϵ
		for(i = 0;i < 8;i++)
			viewPoint[i] = toViewCoord.perspectiveWatch(leftCoord[i],viewfinder.screenDistance);
		
		//�����ӿڱ任
		for(i = 0;i < 8;i++)
			viewPoint[i] = viewfinder.viewport(viewPoint[i]);

		//ֱ�Ӵ�����������ϵ���۲�����ϵ�ĵ�,���пɼ��Բ���
		
		simpleVisibilityTest(leftCoord);
	}
	


	//�򵥵Ŀɼ��Բ���,Ӧ�÷���.�÷�������ƽ�淽��ʽ��
	public void simpleVisibilityTest(Vertex3d[] leftHandCoord)
	{
		Vertex3d leftCoord[] = new Vertex3d[8];
		int i =0;
		
		//������������ϵ
		for( i = 0;i < 8; i++)
			leftCoord[i] = leftHandCoord[i];
		
		for(i = 0;i < 6;i++)
		{
			//���㷨��,��ʵ�ǰ����ֹ�����������Ĳ��
			facet[i].normal = NormalCalculater.normal(
						leftCoord[((Facet4)facet[i]).index[0]],		
						leftCoord[((Facet4)facet[i]).index[1]],
						leftCoord[((Facet4)facet[i]).index[2]]);
			facet[i].visibility = VisibilityTester.tester(facet[i].normal);
		}
	}

	
	public void  render(Graphics g,int renderMode)
	{
		Renderer renderer = new Renderer();
		/*
			.��ʵ,���ζ���������ʱ�ڻ�������ʱ�ļ�����ʱ�Ѳ���Ҫ��,
				�����ζ��㺬�л�������,������ɫ��,�������Ӧ����Ϊһ����������ȥ
		*/
		renderer.render(g,viewPoint,facet,edge,vertex,renderMode);
	}
	
}
