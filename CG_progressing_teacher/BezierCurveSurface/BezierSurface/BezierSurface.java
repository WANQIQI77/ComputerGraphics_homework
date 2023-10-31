import java.awt.Point;
import java.awt.Graphics;
import java.awt.Color;
import java.util.Vector;

//���ദ��˫����Bezier����
public class BezierSurface extends Bezier 
{
	//����Ľ���,����ѧ���� + 1,��ʱ���Ƕ���ĸ���,��������ֻ�������׵����,�����ֵΪ4
	int uorder = 4;
	int vorder = 4;
	
	//ϸ���ȼ�,����Bezierϸ������,�˴������ǲ�����
	int tessellation = 0;
	
	//�ֶαƽ�����,ָ������ö��ٸ������ƽ�
	//u�����ĵȷֶ���
	int un = 20;
	//v�����ĵȷֶ���
	int vn = 20;

	//����һ���ռ��Bezier����,������ռ��Ƕ�ά��(u,v)�����ռ�,��ΧΪ[0.0f,1.0f]��[0.0f,1.0f]��ֱ��
	//���ǿ���ѡ����Ʋ�����[u1,u2]֮��Ķ�,u1,u2����Ϊ����
	float u1 = 0.0f;
	float u2 = 1.0f;

	//����Ҳ����ѡ����Ʋ�����[v1,v2]֮��Ķ�,v1,v2����Ϊ����
	float v1 = 0.0f;
	float v2 = 1.0f;

	
	//��������
	int control_type = this.BEZIER_MAP2_VERTEX_3;
	
	//���ڿ��Ƶ�,���Ƽ���λ�����ꡢ�������ߡ���ɫʱ��һ��Ҫ����������ʾ
	HomogeneousCoordinate controlPoints[][] = new HomogeneousCoordinate[4][4];
	
	//���α任�ı任����
	Matrix3d transGeometry = new Matrix3d();
	
	//�۲�任����
	Matrix3d transView = new Matrix3d();

	//ȡ����
	ViewFinder viewfinder = new ViewFinder();
	
	//�Ƿ��������
	boolean enable_lighting = false;
	
	//������ָ���ڲ໹�����,ȱʡ��,ָ�����
	int normal_direction = BEZIER_SURFACE_NORMAL_OUTSIDE;
	
	//�ֶαƽ���ļ��ζ���
	Vertex3d vertex[];
	
	//����͸��֮��ĵ�
	Point viewPoint[];
	
	//������˽ṹ
	Facet facet[];
	
	//�ߵ����˽ṹ
	Edge edge[];
	
	public BezierSurface(int un,int vn)	
	{
		this.un = un;
		this.vn = vn;
		
		vertex = new Vertex3d[(un + 1) * (vn + 1)];
		viewPoint = new Point[(un + 1) * (vn + 1)];
		facet = new Facet[2 * un * vn ];
		edge =	new Edge[3 * un * vn + un + vn];
		init();
	}
	

	//���ü��ζ���,
	public void setGeometryControlPoints(int control_type,Vertex3d[][] points)
	{
		
		this.control_type = control_type;
		
		//ѭ������i��ʾ��u����ѭ��,��j���ʾ��v����ѭ��
		//���Ƶ����а������ķ�ʽ����������,��uorder��.
		//����˳����������,u=0ʱ,��v�����vorder�����Ƶ����ι��ɵ�һ��,��������,�ɼ����Ƶ��������һ��Ϊu = 1ʱ,��v�ķ����vorder�����Ƶ����
		for(int i = 0;i < uorder;i++)
		{
			for(int j = 0;j < vorder;j++)
			{
				if(control_type == this.BEZIER_MAP2_VERTEX_3)
				{
					float x = points[i][j].x ;
					float y = points[i][j].y ;
					float z = points[i][j].z ;
				
					controlPoints[i][j] = new HomogeneousCoordinate(x,y,z,1.0f);
				}
				else
				{
					float ww = points[i][j].w;
					float sign = (ww < 0.0f) ? -1.0f : 1.0f;
					if(Math.abs(ww) < 0.000001f)//��w = 0 ʱ��ʾһ������Զ��
						ww = 0.000001f * sign;
					float x = points[i][j].x / ww;
					float y = points[i][j].y / ww;
					float z = points[i][j].z / ww;
				
					controlPoints[i][j] = new HomogeneousCoordinate(x,y,z,1.0f);
				}
			}
		}
		mathematicTessellateVertices();
	}

