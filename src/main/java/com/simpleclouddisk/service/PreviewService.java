package com.simpleclouddisk.service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface PreviewService {
    void image(Long fileId, HttpServletResponse response) throws IOException;
}
