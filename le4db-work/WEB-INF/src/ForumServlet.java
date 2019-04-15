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
public class ForumServlet extends HttpServlet {

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

        String handle_name = "ななしさん";
        String forum_id = request.getParameter("forum_id");
        String forum_name = request.getParameter("forum_name");
        String comment = request.getParameter("comment");
        String anime = request.getParameter("anime");

        out.println("<html>");
        out.println("<body>");

        Connection connC = null;
        Statement stmtC = null;
        try{
            if(!(comment.equals(" "))){
            }
            connC = DriverManager.getConnection("jdbc:postgresql://" + _hostname
                    + ":5432/" + _dbname, _username, _password);
            stmtC = connC.createStatement();
            int mf_id = 0;
            //ResultSet rsC = stmtC.executeQuery("SELECT MAX(impress_id) AS mf_id FROM id_to_impress"
            //                                        + "WHERE forum_id = " + forum_id );
            ResultSet rsC = stmtC.executeQuery("SELECT count(*) from id_to_impress" +
                    "                           WHERE forum_id = " + forum_id);
            while (rsC.next()){
                mf_id = rsC.getInt("count");
            }
            rsC.close();
            mf_id++;

            stmtC.executeUpdate("INSERT INTO id_to_impress VALUES(" + forum_id + ", '" + mf_id + "', '" + comment + "')");


        } catch (NullPointerException e){

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                if (connC != null) {
                    connC.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }



        out.println("<br/>");
        out.println("<a href=\"list_forum\">掲示板一覧に戻る</a><br/>");

        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://" + _hostname
                    + ":5432/" + _dbname, _username, _password);
            stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT * FROM id_to_impress " +
                    "WHERE forum_id = "+ forum_id );
            out.println("<h2>話題</h2>");
            out.println("<h3>" + anime + "</h3>");
            out.println("<h3>スレッド名</h3>");
            out.println("<h4>" + forum_name + "</h4>");
            out.println("<br/><br/>");


            while (rs.next()) {
                String impress_id = rs.getString("impress_id");
                String impress = rs.getString("impress");

                out.println(handle_name + " : " +  impress_id + "<br/>");
                char[] cv = impress.toCharArray();
                int i = 0;
                for(char c : cv){
                    if(i == 0) out.println("&nbsp; &nbsp; &nbsp; &nbsp; ");
                    out.println(c);
                    if(i == 20){
                        out.println("<br/>");
                        i = 0;
                    }
                    i++;
                }
                out.println("<br/><br/>");
            }
            rs.close();


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

        out.println("<form action=\"forum\" method=\"GET\">");
        out.println("コメント： ");
        out.println("<input type=\"text\" name=\"comment\"/>");
        out.println("<br/>");
        out.println("<input type=\"hidden\" name=\"forum_id\" value=\"" + forum_id + "\"/>");
        out.println("<input type=\"hidden\" name=\"forum_name\" value=\"" + forum_name + "\"/>");
        out.println("<input type=\"hidden\" name=\"anime\" value=\"" + anime + "\"/>");
        out.println("<input type=\"submit\" value=\"送信\"/>");
        out.println("</form>");

        out.println("<br/>");
        out.println("<a href=\"list_forum\">掲示板一覧に戻る</a>");
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
