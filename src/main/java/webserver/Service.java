package webserver;

import com.google.common.collect.Maps;
import db.DataBase;
import model.User;

import java.util.Collection;
import java.util.Map;

/**
 * Created by jojoldu@gmail.com on 2016-06-30.
 */
public class Service {

    public static void addUser(User user) {
        DataBase.addUser(user);
    }

    public static User findUserById(String userId) {
        return DataBase.findUserById(userId);
    }

    public static Collection<User> findAll() {
        return DataBase.findAll();
    }
}
