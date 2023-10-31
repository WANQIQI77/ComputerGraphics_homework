/*
	A basic extension of the java.applet.Applet class
 */

import java.awt.*;
import java.applet.*;
import java.lang.*;

public class Phong extends Applet
{
	public void init()
	{
		//{{INIT_CONTROLS
		setLayout(null);
		setBackground(java.awt.Color.yellow);
		setSize(544,361);
		label1.setText("小球Z值：");
		add(label1);
		label1.setForeground(java.awt.Color.red);
		label1.setBounds(12,12,60,24);
		textFieldBallZ.setText("-100");
		add(textFieldBallZ);
		textFieldBallZ.setBackground(java.awt.Color.white);
		textFieldBallZ.setForeground(java.awt.Color.blue);
		textFieldBallZ.setBounds(84,12,84,24);
		label2.setText("点光源光强：");
		add(label2);
		label2.setForeground(java.awt.Color.red);
		label2.setBounds(180,12,72,24);
		textFieldIl.setText("2");
		add(textFieldIl);
		textFieldIl.setBackground(java.awt.Color.white);
		textFieldIl.setForeground(java.awt.Color.blue);
		textFieldIl.setBounds(264,12,84,24);
		label3.setText("泛光源光强：");
		add(label3);
		label3.setForeground(java.awt.Color.red);
		label3.setBounds(360,12,72,24);
		textFieldIa.setText("2");
		add(textFieldIa);
		textFieldIa.setBackground(java.awt.Color.white);
		textFieldIa.setForeground(java.awt.Color.blue);
		textFieldIa.setBounds(444,12,84,24);
		label4.setText("点光源X值：");
		add(label4);
		label4.setForeground(java.awt.Color.red);
		label4.setBounds(12,48,72,24);
		textFieldX.setText("0");
		add(textFieldX);
		textFieldX.setBackground(java.awt.Color.white);
		textFieldX.setForeground(java.awt.Color.blue);
		textFieldX.setBounds(84,48,84,24);
		label5.setText("点光源Y值：");
		add(label5);
		label5.setForeground(java.awt.Color.red);
		label5.setBounds(180,48,72,24);
		textFieldY.setText("0");
		add(textFieldY);
		textFieldY.setBackground(java.awt.Color.white);
		textFieldY.setForeground(java.awt.Color.blue);
		textFieldY.setBounds(264,48,84,24);
		label6.setText("点光源Z值：");
		add(label6);
		label6.setForeground(java.awt.Color.red);
		label6.setBounds(360,48,72,24);
		textFieldZ.setText("0");
		add(textFieldZ);
		textFieldZ.setBackground(java.awt.Color.white);
		textFieldZ.setForeground(java.awt.Color.blue);
		textFieldZ.setBounds(444,48,84,24);
		label7.setText("高光指数：");
		add(label7);
		label7.setForeground(java.awt.Color.red);
		label7.setBounds(12,84,72,24);
		textFieldN.setText("10");
		add(textFieldN);
		textFieldN.setBackground(java.awt.Color.white);
		textFieldN.setForeground(java.awt.Color.blue);
		textFieldN.setBounds(84,84,84,24);
		textFieldKd.setText("0.2");
		add(textFieldKd);
		textFieldKd.setBackground(java.awt.Color.white);
		textFieldKd.setForeground(java.awt.Color.blue);
		textFieldKd.setBounds(264,84,84,24);
		label8.setText("漫反射系数：");
		add(label8);
		label8.setForeground(java.awt.Color.red);
		label8.setBounds(180,84,72,24);
		buttonDraw.setLabel("画图");
		add(buttonDraw);
		buttonDraw.setBackground(java.awt.Color.blue);
		buttonDraw.setForeground(java.awt.Color.white);
		buttonDraw.setBounds(228,324,60,24);
		//}}
	
		//{{REGISTER_LISTENERS
		SymAction lSymAction = new SymAction();
		buttonDraw.addActionListener(lSymAction);
		//}}
	}
	
