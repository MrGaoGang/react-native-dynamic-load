package com.reactnativedynamic.core.model;

import com.reactnativedynamic.core.DynamicReactActivity;

/***
 * 加载rn bundle的信息
 */
public class RnBundle {
    /**
     *  如果是网络下载的方式，则需要设置下载的路径，默认可以写自己的bundle名称
     */
    public String scriptPath;
    /**
     *  网络，文件还是assets
     */
    public DynamicReactActivity.ScriptType scriptType;
    /***
     * 必须传递的，不管是网络，文件还是assets，都需要传递脚本url
     */
    public String scriptUrl;
    /**
     * 必须传递: 工程下面的app.json中的Name字段
     */
    public String appName;
}
