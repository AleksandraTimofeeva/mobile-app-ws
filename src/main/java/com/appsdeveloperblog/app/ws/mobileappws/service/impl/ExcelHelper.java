package com.appsdeveloperblog.app.ws.mobileappws.service.impl;

import com.appsdeveloperblog.app.ws.mobileappws.entity.UserEntity;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERs = { "id", "user_id", "first_name", "last_name", "email, encrypted_password", "email_verification_token", "email_verification_status"};
    static String SHEET = "Tabelle1";
    public static boolean hasExcelFormat(MultipartFile file) {
        if (!TYPE.equals(file.getContentType())) {
            return false;
        }
        return true;
    }

    public static List<UserEntity> excelToUsers(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheet(SHEET);
            Iterator<Row> rows = sheet.iterator();
            List<UserEntity> users = new ArrayList<UserEntity>();
            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }
                Iterator<Cell> cellsInRow = currentRow.iterator();
                UserEntity userEntity = new UserEntity();
                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();
                    switch (cellIdx) {
                        case 0:
                            userEntity.setId((int) currentCell.getNumericCellValue());
                            break;
                        case 1:
                            userEntity.setUserId(currentCell.getStringCellValue());
                            break;
                        case 2:
                            userEntity.setFirstName(currentCell.getStringCellValue());
                            break;
                        case 3:
                            userEntity.setLastName(currentCell.getStringCellValue());
                            break;
                        case 4:
                            userEntity.setEmail(currentCell.getStringCellValue());
                            break;
                        case 5:
                            userEntity.setEncryptedPassword(currentCell.getStringCellValue());
                            break;
                        case 6:
                            userEntity.setEmailVerificationToken(currentCell.getStringCellValue());
                            break;
                        case 7:
                            userEntity.setEmailVerificationStatus(Boolean.valueOf(currentCell.getStringCellValue()));
                            break;
                        default:
                            break;
                    }
                    cellIdx++;
                }
                users.add(userEntity);
            }
            workbook.close();
            return users;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }

}
