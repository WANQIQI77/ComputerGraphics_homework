function Bresenham_Line
% This function implements Bresenham algorithm and 
% can be applied to all quadrants
%
% Copyright (c) 2005.  Zhou Dengwen.  All rights reserved.
% Department of Computer Science & Technology
% North China Electric Power University(Beijing)(NCEPU)
%
% Lsat time modified: Feb. 28, 2008
%

% The line end points are (x0,y0) and (x1,y1),
% assumed not equal
x0 = 0; y0 = 0; x1 = 10; y1 = 5;

% Initialize the setting and draw the line to be scan-converted
hold on; axis equal;
grid on;
plot([x0 x1],[y0 y1],'b-','LineWidth',1.3);
text(x0,y0,'\leftarrow P0','FontSize',16,'Color',[0,0,1]);
text(x1,y1,'\leftarrow P1','FontSize',16,'Color',[0,0,1]);
title('Bresenham Algorithm');

% Initialize variables
x = x0; y = y0;
dx = abs(x1-x0); dy = abs(y1- y0); 
s1 = sign(x1-x0); s2 = sign(y1-y0);

% Interchange dx and dy, depending on the slop of the line
if dy > dx
   temp = dx; dx = dy; dy = temp; interchange = 1;
else 
   interchange = 0;
end

% Initiaze the error term to compensate for a nonzero intercept
e = 2*dy-dx;

% Begin main loop
for i = 1:dx
	plot(x,y,'ro','MarkerSize',8,'MarkerFaceColor','g');
	if e > 0
       if interchange == 1 x = x + s1; else y = y + s2; end
	   e = e-2*dx;
    end
	if interchange == 1 y = y + s2; else x = x + s1; end
	e = e + 2*dy;
end
plot(x1,y1,'ro','MarkerSize',8,'MarkerFaceColor','g');
hold off;
