function drawCircleMidpoint(centerX, centerY, radius)
    x = 0;
    y = radius;
    p = 1.25 - radius; % 初始决策参数

    % 初始化绘图窗口
    figure;
    hold on;
    axis equal;

    % 绘制第一个点
    plotPoint(centerX, centerY, x, y);

    % 开始绘制圆
    while x <= y
        
        if p <= 0
            p = p + 2*x + 3;
        else
            p = p + 2*(x-y) +5 ;
            y=y-1;
        end
        x=x+1;
        plotPoint(centerX, centerY, x, y);
    end

    hold off;
end

function plotPoint(centerX, centerY, x, y)
    % 在八个对称象限中绘制像素点
    plot(centerX + x, centerY + y, 'ro'); % 第一象限
    plot(centerX - x, centerY + y, 'ro'); % 第二象限
    plot(centerX + x, centerY - y, 'ro'); % 第六象限
    plot(centerX - x, centerY - y, 'ro'); % 第七象限

    plot(centerX + y, centerY + x, 'ro'); % 第三象限
    plot(centerX - y, centerY + x, 'ro'); % 第四象限
    plot(centerX + y, centerY - x, 'ro'); % 第五象限
    plot(centerX - y, centerY - x, 'ro'); % 第八象限
end


