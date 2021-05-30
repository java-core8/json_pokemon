package ru.tcreator;

import com.google.gson.annotations.SerializedName;

public class Pokemon {
    @SerializedName("id")
    private final int id;
    @SerializedName("name")
    private final String name;
    @SerializedName("height")
    private final int height;
    @SerializedName("weight")
    private final int weight;
    @SerializedName("order")
    private final int order;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getHeight() {
        return height;
    }

    public int getWeight() {
        return weight;
    }

    public int getOrder() {
        return order;
    }


    public Pokemon(int id, String name, int height, int order, int weights) {
        this.id = id;
        this.name = name;
        this.height = height;
        this.order = order;
        this.weight = weights;
    }


    @Override
    public String toString() {
        return "Pokemon{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", height=" + height +
                ", weight=" + weight +
                ", order='" + order + '\'' +
                '}';
    }
}
