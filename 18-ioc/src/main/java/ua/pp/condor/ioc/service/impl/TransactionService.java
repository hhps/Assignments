package ua.pp.condor.ioc.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.pp.condor.ioc.entity.TransactionEntity;
import ua.pp.condor.ioc.repository.ITransactionDAO;
import ua.pp.condor.ioc.service.IAccountService;
import ua.pp.condor.ioc.service.ITransactionService;
import ua.pp.condor.ioc.service.exception.IllegalEntityStateException;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Service
@Transactional
public class TransactionService implements ITransactionService {

    @Inject
    private ITransactionDAO transactionDAO;

    @Inject
    private IAccountService accountService;

    @Override
    public TransactionEntity save(TransactionEntity transaction) {
        requireNonNull(transaction, "transaction");
        if (transaction.getId() != null) {
            throw new IllegalEntityStateException("can not save entity which has already generated ID: " + transaction);
        }
        if (transaction.getAccountFrom() == null) {
            throw new IllegalEntityStateException("account_from can not be null");
        }
        if (transaction.getAccountTo() == null) {
            throw new IllegalEntityStateException("account_to can not be null");
        }
        if (Integer.compare(transaction.getAccountFrom(), transaction.getAccountTo()) == 0) {
            throw new IllegalEntityStateException("account_from can not be equals with account_to");
        }
        if (transaction.getAmount() == null) {
            throw new IllegalEntityStateException("amount can not be null");
        }
        if (Double.compare(transaction.getAmount(), 0) <= 0) {
            throw new IllegalEntityStateException("amount can not be less than 0 or equals to 0");
        }
        if (transaction.getCreationTime() != null) {
            throw new IllegalEntityStateException("creation_time cat not be setted");
        }

        final double amount = transaction.getAmount().doubleValue();
        accountService.loss(transaction.getAccountFrom(), amount);
        transaction.setCreationTime(new Date());
        transaction = transactionDAO.save(transaction);
        accountService.income(transaction.getAccountTo(), amount);
        return transaction;
    }

    @Transactional(readOnly = true)
    @Override
    public TransactionEntity findById(long transactionId) {
        return transactionDAO.findById(transactionId, false);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TransactionEntity> findAll() {
        return transactionDAO.findAll();
    }

    @Override
    public boolean delete(long transactionId) {
        return transactionDAO.delete(transactionId);
    }
}
