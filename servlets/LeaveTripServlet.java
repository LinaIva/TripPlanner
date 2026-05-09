package servlets;

import dao.TripMemberDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;

@WebServlet("/leave-trip")
public class LeaveTripServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.html");
            return;
        }
        int userId = (int) session.getAttribute("userId");
        int tripId = Integer.parseInt(request.getParameter("tripId"));
        TripMemberDAO dao = new TripMemberDAO();
        dao.leaveTrip(tripId, userId);
        response.sendRedirect("trips");
    }
}