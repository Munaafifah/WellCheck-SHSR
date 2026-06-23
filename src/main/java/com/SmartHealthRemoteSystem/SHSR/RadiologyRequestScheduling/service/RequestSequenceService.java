package com.SmartHealthRemoteSystem.SHSR.RadiologyRequestScheduling.service;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

/**
 * UCR008 — Generates sequential imaging request IDs in the format REQ000001.
 * Uses an atomic findAndModify on a MongoDB counter document so IDs are
 * unique even under concurrent submissions.
 */
@Service
public class RequestSequenceService {

    private static final String COUNTER_COLLECTION = "requestSequence";
    private static final String COUNTER_KEY        = "imagingRequestSeq";

    private final MongoTemplate mongoTemplate;

    @Autowired
    public RequestSequenceService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public String nextRequestId() {
        Query  query   = new Query(Criteria.where("_id").is(COUNTER_KEY));
        Update update  = new Update().inc("seq", 1);
        FindAndModifyOptions opts = FindAndModifyOptions.options().returnNew(true).upsert(true);

        Document counter = mongoTemplate.findAndModify(query, update, opts, Document.class, COUNTER_COLLECTION);
        long seq = ((Number) counter.get("seq")).longValue();
        return String.format("REQ%06d", seq);
    }
}
