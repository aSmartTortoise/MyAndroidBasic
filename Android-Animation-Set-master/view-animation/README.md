# Ⅰ. View Animation（视图动画）使用详解

## 1. 概述:
视图动画，也叫 Tween （补间）动画可以在一个视图容器内执行一系列简单变换（位置、大小、旋转、透明度）。
譬如，如果你有一个 TextView 对象，您可以移动、旋转、缩放、透明度设置其文本，当然，如果它有一个背景图像，背景图像会随着文本变化。

补间动画通过 XML 或 Android 代码定义，建议使用 XML 文件定义，因为它更具可读性、可重用性。

如下是视图动画相关的类继承关系：

![视图动画相关的类继承关系](https://cdn.jsdelivr.net/gh/ocnyang/Android-Animation-Set@master/README_Res/view_animation_class.png?token=AQ83MggSP_dWoz60_jMj1OXBnKWS5hJMks5awhndwA%3D%3D)

| java 类名 | xml 关键字 | 描述信息 |
| :-----: | :------- | :----- |
| AlphaAnimation | `<alpha>` 放置在 res/anim/ 目录下 | 渐变透明度动画效果 |
| RotateAnimation | `<rotate>` 放置在 res/anim/ 目录下 | 画面转移旋转动画效果 | 
| ScaleAnimation | `<scale>` 放置在 res/anim/ 目录下 | 渐变尺寸伸缩动画效果 | 
| TranslateAnimation | `<translate>` 放置在 res/anim/ 目录下 | 画面转换位置移动动画效果 | 
| AnimationSet | `<set>` 放置在 res/anim/ 目录下 | 一个持有其它动画元素 alpha、scale、translate、rotate 或者其它 set 元素的容器 | 

通过上图和上表可以直观的看出来补间动画的关系及种类了，接下来我们就详细一个一个的介绍一下各种补间动画。

## 2. 视图动画详细说明

可以看出来 Animation 抽象类是所有补间动画类的基类，所以基类会提供一些通用的动画属性方法，如下我们就来详细看看这些属性，
关于这些属性详细官方解释 [翻墙点击我](https://developer.android.com/reference/android/view/animation/Animation.html) 
或者 [翻墙点击我](https://developer.android.com/guide/topics/resources/animation-resource.html)。

### 2-1. Animation 属性详解

| xml 属性 | java 方法 | 解释 |
| :----- | :------- | :----- |
| android:detachWallpaper | setDetachWallpaper(boolean) | 是否在壁纸上运行 | 
| android:duration | setDuration(long) | 动画持续时间，毫秒为单位 | 
| android:fillAfter | setFillAfter(boolean) | 控件动画结束时是否保持动画最后的状态 | 
| android:fillBefore | setFillBefore(boolean) | 控件动画结束时是否还原到开始动画前的状态 | 
| android:fillEnabled | setFillEnabled(boolean) | 与android:fillBefore效果相同 | 
| android:interpolator | setInterpolator(Interpolator) | 设定插值器（指定的动画效果，譬如回弹等） | 
| android:repeatCount | setRepeatCount(int) | 重复次数 | 
| android:repeatMode | setRepeatMode(int) | 重复类型有两个值，reverse表示倒序回放，restart表示从头播放 | 
| android:startOffset | setStartOffset(long) | 调用start函数之后等待开始运行的时间，单位为毫秒 | 
| android:zAdjustment | setZAdjustment(int) | 表示被设置动画的内容运行时在Z轴上的位置（top/bottom/normal），默认为normal | 

也就是说，无论我们补间动画的哪一种都已经具备了这种属性，也都可以设置使用这些属性中的一个或多个。

那接下来我们就看看每种补间动画特有的一些属性说明吧。

### 2-2. Alpha 属性详解

| xml 属性 | java方法 | 解释 |
| :----- | :------- | :----- |
| android:fromAlpha | AlphaAnimation(float fromAlpha, …) | 动画开始的透明度（0.0到1.0，0.0是全透明，1.0是不透明） | 
| android:toAlpha | AlphaAnimation(…, float toAlpha) | 动画结束的透明度，同上 | 

### 2-3. Rotate 属性详解

| xml 属性 | java方法 | 解释 |
| :----- | :------- | :----- |
| android:fromDegrees | RotateAnimation(float fromDegrees, …) | 旋转开始角度，正代表顺时针度数，负代表逆时针度数 | 
| android:toDegrees | RotateAnimation(…, float toDegrees, …) | 旋转结束角度，正代表顺时针度数，负代表逆时针度数 | 
| android:pivotX | RotateAnimation(…, float pivotX, …) | 缩放起点X坐标（数值、百分数、百分数p，譬如50表示以当前View左上角坐标加50px为初始点、50%表示以当前View的左上角加上当前View宽高的50%做为初始点、50%p表示以当前View的左上角加上父控件宽高的50%做为初始点） | 
| android:pivotY | RotateAnimation(…, float pivotY) | 缩放起点Y坐标，同上规律 | 

### 2-4. Scale 属性详解

| xml 属性 | java方法 | 解释 |
| :----- | :------- | :----- |
| android:fromXScale | ScaleAnimation(float fromX, …) | 初始X轴缩放比例，1.0表示无变化 | 
| android:toXScale | ScaleAnimation(…, float toX, …) | 结束X轴缩放比例 | 
| android:fromYScale | ScaleAnimation(…, float fromY, …) | 初始Y轴缩放比例 | 
| android:toYScale | ScaleAnimation(…, float toY, …) | 结束Y轴缩放比例 | 
| android:pivotX | ScaleAnimation(…, float pivotX, …) | 缩放起点X轴坐标（数值、百分数、百分数p，譬如50表示以当前View左上角坐标加50px为初始点、50%表示以当前View的左上角加上当前View宽高的50%做为初始点、50%p表示以当前View的左上角加上父控件宽高的50%做为初始点） | 
| android:pivotY | ScaleAnimation(…, float pivotY) | 缩放起点Y轴坐标，同上规律 | 

### 2-5. Translate 属性详解

| xml 属性 | java方法 | 解释 |
| :----- | :------- | :----- |
| android:fromXDelta | TranslateAnimation(float fromXDelta, …) | 起始点X轴坐标（数值、百分数、百分数p，譬如50表示以当前View左上角坐标加50px为初始点、50%表示以当前View的左上角加上当前View宽高的50%做为初始点、50%p表示以当前View的左上角加上父控件宽高的50%做为初始点） | 
| android:fromYDelta | TranslateAnimation(…, float fromYDelta, …) | 起始点Y轴从标，同上规律 | 
| android:toXDelta | TranslateAnimation(…, float toXDelta, …) | 结束点X轴坐标，同上规律 | 
| android:toYDelta | TranslateAnimation(…, float toYDelta) | 结束点Y轴坐标，同上规律 | 

### 2-6. AnimationSet 详解

AnimationSet 继承自 Animation，是上面四种的组合容器管理类，没有自己特有的属性，他的属性继承自 Animation，所以特别注意，
当我们对 set 标签使用 Animation 的属性时会对该标签下的所有子控件都产生影响。  

## 3. 视图动画使用方法

通过上面对于动画的属性介绍之后我们来看看在 Android 中这些动画如何使用（PS：这里直接演示 xml 方式，至于 java 方式太简单了就不说了），如下：

    <?xml version="1.0" encoding="utf-8"?>
    <set xmlns:android="http://schemas.android.com/apk/res/android"
        android:interpolator="@[package:]anim/interpolator_resource"
        android:shareInterpolator=["true" | "false"] >
        <alpha
            android:fromAlpha="float"
            android:toAlpha="float" />
        <scale
            android:fromXScale="float"
            android:toXScale="float"
            android:fromYScale="float"
            android:toYScale="float"
            android:pivotX="float"
            android:pivotY="float" />
        <translate
            android:fromXDelta="float"
            android:toXDelta="float"
            android:fromYDelta="float"
            android:toYDelta="float" />
        <rotate
            android:fromDegrees="float"
            android:toDegrees="float"
            android:pivotX="float"
            android:pivotY="float" />
        <set>
            ...
        </set>
    </set>

使用：

    ImageView spaceshipImage = (ImageView) findViewById(R.id.spaceshipImage);
    Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this, R.anim.hyperspace_jump);
    spaceshipImage.startAnimation(hyperspaceJumpAnimation);

上面就是一个标准的使用我们定义的补间动画的模板。至于补间动画的使用，Animation 还有如下一些比较实用的方法介绍：

| Animation 类的方法 | 解释 |
| :-------------- | :----|
| reset() | 重置 Animation 的初始化 | 
| cancel() | 取消 Animation 动画 | 
| start() | 开始 Animation 动画 | 
| setAnimationListener(AnimationListener listener) | 给当前 Animation 设置动画监听 | 
| hasStarted() | 判断当前 Animation 是否开始 | 
| hasEnded() | 判断当前 Animation 是否结束 | 

既然补间动画只能给 View 使用，那就来看看 View 中和动画相关的几个常用方法吧，如下：

| View类的常用动画操作方法 | 解释 | 
| :-------------- | :----|
| startAnimation(Animation animation) | 对当前 View 开始设置的 Animation 动画 | 
| clearAnimation() | 取消当 View 在执行的 Animation 动画 | 

到此整个 Android 的补间动画常用详细属性及方法全部介绍完毕，如有特殊的属性需求可以访问 Android Developer 查阅即可。如下我们就来个综合大演练。

## 4. 视图动画注意事项

关于视图动画（补间动画）的例子我就不介绍了，网上简直多的都泛滥了。只是强调在使用补间动画时注意如下一点即可：

> **特别特别注意：**  
>补间动画执行之后并未改变 View 的真实布局属性值。切记这一点，譬如我们在 Activity 中有一个 Button 在屏幕上方，
我们设置了平移动画移动到屏幕下方然后保持动画最后执行状态呆在屏幕下方，这时如果点击屏幕下方动画执行之后的 Button 是没有任何反应的，
而点击原来屏幕上方没有 Button 的地方却响应的是点击Button的事件。

## 5. 视图动画 Interpolator 插值器详解

## 5-1 插值器简介

介绍补间动画插值器之前我们先来看一幅图，如下：

![补间动画插值器子类结构图](https://cdn.jsdelivr.net/gh/ocnyang/Android-Animation-Set@master/README_Res/interpolator_class.png?token=AQ83MmXiA8vZpTql3vI6zIjps7VcTNRGks5awhogwA%3D%3D)  

可以看见其实各种插值器都是实现了 Interpolator 接口而已，同时可以看见系统提供了许多已经实现 OK 的插值器，  
具体如下：

|　java 类　| xml id值 | 描述 |
| :------ | :------ | :--- |
|　AccelerateDecelerateInterpolator | @android:anim/accelerate_decelerate_interpolator | 动画始末速率较慢，中间加速 | 
|　AccelerateInterpolator | @android:anim/accelerate_interpolator | 动画开始速率较慢，之后慢慢加速 | 
|　AnticipateInterpolator | @android:anim/anticipate_interpolator | 开始的时候从后向前甩 | 
|　AnticipateOvershootInterpolator | @android:anim/anticipate_overshoot_interpolator | 类似上面 AnticipateInterpolator | 
|　BounceInterpolator | @android:anim/bounce_interpolator | 动画结束时弹起 | 
|　CycleInterpolator | @android:anim/cycle_interpolator | 循环播放速率改变为正弦曲线 | 
|　DecelerateInterpolator | @android:anim/decelerate_interpolator | 动画开始快然后慢 | 
|　LinearInterpolator | @android:anim/linear_interpolator | 动画匀速改变 | 
|　OvershootInterpolator | @android:anim/overshoot_interpolator | 向前弹出一定值之后回到原来位置 | 
|　PathInterpolator |  | 新增，定义路径坐标后按照路径坐标来跑。 | 

如上就是系统提供的一些插值器，下面我们来看看怎么使用他们。

## 5-2 插值器使用方法

插值器的使用比较简答，如下：

    <set android:interpolator="@android:anim/accelerate_interpolator">
        ...
    </set>

## 5-3 插值器的自定义

有时候你会发现系统提供的插值器不够用，可能就像 View 一样需要自定义。所以接下来我们来看看插值器的自定义，
关于插值器的自定义分为两种实现方式，xml 自定义实现（其实就是对现有的插值器的一些属性修改）或者 java 代码实现方式。如下我们来说说。

先看看 XML 自定义插值器的步骤：
1. 在 res/anim/ 目录下创建 filename.xml 文件。
2. 修改你准备自定义的插值器如下：

        <?xml version="1.0" encoding="utf-8"?>
        <InterpolatorName xmlns:android="http://schemas.android.com/apk/res/android"
            android:attribute_name="value"
            />

3. 在你的补间动画文件中引用该文件即可。

可以看见上面第二步修改的是现有插值器的一些属性，但是有些插值器却不具备修改属性，具体如下：

**`<accelerateDecelerateInterpolator>`**  
无可自定义的 attribute。

**`<accelerateInterpolator>`**  
android:factor 浮点值，加速速率（默认值为1）。

**`<anticipateInterploator>`**  
android:tension 浮点值，起始点后拉的张力数（默认值为2）。

**`<anticipateOvershootInterpolator>`**  
android:tension 浮点值，起始点后拉的张力数（默认值为2）。 
android:extraTension 浮点值，拉力的倍数（默认值为1.5）。

**`<bounceInterpolator>`**  
无可自定义的 attribute。

**`<cycleInterplolator>`**  
android:cycles 整形，循环的个数（默认为1）。

**`<decelerateInterpolator>`**  
android:factor 浮点值，减速的速率（默认为1）。

**`<linearInterpolator>`**  
无可自定义的 attribute。

**`<overshootInterpolator>`**  
android:tension 浮点值，超出终点后的张力（默认为2）。

再来看看 Java 自定义插值器的（Java 自定义插值器其实是 xml 自定义的升级，也就是说如果我们修改 xml 的属性还不能满足需求，那就可以选择通过 Java 来实现）方式。

可以看见上面所有的 Interpolator 都实现了 Interpolator 接口，而 Interpolator 接口又继承自 TimeInterpolator，
TimeInterpolator 接口定义了一个 float getInterpolation(float input);方法，这个方法是由系统调用的，
其中的参数 input 代表动画的时间，在 0 和 1 之间，也就是开始和结束之间。

如下就是一个动画始末速率较慢、中间加速的 AccelerateDecelerateInterpolator 插值器：

    public class AccelerateDecelerateInterpolator extends BaseInterpolator
            implements NativeInterpolatorFactory {
        ......
        public float getInterpolation(float input) {
            return (float)(Math.cos((input + 1) * Math.PI) / 2.0f) + 0.5f;
        }
        ......
    }

到此整个补间动画与补间动画的插值器都分析完毕了。

## 使用场景

上面介绍的都是 View 动画的基本概念，在开发中我们都将在哪些场景中使用它们呢？  
大家可以查看[**【View 动画几种特殊使用场景】**](./ViewAnimationUseTips.md) 

附录：  
本文摘录自：[工匠若水 - Android应用开发之所有动画使用详解](https://blog.csdn.net/yanbober/article/details/46481171)  

