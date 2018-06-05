# -*- coding: utf-8 -*-
# @Date    : 2018/6/5
# @Author  : Shu
# @Email   : httpservlet@yeah.net

# -*-coding:utf-8 -*-

import multiprocessing

# 监听本机的5000端口
bind = '10.175.0.98:5000'

# 在worker进程被复制（派生）之前载入应用的代码。
# 通过预加载应用，可以节省内存资源和提高服务启动时间。当然，如果你将应用加载进worker进程这个动作延后，那么重启worker将会容易很多。
# preload_app = True

# 开启进程
# workers=4
# 设置处理请求的进程数，官方推荐的值是2-4 x $(NUM_CORES)，就是核心数的2-4倍，而我在网上查到的，大多数是推荐设置为核心数的两倍+1
workers = multiprocessing.cpu_count() * 2 + 1

# 每个进程的开启线程
# 就是设置开启的多线程的数目，官方也是推荐设置为核心数的两至四倍。
# 这个设置只对进程工作方式为Gthread的产生影响。
threads = multiprocessing.cpu_count() * 2

backlog = 2048

# 工作模式为meinheld
worker_class = "egg:meinheld#gunicorn_worker"

# debug=True

# 如果不使用supervisord之类的进程管理工具可以是进程成为守护进程，否则会出问题
daemon = False

# 进程名称
proc_name = 'gunicorn.pid'

# 进程pid记录文件
pidfile = 'app_pid.log'

loglevel = 'debug'
logfile = 'debug.log'
accesslog = 'access.log'
access_log_format = '%(h)s %(t)s %(U)s %(q)s'
