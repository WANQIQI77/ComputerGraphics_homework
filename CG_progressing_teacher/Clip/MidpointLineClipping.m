function MidpointLineClipping
% �е�ָ��߶βü��㷨��Matlab����
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
title('�е�ָ��߶βü��㷨��ʾ');

% �е�ָ��߶βü��㷨ʵ��
[xv0,yv0,flag] = ProximateVisiblePoint(x0,y0,x1,y1,XL,XR,YB,YT);  % ������P0(x0,y0)����Ŀɼ���PV0(xv0,yv0);
if flag ~= false
   [xv1,yv1,flag] = ProximateVisiblePoint(x1,y1,x0,y0,XL,XR,YB,YT);  % ������P1(x1,y1)����Ŀɼ���PV1(xv1,yv1);
end

% ���Ʋü�����߶�
if flag ~= false % �߶β���ȫ�ڴ����⣨���߶β��ֻ�ȫ���ɼ���
   plot([xv0 xv1],[yv0 yv1],'k-','LineWidth',3); 
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

%-------------------------------------------------------------------
function [xv,yv,VisibleFlag] = ProximateVisiblePoint(x0,y0,x1,y1,XL,XR,YB,YT)
% P0(x0,y0)��P1(x1,y1)�Ǵ��ü��߶ε������˵㣬
% XL,XR,YB,YT�ǲü����ڵı߽�
% �����������P0����Ŀɼ���

code0 = PointEncoding(x0,y0,XL,XR,YB,YT);  % ����P0�ı���
VisibleFlag = true; error = 0.01;
if code0 == 0
   xv = x0; yv = y0;
else
   code1 = PointEncoding(x1,y1,XL,XR,YB,YT);  % ����P1�ı���
   if bitand(code0,code1) == 0 % P0P1������Ȼ���ɼ�
              
      % �����е�ָ�����P0(x0,y0)����Ŀɼ���
      while 1  
        xm = (x0+x1)/2; ym = (y0+y1)/2;  % ����P0P1���е�Pm
        if abs(xm-x1) > error | abs(ym-y1) > error
            codem = PointEncoding(xm,ym,XL,XR,YB,YT);  % ����Pm�ı���
            if bitand(code0,codem) ~= 0
                x0 = xm; y0 = ym;
                code0 = codem;  % ����P0�ı���
                if bitand(code0,code1) ~= 0  % ���¼����P0P1������Ƿ���Ȼ���ɼ�
                   VisibleFlag = false; 
                   xv = 0; yv = 0;  % ��(xv,yv)��㸳ֵ����Ϊ��ʱ(xv,yv)û������
                   break;
                end
            else
                x1 = xm; y1 = ym;
                code1 = codem;  % ����P1�ı���
            end
        else
            xv = xm; yv = ym;
            break;
        end
      end  % ��whileƥ��
      
   else
      VisibleFlag = false;
   end
end

