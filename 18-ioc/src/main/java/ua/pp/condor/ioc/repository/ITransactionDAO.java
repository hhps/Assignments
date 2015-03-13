package ua.pp.condor.ioc.repository;

import ua.pp.condor.ioc.entity.TransactionEntity;

import java.util.List;

public interface ITransactionDAO {

    TransactionEntity save(TransactionEntity transaction);

    TransactionEntity findById(long transactionId, boolean lock);

    List<TransactionEntity> findAll();

    boolean delete(long transactionId);
}