	//{{DECLARE_CONTROLS
	java.awt.Label label1 = new java.awt.Label();
	java.awt.TextField textFieldBallZ = new java.awt.TextField();
	java.awt.Label label2 = new java.awt.Label();
	java.awt.TextField textFieldIl = new java.awt.TextField();
	java.awt.Label label3 = new java.awt.Label();
	java.awt.TextField textFieldIa = new java.awt.TextField();
	java.awt.Label label4 = new java.awt.Label();
	java.awt.TextField textFieldX = new java.awt.TextField();
	java.awt.Label label5 = new java.awt.Label();
	java.awt.TextField textFieldY = new java.awt.TextField();
	java.awt.Label label6 = new java.awt.Label();
	java.awt.TextField textFieldZ = new java.awt.TextField();
	java.awt.Label label7 = new java.awt.Label();
	java.awt.TextField textFieldN = new java.awt.TextField();
	java.awt.TextField textFieldKd = new java.awt.TextField();
	java.awt.Label label8 = new java.awt.Label();
	java.awt.Button buttonDraw = new java.awt.Button();
	//}}
	
	int flag = 0;
    double n = 10;
	int cx,cy,cz;
	int light_x = 0,light_y = 0,light_z = 0;
	int r = 70;
	double Ia = 2,Il = 2;
	double kd = 0.2,ks=1.0 - kd;
	Math math;
	
	public void Ball()
	{
	    int ball_x,ball_y,ball_z;
	    int I;
	    double tempz,cosc,cosan,cosn,lr,lx,rn;
	    double hx,hy,hz;
	    Graphics g = this.getGraphics();
	    for(ball_x = cx - r;ball_x <= cx + r;ball_x ++)
			for(ball_y = cy - r;ball_y <= cy + r;ball_y ++)
			{
				tempz = (double)(r * r - (ball_x - cx) * 
				        (ball_x - cx) - (ball_y - cy)*(ball_y - cy));
				if(tempz > 0)
				{
					ball_z = cz - (int)math.sqrt(tempz);
					rn = lr = (double)((light_x - ball_x) * (light_x - ball_x) 
					    + (light_y - ball_y) * (light_y - ball_y)
						+ (light_z - ball_z) * (light_z - ball_z));
					hx = (double)(light_x - ball_x) / math.sqrt(lr);
					hy = (double)(light_y - ball_y) / math.sqrt(lr);
					hz = (double)(light_z - ball_z) / math.sqrt(lr);
					lx = (double)r;
					cosc = (double)(ball_x - cx) * hx / lx + (double)(ball_y-cy)
					    * hy / lx + (double)(ball_z - cz) * hz / lx;
					if(cosc < 0)cosc = 0;
					hz --;
					lr = hx * hx + hy * hy + hz * hz;
					hx = hx / math.sqrt(lr);
					hy = hy / math.sqrt(lr);
					hz = hz / math.sqrt(lr);
					cosn = (double)(ball_x - cx) * hx / lx + (double)(ball_y - cy)
					        * hy / lx + (double)(ball_z - cz) * hz / lx;
					cosan = math.pow(cosn,n);
					if(cosan < 0)cosan = 0;
					I = (int)((kd * Ia + kd * Il * cosc + ks * Il * cosan)
					    * 255 / (kd * Ia + kd * Il + ks * Il));
					if(I > 255)I = 255;
					Color color = g.getColor();
					Color colorp = new Color(0,I,0);
					g.setColor(colorp);
					g.drawLine(ball_x,ball_y,ball_x,ball_y);
				}
			}
    }

	class SymAction implements java.awt.event.ActionListener
	{
		public void actionPerformed(java.awt.event.ActionEvent event)
		{
			Object object = event.getSource();
			if (object == buttonDraw)
				buttonDraw_ActionPerformed(event);
		}
	}

	void buttonDraw_ActionPerformed(java.awt.event.ActionEvent event)
	{
		// to do: code goes here.
		Graphics g = this.getGraphics();
		Rectangle rect = getBounds();
		cx = rect.width/2;
		cy = rect.height/2 + 20;
		
		cz = Integer.valueOf(textFieldBallZ.getText()).intValue();
		Il = Double.valueOf(textFieldIl.getText()).doubleValue();
		Ia = Double.valueOf(textFieldIa.getText()).doubleValue();
		light_x = Integer.valueOf(textFieldX.getText()).intValue();
		light_y = Integer.valueOf(textFieldY.getText()).intValue();
		light_z = Integer.valueOf(textFieldZ.getText()).intValue();
		n = Double.valueOf(textFieldN.getText()).doubleValue();
		kd = Double.valueOf(textFieldKd.getText()).doubleValue();
		ks = 1.0 - kd;
		Ball();
	}
}
