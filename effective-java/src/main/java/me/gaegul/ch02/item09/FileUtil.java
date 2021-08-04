package me.gaegul.ch02.item09;

import java.io.*;

public class FileUtil {

    private static final String path = "";
    private static int BUFFER_SIZE = 1024;

    public static String firstLineOfFile(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        try {
            return br.readLine();
        } finally {
            br.close();
        }
    }

    public static void copy(String src, String dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                byte[] buf = new byte[BUFFER_SIZE];
                int n;
                while ((n = in.read(buf)) >= 0) {

                }
            } finally {
              out.close();
            }
        } finally {
            in.close();
        }
    }
}
