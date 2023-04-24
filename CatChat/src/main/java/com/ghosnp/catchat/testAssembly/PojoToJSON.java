package com.ghosnp.catchat.testAssembly;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PojoToJSON {

    public static void main(String[] args) throws IOException {
        Student student = new Student();
        student.setId(1L);
        student.setName("Alice");
        student.setAge(20);
        student.setCourses(Arrays.asList("Engineering", "Finance", "Chemistry"));

        JSONObject object = new JSONObject(student);
        String json = object.toString();
        System.out.println(json);
        System.out.println(new Gson().toJson(student));

        List<Student> ls = new ArrayList<>();
        ls.add(student);
        ls.add(student);
        ls.add(student);
        String sl = new Gson().toJson(ls);

        // Creating Object of ObjectMapper define in Jackson API
        ObjectMapper Obj = new ObjectMapper();

        // Converting the Java object into a JSON string
        String jsonStr = Obj.writeValueAsString(student);
        // Displaying Java object into a JSON string
        System.out.println(jsonStr);

        ObjectMapper occ = new ObjectMapper();
        Student std = occ.readValue(jsonStr,Student.class);

        System.out.println(std.getId());

        File testJson = new File("test.json");

        if (testJson.exists()) {
            testJson.delete();
        }
        testJson.createNewFile();
        BufferedWriter write = new BufferedWriter(new PrintWriter(testJson, StandardCharsets.UTF_8));
        write.write(sl);
        write.flush();
        write.close();
    }
}