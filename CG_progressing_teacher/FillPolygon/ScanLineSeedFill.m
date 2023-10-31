function ScanLineSeedFill
% 这是一个4连通内点定义区域的scan line seed fill algorithm
% 区域可以为凸或凹，也可以包含一到多个孔
% Seed(x,y) is the seed pixel
% Pop is a function for removing a pixel from the stack
% Push is a function for placing a pixel on the stack
%
% Copyright (c) Mar, 2006.  Zhou Dengwen.  All rights reserved.
% Department of Computer Science & Technology
% North China Electric Power University(Beijing)(NCEPU)
%
% 待填充的多边形
%x = [2, 5, 11, 11, 5, 2, 2]; 
%y = [2,1, 3, 8, 5, 7, 2];
%x = [1, 8, 8, 5, 1, 1]; 
%y = [1, 1, 6, 3, 7, 1];
x = [1, 3, 8, 8, 5, 1, 1]; 
y = [3, 3, 1, 6, 3, 7, 3];
%x = [3, 5, 5, 8, 10, 7, 5, 5, 1, 1, 3]; 
%y = [1, 1, 4, 4, 6, 8, 8, 7, 7, 3, 1];
%

xSeed = 4; ySeed = 4; % 指定种子点
xmin = min(x); xmax = max(x); % 多边形横坐标的范围
ymin = min(y); ymax = max(y); % 多边形纵坐标的范围

% 多边形扫描转换
PixelFlag = PolygonScanConversion(x,y);
title('4连通内点定义区域扫描线种子填充算法演示');
plot(xSeed,ySeed,'ys','MarkerSize',8,'MarkerFaceColor','r'); % 显示种子点
'按任意键继续...'
%pause;

% initialize
stack = []; top = 0; SeedPoint = [xSeed,ySeed];
stack = Push(stack,SeedPoint);

n_time = 0;
while size(stack,1) ~= 0 % 栈不空
    % get the seed pixel and set it to the new value
    [status,SeedPoint,stack] = Pop(stack);

    % save the x and y coordinate of the seed pixel
    Savex = SeedPoint(1); Savey = SeedPoint(2);
    
    % 填充种子点
    plot(Savex,Savey,'ys','MarkerSize',8,'MarkerFaceColor','r');
    pause(n_time); % 延时n_time秒
    PixelFlag(Savex-xmin+3,Savey-ymin+3) = 2;

    % fill the span to the right of the seed pixel
    xSeed = Savex + 1; ySeed = Savey;
    while PixelFlag(xSeed-xmin+3,ySeed-ymin+3) == 1
        plot(xSeed,ySeed,'ys','MarkerSize',8,'MarkerFaceColor','r');
        pause(n_time); % 延时n_time秒
        PixelFlag(xSeed-xmin+3,ySeed-ymin+3) = 2;
        xSeed = xSeed + 1;
    end
    
    % save the extreme right pixel
    Xright = xSeed - 1;
    
    % fill the span to the left of the seed pixel
    xSeed = Savex - 1;
    while PixelFlag(xSeed-xmin+3,ySeed-ymin+3) == 1
        plot(xSeed,ySeed,'ys','MarkerSize',8,'MarkerFaceColor','r');
        pause(n_time); % 延时n_time秒
        PixelFlag(xSeed-xmin+3,ySeed-ymin+3) = 2;
        xSeed = xSeed - 1;
    end
    
    % save the extreme left pixel
    Xleft = xSeed + 1;
    
    % 处理上一条扫描线
    xSeed = Xleft; ySeed = Savey + 1;
    while xSeed <= Xright
        spanNeedFill = false;
        while PixelFlag(xSeed-xmin+3,ySeed-ymin+3) == 1 & xSeed <= Xright
            spanNeedFill = true;
            xSeed = xSeed + 1;
        end
        
        % push the extreme right pixel onto the stack
        if spanNeedFill == true
            SeedPoint = [xSeed-1,ySeed]; 
            stack = Push(stack,SeedPoint);
            spanNeedFill = false;
        end
        
        % continue checking in case the span is interrupted
        while PixelFlag(xSeed-xmin+3,ySeed-ymin+3) ~= 1 & xSeed <= Xright xSeed = xSeed + 1; end
        
    end
    
    % 处理下一条扫描线
    xSeed = Xleft; ySeed = Savey - 1;
    while xSeed <= Xright
        spanNeedFill = false;
        while PixelFlag(xSeed-xmin+3,ySeed-ymin+3) == 1 & xSeed <= Xright
            spanNeedFill = true;
            xSeed = xSeed + 1;
        end
        
        % push the extreme right pixel onto the stack
        if spanNeedFill == true
            SeedPoint = [xSeed-1,ySeed]; 
            stack = Push(stack,SeedPoint);
            spanNeedFill = false;
        end
        
        % continue checking in case the span is interrupted
        while PixelFlag(xSeed-xmin+3,ySeed-ymin+3) ~= 1 & xSeed <= Xright xSeed = xSeed + 1; end
        
    end
    
end

hold off;

%-------------------------------------------------------------------
function stack = Push(stack,SeedPoint)
% 进栈
%
stack = [stack; SeedPoint];

%-------------------------------------------------------------------
function [status,SeedPoint,stack] = Pop(stack)
% 退栈
%
status = true;
top = size(stack,1);
if top ~= 0
   SeedPoint = stack(top,:);
   stack(top,:) = [];
else % 栈空，退栈失败
    status = false;
end

    
    