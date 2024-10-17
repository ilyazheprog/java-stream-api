package com.example.libraryapp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) {
        try {
            Gson gson = new Gson();
            // Используем InputStreamReader для загрузки файла из ресурсов
            InputStreamReader reader = new InputStreamReader(App.class.getClassLoader().getResourceAsStream("books.json"));
            if (reader == null) {
                throw new NullPointerException("Файл books.json не найден в ресурсах.");
            }

            Type visitorListType = new TypeToken<List<Visitor>>() {}.getType();
            List<Visitor> visitors = gson.fromJson(reader, visitorListType);

            // Задача 1: Вывести список посетителей и их количество
            System.out.println("Список посетителей:");
            visitors.forEach(v -> System.out.println(v.getName() + " " + v.getSurname()));
            System.out.println("Общее количество посетителей: " + visitors.size());

            // Задача 2: Вывести уникальные книги и их количество
            Set<Book> uniqueBooks = visitors.stream()
                    .flatMap(visitor -> visitor.getFavoriteBooks().stream())
                    .collect(Collectors.toSet());
            System.out.println("Уникальные книги:");
            uniqueBooks.forEach(book -> System.out.println(book.getName()));
            System.out.println("Количество уникальных книг: " + uniqueBooks.size());

            // Задача 3: Отсортировать книги по году и вывести их
            List<Book> sortedBooks = uniqueBooks.stream()
                    .sorted(Comparator.comparingInt(Book::getPublishingYear))
                    .collect(Collectors.toList());
            System.out.println("Книги, отсортированные по году издания:");
            sortedBooks.forEach(book -> System.out.println(book.getName() + " - " + book.getPublishingYear()));

            // Задача 4: Проверить наличие книг Jane Austen в избранном
            boolean hasJaneAusten = visitors.stream()
                    .flatMap(visitor -> visitor.getFavoriteBooks().stream())
                    .anyMatch(book -> "Jane Austen".equals(book.getAuthor()));
            System.out.println("Есть ли книга Jane Austen в избранном: " + (hasJaneAusten ? "Да" : "Нет"));

            // Задача 5: Максимальное число книг в избранном
            int maxFavorites = visitors.stream()
                    .mapToInt(visitor -> visitor.getFavoriteBooks().size())
                    .max()
                    .orElse(0);
            System.out.println("Максимальное число книг в избранном: " + maxFavorites);

            // Задача 6: Создание SMS сообщений
            double avgFavorites = visitors.stream()
                    .mapToInt(visitor -> visitor.getFavoriteBooks().size())
                    .average()
                    .orElse(0);

            List<SmsMessage> smsMessages = visitors.stream()
                    .filter(Visitor::isSubscribed)
                    .map(visitor -> {
                        int favoriteCount = visitor.getFavoriteBooks().size();
                        String message;
                        if (favoriteCount > avgFavorites) {
                            message = "you are a bookworm";
                        } else if (favoriteCount < avgFavorites) {
                            message = "read more";
                        } else {
                            message = "fine";
                        }
                        return new SmsMessage(visitor.getPhone(), message);
                    })
                    .collect(Collectors.toList());

            System.out.println("SMS сообщения:");
            smsMessages.forEach(sms -> System.out.println("Телефон: " + sms.getPhone() + ", Сообщение: " + sms.getMessage()));

        } catch (NullPointerException e) {
            System.err.println("Файл books.json не найден в ресурсах.");
        }
    }
}
