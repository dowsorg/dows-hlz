package org.dows.hep.biz.base.question;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service
public class QuestionDomainBaseBiz {

    private static final String LAST_VERSION = "SNAPSHOT";

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

    public String getLastVer() {
        return LAST_VERSION;
    }

    public String getVer(Date date) {
        return String.valueOf((date == null ? new Date() : date).getTime());
    }

    public String getQuestionInstancePid() {
        return "0";
    }

    public <S, T> Page<T> convertPage(Page<S> source, Class<T> target) {
        Page<T> result = BeanUtil.copyProperties(source, Page.class);

        List<S> records = source.getRecords();
        if (records == null || records.isEmpty()) {
            return new Page<>();
        }

        List<T> ts = new ArrayList<>();
        records.forEach(item -> {
            T t = BeanUtil.copyProperties(item, target);
            ts.add(t);
        });

        result.setRecords(ts);
        return result;
    }
}
