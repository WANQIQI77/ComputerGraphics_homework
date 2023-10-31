
//	�ɼ��Բ��Թ���,���������ӿɼ��Բ��Է���

public class VisibilityTester
{
	boolean visibility = true;
	public VisibilityTester()
	{
	}
	
	public static boolean tester(Vector3d normal)
	{
		Vector3d viewDirection = new Vector3d(0.0f,0.0f,1.0f);
		
		if(normal.dot(viewDirection) > 0.000001f)
			return true;
		else 
			return false;
	}
	
	//���Ӿ�ȷ���㷨
	public static boolean tester(Vertex3d vertex0,Vertex3d vertex1,Vertex3d vertex2)
	{
		float x = vertex0.x * (vertex1.y * vertex2.z - vertex2.y * vertex1.z);
		float y = vertex1.x * (vertex2.y * vertex0.z - vertex0.y * vertex2.z);
		
		float z = vertex2.x * (vertex0.y * vertex1.z - vertex1.y * vertex0.z);
		
		//ǰ��:�ӵ���(0,0,0)λ��
		//���øõ������һ��ƽ���ȡ��,����ȷ����ƽ���ǿɼ��Ļ������ص�.
		
		if((-x - y - z) >= -10.0f)return false;
		
		return true;
		
	}
	
}
