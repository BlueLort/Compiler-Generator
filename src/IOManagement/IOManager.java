package IOManagement;

import java.io.*;
import java.util.ArrayList;

/* single instance class to deal with files for writing or reading */
public class IOManager {
    private static IOManager instance = null;



    private IOManager(){

    }

    public static IOManager GetInstance(){
        if(instance == null){
            instance = new IOManager();
        }
        return instance;
    }


    public void WriteFile(String s, String filePath) {
        File file = new File(filePath);

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(s);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Reading in binary format to make it easier to read line feeds and new lines */
    public String ReadFile(String filePath) {
        File file = new File(filePath);
        ArrayList<Byte> output=new ArrayList<>();
        try {
            FileInputStream fin=new FileInputStream(file);
            int data;
            while((data=fin.read())!=-1){
                output.add((byte)data);
            }
            fin.close();
        }catch (Exception e) {
            e.printStackTrace();
        }

        return GetStringFromByteList(output);
    }

    private String GetStringFromByteList(ArrayList<Byte> bytes){
        byte[] arr=new byte[bytes.size()];
        for(int i=0;i<bytes.size();i++){
            arr[i]=bytes.get(i);
        }
        return new String(arr);
    }

}
