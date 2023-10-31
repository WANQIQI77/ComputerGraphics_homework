/*
	A basic extension of the java.applet.Applet class
 */

import java.awt.*;
import java.applet.*;

public class BezierSurf extends Applet
{
	public void init()
	{
		//{{INIT_CONTROLS
		setLayout(null);
		setBackground(java.awt.Color.yellow);
		setSize(571,377);
		panel1.setLayout(null);
		add(panel1);
		panel1.setBackground(java.awt.Color.green);
		panel1.setBounds(0,324,564,48);
		radioButtonWire.setCheckboxGroup(Group1);
		radioButtonWire.setState(true);
		radioButtonWire.setLabel("��ɫ�߿�ͼ");
		panel1.add(radioButtonWire);
		radioButtonWire.setForeground(java.awt.Color.blue);
		radioButtonWire.setBounds(12,12,96,24);
		radioButtonLighted.setCheckboxGroup(Group1);
		radioButtonLighted.setLabel("�����߿�ͼ");
		panel1.add(radioButtonLighted);
		radioButtonLighted.setForeground(java.awt.Color.blue);
		radioButtonLighted.setBounds(108,12,96,24);
		radioButtonSmooth.setCheckboxGroup(Group1);
		radioButtonSmooth.setLabel("����ʵ��ͼ");
		panel1.add(radioButtonSmooth);
		radioButtonSmooth.setForeground(java.awt.Color.blue);
		radioButtonSmooth.setBounds(204,12,84,28);
		buttonTrans.setLabel("���ɱ任");
		panel1.add(buttonTrans);
		buttonTrans.setBackground(java.awt.Color.cyan);
		buttonTrans.setForeground(java.awt.Color.red);
		buttonTrans.setBounds(300,12,60,28);
		label1.setText("�任������");
		panel1.add(label1);
		label1.setForeground(java.awt.Color.blue);
		label1.setBounds(372,12,60,16);
		textFieldTimes.setText("20");
		panel1.add(textFieldTimes);
		textFieldTimes.setBackground(java.awt.Color.white);
		textFieldTimes.setForeground(java.awt.Color.red);
		textFieldTimes.setBounds(432,12,60,24);
		buttonReturn.setLabel("��ԭ");
		panel1.add(buttonReturn);
		buttonReturn.setBackground(java.awt.Color.cyan);
		buttonReturn.setForeground(java.awt.Color.red);
		buttonReturn.setBounds(504,12,48,28);
		//}}
	
		//{{REGISTER_LISTENERS
		SymItem lSymItem = new SymItem();
		radioButtonWire.addItemListener(lSymItem);
		radioButtonLighted.addItemListener(lSymItem);
		radioButtonSmooth.addItemListener(lSymItem);
		SymAction lSymAction = new SymAction();
		buttonTrans.addActionListener(lSymAction);
		buttonReturn.addActionListener(lSymAction);
		//}}
		
		initParameters();
		initControlPoints1();
		initBezierSurface();
	}
	
	//{{DECLARE_CONTROLS
	java.awt.Panel panel1 = new java.awt.Panel();
	java.awt.Checkbox radioButtonWire = new java.awt.Checkbox();
	java.awt.CheckboxGroup Group1 = new java.awt.CheckboxGroup();
	java.awt.Checkbox radioButtonLighted = new java.awt.Checkbox();
	java.awt.Checkbox radioButtonSmooth = new java.awt.Checkbox();
	java.awt.Button buttonTrans = new java.awt.Button();
	java.awt.Label label1 = new java.awt.Label();
	java.awt.TextField textFieldTimes = new java.awt.TextField();
	java.awt.Button buttonReturn = new java.awt.Button();
	//}}
	
	ViewFinder viewfinder;
	BezierSurface bezierSurface;
	int width,height;
	Thread surfThread;
	
	//�ó�Ա����ȡ����ֵ,Ϊ0ʱ��ʾ���Ƶ�һ��ɫ���߿�ģ��,Ϊ1ʱ��ʾ���ƾ������պ���߿�ģ��,Ϊ2ʱ,�����һ��ʵ������,���Ը�������й���
	int i_solid_wire = 0;;

	//����˫����
	Image offScreenImage;
	Graphics offScreen;
	
	//Bezier����Ŀ��ƶ���
	Vertex3d controlPoints[][] = new Vertex3d[4][4];
	
