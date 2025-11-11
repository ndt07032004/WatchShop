package com.watchstore.context;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.SQLException;

@WebListener
public class DBContext implements ServletContextListener {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/watch_shop_db?useUnicode=true&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASS = "";

    private static HikariDataSource dataSource;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DB_URL);
        config.setUsername(USER);
        config.setPassword(PASS);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setMaximumPoolSize(10); // Số lượng kết nối tối đa trong pool
        config.setMinimumIdle(5); // Số lượng kết nối nhàn rỗi tối thiểu
        config.setConnectionTimeout(30000); // Thời gian chờ kết nối (30s)
        config.setIdleTimeout(600000); // Thời gian tối đa một kết nối có thể nhàn rỗi (10p)
        config.setMaxLifetime(1800000); // Tuổi thọ tối đa của một kết nối (30p)

        dataSource = new HikariDataSource(config);
        System.out.println("INFO: HikariCP connection pool initialized.");
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (dataSource != null) {
            dataSource.close();
            System.out.println("INFO: HikariCP connection pool closed.");
        }
    }
}
