package ua.pp.condor.ioc.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = "account")
public class AccountEntity {

    private Integer id;
    private String fullName;
    private double balance;
    private Date creationTime;

    public AccountEntity() {
    }

    public AccountEntity(String fullName) {
        this.fullName = fullName;
    }

    public AccountEntity(String fullName, double balance) {
        this(fullName);
        this.balance = balance;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "full_name")
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Column(name = "balance")
    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Column(name = "creation_time")
    @Temporal(TemporalType.TIMESTAMP)
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

        AccountEntity that = (AccountEntity) o;
        if (Double.compare(that.balance, balance) != 0) return false;
        if (creationTime != null ? !creationTime.equals(that.creationTime) : that.creationTime != null) return false;
        if (fullName != null ? !fullName.equals(that.fullName) : that.fullName != null) return false;
        return !(id != null ? !id.equals(that.id) : that.id != null);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        result = 31 * result + (fullName != null ? fullName.hashCode() : 0);
        temp = Double.doubleToLongBits(balance);
        result = 31 * result + (int) (temp ^ temp >>> 32);
        result = 31 * result + (creationTime != null ? creationTime.hashCode() : 0);
        return result;
    }

    @Override

    public String toString() {
        return "AccountEntity{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", balance=" + balance +
                ", creationTime=" + creationTime +
                '}';
    }
}
