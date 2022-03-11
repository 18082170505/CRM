package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface TranDao {

    int save(Tran t);

    Tran detail(String id);

    int updateStage(Tran t);

    Tran getById(String id);

    int getTotal();

    List<Map<String, Object>> getCharts();
}