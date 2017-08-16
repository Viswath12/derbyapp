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

		try (Connection conn = derbyDS.getConnection()) {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(QUERY);
			while (rs.next()) {
                            JSONObject c = new JSONObject();
                            try {
                            c.put("CustomerId", rs.getInt("customer_id"));
                            c.put("CustomerName", rs.getString("name"));
                            customers.put(c);
                            } catch (JSONException j) {
                            }
			}
		} catch (SQLException ex) {
			resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			log(ex.getMessage());
			return;
		}

		resp.setStatus(HttpServletResponse.SC_OK); //200
		resp.setContentType("application/json");

		try (PrintWriter pw = resp.getWriter()) {
                    pw.println(customers.toString());
                    }
        }
}	



