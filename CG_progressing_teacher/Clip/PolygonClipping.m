function PolygonClipping
% 这是一个基于divide and conquer策略的Sutherland-Hodgeman多边形裁剪算法的Matlab程序
%
% Copyright (c) Mar, 2006.  Zhou Dengwen.  All rights reserved.
% Department of Computer Science & Technology
% North China Electric Power University(Beijing)(NCEPU)
%

% 输入参数
xp = [0.5, -2, -2, 1.5, 1.5, 0.5, 0.5, -1.5, -1.5, 0.5];  % 待裁剪的多边形
yp = [-1.5, -1.5, 2, 2, 0, 0, 1.5, 1.5, 0.5, -1.5];
%xp = [-2, 0.2, 1.6, -2];
%yp = [0.5, -2, 1.5, 0.5];
nVertex = length(xp)-1; % 多边形顶点数
xw = [-1 1 1 -1 -1]; yw = [-1 -1 1 1 -1];  % 矩形裁剪窗口(窗口边界为逆时针方向，左下角顶点是第一个顶点)

% 设置绘图环境
hold on; axis equal;
grid on;

% 画出要裁剪的多边形和裁剪窗口
plot(xp,yp,'b-','LineWidth',2);
for i = 1:nVertex
    text(xp(i),yp(i),['P' num2str(i) '(' num2str(xp(i)) ',' num2str(yp(i)) ')'], 'FontSize',16,'Color',[0,1,1]);
end
plot(xw,yw,'g-','LineWidth',1.5);
title('Sutherland-Hodgeman多边形裁剪算法演示');
'按任意键继续...'
pause;

% Sutherland-Hodgeman多边形裁剪算法实现
InVertX = xp; InVertY = yp; clr = ['r' 'c' 'm' 'k'];
xText = (xw(1)+xw(2))/2; yText =(yw(2)+yw(3))/2;
prompt = ['窗口下边界裁剪结果'; '窗口右边界裁剪结果'; '窗口上边界裁剪结果'; '窗口左边界裁剪结果'];
for j = 1:4
    [OutVertX,OutVertY] = SutherlandHodgmanClip(InVertX,InVertY,xw(j),yw(j),xw(j+1),yw(j+1));
    InVertX = OutVertX; InVertY = OutVertY;
    plot(OutVertX,OutVertY,[clr(j) '-'],'LineWidth',3); 
    text(xText,yText, prompt(j,:),'HorizontalAlignment','center','Background',[0.7 0.9 0.7],'Color',[0 0 0], ...
         'FontName','楷体_GB2312','FontSize', 18,'FontWeight','bold');
    '按任意键继续...'
    pause;
end

hold off; 

%--------------------------------------------------------------------------------------
function [OutVertX, OutVertY] = SutherlandHodgmanClip(InVertX, InVertY, x0, y0, x1, y1)
% 本函数计算多边形被裁剪窗口的一条边界裁剪结果

nVertex = length(InVertX); 
xs = InVertX(1); ys = InVertY(1);
OutVertX = []; OutVertY = [];
for j = 2:nVertex
    xp = InVertX(j); yp = InVertY(j);
    VisibleFlagP = inside(xp,yp,x0,y0,x1,y1);
    if VisibleFlagP
       VisibleFlagS = inside(xs,ys,x0,y0,x1,y1);
       if VisibleFlagS  % SP在窗口内，情况(1)
          OutVertX = [OutVertX, xp]; OutVertY = [OutVertY, yp]; 
       else  % S在窗口外, 情况(4)
          [xi,yi] = intersect(xs,ys,xp,yp,x0,y0,x1,y1);
          OutVertX = [OutVertX, xi]; OutVertY = [OutVertY, yi];
          OutVertX = [OutVertX, xp]; OutVertY = [OutVertY, yp];          
       end
    else
       VisibleFlagS = inside(xs,ys,x0,y0,x1,y1);
       if VisibleFlagS  % S在窗口内，P在窗口外,情况(3)
          [xi,yi] = intersect(xs,ys,xp,yp,x0,y0,x1,y1);
          OutVertX = [OutVertX, xi]; OutVertY = [OutVertY, yi];  
       end
       % 情况(2)没有输出
    end
    xs = xp; ys = yp;
end
OutVertX = [OutVertX,OutVertX(1)]; OutVertY = [OutVertY, OutVertY(1)];

%---------------------------------------------------------
function VisibleFlag = inside(x, y, x0, y0, x1, y1)
% 本函数判断点是否在裁剪边的可见侧

VisibleFlag = false;
if x1 > x0  % 裁剪边为窗口下边
   if y >= y0 VisibleFlag = true; end
elseif x1 < x0  % 裁剪边为窗口上边
	if y <= y0 VisibleFlag = true; end
elseif y1 > y0  % 裁剪边为窗口右边
	if x <= x0 VisibleFlag = true; end
elseif y1 < y0  % 裁剪边为窗口左边
	if  x >= x0 VisibleFlag = true; end
end

%-------------------------------------------------------------------------
function [xi, yi] = intersect(xs, ys, xp, yp, x0, y0, x1, y1)
% 本函数计算直线段SP与窗口边界的交点；

if y0 == y1  % 水平裁剪边
    yi = y0;
    xi = xs + (y0-ys)*(xp-xs)/(yp-ys);
else  % 垂直裁剪边
    xi = x0;
    yi = ys + (x0-xs)*(yp-ys)/(xp-xs);
end


