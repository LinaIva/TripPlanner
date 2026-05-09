package servlets;

import dao.FriendDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;

@WebServlet("/send-friend-request")
public class SendFriendRequestServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("index.html");
            return;
        }
        int senderId = (int) session.getAttribute("userId");
        int receiverId = Integer.parseInt(request.getParameter("receiverId"));
        FriendDAO dao = new FriendDAO();
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        if (senderId == receiverId) {
            out.println("<h2>You cannot add yourself</h2>");
        } else if (dao.areFriends(senderId, receiverId)) {
            out.println("<h2>This user is already your friend</h2>");
        } else if (dao.hasWaitingRequestBetween(senderId, receiverId)) {
            out.println("<h2>A friend request already exists</h2>");
        } else {
            dao.sendRequest(senderId, receiverId);
            out.println("<h2>Friend request sent</h2>");
        }
        out.println("<a href='friends'>Back to friends</a>");
        out.println("</body></html>");
    }
}
