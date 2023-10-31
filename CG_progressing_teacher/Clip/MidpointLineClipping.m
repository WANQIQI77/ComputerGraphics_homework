function MidpointLineClipping
% 中点分割线段裁剪算法的Matlab程序
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
title('中点分割线段裁剪算法演示');

% 中点分割线段裁剪算法实现
[xv0,yv0,flag] = ProximateVisiblePoint(x0,y0,x1,y1,XL,XR,YB,YT);  % 计算与P0(x0,y0)最近的可见点PV0(xv0,yv0);
if flag ~= false
   [xv1,yv1,flag] = ProximateVisiblePoint(x1,y1,x0,y0,XL,XR,YB,YT);  % 计算与P1(x1,y1)最近的可见点PV1(xv1,yv1);
end

% 绘制裁剪后的线段
if flag ~= false % 线段不完全在窗口外（即线段部分或全部可见）
   plot([xv0 xv1],[yv0 yv1],'k-','LineWidth',3); 
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

%-------------------------------------------------------------------
function [xv,yv,VisibleFlag] = ProximateVisiblePoint(x0,y0,x1,y1,XL,XR,YB,YT)
% P0(x0,y0)和P1(x1,y1)是待裁剪线段的两个端点，
% XL,XR,YB,YT是裁剪窗口的边界
% 本函数计算距P0最近的可见点

code0 = PointEncoding(x0,y0,XL,XR,YB,YT);  % 计算P0的编码
VisibleFlag = true; error = 0.01;
if code0 == 0
   xv = x0; yv = y0;
else
   code1 = PointEncoding(x1,y1,XL,XR,YB,YT);  % 计算P1的编码
   if bitand(code0,code1) == 0 % P0P1不是显然不可见
              
      % 连续中点分割计算距P0(x0,y0)最近的可见点
      while 1  
        xm = (x0+x1)/2; ym = (y0+y1)/2;  % 计算P0P1的中点Pm
        if abs(xm-x1) > error | abs(ym-y1) > error
            codem = PointEncoding(xm,ym,XL,XR,YB,YT);  % 计算Pm的编码
            if bitand(code0,codem) ~= 0
                x0 = xm; y0 = ym;
                code0 = codem;  % 更新P0的编码
                if bitand(code0,code1) ~= 0  % 重新检查新P0P1，如果是否显然不可见
                   VisibleFlag = false; 
                   xv = 0; yv = 0;  % 给(xv,yv)随便赋值，因为此时(xv,yv)没有意义
                   break;
                end
            else
                x1 = xm; y1 = ym;
                code1 = codem;  % 更新P1的编码
            end
        else
            xv = xm; yv = ym;
            break;
        end
      end  % 与while匹配
      
   else
      VisibleFlag = false;
   end
end

