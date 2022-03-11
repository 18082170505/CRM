package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.workbench.dao.*;
import com.bjpowernode.crm.workbench.domain.*;
import com.bjpowernode.crm.workbench.service.ClueService;

import java.util.ArrayList;
import java.util.List;

public class ClueServiceImpl implements ClueService {
    //线索相关表
    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueActivityRelationDao clueActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);
    private ClueRemarkDao clueRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ClueRemarkDao.class);
    //客户相关表
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private CustomerRemarkDao customerRemarkDao = SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);
    //联系人相关表
    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private ContactsRemarkDao contactsRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkDao.class);
    private ContactsActivityRelationDao contactsActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationDao.class);
    //交易相关表
    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);

    @Override
    public boolean save(Clue c) {
        boolean flag = true;
        int count = clueDao.save(c);
        if (count != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public Clue detail(String id) {
        Clue c = clueDao.detail(id);
        return c;
    }

    @Override
    public boolean unbund(String id) {
        boolean flag = true;
        int count = clueActivityRelationDao.unbund(id);
        if (count != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean bund(String cId, String[] aIds) {
        boolean flag = true;
        for (String aId:aIds){
            ClueActivityRelation car = new ClueActivityRelation();
            String id = UUIDUtil.getUUID();
            car.setId(id);
            car.setClueId(cId);
            car.setActivityId(aId);

            int count = clueActivityRelationDao.bund(car);
            if (count != 1){
                flag = false;
            }
        }
        return flag;
    }

    @Override
    public boolean convert(String clueId, Tran t, String createBy) {
        String createTime = DateTimeUtil.getSysTime();
        boolean flag = true;
        //(1)根据线索id获取线索对象（线索对象当中封装了线索信息）
        Clue c = clueDao.getById(clueId);
        //(2)通过线索对象提取客户信息，当客户不存在的时候，新建客户（根据公司的名称精确匹配，判断该客户是否存在）
        String company = c.getCompany();
        Customer customer = customerDao.getCustomerByName(company);
        //如果customer为空，说明之前没有这个客户，需要新建一个
        if (customer==null){
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setAddress(c.getAddress());
            customer.setCreateBy(createBy);
            customer.setCreateTime(createTime);
            customer.setWebsite(c.getWebsite());
            customer.setPhone(c.getPhone());
            customer.setOwner(c.getOwner());
            customer.setNextContactTime(c.getNextContactTime());
            customer.setName(company);
            customer.setDescription(c.getDescription());
            customer.setContactSummary(c.getContactSummary());
            //添加用户
            int count = customerDao.save(customer);
            if (count != 1){
                flag = false;
            }

        }
        //(3)通过线索对象获取联系人信息，保存联系人
        Contacts con = new Contacts();
        con.setId(UUIDUtil.getUUID());
        con.setSource(c.getSource());
        con.setOwner(c.getOwner());
        con.setNextContactTime(c.getNextContactTime());
        con.setMphone(c.getMphone());
        con.setJob(c.getJob());
        con.setFullname(c.getFullname());
        con.setEmail(c.getEmail());
        con.setDescription(c.getDescription());
        con.setCustomerId(customer.getId());
        con.setCreateTime(createTime);
        con.setCreateBy(createBy);
        con.setContactSummary(c.getContactSummary());
        con.setAppellation(c.getAppellation());
        con.setAddress(c.getAddress());
        //添加联系人
        int count1 = contactsDao.save(con);
        if (count1 != 1){
            flag = false;
        }

        //(4)将线索备注转换到客户备注以及联系人备注
        //查询出与该线索关联的备注信息列表
        List<ClueRemark> clueRemarkList = clueRemarkDao.getListByClueId(clueId);
        for (ClueRemark clueRemark:clueRemarkList){
            //取出备注信息
            String noteContent = clueRemark.getNoteContent();
            //创建客户备注对象，添加客户备注
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setNoteContent(noteContent);
            customerRemark.setCreateBy(createBy);
            customerRemark.setCreateTime(createTime);
            customerRemark.setCustomerId(customer.getId());
            customerRemark.setEditFlag("0");
            int count2 = customerRemarkDao.save(customerRemark);
            if (count2 != 1){
                flag = false;
            }
            //创建联系人备注对象，添加联系人备注
            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setNoteContent(noteContent);
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setCreateTime(createTime);
            contactsRemark.setContactsId(con.getId());
            contactsRemark.setEditFlag("0");
            int count3 = contactsRemarkDao.save(contactsRemark);
            if (count3 != 1){
                flag = false;
            }
        }

        //(5) 将"线索和市场活动"的关联关系转换到"联系人和市场活动"的关联关系
        //查询出与该条线索关联的所以市场活动，查询与市场活动的关联关系表
        List<ClueActivityRelation> clueActivityRelationList = clueActivityRelationDao.getListByClueId(clueId);
        for (ClueActivityRelation clueActivityRelation:clueActivityRelationList){
            //获取线索关联的市场活动的id
            String activityId = clueActivityRelation.getActivityId();
            //创建联系人和市场活动的关联关系
            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            contactsActivityRelation.setActivityId(activityId);
            contactsActivityRelation.setContactsId(con.getId());
            int count4 = contactsActivityRelationDao.save(contactsActivityRelation);
            if (count4 != 1){
                flag = false;
            }

        }

        //(6)如果有创建交易需求，则创建一条交易
        if(t != null){
            /*
               t对象在controller里有已经封装好的信息如下：
                 id,money,name,expectedDate,stage,activityId,createBy,createTime

                 接下来可以通过第一步生成的c对象，取出一些信息，继续完善对t对象的封装
             */
            t.setSource(c.getSource());
            t.setOwner(c.getOwner());
            t.setNextContactTime(c.getNextContactTime());
            t.setDescription(c.getDescription());
            t.setCustomerId(customer.getId());
            t.setContactSummary(c.getContactSummary());
            t.setContactsId(con.getId());
            //添加交易
            int count5 = tranDao.save(t);
            if (count5 != 1){
                flag = false;
            }

            //(7)如果创建了交易，则创建一条该交易下的交易历史
            TranHistory th = new TranHistory();
            th.setId(UUIDUtil.getUUID());
            th.setCreateBy(createBy);
            th.setCreateTime(createTime);
            th.setExpectedDate(t.getExpectedDate());
            th.setMoney(t.getMoney());
            th.setStage(t.getStage());
            th.setTranId(t.getId());
            //添加交易历史
            int count6 = tranHistoryDao.save(th);
            if (count6 != 1){
                flag = false;
            }

        }

        //(8)删除线索备注
        for (ClueRemark clueRemark:clueRemarkList){
            int count7 = clueRemarkDao.delete(clueRemark);
            if (count7 != 1){
                flag = false;
            }
        }
        //(9)删除线索和市场活动的关联关系
        for (ClueActivityRelation clueActivityRelation:clueActivityRelationList){
            int count8 = clueActivityRelationDao.delete(clueActivityRelation);
            if (count8 != 1){
                flag = false;
            }
        }

        //(10)删除线索
        int count9 = clueDao.delete(clueId);
        if (count9 != 1){
            flag = false;
        }

        return flag;
    }

}
