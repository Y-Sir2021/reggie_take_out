import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class test {
    @Test
    public void testSonAndFather() {
        List<People> peopleList = new ArrayList<>();
        People people = new People();
        people.age = 12;
        people.name = "bob";
        peopleList.add(people);

        List<Student> studentList = new ArrayList<>();
        Student student = new Student();
        student.age = 10;
        student.name = "Jon";
        student.id = 19;
        studentList.add(student);


    }
}
