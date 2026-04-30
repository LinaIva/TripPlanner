package servlets;

import dao.FriendDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;

@WebServlet("/respond-friend-request")
public class RespondFriendRequestServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.html");
            return;
        }

        int requestId = Integer.parseInt(request.getParameter("id"));
        String action = request.getParameter("action");

        FriendDAO dao = new FriendDAO();

        if ("accept".equals(action)) {
            dao.acceptRequest(requestId);
        } else if ("decline".equals(action)) {
            dao.declineRequest(requestId);
        }

        response.sendRedirect("friend-requests");
    }
}