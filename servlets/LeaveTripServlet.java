package servlets;

import dao.TripDAO;
import dao.TripMemberDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import util.PageRenderer;

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
        if (!dao.isMember(tripId, userId)) {
            response.sendRedirect("trips");
            return;
        }
        if (!"yes".equals(request.getParameter("confirm"))) {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            String tripTitle = new TripDAO().getTripTitle(tripId);
            PageRenderer.renderPageStart(out, session, "Leave Trip", "trips");
            out.println("<h2>Leave trip</h2>");
            out.println("<p>Are you sure you want to leave <strong>" + tripTitle + "</strong>?</p>");
            out.println("<p>This action will remove you from the trip members list.</p>");
            out.println("<p><a href='leave-trip?tripId=" + tripId + "&confirm=yes'>Yes, leave trip</a></p>");
            out.println("<p><a href='trip-details?tripId=" + tripId + "'>Cancel</a></p>");
            PageRenderer.renderPageEnd(out);
            return;
        }
        dao.leaveTrip(tripId, userId);
        response.sendRedirect("trips");
    }
}
