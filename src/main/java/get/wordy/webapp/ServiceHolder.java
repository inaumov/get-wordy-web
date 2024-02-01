package get.wordy.webapp;

import get.wordy.core.DictionaryService;
import get.wordy.core.ServerInfo;
import get.wordy.core.api.IDictionaryService;
import get.wordy.core.dao.impl.DaoFactory;
import get.wordy.core.db.ConnectionWrapper;

import java.sql.*;
import java.util.Properties;

public class ServiceHolder {

    private static final IDictionaryService dictionaryService;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://mysql-locl:3306/get_wordy?useUnicode=yes,characterEncoding=utf8,connectionCollation=utf8_general_ci,sql_mode=STRICT_TRANS_TABLES", "root", "root");
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT 1");
            rs.next();
            String resultChecked = rs.getString(1);
            if ("1".equals(resultChecked)) {
                System.out.println("Connection established. AutoCommit enabled: " + conn.getAutoCommit());
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getCause().getMessage());
        }
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setHost("jdbc:mysql://mysql-locl:3306");
        serverInfo.setDatabase("get_wordy");
        Properties account = new Properties();
        account.setProperty("user", "root");
        account.setProperty("password", "root");
        serverInfo.setAccount(account);
        ConnectionWrapper connectionWrapper = ConnectionWrapper.getInstance();
        connectionWrapper.setServerInfo(serverInfo);
        DaoFactory factory = DaoFactory.getFactory();
        dictionaryService = new DictionaryService(
                factory.getDictionaryDao(),
                factory.getWordDao(),
                factory.getCardDao(),
                connectionWrapper
        );
    }

    public static IDictionaryService getDictionaryService() {
        return dictionaryService;
    }

}