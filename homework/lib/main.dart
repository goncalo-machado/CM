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
  var favorites = <Pokemon>[];
  var team = <Pokemon>[];
  var detailsClicked = false;
  List<Pokemon> pokemons = [];
  bool isLoading = true;

  void toggleFavorite(Pokemon pokemon) {
    if (isFavorite(pokemon)) {
      favorites.remove(pokemon);
    } else {
      favorites.add(pokemon);
    }
    notifyListeners();
  }

  bool isFavorite(Pokemon pokemon) {
    return favorites.contains(pokemon);
  }

  void toggleTeam(Pokemon pokemon) {
    if (isOnTeam(pokemon)) {
      team.remove(pokemon);
    } else {
      if (team.length < 6) {
        team.add(pokemon);
      }
    }
    notifyListeners();
  }

  bool isOnTeam(Pokemon pokemon) {
    return team.contains(pokemon);
  }

  Future<void> pokemonDetails(BuildContext context, Pokemon pokemon) async {
    var response = await http.get(Uri.parse(pokemon.url));
    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      pokemon.fromDetailsJson(data);
      if (context.mounted) {
        detailsClicked = false;
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => DetailsPage(pokemon: pokemon),
          ),
        );
      }
    }
  }

  Future<void> fetchPokemons() async {
    try {
      final response = await http
          .get(Uri.parse('https://pokeapi.co/api/v2/pokemon?limit=10000'));
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body)['results'] as List<dynamic>;
        pokemons = data.map((item) => Pokemon.fromJson(item)).toList();
        isLoading = false;
        notifyListeners();
      } else {
        throw Exception('Failed to load data');
      }
    } catch (e) {
      print('Error: $e');
    }
  }
}

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  @override
  void initState() {
    super.initState();
    final appState = Provider.of<MyAppState>(context, listen: false);
    appState.fetchPokemons();
  }

  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: 3,
      child: Scaffold(
          appBar: AppBar(
            title: const Text("Pokedex",
                style: TextStyle(fontFamily: 'PokemonClassic')),
            backgroundColor: Theme.of(context).colorScheme.primaryContainer,
            bottom: TabBar(
              tabs: [
                Tab(
                  text: "Pokedex",
                ),
                Tab(text: "Favorites"),
                Tab(text: "Team"),
              ],
              labelStyle: TextStyle(
                fontSize: 11.0,
                fontFamily: 'PokemonClassic',
              ),
            ),
          ),
          body: TabBarView(children: [
            PokedexPage(),
            FavoritesPage(),
            TeamPage(),
          ])),
    );
  }
}

class DetailsPage extends StatelessWidget {
  final Pokemon pokemon;

