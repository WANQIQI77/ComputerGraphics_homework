import matplotlib.pyplot as plt

# 定义裁剪窗口的坐标范围
x_min, y_min, x_max, y_max = 50, 50, 200, 200


# 定义一个函数，用于绘制裁剪窗口
def draw_clip_window():
    plt.plot([x_min, x_max, x_max, x_min, x_min], [y_min, y_min, y_max, y_max, y_min], 'k-')


# 定义中点分割裁剪算法
def midpoint_line_clip(x1, y1, x2, y2):
    draw_clip_window()
    plt.plot([x1, x2], [y1, y2], 'r-')

    # 判断线段的位置关系，用于确定是否需要裁剪
    def region_code(x, y):
        code = 0
        if x < x_min:
            code |= 1
        if x > x_max:
            code |= 2
        if y < y_min:
            code |= 4
        if y > y_max:
            code |= 8
        return code

    code1 = region_code(x1, y1)
    code2 = region_code(x2, y2)

    while True:
        if code1 == 0 and code2 == 0:
            # 完全在裁剪窗口内，接受线段
            plt.plot([x1, x2], [y1, y2], 'b-')
            return
        elif code1 & code2 != 0:
            # 完全在裁剪窗口外，拒绝线段
            return
        else:
            # 线段部分在裁剪窗口内，执行裁剪操作
            if code1 != 0:
                code_out = code1
            else:
                code_out = code2

            if code_out & 1:
                x = x_min
                y = y1 + (x - x1) * (y2 - y1) / (x2 - x1)
            elif code_out & 2:
                x = x_max
                y = y1 + (x - x1) * (y2 - y1) / (x2 - x1)
            elif code_out & 4:
                y = y_min
                x = x1 + (y - y1) * (x2 - x1) / (y2 - y1)
            elif code_out & 8:
                y = y_max
                x = x1 + (y - y1) * (x2 - x1) / (y2 - y1)

            if code_out == code1:
                x1, y1 = x, y
                code1 = region_code(x1, y1)
            else:
                x2, y2 = x, y
                code2 = region_code(x2, y2)


# 测试中点分割裁剪算法
x1, y1, x2, y2 = 30, 30, 250, 250

plt.figure()
plt.xlim(0, 300)
plt.ylim(0, 300)
plt.gca().set_aspect('equal', adjustable='box')

midpoint_line_clip(x1, y1, x2, y2)
plt.title("中点分割裁剪算法")
plt.show()