	public void start()
	{
		this.setEnabled(true);
	}
	
	public void stop()
	{
		this.setEnabled(false);
	}
	
	public void run(){}
	
	public void initParameters()
	{
	    Rectangle rect = this.getBounds();
		width = rect.width;
		height = rect.height;
		Point center = new Point(((width - 20) / 2),(30 + (height - 40) / 2));
		viewfinder = new ViewFinder(0.0f,1.57f,200.0f,300.0f,center,1.0f,1.0f);

		offScreenImage = createImage(width,height);
		offScreen = offScreenImage.getGraphics();
	}
	
	public void initControlPoints1()
	{
	    float u = -60.0f;
		float v = -60.0f;
		for(int i = 0;i < 4;i++)
		{
			v = -60.0f;
			for(int j = 0;j < 4;j++)
			{
				controlPoints[i][j] = new Vertex3d(u ,v,0.0f,1.0f);
				v += 40.0f;
			}
			
			u += 40.0f;
		}
		
		controlPoints[1][0].z = 30.0f;
		controlPoints[1][1].z = 30.0f;
		controlPoints[1][2].z = 30.0f;
		controlPoints[1][3].z = 30.0f;
		
		controlPoints[2][0].z = 30.0f;
		controlPoints[2][1].z = 30.0f;
		controlPoints[2][2].z = 30.0f;
		controlPoints[2][3].z = 30.0f;
	}
	
	public void initControlPoints2()
	{
	    float r;
	    for(int i = 0;i < 4;i ++)
	        for(int j = 0;j < 4;j ++)
	        {
	            r = (float)(Math.random()) * 10.0f - 5.0f;
	            controlPoints[i][j].x += r;
	            controlPoints[i][j].y += r;
	            controlPoints[i][j].z += r;
	        }
	}
	
	public void initBezierSurface()
	{
		bezierSurface = new BezierSurface(15,15);
		
		//���������굽�۲�����ı任����
		Matrix3d transView = new Matrix3d();
		transView.toViewCoordinate(viewfinder.theta ,viewfinder.phi,viewfinder.viewDistance);
		
		//���α任����
		Matrix3d transGeometry = new Matrix3d();

		transGeometry.rotateZ(90.0f);
		transGeometry.rotateY(45.0f);
		
		//���ó����任����
		bezierSurface.setSceneTransform(transView,viewfinder);

		//���ù����Ǳ������
		bezierSurface.enable_lighting = true;
		
		//���ü��ο��Ƶ�
		bezierSurface.setGeometryControlPoints(Bezier.BEZIER_MAP2_VERTEX_3,controlPoints);

		bezierSurface.transform(transGeometry);
		bezierSurface.perspective();
		
		//����Ҳ���ż��α任�ı任���任
		for(int i = 0;i < bezierSurface.getVertexCount();i++)
		{
			Vector3d normal = new Vector3d(0.0f,0.0f,0.0f);
			normal = bezierSurface.vertex[i].normal.rotateZ(90.0f);
			
			bezierSurface.vertex[i].normal = normal.rotateY(45.0f);
		}
		
	}

    public void draw_groupBox_border(Graphics g)
	{
		BorderRectangle br = new BorderRectangle(10,30,width - 20,height - 40);
		br.setBkColor(Color.black);
		br.draw(g);
	}
	
