package com.wyj.view.timeline;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.wyj.view.R;

public class MainActivity extends AppCompatActivity {
    private RecyclerView Rv;
    private LogisticsAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
    }

    // 初始化显示的数据
    public void initData() {
        List<LogisticsInfo> logisticsInfos = new ArrayList<>();
        LogisticsInfo logisticsInfo = new LogisticsInfo();
        logisticsInfo.setState("已签收");
        logisticsInfo.setDateStr("06-12 11:07");
        logisticsInfo.setDesc("包裹已签收！包裹已签收！（凭取件码）如有问题请电联：代收点15827065070/何林峰 17366929255，" +
                "投诉电话：027-87443612 在线求五星 点亮兔兔小行星，只为服务您满意~");
        logisticsInfos.add(logisticsInfo);

        logisticsInfo = new LogisticsInfo();
        logisticsInfo.setState("代取件");
        logisticsInfo.setDateStr("06-11 12:18");
        logisticsInfo.setDesc("您的包裹已放置【代收点】，记得早点来【花城南路42附51号申通快递】取它回家！如有问题请联系：代收点" +
                " 15827065070/何林峰 17366929255，投书电话：027-87443612");
        logisticsInfos.add(logisticsInfo);

        logisticsInfo = new LogisticsInfo();
        logisticsInfo.setState("派送中");
        logisticsInfo.setDateStr("06-11 08:03");
        logisticsInfo.setDesc("【武汉光谷生物城网点】的兔兔快递员 何林峰 正在派件（可放心接听952300专属热线），" +
                "投诉电话：027-87443612。今天的兔兔，怀揣包裹，卯足干劲，为您“加吉”派送中");
        logisticsInfos.add(logisticsInfo);

        logisticsInfo = new LogisticsInfo();
        logisticsInfo.setState("运送中");
        logisticsInfo.setDateStr("06-11 08:03");
        logisticsInfo.setDesc("【快件到达【武汉光谷生物城网点】");
        logisticsInfos.add(logisticsInfo);

        logisticsInfo = new LogisticsInfo();
        logisticsInfo.setDateStr("06-11 03:29");
        logisticsInfo.setDesc("快件离开【武汉转运中心B1】已发往【武汉光谷生物城网点】");
        logisticsInfos.add(logisticsInfo);

        logisticsInfo = new LogisticsInfo();
        logisticsInfo.setDateStr("06-11 03:03");
        logisticsInfo.setDesc("快件到达【武汉转运中心B1】");
        logisticsInfos.add(logisticsInfo);

        logisticsInfo = new LogisticsInfo();
        logisticsInfo.setDateStr("06-10 02:46");
        logisticsInfo.setDesc("快件离开【揭阳转运中心】已发往【武汉转运中心B1】");
        logisticsInfos.add(logisticsInfo);

        logisticsInfo = new LogisticsInfo();
        logisticsInfo.setDateStr("06-10 00:58");
        logisticsInfo.setDesc("快件离开【潮安集散点】已发往【揭阳转运中心】");
        logisticsInfos.add(logisticsInfo);

        logisticsInfo = new LogisticsInfo();
        logisticsInfo.setDateStr("06-09 20:21");
        logisticsInfo.setDesc("快件离开【潮州彩塘网点】已发往【潮安集散点】");
        logisticsInfos.add(logisticsInfo);

        logisticsInfo = new LogisticsInfo();
        logisticsInfo.setState("已揽件");
        logisticsInfo.setDateStr("06-09 20:11");
        logisticsInfo.setDesc("快件离开【潮州彩塘网点】已发往【潮安集散点】");
        logisticsInfos.add(logisticsInfo);

        logisticsInfo = new LogisticsInfo();
        logisticsInfo.setState("已发货");
        logisticsInfo.setDateStr("06-09 17:37");
        logisticsInfo.setDesc("包裹正在等待揽收");
        logisticsInfos.add(logisticsInfo);

        logisticsInfo = new LogisticsInfo();
        logisticsInfo.setState("已出库");
        logisticsInfo.setDateStr("06-09 17:37");
        logisticsInfo.setDesc("您的包裹已出库");
        logisticsInfos.add(logisticsInfo);

        logisticsInfo = new LogisticsInfo();
        logisticsInfo.setState("已下单");
        logisticsInfo.setDateStr("06-09 17:37");
        logisticsInfo.setDesc("您的包裹已打包");
        logisticsInfos.add(logisticsInfo);

        logisticsInfo = new LogisticsInfo();
        logisticsInfo.setDateStr("06-09 17:36");
        logisticsInfo.setDesc("您的订单已打物流单");
        logisticsInfos.add(logisticsInfo);

        logisticsInfo = new LogisticsInfo();
        logisticsInfo.setDateStr("06-09 17:27");
        logisticsInfo.setDesc("商品已经下单");
        logisticsInfos.add(logisticsInfo);

        Rv = (RecyclerView) findViewById(R.id.my_recycler_view);
        //使用线性布局
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        Rv.setLayoutManager(layoutManager);
        Rv.setHasFixedSize(true);

        //用自定义分割线类设置分割线
        LogisticsItemDecoration itemDecoration = new LogisticsItemDecoration.Builder()
                .setDot(12, Color.parseColor("#FF555555"), 8)
                .setLine(2, 40, Color.parseColor("#44222222"))
                .setOffsets(58, 50)
                .build();
        Rv.addItemDecoration(itemDecoration);

        myAdapter = new LogisticsAdapter(this, logisticsInfos);
        Rv.setAdapter(myAdapter);
    }

}
