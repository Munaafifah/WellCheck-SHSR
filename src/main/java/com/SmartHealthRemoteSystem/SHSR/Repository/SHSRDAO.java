package com.SmartHealthRemoteSystem.SHSR.Repository;


import java.util.List;
import java.util.concurrent.ExecutionException;

public interface SHSRDAO<T> {
    T get(String id) throws ExecutionException, InterruptedException;
    List<T> getAll() throws ExecutionException, InterruptedException;
    String save(T t) throws ExecutionException, InterruptedException;
    String update(T t) throws ExecutionException, InterruptedException;
    //String updatePat(T t) throws ExecutionException, InterruptedException;
    String delete(String id) throws ExecutionException, InterruptedException;
}
