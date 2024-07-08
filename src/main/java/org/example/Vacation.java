package org.example;

import java.util.Arrays;
import java.util.Comparator;

import static org.example.Main.vacations;

public class Vacation {
    private static long idCounter = vacations.stream()
            .filter(v -> v.getId() != vacations.stream().max(Comparator.comparingLong(Vacation::getId))
                    .orElse(new Vacation(0, "", "", "", "", new String[]{}, 0.0, "", new int[0])).getId())
            .map(Vacation::getId)
            .findFirst()
            .orElse((long) 0);

    private long id;
    private String title;
    private String country;
    private String city;
    private String season;
    private String[] photos;
    private double price;
    private String description;
    private int[] rating;

    public String[] getPhotos() {
        return photos;
    }

    public void setPhotos(String[] photos) {
        this.photos = photos;
    }

    public Vacation() {
    }

    public Vacation(String title, String country, String city, String season, String[] photos, double price, String description) {
        //create
        this.id = ++idCounter;
        this.title = title;
        this.country = country;
        this.city = city;
        this.season = season;
        this.photos = photos;
        this.price = price;
        this.description = description;
        this.rating = new int[]{};
    }

    public Vacation(long id, String title, String country, String city, String season, String[] photos, double price, String description, int[] rating) {
        //edit
        this.id = id;
        this.title = title;
        this.country = country;
        this.city = city;
        this.season = season;
        this.photos = photos;
        this.price = price;
        this.description = description;
        this.rating = rating;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = this.country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = this.city;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = this.season;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int[] getRating() {
        return rating;
    }

    public void setRating(int[] rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Vacation{" +
                "title='" + title + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", season='" + season + '\'' +
                ", photos='" + photos + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", rating=" + Arrays.toString(rating) +
                '}';
    }
}
