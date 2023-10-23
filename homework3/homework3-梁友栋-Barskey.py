import matplotlib.pyplot as plt

# 定义裁剪窗口的坐标范围
x_min, y_min, x_max, y_max = 50, 50, 200, 200


# 定义一个函数，用于绘制裁剪窗口
def draw_clip_window():
    plt.plot([x_min, x_max, x_max, x_min, x_min], [y_min, y_min, y_max, y_max, y_min], 'k-')


# 定义梁友栋-Barsky算法
def liang_barsky_clip(x1, y1, x2, y2):
    draw_clip_window()
    plt.plot([x1, x2], [y1, y2], 'r-')

    # 计算参数
    dx = x2 - x1
    dy = y2 - y1
    p = [-dx, dx, -dy, dy]
    q = [x1 - x_min, x_max - x1, y1 - y_min, y_max - y1]

    u1, u2 = 0.0, 1.0

    for i in range(4):
        if p[i] == 0:
            if q[i] < 0:
                return
        else:
            t = q[i] / p[i]
            if p[i] < 0 and t > u1:
                u1 = t
            elif p[i] > 0 and t < u2:
                u2 = t

    if u1 < u2:
        x1_clip = x1 + u1 * dx
        y1_clip = y1 + u1 * dy
        x2_clip = x1 + u2 * dx
        y2_clip = y1 + u2 * dy
        plt.plot([x1_clip, x2_clip], [y1_clip, y2_clip], 'b-')
        plt.title("梁友栋-Barsky算法")
    else:
        plt.title("线段被完全裁剪")


# 测试梁友栋-Barsky算法
x1, y1, x2, y2 = 30, 30, 250, 250

plt.figure()
plt.xlim(0, 300)
plt.ylim(0, 300)
plt.gca().set_aspect('equal', adjustable='box')

liang_barsky_clip(x1, y1, x2, y2)
plt.show()