	//�������������Ƶ�
	public void setControlPoints(int control_type,HomogeneousCoordinate[][] points)
	{
		this.control_type = control_type;
		for(int i = 0;i < uorder;i++)
		{
			for(int j = 0;j < vorder;j++)
			{
				//�����ζ���
				if(control_type == this.BEZIER_MAP1_VERTEX_3 )
				{
					float x = points[i][j].x ;
					float y = points[i][j].y ;
					float z = points[i][j].z ;
					
					controlPoints[i][j] = new HomogeneousCoordinate(x,y,z,1.0f);

				}
				else if(control_type == this.BEZIER_MAP1_VERTEX_4)
				{
					float ww = points[i][j].w;
					float sign = (ww < 0.0f) ? -1.0f : 1.0f;
					if(Math.abs(ww) < 0.000001f)//��w = 0 ʱ��ʾһ������Զ��
						ww = 0.000001f * sign;
					float x = points[i][j].x / ww;
					float y = points[i][j].y / ww;
					float z = points[i][j].z / ww;
					
					controlPoints[i][j] = new HomogeneousCoordinate(x,y,z,1.0f);
				}
					
				//������������Ĵ���
				//.........
				else
					controlPoints[i][j] = new HomogeneousCoordinate(points[i][j].x,points[i][j].y,points[i][j].z,points[i][j].w);

			}
		}
		
		//��ȡ����
		if((control_type == this.BEZIER_MAP1_VERTEX_3) ||(control_type == this.BEZIER_MAP1_VERTEX_4))
			mathematicTessellateVertices();
		else
		{
			//Please deal with other situations
		}
	}
	
	//���÷���ָ��
	public void setNormalDirection(int direction)
	{
		this.normal_direction = direction;
	}
	
