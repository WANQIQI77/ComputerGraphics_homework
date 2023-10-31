function ScanLineSeedFill
% ����һ��4��ͨ�ڵ㶨�������scan line seed fill algorithm
% �������Ϊ͹�򰼣�Ҳ���԰���һ�������
% Seed(x,y) is the seed pixel
% Pop is a function for removing a pixel from the stack
% Push is a function for placing a pixel on the stack
%
% Copyright (c) Mar, 2006.  Zhou Dengwen.  All rights reserved.
% Department of Computer Science & Technology
% North China Electric Power University(Beijing)(NCEPU)
%
% �����Ķ����
%x = [2, 5, 11, 11, 5, 2, 2]; 
%y = [2,1, 3, 8, 5, 7, 2];
%x = [1, 8, 8, 5, 1, 1]; 
%y = [1, 1, 6, 3, 7, 1];
x = [1, 3, 8, 8, 5, 1, 1]; 
y = [3, 3, 1, 6, 3, 7, 3];
%x = [3, 5, 5, 8, 10, 7, 5, 5, 1, 1, 3]; 
%y = [1, 1, 4, 4, 6, 8, 8, 7, 7, 3, 1];
%

xSeed = 4; ySeed = 4; % ָ�����ӵ�
xmin = min(x); xmax = max(x); % ����κ�����ķ�Χ
ymin = min(y); ymax = max(y); % �����������ķ�Χ

% �����ɨ��ת��
PixelFlag = PolygonScanConversion(x,y);
title('4��ͨ�ڵ㶨������ɨ������������㷨��ʾ');
plot(xSeed,ySeed,'ys','MarkerSize',8,'MarkerFaceColor','r'); % ��ʾ���ӵ�
'�����������...'
%pause;

% initialize
stack = []; top = 0; SeedPoint = [xSeed,ySeed];
stack = Push(stack,SeedPoint);

n_time = 0;
while size(stack,1) ~= 0 % ջ����
    % get the seed pixel and set it to the new value
    [status,SeedPoint,stack] = Pop(stack);

    % save the x and y coordinate of the seed pixel
    Savex = SeedPoint(1); Savey = SeedPoint(2);
    
    % ������ӵ�
    plot(Savex,Savey,'ys','MarkerSize',8,'MarkerFaceColor','r');
    pause(n_time); % ��ʱn_time��
    PixelFlag(Savex-xmin+3,Savey-ymin+3) = 2;

    % fill the span to the right of the seed pixel
    xSeed = Savex + 1; ySeed = Savey;
    while PixelFlag(xSeed-xmin+3,ySeed-ymin+3) == 1
        plot(xSeed,ySeed,'ys','MarkerSize',8,'MarkerFaceColor','r');
        pause(n_time); % ��ʱn_time��
        PixelFlag(xSeed-xmin+3,ySeed-ymin+3) = 2;
        xSeed = xSeed + 1;
    end
    
    % save the extreme right pixel
    Xright = xSeed - 1;
    
    % fill the span to the left of the seed pixel
    xSeed = Savex - 1;
    while PixelFlag(xSeed-xmin+3,ySeed-ymin+3) == 1
        plot(xSeed,ySeed,'ys','MarkerSize',8,'MarkerFaceColor','r');
        pause(n_time); % ��ʱn_time��
        PixelFlag(xSeed-xmin+3,ySeed-ymin+3) = 2;
        xSeed = xSeed - 1;
    end
    
    % save the extreme left pixel
    Xleft = xSeed + 1;
    
    % ������һ��ɨ����
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
    
    % ������һ��ɨ����
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
% ��ջ
%
stack = [stack; SeedPoint];

%-------------------------------------------------------------------
function [status,SeedPoint,stack] = Pop(stack)
% ��ջ
%
status = true;
top = size(stack,1);
if top ~= 0
   SeedPoint = stack(top,:);
   stack(top,:) = [];
else % ջ�գ���ջʧ��
    status = false;
end

    
    