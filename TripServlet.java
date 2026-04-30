import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.sql.ResultSet;

@WebServlet("/trips")
public class TripServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("index.html");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String username = (String) session.getAttribute("username");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><body>");
        out.println("<h2>Your Trips</h2>");
        out.println("<p>Logged in as: " + username + "</p>");
        out.println("<p>Session ID: " + session.getId() + "</p>");

        out.println("<h3>Create new trip</h3>");
        out.println("<form action='trips' method='post'>");
        out.println("Title: <input type='text' name='title' required><br>");
        out.println("Destination: <input type='text' name='destination' required><br>");
        out.println("Start date: <input type='date' name='startDate' required><br>");
        out.println("End date: <input type='date' name='endDate' required><br>");
        out.println("<input type='submit' value='Create Trip'>");
        out.println("</form>");

        out.println("<h3>Saved trips</h3>");

        try {
            TripDAO tripDAO = new TripDAO();
            ResultSet rs = tripDAO.getTripsByUser(userId);

            out.println("<table border='1'>");
            out.println("<tr><th>ID</th><th>Title</th><th>Destination</th><th>Start</th><th>End</th></tr>");

            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getInt("id") + "</td>");
                out.println("<td>" + rs.getString("title") + "</td>");
                out.println("<td>" + rs.getString("destination") + "</td>");
                out.println("<td>" + rs.getDate("start_date") + "</td>");
                out.println("<td>" + rs.getDate("end_date") + "</td>");
                out.println("</tr>");
            }

            out.println("</table>");

        } catch (Exception e) {
            out.println("<p>Error loading trips</p>");
            e.printStackTrace();
        }

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

        int userId = (int) session.getAttribute("userId");

        String title = request.getParameter("title");
        String destination = request.getParameter("destination");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");

        TripDAO tripDAO = new TripDAO();
        tripDAO.addTrip(title, destination, startDate, endDate, userId);

        response.sendRedirect("trips");
    }
}