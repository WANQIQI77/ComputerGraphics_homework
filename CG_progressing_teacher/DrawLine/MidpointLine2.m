function MidpointLine2
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
x0 = 0; y0 = 0; x1 = 5; y1 = 2;

% Compute the line length, initial value and increment of the discriminant
a = y0-y1; b = x1-x0; 
s1 = sign(x1-x0); s2 = sign(y1-y0);
if abs(x1-x0) >= abs(y1-y0) % when step in x direction
    length = abs(x1-x0); interchange = 0; 
else % when step in y direction
    temp = a; a = b; b = temp;
    temp = s1; s1 = s2; s2 = temp;
    length = abs(y1-y0); interchange = 1;
end
d = 2*a*s1 + b*s2; % the initial value of the discriminant
d1 = 2*a*s1; d2 = 2*(a*s1+b*s2); % the increment of the discriminant

% Initialize the setting and draw the line to be scan-converted
hold on; axis equal;
grid on;
plot([x0 x1],[y0 y1],'b-','LineWidth',1.3);
text(x0,y0,'\leftarrow P0','FontSize',16,'Color',[0,0,1]);
text(x1,y1,'\leftarrow P1','FontSize',16,'Color',[0,0,1]);
title('Midpoint Line Algorithm');

% Begin main loop
x = x0; y = y0; n_time = 0;
plot(x, y, 'o','MarkerSize',8,'MarkerEdgeColor','r','MarkerFaceColor','g');
pause(n_time); % delay n_time seconds
for j = 1:length
    if s1*s2*d < 0
        if interchange == 1 y = y+s1; d = d + d1; % step in y direction
        else  x = x + s1; y = y + s2; d = d + d2; % step in x direction
        end
    else
        if interchange == 1 x = x + s2; y = y + s1; d = d + d2; % step in y direction
        else x = x+s1; d = d + d1; % step in x direction
        end
     end
     plot(x, y, 'o','MarkerSize',8,'MarkerEdgeColor','r','MarkerFaceColor','g');
     pause(n_time); % delay n_time seconds
end 
hold off;
