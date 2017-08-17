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

		JsonObject customerData = null;

		resp.setHeader("Access-Control-Allow-Origin", "*");

		if (Objects.isNull(q)) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		//Try converting to Integer
		try {
			cid = Integer.parseInt(q);
		} catch (NumberFormatException ex) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try (Connection conn = derbyDS.getConnection()) {
			PreparedStatement ps = conn.prepareStatement(QUERY);
			ps.setInt(1, cid);
			ResultSet rs = ps.executeQuery();
			if (! rs.next()) {
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			// { filmId: 1, title: "abc", description: "...." }
			customerData = Json.createObjectBuilder()
					.add("customerId", cid)
					.add("CustomerName", rs.getString("name"))
					.add("phone", rs.getString("phone"))
                                        .add("email", rs.getString("email"))
					.build();

		} catch (SQLException ex) {
			resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			log(ex.getMessage());
			return;
		}

		resp.setStatus(HttpServletResponse.SC_OK); //200
		resp.setContentType("application/json");

		try (PrintWriter pw = resp.getWriter()) {
			pw.println(customerData.toString());
		}
	}
	
}