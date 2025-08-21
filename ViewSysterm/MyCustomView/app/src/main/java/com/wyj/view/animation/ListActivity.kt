package com.wyj.view.animation

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.wyj.view.R
import com.wyj.view.animation.like.adapter.ListAdapter
import com.wyj.view.animation.like.factory.BitmapProviderFactory
import com.wyj.view.animation.like.interfaces.OnLikeAnimationListener
import com.wyj.view.animation.like.model.ListBean
import com.wyj.view.databinding.ActivityListBinding

class ListActivity : AppCompatActivity(), OnLikeAnimationListener {
    companion object {
        fun launch(context: Context) {
            val intent = Intent(context, ListActivity::class.java)
            context.startActivity(intent)
        }
        const val TAG = "ListActivity"
    }

    lateinit var dataBinding: ActivityListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = ActivityListBinding.inflate(LayoutInflater.from(this))
        setContentView(dataBinding.root)

        dataBinding.likeAnimationLayout.provider = BitmapProviderFactory.getProvider(this)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = RecyclerView.VERTICAL
        dataBinding.recyclerView.layoutManager = layoutManager

        val list: MutableList<ListBean> = mutableListOf()
        list.add(
            ListBean(
                "这是一段文本，为了测量更多的显示情况，明月几时有？把酒问青天。不知天上宫阙，今夕是何年。我欲乘风归去，又恐琼楼玉宇，高处不胜寒。起舞弄清影，何似在人间。转朱阁，低绮户，照无眠。不应有恨，何事长向别时圆？人有悲欢离合，月有阴晴圆缺，此事古难全。但愿人长久，千里共婵娟。",
                false,
                1
            )
        )
        list.add(
            ListBean(
                "明月几时有？<a href=\"https://www.qq.com\">我是链接，把酒问青天。</a>不知天上宫阙，<a href=\"https://www.baidu.com\">我也是链接，今夕是何年。</a>我欲乘风归去，又恐琼楼玉宇，高处不胜寒。起舞弄清影，何似在人间。转朱阁，低绮户，照无眠。不应有恨，何事长向别时圆？人有悲欢离合，月有阴晴圆缺，此事古难全。但愿人长久，千里共婵娟。",
                false,
                2
            )
        )
        list.add(ListBean("明月几时有？把酒问青天。不知天上宫阙，今夕是何年。", true, 1))
        list.add(
            ListBean(
                "我欲乘风归去，又恐琼楼玉宇，高处不胜寒。起舞弄清影，何似在人间。转朱阁，低绮户，照无眠。不应有恨，何事长向别时圆？人有悲欢离合，月有阴晴圆缺，此事古难全。但愿人长久，千里共婵娟。",
                false,
                3
            )
        )
        list.add(ListBean("我欲乘风归去，又恐琼楼玉宇，高处不胜寒。起舞弄清影，何似在人间。转朱阁，低绮户，照无眠。不应有恨，何事长向别时圆？人有悲欢离合，月有阴晴圆缺，此事古难全。但愿人长久，千里共婵娟。",true,1))
        list.add(ListBean("我欲乘风归去，又恐琼楼玉宇，高处不胜寒。起舞弄清影，何似在人间。转朱阁，低绮户，照无眠。不应有恨，何事长向别时圆？人有悲欢离合，月有阴晴圆缺，此事古难全。但愿人长久，千里共婵娟。",false,5))
        list.add(ListBean("我欲乘风归去，又恐琼楼玉宇，高处不胜寒。起舞弄清影，何似在人间。转朱阁，低绮户，照无眠。不应有恨，何事长向别时圆？人有悲欢离合，月有阴晴圆缺，此事古难全。但愿人长久，千里共婵娟。",false,6))
        list.add(ListBean("我欲乘风归去，又恐琼楼玉宇，高处不胜寒。起舞弄清影，何似在人间。转朱阁，低绮户，照无眠。不应有恨，何事长向别时圆？人有悲欢离合，月有阴晴圆缺，此事古难全。但愿人长久，千里共婵娟。",false,6))
        list.add(ListBean("我欲乘风归去，又恐琼楼玉宇，高处不胜寒。起舞弄清影，何似在人间。转朱阁，低绮户，照无眠。不应有恨，何事长向别时圆？人有悲欢离合，月有阴晴圆缺，此事古难全。但愿人长久，千里共婵娟。",false,6))
        list.add(ListBean("我欲乘风归去，又恐琼楼玉宇，高处不胜寒。起舞弄清影，何似在人间。转朱阁，低绮户，照无眠。不应有恨，何事长向别时圆？人有悲欢离合，月有阴晴圆缺，此事古难全。但愿人长久，千里共婵娟。",false,6))
        list.add(ListBean("我欲乘风归去，又恐琼楼玉宇，高处不胜寒。起舞弄清影，何似在人间。转朱阁，低绮户，照无眠。不应有恨，何事长向别时圆？人有悲欢离合，月有阴晴圆缺，此事古难全。但愿人长久，千里共婵娟。",false,6))
        list.add(ListBean("我欲乘风归去，又恐琼楼玉宇，高处不胜寒。起舞弄清影，何似在人间。转朱阁，低绮户，照无眠。不应有恨，何事长向别时圆？人有悲欢离合，月有阴晴圆缺，此事古难全。但愿人长久，千里共婵娟。",false,6))
        list.add(ListBean("我欲乘风归去，又恐琼楼玉宇，高处不胜寒。起舞弄清影，何似在人间。转朱阁，低绮户，照无眠。不应有恨，何事长向别时圆？人有悲欢离合，月有阴晴圆缺，此事古难全。但愿人长久，千里共婵娟。",false,6))
        list.add(ListBean("我欲乘风归去，又恐琼楼玉宇，高处不胜寒。起舞弄清影，何似在人间。转朱阁，低绮户，照无眠。不应有恨，何事长向别时圆？人有悲欢离合，月有阴晴圆缺，此事古难全。但愿人长久，千里共婵娟。",false,6))
        list.add(ListBean("我欲乘风归去，又恐琼楼玉宇，高处不胜寒。起舞弄清影，何似在人间。转朱阁，低绮户，照无眠。不应有恨，何事长向别时圆？人有悲欢离合，月有阴晴圆缺，此事古难全。但愿人长久，千里共婵娟。",false,6))
        list.add(ListBean("我欲乘风归去，又恐琼楼玉宇，高处不胜寒。起舞弄清影，何似在人间。转朱阁，低绮户，照无眠。不应有恨，何事长向别时圆？人有悲欢离合，月有阴晴圆缺，此事古难全。但愿人长久，千里共婵娟。",false,6))
        list.add(ListBean("我欲乘风归去，又恐琼楼玉宇，高处不胜寒。起舞弄清影，何似在人间。转朱阁，低绮户，照无眠。不应有恨，何事长向别时圆？人有悲欢离合，月有阴晴圆缺，此事古难全。但愿人长久，千里共婵娟。",false,6))
        list.add(ListBean("我欲乘风归去，又恐琼楼玉宇，高处不胜寒。起舞弄清影，何似在人间。转朱阁，低绮户，照无眠。不应有恨，何事长向别时圆？人有悲欢离合，月有阴晴圆缺，此事古难全。但愿人长久，千里共婵娟。",false,6))
        list.add(ListBean("我欲乘风归去，又恐琼楼玉宇，高处不胜寒。起舞弄清影，何似在人间。转朱阁，低绮户，照无眠。不应有恨，何事长向别时圆？人有悲欢离合，月有阴晴圆缺，此事古难全。但愿人长久，千里共婵娟。",false,6))
        list.add(ListBean("我欲乘风归去，又恐琼楼玉宇，高处不胜寒。起舞弄清影，何似在人间。转朱阁，低绮户，照无眠。不应有恨，何事长向别时圆？人有悲欢离合，月有阴晴圆缺，此事古难全。但愿人长久，千里共婵娟。",false,6))
        list.add(ListBean("我欲乘风归去，又恐琼楼玉宇，高处不胜寒。起舞弄清影，何似在人间。转朱阁，低绮户，照无眠。不应有恨，何事长向别时圆？人有悲欢离合，月有阴晴圆缺，此事古难全。但愿人长久，千里共婵娟。",false,6))
        list.add(ListBean("我欲乘风归去，又恐琼楼玉宇，高处不胜寒。起舞弄清影，何似在人间。转朱阁，低绮户，照无眠。不应有恨，何事长向别时圆？人有悲欢离合，月有阴晴圆缺，此事古难全。但愿人长久，千里共婵娟。",false,6))
        list.add(ListBean("我欲乘风归去，又恐琼楼玉宇，高处不胜寒。起舞弄清影，何似在人间。转朱阁，低绮户，照无眠。不应有恨，何事长向别时圆？人有悲欢离合，月有阴晴圆缺，此事古难全。但愿人长久，千里共婵娟。",false,6))

