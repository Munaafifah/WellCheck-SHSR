package com.SmartHealthRemoteSystem.SHSR.Communication.repository;

import com.SmartHealthRemoteSystem.SHSR.Communication.model.ChatSession;
import com.SmartHealthRemoteSystem.SHSR.Communication.model.ChatStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends MongoRepository<ChatSession, String> {

    List<ChatSession> findByParticipantsContaining(String userId);

    List<ChatSession> findByStatus(ChatStatus status);

    Optional<ChatSession> findByChatId(String chatId);
}
