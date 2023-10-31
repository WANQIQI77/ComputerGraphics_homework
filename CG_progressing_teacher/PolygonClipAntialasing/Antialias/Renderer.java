import java.awt.*;

public class Renderer
{
	public static final int RENDER_VERTICES = 0;
	public static final int RENDER_WIRE = 1;
	public static final int RENDER_WIRE_LIGHTED = 2;
	public static final int RENDER_FLAT = 3;
	public static final int RENDER_GOURAUD_SMOOTH = 4;
	
	public static final int DISABLE_LIGHTING = 0;
	public static final int ENABLE_LIGHTING = 1;
	
	
	public Renderer()
	{
		
	}
	
	/*
	.point----Ϊ���ζ����͸�ӵ�,Ϊ��Ļ����(����ƽ��);
	.facet----������˽ṹ;
	.edge-----Ϊ�ߵ����˽ṹ;
	.renderMode-----Ϊ����ģʽ:�߿��ͻ�̰�����	��Gouraud�⻬������ɫģʽ
	.����ɫ�����ڻ�û�д�����պͲ�������
	*/
	
	public void render(Graphics g,Point[] point,Facet[] facet,Edge[] edge,Vertex3d[] vertex,int renderMode)
	{
		if(renderMode == RENDER_VERTICES)
		{
			render_vertices(g,point,vertex);
			return;
		}
		else if(renderMode == RENDER_WIRE)
		{
			this.render_wire(g, point,edge);
			return;
		}
		else if(renderMode == RENDER_WIRE_LIGHTED)
		{
			this.render_wire(g,point,edge,vertex);
			return;
		}
		else if((renderMode == RENDER_FLAT) || (renderMode == RENDER_GOURAUD_SMOOTH))
		{
			key_Matte(g,point,facet);	
			this.render_solid(g,point,facet,vertex,renderMode);
		}
		
	}
	
	//���ƶ���ģ��
	public void render_vertices(Graphics g,Point[] point,Vertex3d[] vertex)
	{
		Color colorOld = g.getColor();
		for(int i = 0; i < point.length;i++)
		{
			Color color = new Color(vertex[i].color.red,vertex[i].color.green,vertex[i].color.blue);
			g.setColor(color);
			g.drawLine(point[i].x,point[i].y,point[i].x,point[i].y);
		}
		g.setColor(colorOld);
	}
	
	
	//����������
	public void render_wire(Graphics g,Point[] point,Edge[] edge)
	{
		Color colorOld = g.getColor();
		for(int i = 0; i < edge.length;i++)
		{
			
			if(edge[i].visibility)
			{
				Color color = new Color(edge[i].color.red,
							edge[i].color.green,edge[i].color.blue);
				g.setColor(color);
			
				g.drawLine(point[edge[i].index[0]].x,
					   point[edge[i].index[0]].y,
					   point[edge[i].index[1]].x,
					   point[edge[i].index[1]].y);
				
			}
		}
		g.setColor(colorOld);
	}

	//����������,�ߵ���ɫ�������������ɫ������,�������֧�ֹ���
	public void render_wire(Graphics g,Point[] point,Edge[] edge,Vertex3d[] vertex)
	{
		Color colorOld = g.getColor();
		for(int i = 0; i < edge.length;i++)
		{
						
			if(edge[i].visibility)
			{
				float color0[] = {vertex[edge[i].index[0]].color.red,
								vertex[edge[i].index[0]].color.green,
								vertex[edge[i].index[0]].color.blue };
				
				float color1[] = {vertex[edge[i].index[1]].color.red,
								vertex[edge[i].index[1]].color.green,
								vertex[edge[i].index[1]].color.blue };
				
				LinePrimitive lineTool = new LinePrimitive();
				
				lineTool.setColor(color0,color1);
				
				lineTool.lineLighted(g,
						point[edge[i].index[0]].x,
					   point[edge[i].index[0]].y,
					   point[edge[i].index[1]].x,
					   point[edge[i].index[1]].y);
			}
		}
		g.setColor(colorOld);
	}

	/*
		.ͬʱ���������޵�Ԫ��Ϊ�ı��κ������ε�С��
		.������������߳�,�����������ڵ�һ���ı��κ�����������,
			Ҳ�����ڶ�����ɵĻ������
	*/
	
