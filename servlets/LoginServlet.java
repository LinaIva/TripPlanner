package servlets;

import dao.UserDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import util.UserTracker;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("username") != null) {
            response.sendRedirect("trips");
            return;
        }
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html><body>");
        out.println("<h2>Travel Planner App</h2>");
        out.println("<form action='login' method='post'>");
        out.println("Username: <input type='text' name='username'><br>");
        out.println("Password: <input type='password' name='password'><br>");
        out.println("<input type='submit' value='Login'>");
        out.println("</form>");
        out.println("<p>No account? <a href='register'>Register</a></p>");
        out.println("</body></html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
            boolean updatedCookie = false;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("lastUsername".equals(cookie.getName())) {
                        cookie.setValue(username);
                        cookie.setMaxAge(60 * 60 * 24 * 7);
                        response.addCookie(cookie);
                        updatedCookie = true;
                        break;
                    }
                }
            }
            if (!updatedCookie) {
                Cookie lastUserCookie = new Cookie("lastUsername", username);
                lastUserCookie.setMaxAge(60 * 60 * 24 * 7);
                response.addCookie(lastUserCookie);
            }
            out.println("<h2>Login successful</h2>");
            out.println("<p>Welcome, " + username + "</p>");
            out.println("<p>Session ID: " + session.getId() + "</p>");
            out.println("<a href='trips'>Go to trips</a>");
        } else {
            out.println("<h2>Invalid username or password</h2>");
            out.println("<a href='login'>Try again</a>");
        }
    }
}
