function PolygonEdgeMark
% 这是一个无内环多边形界标志扫描转换算法的Matlab程序
%
% Copyright (c) Mar, 2006.  Zhou Dengwen.  All rights reserved.
% Department of Computer Science & Technology
% North China Electric Power University(Beijing)(NCEPU)
%

% 要扫描转换的多边形。如果显示器分辨率为1024*768,则
% x坐标的范围为0~1023的整数, y坐标的范围为0~767的整数
x = [1, 8, 8, 5, 1, 1]; 
y = [1, 1, 6, 3, 7, 1];
%x = [2, 5, 11, 11, 5, 2, 2]; 
%y = [2,1, 3, 8, 5, 7, 2];
%x = [5, 1, 1, 11, 9, 5];
%y = [4, 1, 7, 11, 1, 4];
nVertex = length(x)-1; % 多边形顶点数

% 设置绘图环境
hold on; axis equal;
grid on;

% 画出要扫描转换的多边形
plot(x,y,'b-','LineWidth',2);
for i = 1:nVertex
    text(x(i),y(i),['P' num2str(i) '(' num2str(x(i)) ',' num2str(y(i)) ')'], 'FontSize',16,'Color',[0,1,1]);
end
%axis([0 max(x) 0 max(y)]); 
title('无内环的多边形扫描转换界标志算法演示');

yinf = min(y); ysup = max(y);
xinf = min(x); xsup = max(x);
PixelFlag = zeros(xsup-xinf+10,ysup-yinf+10);
% 对多边形每一条边打上边标志
nVertex = length(x)-1; % 多边形顶点数
for i = 1:nVertex
    dy = y(1,i+1) - y(1,i);
    if dy == 0 continue; end  % 忽略水平边
    dx = (x(1,i+1) - x(1,i))/dy;
    if dy > 0 xScan = x(1,i); else xScan = x(1,i+1); end 
    ymin = min(y(1,i),y(1,i+1));
    ymax = max(y(1,i),y(1,i+1));
    for yScan = ymin+1:ymax
        xScan = round(xScan + dx);
        plot(xScan,yScan,'ro','MarkerSize',8,'MarkerFaceColor','y');
        if PixelFlag(xScan-xinf+1,yScan-yinf+1) == 0
            PixelFlag(xScan-xinf+1,yScan-yinf+1) = 1;
        elseif PixelFlag(xScan-xinf+1,yScan-yinf+1) == 1 % 处理上下边界点
            PixelFlag(xScan-xinf+1,yScan-yinf+1) = 0;
            plot(xScan,yScan,'wo','MarkerSize',8,'MarkerFaceColor','w'); % 檫除上部不配对的边标志
        end
    end
end
'按任意键继续...'
pause;

n_time = 2;
for i = yinf:ysup % 扫描线循环(外循环)
    inside = false;
    for j = xinf:xsup % 扫描线与多边形相交部分象素循环(内循环)
        if PixelFlag(j-xinf+1,i-yinf+1) == 1
            InsideT = inside;
            inside = ~inside;
            if InsideT == true & inside == false  % 填充右边界点
                plot(j,i,'ro','MarkerSize',8,'MarkerFaceColor','g');
                pause(n_time); % 延时n_time秒
            end
        end
        
        if inside == true
           plot(j,i,'ro','MarkerSize',8,'MarkerFaceColor','g');
           pause(n_time); % 延时n_time秒
        end
    end
end
