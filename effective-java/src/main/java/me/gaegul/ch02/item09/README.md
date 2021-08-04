# 아이템 9. try-finally보다 try-with-resources를 사용하라

- 전통적으로 자원을 닫힘을 보장하는 수단
    - try-finally
        - readLine 메서드가 예외를 던지면 close도 실패한다.
        - close에서 발생한 예외가 readLine의 예외를 덮어쓰게 되어 디버깅이 어려워진다.

    ```java
    public static String firstLineOfFile(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        try {
            return br.readLine();
        } finally {
            br.close();
        }
    }
    ```

    - 자원을 둘 이상인 경우 try-finally

    ```java
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
    ```

- try-with-resources 사용하는 방법

    ```java
    public static String firstLineOfFile(String path) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            return br.readLine();
        }
    }

    public static void copy(String src, String dst) throws IOException {
        try (InputStream in = new FileInputStream(src);
             OutputStream out = new FileOutputStream(dst)) {
            byte[] buf = new byte[BUFFER_SIZE];
            int n;
            while ((n = in.read(buf)) >= 0) {
                out.write(buf);
            }
        }
    }
    ```

    - close 메서드 시그니처를 가진 AutoCloseable 인터페이스를 사용한 구현체로 자원을 해제한다.
    - try-with-resources 방식이 가독성 있고 문제를 진단하기 쉽다.
    - readLine 메서드와 close 메서드 둘 다 예외가 발생하는 경우 readLine에서 발생한 예외가 기록된다.