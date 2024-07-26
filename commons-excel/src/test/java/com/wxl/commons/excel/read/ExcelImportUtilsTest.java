package com.wxl.commons.excel.read;

import com.wxl.commons.excel.TestHelper;
import com.wxl.commons.excel.User;
import com.wxl.commons.util.PrettyPrinter;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

/**
 * Create by wuxingle on 2020/11/03
 * excel导入测试
 */
public class ExcelImportUtilsTest {

    @Test
    public void test1() throws Exception {
        List<List<String>> data = ExcelImportUtils.readAndClose(
                TestHelper.getFileInputStream("user1.xlsx"), 1);
        PrettyPrinter.printTable(data);

        List<User> users = ExcelImportUtils.readAndClose(
                TestHelper.getFileInputStream("user1.xlsx"), User.class, 1);
        PrettyPrinter.printJson(users);
    }

    @Test
    public void test2() throws Exception {
        InputStream in = TestHelper.getFileInputStream("user3.xlsx");
        Workbook workbook = ExcelImportUtils.create(in);

        List<List<String>> data = ExcelImportUtils.read(workbook, 0, 1);
        List<User> users = ExcelImportUtils.read(workbook, User.class, 1, 1);

        ExcelImportUtils.close(in);
        PrettyPrinter.printTable(data);
        PrettyPrinter.printJson(users);
    }
}