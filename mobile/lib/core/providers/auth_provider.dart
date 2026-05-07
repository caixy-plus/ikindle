import 'package:flutter/foundation.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../data/models/login_request.dart';
import '../../data/models/user.dart';
import '../api/auth_interceptor.dart';
import 'api_provider.dart';

final authStateProvider = AsyncNotifierProvider<AuthNotifier, User?>(
  AuthNotifier.new,
);

class AuthNotifier extends AsyncNotifier<User?> {
  @override
  Future<User?> build() async {
    final token = await AuthInterceptor.getToken();
    if (token == null || token.isEmpty) return null;

    try {
      final api = ref.read(apiClientProvider);
      final resp = await api.getCurrentUser();
      if (resp.isSuccess) {
        return resp.data;
      }
      await AuthInterceptor.clearToken();
      return null;
    } catch (e) {
      await AuthInterceptor.clearToken();
      return null;
    }
  }

  Future<void> login(String username, String password) async {
    state = const AsyncValue.loading();
    try {
      final api = ref.read(apiClientProvider);
      final resp = await api.login(
        LoginRequest(username: username, password: password),
      );
      if (!resp.isSuccess || resp.data == null) {
        throw Exception(resp.message);
      }
      await AuthInterceptor.saveToken(resp.data!.token);
      state = AsyncValue.data(resp.data!.user);
    } catch (e, st) {
      state = AsyncValue.error(e, st);
    }
  }

  Future<void> oauthLogin(String code) async {
    state = const AsyncValue.loading();
    try {
      final api = ref.read(apiClientProvider);
      final resp = await api.oauthExchange(code);
      if (!resp.isSuccess || resp.data == null) {
        throw Exception(resp.message);
      }
      await AuthInterceptor.saveToken(resp.data!.token);
      state = AsyncValue.data(resp.data!.user);
    } catch (e, st) {
      state = AsyncValue.error(e, st);
    }
  }

  Future<void> register(String username, String password,
      {String? nickname, String? email, String? phone}) async {
    state = const AsyncValue.loading();
    try {
      final api = ref.read(apiClientProvider);
      final resp = await api.register({
        'username': username,
        'password': password,
        if (nickname != null) 'nickname': nickname,
        if (email != null) 'email': email,
        if (phone != null) 'phone': phone,
      });
      if (!resp.isSuccess || resp.data == null) {
        throw Exception(resp.message);
      }
      // Auto login after register
      await login(username, password);
    } catch (e, st) {
      state = AsyncValue.error(e, st);
    }
  }

  Future<void> logout() async {
    await AuthInterceptor.clearToken();
    state = const AsyncValue.data(null);
  }
}
