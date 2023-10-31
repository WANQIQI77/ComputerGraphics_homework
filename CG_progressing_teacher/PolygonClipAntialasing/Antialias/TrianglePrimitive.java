import java.awt.Point;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.Vector;

public class TrianglePrimitive extends BasicPrimitive 
{
	Point point0  = new Point(0,0);
	Point point1  = new Point(0,0);
	Point point2  = new Point(0,0);
	
	float[] color0 = new float[3];
	float[] color1 = new float[3];
	float[] color2 = new float[3];
	
	public TrianglePrimitive(Point point0,Point point1,Point point2)
	{
		this.point0.x = point0.x;
		this.point0.y = point0.y;
		this.point1.x = point1.x;
		this.point1.y = point1.y;
		this.point2.x = point2.x;
		this.point2.y = point2.y;
	}
	
	public void setColor(float[] color0,float[] color1,float[] color2)
	{
		for(int i = 0;i < 3;i++)
		{
			this.color0[i] = color0[i];
			this.color1[i] = color1[i];
			this.color2[i] = color2[i];
		}
	}
	
	public void setPoints(Point point0,Point point1,Point point2)
	{
		this.point0.x = point0.x;
		this.point0.y = point0.y;
			
		this.point1.x = point1.x;
		this.point1.y = point1.y;
			
		this.point2.x = point2.x;
		this.point2.y = point2.y;
	}

	public void drawTrigle(Graphics g)
	{
		Color colorOld = g.getColor();
		g.setColor(new Color(color0[0],color0[1],color0[2]));
		Polygon polygon = new Polygon();
		polygon.addPoint(point0.x,point0.y);
		polygon.addPoint(point1.x,point1.y);
		polygon.addPoint(point2.x,point2.y);
		g.drawPolygon(polygon);
		g.setColor(colorOld);
	}
	
	public void fillTriangle(Graphics g)
	{
		Color colorOld = g.getColor();
		g.setColor(new Color(color0[0],color0[1],color0[2]));
		Polygon polygon = new Polygon();
		polygon.addPoint(point0.x,point0.y);
		polygon.addPoint(point1.x,point1.y);
		polygon.addPoint(point2.x,point2.y);
		g.fillPolygon(polygon);
		g.setColor(colorOld);
	}
	
	//colorΪ�����ɫ,��ʱ�����õ�һ���������ɫ������.
	public void fillTriangle(Graphics g,float[] color)
	{
		Color colorOld = g.getColor();
		g.setColor(new Color(color[0],color[1],color[2]));
		Polygon polygon = new Polygon();
		polygon.addPoint(point0.x,point0.y);
		polygon.addPoint(point1.x,point1.y);
		polygon.addPoint(point2.x,point2.y);
		g.fillPolygon(polygon);
		g.setColor(colorOld);
	}
	
