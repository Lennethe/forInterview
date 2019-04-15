import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class AnimationListServlet extends HttpServlet {

    private String _hostname = null;
    private String _dbname = null;
    private String _username = null;
    private String _password = null;

    public void init() throws ServletException {
        // iniファイルから自分のデータベース情報を読み込む
        String iniFilePath = getServletConfig().getServletContext()
                .getRealPath("WEB-INF/le4db.ini");
        try {
            FileInputStream fis = new FileInputStream(iniFilePath);
            Properties prop = new Properties();
            prop.load(fis);
            _hostname = prop.getProperty("hostname");
            _dbname = prop.getProperty("dbname");
            _username = prop.getProperty("username");
            _password = prop.getProperty("password");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String url = "";
        String user_id = request.getParameter("user_id");
        String user_name = request.getParameter("user_name");

        out.println("<html>");
        out.println("<body>");

        out.println("<h3>検索</h3>");
        out.println("<form action=\"search_animation\"  method=\"GET\">");
        out.println("作品名： ");
        out.println("<input type=\"text\" name=\"animation_name\" />");
        out.println("<br/>");
        out.println("<input type=\"submit\" value=\"検索\"/>");
        out.println("<br/>");
        out.println("<input type=\"hidden\" name=\"user_id\" value=" + user_id + " />");
        out.println("<br/>");
        out.println("<input type=\"hidden\" name=\"user_name\" value=" + user_name + " />");
        out.println("</form>");

        out.println("<h3>一覧</h3>");
        Connection conn = null;
        Statement stmt = null;
        Statement stmt1 = null;

        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
                    + ":5432/" + _dbname, _username, _password);
            stmt = conn.createStatement();
            stmt1 = conn.createStatement();


            out.println("<table border=\"1\">");
            out.println("<tr><th>作品名</th><th>制作会社</th><th>登録数</th><th>視聴数</th>" +
                                        "<th></th><th></th><th>登録済</th></tr>");

            ResultSet rs = stmt.executeQuery("SELECT * FROM anime_company");
            while (rs.next()) {
                String anime = rs.getString("anime");
                String company = rs.getString("company");

                ResultSet rs1 = stmt1.executeQuery("SELECT anime, count(*) from user_history" +
                        "                           WHERE anime = '" + anime +
                                                    "' group by anime");
                String num_anime = "0";
                String watched_anime = "0";
                while(rs1.next()){
                    num_anime = rs1.getString("count");
                }
                rs1.close();
                ResultSet rs2 = stmt1.executeQuery("SELECT anime, count(*) from user_history" +
                        "                           WHERE anime = '" + anime +
                                                    "' AND history = '〇'" +
                                                    "group by anime");
                while(rs2.next()){
                    watched_anime = rs2.getString("count");
                }
                rs2.close();

                ResultSet rs3 = stmt1.executeQuery("SELECT * FROM user_history" +
                                            " WHERE user_id = " + user_id +
                                            " AND anime = '" + anime + "'");
                boolean flag_watch = false;
                while(rs3.next()){
                    String flag_anime = rs3.getString("anime");
                    if(flag_anime.equals(anime)){
                        flag_watch = true;
                    }
                }


                out.println("<tr>");
                out.println("<td>" + anime + "</td>");
                out.println("<td>" + company + "</td>");
                out.println("<td>" + num_anime + "</td>");
                out.println("<td>" + watched_anime + "</td>");
                out.println("<form action=\"add_history\" method=\"GET\">");
                out.println("<td><input type=\"submit\" value=\"登録\"></td>");
                out.println("<input type=\"hidden\" name=\"anime\" value=" + anime + " />");
                out.println("<input type=\"hidden\" name=\"user_id\" value=" + user_id + " />");
                out.println("<input type=\"hidden\" name=\"user_name\" value=" + user_name + " />");
                out.println("</form>");

                out.println("<form action=\"item_animation\" method=\"GET\">");
                out.println("<td><input type=\"submit\" value=\"編集\"></td>");
                out.println("<input type=\"hidden\" name=\"anime\" value=" + anime + " />");
                out.println("<input type=\"hidden\" name=\"user_id\" value=" + user_id + " />");
                out.println("<input type=\"hidden\" name=\"user_name\" value=" + user_name + " />");
                out.println("</form>");

                if(flag_watch){
                    out.println("<td>済</td>");
                }
                else{
                    out.println("<td>未</td>");
                }

                out.println("</tr>");
            }
            rs.close();

            out.println("</table>");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        out.println("<h3>追加</h3>");
        out.println("空列は含まないでね<br/>");
        out.println("作品名は必須です");
        out.println("<form action=\"add_animation\" method=\"GET\">");
        out.println("作品名： ");
        out.println("<input type=\"text\" name=\"add_animation\"/>");
        out.println("<br/>");
        out.println("制作会社： ");
        out.println("<input type=\"text\" name=\"add_company\"/>");
        out.println("<br/>");
        out.println("<input type=\"submit\" value=\"追加\"/>");
        out.println("<input type=\"hidden\" name=\"user_id\" value=" + user_id + " />");
        out.println("<input type=\"hidden\" name=\"user_name\" value=" + user_name + " />");
        out.println("</form>");

        url = "?user_id=" + user_id + "&user_name=" + user_name;
        out.println("<br/>");
        out.println("<a href=\"my_page" + url + "\">マイページに戻る</a>");
        out.println("</body>");
        out.println("</html>");
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    public void destroy() {
    }

}
