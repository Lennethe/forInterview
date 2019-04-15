import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class UpdateAnimationServlet extends HttpServlet {

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

        String b_anime = request.getParameter("b_anime");
        String update_anime = request.getParameter("update_anime");
        String update_company = request.getParameter("update_company");
        String user_id = request.getParameter("user_id");
        String user_name = request.getParameter("user_name");
        String url = "?user_id=" + user_id +
                "&user_name=" + user_name;

        boolean flag_blank = false;
        char[] c = update_anime.toCharArray();
        for(char i : c){
            if(i == ' ') flag_blank = true;
            if(i == '　') flag_blank = true;
        }
        if(update_anime.equals(" ")){
            flag_blank = true;
        }

        out.println("<html>");
        out.println("<body>");

        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
                    + ":5432/" + _dbname, _username, _password);
            stmt = conn.createStatement();

            if(flag_blank){
                out.println("作品内に空列を含めないでください");
            }
            else{
                conn.setAutoCommit(false);
                stmt.executeUpdate("UPDATE anime_company SET anime = '" + update_anime
                        + "' , company = '" + update_company + "' " +
                        "               WHERE anime = '" + b_anime + "'");

                stmt.executeUpdate("UPDATE user_history SET anime = '" + update_anime
                        + "'"  +
                        "               WHERE anime = '" + b_anime + "'");
                conn.commit();



                out.println("作品の情報を更新しました。<br/><br/>");
                out.println("作品: " + update_anime + "<br/>");
                out.println("制作会社: " + update_company + "<br/>");
            }

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

        out.println("<br/>");
        out.println("<a href=\"animation_list" + url + "\">アニメ作品一覧に戻る</a>");
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
