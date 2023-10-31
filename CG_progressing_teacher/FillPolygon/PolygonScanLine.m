function PolygonScanLine
% This function implements polygon scan-conversion using scan-line 
% algorithm for all polygongs without a interior hole 
%
% Copyright (c) Mar, 2006.  Zhou Dengwen.  All rights reserved.
% Department of Computer Science & Technology
% North China Electric Power University(Beijing)(NCEPU)
%
% Last time modified: Mar.6, 2008
%

% The polygons to be scan-converted. The scopes of x and y are respectively
% 0~1023 and 0~767 if the resolution of the display is 1024*768.
%x = [2, 5, 11, 11, 5, 2, 2]; 
%y = [2,1, 3, 8, 5, 7, 2];
%x = [1, 8, 8, 5, 1, 1]; 
%y = [1, 1, 6, 3, 7, 1];
%x = [1, 3, 8, 8, 5, 1, 1]; 
%y = [3, 3, 1, 6, 3, 7, 3];
x = [3, 5, 5, 8, 10, 7, 5, 5, 1, 1, 3]; 
y = [1, 1, 4, 4, 6, 8, 8, 7, 7, 3, 1];
%x = [7, 10, 10, 7, 1, 4, 7]; 
%y = [1, 1, 4, 7, 4, 4, 1];
nVertex = length(x)-1; % the number of the vertexes of a polygon

% Initialize the setting
hold on; axis equal;
grid on;

% Draw the polygon to be scan-converted
plot(x,y,'b-','LineWidth',2);
for i = 1:nVertex
    text(x(i),y(i),['P' num2str(i) '(' num2str(x(i)) ',' num2str(y(i)) ')'], 'FontSize',16,'Color',[0,1,1]);
end
axis([0 max(x) 0 max(y)]); title('Polygon Scan-conversion using Scan-line Algorithm');

% Create the new edge table (NET)
yinf = min(y); ysup = max(y); NET = cell(ysup-yinf+1,1);
nScan = 1;
for i = yinf:ysup % the loop of every scan-line (outer)
    nKnot = 0; knot = {};
    for j = 1:nVertex % the loop of every edge of a polygon (inner)
        y0 = min(y(j),y(j+1));
        if y0 == i
           [a,b,c] = SolveLineEquation(x(j),y(j),x(j+1),y(j+1));
           if a == 0 continue; % ignore horizontal edges
           else
               nKnot = nKnot+1; DeltaX = -b/a;
               if y(j) == y0 x0 = x(j); else x0 = x(j+1); end
               knot(1,nKnot) = {[x0,DeltaX,max(y(j),y(j+1))]};
           end
        end
    end % end of the inner loop
    if nKnot ~= 0
        NET{nScan} = knot;
        NET{nScan} = SortKnot(NET{nScan}); % sort the node according to the ascending of x
    end
    nScan = nScan + 1;
end % end of the outer loop

% Maintain the active edge table (AET)
AET = {}; nScan = 1; n_time = 0;
for i = yinf:ysup
    nNET = size(NET{nScan},2);
    
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    if nNET ~= 0  % Insert a new active edge from the NET to the AET remaining the ascending of x
       nAET = size(AET,2);
       if nAET == 0   % i.e. the NET is empty
          AET = NET{nScan}; nAET = nNET;
       else   % i.e. there are active edges in the NET
           for j = 1:nNET % (outer)
               p = NET{nScan}(1,j);
               for k = 1:nAET % (inner)
                   
                   if AET{1,k}(1,1) < p{1}(1) continue; end  % continue to look for the next node in the AET
                   
                   % Insert a new active edge from the NET to the AET in the correct position 
                   if AET{1,k}(1,1) == p{1}(1)  % i.e. scan-line passes through the vertexes of the polygon 
                      if AET{1,k}(1,3) > i &  p{1}(3) > i  % count by 2 points of intersection
                         AET = InsertKnot(AET,p,k);
                         nAET = nAET + 1;
                     elseif AET{1,k}(1,3) == p{1}(3) & p{1}(3) == i  % count by 0 point of intersection
                         AET = DeleteKnot(AET,k);
                         nAET = nAET - 1;
                      else  % % count by 1 point of intersection
                         AET{1,k} = p{1};
                      end
                   else
                       AET = InsertKnot(AET,p,k);
                       nAET = nAET + 1;
                   end                   
                   break;
                   
               end  % Match with for k = 1:nAET (inner)
               if p{1}(1) > AET{1,nAET}(1,1)
                  AET = {AET{1:nAET},p{1}}; nAET = nAET + 1;
               end
           end  % Match with for j = 1:nNET (outer)
       end
    end
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        
    for j = 1:2:nAET  % pair the points of intersection
        p1 = AET{1,j}; p2 = AET{1,j+1};
        for k = round(p1(1,1)):round(p2(1,1))  % fill between 2 points of intersection paired
            plot(k,i,'ro','MarkerSize',8,'MarkerFaceColor','g');
            pause(n_time); % delay n_time seconds
        end            
    end
     
    j = 1;
    while j <= nAET  % modify the AET
       if i+1 < ysup
          NextScanLine = i+1;
       else % in order to draw the top edge (actually it is needless)
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

hold off;

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


