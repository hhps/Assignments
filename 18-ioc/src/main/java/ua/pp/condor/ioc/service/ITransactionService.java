package ua.pp.condor.ioc.service;

import ua.pp.condor.ioc.entity.TransactionEntity;

import java.util.List;

public interface ITransactionService {

    TransactionEntity save(TransactionEntity transaction);

    TransactionEntity findById(long transactionId);

    List<TransactionEntity> findAll();

    boolean delete(long transactionId);
}
