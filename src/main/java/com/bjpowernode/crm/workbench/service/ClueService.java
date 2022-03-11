package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.Tran;

import java.util.List;

public interface ClueService {
    boolean save(Clue c);

    Clue detail(String id);

    boolean unbund(String id);

    boolean bund(String cId, String[] aIds);

    boolean convert(String clueId, Tran t, String createBy);
}
