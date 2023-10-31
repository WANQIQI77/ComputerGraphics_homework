function MidpointCircle
% This function implements Midpoint circle algorithm and 
% can be applied to all quadrants
% (scan-converting the first octant arc)
%
% Copyright (c) 2005.  Zhou Dengwen.  All rights reserved.
% Department of Computer Science & Technology
% North China Electric Power University(Beijing)(NCEPU)
%
% Lsat time modified: Feb. 28, 2008
%

% The radius of the circle to be scan-converted
% (the centre of the circle is at the original point)
r = 10;

% Initialize the setting and draw the circle to be scan-converted
hold on; axis equal;
grid on;
x = -r:0.01:r; 
y = sqrt(r^2-x.^2); 
plot(x,y,x,-y,'b-','LineWidth',1.3);
text(r,0,'\leftarrow P0','FontSize',16,'Color',[0,0,1]);
text(r/sqrt(2),r/sqrt(2),'\leftarrow P1','FontSize',16,'Color',[0,0,1]);
title(['Midpoint Circle Algorithm, r = ' num2str(r)]);

% Begin main loop (draw the first octant arc)
x = r; y = 0; d = 1.25 - r; PointArray = []; n_time = 3;
plot(x,y,'ro','MarkerSize',8,'MarkerFaceColor','g');
PointArray = [PointArray; x y];
while y <= x 	
	if d < 0  
        d = d + 2*y+3;
    else 
        d = d - 2*(x-y)+5;  
        x = x -1;
    end
    y = y + 1;
    if y > x break; end
    PointArray = [PointArray; [x,y]];
    plot(x,y,'ro','MarkerSize',8,'MarkerFaceColor','g');
    pause(n_time) % delay n_time seconds
end

% Draw the second octant arc
n = size(PointArray,1); PointArray2 = [];
for j = 1:n
    x = (PointArray(j,1)-PointArray(j,2))/sqrt(2);
    y = (PointArray(j,1)+PointArray(j,2))/sqrt(2);
    plot(x,y,'ro','MarkerSize',8,'MarkerFaceColor','g');
    PointArray2 = [PointArray2; x y];
end
pause(n_time) % delay n_time seconds

% Draw the arc in the second quadrant
PointArray2 = [PointArray2; PointArray];
n = size(PointArray2,1); PointArray = [];
for j = 1:n
    x = -PointArray2(j,1); y = PointArray2(j,2);
    plot(x,y,'ro','MarkerSize',8,'MarkerFaceColor','g');
    PointArray = [PointArray; x y];
end
pause(n_time) % delay n_time seconds

% Draw the lower half of the circle
PointArray2 = [PointArray2; PointArray];
n = size(PointArray2,1);
for j = 1:n
    plot(PointArray2(j,1),-PointArray2(j,2),'ro','MarkerSize',8,'MarkerFaceColor','g');
end
hold off;
