# ViewPropertyAnimator 原理解析

我们先通过一副图来大概了解一下 ViewPropertyAnimator 内部的整体运行工作原理（图太小的话请右键在新页面打开哈）： 

![ViewPropertyAnimator](https://github.com/OCNYang/Android-Animation-Set/blob/master/README_Res/ViewPropertyAnimator.jpg)  

我们这里先给出整体执行流程（有个整体的概念就行哈，不理解也没有关系，看完下面的分析，再回来来看看也是可以），然后再详细分析：

1. 通过 `imageView.animate()` 获取 `ViewPropertyAnimator` 对象。
2. 调用 alpha、translationX 等方法，返回当前 ViewPropertyAnimator 对象，可以继续链式调用
3. alpha、translationX 等方法内部最终调用 `animatePropertyBy(int constantName, float startValue, float byValue)`方法
4. 在 animatePropertyBy 方法中则会将 alpha、translationX 等方法的操作封装成 NameVauleHolder，并将每个 NameValueHolder 对象添加到准备列表 mPendingAnimations 中。
5. animatePropertyBy 方法启动 mAnimationStarter，调用startAnimation，开始动画。
6. startAnimation 方法中会创建一个 ValueAnimator 对象设置内部监听器 AnimatorEventListener，并将 mPendingAnimations 和
要进行动画的属性名称封装成一个 PropertyBundle 对象，最后 mAnimatorMap 保存当前 Animator 和对应的P ropertyBundle 对象。
该Map将会在 animatePropertyBy 方法和 Animator 监听器 mAnimatorEventListener 中使用，启动动画。
7. 在动画的监听器的 onAnimationUpdate 方法中设置所有属性的变化值，并通过RenderNode类优化绘制性能，最后刷新界面。
有了整体概念后，现在我们沿着该工作流程图的路线来分析 ViewPropertyAnimator内部执行过程，从上图可以看出，通过 View#animate() 获取
到 ViewPropertyAnimator 实例后，可以通过 ViewPropertyAnimator 提供的多种方法来设置动画，如 translationX()、scaleX() 等等，
而当调用完这些方法后，其内部最终则会通过多次调用 animatorPropertyBy()，我们先看看 animatePropertyBy 方法源码：

        /**
         * Utility function, called by animateProperty() and animatePropertyBy(), which handles the
         * details of adding a pending animation and posting the request to start the animation.
         *
         * @param constantName The specifier for the property being animated
         * @param startValue   The starting value of the property
         * @param byValue      The amount by which the property will change
         */
        private void animatePropertyBy(int constantName, float startValue, float byValue) {
            // First, cancel any existing animations on this property
            //判断该属性上是否存在运行的动画，存在则结束。
            if (mAnimatorMap.size() > 0) {
                Animator animatorToCancel = null;
                Set<Animator> animatorSet = mAnimatorMap.keySet();
                for (Animator runningAnim : animatorSet) {
                    PropertyBundle bundle = mAnimatorMap.get(runningAnim);
                    if (bundle.cancel(constantName)) {// 结束对应属性动画
                        // property was canceled - cancel the animation if it's now empty
                        // Note that it's safe to break out here because every new animation
                        // on a property will cancel a previous animation on that property, so
                        // there can only ever be one such animation running.
                        if (bundle.mPropertyMask == NONE) {//判断是否还有其他属性
                            // the animation is no longer changing anything - cancel it
                            animatorToCancel = runningAnim;
                            break;
                        }
                    }
                }
                if (animatorToCancel != null) {
                    animatorToCancel.cancel();
                }
            }
            //将要执行的属性的名称，开始值，变化值封装成NameValuesHolder对象
            NameValuesHolder nameValuePair = new NameValuesHolder(constantName, startValue, byValue);
            //添加到准备列表中
            mPendingAnimations.add(nameValuePair);
            mView.removeCallbacks(mAnimationStarter);
            mView.postOnAnimation(mAnimationStarter);
        }

从源码可以看出，animatePropertyBy 方法主要干了以下几件事：

* 首先会去当前属性是否还有在动画在执行，如果有则先结束该属性上的动画，保证该属性上只有一个 Animator 在进行动画操作。
* 将本次动画需要执行的动画属性封装成一个NameValueHolder对象
* 将每个 NameValuesHolder 对象添加到 mPendingAnimations 的准备列表中 

NameValuesHolder对象是一个内部类，其相关信息如下:  
**NameValueHolder：**   
内部类，封装每个要进行动画属性值开始值和变化值，比如 translationX(200)，那么这个动画的属性值、
开始值和变化值将被封装成一个 NameValueHolder，其源码也非常简单：

    static class NameValuesHolder {
        int mNameConstant;//要进行动画的属性名称
        float mFromValue;//开始值
        float mDeltaValue;//变化值
        NameValuesHolder(int nameConstant, float fromValue, float deltaValue) {
            mNameConstant = nameConstant;
            mFromValue = fromValue;
            mDeltaValue = deltaValue;
        }
    }

而 mPendingAnimations 的相关信息如下：  
**mPendingAnimations：**  
装载的是准备进行动画的属性值（NameValueHolder）所有列表，也就是每次要同时进行动画的全部属性的集合

    ArrayList<NameValuesHolder> mPendingAnimations = new ArrayList<NameValuesHolder>();

当添加完每个要运行的属性动画后，则会通过 mAnimationStarter 对象去调用 startAnimation()，启动动画。 

**Runnable mAnimationStarter：**  
用来执行动画的 Runnable。它会执行 startAnimation 方法，而在 startAnimation 方法中
会通过 `animator.start()` 启动动画，源码非常简洁：

    private Runnable mAnimationStarter = new Runnable() {
            @Override
            public void run() {
                startAnimation();
            }
    };

接着我们看看 startAnimation() 的源码：

    /**
     * Starts the underlying Animator for a set of properties. We use a single animator that
     * simply runs from 0 to 1, and then use that fractional value to set each property
     * value accordingly.
     */
    private void startAnimation() {
        if (mRTBackend != null && mRTBackend.startAnimation(this)) {
            return;
        }
        mView.setHasTransientState(true);
        //创建ValueAnimator
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f);
        //clone一份mPendingAnimations赋值给nameValueList
        ArrayList<NameValuesHolder> nameValueList =
                (ArrayList<NameValuesHolder>) mPendingAnimations.clone();
         //赋值完后清空
        mPendingAnimations.clear();
        //用于标识要执行动画的属性
        int propertyMask = 0;
        int propertyCount = nameValueList.size();
        //遍历所有nameValuesHolder，取出其属性名称mNameConstant，
        //执行"|"操作并最终赋值propertyMask
        for (int i = 0; i < propertyCount; ++i) {
            NameValuesHolder nameValuesHolder = nameValueList.get(i);
            propertyMask |= nameValuesHolder.mNameConstant;
        }
        //创建PropertyBundle，并添加到mAnimatorMap中
        mAnimatorMap.put(animator, new PropertyBundle(propertyMask, nameValueList));
        if (mPendingSetupAction != null) {
            //设置硬件加速
            mAnimatorSetupMap.put(animator, mPendingSetupAction);
            mPendingSetupAction = null;
        }
        if (mPendingCleanupAction != null) {
           //移除硬件加速
            mAnimatorCleanupMap.put(animator, mPendingCleanupAction);
            mPendingCleanupAction = null;
        }
        if (mPendingOnStartAction != null) {
            //设置开始的动画（监听器的开始方法中调用）
            mAnimatorOnStartMap.put(animator, mPendingOnStartAction);
            mPendingOnStartAction = null;
        }
        if (mPendingOnEndAction != null) {
            //设置结束后要进行的下一个动画（监听器的结束方法中调用）
            mAnimatorOnEndMap.put(animator, mPendingOnEndAction);
            mPendingOnEndAction = null;
        }
        //添加内部监听器
        animator.addUpdateListener(mAnimatorEventListener);
        animator.addListener(mAnimatorEventListener);
        //判断是否延长开始
        if (mStartDelaySet) {
            animator.setStartDelay(mStartDelay);
        }
        //执行动画的实现
        if (mDurationSet) {
            animator.setDuration(mDuration);
        }
        //设置插值器
        if (mInterpolatorSet) {
            animator.setInterpolator(mInterpolator);
        }
        //开始执行动画
        animator.start();
    }

