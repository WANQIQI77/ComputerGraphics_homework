/*
	A basic extension of the java.applet.Applet class
 */

import java.awt.*;
import java.applet.*;
import java.awt.image.*;

public class Antialias extends Applet
{
	public void init()
	{
		//{{INIT_CONTROLS
		setLayout(null);
		setBackground(java.awt.Color.yellow);
		setFont(new Font("Dialog", Font.PLAIN, 12));
		setSize(498,302);
		panel1.setLayout(null);
		add(panel1);
		panel1.setBackground(java.awt.Color.green);
		panel1.setFont(new Font("Dialog", Font.PLAIN, 12));
		panel1.setBounds(120,252,240,44);
		radioButtonOrigin.setCheckboxGroup(Group1);
		radioButtonOrigin.setState(true);
		radioButtonOrigin.setLabel("ԭͼ");
		panel1.add(radioButtonOrigin);
		radioButtonOrigin.setForeground(java.awt.Color.blue);
		radioButtonOrigin.setBounds(24,12,60,28);
		radioButtonAntialias.setCheckboxGroup(Group1);
		radioButtonAntialias.setLabel("������ͼ");
		panel1.add(radioButtonAntialias);
		radioButtonAntialias.setForeground(java.awt.Color.blue);
		radioButtonAntialias.setBounds(132,12,72,28);
		//}}
	
		//{{REGISTER_LISTENERS
		SymItem lSymItem = new SymItem();
		radioButtonOrigin.addItemListener(lSymItem);
		radioButtonAntialias.addItemListener(lSymItem);
		//}}
		
		initParameters();
		initCubes();
	}
	
	//{{DECLARE_CONTROLS
	java.awt.Panel panel1 = new java.awt.Panel();
	java.awt.Checkbox radioButtonOrigin = new java.awt.Checkbox();
	java.awt.CheckboxGroup Group1 = new java.awt.CheckboxGroup();
	java.awt.Checkbox radioButtonAntialias = new java.awt.Checkbox();
	//}}
	
	ViewFinder viewfinder;
	Cube cubeSolid;
	int width,height;
	boolean antialiased = false;
	//��������
	int ditherTimes = 13;
	//�ۻ�������
	Image imgAccumulation[];
	//�������ĳ���
	Image sceneAntialiased;
	//�볡��������Ӧ�ı任����
	Matrix3d ditherMatrix[];
	//ÿ����һ�γ���,�ͻ���һ������,���ǽ�����ת����Image����,������Ķ�ά��������ȡ������ֵ
	int pixelsScene[][];
	//��󾭹��������ĳ���������
	int pixelsAntialiased[];
	
	public void initParameters()
	{
	    Rectangle rect = this.getBounds();
		width = rect.width;
		height = rect.height;
		Point center = new Point((10 + (width - 20) / 2),(30 + (height - 40) / 2));
		viewfinder = new ViewFinder(0.0f,0.0f,200.0f,300.0f,center);
		
		pixelsScene = new int[ditherTimes][width * height];
		pixelsAntialiased = new int[width * height];
	
		MemoryImageSource memoryImage  = new MemoryImageSource(width,height,ColorModel.getRGBdefault(),pixelsAntialiased,0,width);
		
		sceneAntialiased = createImage(memoryImage);
		
		//��ʼ���ۻ�����
		imgAccumulation  = new Image[ditherTimes] ;
		ditherMatrix = new Matrix3d[ditherTimes];
		
		for(int i = 0;i < ditherTimes;i++)
		{
			imgAccumulation[i] = createImage(width, height);
			ditherMatrix[i] = new Matrix3d();
		}
	}
	
	//�������������ı任����
	public void initDitherMatrices()
	{
		Matrix3d geom = new Matrix3d();
		
		geom.rotateX(-15.0f);
		geom.rotateY(-15.0f);
		geom.rotateZ(55.0f);
				
		//�Գ����е��������΢С�Ķ���(��λ����΢С���ƶ�)
		ditherMatrix[1].translate(0.5f,0.0f,0.0f);
		ditherMatrix[2].translate(-0.5f,0.0f,0.0f);
		ditherMatrix[3].translate(0.0f,1.0f,0.0f);
		ditherMatrix[4].translate(0.0f,-1.0f,0.0f);

		ditherMatrix[5].translate(1.0f,1.0f,0.0f);
		ditherMatrix[6].translate(-1.0f,1.0f,0.0f);
		ditherMatrix[7].translate(-1.0f,-1.0f,0.0f);
		ditherMatrix[8].translate(1.0f,-1.0f,0.0f);
		ditherMatrix[9].translate(0.0f,1.0f,1.0f);
		ditherMatrix[10].translate(0.0f,1.0f,-1.0f);
		ditherMatrix[11].translate(1.0f,-1.0f,1.0f);
		ditherMatrix[12].translate(1.0f,-1.0f,-1.0f);

		for(int i = 0;i < this.ditherTimes;i++)
			ditherMatrix[i].multiply(geom,Matrix3d.RIGHT_MULTIPLIER);

	}
	
