package examples;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StreamExamples {
    public static void main(String... args) {

        final ArrayList<User> users = new ArrayList<>();

        users.add(new User(5,"Петя","23@gmail.com"));
        users.add(new User(3,"Вася","12@gmail.com"));
        users.add(new User(1,"Жора","33@gmail.com"));
        users.add(new User(22,"Серега","44@gmail.com"));
        users.add(new User(22,"Паша","44@gmail.com"));
        users.add(new User(22,"Леша","44@gmail.com"));
        users.add(new User(22,"Андрей","44@gmail.com"));
        users.add(new User(22,"Жорж","44@gmail.com"));

        final Consumer<User> namePrinter = (u) -> {
            System.out.println(u.name);
        };

        final Function<User,String> emailMappingFunction = (u) -> u.email;

        System.out.println("---------------");
        users.stream().forEach(namePrinter);

        System.out.println("---------------");
        final Comparator<User> numberComparator = Comparator.comparing(u -> u.number);
        users.stream().sorted(numberComparator.reversed()).forEach(namePrinter);

        System.out.println("---------------");
        final List<String> emails = users.stream().map(emailMappingFunction).collect(Collectors.toList());

        emails.stream().forEach(System.out::println);


        System.out.println("---------------");
        Map<Integer, String> numberToNameMap = users.stream().collect(Collectors.toMap(u -> u.number, u -> u.name,(u1,u2)-> u1));

        for (var e: numberToNameMap.entrySet()) {
            System.out.println(e.getKey() + "->" + e.getValue());
        }

        System.out.println("---------------");
        Map<Integer, List<User>> collect = users.stream().collect(Collectors.groupingBy(u -> u.number));
        for (var e : collect.entrySet()) {
            System.out.println(e.getKey() + "->" +
                    String.join(",", e.getValue().stream().map(u -> u.name).collect(Collectors.toList())));
        }
    }


    public static class User {
        public Integer number;
        public String name;
        public String email;

        public User(Integer n,String name, String email) {
            this.number = n;
            this.name = name;
            this.email = email;
        }
    }
}
