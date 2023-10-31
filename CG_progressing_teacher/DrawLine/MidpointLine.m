function MidpointLine
% This function implements Midpoint line algorithm and 
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

% Compute the increment
a = y0-y1; b = x1-x0; 
s1 = sign(x1-x0); s2 = sign(y1-y0);
d1x = 2*a*s1; d2x = 2*(a*s1+b*s2); % when step in x direction
d1y = 2*b*s2; d2y = 2*(a*s1+b*s2); % when step in y direction 

% Compute the line length and initial value of the discriminant
if abs(x1-x0) >= abs(y1-y0) % when step in x direction
    length = abs(x1-x0); tag = 0; 
    d = 2*a*s1 + b*s2;
else % when step in y direction
    length = abs(y1-y0); tag = 1;
    d = a*s1 + 2*b*s2; 
end

% Initialize the setting and draw the line to be scan-converted
hold on; axis equal;
grid on;
plot([x0 x1],[y0 y1],'b-','LineWidth',1.3);
text(x0,y0,'\leftarrow P0','FontSize',16,'Color',[0,0,1]);
text(x1,y1,'\leftarrow P1','FontSize',16,'Color',[0,0,1]);
title('Midpoint Line Algorithm');

% Begin main loop
x = x0; y = y0; n_time = 3;
plot(x, y, 'o','MarkerSize',8,'MarkerEdgeColor','r','MarkerFaceColor','g');
pause(n_time); % delay n_time seconds
for j = 1:length
     if tag == 0 % when step in x direction
         if j ~= length % draw the midpoint
            plot(x+s1,y+s2/2,'bs','MarkerFaceColor','r'); 
         end
         if s1*s2*d < 0
           x = x + s1; y = y + s2; d = d + d2x;
         else
           x = x + s1; d = d + d1x;
         end
     else % when step in y direction 
         if j ~= length % Draw the midpoint
            plot(x+s1/2,y+s2,'bs','MarkerFaceColor','r'); 
         end
         if s1*s2*d > 0
            x = x + s1; y = y + s2; d = d + d2y; 
         else
            y = y + s2; d = d + d1y;
         end       
     end	
     plot(x, y, 'o','MarkerSize',8,'MarkerEdgeColor','r','MarkerFaceColor','g');
     pause(n_time); % delay n_time seconds
end 
hold off;
