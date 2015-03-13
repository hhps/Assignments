package ua.pp.condor.ioc.service;

import ua.pp.condor.ioc.entity.AccountEntity;

import java.util.List;

public interface IAccountService {

    AccountEntity save(AccountEntity account);

    AccountEntity findById(int accountId);

    List<AccountEntity> findAll();

    boolean delete(int accountId);

    AccountEntity updateFullName(int accountId, String newFullName);

    AccountEntity income(int accountId, double amount);

    AccountEntity loss(int accountId, double amount);
}
