const darkTheme = {
  backgroundColor: '#282b30',
  textStyle: {
    color: '#fff'
  },
  title: {
    textStyle: {
      color: '#fff'
    }
  },
  legend: {
    textStyle: {
      color: '#8c8c8c'
    }
  },
  tooltip: {
    backgroundColor: '#1a1c1e',
    borderColor: '#303030',
    textStyle: {
      color: '#fff'
    }
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    top: '8%',
    containLabel: true
  },
  xAxis: {
    axisLine: {
      lineStyle: {
        color: '#303030'
      }
    },
    axisTick: {
      lineStyle: {
        color: '#303030'
      }
    },
    axisLabel: {
      color: '#8c8c8c'
    },
    splitLine: {
      lineStyle: {
        color: '#303030',
        type: 'dashed'
      }
    }
  },
  yAxis: {
    axisLine: {
      lineStyle: {
        color: '#303030'
      }
    },
    axisTick: {
      lineStyle: {
        color: '#303030'
      }
    },
    axisLabel: {
      color: '#8c8c8c'
    },
    splitLine: {
      lineStyle: {
        color: '#303030',
        type: 'dashed'
      }
    }
  },
  visualMap: {
    textStyle: {
      color: '#8c8c8c'
    }
  },
  series: [{
    itemStyle: {
      borderColor: '#303030'
    },
    lineStyle: {
      color: '#0b82c6'
    },
    areaStyle: {
      color: {
        type: 'linear',
        x: 0,
        y: 0,
        x2: 0,
        y2: 1,
        colorStops: [{
          offset: 0,
          color: 'rgba(11, 130, 198, 0.3)'
        }, {
          offset: 1,
          color: 'rgba(11, 130, 198, 0.05)'
        }]
      }
    }
  }],
  // 饼图颜色配置
  color: [
    '#0b82c6',
    '#2f54eb',
    '#722ed1',
    '#eb2f96',
    '#ff4d4f',
    '#faad14',
    '#52c41a',
    '#13c2c2'
  ]
};

export default darkTheme; 