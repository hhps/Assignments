package ua.pp.condor.ioc.repository.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ua.pp.condor.ioc.entity.TransactionEntity;
import ua.pp.condor.ioc.repository.ITransactionDAO;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class TransactionDAO implements ITransactionDAO {

    private static class TransactionRowMapper implements RowMapper<TransactionEntity> {

        @Override
        public TransactionEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            TransactionEntity transaction = new TransactionEntity();
            transaction.setId(rs.getLong(COLUMN_ID));
            transaction.setAccountFrom(rs.getInt(COLUMN_ACCOUNT_FROM));
            transaction.setAccountTo(rs.getInt(COLUMN_ACCOUNT_TO));
            transaction.setAmount(rs.getDouble(COLUMN_AMOUNT));
            transaction.setCreationTime(rs.getTimestamp(COLUMN_CREATION_TIME));
            return transaction;
        }
    }

    private static final TransactionRowMapper ROW_MAPPER = new TransactionRowMapper();

    private static final String TABLE = "transaction";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ACCOUNT_FROM = "account_from";
    private static final String COLUMN_ACCOUNT_TO = "account_to";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_CREATION_TIME = "creation_time";

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM " + TABLE + " WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM " + TABLE;
    private static final String DELETE_QUERY = "DELETE FROM " + TABLE + " WHERE id = ?";

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert jdbcInsert;

    @Inject
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName(TABLE)
                .usingColumns(COLUMN_ACCOUNT_FROM, COLUMN_ACCOUNT_TO, COLUMN_AMOUNT, COLUMN_CREATION_TIME)
                .usingGeneratedKeyColumns(COLUMN_ID);
    }

    @Override
    public TransactionEntity save(TransactionEntity transaction) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(COLUMN_ACCOUNT_FROM, transaction.getAccountFrom())
                .addValue(COLUMN_ACCOUNT_TO, transaction.getAccountTo())
                .addValue(COLUMN_AMOUNT, transaction.getAmount())
                .addValue(COLUMN_CREATION_TIME, transaction.getCreationTime());
        Number newId = jdbcInsert.executeAndReturnKey(parameters);
        transaction.setId(newId.longValue());
        return transaction;
    }

    @Override
    public TransactionEntity findById(long transactionId, boolean lock) {
        try {
            return jdbcTemplate.queryForObject(FIND_BY_ID_QUERY, new Object[]{transactionId}, ROW_MAPPER);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<TransactionEntity> findAll() {
        return jdbcTemplate.query(FIND_ALL_QUERY, ROW_MAPPER);
    }

    @Override
    public boolean delete(long transactionId) {
        int deleted = jdbcTemplate.update(DELETE_QUERY, transactionId);
        return deleted == 1;
    }
}
