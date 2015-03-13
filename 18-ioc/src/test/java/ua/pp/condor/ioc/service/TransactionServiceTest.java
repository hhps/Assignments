package ua.pp.condor.ioc.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import ua.pp.condor.ioc.config.TestConfiguration;
import ua.pp.condor.ioc.entity.AccountEntity;
import ua.pp.condor.ioc.entity.TransactionEntity;
import ua.pp.condor.ioc.service.exception.IllegalEntityStateException;
import ua.pp.condor.ioc.service.exception.NoSuchEntityException;
import ua.pp.condor.ioc.service.exception.NotEnoughMoneyException;

import javax.inject.Inject;
import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static ua.pp.condor.ioc.service.AccountServiceTest.ACCOUNT_TABLE;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class TransactionServiceTest {

    static final String TRANSACTION_TABLE = "transaction";

    @Inject
    private ITransactionService transactionService;

    @Inject
    private IAccountService accountService;

    private JdbcTemplate jdbcTemplate;

    private Integer firstAccountId;
    private Integer secondAccountId;

    @Inject
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        JdbcTestUtils.deleteFromTables(jdbcTemplate, TRANSACTION_TABLE, ACCOUNT_TABLE);
        AccountEntity account1 = new AccountEntity("Joe Shmoe", 1000);
        firstAccountId = accountService.save(account1).getId();
        AccountEntity account2 = new AccountEntity("John Doe", 1000);
        secondAccountId = accountService.save(account2).getId();
    }

    @Test
    public void testSave() {
        AccountEntity account1 = new AccountEntity("Joe Shmoe", 1000);
        account1 = accountService.save(account1);
        AccountEntity account2 = new AccountEntity("John Doe");
        account2 = accountService.save(account2);
        
        TransactionEntity transaction = new TransactionEntity(account1.getId(), account2.getId(), 1000.0);
        final int countBeforeSave = JdbcTestUtils.countRowsInTable(jdbcTemplate, TRANSACTION_TABLE);

        transaction = transactionService.save(transaction);
        final int countAfterSave = JdbcTestUtils.countRowsInTable(jdbcTemplate, TRANSACTION_TABLE);
        
        assertNotNull(transaction);
        assertNotNull(transaction.getId());
        assertNotNull(transaction.getCreationTime());
        assertEquals(countBeforeSave + 1, countAfterSave);
        
        account1 = accountService.findById(account1.getId());
        account2 = accountService.findById(account2.getId());
        assertEquals(0, account1.getBalance(), 0.001);
        assertEquals(1000, account2.getBalance(), 0.001);
    }

    @Test(expected = NullPointerException.class)
    public void testSaveNull() {
        transactionService.save(null);
    }

    @Test(expected = IllegalEntityStateException.class)
    public void testSaveWithoutAccountFrom() {
        TransactionEntity transaction = new TransactionEntity(1, 2, 100.0);
        transaction.setAccountFrom(null);
        transactionService.save(transaction);
    }

    @Test(expected = IllegalEntityStateException.class)
    public void testSaveWithoutAccountTo() {
        TransactionEntity transaction = new TransactionEntity(1, 2, 100.0);
        transaction.setAccountTo(null);
        transactionService.save(transaction);
    }

    @Test(expected = IllegalEntityStateException.class)
    public void testSaveWithSameIds() {
        TransactionEntity transaction = new TransactionEntity(1, 1, 100.0);
        transactionService.save(transaction);
    }

    @Test(expected = IllegalEntityStateException.class)
    public void testSaveWithoutAmount() {
        TransactionEntity transaction = new TransactionEntity(1, 2, 100.0);
        transaction.setAmount(null);
        transactionService.save(transaction);
    }

    @Test(expected = IllegalEntityStateException.class)
    public void testSaveWithNegativeAmount() {
        TransactionEntity transaction = new TransactionEntity(firstAccountId, secondAccountId, -100.0);
        transactionService.save(transaction);
    }

    @Test(expected = IllegalEntityStateException.class)
    public void testSaveWithZeroAmount() {
        TransactionEntity transaction = new TransactionEntity(firstAccountId, secondAccountId, 0.0);
        transactionService.save(transaction);
    }

    @Test(expected = IllegalEntityStateException.class)
    public void testSaveSavedTransaction() {
        TransactionEntity transaction = new TransactionEntity(firstAccountId, secondAccountId, 100.0);
        transaction = transactionService.save(transaction);
        transactionService.save(transaction);
    }

    @Test(expected = IllegalEntityStateException.class)
    public void testSaveWithCreationTime() {
        TransactionEntity transaction = new TransactionEntity(firstAccountId, secondAccountId, 0.0);
        transaction.setCreationTime(new Date());
        transactionService.save(transaction);
    }

    @Test
    public void testFindById() {
        TransactionEntity transaction = new TransactionEntity(firstAccountId, secondAccountId, 100.0);
        transaction = transactionService.save(transaction);

        TransactionEntity found = transactionService.findById(transaction.getId());
        assertEquals(transaction, found);

        found = transactionService.findById(-1);
        assertNull(found);
    }

    @Test
    public void testFindAll() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, TRANSACTION_TABLE);

        List<TransactionEntity> transactions = transactionService.findAll();
        assertTrue(transactions.isEmpty());

        TransactionEntity transaction1 = new TransactionEntity(firstAccountId, secondAccountId, 100.0);
        transaction1 = transactionService.save(transaction1);

        transactions = transactionService.findAll();
        assertEquals(1, transactions.size());

        TransactionEntity transaction2 = new TransactionEntity(secondAccountId, firstAccountId, 100.0);
        transaction2 = transactionService.save(transaction2);

        transactions = transactionService.findAll();
        assertEquals(2, transactions.size());

        List<TransactionEntity> expectedList = new ArrayList<>(2);
        expectedList.add(transaction1);
        expectedList.add(transaction2);
        assertEquals(expectedList.hashCode(), transactions.hashCode());
    }

    @Test
    public void testDelete() {
        final int countBeforeSave = JdbcTestUtils.countRowsInTable(jdbcTemplate, TRANSACTION_TABLE);
        TransactionEntity transaction = new TransactionEntity(firstAccountId, secondAccountId, 100.0);
        transaction = transactionService.save(transaction);

        final int countBeforeDelete = JdbcTestUtils.countRowsInTable(jdbcTemplate, TRANSACTION_TABLE);
        assertNotEquals(countBeforeSave, countBeforeDelete);

        boolean deleteResult = transactionService.delete(transaction.getId());
        assertTrue(deleteResult);
        final int countAfterDelete = JdbcTestUtils.countRowsInTable(jdbcTemplate, TRANSACTION_TABLE);
        assertEquals(countBeforeSave, countAfterDelete);

        deleteResult = transactionService.delete(-1);
        assertFalse(deleteResult);
    }

    @Test
    public void testTransactionRollbackNotEnoughMoney() {
        AccountEntity account1 = new AccountEntity("Joe Shmoe", 0);
        account1 = accountService.save(account1);
        AccountEntity account2 = new AccountEntity("John Doe", 0);
        account2 = accountService.save(account2);

        TransactionEntity transaction = new TransactionEntity(account1.getId(), account2.getId(), 100.0);
        final int countBeforeSave = JdbcTestUtils.countRowsInTable(jdbcTemplate, TRANSACTION_TABLE);

        try {
            transaction = transactionService.save(transaction);
        } catch (NotEnoughMoneyException e) {}
        final int countAfterSave = JdbcTestUtils.countRowsInTable(jdbcTemplate, TRANSACTION_TABLE);

        assertNull(transaction.getId());
        assertNull(transaction.getCreationTime());
        assertEquals(countBeforeSave, countAfterSave);

        account1 = accountService.findById(account1.getId());
        account2 = accountService.findById(account2.getId());
        assertEquals(0, account1.getBalance(), 0.001);
        assertEquals(0, account2.getBalance(), 0.001);
    }

    @Test
    public void testTransactionRollbackNoSuchEntity() {
        AccountEntity account1 = new AccountEntity("Joe Shmoe", 1000);
        account1 = accountService.save(account1);
        Integer incorrectIdForAccount2 = -1;

        TransactionEntity transaction = new TransactionEntity(account1.getId(), incorrectIdForAccount2, 100.0);
        final int countBeforeSave = JdbcTestUtils.countRowsInTable(jdbcTemplate, TRANSACTION_TABLE);

        try {
            transaction = transactionService.save(transaction);
        } catch (NoSuchEntityException e) {}
        final int countAfterSave = JdbcTestUtils.countRowsInTable(jdbcTemplate, TRANSACTION_TABLE);

        assertNull(transaction.getId());
        assertNull(transaction.getCreationTime());
        assertEquals(countBeforeSave, countAfterSave);

        account1 = accountService.findById(account1.getId());
        assertEquals(1000, account1.getBalance(), 0.001);
    }
}
