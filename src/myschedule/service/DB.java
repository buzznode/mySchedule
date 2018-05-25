/*
 * The MIT License
 *
 * Copyright 2018 bradd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package myschedule.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import myschedule.model.CityModel;
import myschedule.model.CountryModel;
//import java.util.logging.Level;
//import java.util.logging.Logger;

/**
 * @author bradd
 * @version 0.5.0
 */
public class DB {
    Connection conn;
    String db;
    String driver;
    String dbPwd;
    String dbUser;
    PreparedStatement pstmt;
    ResultSet rs;
    Statement stmt;
    String url;
    private Logging log;
    
    /**
     * Default constructor
     */
    public DB() {
        conn = null;
        driver = "com.mysql.jdbc.Driver";
        db  = "U03MuY";
        dbUser = "U03MuY";
        dbPwd = "53688020218";
        url = "jdbc:mysql://52.206.157.109/" + db;
    }
    
    /**
     * Constructor taking log parameter
     * @param _log 
     */
    public DB(Logging _log) {
        log = _log;
        conn = null;
        driver = "com.mysql.jdbc.Driver";
        db  = "U03MuY";
        dbUser = "U03MuY";
        dbPwd = "53688020218";
        url = "jdbc:mysql://52.206.157.109/" + db;
    }
    
    /**
     * Connect to database
     */
    @SuppressWarnings("unchecked")
    protected void connect() {
        try {
            try {
                Class.forName(driver);
            }
            catch (ClassNotFoundException e) {
                log.write(Level.SEVERE, "DB driver error: " + e.toString());
            }
        
            conn = DriverManager.getConnection(url, dbUser, dbPwd);
            stmt = conn.createStatement();
        }
        catch (SQLException e) {
            log.write(Level.SEVERE, e.toString(), e);
            log.write(Level.SEVERE, "SQLException: {0}", e.getMessage());
            log.write(Level.SEVERE, "SQLState: {0}", e.getSQLState());
            log.write(Level.SEVERE, "VendorError: {0}", e.getErrorCode());
        }
    }

    /**
     * Execute SQL and return result
     * @param sql
     * @return ResultSet
     * @throws SQLException 
     */
    @SuppressWarnings("unchecked")
    protected ResultSet exec(String sql) throws SQLException {
        ResultSet rset = null;

        try {
            if (conn == null || conn.isClosed()) {
                connect();
            }
            rset =  stmt.executeQuery(sql);
        }
        catch (SQLException e) {
//            LOGGER.log(Level.SEVERE, e.toString(), e);
//            LOGGER.log(Level.SEVERE, "SQLException: {0}", e.getMessage());
//            LOGGER.log(Level.SEVERE, "SQLState: {0}", e.getSQLState());
//            LOGGER.log(Level.SEVERE, "VendorError: {0}", e.getErrorCode());
        }
        return rset;
    }

    /**
     * Finalize
     * @throws Throwable 
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void finalize() throws Throwable {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } 
        finally {
            super.finalize();
        }
    }
    
    /**
     * Get City list
     * @return CityModel OberservableList
     */
    @SuppressWarnings("unchecked")
    public ObservableList<CityModel> getCities() {
        ObservableList<CityModel> list = FXCollections.observableArrayList();
        String sql;
        connect();

        sql = String.join(" ",
            "SELECT a.cityId, a.city, b.country, a.createDate, a.createdBy, a.lastUpdate, ",
            "               a.lastUpdateBy",
            "  FROM city a",
            "  JOIN country b ON a.countryId = b.countryId"
        );
        
        try {
            rs = stmt.executeQuery(sql);
            rs.beforeFirst();
            
            while (rs.next()) {
                list.add(new CityModel(
                    rs.getInt("cityId"), rs.getString("city"),  rs.getString("country"), rs.getString("createDate"), 
                    rs.getString("createdBy"), rs.getString("lastUpdate"), rs.getString("lastUpdateBy")
                ));
            }
        }
        catch(SQLException ex) {
        }
        
        return list;
    }
    
    /**
     * Get Country list
     * @return CountryModel OberservableList
     */
    @SuppressWarnings("unchecked")
    public ObservableList<CountryModel> getCountries() {
        ObservableList<CountryModel> list = FXCollections.observableArrayList();
        String sql;
        connect();

        sql = String.join(" ",
            "SELECT countryId, country, createDate, createdBy, lastUpdate,",
            "       lastUpdateBy",
            "  FROM country"
        );
        
        try {
            rs = stmt.executeQuery(sql);
            rs.beforeFirst();
            
            while (rs.next()) {
                list.add(new CountryModel(
                    rs.getInt("countryId"), rs.getString("country"),
                    rs.getString("createDate"), rs.getString("createdBy"),
                    rs.getString("lastUpdate"), rs.getString("lastUpdateBy")
                ));
            }
        }
        catch(SQLException ex) {
        }
        
        return list;
    }

