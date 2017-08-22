/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.customer.derby;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import javax.annotation.Resource;
//using imported JSONObject class and JSONArray class to facilitate displaying customers as JSON array 
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import model.Customer;
import org.json.JSONException;
/**
 *
 * @author viswath
 */
@WebServlet(urlPatterns = "/customers")
public class CustomersServlet extends HttpServlet {

	private static final String QUERY = "select * from customer";
        
	private @Resource(lookup = "jdbc/derby_sample")
	DataSource derbyDS;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
            
            JSONArray customers = new JSONArray();

		resp.setHeader("Access-Control-Allow-Origin", "*");

		try (Connection con = derbyDS.getConnection()) {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(QUERY);
                        
                        //while loop to loop through all the cusotmer records and convert them into customer
                        //JSON array
                            while (rs.next()) {
                            JSONObject c = new JSONObject();
                            try {
                            c.put("CustomerId", rs.getInt("customer_id"));
                            c.put("CustomerName", rs.getString("name"));
                            customers.put(c);
                            } catch (JSONException j) {
                            }
			}
		} catch (SQLException e) {
			resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			log(e.getMessage());
			return;
		}

		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentType("application/json");

                //printing customer array in JSON format
		try (PrintWriter p = resp.getWriter()) {
                    p.println(customers.toString());
                    }
        }
}	