我们上面的注释非常全面，这里 startAnimation 主要做下面几件事： 

* 创建 Animator,变化值从 0 到 1，设置内部监听器 mAnimatorEventListener。
* clone 一份 mPendingAnimations 列表，并计算属性值标记 propertyMask，封装成 PropertyBundle 对象。
* 使用 mAnimatorMap 保存当前 Animator 和对应的 PropertyBundle 对象。该Map 将会在 animatePropertyBy 方法和 Animator 监听器 
mAnimatorEventListener 中使用。
* 启动 animator 动画。

关于PropertyBundle的分析如下： 
**PropertyBundle：**  
内部类，存放着将要执行的动画的属性集合信息，每次调用 animator.start(); 前，都会将存放在 
mPendingAnimations 的 clone 一份存入 PropertyBundle 的内部变量 mNameValuesHolder 中，然后再将遍历 
mPendingAnimations 中的 NameValueHolder 类，取出要执行的属性进行 ”|” 操作,最后记录成一个 mPropertyMask 的变量，
存放在 PropertyBundle 中，PropertyBundle 就是最终要执行动画的全部属性的封装类，其内部结构如下图 

![PropertyBundle](https://cdn.jsdelivr.net/gh/ocnyang/Android-Animation-Set@master/README_Res/PropertyBundle.jpg?token=AQ83MuTmmPsjVKQwY_S3KP8KOvh8RIaHks5awfU8wA%3D%3D)  

**AnimatorEventListener:**  
ViewPropertyAnimator 内部的监听器。这个类实现了 Animator.AnimatorListener, 
ValueAnimator.AnimatorUpdateListener 接口。我们前面已经分享过它的部分源码，这个类还有一个 onAnimationUpdate() 
的监听方法，这个方法我们放在后面解析，它是动画执行的关键所在。

**HashMap mAnimatorMap:**  
存放 PropertyBundle 类的Map。这个Map中存放的是正在执行的动画的 PropertyBundle，
这个 PropertyBundle 包含这本次动画的所有属性的信息。最终在 AnimatorEventListener 的 onAnimationUpdate() 
方法中会通过这个map获取相应的属性，然后不断更新每帧的属性值以达到动画效果。通过前面对 animatePropertyBy 方法的分析，
我们可以知道该Map会保证当前只有一个 Animator 对象对该 View 的属性进行操作，不会存在两个 Animator 在操作同一个属性，其声明如下：

    private HashMap<Animator, PropertyBundle> mAnimatorMap =
             new HashMap<Animator, PropertyBundle>();

最后我们看看动画是在哪里执行的，根据我们前面的原理图，内部监听器的 onAnimationUpdate() 方法将会被调用（当然内部监听器 
AnimatorEventListener 实现了两个动画监听接口，其开始，结束，重复，取消4个方法也会被调用，这个我们前面已分析过）。

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        //取出当前Animator对应用propertyBundle对象
        PropertyBundle propertyBundle = mAnimatorMap.get(animation);
        if (propertyBundle == null) {
            // Shouldn't happen, but just to play it safe
            return;
        }
        //是否开启了硬件加速
        boolean hardwareAccelerated = mView.isHardwareAccelerated();
    
        // alpha requires slightly different treatment than the other (transform) properties.
        // The logic in setAlpha() is not simply setting mAlpha, plus the invalidation
        // logic is dependent on how the view handles an internal call to onSetAlpha().
        // We track what kinds of properties are set, and how alpha is handled when it is
        // set, and perform the invalidation steps appropriately.
        boolean alphaHandled = false;
        if (!hardwareAccelerated) {
            mView.invalidateParentCaches();
        }
        //取出当前的估算值（插值器计算值）
        float fraction = animation.getAnimatedFraction();
        int propertyMask = propertyBundle.mPropertyMask;
        if ((propertyMask & TRANSFORM_MASK) != 0) {
            mView.invalidateViewProperty(hardwareAccelerated, false);
        }
        //取出所有要执行的属性动画的封装对象NameValuesHolder
        ArrayList<NameValuesHolder> valueList = propertyBundle.mNameValuesHolder;
        if (valueList != null) {
            int count = valueList.size();
            //遍历所有NameValuesHolder，计算变化值，并设置给对应的属性
            for (int i = 0; i < count; ++i) {
                NameValuesHolder values = valueList.get(i);
                float value = values.mFromValue + fraction * values.mDeltaValue;
                if (values.mNameConstant == ALPHA) {
                    alphaHandled = mView.setAlphaNoInvalidation(value);
                } else {
                    setValue(values.mNameConstant, value);
                }
            }
        }
        if ((propertyMask & TRANSFORM_MASK) != 0) {
            if (!hardwareAccelerated) {
                mView.mPrivateFlags |= View.PFLAG_DRAWN; // force another invalidation
            }
        }
        // invalidate(false) in all cases except if alphaHandled gets set to true
        // via the call to setAlphaNoInvalidation(), above
        if (alphaHandled) {
            mView.invalidate(true);
        } else {
            mView.invalidateViewProperty(false, false);
        }
        if (mUpdateListener != null) {
            mUpdateListener.onAnimationUpdate(animation);
        }
    }

