import 'package:flutter/material.dart';
import 'package:nakama_demo/nakama_controller.dart';
import 'package:provider/provider.dart';

void main() {
  runApp(const MainApp());
}

class MainApp extends StatefulWidget {
  const MainApp({super.key});

  @override
  State<MainApp> createState() => _MainApp();
}

class _MainApp extends State<MainApp> {
  late final NakamaController controller;

  @override
  void initState() {
    super.initState();
    controller = NakamaController();
    controller.connectToNakama();
  }

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
      create: (context) => controller,
      child: const MaterialApp(
        home: HomePage(),
      ),
    );
  }
}

class HomePage extends StatelessWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    NakamaController nakamaController = Provider.of<NakamaController>(context);

    return Scaffold(
      appBar: AppBar(
        title: const Text("Game of Serious"),
      ),
      body: Center(
        child: Column(
          children: [
            Text("Username: ${nakamaController.username}"),
            Text("Session: ${nakamaController.userId}"),
            TextField(
              decoration: const InputDecoration(
                border: OutlineInputBorder(),
                hintText: 'Insert username',
              ),
              onChanged: (value) {
                nakamaController.setUsername(value);
              },
            ),
            TextButton(
                onPressed: () async {
                  await nakamaController.createPlayerSession();
                  if (context.mounted) {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => const PlayPage(),
                      ),
                    );
                  }
                },
                child: const Text("Play")),
          ],
        ),
      ),
    );
  }
}

class PlayPage extends StatefulWidget {
  const PlayPage({super.key});

  @override
  State<PlayPage> createState() => _PlayPage();
}

class _PlayPage extends State<PlayPage> {
  final TextEditingController _lobbyIdController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    NakamaController nakamaController = Provider.of<NakamaController>(context);

    return Scaffold(
      appBar: AppBar(
        title: const Text("Game of Serious"),
      ),
      body: Center(
        child: Column(
          children: [
            Text(nakamaController.username),
            const SizedBox(height: 10),
            ElevatedButton(
              onPressed: () async {
                await nakamaController.createMatch();
                if (context.mounted) {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => const LobbyPage(),
                    ),
                  );
                }
              },
              child: const Text("Create Match"),
            ),
            const SizedBox(height: 10),
            TextField(
              controller: _lobbyIdController,
              decoration: const InputDecoration(
                border: OutlineInputBorder(),
                hintText: 'Insert username',
              ),
            ),
            ElevatedButton(
              onPressed: () async {
                await nakamaController.joinMatch(_lobbyIdController.text);
                if (context.mounted) {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => const LobbyPage(),
                    ),
                  );
                }
              },
              child: const Text("Join Match"),
            ),
          ],
        ),
      ),
    );
  }
}

class LobbyPage extends StatelessWidget {
  const LobbyPage({super.key});

  @override
  Widget build(BuildContext context) {
    NakamaController nakamaController = Provider.of<NakamaController>(context);

    return Scaffold(
      appBar: AppBar(
        title: const Text("Game of Serious"),
      ),
      body: Center(
        child: Column(
          children: [
            Text("LobbyId: ${nakamaController.lobbyId}"),
            ElevatedButton(
                onPressed: () async {
                  await nakamaController.leaveMatch();
                },
                child: Text("Leave Match"),),
            SizedBox(height: 10),
            Text("Players in Lobby"),
            SizedBox(
              height: 500,
              child: ListView.builder(
                  itemCount: nakamaController.connectedOpponents.length,
                  itemBuilder: (context, index) {
                    return Text(
                        "User ${nakamaController.connectedOpponents[index].username}");
                  }),
            ),
            ElevatedButton(onPressed:() {
              nakamaController.sendMessage();
            }, child: Text("Send Hello"),),
          ],
        ),
      ),
    );
  }
}
