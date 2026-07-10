package com.learn.learningarea.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.learn.learningarea.model.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ReportExportService {

    public byte[] generatePdfReport(List<?> data, String accountType) {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Manage Accounts Report - " + accountType.toUpperCase(), titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable table = createPdfTable(data, accountType);
            document.add(table);
            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }

    public byte[] generateExcelReport(List<?> data, String accountType) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(accountType.toUpperCase() + " Report");

            String[] headers = getHeadersForAccountType(accountType);
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowIdx = 1;
            for (Object obj : data) {
                Row row = sheet.createRow(rowIdx++);
                fillExcelRow(row, obj, accountType);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    private PdfPTable createPdfTable(List<?> data, String accountType) {
        String[] headers = getHeadersForAccountType(accountType);
        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);

        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);

        // Add Headers
        for (String header : headers) {
            PdfPCell hcell = new PdfPCell(new Phrase(header, headFont));
            hcell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            table.addCell(hcell);
        }

        // Add Data Rows
        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA);
        for (Object obj : data) {
            String[] rowData = getRowData(obj, accountType);
            for (String val : rowData) {
                PdfPCell cell = new PdfPCell(new Phrase(val != null ? val : "", cellFont));
                cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                table.addCell(cell);
            }
        }

        return table;
    }

    private String[] getHeadersForAccountType(String type) {
        switch (type) {
            case "enquiry":
                return new String[] { "Date", "ID", "Name", "Service", "Mobile" };
            case "enrollment":
                return new String[] { "Date", "Enr. ID", "Name", "Service", "Fee" };
            case "income":
                return new String[] { "Date", "Category", "Source", "Mode", "Amount" };
            case "expense":
                return new String[] { "Date", "Category", "Paid To", "Mode", "Amount" };
            case "payment":
                return new String[] { "Date", "Enr. ID", "Name", "Installment", "Status" };
            case "schedule":
                return new String[] { "Start Date", "Batch No", "Program Name", "Type", "Students" };
            case "daily_report":
                return new String[] { "Date", "Enquiries", "Enrollments", "Revenue" };
            case "college":
                return new String[] { "Created At", "College Name", "Location", "Mobile", "Email" };
            case "corporate":
                return new String[] { "Created At", "Company Name", "Location", "Mobile", "Email" };
            case "employee":
                return new String[] { "Joining Date", "Emp ID", "Name", "Category", "Designation" };
            case "vendor":
                return new String[] { "Created At", "Vendor Name", "Category", "Location", "Contact" };
            default:
                return new String[] { "Data" };
        }
    }

    private String[] getRowData(Object obj, String type) {
        try {
            switch (type) {
                case "enquiry": {
                    Enquiry e = (Enquiry) obj;
                    return new String[] { String.valueOf(e.getEnquiryDate()), e.getSource(), e.getName(),
                            e.getService(), e.getMobileNumber() };
                }
                case "enrollment": {
                    Enrollment e = (Enrollment) obj;
                    return new String[] { String.valueOf(e.getEnrollmentDate()), e.getEnrollmentId(), e.getName(),
                            e.getService(), e.getAmount() };
                }
                case "income": {
                    Income i = (Income) obj;
                    return new String[] { String.valueOf(i.getIncomeDate()), i.getRevenueChannel(), i.getPaidBy(),
                            i.getModeOfPayment(), String.valueOf(i.getAmount()) };
                }
                case "expense": {
                    Expense e = (Expense) obj;
                    return new String[] { String.valueOf(e.getExpenseDate()), e.getExpenseCategory(), e.getPaidTo(),
                            e.getModeOfPayment(), String.valueOf(e.getAmount()) };
                }
                case "payment": {
                    Payment p = (Payment) obj;
                    return new String[] { String.valueOf(p.getCreatedAt()), p.getEnrollmentId(), p.getName(),
                            String.valueOf(p.getInstallments()), p.getPaymentStatus() };
                }
                case "schedule": {
                    Schedule s = (Schedule) obj;
                    return new String[] { String.valueOf(s.getStartDate()), s.getBatchNo(), s.getProgramName(),
                            s.getBatchType(), String.valueOf(s.getStudentCount()) };
                }
                case "daily_report": {
                    DailyReport d = (DailyReport) obj;
                    return new String[] { String.valueOf(d.getReportDate()), String.valueOf(d.getEnquiries()),
                            String.valueOf(d.getEnrollment()), String.valueOf(d.getRevenue()) };
                }
                case "college": {
                    College c = (College) obj;
                    return new String[] { String.valueOf(c.getCreatedAt()), c.getCollegeName(), c.getLocation(),
                            c.getMobileNo(), c.getEmailId() };
                }
                case "corporate": {
                    Corporate c = (Corporate) obj;
                    return new String[] { String.valueOf(c.getCreatedAt()), c.getCompanyName(), c.getLocation(),
                            c.getMobileNo(), c.getEmailId() };
                }
                case "employee": {
                    Employee e = (Employee) obj;
                    return new String[] { String.valueOf(e.getDateOfJoining()), e.getEmployeeId(), e.getName(),
                            e.getCategory(), e.getDesignation() };
                }
                case "vendor": {
                    Vendor v = (Vendor) obj;
                    return new String[] { String.valueOf(v.getCreatedAt()), v.getVendorName(), v.getCategory(),
                            v.getLocation(), v.getName() };
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new String[] { obj.toString() };
    }

    private void fillExcelRow(Row row, Object obj, String type) {
        String[] data = getRowData(obj, type);
        for (int i = 0; i < data.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(data[i] != null ? data[i] : "");
        }
    }
}
