# Ⅴ. 揭露动画

## 1. 概述

在 Android 5.0 及更高的版本中，加入了一种全新的视觉动画效果，就是揭露动画。揭露动画在系统中很常见，就是类似波纹的效果，
从某一个点向四周展开或者从四周向某一点聚合起来，本文实现的效果如下所示，可以用在 Activity 里面的 View 动画效果，
也可以使用在 Activity 跳转过渡动画中，如下图使用在 View 的显示隐藏的效果图：
 
![Reveal Effect](https://cdn.jsdelivr.net/gh/ocnyang/Android-Animation-Set@master/README_Res/reveal_animation.gif)  

## 2. 使用

使用揭露动画非常简单，Android Sdk 中已经帮我们提供了一个工具类 ViewAnimationUtils 来创建揭露动画。ViewAnimationUtils 
里面只有一个静态方法 `createCircularReveal(View view, int centerX, int centerY, float startRadius, float endRadius)`，
返回一个 Animator 动画对象。

    public final class ViewAnimationUtils {
        private ViewAnimationUtils() {}
        /**
         * ......
         * @param view The View will be clipped to the animating circle.
         * @param centerX The x coordinate of the center of the animating circle, relative to
         *                <code>view</code>.
         * @param centerY The y coordinate of the center of the animating circle, relative to
         *                <code>view</code>.
         * @param startRadius The starting radius of the animating circle.
         * @param endRadius The ending radius of the animating circle.
         */
        public static Animator createCircularReveal(View view,
                int centerX,  int centerY, float startRadius, float endRadius) {
            return new RevealAnimator(view, centerX, centerY, startRadius, endRadius);
        }
    }

`ViewAnimationUtils.createCircularReveal()` 方法能够为裁剪区域添加动画以揭露或隐藏视图。我们主要使用 createCircularReveal 方法，
该方法有四个参数:  
* 第一个参数是执行揭露动画的 View 视图
* 第二个参数是相对于视图 View 的坐标系，动画圆的中心的x坐标
* 第三个参数是相对于视图 View 的坐标系，动画圆的中心的y坐标 
* 第四个参数是动画圆的起始半径，第五个参数动画圆的结束半径。

如下图所示： 

![通过坐标系分析各个参数](https://cdn.jsdelivr.net/gh/ocnyang/Android-Animation-Set@master/README_Res/createCircularReveal.jpg?)  

揭露动画有两种效果，一种是显示一组UI元素，另一种是隐藏一组UI元素：   
* 以中心点为轴点，当开始半径小于结束半径时，从开始半径处向外扩大到结束半径处显示View 
* 以中心点为轴点，当开始半径大于结束半径时，从开始半径处向内缩小到结束半径处隐藏View

> 注意：揭露动画对象只能使用一次，不能被重新使用，也就是说每次使用揭露动画都要调用 ViewAnimationUtils.createCircularReveal() 
返回一个揭露动画对象使用，同时一旦开始了动画就不能暂停或重新开始。揭露动画是一种异步动画，可以自动运行在 UI 线程上。
当揭露动画结束后，如果设置了 Animator.AnimatorListener 监听器，那么监听器的 onAnimationEnd(Animator) 方法会被调用，
但可能会被延迟调用，这取决于线程的响应能力。

## 3. 实践

Reveal Animation 要掌握的内容就这么多了，这里通过粗略分析一个实例来介绍一下具体用法，（详细代码请查看 Demo）。

这里以上面的效果图来分析：  

![Reveal demo 分析图](https://cdn.jsdelivr.net/gh/ocnyang/Android-Animation-Set@master/README_Res/reveal_animation_demo.png)  

通过这个分析图，其实应该就很容易理解了，我们要做的所有事情就是确定 `ViewAnimationUtils.createCircularReveal()` 这
个方法的四个参数。

这里查看具体的代码：  

    fab.setOnClickListener(... {
            launchRevealAnimation();
    });

    private void launchRevealAnimation() {
        //求出第2个和第3个参数
        int[] vLocation = new int[2];
        fab.getLocationInWindow(vLocation);
        int centerX = vLocation[0] + fab.getWidth() / 2;
        int centerY = vLocation[1] + fab.getHeight() / 2;

        //求出要揭露 View 的对角线，来作为扩散圆的最大半径
        int hypotenuse = (int) Math.hypot(mPuppet.getWidth(), mPuppet.getHeight());

        if (flag) {//隐藏 揭露对象
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(mPuppet, centerX, centerY, hypotenuse, 0);
            circularReveal.setDuration(2000);
            circularReveal.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}

                @Override
                public void onAnimationEnd(Animator animation) {
                    mPuppet.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {}

                @Override
                public void onAnimationRepeat(Animator animation) {}
            });
            circularReveal.start();
            flag = false;
        } else {//显示 揭露对象
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(mPuppet, centerX, centerY, 0, hypotenuse);
            circularReveal.setDuration(2000);
            //注意：这里显示 mPuppet 调用并没有在监听方法里，并且是在动画开始前调用。
            mPuppet.setVisibility(View.VISIBLE);
            circularReveal.start();
            flag = true;
        }
    }
    
上面就是实现一个揭露对象显示与隐藏的具体代码。没有任何难点，唯一要注意的是：显示揭露对象时并不是在动画监听方法的 `onAnimationEnd` 里
进行的。为什么要这样呢（我也是栽过这个坑里好久,后来才理解）？  
* 首先揭露对象要先于动画开始前显示，因为如果动画开始时，要揭露的对象处于隐藏状态，那么动画就不会有效果，因为隐藏状态是看不到效果的。所以是不能
在动画结束的回调里才设置 View VISIBLE，这时动画已经结束了不会看到任何效果了。  
* 另一方面，为什么不在动画开始的回调方法里设置 View.VISIBLE ，在这里调用也是可以的，一般这样设置也不会有任何问题。但我们在前面就说过揭露动画
是一个异步动画，它的回调方法都不能保证在准确的时间里调用(可能延迟调用，虽然很短)，所以我们建议直接在调用动画开始方法 start() 直接设置 View.VISIBLE 
为好。