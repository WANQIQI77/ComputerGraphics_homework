import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;
import java.lang.Math.*;

public class BezierLine extends Applet implements ActionListener,MouseListener,MouseMotionListener,Runnable

{
	Thread lineThread;
	TextField tf;
	TextField s;
	int x[]=new int[100];//控制点
	int y[]=new int[100];
	int i=0;             //控制点数
	int no;				 //    
    int xx,yy;
	boolean flag=true;//drawn flag
	boolean flag1=true;//new flag
	boolean flag2=false;
	int flag3=0;//1:ready;2:mousedown;
	int lx[]=new int[100];
	int ly[]=new int[100];
//	int r1=85;
//	int g1=210;
//	int b1=85;	
	String str="";
	
	public void init()
	{
		this.setLayout(new BorderLayout());
		this.setSize(500,400);
		Panel pl=new Panel();
		pl.setLayout(new FlowLayout(FlowLayout.CENTER,10,5));
		this.setBackground(Color.black);
		pl.setBackground(Color.green);
		this.add("South",pl);
		
		tf=new TextField(30);
		this.add("North",tf);
		tf.setBackground(Color.white);
		tf.setEditable(false);
		
		Button b=null;
		b=new Button("Decasteljau");
	  	b.addActionListener(this);
	  	pl.add(b);
	  	b=new Button("Bezier");
	  	b.addActionListener(this);
	  	pl.add(b);
	  	b=new Button("升阶");
	  	b.addActionListener(this);
	  	pl.add(b);
	  	b=new Button("修改控制点");
	  	b.addActionListener(this);
	  	pl.add(b);		  
	  	b=new Button("重画");
	  	b.addActionListener(this);
	  	pl.add(b);
		
		pl.add(new Label("请输入t的值"));
		s=new TextField("0.5",4);
		pl.add(s);
	  
      	addMouseListener(this);
		addMouseMotionListener(this);
	  
	  	str="请用鼠标输入控制多边形顶点,然后选择Bezier命令生成曲线";
	  	tf.setText(str);
	}
	
	public void start()
	{
		this.setEnabled(true);
	}
	
	public void stop()
	{
		this.setEnabled(false);
	}
	
	public void run(){}
	
	//鼠标按下，如果是修改控制点状态判断是否在某个控制点附近 
	public void mousePressed(MouseEvent e)
	{
		if (flag3==1)
		{
			flag3=2;
			//searching
			int xx,yy,k;
			xx=e.getX();
			yy=e.getY();
			no=-1;k=1;
			while (k<=i)
			{
				if (Math.abs(xx-x[k])<=5&&Math.abs(yy-y[k])<=5)
					break;
				k++;
			}
			if (k<=i) no=k;
		}	
	}
	
	public void mouseReleased(MouseEvent e)
	{
		//reset state
		if (flag3==2) flag3=1;
	}
	
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
	
	//input control points
    public void mouseClicked(MouseEvent e)
    {
   		if(flag==true)
   		{
			if (i==99)
			{
				str="对不起,控制顶点最多只能有99个";
				tf.setText(str);
				return;
			}
			
			i=i+1;
  	  		Graphics gg=getGraphics();
      		x[i]=e.getX();
      		y[i]=e.getY();
      		gg.setColor(Color.yellow);
  	  		gg.drawRect(x[i]-1,y[i]-1,2,2);
			gg.setColor(Color.green);
  	  		if(i==1)
  	  		{

  	    		gg.drawLine(x[i],y[i],x[i],y[i]);
  	  		}
  	  		else
  	  		{  	    	
  	    		gg.drawLine(x[i-1],y[i-1],x[i],y[i]);
  	  		}
    	}
   	}
    
	public void mouseDragged(MouseEvent e)
	{
		if (flag3==2&&no>0)
		{
			str="修改控制顶点";
			tf.setText(str);
			Graphics gg=getGraphics();
			int xx,yy,k;
			xx=e.getX();
			yy=e.getY();
			x[no]=xx;
			y[no]=yy;
			update(gg);			
//			drawBezier();		
		}	
	}
	
