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
public class CreateAccountServlet extends HttpServlet {

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
        int userID = 0;
        String addUserName = request.getParameter("add_user_name");
        String addPass = request.getParameter("add_password");

        out.println("<html>");
        out.println("<body>");

        boolean flag_blank = false;
        if(addUserName.equals("")) flag_blank = true;
        if(addPass.equals("")) flag_blank = true;
        char[] c = addUserName.toCharArray();
        for(char i : c){
            if(i == ' ') flag_blank = true;
            if(i == '　') flag_blank = true;
            if(i == '\'') flag_blank = true;
        }
        c = addPass.toCharArray();
        for(char i : c){
            if(i == ' ') flag_blank = true;
            if(i == '　') flag_blank = true;
            if(i == '\'') flag_blank = true;
        }
        if(flag_blank){
            out.println("入力に空列や'を入れるのはやめてほしいです...</br>");
            out.println("<a href=\"login\">ログインページにいく</a>");
            out.println("</body>");
            out.println("</html>");
            return;
        }

        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
                    + ":5432/" + _dbname, _username, _password);
            stmt = conn.createStatement();

            int max_pid = 0;
            ResultSet rs = stmt.executeQuery("SELECT MAX(user_id) AS max_pid FROM user_info");
            while (rs.next()) {
                max_pid = rs.getInt("max_pid");

            }
            rs.close();

            int addPID = max_pid + 1;
            userID = addPID;
            stmt.executeUpdate("INSERT INTO user_info VALUES(" + addPID + ", '" + addUserName + "', '" + addPass + "')");

            out.println("無事にアカウントが作成されました<br/><br/>");
            out.println("ID: " + addPID + "<br/>");
            out.println("ユーザー名: " + addUserName + "<br/>");
            out.println("パスワード: " + addPass + "<br/>");
            out.println("ユーザーIDは各自で控えてね！");

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

        String url = "my_page?" + "user_id=" + userID + "&user_name=" + addUserName;
        out.println("<br/>");
        out.println("<a href=\" "+ url + "\">マイページにいく</a>");


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
