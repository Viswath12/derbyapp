/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.customer.derby;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import javax.annotation.Resource;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author viswath
 */

@WebServlet(urlPatterns = "/customersdetails")
public class CustomerDetailsServlet extends HttpServlet {

	private static final String QUERY = "select * from customer where customer_id = ?";
        
	private @Resource(lookup = "jdbc/derby_sample")
	DataSource derbyDS;
        
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
           
            String q = req.getParameter("cid");
		
            Integer cid = null;

                resp.setHeader("Access-Control-Allow-Origin", "*");
                
		JsonObject customerData = null;		

                //checking if query string is passed or not
		if (Objects.isNull(q)) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try {
			cid = Integer.parseInt(q);
		} catch (NumberFormatException e) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try (Connection con = derbyDS.getConnection()) {
			PreparedStatement ps = con.prepareStatement(QUERY);
			ps.setInt(1, cid);
			ResultSet rs = ps.executeQuery();
			if (! rs.next()) {
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			customerData = Json.createObjectBuilder()
					.add("customerId", rs.getString("customer_id"))
					.add("CustomerName", rs.getString("name"))
					.add("phone", rs.getString("phone"))
                                        .add("email", rs.getString("email"))
					.build();

		} catch (SQLException e) {
			resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			log(e.getMessage());
			return;
		}

		resp.setStatus(HttpServletResponse.SC_OK);
		
                resp.setContentType("application/json");

                
                //printing customer JSON object based on the customer id
		try (PrintWriter p = resp.getWriter()) {
			p.println(customerData.toString());
		}
	}
	
}