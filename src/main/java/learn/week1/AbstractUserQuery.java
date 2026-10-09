package learn.week1;

import com.example.crazyjava.entity.User;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractUserQuery implements UserQuery {
    @Override
    public List<User> query(List<User> users) {
        List<User> result = new ArrayList<>();
        for (User user : users) {
            if (match(user)) {
                result.add(user);
            }
        }
        return result;
    }

    protected abstract boolean match(User user);
}