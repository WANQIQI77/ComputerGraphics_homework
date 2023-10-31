function CohenSoutherlandLineClipping
% Cohen-Southerland线段裁剪算法的Matlab程序
%
% Copyright (c) 2005.  Zhou Dengwen.  All rights reserved.
% Department of Computer Science & Technology
% North China Electric Power University(Beijing)(NCEPU)
%

x0 = 3; y0 = 1; x1 = 13; y1 = 8; % 待裁剪的线段
%x0 = 10.5; y0 = 0.5; x1 = 12; y1 = 2.5;
%x0 = 6; y0 = 4; x1 = 10; y1 = 6;
x = [4 11 11 4 4]; y = [2 2 7 7 2]; % 裁剪窗口
XL = min(x); XR = max(x); YB = min(y); YT = max(y); 

% 设置绘图环境
hold on; axis equal;
grid on;

% 画出要裁剪的线段和裁剪窗口
plot([x0 x1],[y0 y1],'r-','LineWidth',2);
text(x0,y0,'\leftarrow P0','FontSize',16,'Color',[0,0,1]);
text(x1,y1,'\leftarrow P1','FontSize',16,'Color',[0,0,1]);
plot(x,y,'g-','LineWidth',1.5);
title('Cohen-Southerland线段裁剪算法演示');

% Cohen-Southerland线段裁剪算法实现
code0 = PointEncoding(x0,y0,XL,XR,YB,YT); % 计算端点P0(x0,y0)的编码
code1 = PointEncoding(x1,y1,XL,XR,YB,YT);  % 计算端点P1(x1,y1)的编码
LEFT = 1; 
RIGHT = 2; 
BOTTOM = 4; 
TOP = 8;
flag = 1;
while code0 ~= 0 | code1 ~= 0  % 线段P0P1至少有一个端点在窗口外
    
    % 计算线段与窗口边界的交点
    if bitand(code0,code1) ~= 0 % 线段完全在窗口外（即完全不可见）
       flag = 0; break;
    end
    if code0 ~= 0 code = code0; % 确定线段在窗口外的点
    else code = code1;
    end
    if bitand(LEFT,code) ~= 0  % 线段与窗口左边界相交
       x = XL;
       y = y0+(y1-y0)*(XL-x0)/(x1-x0);
    elseif bitand(RIGHT,code) ~= 0  % 线段与窗口右边界相交
       x = XR;
       y = y0+(y1-y0)*(XR-x0)/(x1-x0);
    elseif bitand(BOTTOM,code) ~= 0  % 线段与窗口下边界相交
       y = YB;
       x = x0+(x1-x0)*(YB-y0)/(y1-y0);
    elseif bitand(TOP,code) ~= 0  % 线段与窗口上边界相交
       y = YT;
       x = x0+(x1-x0)*(YT-y0)/(y1-y0);
   end
  
   % 修改线段的端点P0(x0,y0)或P1(x1,y1)
   if code == code0  
      x0 = x; y0 = y; code0 = PointEncoding(x,y,XL,XR,YB,YT);
   else
      x1 = x; y1 = y; code1 = PointEncoding(x,y,XL,XR,YB,YT);
   end
   
end

% 绘制裁剪后的线段
if flag ~= 0 % 线段不完全在窗口外（即线段部分或全部可见）
   plot([x0 x1],[y0 y1],'k-','LineWidth',3); 
end

% 恢复绘图环境
hold off; 

%---------------------------------------------
function code = PointEncoding(x,y,XL,XR,YB,YT)
% 计算窗口平面点的编码

% initialize
LEFT = 1; 
RIGHT = 2; 
BOTTOM = 4; 
TOP = 8;
code = 0;

% 计算点(x,y)的编码
if x < XL code = bitor(code,LEFT); end
if x > XR code = bitor(code,RIGHT); end
if y < YB code = bitor(code,BOTTOM); end
if y > YT code = bitor(code,TOP); end

