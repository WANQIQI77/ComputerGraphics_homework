function CohenSoutherlandLineClipping
% Cohen-Southerland�߶βü��㷨��Matlab����
%
% Copyright (c) 2005.  Zhou Dengwen.  All rights reserved.
% Department of Computer Science & Technology
% North China Electric Power University(Beijing)(NCEPU)
%

x0 = 3; y0 = 1; x1 = 13; y1 = 8; % ���ü����߶�
%x0 = 10.5; y0 = 0.5; x1 = 12; y1 = 2.5;
%x0 = 6; y0 = 4; x1 = 10; y1 = 6;
x = [4 11 11 4 4]; y = [2 2 7 7 2]; % �ü�����
XL = min(x); XR = max(x); YB = min(y); YT = max(y); 

% ���û�ͼ����
hold on; axis equal;
grid on;

% ����Ҫ�ü����߶κͲü�����
plot([x0 x1],[y0 y1],'r-','LineWidth',2);
text(x0,y0,'\leftarrow P0','FontSize',16,'Color',[0,0,1]);
text(x1,y1,'\leftarrow P1','FontSize',16,'Color',[0,0,1]);
plot(x,y,'g-','LineWidth',1.5);
title('Cohen-Southerland�߶βü��㷨��ʾ');

% Cohen-Southerland�߶βü��㷨ʵ��
code0 = PointEncoding(x0,y0,XL,XR,YB,YT); % ����˵�P0(x0,y0)�ı���
code1 = PointEncoding(x1,y1,XL,XR,YB,YT);  % ����˵�P1(x1,y1)�ı���
LEFT = 1; 
RIGHT = 2; 
BOTTOM = 4; 
TOP = 8;
flag = 1;
while code0 ~= 0 | code1 ~= 0  % �߶�P0P1������һ���˵��ڴ�����
    
    % �����߶��봰�ڱ߽�Ľ���
    if bitand(code0,code1) ~= 0 % �߶���ȫ�ڴ����⣨����ȫ���ɼ���
       flag = 0; break;
    end
    if code0 ~= 0 code = code0; % ȷ���߶��ڴ�����ĵ�
    else code = code1;
    end
    if bitand(LEFT,code) ~= 0  % �߶��봰����߽��ཻ
       x = XL;
       y = y0+(y1-y0)*(XL-x0)/(x1-x0);
    elseif bitand(RIGHT,code) ~= 0  % �߶��봰���ұ߽��ཻ
       x = XR;
       y = y0+(y1-y0)*(XR-x0)/(x1-x0);
    elseif bitand(BOTTOM,code) ~= 0  % �߶��봰���±߽��ཻ
       y = YB;
       x = x0+(x1-x0)*(YB-y0)/(y1-y0);
    elseif bitand(TOP,code) ~= 0  % �߶��봰���ϱ߽��ཻ
       y = YT;
       x = x0+(x1-x0)*(YT-y0)/(y1-y0);
   end
  
   % �޸��߶εĶ˵�P0(x0,y0)��P1(x1,y1)
   if code == code0  
      x0 = x; y0 = y; code0 = PointEncoding(x,y,XL,XR,YB,YT);
   else
      x1 = x; y1 = y; code1 = PointEncoding(x,y,XL,XR,YB,YT);
   end
   
end

% ���Ʋü�����߶�
if flag ~= 0 % �߶β���ȫ�ڴ����⣨���߶β��ֻ�ȫ���ɼ���
   plot([x0 x1],[y0 y1],'k-','LineWidth',3); 
end

% �ָ���ͼ����
hold off; 

%---------------------------------------------
function code = PointEncoding(x,y,XL,XR,YB,YT)
% ���㴰��ƽ���ı���

% initialize
LEFT = 1; 
RIGHT = 2; 
BOTTOM = 4; 
TOP = 8;
code = 0;

% �����(x,y)�ı���
if x < XL code = bitor(code,LEFT); end
if x > XR code = bitor(code,RIGHT); end
if y < YB code = bitor(code,BOTTOM); end
if y > YT code = bitor(code,TOP); end

