package dk.statsbiblioteket.larm_doms_exporter.persistence.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public interface HibernateUtilIF {

    public SessionFactory getSessionFactory();

    public Session getSession();

}
