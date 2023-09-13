package org.dows.edw.repository;

import org.dows.edw.domain.HepFollowUp;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HepFollowUpRespository extends MongoRepository<HepFollowUp,String> {
}