onAnimationUpdate方法主要做了以下几件事： 

* 取出当前 Animator 对应用 propertyBundle 对象并获取当前的估算值（插值器计算值），用于后续动画属性值的计算
* 从 propertyBundle 取出要进行动画的属性列表 ArrayList<NameValuesHolder> valueList
* 遍历所有 NameValuesHolder，计算变化值，并通过 setValue 设置给对应的属性，如果是 ALPHA，则会特殊处理一下，最终形成动画效果

setValue方法源码：

    private void setValue(int propertyConstant, float value) {
        final View.TransformationInfo info = mView.mTransformationInfo;
        final RenderNode renderNode = mView.mRenderNode;
        switch (propertyConstant) {
            case TRANSLATION_X:
                renderNode.setTranslationX(value);
                break;
            case TRANSLATION_Y:
                renderNode.setTranslationY(value);
                break;
            case TRANSLATION_Z:
                renderNode.setTranslationZ(value);
                break;
            case ROTATION:
                renderNode.setRotation(value);
                break;
            case ROTATION_X:
                renderNode.setRotationX(value);
                break;
            case ROTATION_Y:
                renderNode.setRotationY(value);
                break;
            case SCALE_X:
                renderNode.setScaleX(value);
                break;
            case SCALE_Y:
                renderNode.setScaleY(value);
                break;
            case X:
                renderNode.setTranslationX(value - mView.mLeft);
                break;
            case Y:
                renderNode.setTranslationY(value - mView.mTop);
                break;
            case Z:
                renderNode.setTranslationZ(value - renderNode.getElevation());
                break;
            case ALPHA:
                info.mAlpha = value;
                renderNode.setAlpha(value);
                break;
        }
    }

