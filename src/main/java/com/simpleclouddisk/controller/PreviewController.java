package com.simpleclouddisk.controller;

import com.simpleclouddisk.config.MinioConfig;
import com.simpleclouddisk.service.PreviewService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/preview")
public class PreviewController {

    @Autowired
    private PreviewService previewService;

    @GetMapping("/thumbnail/{fileId}")
    public void thumbnail(@PathVariable String fileId, HttpServletResponse response) throws IOException {
        long id = Long.parseLong(fileId);
        previewService.image(id,response);
    }

}
