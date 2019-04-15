import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class SecureServlet extends HttpServlet {

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
        String user_name = "";
        String pass = "";
        String user_id = request.getParameter("user_id");
        try{
            Integer.parseInt(user_id);
        } catch (NumberFormatException e){
            out.println("<html>");
            out.println("<body>");
            out.println("入力するのは数字のみです");
            out.println("自分のidを再確認してください");
            out.println("<br/>");
            out.println("<a href=\"login\">ログイン画面に戻る</a>");
            out.println("</body>");
            out.println("</html>");
            return;
        }

        String InPass = request.getParameter("password");
        boolean flag_login = false;

        out.println("<html>");
        out.println("<body>");

        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
                    + ":5432/" + _dbname, _username, _password);
            stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT * FROM user_info " +
                                                    "WHERE user_id = "+ user_id);
            while(rs.next()){
                pass = rs.getString("pass");
                user_name = rs.getString("user_name");
            }

            rs.close();

            if(pass.equals(InPass)){
                flag_login = true;
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

        if(flag_login){
            out.println("無事にログインされました");
            String url = "my_page?" + "user_id=" + user_id + "&user_name=" + user_name;
            out.println("<br/>");
            out.println("<a href=\" "+ url + "\">マイページにいく</a>");
        }
        else{
            out.println("パスワードを間違えているか、アカウントが存在しません");
            out.println("<br/>");
            out.println("<a href=\"login\">ログイン画面に戻る</a>");
        }

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
