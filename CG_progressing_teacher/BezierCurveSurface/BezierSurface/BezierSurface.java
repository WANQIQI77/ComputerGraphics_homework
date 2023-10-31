import java.awt.Point;
import java.awt.Graphics;
import java.awt.Color;
import java.util.Vector;

//该类处理双三次Bezier曲面
public class BezierSurface extends Bezier 
{
	//曲面的阶数,即数学阶数 + 1,此时就是顶点的个数,由于我们只处理三阶的情况,因而该值为4
	int uorder = 4;
	int vorder = 4;
	
	//细化等级,采用Bezier细化方法,此处，我们不处理
	int tessellation = 0;
	
	//分段逼近精度,指曲面采用多少个面来逼近
	//u参数的等分段数
	int un = 20;
	//v参数的等分段数
	int vn = 20;

	//对于一个空间的Bezier曲面,其参数空间是二维的(u,v)参数空间,范围为[0.0f,1.0f]与[0.0f,1.0f]的直积
	//我们可以选择绘制参数在[u1,u2]之间的段,u1,u2必须为正数
	float u1 = 0.0f;
	float u2 = 1.0f;

	//我们也可以选择绘制参数在[v1,v2]之间的段,v1,v2必须为正数
	float v1 = 0.0f;
	float v2 = 1.0f;

	
	//控制类型
	int control_type = this.BEZIER_MAP2_VERTEX_3;
	
	//对于控制点,控制几何位置坐标、纹理、法线、颜色时，一般要用齐次坐标表示
	HomogeneousCoordinate controlPoints[][] = new HomogeneousCoordinate[4][4];
	
	//几何变换的变换矩阵
	Matrix3d transGeometry = new Matrix3d();
	
	//观察变换矩阵
	Matrix3d transView = new Matrix3d();

	//取景器
	ViewFinder viewfinder = new ViewFinder();
	
	//是否允许光照
	boolean enable_lighting = false;
	
	//法线是指向内侧还是外侧,缺省地,指向外侧
	int normal_direction = BEZIER_SURFACE_NORMAL_OUTSIDE;
	
	//分段逼近后的几何顶点
	Vertex3d vertex[];
	
	//经过透视之后的点
	Point viewPoint[];
	
	//面的拓扑结构
	Facet facet[];
	
	//边的拓扑结构
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
	

	//设置几何顶点,
	public void setGeometryControlPoints(int control_type,Vertex3d[][] points)
	{
		
		this.control_type = control_type;
		
		//循环变量i表示按u参数循环,而j则表示按v参数循环
		//控制点阵列按递增的方式进行行排列,共uorder行.
		//排列顺序是这样的,u=0时,按v方向的vorder个控制点依次构成第一行,按此类推,可见控制点矩阵的最后一行为u = 1时,按v的方向的vorder个控制点组成
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
					if(Math.abs(ww) < 0.000001f)//当w = 0 时表示一个无穷远点
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

	//设置齐次坐标控制点
	public void setControlPoints(int control_type,HomogeneousCoordinate[][] points)
	{
		this.control_type = control_type;
		for(int i = 0;i < uorder;i++)
		{
			for(int j = 0;j < vorder;j++)
			{
				//处理几何顶点
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
					if(Math.abs(ww) < 0.000001f)//当w = 0 时表示一个无穷远点
						ww = 0.000001f * sign;
					float x = points[i][j].x / ww;
					float y = points[i][j].y / ww;
					float z = points[i][j].z / ww;
					
					controlPoints[i][j] = new HomogeneousCoordinate(x,y,z,1.0f);
				}
					
				//进行其它情况的处理
				//.........
				else
					controlPoints[i][j] = new HomogeneousCoordinate(points[i][j].x,points[i][j].y,points[i][j].z,points[i][j].w);

			}
		}
		
		//获取顶点
		if((control_type == this.BEZIER_MAP1_VERTEX_3) ||(control_type == this.BEZIER_MAP1_VERTEX_4))
			mathematicTessellateVertices();
		else
		{
			//Please deal with other situations
		}
	}
	
	//设置法线指向
	public void setNormalDirection(int direction)
	{
		this.normal_direction = direction;
	}
	
	//设置观察变换矩阵、取景器,几何变换是随着外界的变化而变化的,所以几何变换矩阵不再些处设置
	public void setSceneTransform(Matrix3d transView,ViewFinder viewfinder)
	{
		
		this.transView = transView;
		this.viewfinder = viewfinder;
	}

