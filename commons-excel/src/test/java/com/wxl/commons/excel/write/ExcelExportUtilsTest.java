package com.wxl.commons.excel.write;

import com.google.common.collect.ImmutableMap;
import com.wxl.commons.excel.ExcelVersion;
import com.wxl.commons.excel.TestHelper;
import com.wxl.commons.excel.User;
import com.wxl.commons.util.RandomUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

/**
 * Create by wuxingle on 2020/11/03
 * excel导出
 */
public class ExcelExportUtilsTest {

    @Test
    public void testWrite1() throws Exception {
        List<List<String>> table = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(i + 1));
            row.add(RandomStringUtils.randomAlphabetic(6));
            row.add(RandomStringUtils.randomNumeric(6));
            row.add(RandomUtils.randomMobile());
            row.add(RandomUtils.randomEmail(5, 10));
            row.add(RandomUtils.randomDate("2020-01-01", "2020-12-01", "yyyy-MM-dd"));
            table.add(row);
        }

        ExcelExportUtils.writeAndClose(table, TestHelper.getFileOutputStream("user1.xlsx"), ExcelVersion.XLSX);
        ExcelExportUtils.writeAndClose(table, TestHelper.getFileOutputStream("user2.xlsx"), "user",
                new String[]{"序号", "名字", "数字", "手机", "邮箱", "生日"}, ExcelVersion.XLSX);
    }


    @Test
    public void testWrite2() throws Exception {
        Function<Random, User> generator = random -> {
            User user = new User();
            user.setId(random.nextInt());
            user.setName(RandomUtils.randomAlphabetic(random, 6));
            user.setNumber(RandomUtils.randomNumeric(random, 6));
            user.setMobile(RandomUtils.randomMobile(random));
            user.setEmail(RandomUtils.randomEmail(random, 6));
            user.setBirthday(RandomUtils.randomDate(random, "2000-01-01", "2020-12-12", "yyyy-MM-dd"));
            return user;
        };

        List<User> list1 = RandomUtils.randomObject(10, generator);
        List<User> list2 = RandomUtils.randomObject(10, generator);


        Workbook workbook = ExcelExportUtils.create(ExcelVersion.XLSX);

        ExcelExportUtils.write(workbook, list1, "表1",
                new String[]{"序号", "名字", "数字", "手机", "邮箱", "生日"});
        ExcelExportUtils.write(workbook, list2, "表2",
                new String[]{"序号", "名字", "数字", "手机", "邮箱", "生日"}, new ExportConfigure() {
                    @Override
                    public CellRangeAddress[] getMergeRange() {
                        CellRangeAddress[] cellRangeAddresses = new CellRangeAddress[1];
                        cellRangeAddresses[0] = new CellRangeAddress(11, 12, 1, 1);
                        return cellRangeAddresses;
                    }

                    @Override
                    public Map<Integer, Integer> getRowHeight() {
                        return ImmutableMap.of(1, 10);
                    }

                    @Override
                    public Map<Integer, Integer> getColumnCharNum() {
                        return ImmutableMap.of(1, 10);
                    }
                });

        ExcelExportUtils.exportAndClose(workbook, TestHelper.getFileOutputStream("user3.xlsx"));
    }


}