从源码可以看出实际上都会把属性值的改变设置到 renderNode 对象中，而 RenderNode 类则是一个可以优化绘制流程和绘制动画的类，
该类可以提升优化绘制的性能，其内部操作最终会去调用到 Native 层方法，这里我们就不深追了。  
最后这里我们再回忆一下前面给出的整体流程说明：

1. 通过imageView.animate()获取ViewPropertyAnimator对象。
2. 调用alpha、translationX等方法，返回当前ViewPropertyAnimator对象，可以继续链式调用
3. alpha、translationX等方法内部最终调用animatePropertyBy(int constantName, float startValue, float byValue)方法
4. 在animatePropertyBy方法中则会将alpha、translationX等方法的操作封装成NameVauleHolder，并将每个NameValueHolder对象添加到准备列表mPendingAnimations中。
5. animatePropertyBy方法启动mAnimationStarter，调用startAnimation，开始动画。
6. startAnimation方法中会创建一个ValueAnimator对象设置内部监听器AnimatorEventListener，并将mPendingAnimations和要进行动画的属性名称封装成一个PropertyBundle对象，最后mAnimatorMap保存当前Animator和对应的PropertyBundle对象。该Map将会在animatePropertyBy方法和Animator监听器mAnimatorEventListener中使用，启动动画。
7. 在动画的监听器的onAnimationUpdate方法中设置所有属性的变化值，并通过RenderNode类优化绘制性能，最后刷新界面。

现在应该比较清晰了吧，以上就是 ViewPropertyAnimator 内部的大概执行流程。好~，ViewPropertyAnimator 介绍到这。

附录：  
本文摘录自：[《属性动画-Property Animation之ViewPropertyAnimator 你应该知道的一切》](https://blog.csdn.net/javazejian/article/details/52381558)  