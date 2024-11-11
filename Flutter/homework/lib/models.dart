import 'package:flutter/material.dart';

Map<String, Color> typeColors = {
  "Normal": Color.fromRGBO(168, 168, 120, 1.0),
  "Fighting": Color.fromRGBO(192, 48, 40, 1.0),
  "Flying": Color.fromRGBO(168, 144, 240, 1.0),
  "Poison": Color.fromRGBO(160, 64, 160, 1.0),
  "Ground": Color.fromRGBO(224, 192, 104, 1.0),
  "Rock": Color.fromRGBO(184, 160, 56, 1.0),
  "Bug": Color.fromRGBO(168, 184, 32, 1.0),
  "Ghost": Color.fromRGBO(112, 88, 152, 1.0),
  "Steel": Color.fromRGBO(184, 184, 208, 1.0),
  "Fire": Color.fromRGBO(240, 128, 48, 1.0),
  "Water": Color.fromRGBO(104, 144, 240, 1.0),
  "Grass": Color.fromRGBO(120, 200, 80, 1.0),
  "Electric": Color.fromRGBO(248, 208, 48, 1.0),
  "Psychic": Color.fromRGBO(248, 88, 136, 1.0),
  "Ice": Color.fromRGBO(152, 216, 216, 1.0),
  "Dragon": Color.fromRGBO(112, 56, 248, 1.0),
  "Dark": Color.fromRGBO(112, 88, 72, 1.0),
  "Fairy": Color.fromRGBO(238, 153, 172, 1.0),
  "Stellar": Color.fromRGBO(190, 140, 255, 1.0),
  "Unknown": Color.fromRGBO(104, 160, 144, 1.0),
  "Shadow": Color.fromRGBO(100, 78, 136, 1.0),
};

class Pokemon {
  String name;
  int number;
  String url;

  String image = "";
  double weight = 0;
  double height = 0;

  List<String> types = [];

  Pokemon({
    required this.name,
    required this.number,
    required this.url,
  });

  factory Pokemon.fromJson(Map<String, dynamic> json) {
    String name = json['name'];
    String url = json['url'];
    int number = int.parse(url.split('/')[6]);

    return Pokemon(
        name: name[0].toUpperCase() + name.substring(1),
        url: url,
        number: number);
  }

  void fromDetailsJson(Map<String, dynamic> json) {
    String image = json['sprites']['front_default'];
    final double weight = json['weight'] / 10;
    final double height = json['height'] / 10;

    List<dynamic> jsonTypes = json['types'];
    List<String> types = [];

    for (var type in jsonTypes) {
      types.add(type['type']['name'][0].toUpperCase() +
          type['type']['name'].substring(1));
    }

    this.height = height;
    this.weight = weight;
    this.image = image;
    this.types = types;
  }

  @override
  String toString() {
    return "{ Name : $name, Number : $number, Url : $url, Image : $image, Weight : $weight, Height : $height, Types : $types";
  }
}