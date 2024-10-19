import 'dart:async';

import 'package:flutter/material.dart';

import 'package:watch_connectivity/watch_connectivity.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final _watch = WatchConnectivity();

  var _supported = false;
  var _paired = false;
  var _reachable = false;
  var _context = <String, dynamic>{};
  var _receivedContexts = <Map<String, dynamic>>[];
  final _log = <String>[];

  Timer? timer;

  @override
  void initState() {
    super.initState();

    _watch.messageStream
        .listen((e) => setState(() => _log.add('Received message: $e')));

    _watch.contextStream
        .listen((e) => setState(() => _log.add('Received context: $e')));

    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  void initPlatformState() async {
    _supported = await _watch.isSupported;
    _paired = await _watch.isPaired;
    _reachable = await _watch.isReachable;
    _context = await _watch.applicationContext;
    _receivedContexts = await _watch.receivedApplicationContexts;
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        body: Center(
          child: SingleChildScrollView(
            padding: const EdgeInsets.all(16),
            child: SafeArea(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const SizedBox(height: 8),
                  const Text('Send'),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      TextButton(
                        onPressed: startGame,
                        child: const Text('Start Game'),
                      ),
                      const SizedBox(width: 8),
                      TextButton(
                        onPressed: endGame,
                        child: const Text('End Game'),
                      ),
                    ],
                  ),
                  const SizedBox(width: 16),
                  const Text('Log'),
                  ..._log.reversed.map(Text.new),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }

  void startGame() {
    final message = {'Command': 'START_GAME'};
    _watch.sendMessage(message);
    setState(() => _log.add('Sent message: $message'));
  }

  void endGame() {
    final message = {'Command': 'END_GAME'};
    _watch.sendMessage(message);
    setState(() => _log.add('Sent message: $message'));
  }

  // void sendContext() {
  //   _count++;
  //   final context = {'data': _count};
  //   _watch.updateApplicationContext(context);
  //   setState(() => _log.add('Sent context: $context'));
  // }

  // void toggleBackgroundMessaging() {
  //   if (timer == null) {
  //     timer = Timer.periodic(const Duration(seconds: 1), (_) => sendMessage());
  //   } else {
  //     timer?.cancel();
  //     timer = null;
  //   }
  //   setState(() {});
  // }
}
