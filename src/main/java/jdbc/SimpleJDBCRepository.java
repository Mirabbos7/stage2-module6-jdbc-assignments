package jdbc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {

    private Connection connection = null;
    private PreparedStatement ps = null;
    private Statement st = null;

    private static final String createUserSQL = "Insert into myusers (firstName, lastName, age) values (?, ?, ?)";
    private static final String updateUserSQL = "update myusers set firstName = ?, lastName = ?, age = ?";
    private static final String deleteUser = "delete from myusers where id = ?";
    private static final String findUserByIdSQL = "select * from myusers where id = ?";
    private static final String findUserByNameSQL = "select * from myusers where name = ?";
    private static final String findAllUserSQL = "select * from myusers";

    public Long createUser(User user) {
        Long result = null;
        try(Connection connection = CustomDataSource.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(createUserSQL, Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setInt(3, user.getAge());
            preparedStatement.execute();
            try(ResultSet rs = preparedStatement.getGeneratedKeys()){
                while (rs.next()){
                    result = rs.getLong(1);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public User findUserById(Long userId) {
        User user = null;
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(findUserByIdSQL)) {
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setId(userId);
                user.setFirstName(rs.getString("firstname"));
                user.setLastName(rs.getString("lastname"));
                user.setAge(rs.getInt("age"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public User findUserByName(String userName) {
        User user = null;
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(findUserByNameSQL)) {
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String firstname = rs.getString("firstname");
                String lastname = rs.getString("lastname");
                int age = rs.getInt("age");
                Long id = Long.parseLong(rs.getString("id"));
                user = new User(id, firstname, lastname, age);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public List<User> findAllUser() {
        List<User> users = new ArrayList<>();
        try(Connection connection = CustomDataSource.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(findAllUserSQL)){
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                String firstname = rs.getString("firstname");
                String lastname = rs.getString("lastname");
                int age = rs.getInt("age");
                Long id = Long.parseLong(rs.getString("id"));
                users.add(new User(id, firstname, lastname, age));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return users;
    }

    public User updateUser(User user) {
        try(Connection connection = CustomDataSource.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(updateUserSQL)){
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setLong(3, user.getId());
            ps.setInt(4, user.getAge());
            if(ps.executeUpdate() == 0){
                throw new Exception("No such user exist");
            }
    } catch (Exception e) {
        e.printStackTrace();
        }
        return user;
    }

    private void deleteUser(Long userId) {
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(deleteUser)) {
            ps.setLong(1, userId);
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
