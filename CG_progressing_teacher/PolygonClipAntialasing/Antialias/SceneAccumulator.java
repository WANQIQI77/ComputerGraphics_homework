
//import java.awt.*;
import java.awt.image.*;

public class SceneAccumulator 
{
	public SceneAccumulator(){}	
	
	
	//�ۻ�����,ȡƽ��ֵ
	public static  void accumulate(int[][] src,int[] dst )
	{
		//��������
		int ditherTimes = src.length;
		int nTotal = src[0].length;
 
		ColorModel colorModel = ColorModel.getRGBdefault();
		

		for(int j = 0;j < nTotal;j++)
		{
			int r = 0;
			int g = 0;
			int b = 0;

			for(int i = 0;i < ditherTimes;i++)
			{
				r += colorModel.getRed(src[i][j]);
				g += colorModel.getGreen(src[i][j]);
				b += colorModel.getBlue(src[i][j]);
		
			}
			
			r /= ditherTimes;
			g /= ditherTimes;
			b /= ditherTimes;
			
			dst[j] = (0xFF000000 | (( r << 16 ) | ( g << 8 ) | b ));
		}
	}
	

	
}
