package mate.academy.dao.impl;

import java.util.Optional;
import mate.academy.dao.UserDao;
import mate.academy.exception.DataProcessingException;
import mate.academy.lib.Dao;
import mate.academy.model.User;
import mate.academy.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

@Dao
public class UserDaoImpl implements UserDao {
    @Override
    public User add(User user) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            return user;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can't insert user: " + user, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public Optional<User> get(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Optional<User> userOptional = session.createQuery("FROM User u WHERE u.email = :email",
                            User.class)
                    .setParameter("email", email)
                    .uniqueResultOptional();
            return userOptional;
        } catch (Exception e) {
            throw new DataProcessingException("Can't get a user by email: " + email, e);
        }
    }
}
