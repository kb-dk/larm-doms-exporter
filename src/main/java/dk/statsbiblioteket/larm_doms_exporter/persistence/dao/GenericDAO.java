package dk.statsbiblioteket.larm_doms_exporter.persistence.dao;


public interface GenericDAO<T, PK extends java.io.Serializable>   {

    PK create(T t);

    T read(PK pk);

    void update(T t);

    void delete(T t);
}

