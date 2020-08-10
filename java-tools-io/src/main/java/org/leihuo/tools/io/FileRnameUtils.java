package org.leihuo.tools.io;

import java.io.File;

/**
 * @Description:
 * @Company：com.ylz
 * @Auther: 段志鹏
 * @Date: 2020/8/7 18:14
 */
public class FileRnameUtils {

    public static void main(String[] args) throws Exception {

        String filebase = "E:\\软件考\\云阅读\\系统架构设计师_架构概念\\";


        reNameFile(new File(filebase));

    }




    public static void reNameFile(File f){
        //File f = new File(filebase);
        String filebase = "E:\\软件考\\云阅读\\系统架构设计师_架构概念_DOC\\";

        File[] files = f.listFiles();
        if(files!=null&&files.length>0){
            for (File file : files) {
                if(file.isDirectory()){
                    reNameFile(file);
                }else  if(file.isFile()){
                    String fileName = file.getName();
                    int lastIndexOf = fileName.lastIndexOf(".");
                    //获取文件的后缀名 .jpg
                    String suffix = fileName.substring(lastIndexOf);
                    String fn = fileName.substring(0,lastIndexOf);
                    if(".html".equals(suffix)){
                        String rfile = file.getParent()+"\\"+fn+".doc";
                        System.out.println(fileName);


                        System.out.println(rfile );
                        System.out.println("-----------------------");
                        file.renameTo(new File(rfile));
                    }
                }
            }
        }

    }
}
