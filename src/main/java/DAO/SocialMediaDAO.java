package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import java.util.List;

import Model.*;
import Util.ConnectionUtil;

/**
 * Database Access Object create sql statements and executes them to the database.
 * Methods have names based on the action being taken on the database (e.x select, drop, update, insert, etc.)
 * Turns strings into prepared statements. One reason to to this is to prevent sql injections by checking the input via prepared statement
 * The other is so it is easy to add variable with ?
 */
public class SocialMediaDAO {
    //put connection here so it doesn't declare it in each method scope.
    private Connection connection = ConnectionUtil.getConnection();

    /**
     *
     * @param account handles username and password parameters in the accout object
     * @return returns account with account_id field on success, null on failure
     */
    public Account selectAccount(Account account) {
        //Catches unexpected errors and returns null
        //sql command string
        String sql = "SELECT * FROM account WHERE username = ? AND password = ?";

        //turns string into prepared statement
        try (PreparedStatement ps = this.connection.prepareStatement(sql)) {

            //Adds values for ? fields
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());

            //Gets Result
            ResultSet rs = ps.executeQuery();

            //If there is a result then create object if it fails return null
            while(rs.next()) {
                Account returnAccount = new Account(rs.getInt("account_id"),
                    rs.getString("username"),
                    rs.getString("password"));
                return returnAccount;
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        //if not returned in the try block then it failed or doesn't exist
        return null;
    }
    /**
     * Adds an account to the database. Identical accounts are protected via the UNIQUE column constraint
     *
     * @param account takes an account model as parameter using
     * the overloaded constructor that doesn't take an account id
     * @return returns the account object with the assigned id now. Returns null on error
     * */
    public Account insertAccount(Account account) {

        //Catches unexpected errors and returns null
        //Sql string
        String sql = "INSERT INTO account (username, password) VALUES (?, ?);";

        //Turns into prepared statement
        try (PreparedStatement ps = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            //Adds values for ? fields
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());

            //Inserts into accounts table
            ps.executeUpdate();

            //Gets generated id for object return
            ResultSet rs = ps.getGeneratedKeys();

            //If rs exists then set id to account and return otherwise return null
            if(rs.next()){
                int account_id = (int) rs.getLong(1);
                account.setAccount_id(account_id);
                return account;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Deletes account record from database not in requirements
     * TODO work on once initial project is submitted
     * @param account
     * @return
     */
    public Account deleteAccount(Account account) {
        //Catches unexpected errors and returns null
        String sql = "DELETE FROM account WHERE account_id = ?";
        try (PreparedStatement ps = this.connection.prepareStatement(sql)) {
            ps.setInt(1, account.getAccount_id());
            int deleted = ps.executeUpdate();
            if(deleted > 0){
                return account;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    //
    public Message insertMessage(Message message) {
        //Catches unexpected errors and returns null
        //sql string
        String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";

        //creates prepared statement that can return the generated key
        try (PreparedStatement ps = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            //Add values to sql command
            ps.setInt(1, message.getPosted_by());
            ps.setString(2, message.getMessage_text());
            ps.setLong(3, message.getTime_posted_epoch());

            //inserts message
            int updated = ps.executeUpdate();

            //If updated is greater than 0 then it was inserted
            if(updated > 0){
                //sets id to message and returns it, null on fail or not inserted
                ResultSet rs = ps.getGeneratedKeys();
                if(rs.next()){
                    int message_id = (int) rs.getLong(1);
                    message.setMessage_id(message_id);
                    return message;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public Message selectMessage(int message_id) {
        //Catches unexpected errors and returns null
        //sql string
        String sql = "SELECT * FROM message WHERE message_id = ?";

        //turns it into a prepared statement
        try (PreparedStatement ps = this.connection.prepareStatement(sql)) {

            //Adds value for id
            ps.setInt(1, message_id);

            //gets result from select statement
            ResultSet rs = ps.executeQuery();
            //If there is a result, build and return object otherwise return null
            while(rs.next()){
                Message mesage = new Message(
                    rs.getInt("message_id"),
                    rs.getInt("posted_by"),
                    rs.getString("message_text"),
                    rs.getLong("time_posted_epoch"));
                    return mesage;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public List<Message> selectAllMessages() {
        List<Message> messages = new ArrayList<>();
        //Catches unexpected errors and returns null
        //sql statement
        String sql = "SELECT * FROM message ORDER BY message_id";

        //turns it into prepared statment
        try (PreparedStatement ps = this.connection.prepareStatement(sql)) {

            //Gets reuslts
            ResultSet rs = ps.executeQuery();

            //If results exist, itterate over all of them and build and return list of messages, null on failure
            while(rs.next()){
                Message mesage = new Message(
                    rs.getInt("message_id"),
                    rs.getInt("posted_by"),
                    rs.getString("message_text"),
                    rs.getLong("time_posted_epoch"));
                messages.add(mesage);
            }
            return messages;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public List<Message> selectAllMessagesFromAccountId(int account_id) {
        List<Message> messages = new ArrayList<>();
        //Catches unexpected errors and returns null
        //sql statement
        String sql = "SELECT * FROM message WHERE posted_by = ? ORDER BY message_id";

        //turns it into prepared statment
        try (PreparedStatement ps = this.connection.prepareStatement(sql)) {

            //Adds value for id
            ps.setInt(1, account_id);

            //Gets reuslts
            ResultSet rs = ps.executeQuery();

            //If results exist, itterate over all of them and build and return list of messages, null on failure
            while(rs.next()){
                Message mesage = new Message(
                    rs.getInt("message_id"),
                    rs.getInt("posted_by"),
                    rs.getString("message_text"),
                    rs.getLong("time_posted_epoch"));
                messages.add(mesage);
            }
            return messages;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public Message deleteMessage(int message_id){
        //Catches unexpected errors and returns null
        //First checks if message exists from select message if it does we need this to return deleted message
        Message message = selectMessage(message_id);
        if(message == null) {
            return null;
        }

        //sql statement
        String sql = "DELETE FROM message WHERE message_id = ?;";

        //turns it into prepared statment
        try (PreparedStatement ps = this.connection.prepareStatement(sql)) {

            //sets field value
            ps.setInt(1, message_id);

            //execute update and get returned int
            int deleted = ps.executeUpdate();

            //If int returned is more than 0 then a message was deleted
            if(deleted > 0){
                return message;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    //TODO Delete all messages by id
    //Helps deal with table reference if deleteing something referenced without using cascade
    //Auditing would also require a deleted messages table for admin use
    /**
     *
     * @param message_id id value of the message provided from the pathParam
     * @param message_text
     * @return
     */
    public Message updateMessage(int message_id, String message_text){
        //Catches unexpected errors and returns null
        //sql statement
        String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";

        //turns statement into prepared statement
        try (PreparedStatement ps = this.connection.prepareStatement(sql)) {

            //set field values
            ps.setString(1, message_text);
            ps.setInt(2, message_id);

            //gets returned int on update
            int updated = ps.executeUpdate();
            //If updated is more than 0, then  return message otherwise return null
            if(updated > 0){
                return selectMessage(message_id);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


}
