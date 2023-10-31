function GeometricInvariance
% ����һ����ʾ���߱�ʾ���β������ʵ�Matlab����
% ���߱�ʾ���β�������ָ���ǲ�����������ϵ��ѡ�����˵����ת��ƽ�Ʊ任�²��������
%
% Copyright (c) Apr, 2006.  Zhou Dengwen.  All rights reserved.
% Department of Computer Science & Technology
% North China Electric Power University(Beijing)(NCEPU)
%

set(gcf, 'NumberTitle','off', 'Name','���߱�ʾ���β�������ʾ');
x = [0 1 2]; y = [0 0.5 0];  % ����������ֵ��
[a,b,c] = CalcCoef(x,y);  % ������ʽ��ʾ�Ķ��ζ���ʽ����ϵ�� a,b,c
subplot(2,2,1); % ��������
DrawingCurve(x,y,a,b,c);  
title('ͨ��������ֵ�����ʽ��ʾ�Ķ��ζ���ʽ����');

[xr,yr] = rotate2(x,y,45);  % ������ֵ����ʱ����ת45��
[ar,br,cr] = CalcCoef(xr,yr);  % ������ת���ϵ�� ar,br,cr
subplot(2,2,2);  % ������ת�������
DrawingCurve(xr,yr,ar,br,cr); 
title('��ת������ֵ������ʽ��ʾ�Ķ��ζ���ʽ����');

subplot(2,2,3);
QuadraticBezier(x,y); % ����ͻ��ƶ���Bezier��������
title('ͨ��������ֵ��Ĳ�����ʾ�Ķ�������');

subplot(2,2,4);
QuadraticBezier(xr,yr); % ����ͻ�����ת��Ķ���Bezier��������
title('��ת������ֵ���Ĳ�����ʾ�Ķ�������');

%-----------------------------------------------------------
function [a,b,c] = CalcCoef(x,y)
% ����������ö��ζ���ʽ������ϵ�� a,b,c
% ��ʽ��ʾ�Ķ��ζ���ʽ���� y = a*x^2 + b*x + c
% x,y ��Ϊ����Ԫ�ص����������ֱ��ʾ������������ĺᡢ������

t1 = [y(2)-y(1) x(2)^2-x(1)^2 x(2)-x(1)];
t2 = [y(3)-y(2) x(3)^2-x(2)^2 x(3)-x(2)];

b = (t1(1)*t2(2)-t2(1)*t1(2))/(t1(3)*t2(2)-t2(3)*t1(2));
a = (t1(1)*t2(3)-t2(1)*t1(3))/(t1(2)*t2(3)-t2(2)*t1(3));
c = y(1)-a*x(1)^2-b*x(1);

%----------------------------------------------------------
function [xr,yr] = rotate2(x,y,theta)
% ������������x,y��ʱ����תtheta��
% theta�ǽǶ�

n = size(x,2); theta = theta*pi/180;
for j = 1:n
    xr(j) = x(j)*cos(theta)-y(j)*sin(theta);
    yr(j) = x(j)*sin(theta)+y(j)*cos(theta);
end

%----------------------------------------------
function DrawingCurve(x,y,a,b,c)
% ���������ƶ���ʽ��������

hold on; axis equal;
t0 = min(x); t1 = max(x);
t = t0:0.01:t1;
f = a*t.^2 + b*t + c;
plot(t,f);  % ��������
for j = 1:3
    plot(x(j),y(j),'ro','MarkerSize',8,'MarkerFaceColor','g');
end

%----------------------------------------------
function QuadraticBezier(x,y)
% ����������ͻ��ƶ���Bezier��������
% x,y�Ǹ�����������ֵ��

L1 = sqrt((x(2)-x(1))^2+(y(2)-y(1))^2); % �����ҳ�
L2 = sqrt((x(3)-x(2))^2+(y(3)-y(2))^2);
u = [0 L1/(L1+L2) 1]; % �������ֵ��Ĳ���

x1 = x(1); y1 = y(1); x3 = x(3); y3 = y(3); % ������������ζ���
x2 = (x(2)-(1-u(2))^2*x1-u(2)^2*x3)/(2*u(2)*(1-u(2)));
y2 = (y(2)-(1-u(2))^2*y1-u(2)^2*y3)/(2*u(2)*(1-u(2)));

% �������Bezier���������ϵĲ�ֵ��
hold on; axis equal;
t = 0:0.01:1;
xt = (1-t.^2)*x1+2*t.*(1-t)*x2+t.^2*x3;
yt = (1-t.^2)*y1+2*t.*(1-t)*y2+t.^2*y3;

% ���ƶ���Bezier��������
plot(xt,yt);
for j = 1:3
    plot(x(j),y(j),'ro','MarkerSize',8,'MarkerFaceColor','g');
end
    



