public class HomogeneousCoordinate
{
	float x = 0.0f;
	float y = 0.0f;
	float z = 0.0f; 
	float w = 1.0f;
	
	public HomogeneousCoordinate(float x,float y,float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public HomogeneousCoordinate(float x,float y,float z,float w)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public HomogeneousCoordinate(float homoCoord[])
	{
		this.x = homoCoord[0];
		this.y = homoCoord[1];
		this.z = homoCoord[2];
		this.w = homoCoord[3];
	}
}
