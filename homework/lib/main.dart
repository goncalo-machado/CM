import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'models.dart';


void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Pokedex Demo',
      theme: ThemeData(
        useMaterial3: true,
        colorScheme: ColorScheme.fromSeed(
            seedColor: const Color.fromARGB(255, 60, 62, 209)),
        fontFamily: 'PokemonClassic',
      ),
      home: HomePage(),
    );
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

  Future<void> _pokemonDetails(BuildContext context, String url) async {
    var response = await http.get(Uri.parse(url));
    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      if (context.mounted) {
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => DetailsPage(data: data),
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
    } else {
      // Handle error
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
      drawer: Drawer(
        child: ListView(
          padding: EdgeInsets.zero,
          children: [
            SizedBox(
              height: 100,
              child: DrawerHeader(
                decoration: BoxDecoration(
                  color: Theme.of(context).colorScheme.primaryContainer,
                ),
                child: Text('Menu'),
              ),
            ),
            ListTile(
              title: const Text('Pokedex'),
              onTap: () {
                print("Item 1");
              },
            ),
            ListTile(
              title: const Text('Team'),
              onTap: () {
                print("Item 2");
              },
            ),
            ListTile(
              title: const Text('Favorites'),
              onTap: () {
                print("Item 2");
              },
            ),
          ],
        ),
      ),
      body: isLoading
          ? Center(child: CircularProgressIndicator())
          : ListView(
              children: [
                for (var pokemon in pokemons)
                  ListTile(
                    title: Text(pokemon.name),
                    leading: Text("#${pokemon.number}"),
                    onTap: () => _pokemonDetails(context, pokemon.url),
                  )
              ],
            ),
    );
  }
}

class DetailsPage extends StatelessWidget {
  final dynamic data;

  const DetailsPage({
    required this.data,
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    print(data);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Second Route'),
      ),
      body: Center(
        child: ElevatedButton(
          onPressed: () {
            Navigator.pop(context);
          },
          child: Text('Data: $data!'),
        ),
      ),
    );
  }
}
