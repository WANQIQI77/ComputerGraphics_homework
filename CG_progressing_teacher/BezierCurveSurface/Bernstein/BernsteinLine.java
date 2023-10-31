import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.lang.*;
import java.lang.Math.*;

public class BernsteinLine extends Applet implements ActionListener
{
	TextField s;
	TextField e;
	TextField tf;
	String s1;
	int num1,num2;
	int showflag=0;
	
	public void init()
	{
		Panel pl=new Panel();
		this.setBackground(Color.black);
		pl.setBackground(Color.green);
		setLayout(new BorderLayout());
		
		add("South",pl);
		
		tf=new TextField(30);
		tf.setBackground(Color.white);
		add("North",tf);
		
		pl.add(new Label("次数"));
		s=new TextField("3",4);
		pl.add(s);
		pl.add(new Label("基函数序号"));
		e=new TextField("0",4);
		pl.add(e);
		
		Button b=null;
	  b=new Button("Show");
	  b.addActionListener(this);
	  pl.add(b);
	  b=new Button("ShowAll");
	  b.addActionListener(this);
	  pl.add(b);
	  b=new Button("Clear");
	  b.addActionListener(this);
	  pl.add(b);
	  
    s1="请选择操作";
    tf.setText(s1);

	}
	
	public void start()
	{
		this.setEnabled(true);
	}
	
	public void stop()
	{
		this.setEnabled(false);
	}
	
	public void paint(Graphics g)
	{
		g.setColor(Color.blue);
		g.drawLine(10,340,10,30);
		g.drawLine(10,30,5,35);
		g.drawLine(10,30,15,35);
		g.setColor(Color.yellow);
		g.drawLine(7,40,13,40);
		g.drawString("1",0,40);
    g.drawString("Y",20,40);
		
    g.setColor(Color.blue);
		g.drawLine(10,340,340,340);
		g.drawLine(340,340,335,335);
    g.drawLine(340,340,335,345);
    g.setColor(Color.yellow);
    g.drawLine(310,337,310,343);
		g.drawString("1",310,355);
		g.drawString("X",345,345);
		
		switch (showflag) 
			{
				case 0:
					break;
				case 1:										
	    Bernstein(num1,num2);
					break;
				case 2:
					for(int r=0;r<=num1;r++)
			                {
			                 Bernstein(num1,r);
			                 }
					break;
			}
	}
	
	public void Bernstein(int n, int i)
  {
  	Graphics g1=getGraphics();
		g1.setColor(Color.white);

    int t1=1,t2=1,t3=1;
  	int j;
  	double cnt;
  	double t=0.0;
  	double y0;
  	double y1;
  	double dt=0.001;
  	
  	if(n==0)
  	{
  		t1=1;
  	}
  	else
  	{
  		for(j=1;j<=n;j++)
  		{
  			t1=t1*j;
  		}
  	}
  	
  	
  	if(i==0)
  	{
  		t2=1;
  	}
  	else
  	{
  		for(j=1;j<=i;j++)
  		{
  			t2=t2*j;
  		}
  	}
  	
  	for(j=1;j<=n-i;j++)
  	{
  		t3=t3*j;
  	}
  	
  	cnt=(double)t1/(double)(t2*t3);
    
  	if(i==0)
  	{
  		y0=1;
  	}
  	else
  	{
  		y0=0;
  	}

    while(t<1)
  	{
  		y1=cnt*Math.pow(t,(double)i)*Math.pow((1.0-t),(double)(n-i));
			g1.drawLine((int)(10+t*300),(int)(340-y0*300),(int)((t+dt)*300+10),(int)(340-y1*300));
			y0=y1;
      t=t+dt;
  	}
  }

	
	public void actionPerformed(ActionEvent ev)
	{
		String label=ev.getActionCommand();
		int r;
		
		Graphics g1=getGraphics();
		
		num1=Integer.parseInt(s.getText().trim());
		num2=Integer.parseInt(e.getText().trim());
		
		if((num1<=0)||(num2<0))
		{
			if(num1<=0)
			s1="函数的次数应该大于0";
			if(num2<0)
			s1="基函数的序号应该不小于0";
			tf.setText(s1);
		}
		else
		{
			
		if(num2<=num1)
		{
		  if(label.equals("Show"))
		  {
		  	update(g1);
		  	showflag=1;
			  Bernstein(num1,num2);
			  s1="显示Bernstein基函数";
			  tf.setText(s1);
		  }
		  if(label.equals("ShowAll"))
		  {
		  	update(g1);
		  	showflag=2;
			  for(r=0;r<=num1;r++)
			  {
			    Bernstein(num1,r);
			  }
	     s1="显示Bernstein基函数";
       tf.setText(s1);
		  }
		}
		else
		{
		  s1="基函数的序号应该不大于函数的次数";
		  tf.setText(s1);
		}
	}	
		
		if(label.equals("Clear"))
		{
			repaint();
			showflag=0;
			s1="请选择操作";
			tf.setText(s1);
		}
	}
		
	
}