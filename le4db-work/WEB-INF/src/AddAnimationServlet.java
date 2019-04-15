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
public class AddAnimationServlet extends HttpServlet {

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
        String url = "?user_id=" + user_id +
                "&user_name=" + user_name;
        String add_animation = request.getParameter("add_animation");
        String add_company = request.getParameter("add_company");
        String animation = "";

        boolean flag_blank = false;
        char[] c = add_animation.toCharArray();
        for(char i : c){
            if(i == ' ') flag_blank = true;
            if(i == '　') flag_blank = true;
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

            ResultSet rs = stmt.executeQuery("SELECT * FROM anime_company " +
                                                    "WHERE anime = '" + add_animation + "'");
            while (rs.next()) {
                animation = rs.getString("anime");

            }
            rs.close();

            if(animation.equals(add_animation) || flag_blank){
                out.println("この作品は既に登録されています～<br/>");
                out.println("もしくは作品名に空列が含まれているよ");
            }
            else{
                stmt.executeUpdate("INSERT INTO anime_company VALUES( '" + add_animation + "', '" + add_company + "')");

                out.println("以下の作品が追加されました。<br/><br/>");
                out.println("作品: " + add_animation + "<br/>");
                out.println("制作会社: " + add_company + "<br/>");
            }

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
