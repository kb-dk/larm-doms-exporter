/* $Id$
 * $Revision$
 * $Date$
 * $Author$
 *
 *
 */
package dk.statsbiblioteket.larm_doms_exporter.persistence.dao;

import org.hibernate.Session;

import java.io.Serializable;

public class GenericHibernateDAO<T extends Identifiable<PK>, PK extends Serializable> implements GenericDAO<T, PK> {

    private HibernateUtilIF util;
    private Class<T> type;

    public GenericHibernateDAO(Class<T> type, HibernateUtilIF util) {
        this.type = type;
        this.util=util;
    }

    @SuppressWarnings("unchecked")
   public PK create(T o) {
        Session sess = getSession();
        PK key;
        try {
            sess.beginTransaction();
            key = (PK) sess.save(o);
            sess.getTransaction().commit();
        } finally {
            sess.close();
        }
        return key;
    }

    @SuppressWarnings("unchecked")
    public T read(PK id) {
        Session sess = getSession();
        //sess.beginTransaction();
        T result =  (T) sess.get(type, id);
        //sess.getTransaction().commit();
        sess.close();
        return result;
    }

    @SuppressWarnings("unchecked")
    public T readOrCreate(PK id) {
        Session sess = getSession();
        sess.beginTransaction();
        T result =  (T) sess.get(type, id);
        if (result == null){
            try {
                result = type.newInstance();
                result.setID(id);
                sess.save(result);
            } catch (InstantiationException e) {
                throw new RuntimeException("Failed to create new hibernate entry",e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to create new hibernate entry",e);
            }
        }
        sess.getTransaction().commit();
        sess.close();
        return result;
    }


    public void update(T o) {
        Session sess = getSession();
        sess.beginTransaction();
        sess.update(o);
        sess.getTransaction().commit();
        sess.close();
    }

    public void delete(T o) {
        Session sess = getSession();
        sess.beginTransaction();
        sess.delete(o);
        sess.getTransaction().commit();
        sess.close();
    }

    protected Session getSession() {
        return util.getSession();
    }

    public void flush() {
        getSession().flush();
    }


}

