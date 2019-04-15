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
public class MyPageServlet extends HttpServlet {

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

        String user_id = request.getParameter("user_id");
        String user_name = request.getParameter("user_name");


        out.println("<html>");
        out.println("<body>");

        out.println("<h2>マイページ</h2>");
        out.println("ここは"+user_name+"さんのページです<br/>");
        out.println("id="+user_id+"<br/>");
        out.println("<a href=\"item_account?" + "user_id=" + user_id + "&user_name="+ user_name +"\">プロフィールの編集へ</a>");

        out.println("<h3>視聴歴</h3>");
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
                    + ":5432/" + _dbname, _username, _password);
            stmt = conn.createStatement();

            out.println("<table border=\"1\">");
            out.println("<tr><th>視聴履歴</th><th>作品名</th></tr>");

            ResultSet rs = stmt.executeQuery(" SELECT history, anime" +
                                                  " FROM user_history"+
                                                  " Where user_id = " + user_id );
            while (rs.next()) {
                String history = rs.getString("history");
                String anime = rs.getString("anime");

                out.println("<tr>");
                out.println("<td>" + history + "</td>");
                out.println("<td><a href=\"change_history?anime=" + anime +
                        "&user_id=" + user_id + "&user_name=" + user_name +
                        "\">" + anime + "</a></td>");
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

        String url = "animation_list?user_id=" + user_id + "&user_name=" + user_name;
        out.println("<br/>");
        out.println("<a href=\"" + url +"\">アニメ作品一覧へ</a>");
        out.println("<br/>");
        out.println("<a href=\"list_forum\">掲示板へ</a>");
        out.println("※掲示板へ行く際は自動的にログアウトされます。<br/>");
        out.println("<a href=\"login\">ログアウト</a>");
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
