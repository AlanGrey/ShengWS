package com.jstudio.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by Jason
 */
public class IOUtils {

    /**
     * 将类序列化到文件
     *
     * @param obj  待序列化的对象
     * @param file 写入对象的文件
     * @throws IOException
     */
    public static void writeObj(Serializable obj, File file) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        oos.writeObject(obj);
        oos.close();
    }

    /**
     * 从文件中读取出序列化对象
     * @param file 待读取的文件
     * @return 读取出来的对象
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object readObj(File file) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        Object object = ois.readObject();
        ois.close();
        return object;
    }

}
