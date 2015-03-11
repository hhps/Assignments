package ua.pp.condor.ioc.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import ua.pp.condor.ioc.config.TestConfiguration;
import ua.pp.condor.ioc.entity.AccountEntity;
import ua.pp.condor.ioc.service.exception.IllegalEntityStateException;
import ua.pp.condor.ioc.service.exception.NoSuchEntityException;
import ua.pp.condor.ioc.service.exception.NotEnoughMoneyException;

import javax.inject.Inject;
import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class AccountServiceTest {

    private static final String ACCOUNT_TABLE = "account";

    @Inject
    private IAccountService accountService;

    private JdbcTemplate jdbcTemplate;

    @Inject
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    public void testSave() {
        AccountEntity account = new AccountEntity("Joe Shmoe");
        final int countBeforeSave = JdbcTestUtils.countRowsInTable(jdbcTemplate, ACCOUNT_TABLE);

        account = accountService.save(account);
        final int countAfterSave = JdbcTestUtils.countRowsInTable(jdbcTemplate, ACCOUNT_TABLE);

        assertNotNull(account);
        assertNotNull(account.getId());
        assertNotNull(account.getCreationTime());
        assertEquals(countBeforeSave + 1, countAfterSave);
    }

    @Test(expected = NullPointerException.class)
    public void testSaveNull() {
        accountService.save(null);
    }

    @Test(expected = IllegalEntityStateException.class)
    public void testSaveWithoutFullName() {
        AccountEntity account = new AccountEntity();
        accountService.save(account);
    }

    @Test(expected = IllegalEntityStateException.class)
    public void testSaveWithNegativeBalance() {
        AccountEntity account = new AccountEntity("Joe Shmoe", -1);
        accountService.save(account);
    }

    @Test(expected = IllegalEntityStateException.class)
    public void testSaveSavedAccount() {
        AccountEntity account = new AccountEntity("Joe Shmoe");
        account = accountService.save(account);
        accountService.save(account);
    }

    @Test
    public void testFindById() {
        AccountEntity account = new AccountEntity("Joe Shmoe");
        account = accountService.save(account);

        AccountEntity found = accountService.findById(account.getId());
        assertEquals(account, found);

        found = accountService.findById(-1);
        assertNull(found);
    }

    @Test
    public void testFindAll() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, ACCOUNT_TABLE);

        List<AccountEntity> accounts = accountService.findAll();
        assertTrue(accounts.isEmpty());

        AccountEntity account1 = new AccountEntity("Joe Shmoe");
        account1 = accountService.save(account1);

        accounts = accountService.findAll();
        assertEquals(1, accounts.size());

        AccountEntity account2 = new AccountEntity("John Doe");
        account2 = accountService.save(account2);

        accounts = accountService.findAll();
        assertEquals(2, accounts.size());

        List<AccountEntity> expectedList = new ArrayList<>(2);
        expectedList.add(account1);
        expectedList.add(account2);
        assertEquals(expectedList.hashCode(), accounts.hashCode());
    }

    @Test
    public void testDelete() {
        final int countBeforeSave = JdbcTestUtils.countRowsInTable(jdbcTemplate, ACCOUNT_TABLE);
        AccountEntity account = new AccountEntity("Joe Shmoe");
        account = accountService.save(account);

        final int countBeforeDelete = JdbcTestUtils.countRowsInTable(jdbcTemplate, ACCOUNT_TABLE);
        assertNotEquals(countBeforeSave, countBeforeDelete);

        boolean deleteResult = accountService.delete(account.getId());
        assertTrue(deleteResult);
        final int countAfterDelete = JdbcTestUtils.countRowsInTable(jdbcTemplate, ACCOUNT_TABLE);
        assertEquals(countBeforeSave, countAfterDelete);

        deleteResult = accountService.delete(-1);
        assertFalse(deleteResult);
    }

    @Test
    public void testUpdateFullName() {
        AccountEntity account = new AccountEntity("Joe Shmoe");
        account = accountService.save(account);
        assertEquals("Joe Shmoe", account.getFullName());

        AccountEntity updatedAccount = accountService.updateFullName(account.getId(), "John Doe");
        assertEquals("John Doe", updatedAccount.getFullName());

        account.setFullName("John Doe");
        assertEquals(account, updatedAccount);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateFullNameEmpty() {
        AccountEntity account = new AccountEntity("Joe Shmoe");
        account = accountService.save(account);
        accountService.updateFullName(account.getId(), "   ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateFullNameNull() {
        AccountEntity account = new AccountEntity("Joe Shmoe");
        account = accountService.save(account);
        accountService.updateFullName(account.getId(), null);
    }

    @Test(expected = NoSuchEntityException.class)
    public void testUpdateFullNameWithIncorrectId() {
        accountService.updateFullName(-1, "Joe Shmoe");
    }

    @Test
    public void testIncome() {
        AccountEntity account = new AccountEntity("Joe Shmoe");
        account = accountService.save(account);
        assertEquals(0.0, account.getBalance(), 0.001);

        final double amount = 100;
        account = accountService.income(account.getId(), amount);
        assertEquals(amount, account.getBalance(), 0.001);

        account = accountService.income(account.getId(), amount);
        assertEquals(amount * 2, account.getBalance(), 0.001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncomeZero() {
        AccountEntity account = new AccountEntity("Joe Shmoe");
        account = accountService.save(account);
        accountService.income(account.getId(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncomeNagative() {
        AccountEntity account = new AccountEntity("Joe Shmoe");
        account = accountService.save(account);
        accountService.income(account.getId(), -100);
    }

    @Test(expected = NoSuchEntityException.class)
    public void testIncomeWithIncorrectId() {
        accountService.income(-1, 100);
    }

    @Test
    public void testLoss() {
        final double initialBalance = 500;
        AccountEntity account = new AccountEntity("Joe Shmoe", initialBalance);
        account = accountService.save(account);
        assertEquals(initialBalance, account.getBalance(), 0.001);

        final double lossAmount = 100;
        account = accountService.loss(account.getId(), lossAmount);
        assertEquals(initialBalance - lossAmount, account.getBalance(), 0.001);
    }

    @Test(expected = NotEnoughMoneyException.class)
    public void testLossNotEnoughMoney() {
        AccountEntity account = new AccountEntity("Joe Shmoe");
        account = accountService.save(account);
        accountService.loss(account.getId(), 1000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLossZero() {
        AccountEntity account = new AccountEntity("Joe Shmoe");
        account = accountService.save(account);
        accountService.loss(account.getId(), -100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLossNagative() {
        AccountEntity account = new AccountEntity("Joe Shmoe");
        account = accountService.save(account);
        accountService.loss(account.getId(), -100);
    }

    @Test(expected = NoSuchEntityException.class)
    public void testLossWithIncorrectId() {
        accountService.loss(-1, 100);
    }
}
