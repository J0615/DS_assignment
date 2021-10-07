import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class SkierServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/plain");
        String urlPath = req.getPathInfo();

        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("missing paramterers");
            return;
        }

        String[] urlParts = urlPath.split("/");
        if (!isUrlValid(urlParts)) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            res.setStatus(HttpServletResponse.SC_OK);
            if (urlParts.length == 8) {
                res.setContentType("text/html");
                PrintWriter out = res.getWriter();
                out.println("<h1>" + "It worked" + "</h1>");
            }

            if (urlParts.length == 3) {
                res.setContentType("text/html");
                PrintWriter out = res.getWriter();
                out.println("<h1>" + "It worked" + "</h1>");
            }
        }

    }


    private boolean isUrlValid(String[] urlPath) {
        if (urlPath.length == 8) {
            if (urlPath[2].equals("seasons") && urlPath[4].equals("days") && urlPath[6].equals(
                    "skiers") && isNumeric(urlPath[1]) && isNumeric(urlPath[3])
                    && isNumeric(urlPath[5]) && isNumeric(urlPath[7]) && Integer.parseInt(urlPath[5]) < 366
                    && Integer.parseInt(urlPath[5]) >= 1) {
                return true;
            }
        }

        if (urlPath.length == 3) {
            return urlPath[2].equals("vertical") && isNumeric(urlPath[1]);
        }
        return false;
    }

    private static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String urlPath = req.getPathInfo();
        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("missing paramterers");
            return;
        }
        String[] urlParts = urlPath.split("/");

        if (!isUrlValid(urlParts)) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            res.setStatus(201);
            if (urlParts.length == 8) {
                res.setContentType("text/html");
                PrintWriter out = res.getWriter();
                out.println("<h1>" + "It worked" + "</h1>");
            }

            if (urlParts.length == 3) {
                res.setContentType("text/html");
                PrintWriter out = res.getWriter();
                out.println("<h1>" + "It worked" + "</h1>");
            }
        }
    }
}
