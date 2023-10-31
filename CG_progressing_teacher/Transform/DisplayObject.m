function DisplayObject
% ����һ����ʾ3Dά�ռ������Matlab����
%
% Copyright (c) Mar, 2006.  Zhou Dengwen.  All rights reserved.
% Department of Computer Science & Technology
% North China Electric Power University(Beijing)(NCEPU)
%

% ���볡������
house_vertex_matrix = [0 0 54; 16 0 54; 16 10 54; 8 16 54; 0 10 54; 0 0 30; 16 0 30; 16 10 30; 8 16 30; 0 10 30];  % �۲�任������(����)
house_faces_matrix = [1 2 3 4 5; 2 3 8 7 7; 6 7 8 9 10; 5 1 6 10 10; 1 2 7 6 6; 3 4 9 8 8; 5 4 9 10 10];

% �Գ������۲�任
VPN = [1 1 1]; VUP = [0 1 0]; VRP = [16 0 54]; % ָ���۲�ƽ��͹۲췽��
WC_VRC_Matrix = ViewTransformationMatrix(VPN,VUP,VRP); % ����۲�任����
nVertex = size(house_vertex_matrix,1);
temp = ones(nVertex,4); temp(:,1:3) = house_vertex_matrix; % ���嶥��ת��Ϊ�������
temp = WC_VRC_Matrix*temp'; % �۲�任
for j = 1:nVertex
    temp(1:3,j) = temp(1:3,j)/temp(4,j);
end
house_vertex_matrix = temp(1:3,:)'; % �۲�任������嶥��
DrawingScene(house_vertex_matrix,house_faces_matrix); % ���ƹ۲�任��ĳ���
DisplayCoordinates(house_vertex_matrix);  % ��ʾ��������

%----------------------------------------------------------------------------
function [xmin,xmax,ymin,ymax,zmin,zmax] = SceneRange(vertex_matrix)
% ���������㳡���ķ�Χ

xmin = min(vertex_matrix(:,1)); xmax = max(vertex_matrix(:,1));
ymin = min(vertex_matrix(:,2)); ymax = max(vertex_matrix(:,2));
zmin = min(vertex_matrix(:,3)); zmax = max(vertex_matrix(:,3)); 
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
function DrawingScene(vertex_matrix,faces_matrix)
% ���������ƿռ䳡��

% ���㳡����Χ
[xmin,xmax,ymin,ymax,zmin,zmax] = SceneRange(vertex_matrix);

clf; % �����ǰͼ�δ���
hold on; grid on;
axis equal;
%thetax = pi/3;thetay = pi/6;
%Rx = [1 0 0 0;0 cos(thetax) -sin(thetax) 0;0 sin(thetax) cos(thetax) 0;0 0 0 1];
%Ry = [cos(thetay) 0 sin(thetay) 0;0 1 0 0;-sin(thetay) 0 cos(thetay) 0;0 0 0 1];
T = view(2);
title('3D������ʾ');
h = patch('Vertices',vertex_matrix,'Faces',faces_matrix,...
          'EdgeColor',[0 0 1],'FaceColor',[1 0.4 0.6]);   
set(h,'FaceLighting','gouraud','AmbientStrength',0.7);
light('Position',[1 0 0],'Style','infinite');
DrawingCoordAxis(xmin,xmax,ymin,ymax,zmin,zmax);  % ����������

%--------------------------------------------------------------------
function DisplayCoordinates(vertex_matrix)
% ��������ʾ�ռ����ζ�������ֵ

nVertex = size(vertex_matrix,1);
for i = 1:nVertex
    for j = 1:3
        if abs(vertex_matrix(i,j)) < 10*eps AxisLine(i,j) = 0; end
    end
    text(vertex_matrix(i,1),vertex_matrix(i,2),vertex_matrix(i,3),['(' num2str(vertex_matrix(i,1)) ',' ...
            num2str(vertex_matrix(i,2)),',',num2str(vertex_matrix(i,3)), ')'], ...
            'FontSize',16,'Color',[1,1,0]);
end

%------------------------------------------------------------------------
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