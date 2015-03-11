package ua.pp.condor.ioc.repository;

import ua.pp.condor.ioc.entity.AccountEntity;

import java.util.List;

public interface IAccountDAO {

    AccountEntity save(AccountEntity account);

    AccountEntity findById(int accountId, boolean lock);

    List<AccountEntity> findAll();

    boolean delete(int accountId);
}
