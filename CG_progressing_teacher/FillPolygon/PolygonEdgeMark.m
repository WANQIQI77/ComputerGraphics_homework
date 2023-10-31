function PolygonEdgeMark
% ����һ�����ڻ�����ν��־ɨ��ת���㷨��Matlab����
%
% Copyright (c) Mar, 2006.  Zhou Dengwen.  All rights reserved.
% Department of Computer Science & Technology
% North China Electric Power University(Beijing)(NCEPU)
%

% Ҫɨ��ת���Ķ���Ρ������ʾ���ֱ���Ϊ1024*768,��
% x����ķ�ΧΪ0~1023������, y����ķ�ΧΪ0~767������
x = [1, 8, 8, 5, 1, 1]; 
y = [1, 1, 6, 3, 7, 1];
%x = [2, 5, 11, 11, 5, 2, 2]; 
%y = [2,1, 3, 8, 5, 7, 2];
%x = [5, 1, 1, 11, 9, 5];
%y = [4, 1, 7, 11, 1, 4];
nVertex = length(x)-1; % ����ζ�����

% ���û�ͼ����
hold on; axis equal;
grid on;

% ����Ҫɨ��ת���Ķ����
plot(x,y,'b-','LineWidth',2);
for i = 1:nVertex
    text(x(i),y(i),['P' num2str(i) '(' num2str(x(i)) ',' num2str(y(i)) ')'], 'FontSize',16,'Color',[0,1,1]);
end
%axis([0 max(x) 0 max(y)]); 
title('���ڻ��Ķ����ɨ��ת�����־�㷨��ʾ');

yinf = min(y); ysup = max(y);
xinf = min(x); xsup = max(x);
PixelFlag = zeros(xsup-xinf+10,ysup-yinf+10);
% �Զ����ÿһ���ߴ��ϱ߱�־
nVertex = length(x)-1; % ����ζ�����
for i = 1:nVertex
    dy = y(1,i+1) - y(1,i);
    if dy == 0 continue; end  % ����ˮƽ��
    dx = (x(1,i+1) - x(1,i))/dy;
    if dy > 0 xScan = x(1,i); else xScan = x(1,i+1); end 
    ymin = min(y(1,i),y(1,i+1));
    ymax = max(y(1,i),y(1,i+1));
    for yScan = ymin+1:ymax
        xScan = round(xScan + dx);
        plot(xScan,yScan,'ro','MarkerSize',8,'MarkerFaceColor','y');
        if PixelFlag(xScan-xinf+1,yScan-yinf+1) == 0
            PixelFlag(xScan-xinf+1,yScan-yinf+1) = 1;
        elseif PixelFlag(xScan-xinf+1,yScan-yinf+1) == 1 % �������±߽��
            PixelFlag(xScan-xinf+1,yScan-yinf+1) = 0;
            plot(xScan,yScan,'wo','MarkerSize',8,'MarkerFaceColor','w'); % �߳��ϲ�����Եı߱�־
        end
    end
end
'�����������...'
pause;

n_time = 2;
for i = yinf:ysup % ɨ����ѭ��(��ѭ��)
    inside = false;
    for j = xinf:xsup % ɨ�����������ཻ��������ѭ��(��ѭ��)
        if PixelFlag(j-xinf+1,i-yinf+1) == 1
            InsideT = inside;
            inside = ~inside;
            if InsideT == true & inside == false  % ����ұ߽��
                plot(j,i,'ro','MarkerSize',8,'MarkerFaceColor','g');
                pause(n_time); % ��ʱn_time��
            end
        end
        
        if inside == true
           plot(j,i,'ro','MarkerSize',8,'MarkerFaceColor','g');
           pause(n_time); % ��ʱn_time��
        end
    end
end
