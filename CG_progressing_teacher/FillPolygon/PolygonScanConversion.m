function PixelFlag = PolygonScanConversion(x,y)
% 这是一个无内环多边形扫描转换描线算法的Matlab程序
% 要扫描转换的多边形。如果显示器分辨率为1024*768,则
% x坐标的范围为0~1023的整数, y坐标的范围为0~767的整数
%
% Copyright (c) Mar, 2006.  Zhou Dengwen.  All rights reserved.
% Department of Computer Science & Technology
% North China Electric Power University(Beijing)(NCEPU)
%
% Last time modified: 29/8/2006
%

nVertex = length(x)-1; % 多边形顶点数
xmin = min(x); xmax = max(x);
ymin = min(y); ymax = max(y);
PixelFlag = zeros(xmax-xmin+5,ymax-ymin+5);

% 设置绘图环境
hold on; axis equal;
grid on;

% 画出要扫描转换的多边形
plot(x,y,'b-','LineWidth',2);
for i = 1:nVertex
    text(x(i),y(i),['P' num2str(i) '(' num2str(x(i)) ',' num2str(y(i)) ')'], 'FontSize',16,'Color',[0,1,1]);
end
axis([0 max(x) 0 max(y)]); title('无内环的多边形扫描转换扫线算法演示');

% 创建新边表
yinf = min(y); ysup = max(y); NET = cell(ysup-yinf+1,1);
nScan = 1;
for i = yinf:ysup % 扫描线循环(外循环)
    nKnot = 0; knot = {};
    for j = 1:nVertex % 多边形边循环(内循环)
        y0 = min(y(j),y(j+1));
        if y0 == i
           [a,b,c] = SolveLineEquation(x(j),y(j),x(j+1),y(j+1));
           if a == 0 continue; % 忽略水平边
           else
               nKnot = nKnot+1; DeltaX = -b/a;
               if y(j) == y0 x0 = x(j); else x0 = x(j+1); end
               knot(1,nKnot) = {[x0,DeltaX,max(y(j),y(j+1))]};
           end
        end
    end % 与内循环for匹配
    if nKnot ~= 0
        NET{nScan} = knot;
        NET{nScan} = SortKnot(NET{nScan}); % 将新边表中结点，按照x坐标递增的顺序排序
    end
    nScan = nScan + 1;
end

% 活性边表维护
AET = {}; nScan = 1;
for i = yinf:ysup
    nNET = size(NET{nScan},2);
    
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    if nNET ~= 0  % 将新边表中新的活性边,按结点x坐标的顺序插入到活性边表中
       nAET = size(AET,2);
       if nAET == 0   % 当新边表AET为空时
          AET = NET{nScan}; nAET = nNET;
       else   % 当新边表AET不为空时
           for j = 1:nNET
               p = NET{nScan}(1,j);
               for k = 1:nAET
                   
                   if AET{1,k}(1,1) < p{1}(1) continue; end  % 继续找新边表AET的下一个结点
                   
                   % 找到结点插入位置后, 插入新边表中的结点
                   if AET{1,k}(1,1) == p{1}(1)  % 处理扫描线与多边形顶点相交时的情形
                      if AET{1,k}(1,3) > i &  p{1}(3) > i  % 计2个交点
                         AET = InsertKnot(AET,p,k);
                         nAET = nAET + 1;
                     elseif AET{1,k}(1,3) == p{1}(3) & p{1}(3) == i  % 计0个交点
                         AET = DeleteKnot(AET,k);
                         nAET = nAET - 1;
                      else  % 计1个交点
                         AET{1,k} = p{1};
                      end
                   else
                       AET = InsertKnot(AET,p,k);
                       nAET = nAET + 1;
                   end                   
                   break;
                   
               end  % 与 for k = 1:nAET 匹配
               if p{1}(1) > AET{1,nAET}(1,1)
                  AET = {AET{1:nAET},p{1}}; nAET = nAET + 1;
               end
           end  % 与 for j = 1:nNET 匹配
       end
    end
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
    for j = 1:2:nAET  % 交点配对
        p1 = AET{1,j}; p2 = AET{1,j+1};
        for k = round(p1(1,1)):round(p2(1,1))  % 配对的交点间填色
            plot(k,i,'ro','MarkerSize',8,'MarkerFaceColor','g');
            PixelFlag(k-xmin+3,i-ymin+3) = 1;
        end            
    end
     
    j = 1;
    while j <= nAET  % 修改活性边表
        if i+1 < ysup % i+1是为了去掉水平边
           NextScanLine = i+1;
       else
           NextScanLine = i;
       end
        if AET{1,j}(1,3) == NextScanLine  % 删除下次扫描线的非活性边
           AET = DeleteKnot(AET,j);
           nAET = nAET - 1; j = j - 1;
        else
           AET{1,j}(1,1) = AET{1,j}(1,1) + AET{1,j}(1,2); % 计算出下次扫描线与活性边的交点(横坐标)
        end
        j = j + 1;
    end
    if nAET > 0 
        AET = SortKnot(AET); % 将活性边表中结点，按照x坐标递增的顺序排序
    end
    nScan = nScan + 1;
end

%--------------------------------------------------------------------
function [a,b,c] = SolveLineEquation(x1,y1,x2,y2)
% 根据线段的两个端点，计算方程 ax+by+c=0 的系数

a = y1 - y2; b = x2 - x1; c = x1*y2 - x2*y1;

%-------------------------------------------------------------------
function AET = InsertKnot(AET,p,k)
% 活性边表中插入一个结点

nAET = size(AET,2);
if k == 1 
   AET = {p{1},AET{1:nAET}}; 
else
   AET = {AET{1,1:k-1},p{1},AET{1,k:nAET}};
end

%-------------------------------------------------------------------
function AET = DeleteKnot(AET,k)
% 活性边表中删除一个结点
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
% 将活性边表中结点，按照x坐标递增的顺序排序

nAET = size(AET,2);
for i = 1:nAET
    t(1,i) = AET{1,i}(1,1);
end
[t,index] = sort(t);
AET = {AET{1,index}};

