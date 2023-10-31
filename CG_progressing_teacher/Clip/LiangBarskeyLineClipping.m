function LiangBarskeyLineClipping
% ���Ѷ�-Barskey�߶βü��㷨��Matlab����
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
title('���Ѷ�-Barskey�߶βü��㷨��ʾ');

% ���Ѷ�-Barskey�߶βü��㷨ʵ��
u0 = 0; u1 = 1; dx = x1 - x0; dy = y1 - y0;
[u0, u1, flag] = ParameterU(-dx, x0-XL, u0, u1);  % ������ü��߶���ü�������߽罻�㣬��ѡȡ����u
if flag
   [u0, u1, flag] = ParameterU(dx, XR-x0, u0, u1);% ������ü��߶���ü������ұ߽罻�㣬��ѡȡ����u
   if flag
      [u0, u1, flag] = ParameterU(-dy, y0-YB, u0, u1);  % ������ü��߶���ü������±߽罻�㣬��ѡȡ����u
      if flag
         [u0, u1, flag] = ParameterU(dy, YT-y0, u0, u1);  % ������ü��߶���ü������ϱ߽罻�㣬��ѡȡ����u
         if flag
            plot([x0+u0*dx x0+u1*dx],[y0+u0*dy y0+u1*dy],'k-','LineWidth',3);  % ���Ʋü�����߶�(����u0~u1֮����߶�)
         end
      end
   end
end

% �ָ���ͼ����
hold off; 

%------------------------------------------------------------------
function [u0, u1, VisibleFlag] = ParameterU(p, q, u0, u1)
% ������������ü��߶���ü����ڱ߽罻��Ĳ���u������ȷ��ѡȡ����u

VisibleFlag = true;
if p < 0  % �߶δӴ����ⲿָ�򴰿��ڲ�,uȡ�ϴ��
   r = q/p;
   if r > u1  VisibleFlag = false; % �������uֵ�������ڴ��ڱ߽���ӳ�����
   elseif r > u0
       u0 = r;
   end
else
    if p > 0  % �߶δӴ����ڲ�ָ�򴰿��ⲿ��uȡ��С��
       r = q/p;
	   if r < u0  VisibleFlag = false;  % �������uֵ�������ڴ��ڱ߽���ӳ�����
	   elseif r < u1 
           u1 = r;
       end       
    else
       if q < 0 VisibleFlag = false; end  % pΪ0,q<0 ʱ���߶��ڴ�����
    end
end