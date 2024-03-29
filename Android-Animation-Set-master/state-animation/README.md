# Ⅶ. Animate View State Changes / 视图状态动画

## 介绍

所谓视图状态动画，就是 View 在状态改变时执行的动画效果。和之前我们通过 selector 选择器给 Button 设置不同状态下的背景效果是一样一样的。

咱先看一下实现的一种效果图(点击时Z轴抬高):

![点击时Z轴抬高](https://cdn.jsdelivr.net/gh/ocnyang/Android-Animation-Set@master/README_Res/view_state_change_animation.gif?token=AQ83MkyhlHVruXxcDTI07XfPKKW_ja9Tks5axLfpwA%3D%3D)  

## 使用

因为视图状态动画是在 View 不同的状态下产生的动画效果，所以我们先来看一下 View 都有哪些状态：  

| State | 说明 |
| :------- | ------ | 
| android:constantSize | If true, the drawable's reported internal size will remain constant as the state changes; the size is the maximum of all of the states. | 
| android:state_activated | State value for StateListDrawable, set when a view or its parent has been "activated" meaning the user has currently marked it as being of interest. | 
| android:state_active | State value for StateListDrawable. | 
| android:state_checkable | State identifier indicating that the object may display a check mark. | 
| android:state_checked | State identifier indicating that the object is currently checked. | 
| android:state_enabled | State value for StateListDrawable, set when a view is enabled. | 
| android:state_first | State value for StateListDrawable. | 
| android:state_focused | State value for StateListDrawable, set when a view has input focus. | 
| android:state_last | State value for StateListDrawable. | 
| android:state_middle | State value for StateListDrawable. | 
| android:state_pressed | State value for StateListDrawable, set when the user is pressing down in a view. | 
| android:state_selected | State value for StateListDrawable, set when a view (or one of its parents) is currently selected. | 
| android:state_single | State value for StateListDrawable. | 
| android:state_window_focused | State value for StateListDrawable, set when a view's window has input focus. | 
| android:variablePadding | If true, allows the drawable's padding to change based on the current state that is selected. | 
| android:visible | Indicates whether the drawable should be initially visible.| 

> 小提示(关于在 selector 中设置不同状态的 item 时匹配原则的一个细节):
> 如果有多个item，那么程序将自动从上到下进行匹配，最先匹配的将得到应用。（不是通过最佳匹配）
> 如果一个item没有任何的状态说明，那么它将可以被任何一个状态匹配。

View 状态改变的动画主要是两个类： 
1. StateListAnimator 是个动画, 在 res/anim (或者 res/animator)中  
2. AnimatedStateListDrawable 是个 Drawable, 在 res/drawable 中。 

> ps:这里的 StateListAnimator 大多教程都说放在 `res/anim` 目录下，放在此目录下能够正常使用，但在 AS3.0.1 中会有警示
*`<selector> XML file should be in either "animator" or "drawable", not "anim"`* 。  
> 我尝试放到 `res/animator` 后不再有警示，并且完全不影响使用。所以这里建议大家以后都放到此目录下。

### StateListAnimator 

xml文件：你可以改成任意一种objectAnimator动画。 这里使用的是translationZ

    <!-- animate the translationZ property of a view when pressed -->
    <selector xmlns:android="http://schemas.android.com/apk/res/android">
      <item android:state_pressed="true">
        <set>
          <objectAnimator android:propertyName="translationZ"
            android:duration="@android:integer/config_shortAnimTime"
            android:valueTo="2dp"
            android:valueType="floatType"/>
            <!-- you could have other objectAnimator elements
                 here for "x" and "y", or other properties -->
        </set>
      </item>
      
      <item android:state_enabled="true"
        android:state_pressed="false"
        android:state_focused="true">
        <set>
          <objectAnimator android:propertyName="translationZ"
            android:duration="100"
            android:valueTo="0"
            android:valueType="floatType"/>
        </set>
      </item>
    </selector>

**代码中加载：**

    //加载动画
    StateListAnimator stateLAnim = AnimatorInflater.loadStateListAnimator(this,R.anim.elevation);   
    //设置动画
    tv_elevation.setStateListAnimator(stateLAnim);

**xml 中使用：**

使用 `android:stateListAnimator` 属性将此动画分配给您的视图。

### AnimatedStateListDrawable

xml布局：这个效果有点意思（ps:但没卵用啊，目前我是没有发现适合哪种场景使用）   
当你是 pressed 状态的时候 animation-list 正着走一遍，drawable 使用最后一个。  
当你是 default 状态时 animation-list 反着走一遍，drawable 使用第一个。

`res/drawable/myanimstatedrawable`

    <?xml version="1.0" encoding="utf-8"?>
    <animated-selector 
        xmlns:android="http://schemas.android.com/apk/res/android" >
    
        <!-- provide a different drawable for each state -->
        <item
            android:id="@+id/pressed"
            android:drawable="@drawable/btn_pressed"
            android:state_pressed="true"/>
        <!-- <item
            android:id="@+id/focused"
            android:drawable="@drawable/btn_focused"
            android:state_focused="true"/> -->
        <item
            android:id="@id/default1"
            android:drawable="@drawable/btn_default"/>
    
        <!-- specify a transition -->
        <transition
            android:fromId="@+id/default1"
            android:toId="@+id/pressed" >
            <animation-list>
                <item
                    android:drawable="@drawable/con_time_tk"
                    android:duration="500"/>
                <item
                    android:drawable="@drawable/btn_default"
                    android:duration="500"/>
                <item
                    android:drawable="@drawable/btn_focused"
                    android:duration="500"/>
                <item
                    android:drawable="@drawable/btn_pressed"
                    android:duration="500"/>
                <item
                    android:drawable="@drawable/con_time_xm"
                    android:duration="500"/>
                <item
                    android:drawable="@drawable/con_time_tk"
                    android:duration="500"/>
            </animation-list>
        </transition>
    </animated-selector>

xml文件作为控件的背景使用。

**附录**  
文章摘录自：[原文地址](https://blog.csdn.net/huyuchaoheaven/article/details/47152029)