package org.dows.hep.biz.base.question;

import lombok.RequiredArgsConstructor;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class BaseBiz {

    private final IdGenerator idGenerator;

    public String getAppId() {
        return "3";
    }

    public Integer getSequence() {
        return 0;
    }

    public String getIdStr() {
        return idGenerator.nextIdStr();
    }

    public String getVer() {
        return String.valueOf(new Date().getTime());
    }
}
