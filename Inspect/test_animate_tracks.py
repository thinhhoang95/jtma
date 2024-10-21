import numpy as np
from animate_tracks import get_xy_for_pms
import matplotlib.pyplot as plt
for t in np.linspace(0, 1, 100):
    x, y = get_xy_for_pms(0.5, 1, {'1': (0, 0), '2': (1, 0)}, '1', '2', t)
    plt.plot(x, y, 'o')
plt.show()