import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:provider/provider.dart';
import 'dart:convert';
import 'models.dart';
import 'dart:async';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
      create: (context) => MyAppState(),
      child: MaterialApp(
        title: 'Pokedex Demo',
        theme: ThemeData(
          useMaterial3: true,
          colorScheme: ColorScheme.fromSeed(
              seedColor: const Color.fromARGB(255, 60, 62, 209)),
          fontFamily: 'PokemonClassic',
        ),
        home: HomePage(),
      ),
    );
  }
}

class MyAppState extends ChangeNotifier {
  var favorites = <PokemonBase>[];

  void toggleFavorite(PokemonBase pokemon) {
    if (isFavorite(pokemon)) {
      favorites.remove(pokemon);
    } else {
      favorites.add(pokemon);
    }
    notifyListeners();
  }

  bool isFavorite(PokemonBase pokemon) {
    return favorites.contains(pokemon);
  }
}

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  List<PokemonBase> pokemons = [];
  bool isLoading = true;
  bool detailsClicked = false;

  @override
  void initState() {
    super.initState();
    fetchPokemons();
  }

  Future<void> fetchPokemons() async {
    try {
      final response = await http
          .get(Uri.parse('https://pokeapi.co/api/v2/pokemon?limit=10000'));
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body)['results'] as List<dynamic>;
        setState(() {
          pokemons = data.map((item) => PokemonBase.fromJson(item)).toList();
          isLoading = false;
        });
      } else {
        throw Exception('Failed to load data');
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  Future<void> _pokemonDetails(
      BuildContext context, PokemonBase pokemon) async {
    var response = await http.get(Uri.parse(pokemon.url));
    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      final details = PokemonDetails.fromJson(data);
      if (context.mounted) {
        detailsClicked = false;
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) =>
                DetailsPage(details: details, pokemon: pokemon),
          ),
        );
        // Navigator.push(
        //     context,
        //     PageRouteBuilder(
        //         pageBuilder: (context, animation, secondaryAnimation) =>
        //             DetailsPage(data: data),
        //         transitionsBuilder:
        //             (context, animation, secondaryAnimation, child) {
        //           const begin = Offset(1.0, 0.0);
        //           const end = Offset.zero;
        //           const curve = Curves.ease;

        //           var tween = Tween(begin: begin, end: end)
        //               .chain(CurveTween(curve: curve));

        //           return SlideTransition(
        //             position: animation.drive(tween),
        //             child: child,
        //           );
        //         }));
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Pokedex",
            style: TextStyle(fontFamily: 'PokemonClassic')),
        backgroundColor: Theme.of(context).colorScheme.primaryContainer,
      ),
      // drawer: Drawer(
      //   child: ListView(
      //     padding: EdgeInsets.zero,
      //     children: [
      //       SizedBox(
      //         height: 100,
      //         child: DrawerHeader(
      //           decoration: BoxDecoration(
      //             color: Theme.of(context).colorScheme.primaryContainer,
      //           ),
      //           child: Text('Menu'),
      //         ),
      //       ),
      //       ListTile(
      //         title: const Text('Pokedex'),
      //         onTap: () {
      //           print("Item 1");
      //         },
      //       ),
      //       ListTile(
      //         title: const Text('Team'),
      //         onTap: () {
      //           print("Item 2");
      //         },
      //       ),
      //       ListTile(
      //         title: const Text('Favorites'),
      //         onTap: () {
      //           print("Item 2");
      //         },
      //       ),
      //     ],
      //   ),
      // ),
      body: isLoading
          ? Center(child: CircularProgressIndicator())
          : Consumer<MyAppState>(
              builder: (context, value, child) {
                return ListView.builder(
                  itemCount: pokemons.length,
                  itemBuilder: (context, index) {
                    final pokemon = pokemons[index];
                    final icon = value.isFavorite(pokemon)
                        ? Icons.favorite
                        : Icons.favorite_border;
                    return ListTile(
                      title: Text(pokemon.name),
                      leading: Text("#${pokemon.number}"),
                      onTap: () => {
                        if (!detailsClicked)
                          {_pokemonDetails(context, pokemon)},
                        detailsClicked = true,
                      },
                      trailing: IconButton(
                          onPressed: () {
                            value.toggleFavorite(pokemon);
                          },
                          icon: Icon(icon)),
                    );
                  },
                );
              },
            ),
    );
  }
}

class DetailsPage extends StatelessWidget {
  final PokemonDetails details;
  final PokemonBase pokemon;

  const DetailsPage({
    required this.details,
    required this.pokemon,
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    print(details);
    // for (var type in details.types) {
    //   Color? color = typeColors["Fire"];
    //   Color? color2 = typeColors[type];
    //   print(color.toString());
    //   print(color2.toString());
    // }

    return Consumer<MyAppState>(builder: (context, value, child) {
      return Scaffold(
        appBar: AppBar(
          title: Text('#${details.number} ${details.name}'),
          actions: [
            IconButton(
                icon: Icon(value.isFavorite(pokemon)
                    ? Icons.favorite
                    : Icons.favorite_border),
                onPressed: () {
                  value.toggleFavorite(pokemon);
                }),
          ],
        ),
        body: Center(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Container(
                  height: 260.0,
                  width: 260.0,
                  decoration: BoxDecoration(
                      border: Border.all(color: Colors.grey),
                      image: DecorationImage(
                        image: NetworkImage(details.image, scale: 0.2),
                        fit: BoxFit.cover,
                      ))),
              const Divider(
                thickness: 1,
                endIndent: 0,
                color: Colors.grey,
              ),
              Padding(
                padding: const EdgeInsets.fromLTRB(10, 0, 10, 0),
                child: Row(
                  children: [
                    Expanded(
                      child: Container(
                        decoration: BoxDecoration(
                          border: Border.all(color: Colors.grey),
                        ),
                        child: Padding(
                          padding: const EdgeInsets.all(8.0),
                          child: Text(
                            "Height ${details.height} mt",
                            overflow: TextOverflow.ellipsis,
                            style: TextStyle(
                              fontSize: 12.0, // Set the desired font size
                            ),
                          ),
                        ),
                      ),
                    ),
                    Expanded(
                      child: Container(
                        decoration: BoxDecoration(
                          border: Border.all(color: Colors.grey),
                        ),
                        child: Padding(
                          padding: const EdgeInsets.all(8.0),
                          child: Text(
                            "Weight ${details.weight} Kg",
                            overflow: TextOverflow.ellipsis,
                            style: TextStyle(
                              fontSize: 12.0, // Set the desired font size
                            ),
                          ),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
              Padding(
                padding: const EdgeInsets.fromLTRB(10, 10, 0, 0),
                child: Row(
                  children: [
                    Text("Types:"),
                    for (var type in details.types)
                      Chip(
                        label: Text(type),
                        backgroundColor: typeColors[type],
                        shape: const RoundedRectangleBorder(
                            borderRadius:
                                BorderRadius.all(Radius.circular(20))),
                        visualDensity: const VisualDensity(
                            horizontal: 0, vertical: -4), // Add this line
                      ),
                  ],
                ),
              ),
            ],
          ),
        ),
      );
    });
  }
}