	public void render_solid(Graphics g,Point[] point,Facet[] facet,Vertex3d[] vertex,int renderMode)
	{
		
		Color colorOld = g.getColor();
		for(int i = 0; i < facet.length;i++)
		{	
			int type = facet[i].type;
			
			/*
			.����Facet3��Facet4��Ķ���֮�䲻�ܽ����໥ת��,
				�������ǲ��ò��ظ�����д����ͬ���Ĵ���
			*/
			
			Facet3 facet_3 = new Facet3(0,0,0);
			Facet4 facet_4 = new Facet4(0,0,0,0);
			
			if(type == 3)
			{
				facet_3 = (Facet3)facet[i];
			
				//������С���ǿɼ��ģ��ͽ������Ƴ���
				if(facet_3.visibility)
				{
					//�����Flat��ɫģʽ,
					if(renderMode == RENDER_FLAT)
					{
						Polygon polygon = new Polygon();
			
			
						for(int j = 0;j < facet_3.index.length;j++)
						{
							polygon.addPoint(point[facet_3.index[j]].x,
											 point[facet_3.index[j]].y);
											 
						}
					
						//ֻ�Ǳ�֤��Ϊһ����״,����䲻�Ǿ��Ա�Ҫ
						polygon.addPoint(point[facet_3.index[0]].x,
									point[facet_3.index[0]].y);
					
						//����С����ɫ�����������ǲ��������ɫ�������ǲ��������εĵ�һ���������ɫ.
						
						Color color = new Color(facet_3.color.red,
												facet_3.color.green,
												facet_3.color.blue );
						
						
						g.setColor(color);
					
						g.fillPolygon(polygon);
					}
					
					//�����Gouraud��ɫģʽ
					else if(renderMode == RENDER_GOURAUD_SMOOTH)
					{
						TrianglePrimitive triFacet = new TrianglePrimitive(
									point[facet_3.index[0]],
									point[facet_3.index[1]],
									point[facet_3.index[2]]);
					
						//���ζ���ֻ��������ɫ��Ϣ,��ʱ,����ļ������ݲ�����Ҫ
					
						float color0[] = vertex[facet_3.index[0]].color.getRGB();
						float color1[] = vertex[facet_3.index[1]].color.getRGB();
						float color2[] = vertex[facet_3.index[2]].color.getRGB();
						
						
						triFacet.setColor(color0,color1,color2);
						
						triFacet.fillTriangle(g,BasicPrimitive.FILL_GOURAUD_SMOOTH );
					}
				}
			}
			if(type == 4)
			{
				facet_4 = (Facet4)facet[i];
			
				if(facet_4.visibility)
				{
					if(renderMode == RENDER_FLAT)
					{
						Polygon polygon = new Polygon();
			
						for(int j = 0;j < facet_4.index.length;j++)
						{
							polygon.addPoint(point[facet_4.index[j]].x,
											 point[facet_4.index[j]].y);
											 
						}
					
						polygon.addPoint(point[facet_4.index[0]].x,
									point[facet_4.index[0]].y);
					
					
						Color color = new Color(facet_4.color.red,
												facet_4.color.green,
												facet_4.color.blue);
						
												
						g.setColor(color);
					
						g.fillPolygon(polygon);

					
					}
					else if(renderMode == RENDER_GOURAUD_SMOOTH)
					{
						TrianglePrimitive triFacet_1 = new TrianglePrimitive(
									point[facet_4.index[0]],
									point[facet_4.index[1]],
									point[facet_4.index[2]]);
					
						TrianglePrimitive triFacet_2 = new TrianglePrimitive(
									point[facet_4.index[0]],
									point[facet_4.index[2]],
									point[facet_4.index[3]]);
	
					
						//���ζ���ֻ��������ɫ��Ϣ,��ʱ,����ļ������ݲ�����Ҫ
						float color0[] = vertex[facet_4.index[0]].color.getRGB();
						float color1[] = vertex[facet_4.index[1]].color.getRGB();
						float color2[] = vertex[facet_4.index[2]].color.getRGB();
						float color3[] = vertex[facet_4.index[3]].color.getRGB();
					
						triFacet_1.setColor(color0,color1,color2);
						
						triFacet_2.setColor(color0,color2,color3);
					
						/*
							�������һ���Ϊ:
								triFacet_2.setColor(color0,color2,color2);
							���п��ܻ��Ƴ�����GrouaudЧ���Ŀ���Ч��,��Ȼ��ֻ��һ�ֱ��ʵ��,
								��������֧��,ȷ�е�˵,����������ɫ����д����,���ڱ��������������һ�ڵ����ǳ�����,
								����������һ�ڵ�Բ��ȴ��������,ԭ�������ɫ���ŵ�˳���ǲ�����,������Ʋ�������Ч��.
							���ڹ�������,���ڶ������ɫ�������˹�ָ����,����,�����滻�������Ʋ�������Ч��,�����������
						*/
						
					
						triFacet_1.fillTriangle(g,BasicPrimitive.FILL_GOURAUD_SMOOTH );
						triFacet_2.fillTriangle(g,BasicPrimitive.FILL_GOURAUD_SMOOTH );
					}
				}
			}
		}
		
		g.setColor(colorOld);
	}	
	
	
	/*
		.Key-Matte�㷨��
			��ͬһ����Ļ�ϲ����ֵ���ɫ���ÿ��
			����Σ��Ա�֤���ǵ���Ļ���Ѿ�����
			��ͼ������һ�ּ�˼�룬���Ա�����
			ɫ��������������
		.������ʹ�ú�ɫ
		.������ҳ�һ�������������ɫɫ���������,ʹ������һ����ɫʱ������ͬ
	*/
	public void key_Matte(Graphics g,Point[] point,Facet[] facet)
	{
		Color colorOld = g.getColor();
			
		g.setColor(new Color(0.0f,0.0f,0.0f));

		/*
			facet.length��ʾ���ж��ٸ�����Ҫ����
		*/
		for(int i = 0; i < facet.length;i++)
		{
			int type = facet[i].type;
			
			Facet3 facet_3 = new Facet3(0,0,0);
			Facet4 facet_4 = new Facet4(0,0,0,0);
			if(type == 3)
		    {
				facet_3 = (Facet3)facet[i];
				
				//������С���ǿɼ��ģ��ͽ������Ƴ���
				if(facet_3.visibility)
				{
					Polygon polygon = new Polygon();
			
			
					for(int j = 0;j < facet_3.index.length;j++)
					{
						polygon.addPoint(point[facet_3.index[j]].x,
										 point[facet_3.index[j]].y);
										 
					}
					g.fillPolygon(polygon);
				}
			}
			if(type == 4)
			{
				facet_4 = (Facet4)facet[i];
				if(facet_4.visibility)
				{
					Polygon polygon = new Polygon();
			
			
					for(int j = 0;j < facet_4.index.length;j++)
					{
						polygon.addPoint(point[facet_4.index[j]].x,
										 point[facet_4.index[j]].y);
										 
					}
					g.fillPolygon(polygon);
				}
			}
		}
		g.setColor(colorOld);
	}
}
