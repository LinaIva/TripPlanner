package servlets;

import dao.UserDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("username") != null) {
            response.sendRedirect("trips");
            return;
        }
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html><head><style>");
        out.println("body { font-family: sans-serif; }");
        out.println("input[type='submit'], button { padding: 2px 6px; border: 1px solid rgb(118,118,118); background: rgb(239,239,239); color: black; font: inherit; font-size: 13px; line-height: normal; cursor: pointer; }");
        out.println("</style></head><body>");
        out.println("<h2>Register</h2>");
        out.println("<form action='register' method='post'>");
        out.println("Username: <input type='text' name='username' required><br>");
        out.println("Password: <input type='password' name='password' required><br>");
        out.println("<input type='submit' value='Register'>");
        out.println("</form>");
        out.println("<br><a href='login'>Back to login</a>");
        out.println("</body></html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        UserDAO userDAO = new UserDAO();
        boolean success = userDAO.registerUser(username, password);
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html><head><style>");
        out.println("body { font-family: sans-serif; }");
        out.println("input[type='submit'], button { padding: 2px 6px; border: 1px solid rgb(118,118,118); background: rgb(239,239,239); color: black; font: inherit; font-size: 13px; line-height: normal; cursor: pointer; }");
        out.println("</style></head><body>");
        if (success) {
            out.println("<h2>Registration successful</h2>");
            out.println("<a href='login'>Go to login</a>");
        } else {
            out.println("<h2>Registration failed</h2>");
            out.println("<p>Username may already exist.</p>");
            out.println("<a href='register'>Try again</a>");
        }
        out.println("</body></html>");
    }
}
