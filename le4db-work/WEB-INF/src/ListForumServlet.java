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
public class ListForumServlet extends HttpServlet {

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

        out.println("<html>");
        out.println("<body>");

        out.println("<h3>検索</h3>");
        out.println("<form action=\"search_forum\" method=\"GET\">");
        out.println("作品名： ");
        out.println("<input type=\"text\" name=\"search_name\"/>");
        out.println("<br/>");
        out.println("<input type=\"submit\" value=\"検索\"/>");
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
            out.println("<tr><th>作品名</th><th>スレッド名</th><th>コメント数</th></tr>");

            ResultSet rs = stmt.executeQuery("SELECT * FROM id_anime_forum_name");
            while (rs.next()) {
                int forum_id = rs.getInt("forum_id");
                String anime = rs.getString("anime");
                String forum_name = rs.getString("forum_name");

                ResultSet rs1 = stmt1.executeQuery("SELECT forum_id, count(*) from id_to_impress" +
                        "   WHERE forum_id ="+ forum_id +
                        "   group by forum_id");
                String num_comment = "0";
                while(rs1.next()){
                    num_comment = rs1.getString("count");
                }
                rs1.close();

                out.println("<tr>");
                out.println("<td>" + anime + "</td>");
                out.println("<td><a href=\"forum?forum_id=" + forum_id +
                        "&anime=" + anime +
                        "&forum_name=" + forum_name + "\">" + forum_name
                        + "</a></td>");
                out.println("<td>" + num_comment + "</td>");
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

        out.println("<h3>掲示板追加</h3>");
        out.println("<form action=\"add_forum\" method=\"GET\">");
        out.println("作品名： ");
        out.println("<input type=\"text\" name=\"anime\"/>");
        out.println("<br/>");
        out.println("スレッド名： ");
        out.println("<input type=\"text\" name=\"forum_name\" value=\"無題\"/>");
        out.println("<br/>");
        out.println("<input type=\"submit\" value=\"追加\"/>");
        out.println("</form>");


        out.println("<br/>");
        out.println("<a href=\"login\">トップ画面に戻る</a>");

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
