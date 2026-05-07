import 'dart:math';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:webview_flutter/webview_flutter.dart';
import '../../core/config/app_config.dart';
import '../../core/providers/auth_provider.dart';
import '../../core/theme/app_colors.dart';

class OAuthLoginPage extends ConsumerStatefulWidget {
  const OAuthLoginPage({super.key});

  @override
  ConsumerState<OAuthLoginPage> createState() => _OAuthLoginPageState();
}

class _OAuthLoginPageState extends ConsumerState<OAuthLoginPage> {
  late final WebViewController _controller;
  bool _loading = true;
  bool _exchanging = false;
  String? _error;

  @override
  void initState() {
    super.initState();
    _controller = WebViewController()
      ..setJavaScriptMode(JavaScriptMode.unrestricted)
      ..setNavigationDelegate(
        NavigationDelegate(
          onPageStarted: (_) => setState(() => _loading = true),
          onPageFinished: (_) => setState(() => _loading = false),
          onNavigationRequest: (request) {
            final url = request.url;
            if (url.startsWith(AppConfig.kRedirectUri)) {
              final uri = Uri.parse(url);
              final code = uri.queryParameters['code'];
              final error = uri.queryParameters['error'];
              if (code != null && code.isNotEmpty) {
                _exchangeCode(code);
              } else if (error != null) {
                setState(() => _error = '授权失败: $error');
              }
              return NavigationDecision.prevent;
            }
            return NavigationDecision.navigate;
          },
        ),
      )
      ..loadRequest(Uri.parse(_authorizeUrl));
  }

  String get _authorizeUrl {
    final base = AppConfig.platformBaseUrl;
    final cid = AppConfig.kOAuthClientId;
    final ru = Uri.encodeComponent(AppConfig.kRedirectUri);
    final scope = AppConfig.kOAuthScope;
    final state = 'st_${DateTime.now().millisecondsSinceEpoch}_${Random().nextInt(9999)}';
    return '$base/oauth/authorize?client_id=$cid&redirect_uri=$ru&scope=$scope&state=$state';
  }

  Future<void> _exchangeCode(String code) async {
    setState(() {
      _exchanging = true;
      _error = null;
    });
    try {
      await ref.read(authStateProvider.notifier).oauthLogin(code);
      final user = ref.read(authStateProvider).valueOrNull;
      if (user != null && mounted) {
        context.pop();
      } else {
        setState(() => _error = '登录失败，请重试');
      }
    } catch (e) {
      setState(() => _error = '登录失败: $e');
    } finally {
      if (mounted) setState(() => _exchanging = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('授权登录'),
        actions: [
          TextButton(
            onPressed: () => context.push('/login'),
            child: const Text('账号密码登录', style: TextStyle(color: Colors.white)),
          ),
        ],
      ),
      body: Stack(
        children: [
          WebViewWidget(controller: _controller),
          if (_loading && !_exchanging)
            const Positioned.fill(
              child: Center(child: CircularProgressIndicator()),
            ),
          if (_exchanging || _error != null)
            Positioned.fill(
              child: Container(
                color: Colors.black54,
                alignment: Alignment.center,
                child: Card(
                  margin: EdgeInsets.all(32),
                  child: Padding(
                    padding: EdgeInsets.all(24),
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        if (_exchanging) ...[
                          const CircularProgressIndicator(),
                          const SizedBox(height: 16),
                          const Text('正在登录…'),
                        ],
                        if (_error != null) ...[
                          const Icon(Icons.error_outline, color: AppColors.error, size: 48),
                          const SizedBox(height: 16),
                          Text(_error!, textAlign: TextAlign.center),
                          const SizedBox(height: 16),
                          ElevatedButton(
                            onPressed: () {
                              setState(() => _error = null);
                              _controller.loadRequest(Uri.parse(_authorizeUrl));
                            },
                            child: const Text('重试'),
                          ),
                        ],
                      ],
                    ),
                  ),
                ),
              ),
            ),
        ],
      ),
    );
  }
}
