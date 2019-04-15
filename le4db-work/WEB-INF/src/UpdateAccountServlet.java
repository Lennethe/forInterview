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
public class UpdateAccountServlet extends HttpServlet {

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
        String current_name = request.getParameter("current_name");
        String pass = request.getParameter("pass");
        String url = "?user_id=" + user_id +
                "&user_name=" + current_name;

        boolean flag_blank = false;
        char[] c = user_name.toCharArray();
        for(char i : c){
            if(i == ' ') flag_blank = true;
            if(i == '　') flag_blank = true;
        }
        if(user_name.equals("")){
            flag_blank = true;
        }
        c = pass.toCharArray();
        for(char i : c){
            if(i == ' ') flag_blank = true;
            if(i == '　') flag_blank = true;
        }
        if(pass.equals("")){
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
                out.println("空列を含めないでください");
                out.println("<br/>");
            }
            else{
                stmt.executeUpdate("UPDATE user_info SET user_name = '" + user_name
                        + "' , pass = '" + pass + "' " +
                        "               WHERE user_id = " + user_id);
                out.println("ユーザーの情報を更新しました。<br/><br/>");
                out.println("ユーザー名: " + user_name + "<br/>");
                out.println("パスワード: " + pass + "<br/>");
                url = "?user_id=" + user_id +
                        "&user_name=" + user_name;
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
