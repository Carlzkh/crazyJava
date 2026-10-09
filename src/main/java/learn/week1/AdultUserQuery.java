package learn.week1;

import com.example.crazyjava.entity.User;

public class AdultUserQuery extends AbstractUserQuery {
    @Override
    protected boolean match(User user) {
        return user.getAge() != null && user.getAge() >= 18;
    }
}