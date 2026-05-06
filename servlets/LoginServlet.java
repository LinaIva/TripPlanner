package servlets;

import dao.UserDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import util.UserTracker;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        UserDAO userDAO = new UserDAO();
        boolean valid = userDAO.validateUser(username, password);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        if (valid) {
            int userId = userDAO.getUserId(username);
            UserTracker.userLoggedIn(username);
            HttpSession session = request.getSession();
            session.setAttribute("userId", userId);
            session.setAttribute("username", username);
            session.setAttribute("role", "user");

            Cookie lastUserCookie = new Cookie("lastUsername", username);
            lastUserCookie.setMaxAge(60 * 60 * 24 * 7);
            response.addCookie(lastUserCookie);

            out.println("<h2>Login successful</h2>");
            out.println("<p>Welcome, " + username + "</p>");
            out.println("<p>Session ID: " + session.getId() + "</p>");
            out.println("<a href='trips'>Go to trips</a>");
        } else {
            out.println("<h2>Invalid username or password</h2>");
            out.println("<a href='index.html'>Try again</a>");
        }
    }
}