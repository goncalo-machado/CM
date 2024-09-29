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
    String url = json['url'];
    int number = int.parse(url.split('/')[6]);

    return PokemonBase(name: json['name'], url: url, number: number);
  }
}