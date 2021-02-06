package pl.graduation.util;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import pl.graduation.model.Tool;
import pl.graduation.repository.ToolRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class ExcelReader {

    public static void fillDB(ToolRepository toolRepository, String filename) throws IOException {
        ClassPathResource resource = new ClassPathResource(filename);
        InputStream inputStream = resource.getInputStream();
        Workbook workbook = new XSSFWorkbook(inputStream);

        Sheet sheet = workbook.getSheet("WorksheetPL");
        for (int i = 1; i < 52; i++) {
            Tool tool = new Tool();
            for (int j = 0; j < 16; j++) {
                if (sheet.getRow(i).getCell(j) != null)
                    switch (j) {
                        case 0:
                            tool.setName(sheet.getRow(i).getCell(j).toString());
                            break;
                        case 2:
                            tool.setFullDescription(sheet.getRow(i).getCell(j).toString());
                            break;
                        case 5:
                            tool.setIconURL(sheet.getRow(i).getCell(j).toString());
                            break;
                        case 10:
                            List<String> keywords = Arrays.stream(sheet.getRow(i).getCell(j).toString().split(",")).map(String::trim).collect(Collectors.toList());
                            tool.setKeywords(keywords);
                            break;
                        case 11:
                            List<String> categories = Arrays.stream(sheet.getRow(i).getCell(j).toString().split(",")).map(String::trim).collect(Collectors.toList());
                            tool.setCategories(categories);
                            break;
                        case 12:
                            tool.setDirectLinkURL(sheet.getRow(i).getCell(j).toString());
                            break;
                        case 13:
                            List<String> platforms = Arrays.stream(sheet.getRow(i).getCell(j).toString().split(",")).map(String::trim).collect(Collectors.toList());
                            tool.setPlatforms(platforms);
                            break;
                        case 15:
                            tool.setShortDescription(sheet.getRow(i).getCell(j).toString());
                            break;
                    }
            }
            toolRepository.save(tool);
        }
    }

}
