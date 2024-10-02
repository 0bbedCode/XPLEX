package com.obbedcode.shared.repositories.interfaces;

public interface IRepositoryContainer<T> {
    IRepository<T> getRepository();
    void setRepository(IRepository<T> repository);
}
