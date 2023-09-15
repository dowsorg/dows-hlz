package org.dows.hep.biz.edw;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.edw.repository.HepOperateCostSetRepository;
import org.dows.edw.repository.HepOperateGetRepository;
import org.dows.edw.repository.HepOperateSetRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhysicalExamReportHandler {
    private final HepOperateSetRepository hepOperateSetRepository;
    private final HepOperateGetRepository hepOperateGetRepository;
    private final HepOperateCostSetRepository hepOperateCostSetRepository;


    public String writeBiz(){
        return null;
    }


    public String readBiz(){
        return null;
    }

}
