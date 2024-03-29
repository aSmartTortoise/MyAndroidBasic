# 深入理解 Content Transition

## 什么是 Content Transition

content transition 决定了非共享 view 元素在 activity 和 fragment 切换期间是如何进入或者退出场景的。根据 google 最新的 
Material Design 设计语言，content transition 让我们毫不费力的去协调 `Activity/Fragment` 切换过程中 view 的进入和退出，
让这个过程更流畅。在 5.0 之后 content transition 可以通过调用 Window 和 Fragment 的如下代码来设置：

* (1) setExitTransition() - 当A start B时，使A中的View退出场景的transition
* (2) setEnterTransition() - 当A start B时，使B中的View进入场景的transition
* (3) setReturnTransition() - 当B 返回 A时，使B中的View退出场景的transition
* (4) setReenterTransition() - 当B 返回 A时，使A中的View进入场景的transition

以下图为例，演示了 google play Games app 如何通过 content transition 实现 activity 之间的平滑切换。当第二个 activity 开始的时候，
enter  transition 让用户的头像从底部边缘慢慢滑入。而在 activity  退出的时候，屏幕被分成两半，各自消失在上下边缘。

![Content Transition 1](https://cdn.jsdelivr.net/gh/ocnyang/Android-Animation-Set@master/README_Res/ContentTransition1.gif?token=AQ83Mv4u2K4n4EEQlh5d-eIn_W7k6C3eks5axIYSwA%3D%3D)

到目前位置我们只是肤浅的勾勒出了 content transition 轮廓，有几个非常重要的问题仍然存在。content transition 触发的内部机制，
有哪些 Transition 类可用？framework 如何确定哪些 view 是 transitioning view？ViewGroup 和它的孩子可以被作为一个整体播放动画吗？，我们将逐个解答。

## Content Transition 内部揭秘

一个 Transition 主要有两个职责：捕获目标 view 的开始和结束时的状态、创建一个用于在两个状态之间播放动画的 Animator。Content transition 
同样如此：在 Content transition 动画（animation）创建之前，framework 必须通过设置 transitioning view 的 visibility 
将动画需要的状态信息告诉 animation。具体来说，当 Activity A startsActivity B 之时，发生了如下的事件：

### 一、Activity A 调用startActivity().

1. framework遍历A的View树，确定当A的exit transition运行时哪些view会退出场景（即哪些view是transitioning view）。
2. A的exit transition捕获A中transitioning view的开始状态。
3. framework将A中所有的transitioning view设置为INVISIBLE。
4. A的exit transition捕获到A中transitioning view的结束状态。
5. A的exit transition比较每个transitioning view的开始和结束状态，然后根据前后状态的区别创建一个Animator。Animator开始运行，
同时transitioning view退出场景。

### 二、Activity B启动.

1. framework遍历B的View树，确定当B的enter transition运行时哪些view会进入场景，transitioning view会被初始化为INVISIBLE。
2. B的enter transition捕获B中transitioning view的开始状态。
3. framework将B中所有的transitioning view设置为VISIBLE。
4. B的enter transition捕获到B中transitioning view的结束状态。
5. B的enter transition比较每个transitioning view的开始和结束状态，然后根据前后状态的区别创建一个Animator。Animator开始运行，同时transitioning view进入场景。

通过在每个transitioning view中来回切换 INVISIBLE 和 VISIBLE，framework 确保 content transition 得到创建 
animation（期望的animation）所需的状态信息。显然 content Transition 对象需要在开始和结束场景中都能记录到 
transitioning view 的 visibility。非常幸运的是抽象类 Visibility 已经为你做了这些工作：Visibility 的子类只需要实现 
[onAppear()](https://developer.android.com/reference/android/transition/Visibility.html#onAppear%28android.view.ViewGroup,%20android.transition.TransitionValues,%20int,%20android.transition.TransitionValues,%20int%29) 
和 [onDisappear()](https://developer.android.com/reference/android/transition/Visibility.html#onDisappear%28android.view.ViewGroup,%20android.transition.TransitionValues,%20int,%20android.transition.TransitionValues,%20int%29) 两个工厂方法，
在这两个工厂方法中创建并返回一个进入或者退出场景的 Animator 对象。
在 api 21 中，有三个现成的 Visibility 的实现：
[Fade](https://developer.android.com/reference/android/transition/Fade.html), 
[Slide](https://developer.android.com/reference/android/transition/Slide.html), 
和 [Explode](https://developer.android.com/reference/android/transition/Explode.html)

他们都可以用在 Activity 和 Fragment 中创建 content transition。如果必要，还可以自定义 Visibility，这将在今后的文章中讲解。

### Transitioning Views 以及 Transition Groups

到目前为止，我们假设了 content transition 是操作非共享元素的（即提到了很多次的 transitioning view）。在本节，
我们将讨论 framework 是如何决定 transitioning view 的集合以及如何使用 transition groups 自定义它。

在 transition 开始之前，framework 通过递归遍历 Activity（或者Fragment）的 Window 中的 View 树来决定 transitioning view 
的集合。整个搜索过程开始在根 view 中调用 ViewGroup 的 captureTransitioningView 方法，captureTransitioningView 的代码如下：

    /** @hide */
    @Override
    public void captureTransitioningViews(List<View> transitioningViews) {
        if (getVisibility() != View.VISIBLE) {
            return;
        }
        if (isTransitionGroup()) {
            transitioningViews.add(this);
        } else {
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                child.captureTransitioningViews(transitioningViews);
            }
        }
    }


这个递归的过程比较简单粗暴：framework跟踪不同级别的view树直到它找到一个VISIBLE的叶子view或者是一个transition group。
transition group允许我们将整个ViewGroup作为一个整体来变换。如果一个ViewGroup的isTransitionGroup()方法返回true，
则它的所有孩子都将被视为一个整体一起播放动画。否则将会继续递归该ViewGroup，其子view也会在动画的时候被单独对待。
遍历的最后结果是一个完整的transitioning view的集合将在content transition的时候播放动画。

> 注：默认情况下，isTransitionGroup() 将在 ViewGroup 有背景或者有 transition name 的时候返回 true
（参见 [documentation](https://developer.android.com/reference/android/view/ViewGroup.html#isTransitionGroup%28%29) 中对该方法的声明）。

以下图为例，在整个过程中，用户的头像先是作为一个单独的元素渐渐的进入到下一个界面，而在返回的时候他又是和其他元素一起作为一个整体被动画。
google play Games 中貌似用的是 transition group 来实现将屏幕分成两半的效果。

![Content Transition 2](https://cdn.jsdelivr.net/gh/ocnyang/Android-Animation-Set@master/README_Res/ContentTransition2.gif?token=AQ83MvthOfl1J661noVbohE8dDcqWWprks5axIYUwA%3D%3D)

有时候transition groups被用来修改一些Activity切换是出现的莫名其妙的bug。还是以上图为例，calling Activity 
显示了封面图片的相册界面，而被调用activity则显示了一个header的背景图片，共享的封面图片，一个 webview。
这个app使用了类似与Google Play Games的transition：从中间成两半，各自滑倒上下边缘。但是，
仔细观察你会发现只有上部分有滑动的动画效果，Webview没有。

那么问题来了，到底是哪里没对？上面的结果证明WebView虽然是一个ViewGroup但是没有被系统认为是transition view。
因此content transition没有在它上面运行。幸运的是我们可以在return transition之前的某个地方调用

    webView.setTransitionGroup(true)

来解决这个问题。

### 总结

总的来说，本文涉及到了三个重要的方面：

1. content transition决定非共享元素（即transitioning view）在Activity切换的时候是如何变换的。
2. Content transition的触发是通过改变transitioning view的visibility来实现的。
3. Transition group让我们可以将ViewGroup作为一个整体来变换。


**附录：**  
本文转载自： [原文链接](http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0116/2320.html)  
The address of the article in English： [Content Transitions In-Depth (part 2)](http://www.androiddesignpatterns.com/2014/12/activity-fragment-content-transitions-in-depth-part2.html)