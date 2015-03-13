package ua.pp.condor.ioc.repository;

import java.io.Serializable;
import java.util.List;

public interface IGenericDAO<E, ID extends Serializable> {

    E save(E entity);

    E findById(ID id, boolean lock);

    List<E> findAll();

    boolean delete(ID id);
}
