package ua.pp.condor.ioc.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.pp.condor.ioc.entity.AccountEntity;
import ua.pp.condor.ioc.repository.IAccountDAO;
import ua.pp.condor.ioc.service.IAccountService;
import ua.pp.condor.ioc.service.exception.IllegalEntityStateException;
import ua.pp.condor.ioc.service.exception.NoSuchEntityException;
import ua.pp.condor.ioc.service.exception.NotEnoughMoneyException;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import static java.util.Objects.requireNonNull;

@Service
@Transactional
public class AccountService implements IAccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    @Inject
    private IAccountDAO accountDAO;

    @Override
    public AccountEntity save(AccountEntity account) {
        requireNonNull(account, "account");
        log.debug("Before: {}", account);
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
        account = accountDAO.save(account);
        log.debug("After: {}", account);
        return account;
    }

    @Transactional(readOnly = true)
    @Override
    public AccountEntity findById(int accountId) {
        AccountEntity account = accountDAO.findById(accountId, false);
        log.debug("Found account for id = {}: {}", accountId, account);
        return account;
    }

    @Transactional(readOnly = true)
    @Override
    public List<AccountEntity> findAll() {
        return accountDAO.findAll();
    }

    @Override
    public boolean delete(int accountId) {
        boolean isDeleted = accountDAO.delete(accountId);
        log.debug("Deleted account for id = {}: {}", accountId, isDeleted);
        return isDeleted;
    }

    @Override
    public AccountEntity updateFullName(int accountId, String newFullName) {
        if (StringUtils.isBlank(newFullName)) {
            throw new IllegalArgumentException("blank new full name");
        }
        AccountEntity account = accountDAO.findById(accountId, true);
        if (account == null) {
            throw new NoSuchEntityException(accountId);
        }
        log.debug("Before change name to {}: {}", newFullName, account);
        account.setFullName(newFullName);
        account = accountDAO.save(account);
        log.debug("After change name to {}: {}", newFullName, account);
        return account;
    }

    @Override
    public AccountEntity income(int accountId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount have to be greater than 0");
        }
        AccountEntity account = accountDAO.findById(accountId, true);
        if (account == null) {
            throw new NoSuchEntityException(accountId);
        }
        log.debug("Before income {}: {}", amount, account);
        account.setBalance(account.getBalance() + amount);
        account = accountDAO.save(account);
        log.debug("After income {}: {}", amount, account);
        return account;
    }

    @Override
    public AccountEntity loss(int accountId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount have to be greater than 0");
        }
        AccountEntity account = accountDAO.findById(accountId, true);
        if (account == null) {
            throw new NoSuchEntityException(accountId);
        }
        if (account.getBalance() < amount) {
            throw new NotEnoughMoneyException(account + " does not have " + amount);
        }
        log.debug("Before loss {}: {}", amount, account);
        account.setBalance(account.getBalance() - amount);
        account = accountDAO.save(account);
        log.debug("After loss {}: {}", amount, account);
        return account;
    }
}
