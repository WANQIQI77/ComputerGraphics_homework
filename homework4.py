import matplotlib.pyplot as plt
import numpy as np

# 定义控制点
control_points = [(50, 100), (150, 200), (250, 50), (350, 100)]

# 定义De Casteljau递归函数
def de_casteljau(points, t):
    if len(points) == 1:
        return points[0]
    else:
        new_points = []
        for i in range(len(points) - 1):
            x = (1 - t) * points[i][0] + t * points[i + 1][0]
            y = (1 - t) * points[i][1] + t * points[i + 1][1]
            new_points.append((x, y))
        return de_casteljau(new_points, t)

# 生成Bezier曲线上的点
t_values = np.linspace(0, 1, 100)
curve_points = [de_casteljau(control_points, t) for t in t_values]

# 可视化
plt.figure()
plt.xlim(0, 400)
plt.ylim(0, 300)

# 绘制Bezier曲线
x, y = zip(*curve_points)
plt.plot(x, y, 'b-', label='Bezier Curve')

# 绘制控制多边形
x, y = zip(*control_points)
plt.plot(x, y, 'ro-', label='Control Points')

plt.legend()
plt.title("De Casteljau算法生成Bezier曲线")
plt.show()
