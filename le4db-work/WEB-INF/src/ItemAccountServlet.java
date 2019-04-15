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
public class ItemAccountServlet extends HttpServlet {

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
        String url = "?user_id=" + user_id + "&user_name=" + user_name;

        out.println("<html>");
        out.println("<body>");

        out.println("<h3>更新</h3>");
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
                    + ":5432/" + _dbname, _username, _password);
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM user_info WHERE user_id = " + user_id);

            String pass = "";
            while (rs.next()) {
                pass = rs.getString("pass");
            }
            rs.close();

            out.println("<form action=\"update_account\" method=\"GET\">");
            out.println("今のユーザー名： " + user_name);
            out.println("<br/>");

            out.println("ユーザー名： ");
            out.println("<input type=\"text\" name=\"user_name\" value=\"" + user_name + "\"/>");
            out.println("<br/>");
            out.println("パスワード： ");
            out.println("<input type=\"text\" name=\"pass\" value=\"" + pass + "\"/>");
            out.println("<input type=\"hidden\" name=\"current_name\" value=\"" + user_name + "\"/>");
            out.println("<input type=\"hidden\" name=\"user_id\" value=\"" + user_id + "\"/>");
            out.println("<input type=\"submit\" value=\"更新\"/>");
            out.println("</form>");


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

        out.println("<h3>削除</h3>");
        out.println("削除する場合はパスワードを入力してください</br>");
        out.println("<form action=\"delete_account\" method=\"GET\">");
        out.println("<input type=\"text\" name=\"input_pass\" />");
        out.println("<input type=\"hidden\" name=\"user_id\" value=" + user_id + " />");
        out.println("<input type=\"hidden\" name=\"user_name\" value=" + user_name + " />");
        out.println("<input type=\"submit\" value=\"削除\"/>");
        out.println("</form>");

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