  const DetailsPage({
    required this.pokemon,
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    return Consumer<MyAppState>(builder: (context, appState, child) {
      return Scaffold(
        appBar: AppBar(
          title: Text('#${pokemon.number} ${pokemon.name}'),
          actions: [
            IconButton(
                onPressed: () {
                  appState.toggleTeam(pokemon);
                },
                icon: Icon(
                  appState.isOnTeam(pokemon) ? Icons.remove : Icons.add,
                )),
            IconButton(
                icon: Icon(
                  appState.isFavorite(pokemon)
                      ? Icons.favorite
                      : Icons.favorite_border,
                ),
                onPressed: () {
                  appState.toggleFavorite(pokemon);
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
                        image: NetworkImage(pokemon.image, scale: 0.2),
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
                            "Height ${pokemon.height} mt",
                            overflow: TextOverflow.ellipsis,
                            style: TextStyle(
                              fontSize: 12.0,
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
                            "Weight ${pokemon.weight} Kg",
                            overflow: TextOverflow.ellipsis,
                            style: TextStyle(
                              fontSize: 12.0,
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
                    for (var type in pokemon.types)
                      Chip(
                        label: Text(type),
                        backgroundColor: typeColors[type],
                        shape: const RoundedRectangleBorder(
                            borderRadius:
                                BorderRadius.all(Radius.circular(20))),
                        visualDensity:
                            const VisualDensity(horizontal: 0, vertical: -4),
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

class PokedexPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Consumer<MyAppState>(builder: (context, appState, child) {
      return appState.isLoading
          ? Center(child: CircularProgressIndicator())
          : ListView.builder(
              itemCount: appState.pokemons.length,
              itemBuilder: (context, index) {
                final pokemon = appState.pokemons[index];
                final icon = appState.isFavorite(pokemon)
                    ? Icons.favorite
                    : Icons.favorite_border;
                return ListTile(
                  title: Text(pokemon.name),
                  leading: Text("#${pokemon.number}"),
                  onTap: () => {
                    if (!appState.detailsClicked)
                      {
                        appState.pokemonDetails(context, pokemon),
                        appState.detailsClicked = true,
                      }
                  },
                  trailing: IconButton(
                      onPressed: () {
                        appState.toggleFavorite(pokemon);
                      },
                      icon: Icon(icon)),
                );
              },
            );
    });
  }
}

class FavoritesPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Consumer<MyAppState>(
      builder: (context, appState, child) {
        return appState.favorites.isEmpty
            ? Center(
                child: Text("No favorites"),
              )
            : ListView.builder(
                itemCount: appState.favorites.length,
                itemBuilder: (context, index) {
                  final pokemon = appState.favorites[index];
                  final icon = appState.isFavorite(pokemon)
                      ? Icons.favorite
                      : Icons.favorite_border;
                  return ListTile(
                    title: Text(pokemon.name),
                    leading: Text("#${pokemon.number}"),
                    onTap: () => {
                      if (!appState.detailsClicked)
                        {
                          appState.pokemonDetails(context, pokemon),
                        },
                      appState.detailsClicked = true,
                    },
                    trailing: IconButton(
                        onPressed: () {
                          appState.toggleFavorite(pokemon);
                        },
                        icon: Icon(icon)),
                  );
                },
              );
      },
    );
  }
}

// class TeamPage extends StatelessWidget {
//   @override
//   Widget build(BuildContext context) {
//     return Consumer<MyAppState>(
//       builder: (context, appState, child) {
//         return GridView.builder(
//           gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
//             crossAxisCount: 2,
//             crossAxisSpacing: 10.0,
//             mainAxisSpacing: 10.0,
//           ),
//           itemCount: appState.team.length,
//           itemBuilder: (context, index) {
//             final pokemon = appState.team[index];
//             return Card(
//               child: Column(
//                 children: [
//                   Container(
//                     height: 150,
//                     width: 150,
//                     decoration: BoxDecoration(
//                       image: DecorationImage(
//                         image: NetworkImage(pokemon.image),
//                         fit: BoxFit.cover,
//                       ),
//                     ),
//                   ),
//                   FittedBox(
//                     child: Text('#${pokemon.number} ${pokemon.name}'),
//                   ),
//                 ],
//               ),
//             );
//           },
//         );
//       },
//     );
//   }
// }

class TeamPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Consumer<MyAppState>(
      builder: (context, appState, child) {
        return GridView.builder(
          gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: 2,
            crossAxisSpacing: 10.0,
            mainAxisSpacing: 10.0,
          ),
          itemCount: 6,
          itemBuilder: (context, index) {
            final pokemon =
                index < appState.team.length ? appState.team[index] : null;
            return GestureDetector(
              onTap: () => {
                if (pokemon != null)
                  {
                    appState.pokemonDetails(context, pokemon),
                  }
              },
              child: Card(
                child: Column(
                  children: [
                    Container(
                      height: 100,
                      width: 100,
                      decoration: BoxDecoration(
                          image: pokemon != null
                              ? DecorationImage(
                                  image: NetworkImage(pokemon.image),
                                  fit: BoxFit.cover,
                                )
                              : null),
                    ),
                    Text(pokemon != null ? '#${pokemon.number} ${pokemon.name}' : "Slot #${index + 1}"),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: pokemon != null ? [
                        IconButton(
                          icon: Icon(appState.isFavorite(pokemon)
                              ? Icons.favorite
                              : Icons.favorite_border),
                          onPressed: () => appState.toggleFavorite(pokemon),
                        ),
                        IconButton(
                          icon: Icon(
                            appState.isOnTeam(pokemon)
                                ? Icons.remove
                                : Icons.add,
                          ),
                          onPressed: () => appState.toggleTeam(pokemon),
                        ),
                      ] : [],
                    ),
                  ],
                ),
              ),
            );
          },
        );
      },
    );
  }
}
