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
import jpa.TripNoteEntity;
import jpa.TripNoteService;
import java.util.List;
import util.PageRenderer;

@WebServlet("/trip-details")
public class TripDetailsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
        int currentUserId = (int) session.getAttribute("userId");
        TripMemberDAO accessDAO = new TripMemberDAO();
        if (!accessDAO.isMember(tripId, currentUserId)) {
            session.removeAttribute("currentTripId");
            response.sendRedirect("trips");
            return;
        }
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        TripDAO tripDAO = new TripDAO();
        String tripTitle = tripDAO.getTripTitle(tripId);
        String tripDateRange = tripDAO.getTripDateRange(tripId);
        PageRenderer.renderPageStart(out, session, tripTitle, "trips");
        out.println("<style>");
        out.println(".section-header { display: flex; justify-content: space-between; align-items: center; gap: 16px; margin: 24px 0 12px; }");
        out.println(".section-header h3 { margin: 0; }");
        out.println(".action-dropdown details { display: inline-block; }");
        out.println(".action-dropdown summary { cursor: pointer; list-style: none; border: 1px solid black; padding: 8px 14px; }");
        out.println(".action-dropdown summary::-webkit-details-marker { display: none; }");
        out.println(".action-panel { margin-top: 8px; padding: 12px; border: 1px solid black; min-width: 320px; }");
        out.println(".action-panel p { margin: 0 0 8px; }");
        out.println(".action-panel form { margin: 0; }");
        out.println(".action-panel input, .action-panel select { margin-bottom: 8px; }");
        out.println(".members-list, .notes-list { padding-left: 18px; }");
        out.println(".friend-option { display: flex; justify-content: space-between; align-items: center; gap: 12px; margin-bottom: 8px; }");
        out.println(".friend-option:last-child { margin-bottom: 0; }");
        out.println(".inline-link { white-space: nowrap; }");
        out.println(".delete-form { margin: 0; }");
        out.println(".delete-form input { margin: 0; }");
        out.println(".note-row { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }");
        out.println(".trip-title-row { display: flex; align-items: center; gap: 10px; }");
        out.println("</style>");
        out.println("<div class='trip-title-row'><h2 style='margin:0;'>" + tripTitle + "</h2><button id='chatButton' onclick='toggleChat()'>Chat <span id='newDot'>●</span></button></div>");
        out.println("<p>" + tripDateRange + "</p>");
