package servlets;

import dao.FriendDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.sql.ResultSet;
import util.PageRenderer;

@WebServlet("/friend-requests")
public class FriendRequestsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.html");
            return;
        }
        int userId = (int) session.getAttribute("userId");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        PageRenderer.renderPageStart(out, session, "Friend Requests", "friends");
        out.println("<style>");
        out.println(".request-row { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }");
        out.println("</style>");
        out.println("<h2>Friend Requests</h2>");
        try {
            FriendDAO dao = new FriendDAO();
            ResultSet rs = dao.getWaitingRequests(userId);
            boolean hasRequests = false;
            while (rs.next()) {
                hasRequests = true;
                int requestId = rs.getInt("id");
                String username = rs.getString("username");
                out.println("<div class='request-row'><span>" + username + "</span>" +
                        "<a class='ui-button small' href='respond-friend-request?action=accept&id=" + requestId + "'>Accept</a>" +
                        "<a class='ui-button small' href='respond-friend-request?action=decline&id=" + requestId + "'>Decline</a></div>");
            }
            if (!hasRequests) out.println("<p>No new friend requests right now.</p>");
        } catch (Exception e) {
            out.println("<p>Couldn't load friend requests right now.</p>");
            e.printStackTrace();
        }
        out.println("<br><a href='friends'>Back to friends</a>");
        PageRenderer.renderPageEnd(out);
    }
}
