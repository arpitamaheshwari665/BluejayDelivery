import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Employee {
    private String name;
    private int position;
    private List<Integer> shifts;

    public Employee(String name, int position) {
        this.name = name;
        this.position = position;
        this.shifts = new ArrayList<>();
    }

    public void addShift(int shift) {
        shifts.add(shift);
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public List<Integer> getShifts() {
        return shifts;
    }
}

public class EmployeeAnalyzerExcel {
    public static void main(String[] args) {
        // Path to the Excel file
        String excelFilePath = "employee_data.xlsx";

        try (FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            // Assuming the data is in the first sheet
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = sheet.iterator();

            List<Employee> employees = new ArrayList<>();

            // Skip the header row
            if (iterator.hasNext()) {
                iterator.next();
            }

            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();

                String name = cellIterator.next().getStringCellValue();
                int position = (int) cellIterator.next().getNumericCellValue();

                Employee employee = new Employee(name, position);

                while (cellIterator.hasNext()) {
                    employee.addShift((int) cellIterator.next().getNumericCellValue());
                }

                employees.add(employee);
            }

            // Analyze the data and print the results
            for (Employee employee : employees) {
                if (hasWorkedConsecutiveDays(employee.getShifts(), 7)) {
                    System.out.println("Employee: " + employee.getName() + ", Position: " + employee.getPosition() + " - Worked 7 consecutive days.");
                }
                if (hasLessThan10HoursBetweenShifts(employee.getShifts())) {
                    System.out.println("Employee: " + employee.getName() + ", Position: " + employee.getPosition() + " - Less than 10 hours between shifts.");
                }
                if (hasWorkedMoreThan14HoursInAShift(employee.getShifts())) {
                    System.out.println("Employee: " + employee.getName() + ", Position: " + employee.getPosition() + " - Worked more than 14 hours in a single shift.");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean hasWorkedConsecutiveDays(List<Integer> shifts, int days) {
        for (int i = 0; i < shifts.size() - days + 1; i++) {
            boolean consecutive = true;
            for (int j = i; j < i + days; j++) {
                if (shifts.get(j) == 0) {
                    consecutive = false;
                    break;
                }
            }
            if (consecutive) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasLessThan10HoursBetweenShifts(List<Integer> shifts) {
        for (int i = 0; i < shifts.size() - 1; i++) {
            int hoursBetweenShifts = shifts.get(i + 1) - shifts.get(i);
            if (hoursBetweenShifts > 1 && hoursBetweenShifts < 10) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasWorkedMoreThan14HoursInAShift(List<Integer> shifts) {
        for (int shift : shifts) {
            if (shift > 14) {
                return true;
            }
        }
        return false;
    }
}
