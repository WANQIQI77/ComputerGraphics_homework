function GeometricTransformation
% ����һ��3Dά�ռ���������һ�������ת�任��Matlab����
%
% Copyright (c) Mar, 2006.  Zhou Dengwen.  All rights reserved.
% Department of Computer Science & Technology
% North China Electric Power University(Beijing)(NCEPU)
%

% �������
AxisLine = [0 5 3; 5 8 10];  % ��ת����
vertex_matrix = [1 1 4; 2 1 4; 2 2 4; 1 2 4; 1 1 5; 2 1 5; 2 2 5; 1 2 5];  % ����ת������
faces_matrix = [1 2 6 5; 2 3 7 6; 3 4 8 7; 4 1 5 8; 1 2 3 4; 5 6 7 8];
DrawingScene(AxisLine,vertex_matrix,faces_matrix); % ���Ƴ���
DisplatPrompt(AxisLine,vertex_matrix,faces_matrix,'��ת���ߺͱ���ת������');  % ��ʾ��ʾ��Ϣ
'�����������...'
pause;

% �����ߵ����P0(x0,y0)ƽ�Ƶ�����ԭ��
AxisLine_h = ones(4,size(AxisLine',2));
AxisLine_h(1:3,:) = AxisLine';  % ���߶˵�ת��Ϊ������� 
vertex_matrix_h = ones(4,size(vertex_matrix',2));
vertex_matrix_h(1:3,:) = vertex_matrix';  % ���嶥��ת��Ϊ�������
T1 = [1 0 0 -AxisLine(1,1); 0 1 0 -AxisLine(1,2); 0 0 1 -AxisLine(1,3); 0 0 0 1];  % ƽ�ƾ���
AxisLine_temp = T1*AxisLine_h;  % �������ƽ�Ƶ�ԭ��
vertex_matrix_temp = T1*vertex_matrix_h;  % ������ƽ�Ʊ任
DrawingScene((AxisLine_temp(1:3,:))',(vertex_matrix_temp(1:3,:))',faces_matrix); % ����ƽ�ƺ�ĳ���
DisplatPrompt((AxisLine_temp(1:3,:))',(vertex_matrix_temp(1:3,:))',faces_matrix,'�����ߵ����ƽ�Ƶ�����ԭ��');  % ��ʾ��ʾ��Ϣ
'�����������...'
pause;

% ��������x����ת��ʹ֮��xozƽ���غ�
x1 = AxisLine_temp(1,2); y1 = AxisLine_temp(2,2); z1 = AxisLine_temp(3,2); % ��Ķ˵�P1
x2 = x1; y2 = 0; z2 = z1; % P1��xozƽ���ͶӰ
x3 = x1; y3 = 0; z3 = 0;  % P1��x���ϵ�ͶӰ
sx = abs(y1)/sqrt(y1^2+z1^2); cx = sqrt(1-sx^2);
RX1 = [1 0 0 0; 0 cx -sx 0; 0 sx cx 0; 0 0 0 1];  % ��ת����
AxisLine_temp = RX1*AxisLine_temp;  % ������x����ת
vertex_matrix_temp = RX1*vertex_matrix_temp;  % ������x����ת
DrawingScene((AxisLine_temp(1:3,:))',(vertex_matrix_temp(1:3,:))',faces_matrix); % ������ת��ĳ���
DisplatPrompt((AxisLine_temp(1:3,:))',(vertex_matrix_temp(1:3,:))',faces_matrix,'��������x����ת��ʹ֮��xozƽ���غ�');  % ��ʾ��ʾ��Ϣ
'�����������...'
pause;

% ��������y����ת��ʹ֮��z���غ�
x1 = AxisLine_temp(1,2); y1 = 0; z1 = AxisLine_temp(3,2); % ��Ķ˵�P1
x2 = 0; y2 = 0; z2 = z1; % P1��z���ϵ�ͶӰ
sy = -abs(x1)/sqrt(x1^2+z1^2); cy = sqrt(1-sy^2);
RY1 = [cy 0 sy 0; 0 1 0 0; -sy 0 cy 0; 0 0 0 1];  % ��ת����
AxisLine_temp = RY1*AxisLine_temp;  % ������y����ת
vertex_matrix_temp = RY1*vertex_matrix_temp;  % ������y����ת
DrawingScene((AxisLine_temp(1:3,:))',(vertex_matrix_temp(1:3,:))',faces_matrix); % ������ת��ĳ���
DisplatPrompt((AxisLine_temp(1:3,:))',(vertex_matrix_temp(1:3,:))',faces_matrix,'��������y����ת��ʹ֮��z���غ�');  % ��ʾ��ʾ��Ϣ
'�����������...'
pause;

% ��������z����ת30��
alfa = pi/6; % ��ת�Ƕ�
RZ = [cos(alfa) -sin(alfa) 0 0; sin(alfa) cos(alfa) 0 0; 0 0 1 0; 0 0 0 1];  % ��ת����
vertex_matrix_temp = RZ*vertex_matrix_temp;  % ������z����ת
DrawingScene((AxisLine_temp(1:3,:))',(vertex_matrix_temp(1:3,:))',faces_matrix); % ������ת��ĳ���
DisplatPrompt((AxisLine_temp(1:3,:))',(vertex_matrix_temp(1:3,:))',faces_matrix,'��������z����ת30��');  % ��ʾ��ʾ��Ϣ
'�����������...'
pause;

% ��������y�ᷴ����ת
RY2 = [cy 0 -sy 0; 0 1 0 0; sy 0 cy 0; 0 0 0 1];  % ��ת����
AxisLine_temp = RY2*AxisLine_temp;  % ������y����ת
vertex_matrix_temp = RY2*vertex_matrix_temp;  % ������y����ת
DrawingScene((AxisLine_temp(1:3,:))',(vertex_matrix_temp(1:3,:))',faces_matrix); % ������ת��ĳ���
DisplatPrompt((AxisLine_temp(1:3,:))',(vertex_matrix_temp(1:3,:))',faces_matrix,'��������y�ᷴ����ת');  % ��ʾ��ʾ��Ϣ
'�����������...'
pause;

% ��������x�ᷴ����ת
RX2 = [1 0 0 0; 0 cx sx 0; 0 -sx cx 0; 0 0 0 1];  % ��ת����
AxisLine_temp = RX2*AxisLine_temp;  % ������x����ת
vertex_matrix_temp = RX2*vertex_matrix_temp;  % ������x����ת
DrawingScene((AxisLine_temp(1:3,:))',(vertex_matrix_temp(1:3,:))',faces_matrix); % ������ת��ĳ���
DisplatPrompt((AxisLine_temp(1:3,:))',(vertex_matrix_temp(1:3,:))',faces_matrix,'��������x�ᷴ����ת');  % ��ʾ��ʾ��Ϣ
'�����������...'
pause;

% �����߷���ƽ��
T2 = [1 0 0 AxisLine(1,1); 0 1 0 AxisLine(1,2); 0 0 1 AxisLine(1,3); 0 0 0 1];  % ƽ�ƾ���
AxisLine_temp = T2*AxisLine_temp;  % ����ƽ��
vertex_matrix_temp = T2*vertex_matrix_temp;  % ����ƽ��
DrawingScene((AxisLine_temp(1:3,:))',(vertex_matrix_temp(1:3,:))',faces_matrix); % ������ת��ĳ���
DisplatPrompt((AxisLine_temp(1:3,:))',(vertex_matrix_temp(1:3,:))',faces_matrix,'�����߷���ƽ��');  % ��ʾ��ʾ��Ϣ
'�����������...'
pause;

%----------------------------------------------------------------------------
function [xmin,xmax,ymin,ymax,zmin,zmax] = SceneRange(AxisLine,vertex_matrix)
% ���������㳡���ķ�Χ

xmin = min(min(AxisLine(:,1)),min(vertex_matrix(:,1)));
ymin = min(min(AxisLine(:,2)),min(vertex_matrix(:,2)));
zmin = min(min(AxisLine(:,3)),min(vertex_matrix(:,3)));  

xmax = max(max(AxisLine(:,1)),max(vertex_matrix(:,1)));
ymax = max(max(AxisLine(:,2)),max(vertex_matrix(:,2)));
zmax = max(max(AxisLine(:,3)),max(vertex_matrix(:,3)));  

if xmin > 0 xmin = 0; end
if ymin > 0 ymin = 0; end
if zmin > 0 zmin = 0; end
if xmax < 0 xmax = 0; end
if ymax < 0 ymax = 0; end
if zmax < 0 zmax = 0; end

%-------------------------------------------------------
function DrawingCoordAxis(xmin,xmax,ymin,ymax,zmin,zmax)
% ���������ƿռ�����������

if xmin > 0 xmin = 0; end
if ymin > 0 ymin = 0; end
if zmin > 0 zmin = 0; end
if xmax < 0 xmax = 0; end
if ymax < 0 ymax = 0; end
if zmax < 0 zmax = 0; end
plot3([xmin,xmax],[0,0],[0,0],'g-');
plot3([0,0],[ymin,ymax],[0,0],'g-');
plot3([0,0],[0,0],[zmin,zmax],'g-');
text(xmax,0,0, 'X','Color',[1 0 0]);
text(0,ymax,0, 'Y','Color',[1 0 0]);
text(0,0,zmax, 'Z','Color',[1 0 0]);
%xlabel('x','Color',[0 1 0]);ylabel('y','Color',[0 1 0]);zlabel('z','Color',[0 1 0]);

%--------------------------------------------------------------
function DrawingScene(AxisLine,vertex_matrix,faces_matrix)
% ���������ƿռ䳡��

% ���㳡����Χ
[xmin,xmax,ymin,ymax,zmin,zmax] = SceneRange(AxisLine,vertex_matrix);

clf; % �����ǰͼ�δ���
hold on; grid on;
axis equal;
view(3);
title('3D���α任��ʾ');  % ��ʾ��������
plot3(AxisLine(:,1),AxisLine(:,2),AxisLine(:,3),'b-','LineWidth',2);  % ������ת��
DisplayCoordinates(AxisLine);  % ��ʾ��˵�����ֵ
patch('Vertices',vertex_matrix,'Faces',faces_matrix,...  % ��������
      'FaceVertexCData',hsv(8),'FaceColor','interp');
DrawingCoordAxis(xmin,xmax,ymin,ymax,zmin,zmax);  % ����������

%------------------------------------------------------------------
function DisplatPrompt(AxisLine,vertex_matrix,faces_matrix,string)
% ��������ʾ��ʾ��Ϣ

[xmin,xmax,ymin,ymax,zmin,zmax] = SceneRange(AxisLine,vertex_matrix);
xText =(xmin+xmax)/2 ; yText = (ymin+ymax)/2; zText = (zmin+zmax)/2; 
text(xText,yText,zText+1, string,'HorizontalAlignment','center','Background',[0.7 0.9 0.7],'Color',[0 0 0], ...
         'FontName','����_GB2312','FontSize', 18,'FontWeight','bold');
     
%--------------------------------------------------------------------
function DisplayCoordinates(AxisLine)
% ��������ʾ�ռ����ζ�������ֵ

nVertex = size(AxisLine,1);
for i = 1:nVertex
    for j = 1:3
        if abs(AxisLine(i,j)) < 10*eps AxisLine(i,j) = 0; end
    end
    text(AxisLine(i,1),AxisLine(i,2),AxisLine(i,3),['(' num2str(AxisLine(i,1)) ',' ...
            num2str(AxisLine(i,2)),',',num2str(AxisLine(i,3)), ')'], ...
            'FontSize',16,'Color',[0,0,1]);
end