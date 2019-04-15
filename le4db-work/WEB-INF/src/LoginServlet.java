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
public class LoginServlet extends HttpServlet {

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

        // ここはログインの情報や
        out.println("<h3>ログイン</h3>");
        out.println("<form action=\"secure\" method=\"GET\">");
        out.println("id： ");
        out.println("<input type=\"text\" name=\"user_id\"/>");
        out.println("<br/>");
        out.println("password： ");
        out.println("<input type=\"text\" name=\"password\"/>");
        out.println("<br/>");
        out.println("<input type=\"submit\" value=\"ログイン\"/>");
        out.println("</form>");



        out.println("<h3>アカウント作成</h3>");
        out.println("<form action=\"createAccount\" method=\"GET\">");
        out.println("ユーザー名： ");
        out.println("<input type=\"text\" name=\"add_user_name\"/>");
        out.println("<br/>");
        out.println("password： ");
        out.println("<input type=\"text\" name=\"add_password\"/>");
        out.println("<br/>");
        out.println("<input type=\"submit\" value=\"作成！\"/>");
        out.println("</form>");

        out.println("<br/>");
        out.println("<a href=\"list_forum\">掲示板へ</a>");

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