	//设置第index个小面颜色
	public void setFacetColor(int index,ColorFloat color)
	{
		ColorFloat clrFloat = new ColorFloat(color.red,color.green,color.blue,color.alpha);
		facet[index].color = clrFloat;
	}
	
	
	//设置第index条边颜色
	public void setEdgeColor(int index,ColorFloat color)
	{
		ColorFloat clrFloat = new ColorFloat(color.red,color.green,color.blue,color.alpha);
		edge[index].color = clrFloat;
		
	}
	
	//设置第index个顶点颜色
	public void setVertexColor(int index,ColorFloat color)
	{
		ColorFloat clrFloat = new ColorFloat(color.red,color.green,color.blue,color.alpha);
		vertex[index].color = clrFloat;
	}

	
	//获取数学细化后的顶点数
	public int getVertexCount()
	{
		return ((un + 1) * (vn + 1));
	}
	
	//获取数学细化后的面数
	public int getFacetCount()
	{
		return (2 * un * vn);
	}
	
	//获取数学细化后的边数
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
	
		//i按v方向,j按u方向
		
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
		//Edge的构造方法中第一个参数和第二个参数定义这条边的两个几何端点,
		//第三和第四个参数则说明这条边与哪两个面相邻
		
		int i = 0;
		int j = 0;
	
		//i按v方向,j按u方向
		
