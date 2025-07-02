package emh.dd_site;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SheetsController {

    private final SheetsService service;

    @Value("${google.sheet.range}")
    private String sheetRange;

    @RequestMapping("/api/events")
    public List<List<Object>> home() throws IOException {
        return service.getData(sheetRange);
    }
}