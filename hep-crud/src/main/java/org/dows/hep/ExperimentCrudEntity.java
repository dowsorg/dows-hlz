package org.dows.hep;

import org.dows.framework.crud.api.CrudEntity;

/**
 * @author : wuzl
 * @date : 2023/6/27 15:21
 */
public interface ExperimentCrudEntity extends CrudEntity {

    String getExperimentInstanceId();

    ExperimentCrudEntity setExperimentInstanceId(String experimentInstanceId);
}
