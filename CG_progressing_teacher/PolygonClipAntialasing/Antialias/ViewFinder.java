
//	ȡ������,��ȡ����û�н��м��й��ܴ���,�������ȱ�ݵ�
//	�����۾�λ�ڳ����������֮��ʱ,�ǳ�����

import java.awt.Point;

public class ViewFinder
{
	Point viewCenter = new Point(0,0);	//���������ԭ��͸����Java�����еĵ�
	float theta = 0.0f;
	float phi = 0.0f;
	float viewDistance = 10.0f;		//�ӵ�����������ԭ��ľ���
	float screenDistance = 10.0f;	//�ӵ�����Ļ�ľ���
	
	float scale_x = 1.0f;
	float scale_y = 1.0f;
	
	public ViewFinder()
	{}
	
	public ViewFinder(float theta,float phi,float viewDistance,float screenDistance,Point viewCenter)
	{
		this.theta = theta;
		this.phi = phi;
		this.viewDistance = viewDistance;
		this.screenDistance = screenDistance;
		this.viewCenter = viewCenter;
		
	
	}
	
	public ViewFinder(float theta,float phi,float viewDistance,float screenDistance,Point viewCenter,float scale_x,float scale_y)
	{
		this.theta = theta;
		this.phi = phi;
		this.viewDistance = viewDistance;
		this.screenDistance = screenDistance;
		this.viewCenter = viewCenter;
		
		this.scale_x = scale_x;
		this.scale_y = scale_y;
		
	}
	
	
	
	public void setViewport(Point viewCenter,float scale_x,float scale_y)
	{
		this.viewCenter = viewCenter;
		this.scale_x = scale_x;
		this.scale_y = scale_y;
	}
	
	public Point  viewport(Point toViewport)
	{
		
		return (new Point((int)(toViewport.x * scale_x) + viewCenter.x,(int)(toViewport.y * scale_y) + viewCenter.y));
		
	}
}