        list.add(ListBean("我欲乘风归去，又恐琼楼玉宇，高处不胜寒。起舞弄清影，何似在人间。转朱阁，低绮户，照无眠。",false,6))
        list.add(ListBean("我欲乘风归去，又恐琼楼玉宇，高处不胜寒。起舞弄清影，何似在人间。转朱阁，低绮户，照无眠。",false,7))
        list.add(ListBean("我欲乘风归去，又恐琼楼玉宇，高处不胜寒。起舞弄清影，何似在人间。转朱阁，低绮户，照无眠。",false,8))
        list.add(ListBean("我欲乘风归去，又恐琼楼玉宇，高处不胜寒。起舞弄清影，何似在人间。转朱阁，低绮户，照无眠。",false,9))
        list.add(ListBean("我欲乘风归去，又恐琼楼玉宇，高处不胜寒。起舞弄清影，何似在人间。转朱阁，低绮户，照无眠。",false,10))
        list.add(ListBean("我欲乘风归去，又恐琼楼玉宇，高处不胜寒。起舞弄清影，何似在人间。转朱阁，低绮户，照无眠。",false,11))

        val listAdapter = ListAdapter(this, lifecycleScope, this)
        dataBinding.recyclerView.adapter = listAdapter
        listAdapter.setRecyclerList(list)
    }

    override fun doLikeAnimation(v: View) {
        val itemPosition = IntArray(2)
        val superLikePosition = IntArray(2)
        v.getLocationOnScreen(itemPosition)
        dataBinding.likeAnimationLayout.getLocationOnScreen(superLikePosition)
        val emojiBitmap = BitmapFactory.decodeResource(resources, R.mipmap.emoji_1)
        val width = emojiBitmap.width
        val height = emojiBitmap.height
        Log.d(TAG, "launch: wyj emoji size width: $width, height:$height")
        val x = itemPosition[0] + v.width / 2 - emojiBitmap.width / 2
        val y = itemPosition[1] - superLikePosition[1] + v.height / 2 - emojiBitmap.height / 2
        Log.d(TAG, "doLikeAnimation: wyj itemPosition[0]:${itemPosition[0]}, " +
                "itemPosition[1]:${itemPosition[1]}, superLikePosition[0]:${superLikePosition[0]}" +
                ", superLikePosition[1]:${superLikePosition[1]}")
        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels
        Log.d(TAG, "doLikeAnimation: wyj screenWidth:$screenWidth, screenHeight:$screenHeight")
        dataBinding.likeAnimationLayout.launch(x, y)
    }

}