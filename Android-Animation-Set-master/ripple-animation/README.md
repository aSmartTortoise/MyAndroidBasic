# Ⅳ. Ripple Effect / Touch Feedback / 触摸反馈动画

## 1. 简介

材料设计中的触摸反馈在用户和UI元素交互的点提供瞬时视觉确认。例如，当按钮被触摸时显示一个水波纹效果。
这是 Android 5.0 默认的触摸反馈动画。水波纹动画通过新的 RippleDrawable 类实现。水波纹效果可以配置在 
View 边界的末端或超出 View 的边界。例如，下面的图片序列说明了在一个按钮上触摸时的水波纹效果动画：

![ripple animation](https://cdn.jsdelivr.net/gh/ocnyang/Android-Animation-Set@master/README_Res/ripple_anim.png?token=AQ83MkxOkkejeBUg73SWQOLGYXjGTObCks5awyZAwA%3D%3D)  

刚开始在按钮上触摸时会出现左边第一张图片所示，剩下的图片说明了水波纹效果是怎样扩散到按钮边界的。
当水波纹动画完成，View 恢复到初始状态。默认的水波纹动画不超过一秒，但可以自定义动画的长度。

注意水波纹效果只会在 Android 5.0 及以上显示，之前的版本会高亮显示。

## 2. 使用系统自带的两个 Ripple 波纹效果

    //有边界
    ?android:attr/selectableItemBackground
    //无边界 （要求API21以上）
    ?android:attr/selectableItemBackgroundBorderless 
    
> 使用时只需要把上面这两个值作为 View 的 `android:background=""` 或 `android:foreground=""` 即可（如果已经有背景，
可以设置到前景属性中）。同时如果想要效果显示出来要保证 View 是可点击的(比如控件本身可点击、或者给控件设置点击事件、
或给控件设置 `android:clickable="true"`)。

这里的颜色是系统默认的，可以在 theme 里更改默认的波纹颜色

    <style name="AppTheme" parent="Theme.AppCompat.Light.NoActionBar">
        <!-- API 21 theme customizations can go here. -->
        <item name="android:colorControlHighlight">#ff0000</item>
    </style>

**代码设置**  

如果想通过代码设置 Touch Feedback 效果，方式如下：  

    int[] attrs = new int[]{R.attr.selectableItemBackground};
    TypedArray typedArray = getActivity().obtainStyledAttributes(attrs);
    int backgroundResource = typedArray.getResourceId(0, 0);
    mView.setBackgroundResource(backgroundResource);
    
有边界的效果如下：  

![ripple effect](https://cdn.jsdelivr.net/gh/ocnyang/Android-Animation-Set@master/README_Res/ripple_effect.gif?token=AQ83MtFDtMFUA3OYNYZ2PV6y8Lij2wJ9ks5awyZVwA%3D%3D)  

## 3. 自定义 Ripple

### 3-1 无边界

ripple 说是触摸反馈动画，其实它就是一个 Drawable，所以定义时需要放到 drawable 文件夹下：

    <?xml version="1.0" encoding="utf-8"?>
    <ripple xmlns:android="http://schemas.android.com/apk/res/android"
            android:color="#4285f4">
    </ripple>

* 上面中 ripple 属性中 color 代表的是波纹的颜色。
* ripple 中还有一个属性为 radius （波纹大小），则触摸显示的波纹为设置的固定大小，不会为手指触摸点开始显示。 
* 边界的颜色如果没有透明度，为纯色的话，会遮盖其它无边界的波纹。具体效果可以查看 Demo。

### 3-2 有边界

    <?xml version="1.0" encoding="utf-8"?>
    <ripple xmlns:android="http://schemas.android.com/apk/res/android"
            android:color="#4285f4"><!--波纹点击颜色-->
        <!-- 添加边界 -->
        <item android:drawable="@color/blue"/>
    </ripple>
    
* 上面的 item 是为了设置一个边界，其实它的作用就是为 View 绘制一个默认的背景。并且如果有更复杂的
需求，可以直接在 `<item><shape/></item>` 中绘制一个 shape 。

### 3-3 Button 的触摸反馈

Button 在 v21 以上默认情况下是自带有 ripple 效果和点击Z轴抬高的阴影效果（会在视图状态动画中详解）的。  
但是如果你给 Button 设置了背景图，上面自带的默认点击效果都会失去。这时如果想保持点击效果有下面几种方式:

**1_ 把 Button 的背景设置放到 Theme 中更改**  

    <style name="AutoButton" parent="AppTheme">
        <item name="buttonStyle">@style/Widget.AppCompat.Button</item>
        <item name="colorButtonNormal">?attr/colorAccent</item>
    </style>
    
然后把这个 theme 设置给 Button,其中 clolorButtonNormal 就是默认状态下的背景(无需再设置 background )。这样就会保持按钮默认的点击效果。

**2_ 给 Button 设置普通背景的同时，设置 Ripple 效果给 Foreground 前景属性**  

具体看 Demo，教程中所有方式都能在 Demo 源码中找到。

**3_ 自定义 Ripple**  

一种形式就是通过上面 3-2 中介绍的自定义 ripple。  
如果想设置不同状态的可以通过下面的形式：  

    <?xml version="1.0" encoding="utf-8"?>
    <ripple xmlns:android="http://schemas.android.com/apk/res/android"
        android:color="#FF0000" >
        <item>
            <selector>
                <item
                    android:drawable="@drawable/icon_bg1"
                    android:state_pressed="true">
                </item>
                <item
                    android:drawable="@drawable/icon_bg2"
                    android:state_pressed="false">
                </item>
            </selector>
        </item>
    </ripple>

也就相当于把 Ripple 和 Shape 结合在一起。  

> 另外需要注意的是，在自定义 `<ripple/>` 时，我们一般把它放到 drawable-v21 文件夹下，
在 drawable 文件夹下放置兼容低版本的普通 Drawable 文件，如 `<shape/>` 或者 `<selector/>`。

