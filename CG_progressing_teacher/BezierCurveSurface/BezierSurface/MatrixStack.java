import java.util.Vector;
import Matrix3d;

public class MatrixStack 
{
	Vector matrixQueue;
	int count;
	
	public MatrixStack()
	{
		matrixQueue = new Vector(1,1);
		count = 0;
	}
	
	//ѹ���ջ
	public Matrix3d push(Matrix3d matrix)
	{
		Matrix3d m = new Matrix3d();
		m.replace(matrix);
		
		matrixQueue.addElement(m);
		
		count++;
		return m;
		
	}
	
	//����,ͬ������
	public synchronized Matrix3d pop()
	{
		if(count == 0)return null;

		Matrix3d matrix = new Matrix3d();
		matrix.replace((Matrix3d)matrixQueue.elementAt(count - 1));
		matrixQueue.removeElementAt(count - 1);
		count--;
		//if(count == 0)matrixQueue.removeAllElements();
		return matrix;
		
	}
	
	//�����Ƿ�Ϊ��
	public boolean empty()
	{
		//�ǿվͷ���true
		//return (matrixQueue.isEmpty());
		
		if(count == 0)return true;
		else return false;
	
	}
	
	/*
		.return the distance from the top of the stack 
			where the object is located; 
		.the return value -1 indicates that the object 
			is not on the stack. 
	*/
	public synchronized int search(Matrix3d target) 
	{
		if(count == 0)return -1;
		
		int i = count - 1;
		
		//��ȡջ������;
		Matrix3d m = (Matrix3d)matrixQueue.elementAt(count - 1);
		
		while(i != 0)
		{
			if(m.isEqual(target))
				return (count - 1 - i);
			i--;
			m = (Matrix3d)matrixQueue.elementAt(count - 1);
		}
		
		return -1;
		
	}

	//��ȡջ������,��������
	public synchronized Matrix3d peek() 
	{
		if(count == 0)return null;
		
		return (Matrix3d)((Matrix3d)matrixQueue.elementAt(count - 1));
	}
		
	
}
