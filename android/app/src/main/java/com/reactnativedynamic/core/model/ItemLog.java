package com.reactnativedynamic.core.model;

public class ItemLog {
    public Long clickTime = Long.valueOf(0);
    public Long willTime = Long.valueOf(0);
    public Long initTime  = Long.valueOf(0);;//组件初始化时间
    public Long resDownloadTime  = Long.valueOf(0);;
    public Long resLoadTime  = Long.valueOf(0);;
    public Long startActivityTime  = Long.valueOf(0);;//activity启动开始
    public Long initCreateViewTime  = Long.valueOf(0);;
    public Long createViewEndTime  = Long.valueOf(0);;
    public Long startAppTime  = Long.valueOf(0);;
    public Long startRenderTime  = Long.valueOf(0);;
    public Long componentMountedTime  = Long.valueOf(0);;
    public Long updatedTime  = Long.valueOf(0);;//更新结束时间
}