	//��������ϵͳ�Ͳ���
	public void light_material()
	{
		int i = 0;
		
		//���ù�Դ
		Light[] light = new Light[1];
		
		//����һ��������Դ
		light[0] = new Light(0);
		
		light[0].setType(Light.OMNI_LIGHT);
		float amb_omni[] = {0.2f,0.2f,0.2f,1.0f};
		float dif_omni[] = {1.0f,1.0f,1.0f,1.0f};
		float spe_omni[] = {1.0f,1.0f,1.0f,1.0f};
		float pos_omni[] = {200.0f,-40.0f,100.0f,1.0f};
		light[0].lightfv(Light.AMBIENT,amb_omni);
		light[0].lightfv(Light.DIFFUSE,dif_omni);
		light[0].lightfv(Light.SPECULAR,spe_omni);
		light[0].lightfv(Light.POSITION,pos_omni);
		
		//���ò���
		Material material = new Material();
		float ambientMaterial[] = {0.3f,0.3f,0.3f,1.0f};
		float diffuseMaterial[] = {0.0f,0.8f,0.8f,1.0f};
		float specularMaterial[] = {1.0f,1.0f,1.0f,1.0f};
		float shininess = 80.0f;
		
		material.materialfv(Material.FACE_FRONT,Material.AMBIENT,ambientMaterial);
		material.materialfv(Material.FACE_FRONT,Material.DIFFUSE,diffuseMaterial);
		material.materialfv(Material.FACE_FRONT,Material.SPECULAR,specularMaterial);
		material.materialf(Material.FACE_FRONT,Material.SHININESS,shininess);

		//��������
		LightingSystem lighting = new LightingSystem();
		float globalAmbient[] = {0.8f,0.8f,0.8f,1.0f};
		lighting.lightModelfv(LightingSystem.LIGHT_MODEL_AMBIENT,globalAmbient);
		
		lighting.lighting(light,material,cubeSolid.facet,cubeSolid.vertex,viewfinder );
	
	}

	public void initCubes()
	{
		//toView���������굽�۲�����ı任����
		Matrix3d toView = new Matrix3d();
		toView.toViewCoordinate(viewfinder.theta ,viewfinder.phi,viewfinder.viewDistance);
		
		initDitherMatrices();
		
		for(int i = 0;i < this.ditherTimes;i++)
		{
			cubeSolid = new Cube(65.0f,65.0f,65.0f);
			cubeSolid.transform(ditherMatrix[i]);

			cubeSolid.perspective(toView,viewfinder);
		
			//ȱʡ��С�淨��
			cubeSolid.initFacetNormal();	
			for(int j = 0;j < 6;j++)
			{
				cubeSolid.facet[j].normal.rotateX(-15.0f);
				cubeSolid.facet[j].normal.rotateY(-15.0f);
				cubeSolid.facet[j].normal.rotateZ(55.0f);
			}
		
			//���ռ���
			light_material();
			Graphics g = this.imgAccumulation[i].getGraphics();
			draw_groupBox_border(g);
			cubeSolid.render(g,Renderer.RENDER_FLAT);
		}
		getPixels();
	}
	
	public void getPixels()
	{
		//��������ץȡ��
		for(int i = 0;i < ditherTimes;i++)
		{
			PixelGrabber  pixelGrabber = new PixelGrabber(this.imgAccumulation[i],0,0,width,height,pixelsScene[i],0,width);
	 
			try
			{
				pixelGrabber.grabPixels ();
			}
			catch(InterruptedException e)
			{}
		}
	}
	
	public void draw_groupBox_border(Graphics g)
	{
		BorderRectangle br = new BorderRectangle(10,30,width - 20,height - 40);
		br.setBkColor(Color.black);
		br.draw(g);
	}
	
	public void drawCube(Graphics g)
	{
	    Cursor cursorWait = new Cursor(Cursor.WAIT_CURSOR);
		this.setCursor(cursorWait);

		SceneAccumulator sa = new SceneAccumulator();
		sa.accumulate(pixelsScene,pixelsAntialiased );
		if(antialiased)
		    g.drawImage(sceneAntialiased,0,0,this);
        else
		    g.drawImage(this.imgAccumulation[0],0,0,this);
		//�����ȴ�����
		Cursor cursorDefault = new Cursor(Cursor.DEFAULT_CURSOR );
		this.setCursor(cursorDefault);
	}
	
	public void paint(Graphics g)
	{
	    drawCube(this.getGraphics());
	}

	class SymItem implements java.awt.event.ItemListener
	{
		public void itemStateChanged(java.awt.event.ItemEvent event)
		{
			Object object = event.getSource();
			if (object == radioButtonOrigin)
				radioButtonOrigin_ItemStateChanged(event);
			else if (object == radioButtonAntialias)
				radioButtonAntialias_ItemStateChanged(event);
		}
	}

	void radioButtonOrigin_ItemStateChanged(java.awt.event.ItemEvent event)
	{
		antialiased = false;
		drawCube(this.getGraphics());
	}

	void radioButtonAntialias_ItemStateChanged(java.awt.event.ItemEvent event)
	{
		antialiased = true;
		drawCube(this.getGraphics());
	}
}
