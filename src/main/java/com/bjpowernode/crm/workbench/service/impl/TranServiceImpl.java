package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.workbench.dao.CustomerDao;
import com.bjpowernode.crm.workbench.dao.TranDao;
import com.bjpowernode.crm.workbench.dao.TranHistoryDao;
import com.bjpowernode.crm.workbench.domain.Customer;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.service.TranService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranServiceImpl implements TranService {
    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    @Override
    public boolean save(Tran t, String customerName) {
        /*

           交易添加业务：
             在做添加之前，参数t中少了一项信息，就说客户的主键customerId

             先处理客户相关的需求
             (1) 判断customerName,根据客户名称在客户表进行精确查询
                  如果有这个客户，则取出这个客户的id，封装到t对象中
                  如果没有这个客户，则在客户表新建一条客户信息，然后将新建的客户的id取出，封装到t对象中
             (2) 经过以上操作后，t对象中的信息就全了，需要执行添加交易的操作
             (3) 添加交易完成后，需要创建一条交易历史
         */
        boolean flag = true;
        Customer cus = customerDao.getCustomerByName(customerName);
        if (cus == null){
            cus = new Customer();
            cus.setId(UUIDUtil.getUUID());
            cus.setName(customerName);
            cus.setOwner(t.getOwner());
            cus.setCreateBy(t.getCreateBy());
            cus.setCreateTime(DateTimeUtil.getSysTime());
            cus.setNextContactTime(t.getNextContactTime());
            cus.setDescription(t.getDescription());
            cus.setContactSummary(t.getContactSummary());
            //添加客户
            int count1 = customerDao.save(cus);
            if (count1 != 1){
                flag = false;
            }
        }

        //将customerId封装到t对象中
        t.setCustomerId(cus.getId());
        //添加交易
        int count2 = tranDao.save(t);
        if (count2 != 1){
            flag = false;
        }

        //添加交易历史
        TranHistory th = new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setTranId(t.getId());
        th.setStage(t.getStage());
        th.setMoney(t.getMoney());
        th.setExpectedDate(t.getExpectedDate());
        th.setCreateTime(DateTimeUtil.getSysTime());
        th.setCreateBy(t.getCreateBy());
        int count3 = tranHistoryDao.save(th);
        if (count3 != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public Tran detail(String id) {
        Tran tran = tranDao.detail(id);
        return tran;
    }

    @Override
    public List<TranHistory> getTranHistoryListByTranId(String tranId) {
        List<TranHistory> thList = tranHistoryDao.getTranHistoryListByTranId(tranId);
        return thList;
    }

    @Override
    public boolean changeStage(Tran t) {
        boolean flag = true;
        //根据tranId更新stage
        int count1 = tranDao.updateStage(t);
        if (count1 != 1){
            flag = false;
        }
        //新增一条交易历史
        TranHistory th = new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setMoney(t.getMoney());
        th.setExpectedDate(t.getExpectedDate());
        th.setCreateBy(t.getEditBy());
        th.setCreateTime(DateTimeUtil.getSysTime());
        th.setTranId(t.getId());
        th.setStage(t.getStage());
        th.setPossibility(t.getPossibility());
        int count2 = tranHistoryDao.save(th);
        if (count2 != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public Map<String, Object> getCharts() {
        //获取total
        int total = tranDao.getTotal();
        //获取dataList
        List<Map<String,Object>> dataList = tranDao.getCharts();

        //将total和dataList封装到map中
        Map<String,Object> map = new HashMap<>();
        map.put("total",total);
        map.put("dataList",dataList);
        return map;
    }
}
