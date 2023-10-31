public class LightingSystem
{
	//ȫ�ֻ�����
	public static final int LIGHT_MODEL_AMBIENT = 0;
	//���ӵ�������Զ�ӵ�
	public static final int LIGHT_MODEL_LOCAL_VIEWER = 0;
	//˫�����
	public static final int LIGHT_MODEL_TWO_SIDE = 0;
	
	//ȫ�ֻ�����
	/*
	The parameter contains four floating-point values 
		that specify the ambient RGBA intensity of the entire scene.
	Floating-point values are mapped directly. 
	Neither integer nor floating-point values are clamped. 
	The default ambient scene intensity is (0.2, 0.2, 0.2, 1.0). 
	*/
	ColorFloat globalAmbient = new ColorFloat(0.2f,0.2f,0.2f,1.0f);
	
	
	//���ӵ�������Զ�ӵ�
	/*
	The parameter is a single floating-point value that specifies how specular reflection angles are computed. 
	If bLocalView is false, specular reflection angles take the view direction to be parallel to and in the direction of the �Cz axis, regardless of the location of the vertex in world coordinates. 
	Otherwise specular reflections are computed from the origin of the world coordinate system. 
	The default is 0. 
	*/
	boolean bLocalView = false;
	
	//˫�����
	/*
	The parameter is a single floating-point value that specifies whether one- or two-sided lighting calculations are done for polygons. 
	It has no effect on the lighting calculations for points, lines, or bitmaps. If bTwoSide is  false, one-sided lighting is specified, and only the front material parameters are used in the lighting equation.
	Otherwise, two-sided lighting is specified.
	In this case, vertices of back-facing polygons are lighted using the back material parameters, and have their normals reversed before the lighting equation is evaluated.
	Vertices of front-facing polygons are always lighted using the front material parameters, with no change to their normals. 
	The default is 0. 
	*/
	boolean bTwoSide = false;
	
	/*
	Remarks

	In RGBA mode, the lighted color of a vertex is the sum of 
		(1)the material emission intensity, 
		(2)the product of the material ambient reflectance and the lighting model full-scene ambient intensity,	and 
		(3)the contribution of each enabled light source. 
			Each light source contributes the sum of three terms: ambient, diffuse, and specular. 
			The ambient light source contribution is the product of the material ambient reflectance and the light's ambient intensity. 
			The diffuse light source contribution is the product of the material diffuse reflectance, the light's diffuse intensity, and the dot product of the vertex's normal with the normalized vector from the vertex to the light source. 
			The specular light source contribution is the product of the material specular reflectance, the light's specular intensity, and the dot product of the normalized vertex-to-eye and vertex-to-light vectors, raised to the power of the shininess of the material.
		All three light source contributions are attenuated equally based on the distance from the vertex to the light source and on light source direction, spread exponent, and spread cutoff angle. 
		All dot products are replaced with zero if they evaluate to a negative value.

		The alpha component of the resulting lighted color is set to the alpha value of the material diffuse reflectance.

	*/
	
	public LightingSystem()
	{
	}
	
	public void lightModelb(int property,boolean bFlag)
	{
		if(property == LIGHT_MODEL_LOCAL_VIEWER)
			bLocalView = bFlag;
		else if(property == LIGHT_MODEL_TWO_SIDE)
			bTwoSide = bFlag;
	}
	
	public void lightModelfv(int property,float[] params)
	{
		if(property == LIGHT_MODEL_AMBIENT )
		{
			globalAmbient.red = params[0];
			globalAmbient.green = params[1];
			globalAmbient.blue = params[2];
			globalAmbient.alpha = params[3];
		}
	}
	
	
	/*
	������Ķ�������������
		light----һ����Դ������Դ
		material---����Ĳ���
		vertex----�ļ��ζ���,���еļ��α任���֮��û�н��й۲�任�ĵ�
		viewer----�ӵ��λ��
	
	���м��������������ϵ�����	
	*/
	/*
		��һ,����������������Զ�ӵ�Ĵ���;
		�ڶ�,������������˫����մ���
	*/
	
	public void lighting(Light[] light,Material material,Vertex3d[] vertex,ViewFinder viewer)
	{
		//�������ɸ������
		float toRad = (float)(Math.PI / 180.0);
		float thta = viewer.theta  * toRad;
		float fai = viewer.phi * toRad;
		
		//�۲������������ϵ�е������������
		float xViewer = (float)(viewer.viewDistance * Math.cos(fai) * Math.cos(thta));
		float yViewer = (float)(viewer.viewDistance * Math.cos(fai) * Math.sin(thta));
		float zViewer = (float)(viewer.viewDistance * Math.sin(fai));
		
		
		//i���������ѭ��,j����ĸ���ѭ��
		int i,j;
		for(i = 0;i < vertex.length;i++)
		{
			if(vertex[i].visibility)
			{
				//clrVertexΪ��������չ�����
				ColorFloat clrVertex = new ColorFloat(0.0f,0.0f,0.0f,1.0f);
					
				/*
				�����һ��:ԭ��������ɢ�Ĺ�ǿ
				----��������ɢ�Ĺ���Ϊ��,�ɲ��ʵ�emission����,�����κ�ϵ��ֱ�Ӽ��뵽��ǿ֮��
				*/
				clrVertex.red = material.emission.red;
				clrVertex.green = material.emission.green;
				clrVertex.blue = material.emission.blue;
					
				//�������ڶ���:ȫ�ֻ����������----ȫ�ֻ�����Ĺ�ǿ���Բ��ʵĻ�������
					
				//ǿ��
				float r = globalAmbient.red * material.ambient.red;
				float g = globalAmbient.green * material.ambient.green;
				float b = globalAmbient.blue * material.ambient.blue;
					
				//�����ȥ,�����Ѱ�������
				clrVertex.red += r;
				clrVertex.green += g;
				clrVertex.blue += b;
					
				/*
					����һ��ѭ���������������:�ӹ�Դ���Ĺ���
				*/
			
				for(j = 0;j < light.length;j++)
				{	
					//ֻ�е����ǿ�����,��Դ���ܷ���,����Ż��øù������
					if(light[j].on_off ==true)
					{
						//��һ��:����˥��ϵ��
						float attenuation = 1.0f;
						
						/*
							�����ԴΪһ�����Դ(λ�ù�Դ)����Ҫ���㱻���յĵ�����Դ�ľ���,�Լ���˥��ϵ��
							�����ƽ�й�Դ(�����Դ),��˥��
						*/
						if(light[j].type_source == Light.POSITIONAL_SOURCE)
						{
							float c0 = light[j].constant_attenuation;
							float c1 = light[j].linear_attenuation;
							float c2 = light[j].quadratic_attenuation;
							
							if(c0 < 0.000001f) c0 = 0.000001f;
							attenuation = c0;
							
							//���㶥�����Դ�ľ���
							if((c1 > 0.000001f) ||(c2 > 0.000001f))
							{
								float x_x = light[j].position.x - vertex[i].x ;
								float y_y = light[j].position.y - vertex[i].y ;
								float z_z = light[j].position.z - vertex[i].z ;
							
								float distance = (float)Math.sqrt(x_x * x_x + y_y * y_y + z_z * z_z);
								
								//ͨ��˥����������˥��ϵ��
								attenuation = c0 + c1 * distance + c2 * distance * distance;
				
							}
							
							attenuation = 1.0f / attenuation;
							
							attenuation = Math.min(attenuation,1.0f);
						}
						
						//�ڶ���:����۹�Ч������
						
						//�����Ϊ�۹�,���ֻ��Ӱ��λ�ڹ�׶֮�ڵĶ���,�����Ϊ����,��spotFactor�򵥵�Ϊ1.0f.
						float spotFactor = 1.0f;
						
						//�����Դ��һ�����Դ����һ���۹��,����о۹�Ч��
						if((light[j].type_source == Light.POSITIONAL_SOURCE) &&
						   (light[j].type == Light.SPOT_LIGHT))
						{
							if((light[j].spot_cutoff >= 0.0f) &&
							   (light[j].spot_cutoff <= 90.0f))
							{
								//�۹�ƹ�Դָ�򶥵����������������
								float v_x = vertex[i].x - light[j].position.x;
								float v_y = vertex[i].y - light[j].position.y;
								float v_z = vertex[i].z - light[j].position.z;
								
								//�۹�ƹ�Դָ�򶥵������(viewer ---- vertex)
								Vector3d v_xyz = new Vector3d(v_x,v_y,v_z);
								
								//������λ��
								v_xyz.unitize();
								
								//��������v��۹�����䷽����������λ�����ĵ��
								float fInsideCone = v_xyz.dot(light[j].spot_direction);
								
								//ȡ0��������������
								fInsideCone = fInsideCone < 0.0f ? 0.0f : fInsideCone;
								
								//���fInsideConeС�ھ۹ⷢɢ��ǵ�����,������ö������ڹ�׶֮��,�۹����û���κ�Ӱ��
								if(fInsideCone < Math.cos(light[j].spot_cutoff * toRad))
									spotFactor = 0.0f;
								else
								{
									spotFactor = fInsideCone;
								
									//�۹�Ч������۹�ָ���й�,��Ӧ���Ǿ۹�ָ���Ĵη�
									fInsideCone = 1.0f;
									for(int m = 0;m < (int)light[j].spot_exponent;m++)
										fInsideCone *= spotFactor;
									spotFactor = fInsideCone;	
								}
								
							}
							
						}
						
						//������:��ɵ�����ļ���,��������Ǵӹ�Դ���Ĺ���
						
						//���㻷�����䲿��,��r,g,b���¸�ֵ:����������
						r = material.ambient.red * light[j].ambient.red;	
						g = material.ambient.green * light[j].ambient.green;	
						b = material.ambient.blue * light[j].ambient.blue;	
						
						//���������䲿��
						//�ڼ��������䲿��ʱ,Ҫ���㶥�㷨�������Ͷ���ָ���Դ��������������λ�����ĵ��,���нǵ�����
						
						//�ɶ���ָ���Դ����������������
						float x_l = light[j].position.x - vertex[i].x ;
						float y_l = light[j].position.y - vertex[i].y ;
						float z_l = light[j].position.z - vertex[i].z ;
					
						//�ɶ���ָ���Դ������(vertex----viewer)
						Vector3d xyz_light = new Vector3d(x_l,y_l,z_l);
						
						/*
							�����ԴΪ�����Դ,�������,��ʱ,���λ��������Զ�㴦,
								�������ƽ�й�,�κζ�������Դ��������ƽ���ڹ�����䷽��
						*/
						
						if(light[j].type_source == Light.DIRECTIONAL_SOURCE)
						{
							xyz_light.x = -1.0f * light[j].position.x;
							xyz_light.y = -1.0f * light[j].position.y;
							xyz_light.z = -1.0f * light[j].position.z;
						}
						
						//��λ��
						xyz_light.unitize();
						
						float cos = xyz_light.dot(vertex[i].normal);
						cos = cos < 0.0f ? 0.0f : cos;
						
						//���������䲿�ֵ�����RGB����
						float rDiffuse = material.diffuse.red * light[j].diffuse.red * cos;	
						float gDiffuse = material.diffuse.green * light[j].diffuse.green * cos;	
						float bDiffuse = material.diffuse.blue * light[j].diffuse.blue * cos;
						
						//���������䲿��
						r += rDiffuse;
						g += gDiffuse;
						b += bDiffuse;
						
						//����߹ⲿ��
						/*
							���ȷ�����Ҳȡ���ڹ��Ƿ�ֱ�����ڶ�����,
								������㷨�ߺ��ɶ���ָ���Դ������֮���С�ڻ����0.0,��û�и߹ⷴ��
						*/
						if(cos > 0.0f)
						{
							//����Ӷ���ָ���ӵ�ĵ�λ����
							float x_viewer = xViewer - vertex[i].x;
							float y_viewer = yViewer - vertex[i].y;
							float z_viewer = zViewer - vertex[i].z;
							
							Vector3d xyz_viewer = new Vector3d(x_viewer,y_viewer,z_viewer);
							xyz_viewer.unitize();
							
							//���㶥�㵽��Դ�Ͷ��㵽�ӵ���������λ�����ĺ�
							Vector3d vector = xyz_viewer.add(xyz_light);
							vector.unitize();
							
							//���㶥�㷨������������֮���
							float shine = vector.dot(vertex[i].normal);
							
							shine = shine < 0.0f ? 0.0f : shine;
							
							//������ɢ��
							float factor = 1.0f;
							for(int k = 0;k < (int)material.shininess;k++)
								factor *= shine;
							
							//����߹����������
							float rSpecular = material.specular.red * light[j].specular.red * factor;	
							float gSpecular = material.specular.green * light[j].specular.green * factor;	
							float bSpecular = material.specular.blue * light[j].specular.blue * factor;
							
							//������������
							r += rSpecular;
							g += gSpecular;
							b += bSpecular;
						}
						
						//����ֻҪ��˥��ָ���;۹�Ч���������,�͵õ������Թ�Դ�Ĺ���
						
						r *= (attenuation * spotFactor);
						g *= (attenuation * spotFactor);
						b *= (attenuation * spotFactor);
						
						//һ���������������
						clrVertex.red += r;
						clrVertex.green += g;
						clrVertex.blue += b;
						
					}//if
				}//for --j
			
				//�ù����ȵ���������������ɫ������ǿ��
				clrVertex.clamp();
				vertex[i].color.red = clrVertex.red;
				vertex[i].color.green = clrVertex.green;
				vertex[i].color.blue = clrVertex.blue;
				
				//alpha	������Ϊ���ʵ����������alpha
				vertex[i].color.alpha = material.diffuse.alpha;
			}//if(vertex[i].visibility)end	
		}//for --i
	}
	
	public void lighting(Light[] light,Material material,Facet[] facet,Vertex3d[] vertex,ViewFinder viewer)
	{
		//�������ɸ������
		float toRad = (float)(Math.PI / 180.0);
		float thta = viewer.theta  * toRad;
		float fai = viewer.phi * toRad;
		
		//�۲������������ϵ�е������������
		float xViewer = (float)(viewer.viewDistance * Math.cos(fai) * Math.cos(thta));
		float yViewer = (float)(viewer.viewDistance * Math.cos(fai) * Math.sin(thta));
		float zViewer = (float)(viewer.viewDistance * Math.sin(fai));
		
		
		//i�������ѭ��,j����ĸ���ѭ��
		int i,j;
		for(i = 0;i < facet.length;i++)
		{
			if(facet[i].visibility)
			{
				//clrFacetΪ������չ�����
				ColorFloat clrFacet = new ColorFloat(0.0f,0.0f,0.0f,1.0f);
					
				/*
				�����һ��:ԭ��������ɢ�Ĺ�ǿ
				----��������ɢ�Ĺ���Ϊ��,�ɲ��ʵ�emission����,�����κ�ϵ��ֱ�Ӽ��뵽��ǿ֮��
				*/
				clrFacet.red = material.emission.red;
				clrFacet.green = material.emission.green;
				clrFacet.blue = material.emission.blue;
					
				//�������ڶ���:ȫ�ֻ����������----ȫ�ֻ�����Ĺ�ǿ���Բ��ʵĻ�������
					
				//ǿ��
				float r = globalAmbient.red * material.ambient.red;
				float g = globalAmbient.green * material.ambient.green;
				float b = globalAmbient.blue * material.ambient.blue;
					
				//�����ȥ,�����Ѱ�������
				clrFacet.red += r;
				clrFacet.green += g;
				clrFacet.blue += b;
			
				//����һ��������ĵ�
			
				Vertex3d vertexCenter = new Vertex3d(0.0f,0.0f,0.0f);
			
				Facet3 facet_3 = new Facet3(0,0,0);
				Facet4 facet_4 = new Facet4(0,0,0,0);
				int type = facet[i].type;
			
				//�ƽ��������е�ĵ�������
				int iteration = 0;
				//if-else if{}������С�����ĵļ���
				if(type == 3)
				{
					facet_3 = (Facet3)facet[i];
					
					//�����ε����ĵ�
					vertexCenter.x = (vertex[facet_3.index[0]].x + vertex[facet_3.index[1]].x + vertex[facet_3.index[2]].x) / 3.0f;
					vertexCenter.y = (vertex[facet_3.index[0]].y + vertex[facet_3.index[1]].y + vertex[facet_3.index[2]].y) / 3.0f;
					vertexCenter.z = (vertex[facet_3.index[0]].z + vertex[facet_3.index[1]].z + vertex[facet_3.index[2]].z) / 3.0f;
				}
				else if(type == 4)
				{
					facet_4 = (Facet4)facet[i];
					
					//������0-1-2������
					float xc_012 = (vertex[facet_4.index[0]].x + vertex[facet_4.index[1]].x + vertex[facet_4.index[2]].x) / 3.0f;
					float yc_012 = (vertex[facet_4.index[0]].y + vertex[facet_4.index[1]].y + vertex[facet_4.index[2]].y) / 3.0f;
					float zc_012 = (vertex[facet_4.index[0]].z + vertex[facet_4.index[1]].z + vertex[facet_4.index[2]].z) / 3.0f;
	
					//������0-2-3������
					float xc_023 = (vertex[facet_4.index[0]].x + vertex[facet_4.index[2]].x + vertex[facet_4.index[3]].x) / 3.0f;
					float yc_023 = (vertex[facet_4.index[0]].y + vertex[facet_4.index[2]].y + vertex[facet_4.index[3]].y) / 3.0f;
					float zc_023 = (vertex[facet_4.index[0]].z + vertex[facet_4.index[2]].z + vertex[facet_4.index[3]].z) / 3.0f;
	
					
					vertexCenter.x = (xc_012 + xc_023) / 2.0f;
					vertexCenter.y = (yc_012 + yc_023) / 2.0f;
					vertexCenter.z = (zc_012 + zc_023) / 2.0f;
					
				}
			
				/*
					����һ��ѭ���������������:�ӹ�Դ���Ĺ���
				*/
			
				//��С�����ĵ�ķ��߾�����ķ���
				vertexCenter.normal = facet[i].normal;
			
				for(j = 0;j < light.length;j++)
				{	
					//ֻ�е����ǿ�����,��Դ���ܷ���,����Ż��øù������
					if(light[j].on_off == true)
					{
						//��һ��:����˥��ϵ��
						float attenuation = 1.0f;
						
						/*
							�����ԴΪһ�����Դ(λ�ù�Դ)����Ҫ���㱻���յĵ�����Դ�ľ���,�Լ���˥��ϵ��
							�����ƽ�й�Դ(�����Դ),��˥��
						*/
						if(light[j].type_source == Light.POSITIONAL_SOURCE)
						{
							float c0 = light[j].constant_attenuation;
							float c1 = light[j].linear_attenuation;
							float c2 = light[j].quadratic_attenuation;
							
							if(c0 < 0.000001f) c0 = 0.000001f;
							attenuation = c0;
							
							if((c1 > 0.000001f) ||(c2 > 0.000001f))
							{
								float x_x = light[j].position.x - vertexCenter.x ;
								float y_y = light[j].position.y - vertexCenter.y ;
								float z_z = light[j].position.z - vertexCenter.z ;
							
								//���㶥���Դ�ľ���
								float distance = (float)Math.sqrt(x_x * x_x + y_y * y_y + z_z * z_z);
								
								//ͨ��˥����������˥��ϵ��
								attenuation = c0 + c1 * distance + c2 * distance * distance;
				
							}

							attenuation = 1.0f / attenuation;
							attenuation = Math.min(attenuation,1.0f);
						}
						
						//�ڶ���:����۹�Ч������
						
						//�����Ϊ�۹�,���ֻ��Ӱ��λ�ڹ�׶֮�ڵĶ���,�����Ϊ����,��spotFactor�򵥵�Ϊ1.0f.
						float spotFactor = 1.0f;
						
						//�����Դ��һ�����Դ����һ���۹��,����о۹�Ч��
						if((light[j].type_source == Light.POSITIONAL_SOURCE) &&
						   (light[j].type == Light.SPOT_LIGHT))
						{
							if((light[j].spot_cutoff >= 0.0f) &&
							   (light[j].spot_cutoff <= 90.0f))
							{
								//�۹�ƹ�Դָ�򶥵����������������
								float v_x = vertexCenter.x - light[j].position.x;
								float v_y = vertexCenter.y - light[j].position.y;
								float v_z = vertexCenter.z - light[j].position.z;
								
								//�۹�ƹ�Դָ�򶥵������(viewer ---- vertex)
								Vector3d v_xyz = new Vector3d(v_x,v_y,v_z);
								
								//������λ��
								v_xyz.unitize();
								
								//��������v��۹�����䷽����������λ�����ĵ��
								float fInsideCone = v_xyz.dot(light[j].spot_direction);
								
								//ȡ0��������������
								fInsideCone = fInsideCone < 0.0f ? 0.0f : fInsideCone;
								
								//���fInsideConeС�ھ۹ⷢɢ��ǵ�����,������ö������ڹ�׶֮��,�۹����û���κ�Ӱ��
								if(fInsideCone < Math.cos(light[j].spot_cutoff * toRad))
									spotFactor = 0.0f;
								else
								{
									spotFactor = fInsideCone;
								
									//�۹�Ч������۹�ָ���й�,��Ӧ���Ǿ۹�ָ���Ĵη�
									fInsideCone = 1.0f;
									for(int m = 0;m < (int)light[j].spot_exponent;m++)
										fInsideCone *= spotFactor;
									spotFactor = fInsideCone;	
								}
								
							}
							
						}
						
						//������:��ɵ�����ļ���,��������Ǵӹ�Դ���Ĺ���
						
						//���㻷�����䲿��,��r,g,b���¸�ֵ:����������
						r = material.ambient.red * light[j].ambient.red;	
						g = material.ambient.green * light[j].ambient.green;	
						b = material.ambient.blue * light[j].ambient.blue;	
						
						//���������䲿��
						//�ڼ��������䲿��ʱ,Ҫ���㶥�㷨�������Ͷ���ָ���Դ��������������λ�����ĵ��,���нǵ�����
						
						//�ɶ���ָ���Դ����������������
						float x_l = light[j].position.x - vertexCenter.x ;
						float y_l = light[j].position.y - vertexCenter.y ;
						float z_l = light[j].position.z - vertexCenter.z ;
					
						//�ɶ���ָ���Դ������(vertex----viewer)
						Vector3d xyz_light = new Vector3d(x_l,y_l,z_l);
						
						/*
							�����ԴΪ�����Դ,�������,��ʱ,���λ��������Զ�㴦,
								�������ƽ�й�,�κζ�������Դ��������ƽ���ڹ�����䷽��
						*/
						
						if(light[j].type_source == Light.DIRECTIONAL_SOURCE)
						{
							xyz_light.x = -1.0f * light[j].position.x;
							xyz_light.y = -1.0f * light[j].position.y;
							xyz_light.z = -1.0f * light[j].position.z;
						}
						
						//��λ��
						xyz_light.unitize();
						
						float cos = xyz_light.dot(vertexCenter.normal);
						cos = cos < 0.0f ? 0.0f : cos;
						
						//���������䲿�ֵ�����RGB����
						float rDiffuse = material.diffuse.red * light[j].diffuse.red * cos;	
						float gDiffuse = material.diffuse.green * light[j].diffuse.green * cos;	
						float bDiffuse = material.diffuse.blue * light[j].diffuse.blue * cos;
						
						//���������䲿��
						r += rDiffuse;
						g += gDiffuse;
						b += bDiffuse;
						
						//����߹ⲿ��
						/*
							���ȷ�����Ҳȡ���ڹ��Ƿ�ֱ�����ڶ�����,
								������㷨�ߺ��ɶ���ָ���Դ������֮���С�ڻ����0.0,��û�и߹ⷴ��
						*/
						if(cos > 0.0f)
						{
							//����Ӷ���ָ���ӵ�ĵ�λ����
							float x_viewer = xViewer - vertexCenter.x;
							float y_viewer = yViewer - vertexCenter.y;
							float z_viewer = zViewer - vertexCenter.z;
							
							Vector3d xyz_viewer = new Vector3d(x_viewer,y_viewer,z_viewer);
							xyz_viewer.unitize();
							
							//���㶥�㵽��Դ�Ͷ��㵽�ӵ���������λ�����ĺ�
							Vector3d vector = xyz_viewer.add(xyz_light);
							vector.unitize();
							
							//���㶥�㷨������������֮���
							float shine = vector.dot(vertexCenter.normal);
							
							shine = shine < 0.0f ? 0.0f : shine;
							
							//������ɢ��
							float factor = 1.0f;
							for(int k = 0;k < (int)material.shininess;k++)
								factor *= shine;
							
							//����߹����������
							float rSpecular = material.specular.red * light[j].specular.red * factor;	
							float gSpecular = material.specular.green * light[j].specular.green * factor;	
							float bSpecular = material.specular.blue * light[j].specular.blue * factor;
							
							//������������
							r += rSpecular;
							g += gSpecular;
							b += bSpecular;
						}
						
						//����ֻҪ��˥��ָ���;۹�Ч���������,�͵õ������Թ�Դ�Ĺ���
						
						r *= (attenuation * spotFactor);
						g *= (attenuation * spotFactor);
						b *= (attenuation * spotFactor);
						
						//һ���������������
						clrFacet.red += r;
						clrFacet.green += g;
						clrFacet.blue += b;
					}//if
				}//for --j
			
				//�ù�ǿ����������������ɫ������ǿ��,��ʱ,��ɫӦΪС�����ɫ
				clrFacet.clamp();
				facet[i].color.red = clrFacet.red;
				facet[i].color.green = clrFacet.green;
				facet[i].color.blue = clrFacet.blue;
				//alpha	������Ϊ���ʵ����������alpha
				facet[i].color.alpha = material.diffuse.alpha;
			}//if(facet[i].visibility)--end
		}//for --i
	}
}


