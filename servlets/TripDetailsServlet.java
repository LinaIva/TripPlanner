package servlets;

import dao.ChatMessageDAO;
import dao.ActivityDAO;
import dao.TripDAO;
import dao.TripMemberDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.sql.ResultSet;

@WebServlet("/trip-details")
public class TripDetailsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("index.html");
            return;
        }

        String tripIdParam = request.getParameter("tripId");

        if (tripIdParam != null) {
            session.setAttribute("currentTripId", Integer.parseInt(tripIdParam));
        }

        Integer tripId = (Integer) session.getAttribute("currentTripId");

        if (tripId == null) {
            response.sendRedirect("trips");
            return;
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        TripDAO tripDAO = new TripDAO();
        String tripTitle = tripDAO.getTripTitle(tripId);

        out.println("<html><body>");
        out.println("<h2>" + tripTitle + "</h2>");
        out.println("<p>Trip ID: " + tripId + "</p>");
        out.println("<p><a href='leave-trip?tripId=" + tripId + "'>Leave this trip</a></p>");
        out.println("<h3>Trip members</h3>");

        try {
            TripMemberDAO memberDAO = new TripMemberDAO();
            ResultSet members = memberDAO.getTripMembers(tripId);

            out.println("<ul>");
            while (members.next()) {
                out.println("<li>" + members.getString("username") + "</li>");
            }
            out.println("</ul>");

        } catch (Exception e) {
            out.println("<p>Error loading trip members</p>");
            e.printStackTrace();
        }
        out.println("<h3>Add friend to this trip</h3>");

        try {
            TripMemberDAO memberDAO = new TripMemberDAO();
            int currentUserId = (int) session.getAttribute("userId");

            ResultSet friends = memberDAO.getUserFriends(currentUserId);

            while (friends.next()) {
                int friendId = friends.getInt("id");
                String friendName = friends.getString("username");

                if (memberDAO.isMember(tripId, friendId)) {
                    out.println("<p>" + friendName + "  Already added</p>");
                } else {
                    out.println("<p>" + friendName +
                            " <a href='add-trip-member?tripId=" + tripId +
                            "&friendId=" + friendId + "'>Add to trip</a></p>");
                }
            }

        } catch (Exception e) {
            out.println("<p>Error loading friends</p>");
            e.printStackTrace();
        }
        out.println("<h3>Add plan</h3>");
        out.println("<form action='trip-details' method='post'>");
        out.println("Date: <input type='date' name='activityDate' required><br>");
        out.println("Type: <select name='type'>");
        out.println("<option value='Hotel'>Hotel</option>");
        out.println("<option value='Sightseeing'>Sightseeing</option>");
        out.println("<option value='Food'>Food</option>");
        out.println("<option value='Transport'>Transport</option>");
        out.println("<option value='Other'>Other</option>");
        out.println("</select><br>");
        out.println("Title: <input type='text' name='title' required><br>");
        out.println("Description: <input type='text' name='description'><br>");
        out.println("Price: <input type='number' step='0.01' name='price' value='0'><br>");
        out.println("<input type='submit' value='Add Activity'>");
        out.println("</form>");

        out.println("<h3>Planned activities</h3>");

        double total = 0;

        try {
            ActivityDAO dao = new ActivityDAO();
            ResultSet rs = dao.getActivitiesByTrip(tripId);

            out.println("<table border='1'>");
            out.println("<tr><th>Date</th><th>Type</th><th>Title</th><th>Description</th><th>Price</th></tr>");

            while (rs.next()) {
                double price = rs.getDouble("price");
                total += price;

                out.println("<tr>");
                out.println("<td>" + rs.getDate("activity_date") + "</td>");
                out.println("<td>" + rs.getString("type") + "</td>");
                out.println("<td>" + rs.getString("title") + "</td>");
                out.println("<td>" + rs.getString("description") + "</td>");
                out.println("<td>" + price + "</td>");
                out.println("</tr>");
            }

            out.println("</table>");
            out.println("<h3>Total price: " + total + "</h3>");

        } catch (Exception e) {
            out.println("<p>Error loading activities</p>");
            e.printStackTrace();
        }
        out.println("<br><a href='trips'>Back to trips</a>");
        out.println("<br><a href='logout'>Logout</a>");

        String username = (String) session.getAttribute("username");

        out.println("<style>");
        out.println("#chatBox { position: fixed; right: 20px; bottom: 70px; width: 300px; height: 350px; border: 1px solid black; background: white; display: none; padding: 10px; }");
        out.println("#messages { height: 240px; overflow-y: scroll; border: 1px solid gray; margin-bottom: 10px; padding: 5px; }");
        out.println("#chatButton { position: fixed; right: 20px; bottom: 20px; }");
        out.println("#newDot { color: red; display: none; font-size: 20px; }");
        out.println("</style>");

        out.println("<button id='chatButton' onclick='toggleChat()'>Chat <span id='newDot'>●</span></button>");

        out.println("<div id='chatBox'>");
        out.println("<button onclick='toggleChat()'>X</button>");
        out.println("<h3>Trip Chat</h3>");

        out.println("<div id='messages'>");

        try {
            ChatMessageDAO chatDAO = new ChatMessageDAO();
            ResultSet chatRs = chatDAO.getLastMessages(tripId);

            java.util.ArrayList<String> oldMessages = new java.util.ArrayList<>();

            while (chatRs.next()) {
                String user = chatRs.getString("username");
                String msg = chatRs.getString("message");
                oldMessages.add(user + ": " + msg);
            }

            for (int i = oldMessages.size() - 1; i >= 0; i--) {
                out.println("<p>" + oldMessages.get(i) + "</p>");
            }

        } catch (Exception e) {
            out.println("<p>Error loading chat history</p>");
            e.printStackTrace();
        }

        out.println("</div>");

        out.println("<input type='text' id='chatInput' placeholder='Message'>");
        out.println("<button onclick='sendMessage()'>Send</button>");
        out.println("</div>");

        out.println("<script>");
        out.println("let chatOpen = false;");
        out.println("let username = '" + username + "';");
        out.println("let ws = new WebSocket('ws://localhost:8080/tripplanner/trip-chat/" + tripId + "');");

        out.println("ws.onmessage = function(event) {");
        out.println("  let messages = document.getElementById('messages');");
        out.println("  let p = document.createElement('p');");
        out.println("  p.textContent = event.data;");
        out.println("  messages.appendChild(p);");
        out.println("  messages.scrollTop = messages.scrollHeight;");
        out.println("  if (!chatOpen) { document.getElementById('newDot').style.display = 'inline'; }");
        out.println("};");

        out.println("function toggleChat() {");
        out.println("  let box = document.getElementById('chatBox');");
        out.println("  chatOpen = !chatOpen;");
        out.println("  box.style.display = chatOpen ? 'block' : 'none';");
        out.println("  if (chatOpen) { document.getElementById('newDot').style.display = 'none'; }");
        out.println("}");

        out.println("function sendMessage() {");
        out.println("  let input = document.getElementById('chatInput');");
        out.println("  if (input.value.trim() !== '') {");
        out.println("    ws.send(username + ': ' + input.value);");
        out.println("    input.value = '';");
        out.println("  }");
        out.println("}");
        out.println("</script>");
        out.println("</body></html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect("index.html");
            return;
        }

        Integer tripId = (Integer) session.getAttribute("currentTripId");

        if (tripId == null) {
            response.sendRedirect("trips");
            return;
        }

        String activityDate = request.getParameter("activityDate");
        String type = request.getParameter("type");
        String title = request.getParameter("title");
        String description = request.getParameter("description");

        double price = 0;

        try {
            price = Double.parseDouble(request.getParameter("price"));
        } catch (Exception e) {
            price = 0;
        }

        ActivityDAO dao = new ActivityDAO();
        dao.addActivity(tripId, activityDate, type, title, description, price);

        response.sendRedirect("trip-details");
    }
}