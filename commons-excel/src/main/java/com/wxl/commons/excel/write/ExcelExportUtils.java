package com.wxl.commons.excel.write;

import com.wxl.commons.excel.ExcelVersion;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;


/**
 * Created by wuxingle on 2017/10/21 0021.
 * excel导出工具类
 */
public class ExcelExportUtils {

    /**
     * 根据版本获取workbook
     */
    public static Workbook create(ExcelVersion version) {
        return switch (version) {
            case XLS -> new HSSFWorkbook();
            case XLSX -> new XSSFWorkbook();
        };
    }

    /**
     * 导出
     */
    public static <T> void writeAndClose(List<T> list, OutputStream out, ExcelVersion version) throws IOException {
        writeAndClose(list, out, null, null, version);
    }

    public static <T> void write(Workbook workbook, List<T> list) {
        write(workbook, list, null, null);
    }

    public static <T> void writeAndClose(List<T> list, OutputStream out,
                                         String sheetName,
                                         String[] titles,
                                         ExcelVersion version) throws IOException {
        writeAndClose(list, out, sheetName, new DefaultExportHandler<>(titles), null, version);
    }


    public static <T> void write(Workbook workbook, List<T> list,
                                 String sheetName,
                                 String[] titles) {
        write(workbook, list, sheetName, new DefaultExportHandler<>(titles), null);
    }

    public static <T> void writeAndClose(List<T> list, OutputStream out,
                                         String sheetName,
                                         String[] titles,
                                         ExportConfigure configure,
                                         ExcelVersion version) throws IOException {
        writeAndClose(list, out, sheetName, new DefaultExportHandler<>(titles), configure, version);
    }

    public static <T> void write(Workbook workbook, List<T> list,
                                 String sheetName,
                                 String[] titles,
                                 ExportConfigure configure) {
        write(workbook, list, sheetName, new DefaultExportHandler<>(titles), configure);
    }

    /**
     * 导出一张表
     *
     * @param list      数据列表
     * @param out       输出流
     * @param sheetName 表名
     * @param handler   头处理工具
     * @param configure 参数设置
     * @param version   版本
     */
    public static <T> void writeAndClose(List<T> list, OutputStream out,
                                         String sheetName,
                                         ExportHandler<T> handler,
                                         ExportConfigure configure,
                                         ExcelVersion version) throws IOException {
        try {
            Workbook workbook = create(version);
            write(workbook, list, sheetName, handler, configure);
            export(workbook, out);
        } finally {
            close(out);
        }
    }

    /**
     * 把数据写入workbook
     */
    public static <T> void write(final Workbook workbook, List<T> list,
                                 String sheetName,
                                 ExportHandler<T> handler,
                                 ExportConfigure configure) {
        Sheet sheet = workbook.createSheet(WorkbookUtil.createSafeSheetName(sheetName));

        //头处理
        HeadExportContext headGenerator = new HeadExportContext(workbook, sheet);
        handler.exportHeader(headGenerator);

        //数据体处理
        BodyExportContext bodyGenerator = new BodyExportContext(workbook, sheet, headGenerator.getRowCount());
        handler.exportData(list, bodyGenerator);

        if (configure != null) {
            //合并单元格
            if (!ObjectUtils.isEmpty(configure.getMergeRange())) {
                for (CellRangeAddress address : configure.getMergeRange()) {
                    sheet.addMergedRegion(address);
                }
            }
            //设置行高
            Map<Integer, Integer> rowMap = configure.getRowHeight();
            if (!CollectionUtils.isEmpty(rowMap)) {
                for (Integer index : rowMap.keySet()) {
                    int rowHight = rowMap.get(index);
                    Assert.isTrue(rowHight > 0, "row height must > 0");
                    setRowHeight(sheet.getRow(index), rowHight);
                }
            }
            //设置列宽
            Map<Integer, Integer> columnMap = configure.getColumnCharNum();
            if (!CollectionUtils.isEmpty(columnMap)) {
                for (Integer index : columnMap.keySet()) {
                    int columnWidth = columnMap.get(index);
                    Assert.isTrue(columnWidth > 0, "column Width must > 0");
                    setColumnWidth(sheet, index, columnWidth);
                }
            }
        }
    }

    /**
     * 导出并关闭
     */
    public static void exportAndClose(Workbook workbook, OutputStream out) throws IOException {
        try {
            export(workbook, out);
        } finally {
            close(out);
        }
    }

    /**
     * 导出
     */
    public static void export(Workbook workbook, OutputStream out) throws IOException {
        workbook.write(out);
    }

    /**
     * 关闭输出流
     */
    public static void close(OutputStream out) throws IOException {
        out.close();
    }


    /**
     * 设置行高
     *
     * @param height 磅
     */
    public static void setRowHeight(Row row, int height) {
        row.setHeightInPoints(height);
    }

    /**
     * 设置列宽
     *
     * @param charNum 字符数
     */
    public static void setColumnWidth(Sheet sheet, int index, int charNum) {
        sheet.setColumnWidth(index, charNum * 256);
    }

}
