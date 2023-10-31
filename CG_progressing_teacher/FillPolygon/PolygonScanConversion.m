function PixelFlag = PolygonScanConversion(x,y)
% ����һ�����ڻ������ɨ��ת�������㷨��Matlab����
% Ҫɨ��ת���Ķ���Ρ������ʾ���ֱ���Ϊ1024*768,��
% x����ķ�ΧΪ0~1023������, y����ķ�ΧΪ0~767������
%
% Copyright (c) Mar, 2006.  Zhou Dengwen.  All rights reserved.
% Department of Computer Science & Technology
% North China Electric Power University(Beijing)(NCEPU)
%
% Last time modified: 29/8/2006
%

nVertex = length(x)-1; % ����ζ�����
xmin = min(x); xmax = max(x);
ymin = min(y); ymax = max(y);
PixelFlag = zeros(xmax-xmin+5,ymax-ymin+5);

% ���û�ͼ����
hold on; axis equal;
grid on;

% ����Ҫɨ��ת���Ķ����
plot(x,y,'b-','LineWidth',2);
for i = 1:nVertex
    text(x(i),y(i),['P' num2str(i) '(' num2str(x(i)) ',' num2str(y(i)) ')'], 'FontSize',16,'Color',[0,1,1]);
end
axis([0 max(x) 0 max(y)]); title('���ڻ��Ķ����ɨ��ת��ɨ���㷨��ʾ');

% �����±߱�
yinf = min(y); ysup = max(y); NET = cell(ysup-yinf+1,1);
nScan = 1;
for i = yinf:ysup % ɨ����ѭ��(��ѭ��)
    nKnot = 0; knot = {};
    for j = 1:nVertex % ����α�ѭ��(��ѭ��)
        y0 = min(y(j),y(j+1));
        if y0 == i
           [a,b,c] = SolveLineEquation(x(j),y(j),x(j+1),y(j+1));
           if a == 0 continue; % ����ˮƽ��
           else
               nKnot = nKnot+1; DeltaX = -b/a;
               if y(j) == y0 x0 = x(j); else x0 = x(j+1); end
               knot(1,nKnot) = {[x0,DeltaX,max(y(j),y(j+1))]};
           end
        end
    end % ����ѭ��forƥ��
    if nKnot ~= 0
        NET{nScan} = knot;
        NET{nScan} = SortKnot(NET{nScan}); % ���±߱��н�㣬����x���������˳������
    end
    nScan = nScan + 1;
end

% ���Ա߱�ά��
AET = {}; nScan = 1;
for i = yinf:ysup
    nNET = size(NET{nScan},2);
    
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    if nNET ~= 0  % ���±߱����µĻ��Ա�,�����x�����˳����뵽���Ա߱���
       nAET = size(AET,2);
       if nAET == 0   % ���±߱�AETΪ��ʱ
          AET = NET{nScan}; nAET = nNET;
       else   % ���±߱�AET��Ϊ��ʱ
           for j = 1:nNET
               p = NET{nScan}(1,j);
               for k = 1:nAET
                   
                   if AET{1,k}(1,1) < p{1}(1) continue; end  % �������±߱�AET����һ�����
                   
                   % �ҵ�������λ�ú�, �����±߱��еĽ��
                   if AET{1,k}(1,1) == p{1}(1)  % ����ɨ���������ζ����ཻʱ������
                      if AET{1,k}(1,3) > i &  p{1}(3) > i  % ��2������
                         AET = InsertKnot(AET,p,k);
                         nAET = nAET + 1;
                     elseif AET{1,k}(1,3) == p{1}(3) & p{1}(3) == i  % ��0������
                         AET = DeleteKnot(AET,k);
                         nAET = nAET - 1;
                      else  % ��1������
                         AET{1,k} = p{1};
                      end
                   else
                       AET = InsertKnot(AET,p,k);
                       nAET = nAET + 1;
                   end                   
                   break;
                   
               end  % �� for k = 1:nAET ƥ��
               if p{1}(1) > AET{1,nAET}(1,1)
                  AET = {AET{1:nAET},p{1}}; nAET = nAET + 1;
               end
           end  % �� for j = 1:nNET ƥ��
       end
    end
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
    for j = 1:2:nAET  % �������
        p1 = AET{1,j}; p2 = AET{1,j+1};
        for k = round(p1(1,1)):round(p2(1,1))  % ��ԵĽ������ɫ
            plot(k,i,'ro','MarkerSize',8,'MarkerFaceColor','g');
            PixelFlag(k-xmin+3,i-ymin+3) = 1;
        end            
    end
     
    j = 1;
    while j <= nAET  % �޸Ļ��Ա߱�
        if i+1 < ysup % i+1��Ϊ��ȥ��ˮƽ��
           NextScanLine = i+1;
       else
           NextScanLine = i;
       end
        if AET{1,j}(1,3) == NextScanLine  % ɾ���´�ɨ���ߵķǻ��Ա�
           AET = DeleteKnot(AET,j);
           nAET = nAET - 1; j = j - 1;
        else
           AET{1,j}(1,1) = AET{1,j}(1,1) + AET{1,j}(1,2); % ������´�ɨ��������ԱߵĽ���(������)
        end
        j = j + 1;
    end
    if nAET > 0 
        AET = SortKnot(AET); % �����Ա߱��н�㣬����x���������˳������
    end
    nScan = nScan + 1;
end

%--------------------------------------------------------------------
function [a,b,c] = SolveLineEquation(x1,y1,x2,y2)
% �����߶ε������˵㣬���㷽�� ax+by+c=0 ��ϵ��

a = y1 - y2; b = x2 - x1; c = x1*y2 - x2*y1;

%-------------------------------------------------------------------
function AET = InsertKnot(AET,p,k)
% ���Ա߱��в���һ�����

nAET = size(AET,2);
if k == 1 
   AET = {p{1},AET{1:nAET}}; 
else
   AET = {AET{1,1:k-1},p{1},AET{1,k:nAET}};
end

%-------------------------------------------------------------------
function AET = DeleteKnot(AET,k)
% ���Ա߱���ɾ��һ�����
nAET = size(AET,2);
if k == 1 
   AET = {AET{1,2:nAET}}; 
else if k == nAET
        AET = {AET{1,1:nAET-1}}; 
    else
        AET = {AET{1,1:k-1},AET{1,k+1:nAET}};
    end
end

%-------------------------------------------------------------------
function AET = SortKnot(AET)
% �����Ա߱��н�㣬����x���������˳������

nAET = size(AET,2);
for i = 1:nAET
    t(1,i) = AET{1,i}(1,1);
end
[t,index] = sort(t);
AET = {AET{1,index}};

