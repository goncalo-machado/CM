import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:nakama/nakama.dart';
import 'dart:math';
import 'dart:async';

class NakamaController extends ChangeNotifier {
  static const String _chars = 'ABCDEF1234567890';

  late final NakamaBaseClient _nakamaClient;
  late final Session _playerSession;
  late final NakamaWebsocketClient _socket;
  late Match _match;
  late StreamSubscription<MatchPresenceEvent> presenceSubscription;
  late StreamSubscription<MatchData> dataSubscription;
  final Random _rnd = Random();
  List<UserPresence> connectedOpponents = [];

  String username = "";
  String userId = "";
  String lobbyId = "";

  void setUsername(String username) {
    this.username = username;
    notifyListeners();
  }

  String getRandomString(int length) => String.fromCharCodes(Iterable.generate(
      length, (_) => _chars.codeUnitAt(_rnd.nextInt(_chars.length))));

  Future<void> connectToNakama() async {
    _nakamaClient = getNakamaClient(
      host: '127.0.0.1',
      ssl: false,
      serverKey: 'defaultkey',
    );
  }

  Future<void> createPlayerSession() async {
    _playerSession = await _nakamaClient.authenticateDevice(
        deviceId: 'useranonymouse$username', username: username);

    userId = _playerSession.userId;
    notifyListeners();

    print("Username $username");

    _socket = NakamaWebsocketClient.init(
      host: '127.0.0.1',
      ssl: false,
      token: _playerSession.token,
    );
  }

  Future<void> createMatch() async {
    lobbyId = getRandomString(8);
    presenceSubscription = _socket.onMatchPresence.listen((event) {
      connectedOpponents.removeWhere((opponent) =>
          event.leaves.any((leave) => leave.userId == opponent.userId));
      connectedOpponents.addAll(event.joins);
      notifyListeners();
    });
    _socket.onMatchData.listen((data) {
      final content = utf8.decode(data.data);
      switch (data.opCode) {
        case 101:
          print('A custom opcode.');
        default:
          print('User ${data.presence.userId} sent $content');
      }
    });
    _match = await _socket.createMatch(lobbyId);
    print('Match created with ID: $lobbyId');
    print('Match created with id: ${_match.matchId}');
  }

  Future<void> joinMatch(String lobbyId) async {
    this.lobbyId = lobbyId.toUpperCase();
    presenceSubscription = _socket.onMatchPresence.listen((event) {
      connectedOpponents.removeWhere((opponent) =>
          event.leaves.any((leave) => leave.userId == opponent.userId));
      connectedOpponents.addAll(event.joins);
      notifyListeners();
    });
    _socket.onMatchData.listen((data) {
      final content = utf8.decode(data.data);
      switch (data.opCode) {
        case 101:
          print('A custom opcode.');
        default:
          print('User ${data.presence.userId} sent $content');
      }
    });
    _match = await _socket.createMatch(this.lobbyId);
    print('Match joined with ID: ${this.lobbyId}');
    print('Match joined with id: ${_match.matchId}');
  }

  Future<void> leaveMatch() async {
    await _socket.leaveMatch(_match.matchId);
    print('Left match with id: ${_match.matchId}');
    connectedOpponents = [];
    notifyListeners();
    await presenceSubscription.cancel();
    await dataSubscription.cancel();
  }

  void sendMessage() {
    final state = jsonEncode({'Hello': 'Im user $username'});
    _socket.sendMatchData(
        matchId: _match.matchId, opCode: Int64(1), data: utf8.encode(state));
  }
}
