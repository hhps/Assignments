package ua.pp.condor.ioc.repository.impl;

import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ua.pp.condor.ioc.entity.AccountEntity;
import ua.pp.condor.ioc.repository.IAccountDAO;

import javax.inject.Inject;
import java.util.List;

@Repository
public class AccountDAO implements IAccountDAO {

    @Inject
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public AccountEntity save(AccountEntity account) {
        getSession().saveOrUpdate(account);
        return account;
    }

    @Override
    public AccountEntity findById(int accountId, boolean lock) {
        return (AccountEntity) getSession()
                .get(AccountEntity.class, accountId, lock ? LockOptions.UPGRADE : LockOptions.NONE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<AccountEntity> findAll() {
        return getSession()
                .createCriteria(AccountEntity.class)
                .list();
    }

    @Override
    public boolean delete(int accountId) {
        int deleted = getSession()
                .createQuery("DELETE AccountEntity WHERE id = :id")
                .setInteger("id", accountId)
                .executeUpdate();
        return deleted == 1;
    }
}
