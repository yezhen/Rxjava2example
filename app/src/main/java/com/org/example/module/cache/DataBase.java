package com.org.example.module.cache;

import android.content.res.Resources;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.org.example.App;
import com.org.example.model.ItemBean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

public class DataBase {
    private static final String DATA_FILE_NAME = "data.db";

    private File datafile = new File(App.getInstance().getFilesDir(), DATA_FILE_NAME);

    private static DataBase instance;

    private Gson gson = new Gson();

    public DataBase() {
    }

    public static DataBase getInstance() {
        if (instance == null) {
            instance = new DataBase();
        }
        return instance;
    }

    public List<ItemBean> readItems() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Reader reader = new FileReader(datafile);
            return gson.fromJson(reader, new TypeToken<List<ItemBean>>() {
            }.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void writeItems(List<ItemBean> items) {
        String json = gson.toJson(items);
        try {
            if (!datafile.exists()) {
                datafile.createNewFile();
            }
            Writer writer = new FileWriter(datafile);
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        datafile.delete();
    }
}


