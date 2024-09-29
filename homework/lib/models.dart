class PokemonBase {
  final String name;
  final String url;
  final int number;

  const PokemonBase({
    required this.name,
    required this.url,
    required this.number,
  });

  factory PokemonBase.fromJson(Map<String, dynamic> json) {
    String name = json['name'];
    String url = json['url'];
    int number = int.parse(url.split('/')[6]);

    return PokemonBase(name: name[0].toUpperCase() + name.substring(1), url: url, number: number);
  }
}