    public void mouseMoved(MouseEvent e){}
	
	public void paint(Graphics g)
	{
		if (i>0)
		{
			int k;			
			g.setColor(Color.yellow);
			for (k=1;k<=i;k++)	
				g.drawRect(x[k]-1,y[k]-1,2,2);
			
			if (flag3==2)
			{						
				g.drawRect(x[no]-4,y[no]-4,8,8);
			}	

			g.setColor(Color.green);
			for (k=1;k<i;k++)			
				g.drawLine(x[k],y[k],x[k+1],y[k+1]);
			drawBezier();		
		}	
	}
		
	public void drawBezier()
	{
		  int m=i;
		  Graphics g1=getGraphics();
		  g1.setColor(Color.blue);
		  Draw(x,y,m,400);
		  flag=false;
	}
		
		//de casteljau 算法
	public void Decasteljau(int x1[],int y1[],int n, double t)
    {
	    int r,j;
	    double bx[]=new double[100];
	    double by[]=new double[100];
	    r=n;
	    
	    for(j=1;j<=n;j++)
	    {
		    bx[j]=x1[j];
		    by[j]=y1[j];
	    }

	    while(r>1)
	    {
	      for(j=1;j<r;j++)
	      {
	    	  bx[j]=bx[j]+t*(bx[j+1]-bx[j]);
	    	  by[j]=by[j]+t*(by[j+1]-by[j]);
	      }
	      r=r-1;
	    }

	    xx=(int)(bx[1]+0.5);
	    yy=(int)(by[1]+0.5); 
	}
	  
	//de casteljau 画法的演示
	public void drawDecasteljau(int x1[],int y1[],int n, double t)
    {
	    int r,j;
	    double bx[]=new double[100];
	    double by[]=new double[100];
	    Graphics g2=getGraphics();
	    r=n;
	    
	    update(g2);
		for(j=1;j<=n;j++)
	      {
		     bx[j]=x1[j];
		     by[j]=y1[j];
	       }

	    while(r>1)
	    {
	      for(j=1;j<r;j++)
	      {
	    	  bx[j]=bx[j]+t*(bx[j+1]-bx[j]);
	    	  by[j]=by[j]+t*(by[j+1]-by[j]);
	      }
	      for(j=1;j<r-1;j++)
	      {
	      	g2.setColor(Color.magenta);
	      	g2.drawLine((int)bx[j],(int)by[j],(int)bx[j+1],(int)by[j+1]);
	      
	        try
		      {
		        lineThread.sleep(1000);
		      }
		      catch(InterruptedException e){}
	      }
	      r=r-1;
	    }
	    	xx=(int)(bx[1]+0.5);
	        yy=(int)(by[1]+0.5);
	        g2.setColor(Color.green);
	        g2.drawRect(xx-1,yy-1,3,3);
	  }

    
	  //画曲线
    public void Draw(int x3[],int y3[],int n,int count)
    {								 
	    int j,m;
    	int dx[]=new int[100];
    	int dy[]=new int[100];
    	double dt = 1.0/(double)count;
	    double t = 0.0;
	    int x1,x2,y1,y2;
	    
	    m=n;
    	for(j=1;j<=m;j++)
    	{
    		dx[j]=x3[j];
    		dy[j]=y3[j];
    	}
    	
    	Decasteljau(dx,dy,m,0);
    	x1=xx;
    	y1=yy;
    	t=t+dt;
    	Graphics g1=getGraphics();
    	while(t<=1.0)
    	{
    	  Decasteljau(dx,dy,m,t);
          x2=xx;
          y2=yy;
          g1.setColor(Color.white);
          g1.drawLine(x1,y1,x2,y2);
          x1=x2;
          y1=y2;
          t=t+dt;
    	}
    }
    