	//���ù۲�任����ȡ����,���α任���������ı仯���仯��,���Լ��α任������Щ������
	public void setSceneTransform(Matrix3d transView,ViewFinder viewfinder)
	{
		
		this.transView = transView;
		this.viewfinder = viewfinder;
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

	
	//��ȡ��ѧϸ����Ķ�����
	public int getVertexCount()
	{
		return ((un + 1) * (vn + 1));
	}
	
	//��ȡ��ѧϸ���������
	public int getFacetCount()
	{
		return (2 * un * vn);
	}
	
	//��ȡ��ѧϸ����ı���
	public int getEdgeCount()
	{
		return (3 * un * vn + un + vn);
	}
	
	public void init()
	{
		initFacets();
		initEdges();
	}
	
	private void  initFacets()
	{
		int i = 0;
		int j = 0;
	
		//i��v����,j��u����
		
		for(i = 0;i < vn;i++)
		{
			for(j = 0;j < un;j++)
			{
				int indexBase = i * (un + 1);
				facet[i * (2 * un) + 2 * j] = new Facet3(indexBase + j ,indexBase + j + 1,indexBase + un + j + 1);
				facet[i * (2 * un) + 2 * j + 1] = new Facet3(indexBase + j + 1,indexBase + un + j + 2,indexBase + un + j + 1);
			}
		}
	}
	
	private void initEdges()
	{
		//Edge�Ĺ��췽���е�һ�������͵ڶ����������������ߵ��������ζ˵�,
		//�����͵��ĸ�������˵����������������������
		
		int i = 0;
		int j = 0;
	
		//i��v����,j��u����
		
		//��һ,�����u���������ϵıߵĶ���
		for(i = 0;i < vn + 1;i++)
		{
			for(j = 0;j < un;j++)
			{
				if(i == 0)//v = v1�߽�
				{
					edge[j] = new Edge(j,j + 1,2 * j,2 * j); 
				}
				else if(i == vn)//v = v2�߽�
				{
					int indexFacet = ( vn - 1) * 2 * un;
					int indexVertex = vn * (un + 1);
					int indexEdge = vn * un;
					
					edge[indexEdge + j] = new Edge(indexVertex + j,
												   indexVertex + j + 1,
												   indexFacet + 2 * j + 1,
												   indexFacet + 2 * j + 1);
													
				}
				else
				{
					edge[i * un + j] = new Edge(i * (un + 1) + j,
												i * (un + 1) + j + 1,
												(i - 1) * (2 * un) + 2 * j + 1,
												i * (2 * un) + 2 * j);
				}

			}
		}
		
		//�ڶ�,�����v�����ϵıߵĶ���
		
		//�ߵĻ���
		int indexBase = un * (vn + 1);
			
		for(j = 0;j < (un + 1); j++)
		{
			for(i = 0;i < vn; i++)
			{
				if(j == 0)//u = u1�߽�
				{
					edge[indexBase + i] = new Edge(i * (un + 1),
												   (i + 1) * (un + 1),
												   i * (2 * un),i * (2 * un));
				}
				else if(j == un)//u = u2�߽�
				{
					int indexEdge = indexBase + un * vn;
					edge[indexEdge + i] = new Edge((i + 1) * (un + 1) - 1,
												   (i + 2) * (un + 1) - 1,
												   (i + 1) * (2 * un) - 1,
												   (i + 1) * (2 * un) - 1);
				}
				else
				{
					edge[indexBase + j * vn + i] = new Edge( i * (un + 1) + j ,
															 (i + 1 ) * (un + 1) + j,
															 i * (2 * un) + 2 * j - 1,
															 i * (2 * un) + 2 * j );
				
				}
			}
		}
		
		//����,������u�����v�����������ɵ�С�ı��εĶԽ���,����un * vn��
	
		//�ߵĻ���
		indexBase = un * (vn + 1) + vn * (un + 1);
	
		for(i = 0;i < vn;i++)
		{
			for(j = 0;j < un;j++)
			{
				edge[indexBase + i * un + j] = new Edge(i * (un + 1) + j + 1,
														(i + 1) * (un + 1) + j,
														i * (2 * un) + 2 * j,
														i * (2 * un) + 2 * j + 1);
			
			}
		}
		
	}
	
	
	
	//��ά����ӳ��
	public void mapGird2f(int un,float u1,float u2,int vn,float v1,float v2)
	{
		this.un = un;
		float u = u1 < 0.0f ? 0.0f : (u1 > 1.0f ? 1.0f : u1);
		this.u1 = u;
		u = u2 < 0.0f ? 0.0f : (u2 > 1.0f ? 1.0f : u2);
		this.u2 = u;
		
		this.vn = vn;
		float v = v1 < 0.0f ? 0.0f : (v1 > 1.0f ? 1.0f : v1);
		this.v1 = v;
		v = v2 < 0.0f ? 0.0f : (v2 > 1.0f ? 1.0f : v2);
		this.v2 = v;
	
	}
	
	/*
	 ��ά������
	control_type----��ʾ���Ƶ�Ŀ�������
	[u1,u2]---------u����ӳ������
	[v1,v2]---------v����ӳ������
	ustride---------��һ�����Ƶ㵽��һ�����Ƶ���������,����Ϊ����3,
					����Ҫ��ʾ����ƫ����,��ʱ,��������������һ�ֻ���,������ѡ��n+1�����Ƶ��е�һ���Ӽ���Ϊ���Ƶ�,���ԭ����(������)�Ĳ���ͼ��
	vstride---------�����ƽ���
	uorder-----------u�����ϵĽ���,���͵�,����ʾ���Ƶ�ĸ���
	vorder-----------v�����ϵĽ���,���͵�,����ʾ���Ƶ�ĸ���
	ע��:�÷�����Ҫ����Ը�ά��,Ҫ��һ���������ζ���,��������,���ߺ���ɫ��ͳһBezier����ģ��,���ཫ�Ǻ��Ӵ��,��Ҫ��֪ʶҲ�Ǻܶ��,����
	*/
	public void map(int control_type,float u1,float u2,int ustride,int uorder,float v1,float v2,int vstride,int vorder,HomogeneousCoordinate[][] points)
	{

		this.control_type = control_type;
		this.u1 = u1;
		this.u2 = u2;
		this.uorder = uorder;
		
		this.v1 = v1;
		this.v2 = v2;
		this.vorder = vorder;
		
		
		for(int i = 0;i < uorder;i++)
		for(int j = 0;j < vorder;j++)
			controlPoints[i][j] = new HomogeneousCoordinate(points[i][j].x,points[i][j].y,points[i][j].z,points[i][j].w);
		

		//���Ž�����������........
	}
	
	
	/*
	˫����bezier����,
		��ò��ø�ά��������,����������Ƚ���һ����ά����,�������ǴӸ�ά��������ȡά��������
		��Ϊֻ������ά����,�ʴ˴���������ά������,��ά��������ά������,����һЩ����Ĵ���,����

	*/
	public float threeOrderSpline(float[] u,float[] v,int index_xyz)
	{
		float bernstein_u[] = {0.0f,0.0f,0.0f,0.0f};
		float bernstein_v[] = {0.0f,0.0f,0.0f,0.0f};
		
		//Bernstein����ʽ����:u
		bernstein_u[0] = -1.0f * u[0] + 3.0f * u[1] - 3.0f * u[2] + u[3];
		bernstein_u[1] =  3.0f * u[0] - 6.0f * u[1] + 3.0f * u[2];
		bernstein_u[2] = -3.0f * u[0] + 3.0f * u[1];
		bernstein_u[3] = u[0];
		
		//Bernstein����ʽ����:v
		bernstein_v[0] = -1.0f * v[0] + 3.0f * v[1] - 3.0f * v[2] + v[3];
		bernstein_v[1] =  3.0f * v[0] - 6.0f * v[1] + 3.0f * v[2];
		bernstein_v[2] = -3.0f * v[0] + 3.0f * v[1];
		bernstein_v[3] = v[0];
		
		//���Ƶ������˳����������,u=0ʱ,��v�����vorder�����Ƶ����ι��ɵ�һ��,��������,�ɼ����Ƶ��������һ��Ϊu = 1ʱ,��v�ķ����vorder�����Ƶ����		
		
		//ֻ������uorder = 4 �� vorder = 4������
		
		
		//����������bernstein_u�Ϳ��Ƶ����ĳ˻�
		//iΪ�к�,jΪ�к�
		int i = 0;
		int j = 0;
		float u_points[] = {0.0f,0.0f,0.0f,0.0f};
		for(j = 0;j < vorder;j++)
		{
			for(i = 0;i < uorder;i++)
			{
				float x_y_z = 0.0f;
				//����x����
				if(index_xyz == 0)
					x_y_z = this.controlPoints[i][j].x; 
				//����y����
				else if(index_xyz == 1)
					x_y_z = this.controlPoints[i][j].y; 
				//����z����
				else if(index_xyz == 2)
					x_y_z = this.controlPoints[i][j].z;
				
				 u_points[j] += (bernstein_u[i] * x_y_z);
				
			}
			
		}
		
		//��������ֵ,����u_points������bernstein_v�ĵ��
		float fValue = 0.0f;
		for(i = 0;i < vorder;i++)
			fValue += u_points[i] * bernstein_v[i];
		return (fValue);
	}
	
	//���м��ι���,�������.�Զ��������ѧϸ��
	public void mathematicTessellateVertices()
	{
				
		float u = u1;
		
		float delta_u = (u2 - u1) / un;
		
		float v = v1;
		
		float delta_v = (v2 - v1) / vn;
		
		int i = 0;
		int j = 0;
		//i��v����,j��u����
		
		for(i = 0; i < vn + 1;i++)
		{
			u = u1;
			for(j = 0;j < un + 1;j++)
			{
				float u_vector[] = {u * u * u,u * u,u,1.0f};
				
				float v_vector[] = {v * v * v,v * v,v,1.0f};
				
				float x = this.threeOrderSpline(u_vector,v_vector,0);
				float y = this.threeOrderSpline(u_vector,v_vector,1);
				float z = this.threeOrderSpline(u_vector,v_vector,2);
				
				vertex[i * (un + 1) + j] = new Vertex3d(x,y,z);
				
				//���Ҫ���й��վͱ�����㶥��ķ���
				if(this.enable_lighting == true)
				{
					Vector3d normal = obtainBezierNormal(u,v);
					
					vertex[i * (un + 1) + j].normal = normal;
				}
				
				u += delta_u;
			}
			
			v += delta_v;
		}
		
		resideInFacets();
		
	}
	
	//��ȡ���������Bezier����
	public Vector3d obtainBezierNormal(float u,float v)
	{
		//����΢�ּ��εļ��㷽��
		//��Ҫ��������ƫ����
		
		float x_u,x_v;
		float y_u,y_v;
		float z_u,z_v;
		
		//׼���ĸ�����
		
		//������(u.u.u,u.u,u,1);
		float u_source[] = {u * u * u,u * u,u,1.0f};
	
		//����������(����)�ĵ���
		float u_derivation[] = {3.0f * u * u,2.0f * u,1.0f,0.0f};
		
		//������(v.v.v,v.v,v,1);
		float v_source[] = {v * v * v,v * v,v,1.0f};
	
		//����������(����)�ĵ���
		float v_derivation[] = {3.0f * v * v,2.0f * v,1.0f,0.0f};
		
		//x��u������ƫ����
		x_u = threeOrderSpline(u_derivation,v_source,0);
		
		//x��v������ƫ����
		x_v = threeOrderSpline(u_source,v_derivation,0);
		
		//y��u������ƫ����
		y_u = threeOrderSpline(u_derivation,v_source,1);
		
		//y��v������ƫ����
		y_v = threeOrderSpline(u_source,v_derivation,1);
		
		//z��u������ƫ����
		z_u = threeOrderSpline(u_derivation,v_source,2);
		
		//z��v������ƫ����
		z_v = threeOrderSpline(u_source,v_derivation,2);
		
		float x = y_u * z_v - z_u * y_v;
		float y = z_u * x_v - x_u * z_v;
		float z = x_u * y_v - y_u * x_v;
		
		Vector3d vector = new Vector3d(x,y,z);
		
		vector.unitize();
		
		//�������ָ���ڲ�,���߷���
		if(this.normal_direction == Bezier.BEZIER_SURFACE_NORMAL_INSIDE )
		{
			vector.x *= -1.0f;
			vector.y *= -1.0f;
			vector.z *= -1.0f;
		}
		
		return	(vector);
	}

	
	//ȷ��һ������פ������Щ����
	private void resideInFacets()
	{
		int i = 0;
		int j = 0;
		
		//i��v����,j��u����

		//�ĸ��ǵ�
		
		//v = v1,u = u1;
		vertex[0].residence.addElement(new Integer(0));
		
		//v = v2,u = u2;
		vertex[(vn + 1) * (un + 1) - 1].residence.addElement(new Integer(2 * vn * un - 1));
		
		//v = v1,u = u2;
		vertex[un].residence.addElement( new Integer(2 * un - 2));
		vertex[un].residence.addElement( new Integer(2 * un - 1));
		
		//v = v2,u = u1;
		vertex[vn * (un + 1)].residence .addElement(new Integer((vn - 1) * (2 * un)));
		vertex[vn * (un + 1)].residence .addElement(new Integer((vn - 1) * (2 * un) + 1));
		
		//v=v1�߽��ϵĵ�
		for(j = 1; j < un;j++)
		{
			vertex[j].residence.addElement(new Integer(2 * j - 2 ));
			vertex[j].residence.addElement(new Integer(2 * j - 1 ));
			vertex[j].residence.addElement(new Integer(2 * j ));
		}
		
		//v=v2�߽��ϵĵ�
		int vertexBase = vn * (un + 1);
		int facetBase = (vn - 1) * (2 * un);
		for(j = 1; j < un;j++)
		{
			vertex[vertexBase + j].residence.addElement(new Integer(facetBase + 2 * j - 1 ));
			vertex[vertexBase + j].residence.addElement(new Integer(facetBase + 2 * j ));
			vertex[vertexBase + j].residence.addElement(new Integer(facetBase + 2 * j + 1 ));
		}
		
		//u =u1�߽��ϵĵ�
		for(i = 1; i < vn;i++)
		{
			vertex[i * (un + 1)].residence.addElement(new Integer((i - 1) * (2 * un )));
			vertex[i * (un + 1)].residence.addElement(new Integer((i - 1) * (2 * un) + 1 ));
			vertex[i * (un + 1)].residence.addElement(new Integer(i * (2 * un )));
		}
		
		//u =u2�߽��ϵĵ�
		for(i = 1; i < vn;i++)
		{
			vertex[(i + 1) * (un + 1) - 1].residence.addElement(new Integer(i * (2 * un) - 1));
			vertex[(i + 1) * (un + 1) - 1].residence.addElement(new Integer((i + 1) * (2 * un) - 2));
			vertex[(i + 1) * (un + 1) - 1].residence.addElement(new Integer((i + 1) * (2 * un) - 1));
		}
		
		
		//�Ǳ߽��
		for(i = 1;i < vn;i++)
		{
			for(j = 1;j < un;j++)
			{
				vertex[ i * (un + 1) + j].residence.addElement(new Integer((i - 1) * (2 * un) + 2 * j - 1 ));
				vertex[ i * (un + 1) + j].residence.addElement(new Integer((i - 1) * (2 * un) + 2 * j));
				vertex[ i * (un + 1) + j].residence.addElement(new Integer((i - 1) * (2 * un) + 2 * j + 1 ));
				vertex[ i * (un + 1) + j].residence.addElement(new Integer(i * (2 * un) + 2 * j - 2 ));
				vertex[ i * (un + 1) + j].residence.addElement(new Integer(i * (2 * un) + 2 * j - 1 ));
				vertex[ i * (un + 1) + j].residence.addElement(new Integer(i * (2 * un) + 2 * j ));
			}
		}
	}
	
	//��ķ���.
	public void facetNormal()
	{
		int total = 2 * un * vn;
		for(int i = 0; i < total;i++)
		{
			Facet3 facet_3 = new Facet3(0,0,0);
						
			facet_3 = (Facet3)facet[i];
				
				facet[i].normal = NormalCalculater.normal(vertex[facet_3.index[0]],
									 vertex[facet_3.index[1]],vertex[facet_3.index[2]]);
			
		}
	}

	//Ϊ������һ���ӿ�
	//���㶥�㷨��,����������б��κ�,����ķ���Ӧ������ķ���������,����������ѧ��ʽ������
	public void vertexNormal()
	{
		int total = (un + 1) * (vn + 1);
		for (int i = 0;i < total;i++)
		{
			int length = 0;
			length = vertex[i].residence.size();
			
			Vector3d normal = new Vector3d(0.0f,0.0f,0.0f);
			for(int j = 0;j < length;j++)
			{
				int index = ((Integer)(vertex[i].residence.elementAt(j))).intValue();
				normal = normal.add(facet[index].normal);
				
			}
			
			if(length == 0)length = 1;
			normal = normal.numberMultiply(1.0f / length);
			normal.unitize();
			vertex[i].normal = normal;
		}
	}
	
	
	//������Ŀɼ��Բ���
	//�򵥵Ŀɼ��Բ���,�÷�������ƽ�淽��ʽ��
	//leftCoordΪ�任���۲�����ϵ(��������ϵ)��Ķ���
	public void facetsVisibilityTest(Vertex3d leftCoord[])
	{
		
		for(int i = 0;i < facet.length ;i++)
		{
			((Facet3)facet[i]).visibility = VisibilityTester.tester
					(leftCoord[((Facet3)facet[i]).index[0]],
					leftCoord[((Facet3)facet[i]).index[1]],
					leftCoord[((Facet3)facet[i]).index[2]]);
			
		}
	}

	/*
		�ߵĿɼ��Բ��Է���:
		�÷�����ǰ���ǽ�������Ŀɼ��Բ���,���ҽ���������͹������,����Bezier������ܻ�������,��ΪBezier���治һ����͹��,
		���Ǽ򵥵�����һ����������.�����������漰����ı��,���ǲ�������һ��ͼ��ϵͳ!
		
	*/
	public void edgesVisibilityTest()
	{
		int indexBase = un * (vn + 1) + vn * (un + 1);
		int i = 0;
		
		//�����һ�������ڵ��������Ϊ���ɼ���,��ñ�ҲΪ���ɼ���
		for(i = 0; i < indexBase; i++)
		{
			boolean bFacet_1 = facet[edge[i].residence[0]].visibility ;
			boolean bFacet_2 = facet[edge[i].residence[1]].visibility ;
			edge[i].visibility = (bFacet_1 || bFacet_2);
		}
		
		//������һ���ռ��ı��εĶԽ��������ɵı�,��Ҫ�����������,
		//��,�����������涼�ɼ�ʱ,���ǲ��ɼ���;�������ڵ����������ɼ�ʱ,����ȻҲ�ǲ��ɼ���;��һ����ɼ�,����һ�����ɼ�ʱ,�ԽǱ�Ӧ�ÿɼ�
		//����Ҫ��Ϊ��������׼����,��Ϊ�������ı��������������߿�ģ��,������������������������
		//���ⲻӰ����Ļ���,��Ļ�����Ȼ����������С��
		for(i = indexBase; i < edge.length;i++)
		{
			boolean bFacet_1 = facet[edge[i].residence[0]].visibility ;
			boolean bFacet_2 = facet[edge[i].residence[1]].visibility ;
			edge[i].visibility = (bFacet_1 ^ bFacet_2);
		}
	
	}

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
	}
	
	public void perspective()
	{
		
		/*
			.Ϊ������Ŀɼ��Բ�����Ҳ��Ӧ�ù۲�����ϵ�ĵ�,�����ظ�����,���Ӧ��¼��Ϊ
				�м�������Ĺ۲�����ĵ�
		*/
		Vertex3d leftCoord[] = new Vertex3d[(vn + 1) * (un + 1)];
		
		
		int i =0;
		
		//������������ϵ,���۲�����ϵ
		for( i = 0;i < vertex.length ; i++)
			leftCoord[i] = transView.transform(vertex[i]);
		
		//�任����Ļ����ϵ
		for(i = 0;i < vertex.length ;i++)
			viewPoint[i] = transView.perspectiveWatch(leftCoord[i],viewfinder.screenDistance);
		
		//�����ӿڱ任
		for(i = 0;i < vertex.length ;i++)
			viewPoint[i] = viewfinder.viewport(viewPoint[i]);
		
		//��Ŀɼ��Բ���
		facetsVisibilityTest(leftCoord);
		
		//�ߵĿɼ��Բ���
		edgesVisibilityTest();
		
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
