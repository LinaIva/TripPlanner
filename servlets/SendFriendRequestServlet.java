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
        dao.sendRequest(senderId, receiverId);
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h2>Friend request sent</h2>");
        out.println("<a href='friends'>Back to friends</a>");
        out.println("</body></html>");
    }
}