//        out.println("<p>Trip ID: " + tripId + "</p>");
        out.println("<p><a href='leave-trip?tripId=" + tripId + "'>Leave this trip</a></p>");
        out.println("<h3>Trip members</h3>");
        try {
            TripMemberDAO memberDAO = new TripMemberDAO();
            ResultSet members = memberDAO.getTripMembers(tripId);
            out.println("<ul class='members-list'>");
            while (members.next()) {
                out.println("<li>" + members.getString("username") + "</li>");
            }
            out.println("</ul>");
        } catch (Exception e) {
            out.println("<p>Couldn't load trip members right now.</p>");
            e.printStackTrace();
        }
        out.println("<div class='action-dropdown'><details><summary>Add friend</summary><div class='action-panel'>");
        try {
            TripMemberDAO memberDAO = new TripMemberDAO();
            ResultSet friends = memberDAO.getUserFriends(currentUserId);
            boolean hasFriendOption = false;
            while (friends.next()) {
                int friendId = friends.getInt("id");
                String friendName = friends.getString("username");
                if (memberDAO.isMember(tripId, friendId)) continue;
                hasFriendOption = true;
                out.println("<div class='friend-option'><span>" + friendName + "</span>" +
                        "<a class='inline-link' href='add-trip-member?tripId=" + tripId +
                        "&friendId=" + friendId + "'>Add to trip</a></div>");
            }
            if (!hasFriendOption) out.println("<p>All your friends are already in this trip.</p>");
        } catch (Exception e) {
            out.println("<p>Couldn't load your friends right now.</p>");
            e.printStackTrace();
        }
        out.println("</div></details></div>");
        out.println("<br><h3>Trip Notes</h3>");

        try {
            TripNoteService noteService = new TripNoteService();
            List<TripNoteEntity> notes = noteService.getNotesByTrip(tripId);
            out.println("<form action='trip-details' method='post'>");
            out.println("<input type='hidden' name='action' value='addNote'>");
            out.println("Note: <input type='text' name='noteText' required>");
            out.println("<input type='submit' value='Add Note'>");
            out.println("</form>");
            if (notes.isEmpty()) out.println("<p>No notes yet. Add the first one above.</p>");
            else out.println("<ul class='notes-list'>");
            for (TripNoteEntity note : notes) {
                out.println("<li><div class='note-row'><span>" + note.getNoteText() + "</span>" +
                        "<form class='delete-form' action='trip-details' method='post'>" +
                        "<input type='hidden' name='action' value='deleteNote'>" +
                        "<input type='hidden' name='noteId' value='" + note.getId() + "'>" +
                        "<input type='submit' value='Delete'></form></div></li>");
            }
            if (!notes.isEmpty()) out.println("</ul>");
        } catch (Throwable e) {
            out.println("<p style='color:red;'>Couldn't load trip notes right now.</p>");
            e.printStackTrace();
        }
        out.println("<h3>Planned activities</h3>");
        double total = 0;
        try {
            ActivityDAO dao = new ActivityDAO();
            ResultSet rs = dao.getActivitiesByTrip(tripId);
            boolean hasActivities = false;
            while (rs.next()) {
                if (!hasActivities) {
                    hasActivities = true;
                    out.println("<table border='1'>");
                    out.println("<tr><th>Date</th><th>Type</th><th>Title</th><th>Description</th><th>Price</th><th></th></tr>");
                }
                double price = rs.getDouble("price");
                total += price;
                out.println("<tr>");
                out.println("<td>" + rs.getDate("activity_date") + "</td>");
                out.println("<td>" + rs.getString("type") + "</td>");
                out.println("<td>" + rs.getString("title") + "</td>");
                out.println("<td>" + rs.getString("description") + "</td>");
                out.println("<td>" + price + "</td>");
                out.println("<td><form class='delete-form' action='trip-details' method='post'>" +
                        "<input type='hidden' name='action' value='deleteActivity'>" +
                        "<input type='hidden' name='activityId' value='" + rs.getInt("id") + "'>" +
                        "<input type='submit' value='Delete'></form></td>");
                out.println("</tr>");
            }
            if (hasActivities) out.println("</table>");
            else out.println("<p>No trip plans yet. Add the first one below.</p>");
            out.println("<br><h3>Total price: " + total + "</h3>");
        } catch (Exception e) {
            out.println("<p>Couldn't load trip plans right now.</p>");
            e.printStackTrace();
        }
        out.println("<div class='action-dropdown'><details><summary>Add plan</summary><div class='action-panel'>");
        out.println("<form action='trip-details' method='post'>");
        out.println("<div>Date:</div><input type='date' name='activityDate' required><br>");
        out.println("<div>Type:</div><select name='type'>");
        out.println("<option value='Hotel'>Hotel</option>");
        out.println("<option value='Sightseeing'>Sightseeing</option>");
        out.println("<option value='Food'>Food</option>");
        out.println("<option value='Transport'>Transport</option>");
        out.println("<option value='Other'>Other</option>");
        out.println("</select><br>");
        out.println("<div>Title:</div><input type='text' name='title' required><br>");
        out.println("<div>Description:</div><input type='text' name='description'><br>");
        out.println("<div>Price:</div><input type='number' step='0.01' name='price' value='0'><br>");
        out.println("<input type='submit' value='Add Activity'>");
        out.println("</form>");
        out.println("</div></details></div>");
        out.println("<br><a href='trips'>Back to my trips</a>");
        out.println("<br><a href='logout'>Log out</a>");
        String username = (String) session.getAttribute("username");
        out.println("<style>");
        out.println("#chatBox { position: fixed; right: 20px; top: 120px; width: 300px; height: 350px; border: 1px solid black; background: white; display: none; padding: 10px; }");
        out.println("#messages { height: 240px; overflow-y: scroll; border: 1px solid gray; margin-bottom: 10px; padding: 5px; }");
        out.println("#newDot { color: red; display: none; font-size: 20px; }");
        out.println("</style>");
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
            out.println("<p>Couldn't load chat history right now.</p>");
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
        PageRenderer.renderPageEnd(out);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
        int currentUserId = (int) session.getAttribute("userId");
        TripMemberDAO accessDAO = new TripMemberDAO();
        if (!accessDAO.isMember(tripId, currentUserId)) {
            session.removeAttribute("currentTripId");
            response.sendRedirect("trips");
            return;
        }
        String action = request.getParameter("action");
        if ("addNote".equals(action)) {
            String noteText = request.getParameter("noteText");
            TripNoteService noteService = new TripNoteService();
            noteService.addNote(tripId, noteText);
            response.sendRedirect("trip-details");
            return;
        }
        if ("deleteNote".equals(action)) {
            try {
                int noteId = Integer.parseInt(request.getParameter("noteId"));
                TripNoteService noteService = new TripNoteService();
                noteService.deleteNote(noteId, tripId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            response.sendRedirect("trip-details");
            return;
        }
        if ("deleteActivity".equals(action)) {
            try {
                int activityId = Integer.parseInt(request.getParameter("activityId"));
                ActivityDAO dao = new ActivityDAO();
                dao.deleteActivity(activityId, tripId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            response.sendRedirect("trip-details");
            return;
        }
        String activityDate = request.getParameter("activityDate");
        String type = request.getParameter("type");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        TripDAO tripDAO = new TripDAO();
        if (!tripDAO.isActivityDateWithinTrip(tripId, activityDate)) {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            PageRenderer.renderPageStart(out, session, "Invalid activity date", "trips");
            out.println("<h2>That date doesn't fit this trip</h2>");
            out.println("<p>Please choose a plan date that is inside the trip date range.</p>");
            out.println("<p><a href='trip-details?tripId=" + tripId + "'>Back to trip</a></p>");
            PageRenderer.renderPageEnd(out);
            return;
        }
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
