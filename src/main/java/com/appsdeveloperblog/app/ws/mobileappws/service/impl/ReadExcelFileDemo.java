package com.appsdeveloperblog.app.ws.mobileappws.service.impl;

import java.io.*;
import java.sql.*;
import java.util.*;

import com.appsdeveloperblog.app.ws.mobileappws.entity.UserEntity;
import com.appsdeveloperblog.app.ws.mobileappws.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import javax.transaction.Transactional;

@Component
public class ReadExcelFileDemo implements ApplicationListener<ApplicationStartedEvent> {

    private final DataSource dataSource;
    private final UserRepository userRepository;

    public ReadExcelFileDemo(DataSource dataSource, UserRepository userRepository) {
        this.dataSource = dataSource;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
//        registerUserFromExcelJDBC();
        registerUserFromExcelJPA();
    }

    private void registerUserFromExcelJPA() {
        String excelFilePath = "C:\\dev\\edu\\Java\\mobile-app-ws\\src\\main\\resources\\excel\\Users.xlsx";
        String[] HEADERs = { "id", "user_id", "first_name", "last_name", "email, encrypted_password", "email_verification_token", "email_verification_status"};

        try {
            long start = System.currentTimeMillis();
            FileInputStream inputStream = new FileInputStream(excelFilePath);
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet firstSheet = workbook.getSheet("Tabelle1");
            Iterator<Row> rowIterator = firstSheet.iterator();
            List<UserEntity> users = new ArrayList<UserEntity>();

            int count = 0;
            rowIterator.next(); // skip the header row
            while (rowIterator.hasNext()) {
                Row nextRow = rowIterator.next();
                Iterator<Cell> cellIterator = nextRow.cellIterator();
                UserEntity userEntity = new UserEntity();

                while (cellIterator.hasNext()) {
                    Cell nextCell = cellIterator.next();

                    int columnIndex = nextCell.getColumnIndex();

                    switch (columnIndex) {
                        case 0:
                            userEntity.setId((int) nextCell.getNumericCellValue());
                            break;
                        case 1:
                            userEntity.setUserId(nextCell.getStringCellValue());
                            break;
                        case 2:
                            userEntity.setFirstName(nextCell.getStringCellValue());
                            break;
                        case 3:
                            userEntity.setLastName(nextCell.getStringCellValue());
                            break;
                        case 4:
                            userEntity.setEmail(nextCell.getStringCellValue());
                            break;
                        case 5:
                            userEntity.setEncryptedPassword(nextCell.getStringCellValue());
                            break;
                        case 6:
                            userEntity.setEmailVerificationToken(nextCell.getStringCellValue());
                            break;
                        case 7:
                            userEntity.setEmailVerificationStatus(Boolean.valueOf(nextCell.getStringCellValue()));
                            break;
                        default:
                            break;
                    }

                }
                users.add(userEntity);
            }

            workbook.close();
            userRepository.saveAll(users);

            long end = System.currentTimeMillis();
            System.out.printf("Import done in %d ms\n", (end - start));

        } catch (IOException ex1) {
            System.out.println("Error reading file");
            ex1.printStackTrace();
        }
    }

    private void registerUserFromExcelJDBC() {
        String excelFilePath = "C:\\dev\\edu\\Java\\mobile-app-ws\\src\\main\\resources\\excel\\Users.xlsx";
        int batchSize = 20;
        try {
            Connection connection = dataSource.getConnection();
            long start = System.currentTimeMillis();
            FileInputStream inputStream = new FileInputStream(excelFilePath);
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet firstSheet = workbook.getSheet("Tabelle1");
            Iterator<Row> rowIterator = firstSheet.iterator();
            connection.setAutoCommit(false);
            String sql = "INSERT INTO users (id, user_id, first_name, last_name, email, encrypted_password, email_verification_token, email_verification_status, address) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            int count = 0;
            rowIterator.next(); // skip the header row
            while (rowIterator.hasNext()) {
                Row nextRow = rowIterator.next();
                Iterator<Cell> cellIterator = nextRow.cellIterator();

                while (cellIterator.hasNext()) {
                    Cell nextCell = cellIterator.next();

                    int columnIndex = nextCell.getColumnIndex();

                    switch (columnIndex) {
                        case 0:
                            int id = (int) nextCell.getNumericCellValue();
                            statement.setInt(1, id);
                            break;
                        case 1:
                            String user_id = nextCell.getStringCellValue();
                            statement.setString(2, user_id);
                            break;
                        case 2:
                            String first_name = nextCell.getStringCellValue();
                            statement.setString(3, first_name);
                            break;
                        case 3:
                            String last_name = nextCell.getStringCellValue();
                            statement.setString(4, last_name);
                            break;
                        case 4:
                            String email = nextCell.getStringCellValue();
                            statement.setString(5, email);
                            break;
                        case 5:
                            String encrypted_password = nextCell.getStringCellValue();
                            statement.setString(6, encrypted_password);
                            break;
                        case 6:
                            String email_verification_token = nextCell.getStringCellValue();
                            statement.setString(7, email_verification_token);
                            break;
                        case 7:
                            String email_verification_status = nextCell.getStringCellValue();
                            statement.setBoolean(8, Boolean.parseBoolean(email_verification_status));
                            break;
                        case 8:
                            String address = nextCell.getStringCellValue();
                            statement.setString(9, address);
                            break;
                    }

                }

                statement.addBatch();

                if (count % batchSize == 0) {
                    statement.executeBatch();
                }

            }

            workbook.close();

            // execute the remaining queries
            statement.executeBatch();

            connection.commit();
            connection.close();

            long end = System.currentTimeMillis();
            System.out.printf("Import done in %d ms\n", (end - start));

        } catch (IOException ex1) {
            System.out.println("Error reading file");
            ex1.printStackTrace();
        } catch (SQLException ex2) {
            System.out.println("Database error");
            ex2.printStackTrace();
        }
    }

//    public static boolean hasExcelFormat(MultipartFile file) {
//        if (!TYPE.equals(file.getContentType())) {
//            return false;
//        }
//        return true;
//    }
}
