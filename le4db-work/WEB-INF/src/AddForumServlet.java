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
public class AddForumServlet extends HttpServlet {

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

        String anime = request.getParameter("anime");
        String forum_name = request.getParameter("forum_name");

        out.println("<html>");
        out.println("<body>");

        boolean flag_blank = false;
        if(anime.equals("")) flag_blank = true;
        if(forum_name.equals("")) flag_blank = true;
        char[] c = anime.toCharArray();
        for(char i : c){
            if(i == ' ') flag_blank = true;
            if(i == '　') flag_blank = true;
            if(i == '\'') flag_blank = true;
        }
        c = forum_name.toCharArray();
        for(char i : c){
            if(i == ' ') flag_blank = true;
            if(i == '　') flag_blank = true;
            if(i == '\'') flag_blank = true;
        }
        if(flag_blank){
            out.println("入力に空列や'を入れるのはやめてほしいです...</br>");
            out.println("<a href=\"list_forum\">掲示板一覧に戻る</a>");
            out.println("</body>");
            out.println("</html>");
            return;
        }


        Connection conn = null;
        Statement stmt = null;
        int num_forum = 0;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
                    + ":5432/" + _dbname, _username, _password);
            stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT count(*) FROM id_anime_forum_name ");

            while (rs.next()) {
                num_forum = rs.getInt("count");
            }
            rs.close();
            num_forum++;
            stmt.executeUpdate("INSERT INTO id_anime_forum_name VALUES( " + num_forum + " , '" + anime + " ', '" + forum_name + "')");

            out.println("以下のスレッドが立ちました。<br/><br/>");
            out.println("話題: " + anime + "<br/>");
            out.println("掲示板名: " + forum_name + "<br/>");

        } catch (Exception e) {
            out.println("何らかの不正が検出されました　<br/>");
            out.println("文字列として　’　は使用できないのでご確認ください");
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

        out.println("<br/>");
        out.println("<a href=\"list_forum\">掲示板一覧に戻る</a>");
        out.println("<br/>");
        out.println("<a href=\"forum?forum_id=" + num_forum + "\">立てた掲示板へ</a>");

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
