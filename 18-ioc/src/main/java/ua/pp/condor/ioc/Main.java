package ua.pp.condor.ioc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ua.pp.condor.ioc.config.ProdConfiguration;
import ua.pp.condor.ioc.entity.AccountEntity;
import ua.pp.condor.ioc.entity.TransactionEntity;
import ua.pp.condor.ioc.service.IAccountService;
import ua.pp.condor.ioc.service.ITransactionService;

public final class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.trace("Application starts...");

        try {
            final ApplicationContext context = new AnnotationConfigApplicationContext(ProdConfiguration.class);
            IAccountService accountService = context.getBean(IAccountService.class);
            ITransactionService transactionService = context.getBean(ITransactionService.class);

            AccountEntity account1 = new AccountEntity("Joe Shmoe", 1000);
            account1 = accountService.save(account1);
            log.info("First account before transaction: {}", account1);
            AccountEntity account2 = new AccountEntity("John Doe");
            account2 = accountService.save(account2);
            log.info("Second account before transaction: {}", account2);

            TransactionEntity transaction = new TransactionEntity(account1.getId(), account2.getId(), 500.0);
            transaction = transactionService.save(transaction);
            log.info(transaction.toString());

            account1 = accountService.findById(account1.getId());
            log.info("First account after transaction: {}", account1);
            account2 = accountService.findById(account2.getId());
            log.info("Second account after transaction: {}", account2);
        } catch (Throwable th) {
            log.error("Exception during application work", th);
        }

        log.trace("Application ends.");
    }
}
