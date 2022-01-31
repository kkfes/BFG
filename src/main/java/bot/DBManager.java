package bot;

import java.sql.*;

public class DBManager {
    private static final String TABLE_NAME = "";// Название базы данных
    public static Connection c;

    static {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            c = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+TABLE_NAME+"?serverTimezone=UTC", "root", "");
        }catch (Exception e){
            e.printStackTrace();
        }
        createTable();
    }

    private static void createTable(){
        try {
            PreparedStatement statement = c.prepareStatement("SELECT * FROM users");
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            statement.close();
        }catch (Exception e){
            try {
                {
                    PreparedStatement statement = c.prepareStatement("CREATE TABLE `"+TABLE_NAME+"`.`users` ( `id` VARCHAR(255) NOT NULL , `user_fullname` VARCHAR(255) NOT NULL , `user_name` VARCHAR(255) NOT NULL , `money` VARCHAR(255) NOT NULL , `bitcoin` INT(11) NOT NULL , `bank` VARCHAR(255) NOT NULL , `registration` INT(11) NOT NULL , `experience` INT(11) NOT NULL , `business` INT(11) NOT NULL ) ENGINE = InnoDB;");
                    statement.executeUpdate();
                    statement.close();
                }
                {
                    PreparedStatement statement = c.prepareStatement("ALTER TABLE `users` ADD `bonus` INT(11) NOT NULL AFTER `business`;");
                    statement.executeUpdate();
                    statement.close();
                }//ALTER TABLE `users` ADD `hall` INT(11) NOT NULL AFTER `bonus`;
                {
                    PreparedStatement statement = c.prepareStatement("ALTER TABLE `users` ADD `hall` INT(11) NOT NULL AFTER `bonus`;");
                    statement.executeUpdate();
                    statement.close();
                }//CREATE TABLE `bfg`.`business` ( `id` VARCHAR(255) NOT NULL , `name` VARCHAR(255) NOT NULL , `workers` INT(11) NOT NULL , `cost` INT(11) NOT NULL , `profit` INT(11) NOT NULL , `unix` INT(11) NOT NULL ) ENGINE = InnoDB;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        try {
            PreparedStatement statement = c.prepareStatement("SELECT * FROM business");
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            statement.close();
        }catch (Exception e){
            try {
                {
                    PreparedStatement statement = c.prepareStatement("CREATE TABLE `"+TABLE_NAME+"`.`business` ( `id` VARCHAR(255) NOT NULL , `name` VARCHAR(255) NOT NULL , `workers` INT(11) NOT NULL , `cost` INT(11) NOT NULL , `profit` INT(11) NOT NULL , `unix` INT(11) NOT NULL ) ENGINE = InnoDB;");
                    statement.executeUpdate();
                    statement.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }




}
