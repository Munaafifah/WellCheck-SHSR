package com.SmartHealthRemoteSystem.SHSR.Repository;

// import com.SmartHealthRemoteSystem.SHSR.SendDailyHealth.HealthStatus;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface SubCollectionSHSRDAO<T> {
    T get(String documentId,String subCollectionDocumentId) throws ExecutionException, InterruptedException;
    List<T> getAll(String documentId) throws ExecutionException, InterruptedException;
    // List<T> getAll(String documentId, String keyword) throws ExecutionException, InterruptedException;
    // List<T> search(String documentId) throws ExecutionException, InterruptedException;
    String save(T t,String documentId ) throws ExecutionException, InterruptedException;
    String update(T t, String documentId) throws ExecutionException, InterruptedException;
    String delete(String documentId,String subCollectionDocumentId) throws ExecutionException, InterruptedException;
}
