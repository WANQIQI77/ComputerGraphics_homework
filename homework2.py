import numpy as np
import matplotlib.pyplot as plt

# 要扫描转换的多边形
x = [1, 8, 8, 5, 1, 1]
y = [1, 1, 6, 3, 7, 1]
nVertex = len(x) - 1  # 多边形顶点数

# 设置绘图环境
plt.figure()
plt.axis('equal')
plt.grid()

# 画出要扫描转换的多边形
plt.plot(x, y, 'b-', linewidth=2)
for i in range(nVertex):
    plt.text(x[i], y[i], f'P{i + 1} ({x[i]}, {y[i]})', fontsize=16, color='cyan')

yinf = min(y)
ysup = max(y)
xinf = min(x)
xsup = max(x)

PixelFlag = np.zeros((xsup - xinf + 10, ysup - yinf + 10))

# 对多边形每一条边打上边标志
for i in range(nVertex):
    dy = y[i + 1] - y[i]
    if dy == 0:
        continue  # 忽略水平边
    dx = (x[i + 1] - x[i]) / dy
    if dy > 0:
        xScan = x[i]
    else:
        xScan = x[i + 1]
    ymin = min(y[i], y[i + 1])
    ymax = max(y[i], y[i + 1])
    for yScan in range(ymin + 1, ymax):
        xScan = round(xScan + dx)
        plt.plot(xScan, yScan, 'ro', markersize=8, markerfacecolor='yellow')
        if PixelFlag[xScan - xinf + 1, yScan - yinf + 1] == 0:
            PixelFlag[xScan - xinf + 1, yScan - yinf + 1] = 1
        elif PixelFlag[xScan - xinf + 1, yScan - yinf + 1] == 1:
            PixelFlag[xScan - xinf + 1, yScan - yinf + 1] = 0
            plt.plot(xScan, yScan, 'wo', markersize=8, markerfacecolor='white')
for i in range(yinf, ysup + 1):  # 扫描线循环(外循环)
    inside = False
    for j in range(xinf, xsup + 1):  # 扫描线与多边形相交部分象素循环(内循环)
        if PixelFlag[j - xinf + 1, i - yinf + 1] == 1:
            InsideT = inside
            inside = not inside
            if InsideT == True and inside == False:
                # 填充右边界点
                plt.plot(j, i, 'ro', markersize=8, markerfacecolor='green')
        if inside:
            plt.plot(j, i, 'ro', markersize=8, markerfacecolor='green')
plt.show()
