package ua.pp.condor.ioc.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.pp.condor.ioc.entity.AccountEntity;
import ua.pp.condor.ioc.repository.IAccountDAO;
import ua.pp.condor.ioc.service.IAccountService;
import ua.pp.condor.ioc.service.exception.IllegalEntityStateException;
import ua.pp.condor.ioc.service.exception.NoSuchEntityException;
import ua.pp.condor.ioc.service.exception.NotEnoughMoneyException;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Service
@Transactional
public class AccountService implements IAccountService {

    @Inject
    private IAccountDAO accountDAO;

    @Override
    public AccountEntity save(AccountEntity account) {
        requireNonNull(account, "account");
        if (account.getId() != null) {
            throw new IllegalEntityStateException("can not save entity which has already generated ID: " + account);
        }
        if (StringUtils.isBlank(account.getFullName())) {
            throw new IllegalEntityStateException("blank full name");
        }
        if (account.getBalance() < 0) {
            throw new IllegalEntityStateException("negative balance of new account");
        }
        account.setCreationTime(new Date());
        return accountDAO.save(account);
    }

    @Override
    public AccountEntity findById(int accountId) {
        return accountDAO.findById(accountId, false);
    }

    @Override
    public List<AccountEntity> findAll() {
        return accountDAO.findAll();
    }

    @Override
    public boolean delete(int accountId) {
        return accountDAO.delete(accountId);
    }

    @Override
    public AccountEntity updateFullName(int accountId, String newFullName) {
        if (StringUtils.isBlank(newFullName)) {
            throw new IllegalArgumentException("newFullName");
        }
        AccountEntity account = accountDAO.findById(accountId, true);
        if (account == null) {
            throw new NoSuchEntityException(accountId);
        }
        account.setFullName(newFullName);
        return accountDAO.save(account);
    }

    @Override
    public AccountEntity income(int accountId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount");
        }
        AccountEntity account = new AccountEntity("Joe Shmoe"); //FIXME remove
        if (accountId < 0) {    //FIXME incorrect check
            throw new NoSuchEntityException(accountId);
        }
        account.setBalance(amount);
        return account;
    }

    @Override
    public AccountEntity loss(int accountId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount");
        }
        AccountEntity account = new AccountEntity("Joe Shmoe", 500); //FIXME remove
        if (accountId < 0) {    //FIXME incorrect check
            throw new NoSuchEntityException(accountId);
        }
        if (account.getBalance() < amount) {
            throw new NotEnoughMoneyException(account.toString());
        }
        //TODO Auto-generated method stub
        account.setBalance(account.getBalance() - amount);
        return account;
    }
}