	public void fillTriangle(Graphics g,int fillModel)
	{
		Color colorOld = g.getColor();
		
		int i = 0;
		int j = 0;
		
		//��¼��ɫ��һ������:��������ʱ,��ɫҲӦ�ö�Ӧ����
		float clr0[] = {color0[0],color0[1],color0[2]};
		float clr1[] = {color1[0],color1[1],color1[2]};
		float clr2[] = {color2[0],color2[1],color2[2]};
		
		//��ɫ����
		float[] c01 = new float[3];
		float[] c02 = new float[3];
		float[] c21 = new float[3];
		
		//���Բ�ֵ��ɫ(λ�������εı߽��ϵĵ�)
		float[] clrLinear01 = new float[3];
		float[] clrLinear02 = new float[3];

		
		//��¼�����һ������:���¿��ٵ�ַ
		Point p0 = new Point(point0.x,point0.y);
		Point p1 = new Point(point1.x,point1.y);
		Point p2 = new Point(point2.x,point2.y);
		
		
		/*
			.�������򣬵�һ����Ϊy������С�ߣ�
				�ڶ�����Ϊy��������ߣ�
				��������λ�����ǵ��м�
			.��ɫ��Ӧ����
		*/
		if(p0.y > p1.y)
		{
			p0 = point1;
			p1 = point0;
			clr0 = color1;
			clr1 = color0;
		}
		if(p0.y > p2.y)
		{
			Point pTemp = p0;
			p0 = p2;
			p2 = pTemp;
			
			float[] clrTemp = new float[3];
			clrTemp = clr0;
			clr0 = clr2;
			clr2 = clrTemp;
		}
		
		//���ڶ�������Ϊy�������ĵ�
		if(p1.y < p2.y)
		{
			Point pTmp = p1;
			p1 = p2;
			p2 = pTmp;
			
			float[] clrTmp = new float[3];
			clrTmp = clr1;
			clr1 = clr2;
			clr2 = clrTmp;
		}
		
		for(j = 0;j < 3; j++)
		{
			if(p1.y != p0.y)
				c01[j] = (clr1[j] - clr0[j]) / (p1.y - p0.y);
			if(p2.y != p0.y)
				c02[j] = (clr2[j] - clr0[j]) / (p2.y - p0.y);
			if(p1.y != p2.y)
				c21[j] = (clr1[j] - clr2[j]) / (p1.y - p2.y);
		}
		
		for(j = 0;j < 3;j++)
		{
			clrLinear01[j] = clr0[j];
			clrLinear02[j] = clr0[j];
		}
		

		/*
			.��ȡ�߽��,
			.��Ȼ�⽫Ӱ������Ч��.
			.�����obtainBorder()�������������������,
				������������Ҳ��,��������ϸ���
		*/
		
		Vector v01 = obtainBorder(p0.x,p0.y,p1.x,p1.y);
		Vector v02 = obtainBorder(p0.x,p0.y,p2.x,p2.y);
		Vector v21 = obtainBorder(p2.x,p2.y,p1.x,p1.y);
		
		/*
		v01.addElement(new Integer(p1.x));
		v02.addElement(new Integer(p2.x));
		v21.addElement(new Integer(p1.x));
		*/
		

		
		//��ʼɨ��,  plumbScan ---- ɨ��ڼ�����
		int plumbScan = 0;
		int y = p0.y;
		//����һ��͵ڶ��������ƽ����ˮƽ��ʱ,�Ͳ�����
		if(p0.y < p2.y)
		{
			//���ڴ����ڶ�����(���2)��ɨ����,��������һ�׶ν�������y < p2.y
			for(y = p0.y; y < p2.y; y++)
			{
				int x01 = ((Integer)v01.elementAt(plumbScan)).intValue();
				int x02 = ((Integer)v02.elementAt(plumbScan)).intValue();
				
				float[] clrEdge01 = new float[3];
				float[] clrEdge02 = new float[3];
				
				for(j = 0;j < 3; j++)
				{
					clrEdge01[j] = clrLinear01[j];
					clrEdge02[j] = clrLinear02[j];
				}
				
				//�ڲ��������
				gouraud(g,x01,x02,y,clrEdge01,clrEdge02,fillModel);
				
				//��ȡ��һ����ɨ���ߵ������߽�����ɫ
				for(j = 0;j < 3;j++)
				{
					clrLinear01[j] += c01[j];
					clrLinear02[j] += c02[j];
				}
				
				//ɨ��ָ���������1
				plumbScan++;
				
			}
		}
		
		//������һ��ɨ��ָ��:����õ�2����1�ı߽��������
		
		int plumbScan21 = 0;
		
		//��ʱclrLinear02���ʾλ�ڵ�2�͵�1�������ϵĵ����ɫ
		
		clrLinear02 = clr2;
		
		
		for(y = p2.y; y <= p1.y; y++)
		{
			int x01 = ((Integer)v01.elementAt(plumbScan)).intValue();
			int x21 = ((Integer)v21.elementAt(plumbScan21)).intValue();
				
			float[] clrEdge01 = clrLinear01;
			float[] clrEdge02 = clrLinear02;
				
			//�ڲ��������
			gouraud(g,x01,x21,y,clrEdge01,clrEdge02,fillModel);
				
			//��ȡ��һ��ɨ���ߵ������߽�����ɫ
			for(j = 0;j < 3;j++)
			{
				clrLinear01[j] += c01[j];
				clrLinear02[j] += c21[j];
			}
				
			//ɨ��ָ���������1
			plumbScan++;
			plumbScan21++;
				
		}
		
		//g.drawLine(p1.x,p1.y,p1.x,p1.y);
		g.setColor(colorOld);
		
	}
	
	/*
		.Gouraud����
		.clr0,clr1�ֱ�Ϊ�߽��(x0,scanLine)��(x1,scanLine)����ɫ
	
	*/
	public void gouraud(Graphics g,int x0,int x1,int scanLine,float[] clr0,float[] clr1,int fillModel)
	{
		int direction = (x0 < x1) ? 1 : ((x0 > x1) ? -1 : 0);			
						
		int fillPixels = Math.abs(x1 - x0);
		if(fillPixels == 0) fillPixels = 1;
		
		float rIncrement = (clr1[0] - clr0[0]) / fillPixels ;
		float gIncrement = (clr1[1] - clr0[1]) / fillPixels;
		float bIncrement = (clr1[2] - clr0[2]) / fillPixels;
		
		float red = clr0[0];
		float green = clr0[1];
		float blue = clr0[2];
		
		if(x0 == x1)fillPixels =0;
		else	fillPixels++;	//ȷ����䵽�߽�
		
		int x = x0;
		
		if(fillModel == 0)
		{
			Color color = new Color(color0[0],color0[1],color0[2]);
			g.setColor(color);
		}
		
		for(int i = 0; i < fillPixels; i++)
		{
			if(fillModel == 1)
			{
				Color color = new Color(red,green,blue);
				g.setColor(color);
			}
			
			g.drawLine(x,scanLine,x,scanLine);
			
			x += direction;
			
			red +=  rIncrement;
			green +=  gIncrement;
			blue +=  bIncrement;
		}
		
	}
	
	private Vector obtainBorder(int x1,int y1,int x2,int y2)
	{
		Vector vx = new Vector(1,1);
		int dx = x2 - x1;
		int dy = y2 - y1;	//��ʱ,dy������С��0
		
		int ix = (int)Math.abs(dx);
		int iy = (int)Math.abs(dy);
		int inc = (ix >= iy) ? ix : iy;
		
		//��һ����Ҫ��¼
		vx.addElement(new Integer(x1));
		
		int x = 0;
		int y = 0;
		boolean bRecord = false;
		
		int plotX = x1;
		int plotY = y1;
		
		//��¼���յ�����е�
		for(int i = 0;i <= inc;i++) 
		{
			x += ix;
			y += iy;
			
			bRecord = false;
			
			if(x > inc)
			{
				x -= inc;
				
				if(dx > 0) plotX++;
				if(dx < 0) plotX--;
			}
			if(y > inc)
			{
				bRecord = true;
				y -= inc;
				if(dy > 0) plotY++;
				
			}
			//ֻ�е�y��������ʱ�ż�¼
			if(bRecord)vx.addElement(new Integer(plotX));
		}
		return vx;
	}
}
