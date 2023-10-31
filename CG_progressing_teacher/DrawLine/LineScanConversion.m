function [xScan,yScan] = LineScanConversion(x0,y0,x1,y1)
% 用Bresenham算法扫描变换线段(x0,y0)~(x1,y1)
%
% Copyright (c) 2005.  Zhou Dengwen.  All rights reserved.
% Department of Computer Science & Technology
% North China Electric Power University(Beijing)(NCEPU)
%

x = x0; y = y0; xScan = []; yScan = []; % 初始化变量
dx = abs(x1-x0); dy = abs(y1- y0); 
s1 = sign(x1-x0); s2 = sign(y1-y0);
if dy > dx % 根据直线的斜率，交换dx和dy
   temp = dx; dx = dy; dy = temp; interchange = 1;
else 
   interchange = 0;
end
e = 2*dy-dx; % 初始化误差项
for i = 1:dx % 主循环
    xScan = [xScan x]; yScan = [yScan y];
	if e > 0
       if interchange == 1 x = x + s1; else y = y + s2; end
	   e = e-2*dx;
    end
	if interchange == 1 y = y + s2; else x = x + s1; end
	e = e + 2*dy;
end
xScan = [xScan x1]; yScan = [yScan y1];
