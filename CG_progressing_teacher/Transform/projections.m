function projections
% ����һ��3Dά�ռ�۲�任��Matlab����
%
% Copyright (c) Mar, 2006.  Zhou Dengwen.  All rights reserved.
% Department of Computer Science & Technology
% North China Electric Power University(Beijing)(NCEPU)
%

% �������
house_vertex_matrix = [0 0 54; 16 0 54; 16 10 54; 8 16 54; 0 10 54; 0 0 30; 16 0 30; 16 10 30; 8 16 30; 0 10 30];  % �۲�任������(����)
house_faces_matrix = [1 2 3 4 5; 2 3 8 7 7; 6 7 8 9 10; 5 1 6 10 10; 1 2 7 6 6; 3 4 9 8 8; 5 4 9 10 10];
%house_vertex_matrix = [0 0 0; 1 0 0; 1 1 0; 0 1 0; 0 0 1; 1 0 1; 1 1 1; 0 1 1];
%house_faces_matrix = [1 2 6 5; 2 3 7 6; 3 4 8 7; 4 1 5 8; 1 2 3 4; 5 6 7 8];
DrawingScene(house_vertex_matrix,house_faces_matrix); % ���Ƴ���
disp('�����������...')
pause;

% �Գ������۲�任(������x,y���������൱������ƽ��ͶӰ)
VRP = [16 0 54]; VPN = [1 0 1]; VUP = [0.1 1 0];  % ָ���۲�ƽ��͹۲췽��
PRP = [0 25 20*sqrt(2)];  % ͶӰ�ο���(ͶӰ���Ļ��ӵ�)

WC_VRC_Matrix = ViewTransformationMatrix(VPN,VUP,VRP); % ����۲�任����
nVertex = size(house_vertex_matrix,1);
temp = ones(nVertex,4); temp(:,1:3) = house_vertex_matrix; % ���嶥��ת��Ϊ�������
temp = WC_VRC_Matrix*temp'; % �۲�任
for j = 1:nVertex
    temp(1:3,j) = temp(1:3,j)/temp(4,j);
end
house_vertex_matrix = temp(1:3,:)'; % �۲�任������嶥��
DrawingScene(house_vertex_matrix,house_faces_matrix); % ���ƹ۲�任��ĳ���
disp('�����������...')
pause;

% �Գ�����͸��ͶӰ�任
Tfwd = [1 0 0 -PRP(1); 0 1 0 -PRP(2); 0 0 1 0; 0 0 0 1];  % ��ͶӰ�ο���ƽ�Ƶ�n���ϵ�ƽ�Ʊ任����
Tinv = [1 0 0 PRP(1); 0 1 0 PRP(2); 0 0 1 0; 0 0 0 1];  % ����ƽ�Ʊ任����
M_per = [1 0 0 0; 0 1 0 0; 0 0 0 0; 0 0 -1/PRP(3) 1];  % ͸�ӱ任����
temp = Tinv*M_per*Tfwd*temp;  % ͸�ӱ任
for j = 1:nVertex
    temp(1:3,j) = temp(1:3,j)/temp(4,j);
end
house_vertex_matrix = temp(1:3,:)'; % ͸�ӱ任������嶥��
DrawingScene(house_vertex_matrix,house_faces_matrix); % ���ƹ۲�任��ĳ���

%----------------------------------------------------------------------------
function [xmin,xmax,ymin,ymax,zmin,zmax] = SceneRange(vertex_matrix)
% ���������㳡���ķ�Χ

xmin = min(vertex_matrix(:,1)); xmax = max(vertex_matrix(:,1));
ymin = min(vertex_matrix(:,2)); ymax = max(vertex_matrix(:,2));
zmin = min(vertex_matrix(:,3)); zmax = max(vertex_matrix(:,3)); 
if  xmin > 0 
    xmin = 0; 
end
if  ymin > 0 
    ymin = 0; 
end
if  zmin > 0 
    zmin = 0; 
end
if  xmax < 0 
    xmax = 0; 
end
if  ymax < 0 
    ymax = 0; 
end
if  zmax < 0 
    zmax = 0; 
end

%-------------------------------------------------------
function DrawingCoordAxis(xmin,xmax,ymin,ymax,zmin,zmax)
% ���������ƿռ�����������

if  xmin > 0 
    xmin = 0; 
end
if  ymin > 0 
    ymin = 0; 
end
if  zmin > 0 
    zmin = 0; 
end
if  xmax < 0 
    xmax = 0; 
end
if  ymax < 0 
    ymax = 0; 
end
if  zmax < 0 
    zmax = 0; 
end
plot3([xmin,xmax],[0,0],[0,0],'g-');
plot3([0,0],[ymin,ymax],[0,0],'g-');
plot3([0,0],[0,0],[zmin,zmax],'g-');
text(xmax,0,0, 'X','Color',[1 0 0]);
text(0,ymax,0, 'Y','Color',[1 0 0]);
text(0,0,zmax, 'Z','Color',[1 0 0]);
%xlabel('x','Color',[0 1 0]);ylabel('y','Color',[0 1 0]);zlabel('z','Color',[0 1 0]);
%--------------------------------------------------------------
function DrawingScene(vertex_matrix,faces_matrix)
% ���������ƿռ䳡��

% ���㳡����Χ
[xmin,xmax,ymin,ymax,zmin,zmax] = SceneRange(vertex_matrix);

clf; % �����ǰͼ�δ���
hold on; grid on;
axis equal;
view(2);
title('3D�۲�任��ʾ');  % ��ʾ��������
patch('Vertices',vertex_matrix,'Faces',faces_matrix, ...   
        'FaceColor','none','EdgeColor',[0 0 1,],'LineWidth',2);
DrawingCoordAxis(xmin,xmax,ymin,ymax,zmin,zmax);  % ����������

%-------------------------------------------------------------
function WC_VRC_Matrix = ViewTransformationMatrix(VPN,VUP,VRP)
% �������������������ϵWC���۲�����ϵVRC�ı任����
% VPN -- �۲�ƽ��ķ���
% VUP -- �۲�����
% VRP -- �۲�ο���
% for example,VPN = [0 0 1], VUP = [0 1 0],VRP = [0 0 0]

n = VPN/norm(VPN);
u = cross(VUP,VPN)/norm(cross(VUP,VPN));
v = cross(n,u);
temp1 = eye(4); temp2 = eye(4);
temp1(1:3,1:3) = [u;v;n]; temp2(1:3,4) = -VRP';
WC_VRC_Matrix = temp1*temp2;