    /**
     * @param country
     * @return countryId
     */    
    @SuppressWarnings("unchecked")
    public int getCountryId(String country) {
        int countryId = 0;
        String sql;
        connect();

        sql = String.join(" ",
            "SELECT countryId",
            "  FROM country",
            "WHERE country = ?"
        );
            
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, country);
            rs = pstmt.executeQuery(sql);
            rs.beforeFirst();
            rs.next();
            countryId = rs.getInt("countryId");
        }
        catch (SQLException ex) {
        }
        
        return countryId;
    }
    
    /**
     * @param countryId
     * @return country
     */
    @SuppressWarnings("unchecked")
    public String getCountryName(int countryId) {
        String country = "";
        String sql;
        connect();

        sql = String.join(" ",
            "SELECT country",
            "  FROM country",
            "WHERE countryId = ?"
        );
        
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, countryId);
            rs = pstmt.executeQuery(sql);
            rs.beforeFirst();
            rs.next();
            country = rs.getString("country");
        }
        catch (SQLException ex) {
        }
        
        return country;
    }

    /**
     * @return Country names list
     */
    @SuppressWarnings("unchecked")
    public List getCountryNames() {
        ObservableList<String> list = FXCollections.observableArrayList();
        String sql;
        connect();

        sql = String.join(" ",
            "SELECT country",
            "  FROM country",
            " ORDER BY country"
        );
        
        try {
            rs = stmt.executeQuery(sql);
            rs.beforeFirst();
            
            while (rs.next()) {
                list.add(rs.getString("country"));
            }
        }
        catch (SQLException ex) {
        }
        
        return list;
    }
    
    /**
     * Execute query without result set
     * @param sql
     * @throws SQLException 
     */
    @SuppressWarnings("unchecked")
    protected void run(String sql) throws SQLException {
        try {
            if (conn == null || conn.isClosed()) {
                connect();
            }
        }
        catch (SQLException ex) {
            throw new SQLException(ex.getMessage());
        }

        stmt.execute(sql);
    }

    /**
     * Update city
     * @param list
     * @param country
     * @return boolean
     * @throws SQLException 
     */
    @SuppressWarnings("unchecked")
    public boolean updateCities(ObservableList<CityModel> list, String country) throws SQLException{
        int countryId = 0;
        String sql;
        
        try {
            sql = "TRUNCATE country";
            run(sql);
            countryId = getCountryId(country);
            
            sql = String.join(" ", 
                "INSERT ",
                "  INTO city (cityId, city, countryId, createDate, createdBy,",
                "             lastUpdate, lastUpdateBy)",
                "VALUES (?, ?, ?, ?, ?, ?, ?)"
            );
            
            pstmt = conn.prepareStatement(sql);
            
            for (CityModel c : list) {
                pstmt.setInt(1, c.getCityId());
                pstmt.setString(2, c.getCity());
                pstmt.setInt(3, countryId);
                pstmt.setString(4, c.getCreateDate());
                pstmt.setString(5, c.getCreatedBy());
                pstmt.setString(6, c.getLastUpdate());
                pstmt.setString(7, c.getLastUpdateBy());
                pstmt.executeUpdate();
            }
            
            return true;
        }
        catch (SQLException ex) {
            throw new SQLException(ex.getMessage());
        }
    }
    
    /**
     * Update country
     * @param list
     * @return boolean
     * @throws SQLException 
     */
    @SuppressWarnings("unchecked")
    public boolean updateCountries(ObservableList<CountryModel> list) throws SQLException{
        String sql;
        connect();
        
        try {
            sql = "TRUNCATE country";
            run(sql);
            
            sql = String.join(" ", 
                "INSERT ",
                "  INTO country (countryId, country, createDate, createdBy,",
                "                              lastUpdate, lastUpdateBy)",
                "VALUES (?, ?, ?,?, ?, ?)"
            );
            
            pstmt = conn.prepareStatement(sql);
            
            for (CountryModel c : list) {
                pstmt.setInt(1, c.getCountryId());
                pstmt.setString(2, c.getCountry());
                pstmt.setString(3, c.getCreateDate());
                pstmt.setString(4, c.getCreatedBy());
                pstmt.setString(5, c.getLastUpdate());
                pstmt.setString(6, c.getLastUpdateBy());
                pstmt.executeUpdate();
            }
            
            return true;
        }
        catch (SQLException ex) {
            throw new SQLException(ex.getMessage());
        }
    }
}  
