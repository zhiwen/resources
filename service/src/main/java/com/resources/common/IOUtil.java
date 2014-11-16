package com.resources.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class IOUtil {

    private final static Logger log = LoggerFactory.getLogger(IOUtil.class);

    public static void writeFile(File file, String data, boolean append) {
        FileWriterWithEncoding fw = null;
        try {
            fw = new FileWriterWithEncoding(file, Charsets.UTF_8, append);
            fw.write(data);
            fw.write("\n");
            fw.flush();
        } catch (IOException e) {
            throw new RuntimeException("文件创建或者写入失败!");
        } finally {
            try {
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException e) {
                throw new RuntimeException("文件关闭失败!");
            }
        }

    }

    public static List<String> readFile(File file) throws IOException {
        try {
            return Files.readLines(file, Charsets.UTF_8);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public static ByteArrayOutputStream getByteData(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int len = 0;
        byte[] b = new byte[1024];
        while ((len = in.read(b)) > 0) {
            out.write(b, 0, len);
        }
        return out;
    }

}
