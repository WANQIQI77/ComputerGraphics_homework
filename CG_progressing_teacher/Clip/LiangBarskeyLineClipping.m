function LiangBarskeyLineClipping
% 梁友栋-Barskey线段裁剪算法的Matlab程序
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
title('梁友栋-Barskey线段裁剪算法演示');

% 梁友栋-Barskey线段裁剪算法实现
u0 = 0; u1 = 1; dx = x1 - x0; dy = y1 - y0;
[u0, u1, flag] = ParameterU(-dx, x0-XL, u0, u1);  % 计算待裁剪线段与裁剪窗口左边界交点，并选取参数u
if flag
   [u0, u1, flag] = ParameterU(dx, XR-x0, u0, u1);% 计算待裁剪线段与裁剪窗口右边界交点，并选取参数u
   if flag
      [u0, u1, flag] = ParameterU(-dy, y0-YB, u0, u1);  % 计算待裁剪线段与裁剪窗口下边界交点，并选取参数u
      if flag
         [u0, u1, flag] = ParameterU(dy, YT-y0, u0, u1);  % 计算待裁剪线段与裁剪窗口上边界交点，并选取参数u
         if flag
            plot([x0+u0*dx x0+u1*dx],[y0+u0*dy y0+u1*dy],'k-','LineWidth',3);  % 绘制裁剪后的线段(参数u0~u1之间的线段)
         end
      end
   end
end

% 恢复绘图环境
hold off; 

%------------------------------------------------------------------
function [u0, u1, VisibleFlag] = ParameterU(p, q, u0, u1)
% 本函数计算待裁剪线段与裁剪窗口边界交点的参数u，并正确地选取参数u

VisibleFlag = true;
if p < 0  % 线段从窗口外部指向窗口内部,u取较大的
   r = q/p;
   if r > u1  VisibleFlag = false; % 不合理的u值，交点在窗口边界的延长线上
   elseif r > u0
       u0 = r;
   end
else
    if p > 0  % 线段从窗口内部指向窗口外部，u取较小的
       r = q/p;
	   if r < u0  VisibleFlag = false;  % 不合理的u值，交点在窗口边界的延长线上
	   elseif r < u1 
           u1 = r;
       end       
    else
       if q < 0 VisibleFlag = false; end  % p为0,q<0 时，线段在窗口外
    end
end