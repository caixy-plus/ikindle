import 'dart:io';
import 'dart:math';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:url_launcher/url_launcher.dart';
import '../../core/config/app_config.dart';
import '../../core/providers/auth_provider.dart';
import '../../core/theme/app_colors.dart';

class OAuthLoginPage extends ConsumerStatefulWidget {
  const OAuthLoginPage({super.key});

  @override
  ConsumerState<OAuthLoginPage> createState() => _OAuthLoginPageState();
}

class _OAuthLoginPageState extends ConsumerState<OAuthLoginPage> {
  bool _exchanging = false;
  String? _error;

  String get _authorizeUrl {
    final base = AppConfig.platformBaseUrl;
    final cid = AppConfig.kOAuthClientId;
    final ru = Uri.encodeComponent(AppConfig.kRedirectUri);
    final scope = AppConfig.kOAuthScope;
    final state = 'st_${DateTime.now().millisecondsSinceEpoch}_${Random().nextInt(9999)}';
    return '$base/oauth/authorize?client_id=$cid&redirect_uri=$ru&scope=$scope&state=$state';
  }

  Future<void> _launchOAuth() async {
    final uri = Uri.parse(_authorizeUrl);
    if (Platform.isWindows || Platform.isMacOS || Platform.isLinux) {
      // 桌面端使用外部浏览器
      await launchUrl(uri, mode: LaunchMode.externalApplication);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('请在浏览器中完成授权，然后返回应用')),
        );
      }
    } else {
      // 移动端使用 WebView
      context.push('/oauth-webview', extra: _authorizeUrl);
    }
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
    final isDesktop = Platform.isWindows || Platform.isMacOS || Platform.isLinux;

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
      body: Center(
        child: ConstrainedBox(
          constraints: isDesktop
              ? const BoxConstraints(maxWidth: 400)
              : const BoxConstraints(),
          child: Padding(
            padding: const EdgeInsets.all(24),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const Icon(
                  Icons.login,
                  size: 64,
                  color: AppColors.primary,
                ),
                const SizedBox(height: 24),
                Text(
                  '统一授权登录',
                  style: Theme.of(context).textTheme.headlineMedium,
                ),
                const SizedBox(height: 16),
                Text(
                  isDesktop
                      ? '点击按钮将在浏览器中打开授权页面，完成后请返回应用'
                      : '点击按钮开始授权登录',
                  textAlign: TextAlign.center,
                  style: Theme.of(context).textTheme.bodyMedium,
                ),
                const SizedBox(height: 32),
                if (_error != null)
                  Padding(
                    padding: const EdgeInsets.only(bottom: 16),
                    child: Text(
                      _error!,
                      style: const TextStyle(color: AppColors.error),
                      textAlign: TextAlign.center,
                    ),
                  ),
                SizedBox(
                  width: double.infinity,
                  child: ElevatedButton.icon(
                    onPressed: _exchanging ? null : _launchOAuth,
                    icon: _exchanging
                        ? const SizedBox(
                            height: 20,
                            width: 20,
                            child: CircularProgressIndicator(strokeWidth: 2),
                          )
                        : const Icon(Icons.open_in_browser),
                    label: Text(isDesktop ? '在浏览器中打开' : '开始授权'),
                  ),
                ),
                if (isDesktop) ...[
                  const SizedBox(height: 16),
                  const Text(
                    '授权完成后，请将授权码粘贴到下方：',
                    style: TextStyle(color: AppColors.textSecondary),
                  ),
                  const SizedBox(height: 8),
                  TextField(
                    decoration: const InputDecoration(
                      hintText: '输入授权码',
                      prefixIcon: Icon(Icons.vpn_key),
                    ),
                    onSubmitted: (code) {
                      if (code.isNotEmpty) _exchangeCode(code);
                    },
                  ),
                  const SizedBox(height: 8),
                  SizedBox(
                    width: double.infinity,
                    child: ElevatedButton(
                      onPressed: _exchanging ? null : () {
                        // 需要获取 TextField 的值，这里简化处理
                      },
                      child: const Text('提交授权码'),
                    ),
                  ),
                ],
              ],
            ),
          ),
        ),
      ),
    );
  }
}
