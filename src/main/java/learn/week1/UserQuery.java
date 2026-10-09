package learn.week1;

import com.example.crazyjava.entity.User;

import java.util.List;

public interface UserQuery {
    List<User> query(List<User> users);
}
