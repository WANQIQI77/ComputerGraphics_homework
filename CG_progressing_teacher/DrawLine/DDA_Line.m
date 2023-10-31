function DDA_Line
% This function implements DDA algorithm and 
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
%x0 = -5; y0 = 0; x1 = 0; y1 = -5;
x0 = 0; y0 = 0; x1 = 5; y1 = 2;

% Initialize the setting and draw the line to be scan-converted
hold on; axis equal;
grid on;
plot([x0 x1],[y0 y1],'b-','LineWidth',1.3);
text(x0,y0,'\leftarrow P0','FontSize',16,'Color',[0,0,1]);
text(x1,y1,'\leftarrow P1','FontSize',16,'Color',[0,0,1]);
title('DDA Algorithm');

% Approximate the line length
if abs(x1-x0) >= abs(y1-y0)
    length = abs(x1-x0);
else  
    length = abs(y1-y0); 
end

% Select the larger of dx and dy to be one raster unit
dx = (x1-x0)/length; dy = (y1-y0)/length;
x = x0; y = y0;

% Begin main loop	
i = 1;	
while i <= length 	   
 	plot(round(x),round(y),'ro','MarkerSize',8,'MarkerFaceColor','g');	
	x = x + dx; y = y + dy; 
    i = i + 1;
end
plot(round(x1),round(y1),'ro','MarkerSize',8,'MarkerFaceColor','g');
hold off;
