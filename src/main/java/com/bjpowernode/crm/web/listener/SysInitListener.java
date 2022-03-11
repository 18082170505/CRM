package com.bjpowernode.crm.web.listener;

import com.bjpowernode.crm.settings.domain.DicType;
import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.service.DicService;
import com.bjpowernode.crm.settings.service.impl.DicServiceImpl;
import com.bjpowernode.crm.utils.ServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

public class SysInitListener implements ServletContextListener {

    /*
      该方法是用来监听上下文域对象的方法，当服务器启动时，上下文域对象创建
      对象创建完毕后，马上执行该方法

      event:该参数能够取得监听的对象
           监听的是什么对象，就可以通过该参数取得什么对象
           例如我们现在监听的是上下文域对象，通过该参数就可以取得上下文域对象
     */
    public void contextInitialized(ServletContextEvent event) {
        System.out.println("服务器缓存处理数据字典开始");
        //获取上下文域对象
        ServletContext application = event.getServletContext();

        DicService ds = (DicService) ServiceFactory.getService(new DicServiceImpl());
        //取得数据字典，一定要分门别类保存
        //按照typeCode进行分类，最后应该保存为7个List对象
        //业务层应该返回一个map,应该如下在业务层保存数据
        /*
        map.put("appellationList",dvList1)
        map.put("clueStateList",dvList1)
        map.put("stageList",dvList1)
        ....
         */
        Map<String, List<DicValue>> map = ds.getAll();
        //将map保存为上下文域对象中的键值对
        Set<String> set = map.keySet();
        for (String key:set){
            application.setAttribute(key,map.get(key));
        }

        System.out.println("服务器缓存处理数据字典结束");

        //-------------------------------------------------------------
        //处理数据字典结束后读取Stage2Possibility.properties属性文件
        /*
        处理步骤：
         解析该文件，将该属性文件中的键值对关系处理成java中的键值对关系(map)
         */
        Map<String,String> pMap = new HashMap<>();
        //解析Properties文件(注意不用加后缀名)
        ResourceBundle rb = ResourceBundle.getBundle("Stage2Possibility");
        //读取key值
        Enumeration<String> stages = rb.getKeys();
        while (stages.hasMoreElements()){
            String stage = stages.nextElement();
            String possibility = rb.getString(stage);
            pMap.put(stage,possibility);
        }
        //将pMap保存到服务器缓存中
        application.setAttribute("pMap",pMap);

    }
}
