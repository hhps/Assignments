package ua.pp.condor.ioc.entity;

import java.util.Date;

public class TransactionEntity {

    private Long id;
    private Integer accountFrom;
    private Integer accountTo;
    private Double amount;
    private Date creationTime;

    public TransactionEntity() {
    }

    public TransactionEntity(Integer accountFrom, Integer accountTo, Double amount) {
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(Integer accountFrom) {
        this.accountFrom = accountFrom;
    }

    public Integer getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(Integer accountTo) {
        this.accountTo = accountTo;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Date getCreationTime() {
        return creationTime != null ? (Date) creationTime.clone() : null;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime != null ? (Date) creationTime.clone() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransactionEntity that = (TransactionEntity) o;
        if (accountFrom != null ? !accountFrom.equals(that.accountFrom) : that.accountFrom != null) return false;
        if (accountTo != null ? !accountTo.equals(that.accountTo) : that.accountTo != null) return false;
        if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
        if (creationTime != null ? !creationTime.equals(that.creationTime) : that.creationTime != null) return false;
        return !(id != null ? !id.equals(that.id) : that.id != null);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (accountFrom != null ? accountFrom.hashCode() : 0);
        result = 31 * result + (accountTo != null ? accountTo.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (creationTime != null ? creationTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TransactionEntity{" +
                "id=" + id +
                ", accountFrom=" + accountFrom +
                ", accountTo=" + accountTo +
                ", amount=" + amount +
                ", creationTime=" + creationTime +
                '}';
    }
}