	public void drawSurface(Graphics g)
	{
	    draw_groupBox_border(offScreen);

		Cursor cursorWait = new Cursor(Cursor.WAIT_CURSOR);
		this.setCursor(cursorWait);

		//���ù�Դ
		Light[] light = new Light[1];
		
		//����һ��������Դ
		light[0] = new Light(0);
		
		light[0].setType(Light.OMNI_LIGHT);
		float amb_omni[] = {0.2f,0.2f,0.2f,1.0f};
		float dif_omni[] = {1.0f,1.0f,1.0f,1.0f};
		float spe_omni[] = {1.0f,1.0f,1.0f,1.0f};
		float pos_omni[] = {0.0f,20.0f,150.0f,1.0f};
		light[0].lightfv(Light.AMBIENT,amb_omni);
		light[0].lightfv(Light.DIFFUSE,dif_omni);
		light[0].lightfv(Light.SPECULAR,spe_omni);
		light[0].lightfv(Light.POSITION,pos_omni);
		
		//���ò���
		Material material = new Material();
		float ambientMaterial[] = {0.3f,0.3f,0.3f,1.0f};
		float diffuseMaterial[] = {0.2f,0.8f,0.8f,1.0f};
		float specularMaterial[] = {1.0f,1.0f,1.0f,1.0f};
		float shininess = 80.0f;
		
		material.materialfv(Material.FACE_FRONT,Material.AMBIENT,ambientMaterial);
		material.materialfv(Material.FACE_FRONT,Material.DIFFUSE,diffuseMaterial);
		material.materialfv(Material.FACE_FRONT,Material.SPECULAR,specularMaterial);
		material.materialf(Material.FACE_FRONT,Material.SHININESS,shininess);

		//��������
		LightingSystem lighting = new LightingSystem();
		float globalAmbient[] = {0.5f,0.5f,0.5f,1.0f};
		lighting.lightModelfv(LightingSystem.LIGHT_MODEL_AMBIENT,globalAmbient);
		
		//�߿�ģ��,��һ��ɫ
		if(i_solid_wire == 0)
		{
			int iTotalEdges = bezierSurface.getEdgeCount();
		
			ColorFloat colorWire = new ColorFloat(0.0f,1.0f,0.0f);
			for(int i = 0;  i < iTotalEdges;i++)
				bezierSurface.setEdgeColor(i,colorWire);
		
			bezierSurface.render(offScreen,Renderer.RENDER_WIRE );
		}
		//�߿�ģ��,���й���Ч��
		else if(i_solid_wire == 1)
		{
			lighting.lighting(light,material,bezierSurface.vertex,viewfinder );
			bezierSurface.render(offScreen,Renderer.RENDER_WIRE_LIGHTED  );
		}
		//ʵ������
		else
		{
			lighting.lighting(light,material,bezierSurface.vertex,viewfinder );
			bezierSurface.render(offScreen,Renderer.RENDER_GOURAUD_SMOOTH );
		}
		
		//���Ƶ���Ļ
		g.drawImage(offScreenImage,0,0,this);
		
		Cursor cursorDefault = new Cursor(Cursor.DEFAULT_CURSOR );
		this.setCursor(cursorDefault);
	}
	
	public void paint(Graphics g)
    {
        drawSurface(this.getGraphics());
    }
	
	class SymItem implements java.awt.event.ItemListener
	{
		public void itemStateChanged(java.awt.event.ItemEvent event)
		{
			Object object = event.getSource();
			if (object == radioButtonWire)
				radioButtonWire_ItemStateChanged(event);
			else if (object == radioButtonLighted)
				radioButtonLighted_ItemStateChanged(event);
			else if (object == radioButtonSmooth)
				radioButtonSmooth_ItemStateChanged(event);
		}
	}

	void radioButtonWire_ItemStateChanged(java.awt.event.ItemEvent event)
	{
		i_solid_wire = 0;
		drawSurface(this.getGraphics());
	}

	void radioButtonLighted_ItemStateChanged(java.awt.event.ItemEvent event)
	{
		i_solid_wire = 1;
		drawSurface(this.getGraphics());
	}

	void radioButtonSmooth_ItemStateChanged(java.awt.event.ItemEvent event)
	{
		i_solid_wire = 2;
		drawSurface(this.getGraphics());
	}

	class SymAction implements java.awt.event.ActionListener
	{
		public void actionPerformed(java.awt.event.ActionEvent event)
		{
			Object object = event.getSource();
			if (object == buttonTrans)
				buttonTrans_ActionPerformed(event);
			else if (object == buttonReturn)
				buttonReturn_ActionPerformed(event);
		}
	}

	void buttonTrans_ActionPerformed(java.awt.event.ActionEvent event)
	{
	    int times = Integer.valueOf(textFieldTimes.getText()).intValue();
	    i_solid_wire = 0;
	    radioButtonWire.setState(true);
	    for(int i = 0;i < times;i ++)
	    {
		    initParameters();
		    initControlPoints2();
		    initBezierSurface();
		    drawSurface(this.getGraphics());
		    try
		      {
		        surfThread.sleep(500);
		      }
		      catch(InterruptedException e){}
		}
	}

	void buttonReturn_ActionPerformed(java.awt.event.ActionEvent event)
	{
	    i_solid_wire = 0;
	    radioButtonWire.setState(true);
		initParameters();
		initControlPoints1();
		initBezierSurface();
		drawSurface(this.getGraphics());	 
	}
}
