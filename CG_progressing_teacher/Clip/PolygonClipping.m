function PolygonClipping
% ����һ������divide and conquer���Ե�Sutherland-Hodgeman����βü��㷨��Matlab����
%
% Copyright (c) Mar, 2006.  Zhou Dengwen.  All rights reserved.
% Department of Computer Science & Technology
% North China Electric Power University(Beijing)(NCEPU)
%

% �������
xp = [0.5, -2, -2, 1.5, 1.5, 0.5, 0.5, -1.5, -1.5, 0.5];  % ���ü��Ķ����
yp = [-1.5, -1.5, 2, 2, 0, 0, 1.5, 1.5, 0.5, -1.5];
%xp = [-2, 0.2, 1.6, -2];
%yp = [0.5, -2, 1.5, 0.5];
nVertex = length(xp)-1; % ����ζ�����
xw = [-1 1 1 -1 -1]; yw = [-1 -1 1 1 -1];  % ���βü�����(���ڱ߽�Ϊ��ʱ�뷽�����½Ƕ����ǵ�һ������)

% ���û�ͼ����
hold on; axis equal;
grid on;

% ����Ҫ�ü��Ķ���κͲü�����
plot(xp,yp,'b-','LineWidth',2);
for i = 1:nVertex
    text(xp(i),yp(i),['P' num2str(i) '(' num2str(xp(i)) ',' num2str(yp(i)) ')'], 'FontSize',16,'Color',[0,1,1]);
end
plot(xw,yw,'g-','LineWidth',1.5);
title('Sutherland-Hodgeman����βü��㷨��ʾ');
'�����������...'
pause;

% Sutherland-Hodgeman����βü��㷨ʵ��
InVertX = xp; InVertY = yp; clr = ['r' 'c' 'm' 'k'];
xText = (xw(1)+xw(2))/2; yText =(yw(2)+yw(3))/2;
prompt = ['�����±߽�ü����'; '�����ұ߽�ü����'; '�����ϱ߽�ü����'; '������߽�ü����'];
for j = 1:4
    [OutVertX,OutVertY] = SutherlandHodgmanClip(InVertX,InVertY,xw(j),yw(j),xw(j+1),yw(j+1));
    InVertX = OutVertX; InVertY = OutVertY;
    plot(OutVertX,OutVertY,[clr(j) '-'],'LineWidth',3); 
    text(xText,yText, prompt(j,:),'HorizontalAlignment','center','Background',[0.7 0.9 0.7],'Color',[0 0 0], ...
         'FontName','����_GB2312','FontSize', 18,'FontWeight','bold');
    '�����������...'
    pause;
end

hold off; 

%--------------------------------------------------------------------------------------
function [OutVertX, OutVertY] = SutherlandHodgmanClip(InVertX, InVertY, x0, y0, x1, y1)
% �������������α��ü����ڵ�һ���߽�ü����

nVertex = length(InVertX); 
xs = InVertX(1); ys = InVertY(1);
OutVertX = []; OutVertY = [];
for j = 2:nVertex
    xp = InVertX(j); yp = InVertY(j);
    VisibleFlagP = inside(xp,yp,x0,y0,x1,y1);
    if VisibleFlagP
       VisibleFlagS = inside(xs,ys,x0,y0,x1,y1);
       if VisibleFlagS  % SP�ڴ����ڣ����(1)
          OutVertX = [OutVertX, xp]; OutVertY = [OutVertY, yp]; 
       else  % S�ڴ�����, ���(4)
          [xi,yi] = intersect(xs,ys,xp,yp,x0,y0,x1,y1);
          OutVertX = [OutVertX, xi]; OutVertY = [OutVertY, yi];
          OutVertX = [OutVertX, xp]; OutVertY = [OutVertY, yp];          
       end
    else
       VisibleFlagS = inside(xs,ys,x0,y0,x1,y1);
       if VisibleFlagS  % S�ڴ����ڣ�P�ڴ�����,���(3)
          [xi,yi] = intersect(xs,ys,xp,yp,x0,y0,x1,y1);
          OutVertX = [OutVertX, xi]; OutVertY = [OutVertY, yi];  
       end
       % ���(2)û�����
    end
    xs = xp; ys = yp;
end
OutVertX = [OutVertX,OutVertX(1)]; OutVertY = [OutVertY, OutVertY(1)];

%---------------------------------------------------------
function VisibleFlag = inside(x, y, x0, y0, x1, y1)
% �������жϵ��Ƿ��ڲü��ߵĿɼ���

VisibleFlag = false;
if x1 > x0  % �ü���Ϊ�����±�
   if y >= y0 VisibleFlag = true; end
elseif x1 < x0  % �ü���Ϊ�����ϱ�
	if y <= y0 VisibleFlag = true; end
elseif y1 > y0  % �ü���Ϊ�����ұ�
	if x <= x0 VisibleFlag = true; end
elseif y1 < y0  % �ü���Ϊ�������
	if  x >= x0 VisibleFlag = true; end
end

%-------------------------------------------------------------------------
function [xi, yi] = intersect(xs, ys, xp, yp, x0, y0, x1, y1)
% ����������ֱ�߶�SP�봰�ڱ߽�Ľ��㣻

if y0 == y1  % ˮƽ�ü���
    yi = y0;
    xi = xs + (y0-ys)*(xp-xs)/(yp-ys);
else  % ��ֱ�ü���
    xi = x0;
    yi = ys + (x0-xs)*(yp-ys)/(xp-xs);
end


