package org.dows.hep.biz.base.question;

import lombok.RequiredArgsConstructor;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class QuestionBaseBiz {

    private final IdGenerator idGenerator;

    protected String getAppId() {
        return "3";
    }

    protected Integer getSequence() {
        return 0;
    }

    protected String getIdStr() {
        return idGenerator.nextIdStr();
    }

    protected String getVer() {
        return String.valueOf(new Date().getTime());
    }
}