		//第一,完成在u参数方向上的边的定义
		for(i = 0;i < vn + 1;i++)
		{
			for(j = 0;j < un;j++)
			{
				if(i == 0)//v = v1边界
				{
					edge[j] = new Edge(j,j + 1,2 * j,2 * j); 
				}
				else if(i == vn)//v = v2边界
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
		
		//第二,完成在v方向上的边的定义
		
		//边的基数
		int indexBase = un * (vn + 1);
			
		for(j = 0;j < (un + 1); j++)
		{
			for(i = 0;i < vn; i++)
			{
				if(j == 0)//u = u1边界
				{
					edge[indexBase + i] = new Edge(i * (un + 1),
												   (i + 1) * (un + 1),
												   i * (2 * un),i * (2 * un));
				}
				else if(j == un)//u = u2边界
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
		
		//第三,定义由u方向和v方向的曲线组成的小四边形的对角线,共有un * vn条
	
		//边的基数
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
	
	
	
	//二维网格映射
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
	 二维评价器
	control_type----表示控制点的控制类型
	[u1,u2]---------u参数映射区间
	[v1,v2]---------v参数映射区间
	ustride---------上一个控制点到下一个控制点间的数据量,至少为整数3,
					它主要表示数据偏移量,此时,它给了我们这样一种机会,即可以选择n+1个控制点中的一个子集作为控制点,输出原曲线(或曲面)的部分图形
	vstride---------有类似解释
	uorder-----------u参数上的阶数,典型地,它表示控制点的个数
	vorder-----------v参数上的阶数,典型地,它表示控制点的个数
	注意:该方法主要是针对高维的,要作一个包括几何顶点,纹理坐标,法线和颜色的统一Bezier计算模型,该类将是很庞大的,需要的知识也是很多的,从略
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
		

		//接着进行其它处理........
	}
	
	
	/*
	双三次bezier样条,
		最好采用高维矩阵运算,因而必须事先建立一个高维矩阵,这里我们从高维矩阵中提取维矩阵运算
		因为只处理四维情形,故此处不给出高维矩阵类,高维矩阵由于维数不定,故有一些额外的处理,从略

	*/
	public float threeOrderSpline(float[] u,float[] v,int index_xyz)
	{
		float bernstein_u[] = {0.0f,0.0f,0.0f,0.0f};
		float bernstein_v[] = {0.0f,0.0f,0.0f,0.0f};
		
		//Bernstein多项式向量:u
		bernstein_u[0] = -1.0f * u[0] + 3.0f * u[1] - 3.0f * u[2] + u[3];
		bernstein_u[1] =  3.0f * u[0] - 6.0f * u[1] + 3.0f * u[2];
		bernstein_u[2] = -3.0f * u[0] + 3.0f * u[1];
		bernstein_u[3] = u[0];
		
		//Bernstein多项式向量:v
		bernstein_v[0] = -1.0f * v[0] + 3.0f * v[1] - 3.0f * v[2] + v[3];
		bernstein_v[1] =  3.0f * v[0] - 6.0f * v[1] + 3.0f * v[2];
		bernstein_v[2] = -3.0f * v[0] + 3.0f * v[1];
		bernstein_v[3] = v[0];
		
		//控制点的排列顺序是这样的,u=0时,按v方向的vorder个控制点依次构成第一行,按此类推,可见控制点矩阵的最后一行为u = 1时,按v的方向的vorder个控制点组成		
		
		//只适用于uorder = 4 且 vorder = 4的情形
		
		
		//计算行向量bernstein_u和控制点矩阵的乘积
		//i为行号,j为列号
		int i = 0;
		int j = 0;
		float u_points[] = {0.0f,0.0f,0.0f,0.0f};
		for(j = 0;j < vorder;j++)
		{
			for(i = 0;i < uorder;i++)
			{
				float x_y_z = 0.0f;
				//计算x分量
				if(index_xyz == 0)
					x_y_z = this.controlPoints[i][j].x; 
				//计算y分量
				else if(index_xyz == 1)
					x_y_z = this.controlPoints[i][j].y; 
				//计算z分量
				else if(index_xyz == 2)
					x_y_z = this.controlPoints[i][j].z;
				
				 u_points[j] += (bernstein_u[i] * x_y_z);
				
			}
			
		}
		
		//计算最后的值,向量u_points和向量bernstein_v的点积
		float fValue = 0.0f;
		for(i = 0;i < vorder;i++)
			fValue += u_points[i] * bernstein_v[i];
		return (fValue);
	}
	
	//进行几何构造,顶点计算.对顶点进行数学细化
	public void mathematicTessellateVertices()
	{
				
		float u = u1;
		
		float delta_u = (u2 - u1) / un;
		
		float v = v1;
		
		float delta_v = (v2 - v1) / vn;
		
		int i = 0;
		int j = 0;
		//i按v方向,j按u方向
		
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
				
				//如果要进行光照就必须计算顶点的法线
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
	
	//获取单个顶点的Bezier法线
	public Vector3d obtainBezierNormal(float u,float v)
	{
		//采用微分几何的计算方法
		//需要计算六个偏导数
		
		float x_u,x_v;
		float y_u,y_v;
		float z_u,z_v;
		
		//准备四个向量
		
		//行向量(u.u.u,u.u,u,1);
		float u_source[] = {u * u * u,u * u,u,1.0f};
	
		//上述行向量(矩阵)的导数
		float u_derivation[] = {3.0f * u * u,2.0f * u,1.0f,0.0f};
		
		//行向量(v.v.v,v.v,v,1);
		float v_source[] = {v * v * v,v * v,v,1.0f};
	
		//上述行向量(矩阵)的导数
		float v_derivation[] = {3.0f * v * v,2.0f * v,1.0f,0.0f};
		
		//x对u参数的偏导数
		x_u = threeOrderSpline(u_derivation,v_source,0);
		
		//x对v参数的偏导数
		x_v = threeOrderSpline(u_source,v_derivation,0);
		
		//y对u参数的偏导数
		y_u = threeOrderSpline(u_derivation,v_source,1);
		
		//y对v参数的偏导数
		y_v = threeOrderSpline(u_source,v_derivation,1);
		
		//z对u参数的偏导数
		z_u = threeOrderSpline(u_derivation,v_source,2);
		
		//z对v参数的偏导数
		z_v = threeOrderSpline(u_source,v_derivation,2);
		
		float x = y_u * z_v - z_u * y_v;
		float y = z_u * x_v - x_u * z_v;
		float z = x_u * y_v - y_u * x_v;
		
		Vector3d vector = new Vector3d(x,y,z);
		
		vector.unitize();
		
		//如果法线指向内侧,则法线反向
		if(this.normal_direction == Bezier.BEZIER_SURFACE_NORMAL_INSIDE )
		{
			vector.x *= -1.0f;
			vector.y *= -1.0f;
			vector.z *= -1.0f;
		}
		
		return	(vector);
	}

	
	//确定一个顶点驻留在哪些面内
	private void resideInFacets()
	{
		int i = 0;
		int j = 0;
		
		//i按v方向,j按u方向

		//四个角点
		
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
		
		//v=v1边界上的点
		for(j = 1; j < un;j++)
		{
			vertex[j].residence.addElement(new Integer(2 * j - 2 ));
			vertex[j].residence.addElement(new Integer(2 * j - 1 ));
			vertex[j].residence.addElement(new Integer(2 * j ));
		}
		
		//v=v2边界上的点
		int vertexBase = vn * (un + 1);
		int facetBase = (vn - 1) * (2 * un);
		for(j = 1; j < un;j++)
		{
			vertex[vertexBase + j].residence.addElement(new Integer(facetBase + 2 * j - 1 ));
			vertex[vertexBase + j].residence.addElement(new Integer(facetBase + 2 * j ));
			vertex[vertexBase + j].residence.addElement(new Integer(facetBase + 2 * j + 1 ));
		}
		
		//u =u1边界上的点
		for(i = 1; i < vn;i++)
		{
			vertex[i * (un + 1)].residence.addElement(new Integer((i - 1) * (2 * un )));
			vertex[i * (un + 1)].residence.addElement(new Integer((i - 1) * (2 * un) + 1 ));
			vertex[i * (un + 1)].residence.addElement(new Integer(i * (2 * un )));
		}
		
		//u =u2边界上的点
		for(i = 1; i < vn;i++)
		{
			vertex[(i + 1) * (un + 1) - 1].residence.addElement(new Integer(i * (2 * un) - 1));
			vertex[(i + 1) * (un + 1) - 1].residence.addElement(new Integer((i + 1) * (2 * un) - 2));
			vertex[(i + 1) * (un + 1) - 1].residence.addElement(new Integer((i + 1) * (2 * un) - 1));
		}
		
		
		//非边界点
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
	
	//面的法线.
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

	//为变形留一个接口
	//计算顶点法线,当对曲面进行变形后,顶点的法就应该由面的法线来决定,而不是由数学公式来计算
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
	
	
	//进行面的可见性测试
	//简单的可见性测试,该方法来自平面方程式法
	//leftCoord为变换到观察坐标系(左手坐标系)后的顶点
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
		边的可见性测试方法:
		该方法的前提是进行了面的可见性测试,而且仅仅适用于凸多面体,对于Bezier曲面可能会有问题,因为Bezier曲面不一定是凸的,
		我们简单地用这一方法来代替.其它方法将涉及更多的编程,我们不是制作一个图形系统!
		
	*/
	public void edgesVisibilityTest()
	{
		int indexBase = un * (vn + 1) + vn * (un + 1);
		int i = 0;
		
		//如果与一条边相邻的两个面均为不可见的,则该边也为不可见的
		for(i = 0; i < indexBase; i++)
		{
			boolean bFacet_1 = facet[edge[i].residence[0]].visibility ;
			boolean bFacet_2 = facet[edge[i].residence[1]].visibility ;
			edge[i].visibility = (bFacet_1 || bFacet_2);
		}
		
		//至于由一个空间四边形的对角线所构成的边,则要采用异或运算,
		//即,当它的两个面都可见时,它是不可见的;当它属于的两个都不可见时,它自然也是不可见的;当一个面可见,而另一个不可见时,对角边应该可见
		//这主要是为绘制网格准备的,因为我们用四边形网格来绘制线框模型,而不是用三角形网格来绘制
		//但这不影响面的绘制,面的绘制仍然采用三角形小面
		for(i = indexBase; i < edge.length;i++)
		{
			boolean bFacet_1 = facet[edge[i].residence[0]].visibility ;
			boolean bFacet_2 = facet[edge[i].residence[1]].visibility ;
			edge[i].visibility = (bFacet_1 ^ bFacet_2);
		}
	
	}

	public void transform(Matrix3d transMatrix)
	{
		//vertex将保存经几何变换之后的点
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
			.为了在面的可见性测试中也能应用观察坐标系的点,减少重复计算,因而应记录作为
				中间计算量的观察坐标的点
		*/
		Vertex3d leftCoord[] = new Vertex3d[(vn + 1) * (un + 1)];
		
		
		int i =0;
		
		//基于左手坐标系,即观察坐标系
		for( i = 0;i < vertex.length ; i++)
			leftCoord[i] = transView.transform(vertex[i]);
		
		//变换到屏幕坐标系
		for(i = 0;i < vertex.length ;i++)
			viewPoint[i] = transView.perspectiveWatch(leftCoord[i],viewfinder.screenDistance);
		
		//进行视口变换
		for(i = 0;i < vertex.length ;i++)
			viewPoint[i] = viewfinder.viewport(viewPoint[i]);
		
		//面的可见性测试
		facetsVisibilityTest(leftCoord);
		
		//边的可见性测试
		edgesVisibilityTest();
		
	}
	
	public void  render(Graphics g,int renderMode)
	{
		Renderer renderer = new Renderer();
		/*
			.其实,几何顶点坐标这时在绘制物体时的几何体时已不必要了,
				但几何顶点含有绘制属性,比如颜色等,因而还是应该作为一个参数传过去
		*/
		renderer.render(g,viewPoint,facet,edge,vertex,renderMode);
	}

	
}
