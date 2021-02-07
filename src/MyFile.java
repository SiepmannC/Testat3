import java.io.*;


class MyFile {

    private String fileName;


    MyFile(String fileName) {
        this.fileName = fileName;
    }

    String read(int lineNo) {
        String answer = "ERROR cannot open file";
        BufferedReader file = null;
        try {
            file = new BufferedReader(new FileReader(fileName));
            String s = " ERROR Line not found";
            for (int i = 0; (i < lineNo) && (s != null); i++) {
                s = file.readLine();
            }
            if (s != null) {
                answer = s;
            } else {
                answer = "ERROR Line not found";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (file != null) {
            try {
                file.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return answer;
    }//read

    String write(int lineNo, String data) {


        String answer = "";
        BufferedReader inFile = null;
        PrintWriter outFile = null;
        boolean found = false;

        try {

            //String fileContents = "";
			System.out.println(fileName);
            inFile = new BufferedReader(new FileReader(fileName));
            outFile = new PrintWriter(new FileWriter(fileName + ".temp"));
            answer = "WRITE Faild";
            String s = "";

            for (int i = 0; (s != null); i++) {
                s = inFile.readLine();
                if (i == lineNo - 1) {
                    found = true;
                    outFile.println(data);
                } else if (s != null) {
                    outFile.println(s);
                }
            }//for
        } catch (Exception e) {
            e.printStackTrace();
        }//catch
        if (inFile != null) {
            try {
                inFile.close();
            } catch (Exception e) {
                e.printStackTrace();
            }//Try/chatch
        }
        if (outFile != null) {
            try {
                outFile.close();
            } catch (Exception e) {
                e.printStackTrace();
            }//Try/chatch
        }

        if (found) {
            answer = data;
            try {
                File f1 = new File(fileName);
                File f2 = new File(fileName + ".temp");
                File f3 = new File(fileName + ".bak");
                f3.delete();
                f1.renameTo(f3);
                f2.renameTo(f1);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }//if

        return answer;
    }//write
}//class