    //升阶
    public void ascend(int x4[],int y4[])
    {
		int j;
    	int m;
    	int bx[]=new int[100];
    	int by[]=new int[100];
    	int dx[]=new int[100];
    	int dy[]=new int[100];
    	
    	m=i;
		i=0;
    	for(j=1;j<=m;j++)
    	{
    		bx[j]=x4[j];
    		by[j]=y4[j];
    	}
    	
    	Graphics gg=getGraphics();
    	update(gg);
    	gg.setColor(Color.yellow);

    	dx[1]=bx[1];
    	dy[1]=by[1];
      	dx[m+1]=bx[m];
      	dy[m+1]=by[m];
      	gg.drawRect(dx[1]-1,dy[1]-1,2,2);
      	gg.drawRect(dx[m+1]-1,dy[m+1]-1,2,2);

    	for(j=2;j<=m;j++)
    	{ 
    		dx[j]=(int)(((double)(j-1)/(double)(m+1))*bx[j-1]+(1-(double)(j-1)/(double)(m+1))*bx[j]);
    		dy[j]=(int)(((double)(j-1)/(double)(m+1))*by[j-1]+(1-(double)(j-1)/(double)(m+1))*by[j]);
     		gg.drawRect(dx[j],dy[j],2,2);
    	}

//		r1=(r1+85)%255;
//    	g1=(g1+85)%255;
//    	b1=(b1+85)%255;    	
    	gg.setColor(Color.green);
    	for(j=1;j<=m;j++)
    	{
    		gg.drawLine(dx[j],dy[j],dx[j+1],dy[j+1]);
    	}
    	Draw(dx,dy,m+1,400);
      	for(j=1;j<=m+1;j++)
    	{
    		x[j]=dx[j];
    		y[j]=dy[j];
    	} 
    	i=m+1;
    }
	
	public void actionPerformed(ActionEvent ev)
	{
		String label=ev.getActionCommand();
		//double t=Double.parseDouble(s.getText().trim());
    	Double t1=Double.valueOf(s.getText().trim());
    	double t=t1.doubleValue();

		
		if(label.equals("重画"))
			{
				i=0;
			  	repaint();
			  	flag=true;
				flag1=true;
				flag2=false;
				flag3=0;
//				this.setCursor(new Cursor(DEFAULT_CURSOR));
				str="请用鼠标输入控制多边形顶点,然后选择Bezier命令生成曲线";
	      		tf.setText(str);
			}
			
		if(label.equals("Decasteljau"))
		{
			if(flag2==true)
			{
				flag1=false;				
			  	if(t<=1.0)
			  	{
					str="De casteljau 画法演示";
					tf.setText(str);			
			    	drawDecasteljau(x,y,i,t);		
			  	}
			  	else
			  	{
				  	str="请输入小于1的t值";
	          		tf.setText(str);
				}
			}
			else
			{
				str="请先生成Bezier曲线";
	        	tf.setText(str);
			}
		}
			
		if(label.equals("升阶"))
		{
			if(flag2==true)
			{
			  	if (i==99)
			  	{
			  		str="对不起,控制顶点最多只能有99个";
			  		tf.setText(str);
			  		return;
			  	}
	
			  	ascend(x,y);
			  	flag1=false;
			  	str="升阶";		
			  	tf.setText(str);		  		  
			}
			else
			{
				str="请先生成Bezier曲线";
        		tf.setText(str);
			}
		}
	
		if(label.equals("Bezier"))
		{
			if(i>1)
			{
				if (flag2==false)
				{
					drawBezier();
					str="生成Bezier曲线";
					tf.setText(str);
					flag2=true;
					flag3=0;
//					this.setCursor(new Cursor(DEFAULT_CURSOR));
				}
				else
				{
					str="已生成Bezier曲线,请选择其他命令";
					tf.setText(str);
				}
			}
			else
			{
				str="请至少在画布上输入两个控制顶点";
			    tf.setText(str);
			}
			
		}
			
		if (label.equals("修改控制点"))
		{
			
			if (flag2==true)
			{
				flag3=1;	
//				this.setCursor(cursor);
				str="请点击需要修改的控制点并拖动";
				tf.setText(str);									
			}
			else
			{
				str="请先生成Bezier曲线";
				tf.setText(str);
			}
		}			
	}		
}
