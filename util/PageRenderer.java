package util;

import jakarta.servlet.http.HttpSession;
import java.io.PrintWriter;

public final class PageRenderer {

    private PageRenderer() { }

    public static void renderPageStart(PrintWriter out, HttpSession session, String title, String activePage) {
        String username = (String) session.getAttribute("username");
        Object userId = session.getAttribute("userId");
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>" + title + "</title>");
        out.println("<style>");
        out.println("body { margin: 0; font-family: sans-serif; }");
        out.println(".page-shell { padding: 24px; }");
        out.println(".topbar { display: flex; justify-content: space-between; align-items: flex-start; gap: 24px; padding: 20px 24px 16px; border-bottom: 1px solid black; }");
        out.println(".brand-block { display: flex; flex-direction: column; gap: 8px; }");
        out.println(".brand-title { font-size: 28px; font-weight: bold; line-height: 1; }");
        out.println(".user-menu details { display: inline-block; }");
        out.println(".user-menu summary { cursor: pointer; list-style: none; }");
        out.println(".user-menu summary::-webkit-details-marker { display: none; }");
        out.println(".user-name { font-size: 15px; text-decoration: underline; }");
        out.println(".user-panel { margin-top: 8px; padding: 10px 12px; border: 1px solid black; min-width: 180px; }");
        out.println(".user-panel p { margin: 0 0 6px; }");
        out.println(".user-panel p:last-child { margin-bottom: 0; }");
        out.println(".nav-links { display: flex; align-items: center; gap: 12px; }");
        out.println(".nav-link { display: inline-block; padding: 8px 14px; border: 1px solid black; text-decoration: none; color: inherit; }");
        out.println(".nav-link.active { font-weight: bold; }");
        out.println("h2, h3 { margin-top: 0; }");
        out.println("table { border-collapse: collapse; }");
        out.println("th, td { padding: 8px 10px; }");
        out.println("</style></head><body>");
        out.println("<header class='topbar'>");
        out.println("<div class='brand-block'>");
        out.println("<div class='brand-title'>Trip Planner</div>");
        out.println("<div class='user-menu'><details>");
        out.println("<summary class='user-name'>" + username + "</summary>");
        out.println("<div class='user-panel'>");
        out.println("<p>User ID: " + userId + "</p>");
        out.println("<p>Session ID: " + session.getId() + "</p>");
        out.println("</div>");
        out.println("</details></div>");
        out.println("</div>");
        out.println("<nav class='nav-links'>");
        out.println("<a class='nav-link" + ("trips".equals(activePage) ? " active" : "") + "' href='trips'>My trips</a>");
        out.println("<a class='nav-link" + ("friends".equals(activePage) ? " active" : "") + "' href='friends'>Friends</a>");
        out.println("</nav>");
        out.println("</header>");
        out.println("<main class='page-shell'>");
    }

    public static void renderPageEnd(PrintWriter out) {
        out.println("</main></body></html>");
    }
}
