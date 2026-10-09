package learn.week1;

import com.example.crazyjava.entity.User;

import java.util.ArrayList;
import java.util.List;

public class Week1Main {
    public static void main(String[] args) {
        List<User> users = new ArrayList<>();
        users.add(createUser("张三", 16));
        users.add(createUser("李四", 20));
        users.add(createUser("张伟", 30));

        UserQuery query = new AdultUserQuery();
        System.out.println(query.query(users).size());

        query = new NameUserQuery("张");
        System.out.println(query.query(users));
    }

    private static User createUser(String name, int age) {
        User user = new User();
        user.setName(name);
        user.setAge(age);
        return user;
    }
}
