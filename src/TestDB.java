import java.sql.*;
import java.util.Vector;

public class TestDB {
    private Connection connect = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private String url = "jdbc:mysql://localhost:3306/mydbtest?serverTimezone=UTC";
    private String user = "root", pass = "root";

    TestDB(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try{
            connect = DriverManager.getConnection(url, user, pass);
            if (!connect.isClosed()) {
                System.out.println("FINE");
            }
            statement = connect.createStatement();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void close(){
        try {
            connect.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    public void addAll(Vector<Animal> animals){
        if(animals!=null){
            if(animals.size()>0){
                clearAll();
                synchronized (animals){
                    for (Animal a:animals) {
                        try {
                            if(a instanceof Cat){
                                resultSet = statement.executeQuery("insert into animals (animaltype, x, y, lifetime) values("+2+","+(int)a.x+","+(int)a.y+","+a.duration+")");
                            }
                            if(a instanceof Dog){
                                resultSet = statement.executeQuery("insert into animals (animaltype, x, y, lifetime) values("+1+","+(int)a.x+","+(int)a.y+","+a.duration+")");
                            }
                        } catch (SQLException throwables) {
                            System.out.println("error adding All to db");
                            break;
                        }
                    }
                }
            }
        }
    }
    public void addCats(Vector<Animal> animals){
        if(animals!=null){
            if(animals.size()>0){
                clearCats();
                synchronized (animals){
                    for (Animal a:animals) {
                        try {
                            if(a instanceof Cat){
                                statement.execute("insert into animals (animaltype, x, y, lifetime) values("+2+","+(int)a.x+","+(int)a.y+","+a.duration+")");
                            }
                        } catch (SQLException throwables) {
                            System.out.println("error adding Cats to db");
                            break;
                        }
                    }
                }
            }
        }
    }
    public void addDogs(Vector<Animal> animals){
        if(animals!=null){
            if(animals.size()>0){
                clearDogs();
                synchronized (animals){
                    for (Animal a:animals) {
                        try {
                            if(a instanceof Dog){
                                statement.execute("insert into animals (animaltype, x, y, lifetime) values("+1+","+(int)a.x+","+(int)a.y+","+a.duration+")");
                            }
                        } catch (SQLException throwables) {
                            System.out.println("error adding Dog to db");
                            break;
                        }
                    }
                }
            }
        }
    }
    public Vector<Animal> getAll(long currentTime){
        Vector<Animal> result = new Vector<Animal>();
        try{
            resultSet = statement.executeQuery("select * from animals");
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                int animalType = resultSet.getInt(2);
                int x = resultSet.getInt(3);
                int y = resultSet.getInt(4);
                int lifeTime = resultSet.getInt(5);
                if(animalType==1){
                    result.add(new Dog(x,y,currentTime,lifeTime));
                }
                if(animalType==2){
                    result.add(new Cat(x,y,currentTime,lifeTime));
                }
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return result;
    }
    public Vector<Animal> getDogs(long currentTime){
        Vector<Animal> result = new Vector<Animal>();
        try{
            resultSet = statement.executeQuery("select * from animals where animaltype = 1");
            while (resultSet.next()) {
                int animalType = resultSet.getInt(2);
                int x = resultSet.getInt(3);
                int y = resultSet.getInt(4);
                int lifeTime = resultSet.getInt(5);
                if(animalType==1){
                    result.add(new Dog(x,y,currentTime,lifeTime,true));
                }
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return result;
    }
    public Vector<Animal> getCats(long currentTime){
        Vector<Animal> result = new Vector<Animal>();
        try{
            resultSet = statement.executeQuery("select * from animals where animaltype = 2");
            while (resultSet.next()) {
                int animalType = resultSet.getInt(2);
                int x = resultSet.getInt(3);
                int y = resultSet.getInt(4);
                int lifeTime = resultSet.getInt(5);
                if(animalType==2){
                    result.add(new Cat(x,y,currentTime,lifeTime,true));
                }
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return result;
    }
    public void clearAll(){
        try {
            statement.execute("delete from animals");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public void clearDogs(){
        try {
            statement.execute("delete from animals where animaltype=1");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public void clearCats(){
        try {
            statement.execute("delete from animals where animaltype=2");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
