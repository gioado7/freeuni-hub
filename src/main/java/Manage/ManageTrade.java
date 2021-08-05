package Manage;

import DataBaseConnection.BaseConnector;
import Manage.Configurations.SaveleConfiguration;
import Manage.HelperClasses.UserById;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ManageTrade implements SaveleConfiguration {
    private static Connection con;
    private final BaseConnector bc;

    public ManageTrade(BaseConnector bc) {
        this.bc = bc;
        con = bc.accessConnection();
    }

    // methods checks whether exists or not
    public boolean isLocation(String location) throws SQLException { // to fix
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select * from " + LOCATIONS_TABLE + ";");

        while (rs.next()) {
            if (rs.getString("name").equals(location)) return true;
        }
        return false;
    }

    public void addLocation(String location) throws SQLException { // to fix
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select * from " + LOCATIONS_TABLE + ";");

        int count = 0; // initially no students
        while (rs.next()) {
            if (rs.getString("name").equals(location)) {
                count = Integer.parseInt(rs.getString("numStudents"));
            }
        }

        stmt.execute("insert into " + LOCATIONS_TABLE + "(name, numStudents) values ('"
                + location + "', '" + count + "');");
    }


    public void removeLocation(String location) throws SQLException { // to fix
        Statement stmt = con.createStatement();

        boolean exists = isLocation(location); // check whether location exists or not
        if (exists)
            stmt.execute("delete from " + LOCATIONS_TABLE + " where name = '" + location + "';");
        else throw new Error("location with given name isn't found");
    }


    // 2 helper methods for adding and removing student----------------------------------------------------------------
    private void increaseNumStudents(String location) throws SQLException { // increases number of students in 'locations' table
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select * from " + LOCATIONS_TABLE + ";");

        int count = 0, id = -1;
        while (rs.next()) {
            if (rs.getString("name").equals(location)) {
                count = Integer.parseInt(rs.getString("numStudents"));
                id = Integer.parseInt(rs.getString("id"));
                count++; // 1 more
                break;
            }
        }

        stmt.execute("delete from " + LOCATIONS_TABLE + " where id = " + id + ";");
        stmt.execute("insert into " + LOCATIONS_TABLE + " values ('" + id + "' , '" +
              location + "' , '" + count + "');");
    }

    private void decreaseNumStudents(String location) throws SQLException { // increases number of students in 'locations' table
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select * from " + LOCATIONS_TABLE + ";");

        int count = 0, id = -1;
        while (rs.next()) {
            if (rs.getString("name").equals(location)) {
                count = Integer.parseInt(rs.getString("numStudents"));
                id = Integer.parseInt(rs.getString("id"));
                count--; // 1 less
                break;
            }
        }

        stmt.execute("delete from " + LOCATIONS_TABLE + " where id = " + id + ";");
        stmt.execute("insert into " + LOCATIONS_TABLE + " values ('" + id + "' , '" +
                location + "' , '" + count + "');");
    }


    //--------------------------------------------------------------------------------------------------------

    public void addStudentToLocation(String mail, String location) throws SQLException {
        Statement stmt = con.createStatement();
        UserById ubi = new UserById(bc);
        int user_id = ubi.getIdByMail(mail);
        stmt.execute("insert into " + MEMBERS_TABLE + " (location_id, user_id) values ('" +
               location + "' , '" + user_id + "');");
        increaseNumStudents(location);
    }


    public void removeStudentFromLocation(String mail, String location) throws SQLException {
        Statement stmt = con.createStatement();
        UserById ubi = new UserById(bc);
        int user_id = ubi.getIdByMail(mail);

        stmt.execute("delete from " + MEMBERS_TABLE + " where user_id = '" +
               user_id + "';");
        decreaseNumStudents(location);

        if (getNumStudents(location) == 0) removeLocation(location); // if no locations remaining, remove it
    }

    public int getNumStudents(String location) throws SQLException { // gets number of students for current location
        Statement stmt = con.createStatement();
        return Integer.parseInt(stmt.executeQuery("select count(*) as COUNT from " + LOCATIONS_TABLE + " where name = '" +
                location + "';").getString("COUNT"));
    }
}
