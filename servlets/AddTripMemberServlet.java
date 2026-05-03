package servlets;

import dao.TripMemberDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;

@WebServlet("/add-trip-member")
public class AddTripMemberServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("index.html");
            return;
        }

        int tripId = Integer.parseInt(request.getParameter("tripId"));
        int friendId = Integer.parseInt(request.getParameter("friendId"));

        TripMemberDAO dao = new TripMemberDAO();
        dao.addMemberToTrip(tripId, friendId);

        response.sendRedirect("trip-details?tripId=" + tripId);
    }
}