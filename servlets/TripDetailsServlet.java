package servlets;

import dao.ActivityDAO;
import dao.TripDAO;
import dao.TripMemberDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.sql.ResultSet;

@WebServlet("/trip-details")
public class TripDetailsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("index.html");
            return;
        }

        String tripIdParam = request.getParameter("tripId");

        if (tripIdParam != null) {
            session.setAttribute("currentTripId", Integer.parseInt(tripIdParam));
        }

        Integer tripId = (Integer) session.getAttribute("currentTripId");

        if (tripId == null) {
            response.sendRedirect("trips");
            return;
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        TripDAO tripDAO = new TripDAO();
        String tripTitle = tripDAO.getTripTitle(tripId);

        out.println("<html><body>");
        out.println("<h2>" + tripTitle + "</h2>");
        out.println("<p>Trip ID: " + tripId + "</p>");
        out.println("<p><a href='leave-trip?tripId=" + tripId + "'>Leave this trip</a></p>");
        out.println("<h3>Trip members</h3>");

        try {
            TripMemberDAO memberDAO = new TripMemberDAO();
            ResultSet members = memberDAO.getTripMembers(tripId);

            out.println("<ul>");
            while (members.next()) {
                out.println("<li>" + members.getString("username") + "</li>");
            }
            out.println("</ul>");

        } catch (Exception e) {
            out.println("<p>Error loading trip members</p>");
            e.printStackTrace();
        }
        out.println("<h3>Add friend to this trip</h3>");

        try {
            TripMemberDAO memberDAO = new TripMemberDAO();
            int currentUserId = (int) session.getAttribute("userId");

            ResultSet friends = memberDAO.getUserFriends(currentUserId);

            while (friends.next()) {
                int friendId = friends.getInt("id");
                String friendName = friends.getString("username");

                if (memberDAO.isMember(tripId, friendId)) {
                    out.println("<p>" + friendName + " ✅ Already added</p>");
                } else {
                    out.println("<p>" + friendName +
                            " <a href='add-trip-member?tripId=" + tripId +
                            "&friendId=" + friendId + "'>Add to trip</a></p>");
                }
            }

        } catch (Exception e) {
            out.println("<p>Error loading friends</p>");
            e.printStackTrace();
        }
        out.println("<h3>Add plan</h3>");
        out.println("<form action='trip-details' method='post'>");
        out.println("Date: <input type='date' name='activityDate' required><br>");
        out.println("Type: <select name='type'>");
        out.println("<option value='Hotel'>Hotel</option>");
        out.println("<option value='Sightseeing'>Sightseeing</option>");
        out.println("<option value='Food'>Food</option>");
        out.println("<option value='Transport'>Transport</option>");
        out.println("<option value='Other'>Other</option>");
        out.println("</select><br>");
        out.println("Title: <input type='text' name='title' required><br>");
        out.println("Description: <input type='text' name='description'><br>");
        out.println("Price: <input type='number' step='0.01' name='price' value='0'><br>");
        out.println("<input type='submit' value='Add Activity'>");
        out.println("</form>");

        out.println("<h3>Planned activities</h3>");

        double total = 0;

        try {
            ActivityDAO dao = new ActivityDAO();
            ResultSet rs = dao.getActivitiesByTrip(tripId);

            out.println("<table border='1'>");
            out.println("<tr><th>Date</th><th>Type</th><th>Title</th><th>Description</th><th>Price</th></tr>");

            while (rs.next()) {
                double price = rs.getDouble("price");
                total += price;

                out.println("<tr>");
                out.println("<td>" + rs.getDate("activity_date") + "</td>");
                out.println("<td>" + rs.getString("type") + "</td>");
                out.println("<td>" + rs.getString("title") + "</td>");
                out.println("<td>" + rs.getString("description") + "</td>");
                out.println("<td>" + price + "</td>");
                out.println("</tr>");
            }

            out.println("</table>");
            out.println("<h3>Total price: " + total + "</h3>");

        } catch (Exception e) {
            out.println("<p>Error loading activities</p>");
            e.printStackTrace();
        }

        out.println("<br><a href='trips'>Back to trips</a>");
        out.println("<br><a href='logout'>Logout</a>");
        out.println("</body></html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("index.html");
            return;
        }

        Integer tripId = (Integer) session.getAttribute("currentTripId");

        if (tripId == null) {
            response.sendRedirect("trips");
            return;
        }

        String activityDate = request.getParameter("activityDate");
        String type = request.getParameter("type");
        String title = request.getParameter("title");
        String description = request.getParameter("description");

        double price = 0;

        try {
            price = Double.parseDouble(request.getParameter("price"));
        } catch (Exception e) {
            price = 0;
        }

        ActivityDAO dao = new ActivityDAO();
        dao.addActivity(tripId, activityDate, type, title, description, price);

        response.sendRedirect("trip-details");
    }
}