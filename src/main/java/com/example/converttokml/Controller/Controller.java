package com.example.converttokml.Controller;

import com.example.converttokml.Service.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/csvtokml")
public class Controller {
    @Autowired
    Convert convert;
    @GetMapping("/{fileNameCsv}/{fileNameKml}")
    public void readFile(@PathVariable String fileNameCsv, @PathVariable String fileNameKml) throws IOException {
        convert.readFile(fileNameCsv);
        convert.createFile(fileNameKml);
        System.out.println(convert.getHeaders());
        System.out.println(convert.getValues());
    }
}
