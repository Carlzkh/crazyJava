package learn.week1;

import com.example.crazyjava.entity.User;


public class NameUserQuery extends AbstractUserQuery {
    private final String name;
    public NameUserQuery(String name){this.name=name;}
    @Override
    protected boolean match(User user) {
        return user.getName() != null && user.getName().contains(name);
    }
}