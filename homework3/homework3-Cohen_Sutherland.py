import matplotlib.pyplot as plt

# 定义裁剪窗口的边界
LEFT = 1
RIGHT = 2
BOTTOM = 4
TOP = 8

# 定义裁剪窗口的坐标范围
x_min, y_min, x_max, y_max = 50, 50, 200, 200


# 定义一个函数，用于计算区域码
def compute_region_code(x, y):
    code = 0
    if x < x_min:
        code |= LEFT
    if x > x_max:
        code |= RIGHT
    if y < y_min:
        code |= BOTTOM
    if y > y_max:
        code |= TOP
    return code


# 定义 Cohen-Sutherland 裁剪算法
def cohen_sutherland_clip(x1, y1, x2, y2):
    while True:
        code1 = compute_region_code(x1, y1)
        code2 = compute_region_code(x2, y2)

        # 如果两个点都在裁剪窗口内，接受该线段
        if code1 == 0 and code2 == 0:
            return x1, y1, x2, y2

        # 如果两个点都在裁剪窗口外部，拒绝该线段
        if (code1 & code2) != 0:
            return None

        # 计算需要裁剪的点的坐标
        code_out = code1 if code1 != 0 else code2
        x, y = 0, 0
        if code_out & TOP:
            x = x1 + (x2 - x1) * (y_max - y1) / (y2 - y1)
            y = y_max
        elif code_out & BOTTOM:
            x = x1 + (x2 - x1) * (y_min - y1) / (y2 - y1)
            y = y_min
        elif code_out & RIGHT:
            y = y1 + (y2 - y1) * (x_max - x1) / (x2 - x1)
            x = x_max
        elif code_out & LEFT:
            y = y1 + (y2 - y1) * (x_min - x1) / (x2 - x1)
            x = x_min

        # 更新坐标并继续检查
        if code_out == code1:
            x1, y1 = x, y
        else:
            x2, y2 = x, y


# 测试 Cohen-Sutherland 裁剪算法
x1, y1, x2, y2 = 30, 30, 250, 250

# 创建裁剪窗口的可视化
plt.figure()
plt.plot([x_min, x_max, x_max, x_min, x_min], [y_min, y_min, y_max, y_max, y_min], 'k-')
plt.xlim(0, 300)
plt.ylim(0, 300)
plt.gca().set_aspect('equal', adjustable='box')

# 绘制裁剪前的线段
plt.plot([x1, x2], [y1, y2], 'r-')

result = cohen_sutherland_clip(x1, y1, x2, y2)

if result:
    x1, y1, x2, y2 = result
    # 绘制裁剪后的线段
    plt.plot([x1, x2], [y1, y2], 'b-')
    plt.title(f"裁剪后的线段：({x1}, {y1}) 到 ({x2}, {y2})")
else:
    plt.title("线段被完全裁剪")

plt.show()
